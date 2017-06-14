package com.netflix.simianarmy.aws.janitor.rule;

import java.util.Date;

public class MetaTag {

    private String instance;
    private String owner;
    private Date lastDetachTime;

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getLastDetachTime() {
        return lastDetachTime;
    }

    public void setLastDetachTime(Date lastDetachTime) {
        this.lastDetachTime = lastDetachTime;
    }
}
