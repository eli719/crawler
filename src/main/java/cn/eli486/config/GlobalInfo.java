package cn.eli486.config;

import cn.eli486.utils.DateUtil;
import org.springframework.context.annotation.Configuration;

/**
 * @author eli 全局变量配置
 */
@Configuration
public class GlobalInfo {
	/**
	 * 验证码图片存储地址
	 */
	public static  String verifyStorePath;
	/**
	 * 查询日期范围
	 */
	public static final String BEGIN_DATE = DateUtil.getBeforeDayAgainstToday(60,"yyyy-MM-dd");
	public static final String END_DATE = DateUtil.getBeforeDayAgainstToday(1,"yyyy-MM-dd");


	public static final String DIR = "D:/XJPFile/auto17/" + DateUtil.getBeforeDayAgainstToday (1, "yyyy-MM-dd")+"/";
	public static final String BAK_DIR = "D:/XJPFile/bak/" + DateUtil.getBeforeDayAgainstToday (1, "yyyy-MM-dd")+"/";
}
