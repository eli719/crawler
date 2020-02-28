package cn.eli486.config;

import cn.eli486.utils.DateUtil;
import org.springframework.context.annotation.Configuration;

/**
 * @author eli 全局变量配置
 */
@Configuration
public class GlobalInfo {
	public static  String fileNamePrefix="";
	/**
	 * 验证码图片存储地址
	 */
	public static  String verifyStorePath;
	/**
	 * 查询日期范围
	 */
	public static  String beginDate = DateUtil.getBeforeDayAgainstToday(60,"yyyy-MM-dd");
	public static  String endDate = DateUtil.getBeforeDayAgainstToday(1,"yyyy-MM-dd");

}
