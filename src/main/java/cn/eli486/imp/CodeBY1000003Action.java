package cn.eli486.imp;

import cn.eli486.config.GlobalInfo;
import cn.eli486.config.TitleConfig;
import cn.eli486.entity.Customer;
import cn.eli486.excel.Abstraction;
import cn.eli486.utils.StringUtil;
import cn.eli486.utils.WebUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author eli
 */
public class CodeBY1000003Action extends Abstraction {
    String token;
    String headerName = "X-Auth-Token";

    @Override
    protected void login (CloseableHttpClient client, Map<String, String> params) throws Exception {
        String str = WebUtil.httpGet (client, "http://www.hnjxyy.com.cn");
        str = WebUtil.httpGet (client, "http://58.47.159.179:9000/");
        str = WebUtil.httpGet (client, "http://58.47.159.179:9000/liux");
        str = WebUtil.httpGet (client, "http://58.47.159.179:9000/liux#!/login");
        params.put ("username", "A00000131");
        params.put ("password", "A00000131");
        params.put ("rememberMe", "false");
        str = WebUtil.httpPost (client, params, "http://58.47.159.179:9000/api/user/authenticate");
        token = StringUtil.getValue (str, "access_token\":\"", "\"", 1);
        str = WebUtil.httpGet (client, "http://58.47.159.179:9000/api/user", headerName, token);
    }

    @Override
    public List<List<String>> getPurchase (CloseableHttpClient client, List<String> title) throws Exception{
        List<List<String>> content = new ArrayList<> ();
        List<String> data;
        String result = WebUtil.httpGet (client, "http://58.47.159.179:9000/liux#!/main/purchase", headerName, token);
        String json = "{\"type\":\"purchase\",\"startDate\":\"" + GlobalInfo.BEGIN_DATE + "T14:00:39.960Z\",\"endDate\":\"" + GlobalInfo.END_DATE + "T14:00:39.960Z\",\"owner\":\"ZDA\"}";
        result = WebUtil.httpPostJson (client, json, "http://58.47.159.179:9000/api/flow/purchase", headerName, token);
        JSONObject jsonObject = JSON.parseObject (result);
        JSONArray recordList = jsonObject.getJSONArray ("recordList");
        //{"rq":"2020-03-10","djbh":"JHAZDA00067683","pihao":"9A196293","sxrq":"2021-01-14","baozhiqi":"","shl":200.0,"dj":36.64,"je":7328.0,"zhy":"进货入库确认","spid":"SPH00105146","spbh":"01012355","spmch":"儿童维D钙咀嚼片/迪巧","shpgg":"30片","jlgg":200,"dw":"盒","pizhwh":"国药准字J20140153","jixing":"片剂","shpchd":"安士制药（中山）有限公司","danwbh":"Y010100209","dwmch":"湖南九州通医药有限公司"}
        for (int i = 1; i < recordList.size (); i++) {
            jsonObject = recordList.getJSONObject (i);
            data = new ArrayList<> ();
            content = addRows (content, data, title);
            addCell (data, jsonObject.getString ("rq"), 1);
            addCell (data, jsonObject.getString ("shpchd"), 2);
            addCell (data, jsonObject.getString ("danwbh"), 3);
            addCell (data, jsonObject.getString ("dwmch"), 4);
            addCell (data, jsonObject.getString ("spid"), 5);
            addCell (data, jsonObject.getString ("spmch"), 6);
            addCell (data, jsonObject.getString ("shpgg"), 7);
            addCell (data, jsonObject.getString ("jixing"), 8);
            addCell (data, jsonObject.getString ("dw"), 9);
            addCell (data, jsonObject.getString ("pihao"), 10);
            addCell (data, jsonObject.getString ("shl"), 11);
            addCell (data, jsonObject.getString ("dj"), 12);
            addCell (data, jsonObject.getString ("je"), 13);
            addCell (data, jsonObject.getString ("zhy"), 14);
            addCell (data, jsonObject.getString ("djbh"), 15);
            addCell (data, jsonObject.getString ("sxrq"), 16);
        }
        return content;
    }

    @Override
    public List<String> createPurchase () {
        return TitleConfig.getTitle ().get (12);
    }

    @Override
    public List<List<String>> getSale (CloseableHttpClient client,  List<String> title) throws IOException {
        List<List<String>> content = new ArrayList<> ();
        List<String> data;
        String result = WebUtil.httpGet (client, "http://58.47.159.179:9000/liux#!/main/sale", headerName, token);

        String json = "{\"type\":\"sale\",\"startDate\":\"" + GlobalInfo.BEGIN_DATE + "T14:00:39.960Z\",\"endDate\":\"" + GlobalInfo.END_DATE + "T14:00:39.960Z\",\"owner\":\"ZDA\"}";
        result = WebUtil.httpPostJson (client, json, "http://58.47.159.179:9000/api/flow/sale", headerName, token);
        JSONObject jsonObject = JSON.parseObject (result);
        JSONArray recordList = jsonObject.getJSONArray ("recordList");
        //{"rq":"2020-02-26","djbh":"XSAZDA00499885","pihao":"9A196293","sxrq":"2021-01-14","baozhiqi":"","shl":30.0,"dj":37.0,"je":1110.0,"zhy":"销售出库确认","spid":"SPH00105146","spbh":"01012355","spmch":"儿童维D钙咀嚼片/迪巧","shpgg":"30片","jlgg":200,"dw":"盒","pizhwh":"国药准字J20140153","jixing":"片剂","shpchd":"安士制药（中山）有限公司","danwbh":"X010400105","dwmch":"常德市华斌医药有限公司"}
        for (int i = 1; i < recordList.size (); i++) {
            jsonObject = recordList.getJSONObject (i);
            data = new ArrayList<> ();
            content = addRows (content, data, title);
            addCell (data, jsonObject.getString ("rq"), 1);
            addCell (data, jsonObject.getString ("shpchd"), 2);
            addCell (data, jsonObject.getString ("danwbh"), 3);
            addCell (data, jsonObject.getString ("dwmch"), 4);
            addCell (data, jsonObject.getString ("spid"), 5);
            addCell (data, jsonObject.getString ("spmch"), 6);
            addCell (data, jsonObject.getString ("shpgg"), 7);
            addCell (data, jsonObject.getString ("jixing"), 8);
            addCell (data, jsonObject.getString ("dw"), 9);
            addCell (data, jsonObject.getString ("pihao"), 10);
            addCell (data, jsonObject.getString ("shl"), 11);
            addCell (data, jsonObject.getString ("dj"), 12);
            addCell (data, jsonObject.getString ("je"), 13);
            addCell (data, jsonObject.getString ("zhy"), 14);
            addCell (data, jsonObject.getString ("djbh"), 17);
        }
        return content;
    }


    @Override
    public List<String> createSale () {
        return TitleConfig.getTitle ().get (11);
    }

    @Override
    public List<List<String>> getStock (CloseableHttpClient client, List<String> title) throws IOException {
        List<List<String>> content = new ArrayList<> ();
        List<String> data;
        String result = WebUtil.httpGet (client, "http://58.47.159.179:9000/liux/partials/stock.html", headerName, token);
        String json = "{\"type\":\"stock\",\"owner\":\"ZDA\"}";
        result = WebUtil.httpPostJson (client, json, "http://58.47.159.179:9000/api/flow/stock", headerName, token);
        JSONObject jsonObject = JSON.parseObject (result);
        JSONArray recordList = jsonObject.getJSONArray ("recordList");
        //{"pihao":"8H166548","sxrq":"2021-08-22","baozhiqi":"2018-08-23","shl":338.0,"spbh":"01012360","spmch":"维D钙咀嚼片/迪巧","shpgg":"60片/瓶","jlgg":200,"dw":"盒","pizhwh":"国药准字J20140154","jixing":"片剂","shpchd":"安士制药（中山）有限公司"}
        for (int i = 1; i < recordList.size (); i++) {
            jsonObject = recordList.getJSONObject (i);
            data = new ArrayList<> ();
            content = addRows (content, data, title);
            addCell (data, jsonObject.getString ("shpchd"), 2);
            addCell (data, jsonObject.getString ("spbh"), 3);
            addCell (data, jsonObject.getString ("spmch"), 4);
            addCell (data, jsonObject.getString ("dw"), 7);
            addCell (data, jsonObject.getString ("shpgg"), 5);
            addCell (data, jsonObject.getString ("jixing"), 6);
            addCell (data, jsonObject.getString ("pihao"), 8);
            addCell (data, jsonObject.getString ("shl"), 9);
            addCell (data, jsonObject.getString ("sxrq"), 13);
        }
        return content;
    }


    @Override
    public List<String> createStock () {
        return TitleConfig.getTitle ().get (10);
    }

    public static void main (String[] args) throws Exception {
        CloseableHttpClient client = WebUtil.getHttpClient ();
        Map<String, String> stringStringMap = new HashMap<> ();
        Customer customer = new Customer ();
        customer.setOrgcode ("BY1000003");
        customer.setOrgname ("湖南津湘药业有限公司");
        new CodeBY1000003Action ().exec (client, stringStringMap, customer);
    }
}


