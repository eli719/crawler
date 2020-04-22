package cn.eli486.ocr;

import net.sf.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 获取token类
 */
public class AuthService {
	private final static String AUTH_HOST = "https://aip.baidubce.com/oauth/2.0/token?";
	private final static Long EXPIRE_TIME = 2592000L;
	// 官网获取的 API Key 更新为你注册的
	private final static String API_KEY="AoB0l1kaD644BWz0KWxKDwTc";
	// 官网获取的 Secret Key 更新为你注册的
	private final static String SECRET_KEY="2xKmo6y4UukKz2ivZ7WjoD03MFalCXYL";


	/**
	 * 获取权限token
	 * 
	 * @return 返回示例： { "access_token":
	 *         "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567",
	 *         "expires_in": 2592000 }
	 */
	public static String getAuth() {
		return getAuth(API_KEY, SECRET_KEY);
	}

	/**
	 * 获取API访问token 该token有一定的有效期，需要自行管理，当失效时需重新获取.
	 * 
	 * @param ak - 百度云官网获取的 API Key
	 * @param sk - 百度云官网获取的 Securet Key
	 * @return assess_token 示例：
	 *         "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567"
	 */
	public static String getAuth(String ak, String sk) {
		File file = new File ("token.txt");
		if (!(file.exists ())){
			try {
				file.createNewFile ();
			} catch (IOException e) {
				e.printStackTrace ();
			}
		}
		try(FileInputStream fis = new FileInputStream("token.txt")) {
			InputStreamReader reader = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(reader);
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			line = sb.toString();
			System.out.println(line);
			String[] arr = line.split("@@");
			if (arr.length > 1) {
				String token = arr[0];
				String exp = arr[1];
				Date now = new Date();
				long t = now.getTime();
				long n = t - Long.valueOf(exp);
				if (n < EXPIRE_TIME) {
					return token;
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		// 获取token地址
		String authHost = AUTH_HOST;
		String getAccessTokenUrl = authHost
				// 1. grant_type为固定参数
				+ "grant_type=client_credentials"
				// 2. 官网获取的 API Key
				+ "&client_id=" + ak
				// 3. 官网获取的 Secret Key
				+ "&client_secret=" + sk;
		try {
			URL realUrl = new URL(getAccessTokenUrl);
			// 打开和URL之间的连接
			HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			for (String key : map.keySet()) {
				System.err.println(key + "--->" + map.get(key));
			}
			// 定义 BufferedReader输入流来读取URL的响应
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String result = "";
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			/**
			 * 返回结果示例
			 */
			System.err.println("result:" + result);
			JSONObject jsonObject = JSONObject.fromObject(result);
			String access_token = jsonObject.getString("access_token");
			
			//将token写入文件保存并记录当前日期以便更新
			Date date = new Date();
			access_token = access_token + "@@" + String.valueOf(date.getTime());
			File f = new File("token.txt");
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(access_token.getBytes());
			fos.flush();
			fos.close();
			return access_token;
		} catch (Exception e) {
			System.err.printf("获取token失败！");
			e.printStackTrace(System.err);
		}
		return null;
	}
}
