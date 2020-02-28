package cn.eli486.imp;

import cn.eli486.excel.AbstractionVerify;
import cn.eli486.utils.WebUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author eli
 */
public class Demo4 extends AbstractionVerify {
    @Override
    protected void login () throws IOException {
        System.out.println (client);
        String str = "";
        loginParams.put("username", "ys");
        loginParams.put("password", "welcome123");
        loginParams.put("isMoblie", "false");
        str = WebUtil.httpPost (client, loginParams, "http://e.hjt360.cn/login/vali");
        str = WebUtil.httpGet (client, "http://e.hjt360.cn/index.html");

    }

    @Override
    protected void login (CloseableHttpClient client, Map<String, String> params) throws Exception {

    }

    @Override
    protected String getLoginUrl () {
        return "http://e.hjt360.cn/login/login.html";
    }

    @Override
    public void addVerifyCodeParam (String verifyCode) {
        loginParams.put("authCode", verifyCode);
    }

    @Override
    protected String getPictureUrl () {
        return "http://e.hjt360.cn/authimg";
    }


    @Override
    public List<List<String>> getPurchase (CloseableHttpClient client, String orgName, List<String> title) {
        return null;
    }

    @Override
    public List<String> createPurchase () {
        return null;
    }


    @Override
    public List<List<String>> getSale (CloseableHttpClient client, String orgName, List<String> title) throws IOException {
        return null;
    }

    @Override
    public List<String> createSale () {
        return null;
    }


    @Override
    public List<List<String>> getStock (CloseableHttpClient client, String orgName, List<String> title) throws IOException {
        List<String> rows = null;
        String str = WebUtil.httpGet (client, "http://e.hjt360.cn/bill/view/multibill?billKey=changjiakucunquery");
        loginParams.clear();
        loginParams.put("billkey", "changjiakucunquery");

        str = WebUtil.httpPost (client, loginParams, "http://e.hjt360.cn/bill/data/multibill");
        JSONObject json = new JSONObject();
        json = JSONObject.fromObject(str);
        JSONObject target = json.getJSONObject("data");
        JSONObject inv = target.getJSONObject("changjiakucunquery");
        Integer page = (Integer) inv.getJSONObject("page").get("totalPage");
        Integer totalRow = (Integer) inv.getJSONObject("page").get("totalRow");
        loginParams.clear();

        loginParams.put("entityKey", "changjiakucunquery");
        loginParams.put("billkey", "changjiakucunquery");
        loginParams.put("fullKey", "MultiBill_changjiakucunquery");
        loginParams.put("page", "{\"pageNumber\":" + 1 + ",\"pageSize\":"+totalRow+",\"totalPage\":" + page + ",\"totalRow\":"
                + totalRow + "}");
        loginParams.put("model",
                "{\"changjiakucunquery\":{\"page\":{\"pageNumber\":" + 1 + ",\"pageSize\":"+totalRow+",\"totalPage\":" + page
                        + ",\"totalRow\":" + totalRow + "},\"params\":{\"goodsCode\":null,\"goodsName\":null}}}");
        str = WebUtil.httpPost (client, loginParams, "http://e.hjt360.cn/bill/data/refresh");
        json = JSONObject.fromObject (str);
        target = json.getJSONObject("data");
        JSONArray c = target.getJSONArray("cos");
        List<List<String>> content = new ArrayList<> ();
        for (int i = 0; i < c.size(); i++) {
            JSONObject n = c.getJSONObject(i);
            rows = new ArrayList<String> ();
            addRows(content,rows, title);
            addCell(rows, n.get("goodsName").toString(), 2);
            addCell(rows, n.get("goodsSpec").toString(),3);
            addCell(rows, n.get("placeNum").toString(),4);
            addCell(rows, n.get("batchCode").toString(),5);
            addCell(rows, n.get("unit").toString(),6);
        }
        return content;
    }

    @Override
    public List<String> createStock () {
        return null;
    }

    public static void main (String[] args) throws IOException {
        CloseableHttpClient client = WebUtil.getHttpClient ();
        String s = WebUtil.httpGet (client, "http://e.hjt360.cn/login/login.html");
        WebUtil.requestFile (client,"http://e.hjt360.cn/authimg","d:\\1.jpg");
        String sr = FileUtils.readFileToString (new File ("d:\\code.txt"),"utf-8");
        Map<String,String> loginparams = new HashMap<> ();
        loginparams.put("username", "ys");
        loginparams.put("password", "welcome123");
        loginparams.put("isMoblie", "false");
        loginparams.put("authCode", sr);
        s = WebUtil.httpPost (client, loginparams, "http://e.hjt360.cn/login/vali");
        s = WebUtil.httpGet (client, "http://e.hjt360.cn/index.html");
    }

}
