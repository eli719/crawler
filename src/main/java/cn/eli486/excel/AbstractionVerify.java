package cn.eli486.excel;

import cn.eli486.config.GlobalInfo;
import cn.eli486.controller.VerifyController;
import cn.eli486.entity.Customer;
import cn.eli486.utils.DateUtil;
import cn.eli486.utils.WebUtil;
import org.apache.commons.io.FileUtils;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author eli
 */
public abstract class AbstractionVerify extends Abstraction {
    private Customer customer;
    private String orgName;
    private String orgCode;
    protected Map<String, String> loginParams = new HashMap<> ();

    /**登录页面地址
     * @return 获取登录页面
     */
    protected abstract String getLoginUrl();

    /**获取验证码
     * @param verifyCode 验证码
     */
    public abstract void addVerifyCodeParam(String verifyCode);

    /**验证码图片地址
     * @return 获取验证码图片地址
     */
    protected abstract String getPictureUrl();
    protected CloseableHttpClient client;

    public String getOrgName () {
        return orgName;
    }

    public Customer getCustomer () {
        return customer;
    }

    public void setCustomer (Customer customer) {
        this.customer = customer;
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
        WebUtil.getFile (client, getPictureUrl(), GlobalInfo.verifyStorePath + "/" + orgCode + ".jpg");
    }

    public void addLoginParam(Map<String,String> params) {
        loginParams.putAll (params);
    }

    /**
     * 登录
     * @throws Exception 异常
     */
    abstract protected void login() throws Exception;

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


        try {
            Map<String, List<Integer>> doStatus = VerifyController.pageMessage.getDoStatus ();
            execTasks (doStatus,client,customer);

        } catch (Exception e) {
            e.printStackTrace ();
        }

        logger.info (orgName + "  " + DateUtil.getBeforeDayAgainstToday (1, "yyyy-MM-dd") + "  日报完成");
        logger.info ("------------------------------------------------------------------------------------------------------");
    }
}
