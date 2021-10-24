package com.twa.flights.api.clusters.configuration.settings;

public class CacheSettings {
    private long expireAfterWriteTime;

    public long getExpireAfterWriteTime() {
        return expireAfterWriteTime;
    }

    public void setExpireAfterWriteTime(long expireAfterWriteTime) {
        this.expireAfterWriteTime = expireAfterWriteTime;
    }
}
