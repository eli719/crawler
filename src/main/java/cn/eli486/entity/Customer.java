package cn.eli486.entity;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Customer属性配置类
 * @author eli
 */
@Component
public class Customer {
    private String orgcode;
    private String orgname;
    private ConcurrentHashMap<String,String> params;
    private String website;
    private String dailyTime;
    private boolean merge;
    private boolean status;
    private String type;
    private String action;

    public String getOrgcode () {
        return orgcode;
    }

    public void setOrgcode (String orgcode) {
        this.orgcode = orgcode;
    }

    public String getOrgname () {
        return orgname;
    }

    public void setOrgname (String orgname) {
        this.orgname = orgname;
    }

    public ConcurrentHashMap<String, String> getParams () {
        return params;
    }

    public void setParams (ConcurrentHashMap<String, String> params) {
        this.params = params;
    }

    public String getWebsite () {
        return website;
    }

    public void setWebsite (String website) {
        this.website = website;
    }

    public String getDailyTime () {
        return dailyTime;
    }

    public void setDailyTime (String dailyTime) {
        this.dailyTime = dailyTime;
    }

    public boolean isMerge () {
        return merge;
    }

    public void setMerge (boolean merge) {
        this.merge = merge;
    }

    public boolean isStatus () {
        return status;
    }

    public void setStatus (boolean status) {
        this.status = status;
    }

    public String getType () {
        return type;
    }

    public void setType (String type) {
        this.type = type;
    }

    public String getAction () {
        return action;
    }

    public void setAction (String action) {
        this.action = action;
    }

    @Override
    public String toString () {
        return "Customer{" +
                "orgcode='" + orgcode + '\'' +
                ", orgname='" + orgname + '\'' +
                ", params=" + params +
                ", website='" + website + '\'' +
                ", dailyTime='" + dailyTime + '\'' +
                ", merge=" + merge +
                ", status=" + status +
                ", type='" + type + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}
