package cn.eli486.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;

/**
 * @author eli
 */
public class StringUtil {
	
	 public static String unicode2GBK(String dataStr) {   
	       String cache = new String(dataStr);
	       int beginIndex = 0;
	       StringBuffer sb = new StringBuffer();  
	       while (beginIndex >= 0) {
	    	   beginIndex = cache.indexOf("\\u");
		       
		       if (beginIndex < 0){
		    	   sb.append(cache);
		    	   return sb.toString();		    	   
		       }

		       if (beginIndex > 0){
		    	   sb.append(cache.substring(0, beginIndex));
		    	   cache = cache.substring(beginIndex);
		       }

		        String charStr = cache.substring(2,6);   
		        char letter = (char) Integer.parseInt(charStr, 16 );
		        sb.append(letter);
		        
		        cache = cache.substring(6);
		    	beginIndex = cache.indexOf("\\u");
			    if (beginIndex < 0){
			      sb.append(cache);
			      break;	    	   
			       }
	       }   
	       
	       return sb.toString();   
	   } 
	
   public static boolean isEmpty(String text){
	   return (text == null) || "".equals (text.trim ());
   }
   
   public static String getValue(String content,String beginStr,String endStr,int count){
	   
	   String result = "";
	   
	   int beginIndex = -1;
	   int endIndex = -1;
	   for (int i = 0; i < count; i++) {
		 if(beginStr.equals("")){
			 beginIndex=0;
		 }else{
			 beginIndex = content.indexOf(beginStr,endIndex+1);
			 if (beginIndex < 0) {
				 return "";
			 }
		 }

		 if(endStr.equals("")){
			 endIndex=content.length();
		 }else{
			 endIndex= content.indexOf(endStr, beginIndex+beginStr.length());
			 if (endIndex < 0) {
				 return "";
			 }
		 }
	}
	   result= content.substring(beginIndex + beginStr.length(), endIndex).trim();
	   if ("&nbsp;".equalsIgnoreCase(result)) {
		   return "";
	   }
	   return result;
   } 
   
   public static String getColumnValue(String row,int columnCount){
	   return getColumnValue(row.toLowerCase(), "td", columnCount);
   }
   
   public static String getColumnValue(String row,String columnFlag,int columnCount){
	   String result = getValue(row,"<"+columnFlag,"</" + columnFlag + ">",columnCount);
	   if (StringUtil.isEmpty(result)) {
		   return "";
	   }
	   
	   int endIndex = result.lastIndexOf("</");
	   if (endIndex>0){
		  result = result.substring(0, endIndex);
	   }
	   
	   int beginIndex = result.lastIndexOf(">");
	   result = result.substring(beginIndex + 1);
	   return result.replaceAll("&nbsp;", "").trim();
   }   
     
   public static int getCount(String text,String subStr){
	   int count = 0;
	   int index = -1;
	   boolean goon= true;
	   while (goon){
		  index = text.indexOf(subStr, index + 1); 
		  if (index >= 0) {
			  count++;
		  } else {
			  goon= false;
		  }
	   }
	   
	   return count;
   }
   /**
    * 用于密码进行RSA加密的处理
    * @param modulus 模，一般需要动态的获取
    * @param exponent 指数，一般 是 固定不变
    * @param password 进行加密处理的密码
    * @return 加密后的base64编码
    * @throws IOException
    * @throws Exception
    */
   public static String getKeyPwd(String modulus,String exponent,String password) throws IOException, Exception{
	   if("".equals(modulus.trim())||"".equals(exponent.trim())){
		   throw new RuntimeException("参数不能为空");
	   }
		//base64编码
		BASE64Decoder base64d = new BASE64Decoder();
		//base64解码
		BASE64Encoder base64 = new BASE64Encoder();	
		//对modulus进行解码
		byte[] m = base64d.decodeBuffer(modulus);
		//对exponent进行解码
		byte[] e = base64d.decodeBuffer(exponent);
		//base64到toHex的转换
		String strm="";
		for(int i=0;i<m.length; i++){
			strm+=Integer.toHexString(m[i]);
		}
		String stre ="";
		for(int i=0;i<e.length;i++){
			stre+=Integer.toHexString(e[i]);
		}
		BigInteger b1 = new BigInteger(m);
		BigInteger b2 = new BigInteger(e);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
       RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2); 
       //获取公钥
       RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
       //加密
       Cipher cipher = Cipher.getInstance("RSA");  
       cipher.init(Cipher.ENCRYPT_MODE, publicKey);
       byte[]d = cipher.doFinal(password.getBytes());
       //转换成base64编码
       String pwd = base64.encode(d);
       return pwd;
	}
}
