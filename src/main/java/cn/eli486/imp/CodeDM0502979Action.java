package cn.eli486.imp;

import cn.eli486.config.TitleConfig;
import cn.eli486.entity.Customer;
import cn.eli486.excel.Abstraction;
import cn.eli486.utils.DateUtil;
import cn.eli486.utils.StringUtil;
import cn.eli486.utils.WebUtil;
import org.apache.http.Header;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author eli
 */
public class CodeDM0502979Action extends Abstraction {
    // SID
    public static String chainUId = "";

    @Override
    protected void login (CloseableHttpClient client, Map<String, String> params) throws Exception {
        Map<String, String> loginParamsA = new HashMap<String, String>();
        String res = WebUtil.httpGet (client, "http://61.183.138.78:8090/index.aspx");
        String eventvalidation = StringUtil.getValue(res,
                "<input type=\"hidden\" name=\"__EVENTVALIDATION\" id=\"__EVENTVALIDATION\" value=\"", "\" />", 1);
        String viewstate = StringUtil.getValue(res,
                "<input type=\"hidden\" name=\"__VIEWSTATE\" id=\"__VIEWSTATE\" value=\"", "\" />", 1);
        String viewstategenerator = StringUtil.getValue(res,
                "<input type=\"hidden\" name=\"__VIEWSTATEGENERATOR\" id=\"__VIEWSTATEGENERATOR\" value=\"", "\" />",
                1);
        loginParamsA.put("__EVENTVALIDATION", eventvalidation);
        loginParamsA.put("__VIEWSTATE", viewstate);
        loginParamsA.put("__VIEWSTATEGENERATOR", viewstategenerator);
        loginParamsA.put("ImgSubmit", "登录");
        loginParamsA.put("txtname", "1001772");
        loginParamsA.put("txtpwd", "XAYS123");
        String str =WebUtil.httpPost (client, loginParamsA, "http://61.183.138.78:8090/index.aspx");
        Header[] headers = WebUtil.headers;
        for (Header h :
                headers) {
           if (h.getValue ().contains ("BRRSSSP")){
               chainUId=h.getValue ().substring (8,h.getValue ().indexOf (';'));
           }
        }
        WebUtil.httpGet (client, "http://61.183.138.78:8090/main.htm");
    }

    @Override
    public List<List<String>> getPurchase (CloseableHttpClient client, List<String> title) {
        return null;
    }

    @Override
    public List<String> createPurchase () {
        return null;
    }

    @Override
    public List<List<String>> getSale (CloseableHttpClient client,  List<String> title) throws IOException {
        String startdate = DateUtil.getBeforeDayAgainstToday(60, "yyyy-MM-dd");
        String enddate = DateUtil.getBeforeDayAgainstToday(1, "yyyy-MM-dd");

        Map<String, String> params = new HashMap<String, String>();
        String result = "";
        result = WebUtil.httpPostJson (client, "{\"SId\":\""
                + chainUId + "\",\"startdate\":\"" + startdate + "\",\"enddate\":\"" + enddate
                + "\",\"batchNo\":\"\",\"proofType\":\"\",\"medIds\":[],\"supIds\":[],\"clientIds\":[],\"goodsIds\":[],\"orgCodes\":[],\"page\":1,\"rows\":10000,\"sort\":\"a.ORG_CODE\",\"order\":\"desc\"}","http://61.183.138.78:8090/Sale/GetSaleList");

        int beginIndexs = result.indexOf("rows");
        int endIndexs = result.indexOf("other");
        if (beginIndexs > 0 && endIndexs > 0) {
            result = result.substring(beginIndexs, endIndexs);
        }
        String[] rows = result.split("ROWNO");
        List<List<String>> content = new ArrayList<> ();
        List<String> data = null;
        for (int i = 1; i < rows.length; i++) {
            String danweimingchen = StringUtil.getValue(rows[i], "ORG_NAME\": \"", "\",", 1);
            if (!danweimingchen.equals("华润襄阳医药有限公司")) {
                continue;
                // System.out.println(rows[i]);
                // System.out.println("+++++++++++++++++++++++++++++++++++++++++");
            }
            StringBuilder sb = new StringBuilder();
            String shuliang = StringUtil.getValue(rows[i], "QUANTITY\": ", ",", 1).trim();
            if ((shuliang.equals("")) || (shuliang == null)) {
                shuliang = "0";
            }
            String type = StringUtil.getValue(rows[i], "PROOF_TYPENAME\": \"", "\",", 1).trim();
            if (!type.contains("销售")) {
                continue;
            }
            data = new ArrayList<String> ();
            content = addRows (content,data, title);
            addCell (data, StringUtil.getValue(rows[i], "DELIVERY_DATE\": \"", "\",", 1).trim(), 1);
            addCell (data, StringUtil.getValue(rows[i], "CLIENT_NAME\": \"", "\",", 1).trim(), 2);
            addCell (data, StringUtil.getValue(rows[i], "GOODS_NAME\": \"", "\",", 1).trim(), 3);
            addCell (data, StringUtil.getValue(rows[i], "GOODS_SPEC\": \"", "\",", 1).trim(), 4);
            addCell (data, shuliang, 5);
            addCell (data, StringUtil.getValue(rows[i], "PRICE\": ", ",", 1).trim(), 7);
            addCell (data, StringUtil.getValue(rows[i], "BATCH_NO\": \"", "\",", 1).trim(), 12);
            addCell (data, StringUtil.getValue(rows[i], "MAKER_NAME\": \"", "\",", 1).trim(), 14);
            addCell (data, StringUtil.getValue(rows[i], "UNIT_NAME\": \"", "\",", 1).trim(), 16);
            addCell (data, StringUtil.getValue(rows[i], "PROOF_MAN\": \"", "\",", 1).trim(), 17);
            addCell (data, StringUtil.getValue(rows[i], "PROOF_TYPENAME\": \"", "\",", 1).trim(), 18);
            addCell (data, StringUtil.getValue(rows[i], "MAKER_NAME\": \"", "\",", 1).trim(), 19);
        }
        return content;
    }


    @Override
    public List<String> createSale () {
        return TitleConfig.getTitle ().get (6);
    }

    @Override
    public List<List<String>> getStock (CloseableHttpClient client, List<String> title) throws IOException {
        Map<String, String> loginParams = new HashMap<String, String>();
        //{"SId":"BRRSSSP_d0chdw5tzwyr2xlc13jvot2q","orgCodes":["XYYY0"],"keyword":"","sort":"a.GOODS_ID","order":"desc"}
        String da = "{\"SId\":\"" + chainUId + "\",\"startdate\":\"" + DateUtil.getLastDay("yyyy-MM-dd")
                + "\",\"validatemin\":\"\",\"validatemax\":\"\",\"batchNo\":\"\",\"medIds\":[],\"supIds\":[],\"goodsIds\":[],\"orgCodes\":[\"XYYY0\"],\"page\":1,\"rows\":1000,\"sort\":\"a.ORG_CODE\",\"order\":\"desc\"}";
        String result = WebUtil.httpPostJson (client,da, "http://61.183.138.78:8090/Sale/GetStoreList");


        int beginIndexs = result.indexOf("rows");
        int endIndexs = result.indexOf("other");
        if (beginIndexs > 0 && endIndexs > 0) {
            result = result.substring(beginIndexs, endIndexs);
        }
        String[] rows = result.split("ROWNO");
        List<List<String>> content = new ArrayList<> ();
        List<String> data = null;
        for (int j = 1; j < rows.length; j++) {
            String danweimingchen = StringUtil.getValue(rows[j], "WAREHOUSE_NAME\": \"", "\",", 1);
            if (!StringUtil.isEmpty(danweimingchen) && !danweimingchen.equals("襄阳医药")) {
                continue;
            }
            data = new ArrayList<String> ();
            content = addRows (content,data, title);
            addCell (data, DateUtil.getBeforeDayAgainstToday (0, "yyyy-MM-dd"), 1);
            addCell (data, StringUtil.getValue(rows[j], "GOODS_NAME\": \"", "\",", 1), 2);
            addCell (data, StringUtil.getValue(rows[j], "GOODS_SPEC\": \"", "\",", 1), 3);
            addCell (data, StringUtil.getValue(rows[j], "\"QUANTITY\": ", ",", 1), 4);
            addCell (data, StringUtil.getValue(rows[j], "GOODS_ID\": \"", "\",", 1), 5);
            addCell (data, StringUtil.getValue(rows[j], "MAKER_NAME\": \"", "\",", 1), 7);
            addCell (data, StringUtil.getValue(rows[j], "UNIT_NAME\": \"", "\",", 1), 8);
            addCell (data, StringUtil.getValue(rows[j], "PRODUCT_DATE\": \"", "\",", 1), 9);
            addCell (data, StringUtil.getValue(rows[j], "MAKER_NAME\": \"", "\",", 1), 10);
        }
        return content;
    }


    @Override
    public List<String> createStock () {
        return TitleConfig.getTitle ().get (8);
    }

    public static void main (String[] args) throws Exception {
        CloseableHttpClient client = WebUtil.getHttpClient ();
        Map<String, String> stringStringMap = new HashMap<> ();
        Customer customer = new Customer ();
        customer.setOrgcode ("DM0502979");
        customer.setOrgname ("华润襄阳医药有限公司");
        new CodeDM0502979Action ().exec (client, stringStringMap, customer);
    }
}


