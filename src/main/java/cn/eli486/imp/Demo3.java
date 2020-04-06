package cn.eli486.imp;

import cn.eli486.entity.Customer;
import cn.eli486.excel.Abstraction;
import cn.eli486.utils.DateUtil;
import cn.eli486.utils.StringUtil;
import cn.eli486.utils.WebUtil;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.*;

public class Demo3 extends Abstraction {
    @Override
    protected void login (CloseableHttpClient client, Map<String, String> params) throws Exception {
        String str = WebUtil.httpGet (client, "http://218.86.35.195:8008/");
        String eventvalidation = StringUtil.getValue(str,
                "<input type=\"hidden\" name=\"__EVENTVALIDATION\" id=\"__EVENTVALIDATION\" value=\"", "\" />", 1);
        String viewstate = StringUtil.getValue(str,
                "<input type=\"hidden\" name=\"__VIEWSTATE\" id=\"__VIEWSTATE\" value=\"", "\" />", 1);


        params.put("__VIEWSTATE", viewstate);
        params.put("__EVENTVALIDATION", eventvalidation);
        params.put("txtadmin", "57");
        params.put("txtpwd", "1");
        params.put("ibtnenter.x", "97");
        params.put("ibtnenter.y", "47");
        WebUtil.httpPost (client, params, "http://218.86.35.195:8008/default.aspx");
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
        Map<String, String> params = new HashMap<String, String>();
        String startDate = DateUtil.getBeforeDayAgainstToday(60, "yyyy-MM-dd");
        String endDate = DateUtil.getBeforeDayAgainstToday(1, "yyyy-MM-dd");

        String str = WebUtil.httpGet (client, "http://218.86.35.195:8008/default5.aspx");
        String eventvalidation = StringUtil.getValue(str,
                "<input type=\"hidden\" name=\"__EVENTVALIDATION\" id=\"__EVENTVALIDATION\" value=\"", "\" />", 1);
        String viewstate = StringUtil.getValue(str,
                "<input type=\"hidden\" name=\"__VIEWSTATE\" id=\"__VIEWSTATE\" value=\"", "\" />", 1);

        params.put("__VIEWSTATE", viewstate);
        params.put("__EVENTVALIDATION", eventvalidation);
        params.put("TextBox1", "57");
        params.put("TextBox2", startDate);
        params.put("TextBox3", endDate);
        params.put("Button2", "流向查询");
        str=WebUtil.httpPost (client, params, "http://218.86.35.195:8008/default5.aspx");
        str=StringUtil.getValue(str, "</caption>", "</table>", 1);
        String[] trs = str.split("<tr");
        List<String> rows= null;
        List<List<String>> content = new ArrayList<> ();

        for (int i = 2; i < trs.length; i++) {
            String[] tds = trs[i].replaceAll ("<font color=\"#\\d+\" size=\"2\">","").split("/td>");
            rows= new ArrayList<String> ();
            if(StringUtil.getValue(tds[1], ">", "<", 1).equals("采购入库单")) {
                continue;
            }
            String salesDate = StringUtil.getValue(tds[0], "<td>", "<", 1);
            addRows(content,rows, title);
            addCell(rows, salesDate, 1);
            addCell(rows, StringUtil.getValue(tds[5], ">", "<", 1),2);
            addCell(rows, StringUtil.getValue(tds[1], ">", "<", 1),4);
            addCell(rows, StringUtil.getValue(tds[2], ">", "<", 1),5);
            addCell(rows, StringUtil.getValue(tds[9], ">", "<", 1),6);
            addCell(rows, StringUtil.getValue(tds[11], ">", "<", 1),12);
            addCell(rows, StringUtil.getValue(tds[3], ">", "<", 1),13);
            addCell(rows, StringUtil.getValue(tds[10], ">", "<", 1),7);
        }
        return content;
    }

    @Override
    public List<String> createSale () {
        return null;
    }

    @Override
    public List<List<String>> getStock (CloseableHttpClient client,  List<String> title) throws IOException {
        String startDate = DateUtil.getBeforeDayAgainstToday(60, "yyyy-MM-dd");
        String endDate = DateUtil.getBeforeDayAgainstToday(1, "yyyy-MM-dd");
        Map<String, String> params = new HashMap<> ();
        String str = WebUtil.httpGet (client, "http://218.86.35.195:8008/default5.aspx");


        String eventvalidation = StringUtil.getValue(str,
                "<input type=\"hidden\" name=\"__EVENTVALIDATION\" id=\"__EVENTVALIDATION\" value=\"", "\" />", 1);
        String viewstate = StringUtil.getValue(str,
                "<input type=\"hidden\" name=\"__VIEWSTATE\" id=\"__VIEWSTATE\" value=\"", "\" />", 1);
        params.put("__VIEWSTATE", viewstate);
        params.put("__EVENTVALIDATION", eventvalidation);
        params.put("TextBox1", "57");
        params.put("TextBox2", startDate);
        params.put("TextBox2", endDate);
        params.put("Button1", "库存查询");
        str=WebUtil.httpPost (client, params, "http://218.86.35.195:8008/default5.aspx");
        str=StringUtil.getValue(str, "</caption>", "</table>", 1);
        String[] trs = str.split("<tr");
        List<String> rows= null;
        List<List<String>> content = new ArrayList<> ();
        for (int i = 2; i < trs.length; i++) {
            String[] tds = trs[i].replaceAll ("<font color=\"#\\d+\" size=\"2\">","").split("/td>");rows= new ArrayList<String> ();
            addRows(content,rows, title);
            addCell(rows, StringUtil.getValue(tds[0], "<td>", "<", 1), 2);
            addCell(rows, StringUtil.getValue(tds[1], ">", "<", 1),3);
            addCell(rows, StringUtil.getValue(tds[4], ">", "<", 1),4);
            addCell(rows, StringUtil.getValue(tds[5], ">", "<", 1),5);
            addCell(rows, StringUtil.getValue(tds[6], ">", "<", 1),7);
            addCell(rows, StringUtil.getValue(tds[2], ">", "<", 1),6);
        }
        return content;
    }

    @Override
    public List<String> createStock () {
        return null;
    }

    public static void main (String[] args) throws Exception {
        CloseableHttpClient client = WebUtil.getHttpClient ();
        Map<String, String> stringStringMap = new HashMap<> ();
        Customer customer = new Customer ();
        customer.setOrgcode ("W169027");
        customer.setOrgname ("聚善堂（福建）医药集团有限公司");
        customer.setFilesName (Arrays.asList ("a","b","c"));
        new Demo3 ().exec (client,stringStringMap,customer);
    }
}
