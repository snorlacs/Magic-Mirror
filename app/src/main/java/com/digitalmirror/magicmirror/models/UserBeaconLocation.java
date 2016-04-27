package com.digitalmirror.magicmirror.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserBeaconLocation {

    @JsonProperty
    String userId;

    @JsonProperty
    String uuId;

    @JsonProperty
    Long majorId;

    @JsonProperty
    Long minorId;

    public UserBeaconLocation() {
    }

    public UserBeaconLocation(String userId, String uuId, Long majorId, Long minorId) {
        this.userId = userId;
        this.uuId = uuId;
        this.majorId = majorId;
        this.minorId = minorId;
    }
}
