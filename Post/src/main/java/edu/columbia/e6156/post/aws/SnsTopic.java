package edu.columbia.e6156.post.aws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.columbia.e6156.post.config.BeanConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.util.Map;

@Component
public final class SnsTopic {
    private String topicArn;

    private final ObjectMapper objectMapper;
    private final SnsClient client;

    private final String awsRegion, awsAccount;

    @Autowired
    public SnsTopic(final SnsClient client,
                    @Qualifier(BeanConstants.AWS_REGION) String awsRegion,
                    @Qualifier(BeanConstants.AWS_ACCOUNT_ID) String awsAccount) {
        this.objectMapper = new ObjectMapper();
        this.client = client;
        this.awsRegion = awsRegion;
        this.awsAccount = awsAccount;
    }

    public void setName(String name) {
        if(topicArn != null) {
            throw new IllegalStateException("topic name already set");
        }

        this.topicArn = "arn:aws:sns:" + awsRegion + ":" + awsAccount + ":" + name;
    }

    public void send(Map<String, Object> data) {
        try {
            send(objectMapper.writeValueAsString(data));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void send(String message) {
        PublishRequest request = PublishRequest.builder()
                .topicArn(topicArn).message(message).build();

        client.publish(request);
    }
}
