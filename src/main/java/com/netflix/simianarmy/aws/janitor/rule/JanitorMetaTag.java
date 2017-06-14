package com.netflix.simianarmy.aws.janitor.rule;

import com.amazonaws.services.ec2.model.Volume;
import com.netflix.simianarmy.client.aws.AWSClient;

public class JanitorMetaTag {

    private MetaTag metaTag;
    private Volume volume;
    private AWSClient awsClient;

    public MetaTag getMetaTag() {
        return metaTag;
    }

    public void setMetaTag(MetaTag metaTag) {
        this.metaTag = metaTag;
    }

    public Volume getVolume() {
        return volume;
    }

    public void setVolume(Volume volume) {
        this.volume = volume;
    }

    public AWSClient getAwsClient() {
        return awsClient;
    }

    public void setAwsClient(AWSClient awsClient) {
        this.awsClient = awsClient;
    }
}
