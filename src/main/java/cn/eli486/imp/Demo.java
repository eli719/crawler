package cn.eli486.imp;

import cn.eli486.excel.ExcelDemo;
import cn.eli486.util.Title;
import cn.eli486.util.WebUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demo extends ExcelDemo {
    @Override
    protected void login (CloseableHttpClient client, Map<String, String> params) throws Exception {
        params.clear ();
        params.put("username", "西安杨森制药有限公司");
        params.put("password", "888888");
        String str = WebUtil.httpPost (client, params, "http://www.yaozhicai.cn:8081/a/login");
    }

    @Override
    public List<List<String>> getPurchase (CloseableHttpClient client, String orgName, Title<String> title) {
        return null;
    }

    @Override
    public Title<String> createPurchase () {
        return null;
    }

    @Override
    public List<List<String>> getSale (CloseableHttpClient client, String orgName, Title<String> title) throws IOException {
        return null;
    }

    @Override
    public Title<String> createSale () {
        return null;
    }

    @Override
    public List<List<String>> getStock (CloseableHttpClient client, String orgName, Title<String> title) throws IOException {
        String str = WebUtil.httpGet (client, "http://www.yaozhicai.cn:8081/a/stock/stock/list1");
        List<String> rows=null;
        String stockFile = "D:\\XJPFile\\auto\\VW703310_山东益昶源医药有限公司.xlsx";
        HttpPost httppost = new HttpPost("http://www.yaozhicai.cn:8081/a/stock/stock/export");
        HttpResponse response = client.execute(httppost);
        HttpEntity respEntity = response.getEntity();
        File storeFile = new File(stockFile);
        FileOutputStream output = new FileOutputStream(storeFile);
        respEntity.writeTo(output);
        output.flush();
        output.close();

        XSSFWorkbook sfb = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(storeFile);
            sfb = new XSSFWorkbook(fis);
            XSSFSheet sheet = sfb.getSheetAt(0);
            XSSFRow row = null;
            for (int i = 2; i < sheet.getPhysicalNumberOfRows(); i++) {
                row = sheet.getRow(i);
                rows= new ArrayList<String> ();
                addRows(rows, title);
                addCell(rows, String.valueOf(row.getCell(1)), 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sfb != null) {
                sfb.close();
            }
            if (fis != null) {
                fis.close();
            }
            if (new File(stockFile).exists()) {
                new File(stockFile).delete();
            }
        }
        return content;
    }

    @Override
    public Title<String> createStock () {
        Title<String> title = defaultStock ();
        title.addAfter ("数量","E");
        return title;
    }

    public static void main (String[] args) throws Exception {
        CloseableHttpClient client = WebUtil.getHttpClient ();
        Map<String, String> stringStringMap = new HashMap<> ();
        new Demo ().exec (client,stringStringMap,"a","b");
    }
}
