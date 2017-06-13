package com.netflix.simianarmy.aws.janitor.crawler.edda;

import com.google.common.collect.Maps;
import com.netflix.simianarmy.Resource;
import com.netflix.simianarmy.client.edda.EddaClient;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class UpdateReferenceTime {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateReferenceTime.class);

    private final EddaClient eddaClient;

    protected UpdateReferenceTime(EddaClient eddaClient) {
        this.eddaClient = eddaClient;
    }

    protected EddaClient getEddaClient() {
        return eddaClient;
    }

    public void updateReferenceTime(String region, List<Resource> batch, long since) {
        LOGGER.info(String.format("Getting the last reference time by instance for batch of size %d", batch.size()));
        String batchUrl = getUrl(region, batch, since);
        JsonNode batchResult = null;

        Map<String, Resource> idToResource = getIdToResourceMap(batch);

        try {
            batchResult = eddaClient.getJsonNodeFromUrl(batchUrl);
        } catch (IOException e) {
            LOGGER.error("Failed to get response for the batch.", e);
        }
        if (batchResult == null || !batchResult.isArray()) {
            throw new RuntimeException(String.format("Failed to get valid document from %s, got: %s",
                batchUrl, batchResult));
        }
        for (Iterator<JsonNode> it = batchResult.getElements(); it.hasNext(); ) {
            JsonNode elem = it.next();
            JsonNode data = elem.get("data");
            String imageId = data.get("imageId").getTextValue();

            foo(idToResource, elem, data, imageId);
        }


    }

    protected abstract void foo(Map<String, Resource> idToResource, JsonNode elem, JsonNode data, String imageId);

    protected abstract String getUrl(String region, List<Resource> batch, long since);

    private Map<String, Resource> getIdToResourceMap(List<Resource> batch) {
        Map<String, Resource> idToResource = Maps.newHashMap();
        for (Resource resource : batch) {
            idToResource.put(resource.getId(), resource);
        }
        return idToResource;
    }

    public static String getImageIdsString(List<Resource> resources) {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (Resource resource : resources) {
            if (!isFirst) {
                sb.append(',');
            } else {
                isFirst = false;
            }
            sb.append(resource.getId());
        }
        return sb.toString();
    }

}
