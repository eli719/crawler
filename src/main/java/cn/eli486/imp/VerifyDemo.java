package cn.eli486.imp;

import cn.eli486.config.GlobalInfo;
import cn.eli486.excel.ExcelDemo;
import cn.eli486.util.DateUtil;
import cn.eli486.util.WebUtil;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author eli
 */
public abstract class VerifyDemo extends ExcelDemo {
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
//        FileUtils.deleteQuietly (new File (GlobalInfo.verifyStorePath + "/" + orgCode + ".jpg"));
        WebUtil.httpGet (client, getLoginUrl());
        Long s = System.currentTimeMillis ();
        WebUtil.requestFile(client, getPictureUrl(), GlobalInfo.verifyStorePath + "/" + orgCode + ".jpg");
        Long e =System.currentTimeMillis ();
        System.out.println ("耗时：-----"+(e-s));
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
        WebUtil.httpPost (client,params,getLoginUrl ());

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
        String dir = "D:/XJPFile/auto17/" + DateUtil.getBeforeDayAgainstToday (1, "yyyy-MM-dd");
        stock (client, orgCode, orgName);
        sale (client, orgCode, orgName);
        purchase (client, orgCode, orgName);
    }
}
