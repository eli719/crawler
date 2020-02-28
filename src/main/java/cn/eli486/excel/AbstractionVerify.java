package cn.eli486.excel;

import cn.eli486.config.GlobalInfo;
import cn.eli486.utils.DateUtil;
import cn.eli486.utils.WebUtil;
import org.apache.commons.io.FileUtils;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author eli
 */
public abstract class AbstractionVerify extends Abstraction {
    protected String orgName;
    protected String orgCode;
    protected Map<String, String> loginParams = new HashMap<String, String> ();
    protected abstract String getLoginUrl();
    public abstract void addVerifyCodeParam(String verifyCode);
    protected abstract String getPictureUrl();
    protected CloseableHttpClient client;

    public String getOrgName () {
        return orgName;
    }

    public void setOrgName (String orgName) {
        this.orgName = orgName;
    }
    public String getOrgCode() {
        return orgCode;
    }
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }
    public  void getVerifyCode() throws Exception{
        client = WebUtil.getHttpClient ();
        File f = new File (GlobalInfo.verifyStorePath + "/" + orgCode + ".jpg");
        FileUtils.deleteQuietly (f);
        WebUtil.httpGet (client, getLoginUrl());
        WebUtil.requestFile(client, getPictureUrl(), GlobalInfo.verifyStorePath + "/" + orgCode + ".jpg");
    }

    public void addLoginParam(String name, String value) {
        loginParams.put(name, value);
    }

    public void addLoginParam(Map<String,String> params) {
        loginParams.putAll (params);
    }

    protected void login() throws IOException {
        Map<String,String> params = new HashMap<> ();
        params.putAll(loginParams);
        String str=WebUtil.httpPost (client,params,getLoginUrl ());

    }

    public void doExec() {
        logger.info (orgName + "  " + DateUtil.getBeforeDayAgainstToday (1, "yyyy-MM-dd") + "  日报");
        try {
            login ();
        } catch (Exception e) {
            logger.error (orgName + "的网站不可访问！");
            try {
                throw new Exception (e.getMessage ());
            } catch (Exception ex) {
                ex.printStackTrace ();
            }
        }


        stock (client, orgCode, orgName);
        sale (client, orgCode, orgName);
        purchase (client, orgCode, orgName);

        logger.info (orgName + "  " + DateUtil.getBeforeDayAgainstToday (1, "yyyy-MM-dd") + "  日报完成");
        logger.info ("------------------------------------------------------------------------------------------------------");
    }
}
