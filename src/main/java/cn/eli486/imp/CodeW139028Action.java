package cn.eli486.imp;

import cn.eli486.config.TitleConfig;
import cn.eli486.entity.Customer;
import cn.eli486.excel.Abstraction;
import cn.eli486.utils.DateUtil;
import cn.eli486.utils.StringUtil;
import cn.eli486.utils.WebUtil;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author eli
 */
public class CodeW139028Action extends Abstraction {
    @Override
    protected void login (CloseableHttpClient client, Map<String, String> params) throws Exception {
        String str = WebUtil.httpGet (client, "http://112.11.203.72:30/Login.aspx");
        String eventValidation = StringUtil.getValue (str,
                "<input type=\"hidden\" name=\"__EVENTVALIDATION\" id=\"__EVENTVALIDATION\" value=\"", "\" />", 1);
        String viewState = StringUtil.getValue (str,
                "<input type=\"hidden\" name=\"__VIEWSTATE\" id=\"__VIEWSTATE\" value=\"", "\" />", 1);
        params.put ("__VIEWSTATE", viewState);
        params.put ("__EVENTVALIDATION", eventValidation);
        params.put ("tbLoginName", "xays");
        params.put ("tbPassword", "hsyy2014");
        params.put ("btnLogin", "111111");
        WebUtil.httpPost (client, params, "http://112.11.203.72:30/Login.aspx");
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
        List<List<String>> content = new ArrayList<> ();
        List<String> data;
        String results = WebUtil.httpGet (client, "http://112.11.203.72:30/SaleQuery.aspx");

        String eventValidation = StringUtil.getValue(results,
                "<input type=\"hidden\" name=\"__EVENTVALIDATION\" id=\"__EVENTVALIDATION\" value=\"", "\" />", 1);
        String viewState = StringUtil.getValue(results,
                "<input type=\"hidden\" name=\"__VIEWSTATE\" id=\"__VIEWSTATE\" value=\"", "\" />", 1);
        String bdate = DateUtil.getBeforeDayAgainstToday(60, "yyyy-MM-dd");
        String edate = DateUtil.getBeforeDayAgainstToday(1, "yyyy-MM-dd");
        Map<String, String> params = new HashMap<String, String>();
        params.put("__EVENTTARGET", "");
        params.put("__EVENTARGUMENT", "");
        params.put("__VIEWSTATE", viewState);
        params.put("__EVENTVALIDATION", eventValidation);
        params.put("ctl00$ContentPlaceHolder1$beginDate", bdate);
        params.put("ctl00$ContentPlaceHolder1$endDate", edate);
        params.put("ctl00$ContentPlaceHolder1$ddlSelectableGoods", "0");
        params.put("ctl00$ContentPlaceHolder1$btnQuery", "查询");

        String result2 = WebUtil.httpPost (client, params, "http://112.11.203.72:30/SaleQuery.aspx");
        eventValidation = StringUtil.getValue(results,
                "<input type=\"hidden\" name=\"__EVENTVALIDATION\" id=\"__EVENTVALIDATION\" value=\"", "\" />", 1);
        viewState = StringUtil.getValue(results,
                "<input type=\"hidden\" name=\"__VIEWSTATE\" id=\"__VIEWSTATE\" value=\"", "\" />", 1);
        params.clear();
        params.put("__EVENTTARGET", "");
        params.put("__EVENTARGUMENT", "");
        params.put("__VIEWSTATE", viewState);
        params.put("__EVENTVALIDATION", eventValidation);
        params.put("ctl00$ContentPlaceHolder1$beginDate", bdate);
        params.put("ctl00$ContentPlaceHolder1$endDate", edate);
        params.put("ctl00$ContentPlaceHolder1$ddlSelectableGoods", "0");
        params.put("ctl00$ContentPlaceHolder1$btnExport", "导出");
        result2 = WebUtil.httpPost (client, params, "http://112.11.203.72:30/SaleQuery.aspx");

        String url = "http://112.11.203.72:30"+StringUtil.getValue(result2, "<a href=\"", "\"", 1);
        String filePath = "D:\\XJPFile\\auto\\139028_sale.xls";
        WebUtil.getFile (client, url, filePath);

        HSSFWorkbook workbook ;

        try (FileInputStream fis = new FileInputStream (filePath)) {
            workbook = new HSSFWorkbook (fis);
            HSSFSheet sheet = workbook.getSheetAt (0);
            for (int i = 1; i < sheet.getPhysicalNumberOfRows (); i++) {
                HSSFRow row = sheet.getRow (i);
                data = new ArrayList<> ();
                content = addRows (content, data, title);
                addCell (data, row.getCell (0).toString (), 7);
                addCell (data, row.getCell (1).toString (), 2);
                addCell (data, row.getCell (2).toString (), 4);
                addCell (data, row.getCell (3).toString (), 5);
                addCell (data, row.getCell (9).toString (), 6);
                addCell (data, row.getCell (6).toString (), 9);
                addCell (data, row.getCell (5).toString (), 11);
                addCell (data, row.getCell (8).toString (), 19);
            }
        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            if (new File (filePath).exists ()) {
                new File (filePath).delete ();
            }
        }
        return content;
    }


    @Override
    public List<String> createSale () {
        return TitleConfig.getTitle ().get (9);
    }

    @Override
    public List<List<String>> getStock (CloseableHttpClient client,  List<String> title) throws IOException {
        List<List<String>> content = new ArrayList<> ();
        List<String> data;
        String result = WebUtil.httpGet (client, "http://112.11.203.72:30/StockQuery.aspx");


        String viewState = StringUtil.getValue (result, "__VIEWSTATE\" value=\"", "\"", 1);
        String eventvalidation = StringUtil.getValue (result, "__EVENTVALIDATION\" value=\"", "\"", 1);
        Map<String, String> params = new HashMap<> ();
        params.put ("viewState", viewState);
        params.put ("eventvalidation", eventvalidation);
        params.put ("ctl00$ContentPlaceHolder1$ddlSelectableGoods", "0");
        params.put ("ctl00$ContentPlaceHolder1$btnQuery", "查询");
        result = WebUtil.httpPost (client, params, "http://112.11.203.72:30/StockQuery.aspx");
        viewState = StringUtil.getValue (result, "__VIEWSTATE\" value=\"", "\"", 1);
        eventvalidation = StringUtil.getValue (result, "__EVENTVALIDATION\" value=\"", "\"", 1);
        params.clear ();
        params.put ("__VIEWSTATE", viewState);
        params.put ("__EVENTVALIDATION", eventvalidation);
        params.put ("ctl00$ContentPlaceHolder1$ddlSelectableGoods", "0");
        params.put ("ctl00$ContentPlaceHolder1$btnExport", "导出");
        result = WebUtil.httpPost (client, params, "http://112.11.203.72:30/StockQuery.aspx");
        String url = "http://112.11.203.72:30" + StringUtil.getValue (result, "<a href=\"", "\"", 1);
        String filePath = "D:\\XJPFile\\auto\\139028_stock.xls";
        WebUtil.getFile (client, url, filePath);

        HSSFWorkbook workbook;

        try (FileInputStream fis = new FileInputStream (filePath)) {
            workbook = new HSSFWorkbook (fis);
            HSSFSheet sheet = workbook.getSheetAt (0);
            for (int i = 1; i < sheet.getPhysicalNumberOfRows (); i++) {
                HSSFRow row = sheet.getRow (i);
                data = new ArrayList<> ();
                content = addRows (content, data, title);
                addCell (data, row.getCell (0).toString (), 3);
                addCell (data, row.getCell (1).toString (), 4);
                addCell (data, row.getCell (5).toString (), 7);
                addCell (data, row.getCell (9).toString (), 5);
                addCell (data, row.getCell (7).toString (), 6);
                addCell (data, row.getCell (8).toString (), 8);
            }
        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            if (new File (filePath).exists ()) {
                new File (filePath).delete ();
            }
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
        customer.setOrgcode ("W139028");
        customer.setOrgname ("浙江华圣药业集团有限公司");
        new CodeW139028Action ().exec (client, stringStringMap, customer);
    }
}


