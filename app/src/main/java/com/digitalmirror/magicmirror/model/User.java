package com.digitalmirror.magicmirror.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    @JsonProperty
    String facebookId;

    @JsonProperty
    String firstName;

    @JsonProperty
    String lastName;

    @JsonProperty
    String gender;

    @JsonProperty
    String displayPicture;

    public User() {

    }

    public User(String facebookId, String firstName, String lastName, String gender, String displayPicture) {
        this.facebookId = facebookId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.displayPicture = displayPicture;
    }


}
