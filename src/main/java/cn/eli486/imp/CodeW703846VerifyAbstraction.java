package cn.eli486.imp;

import cn.eli486.config.TitleConfig;
import cn.eli486.excel.AbstractionVerify;
import cn.eli486.utils.DateUtil;
import cn.eli486.utils.StringUtil;
import cn.eli486.utils.WebUtil;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeW703846VerifyAbstraction extends AbstractionVerify {
    @Override
    protected String getLoginUrl () {
        return "http://lx.jmjyt.com/login.php";
    }

    @Override
    public void addVerifyCodeParam (String verifyCode) {
        loginParams.put("num", verifyCode);
        loginParams.put("login", " 提交 ");
    }

    @Override
    protected String getPictureUrl () {
        return "http://lx.jmjyt.com/anipic.php";
    }

    @Override
    protected void login() throws IOException {
        WebUtil.httpPost (client, loginParams,"http://lx.jmjyt.com/login.php");
        WebUtil.httpGet (client, "http://lx.jmjyt.com/home.php");
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
        List<String> data = null;
        List<List<String>> content = new ArrayList<> ();
        String str = WebUtil.httpGet (client, "http://lx.jmjyt.com/goto.php?med_id=100396&med_name=%E5%88%A9%E5%9F%B9%E9%85%AE%E7%89%87%28%E7%BB%B4%E6%80%9D%E9%80%9A%29&med_unit=%E7%9B%92");
        str = WebUtil.httpGet (client, "http://lx.jmjyt.com/medinfo.php");
        int m=str.indexOf("状态");
        str=str.substring(m, str.length());
        String[] rows = str.split("<tr>");
        String tongyongming="";
        String guige="";
        for (int i = 1; i < rows.length; i++) {
            String a=StringUtil.getValue(rows[i], "<a href=","</td>", 1);
            if(tongyongming.equals(StringUtil.getValue(a, "'>","</a>", 1))&&guige.equals(StringUtil.getValue(rows[i], "align=\"center\">","&nbsp;</td>", 2))){
                continue;
            }
            tongyongming=StringUtil.getValue(a, "'>","</a>", 1);
            guige=StringUtil.getValue(rows[i], "align=\"center\">","&nbsp;</td>", 2);
            String bianma=StringUtil.getValue(rows[i], "align=\"center\">","</td>", 1);
            String danwei=StringUtil.getValue(rows[i], "align=\"center\">","&nbsp;</td>", 5);
            String chandi=StringUtil.getValue(rows[i], "align=\"center\">","&nbsp;</td>", 3);
            String changjia=StringUtil.getValue(rows[i], "align=\"center\">","&nbsp;</td>", 4);

            Map<String, String> params = new HashMap<String, String> ();
            params.clear();
            params.put("med_id", bianma);
            params.put("med_name", tongyongming);
            params.put("med_unit", danwei);
            str = WebUtil.httpPost (client, params, "http://lx.jmjyt.com/goto.php?med_id="+bianma+"&med_name=%E5%88%A9%E5%9F%B9%E9%85%AE%E7%89%87%28%E7%BB%B4%E6%80%9D%E9%80%9A%29&med_unit=%E7%9B%92");
            //                                           http://lx.jmjyt.com/goto.php?med_id=100671&med_name=%E7%9B%90%E9%85%B8%E6%B4%9B%E5%93%8C%E4%B8%81%E8%83%BA%E8%83%B6%E5%9B%8A%28%E6%98%93%E8%92%99%E5%81%9C%29&med_unit=%E7%9B%92
            int n=str.indexOf("生效日期");
            if(n<0){continue;}
            str=str.substring(n,str.length());
            int n1=str.indexOf("汇总");
            if(n1<0){continue;}
            str=str.substring(0,n1-40);

            String[] rows1 = str.split("<tr>");
            for (int j = 1; j < rows1.length; j++) {
                String k=StringUtil.getValue(rows1[j], "align=\"center\">","</td>", 4);
                int saledate=Integer.parseInt(StringUtil.getValue(rows1[j], "align=\"center\">","</td>", 6).replaceAll("-", "").trim());
                int min=Integer.parseInt(DateUtil.getBeforeDayAgainstToday(60,"yyyy-MM-dd").toString().replaceAll("-", "").trim());
                int max=Integer.parseInt(DateUtil.getBeforeDayAgainstToday(1,"yyyy-MM-dd").toString().replaceAll("-", "").trim());
                if(!k.equals("配送单") && !k.equals("批发单")&& !k.equals("批发退货单"))
                {
                    continue;
                }
                if(saledate >= min && saledate <= max){
                    data = new ArrayList<String> ();
                    addRows(content,data, title);
                    addCell(data, StringUtil.getValue(rows1[j], "align=\"center\">",
                            "</td>", 6), 1);
                    addCell(data, StringUtil.getValue(rows1[j], "align=\"center\">",
                            "</td>", 5), 2);
                    addCell(data, tongyongming, 3);
                    addCell(data, guige, 4);
                    addCell(data, StringUtil.getValue(rows1[j], "align=\"center\">",
                            "</td>", 2), 5);
                    addCell(data, bianma, 6);
                    addCell(data, StringUtil.getValue(rows1[j], "align=\"center\">",
                            "</td>", 1), 12);
                    addCell(data, StringUtil.getValue(rows1[j], "align=\"center\">",
                            "</td>", 4), 13);
                }
            }
        }
        return content;
    }

    @Override
    public List<String> createSale () {
        return TitleConfig.getTitle ().get (3);
    }

    @Override
    public List<List<String>> getStock (CloseableHttpClient client,  List<String> title) throws IOException {
        List<String> data = null;
        List<List<String>> content = new ArrayList<> ();
        String result = WebUtil.httpGet (client, "http://lx.jmjyt.com/home.php");
        result = WebUtil.httpGet (client, "http://lx.jmjyt.com/medinfo.php");
        int m=result.indexOf("状态");
        result=result.substring(m, result.length());

        String[] stockInfo = null;
        String[] rows = result.split("<tr>");
        String date = DateUtil.getLastDay("yyyy-MM-dd");
        for (int i = 1; i < rows.length; i++) {
            String a=StringUtil.getValue(rows[i], "<a href=","</td>", 1);
            String b=StringUtil.getValue(a, "'>","</a>", 1);
            data = new ArrayList<String> ();
            addRows(content,data, title);
            addCell(data, date, 1);
            addCell(data, b, 2);
            addCell(data, StringUtil.getValue(rows[i], "align=\"center\">",
                    "&nbsp;</td>", 2), 3);
            addCell(data, StringUtil.getValue(rows[i], "width='50' align='center'>",
                    "&nbsp;</td>", 1), 4);
            addCell(data, StringUtil.getValue(rows[i], "align=\"center\">",
                    "&nbsp;</td>", 6), 5);
            addCell(data, StringUtil.getValue(rows[i], "align=\"center\">",
                    "</td>", 1), 6);
            addCell(data, StringUtil.getValue(rows[i], "align=\"center\">",
                    "&nbsp;</td>", 3), 7);
            addCell(data, StringUtil.getValue(rows[i], "align=\"center\">",
                    "&nbsp;</td>", 4), 8);
            addCell(data, StringUtil.getValue(rows[i], "align=\"center\">",
                    "&nbsp;</td>", 5), 9);
            addCell(data, StringUtil.getValue(rows[i], "align=\"center\">",
                    "&nbsp;</td>", 7), 10);
            addCell(data, StringUtil.getValue(rows[i], "width='50' align='center'>",
                    "&nbsp;</td>", 2), 11);
        }
        return content;
    }

    @Override
    public List<String> createStock () {
        return TitleConfig.getTitle ().get (4);
    }
}
