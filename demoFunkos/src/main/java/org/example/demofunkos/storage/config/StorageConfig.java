package org.example.demofunkos.storage.config;

import org.springframework.context.annotation.Configuration;

@Configuration("storage")
public class StorageConfig {

    private String location = "upload-dir";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}