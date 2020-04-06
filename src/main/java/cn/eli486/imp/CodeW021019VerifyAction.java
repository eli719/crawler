package cn.eli486.imp;

import cn.eli486.config.TitleConfig;
import cn.eli486.excel.AbstractionVerify;
import cn.eli486.utils.DateUtil;
import cn.eli486.utils.StringUtil;
import cn.eli486.utils.WebUtil;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author eli
 */
public class CodeW021019VerifyAction extends AbstractionVerify {
    public static String session = "";
    @Override
    protected String getLoginUrl () {
        return "http://passport.shaphar.com/cas-webapp-server/login?service=http://scp.shaphar.com/cas-webapp-portal/main/index.do";
    }

    @Override
    public void addVerifyCodeParam (String verifyCode) {
        try {
            String result = WebUtil.httpGet (client,
                    "http://passport.shaphar.com/cas-webapp-server/login?service=http://scp.shaphar.com/cas-webapp-portal/main/index.do");
            String lt = StringUtil.getValue(result, "name=\"lt\" value=\"", "\" />", 1);
            loginParams.put("lt", lt);
            loginParams.put("captcha", verifyCode);
            loginParams.put("password", "a123456");
            loginParams.put("username", "bj60340");
            loginParams.put("_eventId", "submit");
            loginParams.put("submit", "登录");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getPictureUrl () {
        return "http://passport.shaphar.com/cas-webapp-server/kaptcha.jpg";
    }

    @Override
    protected void login() throws IOException {
        String result = WebUtil.httpGet (client, "http://passport.shaphar.com/cas-webapp-server/portal/queryNotice");
        result = WebUtil.httpPost (client, loginParams,
                "http://passport.shaphar.com/cas-webapp-server/login?service=http://scp.shaphar.com/cas-webapp-portal/main/index.do");
        result = WebUtil.httpGet (client, "http://scp.shaphar.com/cas-webapp-portal/main/index.do");
    }
    @Override
    protected void login (CloseableHttpClient client, Map<String, String> params) throws Exception {

    }

    @Override
    public List<List<String>> getPurchase (CloseableHttpClient client,  List<String> title) {
        return null;
    }

    @Override
    public List<String> createPurchase () {
        return null;
    }

    @Override
    public List<List<String>> getSale (CloseableHttpClient client,  List<String> title) throws IOException {
        return null;
    }

    @Override
    public List<String> createSale () {
        return null;
    }

    @Override
    public List<List<String>> getStock (CloseableHttpClient client,  List<String> title) throws IOException {
        List<String> data = null;
        List<List<String>> datas = new ArrayList<> ();
        String sdate = DateUtil.getBeforeDayAgainstToday(1, "yyyy-MM-dd");
        String result = WebUtil.httpGet (client, "http://report10.shaphar.com/WebReport/decision/v10/entry/access/old-platform-reportlet-entry-2157?dashboardType=5&width=1051&height=594");
        System.out.println (result);
        session = StringUtil.getValue(result, "FR.SessionMgr.register('", "',", 1);

        loginParams.clear();
        String para = "__parameters__";
        String content = "{\"F_U_N\":\"\",\"AS_COM_GOODS\":\"\",\"AS_INV_STORAGE\":\"\",\"AS_INV_OWNER\":\"\",\"AS_ATTR1\":\"\",\"AS_ATTR\":\"\",\"LABEL3_C\":\"[5206][3001][5b50][516c][53f8][ff1a]\",\"STARTDATE\":\""+sdate+"\",\"LABEL0\":\"[65e5][671f][ff1a]\",\"LABEL1_C_C_C\":\"[4ea7][54c1][ff1a]\",\"LABEL1_C_C\":\"[4ed3][5e93][ff1a]\",\"SORT5\":\"asc\",\"COLUMN5\":\"\",\"SORT4\":\"asc\",\"COLUMN4\":\"\",\"SORT3\":\"asc\",\"COLUMN3\":\"\",\"SORT2\":\"asc\",\"COLUMN2\":\"\",\"SORT1\":\"asc\",\"COLUMN1\":\"\",\"LABEL1\":\"[6392][5e8f][ff1a]\"}";

        HttpPost httpPost=new HttpPost("http://report10.shaphar.com/WebReport/decision/view/report?op=fr_dialog&cmd=parameters_d");
        httpPost.setHeader("Proxy-Connection","keep-alive");
        httpPost.setHeader("Host","report10.shaphar.com");
        httpPost.setHeader("Origin","http://report10.shaphar.com");
        httpPost.setHeader("Referer","http://report10.shaphar.com/WebReport/decision/v10/entry/access/old-platform-reportlet-entry-2157?dashboardType=5&width=810&height=594");
        httpPost.setHeader("Host","report10.shaphar.com");
        httpPost.setHeader("sessionID",session);
        System.out.println (session);
        List<NameValuePair>params=new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair (para,content));
        UrlEncodedFormEntity urlEncodedFormEntity=new UrlEncodedFormEntity(params,"utf8");
        httpPost.setEntity(urlEncodedFormEntity);
        HttpResponse response =client.execute(httpPost);

        if(response.getStatusLine().getStatusCode()==200){
            String context= EntityUtils.toString(response.getEntity());
        }

        result = WebUtil.httpGet (client, "http://report10.shaphar.com/WebReport/decision/view/report?_=1571116515219&__boxModel__=true&op=fr_write&cmd=read_w_content&reportIndex=0&browserWidth=1291&iid=0.20099228397639246&__cutpage__=v&pn=1&__webpage__=true&_paperWidth=1291&_paperHeight=595&__fit__=false&sessionID="+session);
        System.out.println (result);

        int start1 = result.indexOf("td valign=\\\"top\\\"");
        int start2 = result.indexOf("td valign=\\\"top\\\"",start1+1);
        String result1 = result.substring(start1, start2);
        String result2 = result.substring(start2);
        String[] rows = result1.split("tridx=");
        String[] rows2 = result2.split("tridx=");
        String date = DateUtil.getLastDay("yyyy-MM-dd");

        for (int i = 1; i < rows.length-1; i++) {
            data = new ArrayList<String> ();
            String arr[] = StringUtil.getValue(rows[i], "<div style=\\\"max-height:43px;\\\" heavytd=\\\"light\\\">", "</div>", 5).split("-");
            addRows(datas,data, title);
            addCell(data, arr[0], 2);
            addCell(data, arr[1], 3);
            addCell(data, StringUtil.getValue(rows2[i], "<div style=\\\"max-height:43px;\\\" heavytd=\\\"light\\\">", "</div>", 4).trim(), 4);
            addCell(data, StringUtil.getValue(rows2[i], "<div style=\\\"max-height:43px;\\\" heavytd=\\\"light\\\">", "</div>", 1).trim(), 5);
            addCell(data, StringUtil.getValue(rows[i], "<div style=\\\"max-height:43px;\\\" heavytd=\\\"light\\\">", "</div>", 4).trim(), 6);
            addCell(data, arr[2], 7);
            addCell(data, StringUtil.getValue(rows[i], "<div style=\\\"max-height:43px;\\\" heavytd=\\\"light\\\">", "</div>", 6).trim(), 8);
            addCell(data, StringUtil.getValue(rows2[i], "<div style=\\\"max-height:43px;\\\" heavytd=\\\"light\\\">", "</div>", 2).trim(), 9);
            addCell(data, StringUtil.getValue(rows2[i], "<div style=\\\"max-height:43px;\\\" heavytd=\\\"light\\\">", "</div>", 3).trim(), 10);
            addCell(data, StringUtil.getValue(rows[i], "<div style=\\\"max-height:43px;\\\" heavytd=\\\"light\\\">", "</div>", 3).trim(), 11);
        }

        return datas;
    }

    @Override
    public List<String> createStock () {
        return TitleConfig.getTitle ().get (7);
    }
}
