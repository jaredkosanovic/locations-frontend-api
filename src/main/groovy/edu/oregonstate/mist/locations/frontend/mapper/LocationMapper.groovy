package edu.oregonstate.mist.locations.frontend.mapper

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import edu.oregonstate.mist.api.jsonapi.ResourceObject

class LocationMapper {
    public static ResourceObject map(JsonNode hit) {
        def hitSource = hit.get("_source").toString()
        buildResourceObject(hitSource, hit)
    }

    public static map(String hitSource) {
        buildResourceObject(hitSource)
    }

    private static buildResourceObject(String hitSource, JsonNode hit = null) {
        ResourceObject ro = new ObjectMapper().readValue(hitSource, ResourceObject.class)
        adjustLocationsResource(ro, hit)

        ro
    }

    /**
     * Modify the json object from ElasticSearch to the API specification.
     *
     * @param attr
     * @param sort
     * @return
     */
    private static void adjustLocationsResource(ResourceObject ro, JsonNode hit) {
        // setup the individual latitude, longitude and remove ES geoLocation object
        if (ro?.type != "services") { // services don't have lat / lon
            ro?.attributes?.latitude = ro?.attributes?.geoLocation?.lat?.toString()
            ro?.attributes?.longitude = ro?.attributes?.geoLocation?.lon?.toString()
        } else if (ro?.type == "services") {
            // Services don't have a concept of type
            ro?.attributes?.remove("type")
        }

        // add the sort ES metadata to attributes
        if (hit?.get('sort')?.get(0)) {
            ro?.attributes?.distance = hit?.get('sort')?.get(0)?.asDouble()
        }

        // remove attributes not part of the api spec
        ro?.attributes?.remove("geoLocation")
        ro?.attributes?.remove("parent")
        ro?.attributes?.remove("locationId")

        // hashCode is used as metadata in ES. No need to expose it
        ro?.attributes?.remove("hashCode")
    }
}
