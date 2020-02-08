package cn.eli486.imp;

import cn.eli486.util.FileUtil;
import cn.eli486.util.StringUtil;
import cn.eli486.util.Title;
import cn.eli486.util.WebUtil;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author eli
 */
public class Demo2 extends VerifyDemo {
	@Override
	protected void login (CloseableHttpClient client, Map<String, String> params) throws Exception {

	}

	@Override
	protected String getLoginUrl () {
		return "http://114.255.42.204:83/index.aspx";
	}

	@Override
	public void addVerifyCodeParam (String verifyCode) {
		loginParams.put("captcha", verifyCode);
	}

	@Override
	protected String getPictureUrl () {
		return "http://114.255.42.204:83/checkcode.ashx";
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
		return null;
	}

	@Override
	public Title<String> createStock () {
		return null;
	}


	public static void main (String[] args) {
		CloseableHttpClient client = WebUtil.getHttpClient ();
		WebUtil.requestFile(client, "http://114.255.42.204:83/checkcode.ashx", "D:\\2.jpg");
		Map<String,String> params = new HashMap<> ();
		try {
			String code = FileUtil.readFile ("D:/verify.txt", "UTF-8").trim();
			String str = WebUtil.httpGet (client, "http://114.255.42.204:83/index.aspx");

			String viewState= StringUtil.getValue(str, "name=\"__VIEWSTATE\" id=\"__VIEWSTATE\" value=\"","\" />", 1);
			String EVENTVALIDATION= StringUtil.getValue(str, "name=\"__EVENTVALIDATION\" id=\"__EVENTVALIDATION\" value=\"","\" />", 1);

			params.put("__VIEWSTATE", viewState);
			params.put("__EVENTVALIDATION", EVENTVALIDATION);
			params.put("txtname", "XAYS");
			params.put("txtpwd", "a12345");
			params.put("ImgSubmit", "登录验证中...");
			params.put("txtcheckcode", code);
			new Demo2 ().exec (client,params,"1","asd");
		} catch (Exception e) {
			e.printStackTrace ();
		}
	}
}
