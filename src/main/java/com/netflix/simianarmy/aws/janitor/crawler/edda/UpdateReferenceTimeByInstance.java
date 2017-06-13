package com.netflix.simianarmy.aws.janitor.crawler.edda;

import com.netflix.simianarmy.Resource;
import com.netflix.simianarmy.client.edda.EddaClient;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class UpdateReferenceTimeByInstance extends UpdateReferenceTime {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateReferenceTimeByLaunchConfig.class);
    /**
     * The name representing the additional field name for the last reference time by instance.
     */
    public static final String AMI_FIELD_LAST_INSTANCE_REF_TIME = "Last_Instance_Reference_Time";

    public UpdateReferenceTimeByInstance(EddaClient eddaClient) {
        super(eddaClient);
    }

    @Override
    public void foo(Map<String, Resource> idToResource, JsonNode elem, JsonNode data, String imageId) {
        String instanceId = data.get("instanceId").getTextValue();
        JsonNode ltimeNode = elem.get("ltime");
        if (ltimeNode != null && !ltimeNode.isNull()) {
            long ltime = ltimeNode.asLong();
            Resource ami = idToResource.get(imageId);
            String lastRefTimeByInstance = ami.getAdditionalField(AMI_FIELD_LAST_INSTANCE_REF_TIME);
            if (lastRefTimeByInstance == null || Long.parseLong(lastRefTimeByInstance) < ltime) {
                LOGGER.info(String.format("The last time that the image %s was referenced by instance %s is %d",
                    imageId, instanceId, ltime));
                ami.setAdditionalField(AMI_FIELD_LAST_INSTANCE_REF_TIME, String.valueOf(ltime));
            }
        }
    }

    @Override
    public String getUrl(String region, List<Resource> batch, long since) {
        return getInstanceBatchUrl(region, batch, since);
    }

    private String getInstanceBatchUrl(String region, List<Resource> batch, long since) {
        StringBuilder batchUrl = new StringBuilder(getEddaClient().getBaseUrl(region)
            + "/view/instances/;data.imageId=");
        batchUrl.append(getImageIdsString(batch));
        batchUrl.append(String.format(";data.state.name=terminated;_since=%d;_expand;_meta:"
            + "(ltime,data:(imageId,instanceId))", since));
        return batchUrl.toString();
    }

}
