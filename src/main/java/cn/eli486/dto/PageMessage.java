package cn.eli486.dto;

import java.util.List;
import java.util.Map;

/**
 * @author eli
 */
public class PageMessage {
    private String log;

    private Map<String,List<Integer>> doStatus;

    public String getLog () {
        return log;
    }

    public void setLog (String log) {
        this.log = log;
    }

    public Map<String, List<Integer>> getDoStatus () {
        return doStatus;
    }

    public void setDoStatus (Map<String, List<Integer>> doStatus) {
        this.doStatus = doStatus;
    }


}
