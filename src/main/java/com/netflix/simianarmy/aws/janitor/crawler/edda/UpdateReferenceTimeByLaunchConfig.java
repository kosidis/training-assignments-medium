package com.netflix.simianarmy.aws.janitor.crawler.edda;

import com.netflix.simianarmy.Resource;
import com.netflix.simianarmy.client.edda.EddaClient;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class UpdateReferenceTimeByLaunchConfig extends UpdateReferenceTime {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateReferenceTimeByLaunchConfig.class);
    /**
     * The name representing the additional field name for the last reference time by launch config.
     */
    public static final String AMI_FIELD_LAST_LC_REF_TIME = "Last_Launch_Config_Reference_Time";

    public UpdateReferenceTimeByLaunchConfig(EddaClient eddaClient) {
        super(eddaClient);
    }

    @Override
    public void foo(Map<String, Resource> idToResource, JsonNode elem, JsonNode data, String imageId) {
        String launchConfigurationName = data.get("launchConfigurationName").getTextValue();
        JsonNode ltimeNode = elem.get("ltime");
        if (ltimeNode != null && !ltimeNode.isNull()) {
            long ltime = ltimeNode.asLong();
            Resource ami = idToResource.get(imageId);
            String lastRefTimeByLC = ami.getAdditionalField(AMI_FIELD_LAST_LC_REF_TIME);
            if (lastRefTimeByLC == null || Long.parseLong(lastRefTimeByLC) < ltime) {
                LOGGER.info(String.format(
                    "The last time that the image %s was referenced by launch config %s is %d",
                    imageId, launchConfigurationName, ltime));
                ami.setAdditionalField(AMI_FIELD_LAST_LC_REF_TIME, String.valueOf(ltime));
            }
        }

    }

    @Override
    public String getUrl(String region, List<Resource> batch, long since) {
        return getLaunchConfigBatchUrl(region, batch, since);
    }

    private String getLaunchConfigBatchUrl(String region, List<Resource> batch, long since) {
        StringBuilder batchUrl = new StringBuilder(getEddaClient().getBaseUrl(region)
            + "/aws/launchConfigurations/;data.imageId=");
        batchUrl.append(getImageIdsString(batch));
        batchUrl.append(String.format(";_since=%d;_expand;_meta:(ltime,data:(imageId,launchConfigurationName))",
            since));
        return batchUrl.toString();
    }
}
