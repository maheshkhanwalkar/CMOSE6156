package edu.columbia.e6156.post.providers;

import edu.columbia.e6156.post.config.BeanConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;

@Component
public final class S3Provider {

    @Bean(BeanConstants.IMAGE_S3_BUCKET)
    public String getImageBucketName() {
        return "s3://five-lions-image-store";
    }

    @Bean(BeanConstants.S3_CLIENT)
    public S3Client getS3Client() {
        return S3Client.create();
    }
}
