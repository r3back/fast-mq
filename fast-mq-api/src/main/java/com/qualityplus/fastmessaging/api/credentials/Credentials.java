package com.qualityplus.fastmessaging.api.credentials;

public interface Credentials {
    public String getUri();
    public String getPrefix();
    public MessagingType getType();

    public enum MessagingType {
        RABBIT_MQ,
        REDIS
    }
}
