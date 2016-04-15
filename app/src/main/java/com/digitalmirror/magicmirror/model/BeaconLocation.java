package com.digitalmirror.magicmirror.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BeaconLocation {

    @JsonProperty
    String uuid;

    @JsonProperty
    String majorId;

    @JsonProperty
    String minorId;

    public BeaconLocation() {
    }

    public BeaconLocation(String uuid, String majorId, String minorId) {
        this.uuid = uuid;
        this.majorId = majorId;
        this.minorId = minorId;
    }
}
