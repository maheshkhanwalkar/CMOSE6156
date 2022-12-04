package edu.columbia.e6156.post.providers;

import edu.columbia.e6156.post.config.BeanConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.providers.DefaultAwsRegionProviderChain;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sts.StsClient;

@Component
public final class AwsProvider
{
    @Bean(BeanConstants.IMAGE_S3_BUCKET)
    public String getImageBucketName() {
        return "five-lions-image-store";
    }

    @Bean
    public S3Client getS3Client() {
        return S3Client.create();
    }

    @Bean
    public SnsClient getSNSClient() {
        return SnsClient.create();
    }

    @Bean
    public StsClient getSTSClient() {
        return StsClient.create();
    }

    @Bean(BeanConstants.AWS_ACCOUNT_ID)
    public String getAccountId(StsClient client) {
        return client.getCallerIdentity().account();
    }

    @Bean(BeanConstants.AWS_REGION)
    public String getRegion() {
        return DefaultAwsRegionProviderChain.builder().build().getRegion().id();
    }
}
