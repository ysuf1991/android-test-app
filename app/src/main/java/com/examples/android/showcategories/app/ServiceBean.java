package com.examples.android.showcategories.app;

import java.io.Serializable;

public class ServiceBean implements Serializable{
    private String title;
    private long id;
    private long keyId;
    private ServiceBean[] subs;

    public ServiceBean(long keyId, long id, String title) {
        this.id = id;
        this.keyId = keyId;
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ServiceBean[] getSubs() {
        return subs;
    }

    public void setSubs(ServiceBean[] subs) {
        this.subs = subs;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getKeyId() {
        return keyId;
    }
}
