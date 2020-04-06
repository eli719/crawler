package cn.eli486.imp;

import cn.eli486.excel.AbstractionVerify;
import cn.eli486.utils.StringUtil;
import cn.eli486.utils.WebUtil;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author eli
 */
public class Demo2 extends AbstractionVerify {

    @Override
    protected void login () throws IOException {
        System.out.println (client);
        String str = WebUtil.httpGet (client, "https://redirect.gzmpc.com/gzmpcscm3/lxlogin.jsp");
        str = WebUtil.httpPost (client, loginParams, "https://redirect.gzmpc.com/gzmpcscm3/loginactionservlet");
    }

    @Override
    protected void login (CloseableHttpClient client, Map<String, String> params) throws Exception {

    }

    @Override
    protected String getLoginUrl () {
        return "https://redirect.gzmpc.com/gzmpcscm3/lxlogin.jsp";
    }

    @Override
    public void addVerifyCodeParam (String verifyCode) {
        loginParams.put ("validateword", verifyCode);
    }

    @Override
    protected String getPictureUrl () {
        return "https://redirect.gzmpc.com/gzmpcscm3/PicCheckCode";
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
        String str = WebUtil.httpGet (client,
                "https://redirect.gzmpc.com/gzmpcscm3/extjsgridQueryServlet/query?_dc=1573006942787&startIndex=0&pageRowNum=15&needpagecount=true&gridcode=func-salesstquery-grid&queryType=query&dataSource=salesstqueryquery&stagetype=SALESFLOWS&stageid=24&querymoduleid=FuncSalesstqueryNorQuery&sumfieldnames=goodsqty&oper_length=0&page=1&start=0&limit=25");
        String num = StringUtil.getValue (str, "totalrows\":\"", "\",\"", 1);
        str = WebUtil.httpGet (client,
                "https://redirect.gzmpc.com/gzmpcscm3/extjsgridQueryServlet/query?_dc=1572682155855&startIndex=0&pageRowNum="
                        + num
                        + "&needpagecount=true&gridcode=func-salesstquery-grid&queryType=query&dataSource=salesstqueryquery&stagetype=SALESFLOWS&stageid=24&querymoduleid=FuncSalesstqueryNorQuery&sumfieldnames=goodsqty&oper_length=0&page=1&start=0&limit=25");
        str = StringUtil.getValue (str, "\"rows\":[", "]}", 1);
        String[] trs = str.split ("}");
        List<List<String>> content = new ArrayList<> ();
        for (int i = 0; i < trs.length - 1; i++) {
            rows = new ArrayList<String> ();
            addRows(content,rows, title);
            addCell(rows, StringUtil.getValue (trs[i], "\"goodsname\":\"", "\",", 1), 2);
            addCell(rows, StringUtil.getValue (trs[i], "\"goodstype\":\"", "\",", 1),3);
            addCell(rows, StringUtil.getValue (trs[i], "\"goodsqty\":\"", "\",", 1),4);
            addCell(rows, StringUtil.getValue (trs[i], "\"lotno\":\"", "\"", 1),5);
            addCell(rows, StringUtil.getValue (trs[i], "\"invaliddate\":\"", "\",", 1),7);
            addCell(rows, StringUtil.getValue (trs[i], "\"goodsunit\":\"", "\",", 1),6);
        }
        return content;
    }

    @Override
    public List<String> createStock () {
        return null;
    }

}
