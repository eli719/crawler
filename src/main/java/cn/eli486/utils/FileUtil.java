package cn.eli486.utils;

import cn.eli486.config.GlobalInfo;
import cn.eli486.entity.Customer;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author eli
 */
public  class FileUtil {


	public static void createExcel(List<List<String>> list, String fileName, List<String> title) throws IOException {

		HSSFWorkbook excel = new HSSFWorkbook();
		HSSFSheet sheet = excel.createSheet("page1");
		if(title!=null) {
			// 创建表头
			String[] t= (String[]) title.toArray (new String[title.size()]);
			HSSFRow titleRow = sheet.createRow(0);
			for (int i = 0; i < t.length; i++) {
				titleRow.createCell(i).setCellValue(t[i]);
			}
			// 插入数据
			for (int i = 0; i < list.size(); i++) {
				// 根据传入数据长度创建行数
				HSSFRow row = sheet.createRow(i+1);
				// 对每组数据遍历插入单元格
				for (int j = 0; j < list.get(i).size(); j++) {
					row.createCell(j).setCellValue(list.get(i).get(j));
				}
			}
		}else{
			// 插入数据
			for (int i = 0; i < list.size(); i++) {
				// 根据传入数据长度创建行数
				HSSFRow row = sheet.createRow(i);
				// 对每组数据遍历插入单元格
				for (int j = 0; j < list.get(i).size(); j++) {
					row.createCell(j).setCellValue(list.get(i).get(j));
				}
			}
		}
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			excel.write(file);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			excel.close();
		}
	}
	
	/**
	 * 检查文件是否存在
	 */
	public static boolean checkFile(String fileName) {
		File file = new File(fileName);
		return file.exists ();
	}


	public static List<Boolean> hasExistFileList(Customer customer) {
		String stockFile;
		String bakFileV;
		String fileNameV;
		String saleFile;
		String bakFileS;
		String fileNameS;
		String purchaseFile;
		String bakFileP;
		String fileNameP;
		if (customer.getFilesName () == null) {
			stockFile = GlobalInfo.DIR + "/V"
					+ customer.getOrgcode ().split ("-")[0] + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + customer.getOrgname () + ".xls";
			bakFileV = GlobalInfo.BAK_DIR + "/V" + customer.getOrgcode ().split ("-")[0]
					+ "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + customer.getOrgname () + ".xls";

			saleFile = GlobalInfo.DIR + "/S"
					+ customer.getOrgcode ().split ("-")[0] + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + customer.getOrgname () + ".xls";
			bakFileS = GlobalInfo.BAK_DIR + "/S" + customer.getOrgcode ().split ("-")[0]
					+ "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + customer.getOrgname () + ".xls";

			purchaseFile = GlobalInfo.DIR + "/P"
					+ customer.getOrgcode ().split ("-")[0] + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + customer.getOrgname () + ".xls";
			bakFileP = GlobalInfo.BAK_DIR + "/P" + customer.getOrgcode ().split ("-")[0]
					+ "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + customer.getOrgname () + ".xls";
		}else {
			fileNameV = customer.getFilesName ().get (0)+".xls";
			fileNameV=parseFileName(fileNameV,customer);
			stockFile = GlobalInfo.DIR+fileNameV;
			bakFileV = GlobalInfo.BAK_DIR+fileNameV;

			fileNameS = customer.getFilesName ().get (1)+".xls";
			fileNameS=parseFileName(fileNameS,customer);
			saleFile = GlobalInfo.DIR+fileNameS;
			bakFileS = GlobalInfo.BAK_DIR+fileNameS;

			fileNameP = customer.getFilesName ().get (2)+".xls";
			fileNameP=parseFileName(fileNameP,customer);
			purchaseFile = GlobalInfo.DIR+fileNameP;
			bakFileP = GlobalInfo.BAK_DIR+fileNameP;
		}
		List<Boolean> list = new ArrayList<> ();
		boolean isExistStockFile;
		isExistStockFile = (FileUtil.checkFile (stockFile)) || (FileUtil.checkFile (bakFileV));

		boolean isExistSaleFile;
		isExistSaleFile = (FileUtil.checkFile (saleFile)) || (FileUtil.checkFile (bakFileS));

		boolean isExistPurchaseFile;
		isExistPurchaseFile = (FileUtil.checkFile (purchaseFile)) || (FileUtil.checkFile (bakFileP));
		list.add(isExistStockFile);
		list.add(isExistSaleFile);
		list.add(isExistPurchaseFile);
		return list;
	}

	/**
	 * 检查并创建文件夹
	 */
	public static void checkAndCreateDir(String dir) {
		File dirFile = new File(dir);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
	}
	public static String readFile(String fileName,String charSet) throws IOException{
		File file = new File(fileName);
		return FileUtils.readFileToString(file, charSet);
	}

	public static void hasCreateDir (String fileDir) {
		File parent = new File(fileDir);
		if (!(parent.exists())) {
			String child = parent.getParent();
			new File(child).mkdirs();
		}
	}


	public static List<List<String>> getSumInfo (String filepath, String type) throws Exception {
		//定义数据存储集合
		List<List<String>> data= new ArrayList<> ();
		List<String> list =new ArrayList<String>();
		//获取文件路下的所有文件
		File file = new File(filepath);
		String[] fileList = file.list();

		//循环下列文件
		for (String s : fileList) {
			File readFile = new File (filepath + "\\" + s);

			// 找出已V开头的excel文件character： V代表库存文件 S代表销售文件
			if (readFile.getName ().startsWith (type)) {
				FileInputStream fileInputStream;
				try {
					//获取excel第一个页签的内容
					fileInputStream = new FileInputStream (readFile);
					HSSFWorkbook wb = new HSSFWorkbook (fileInputStream);
					HSSFSheet sheet = wb.getSheetAt (0);
					//第一个excel的表头需要获取， 其他的去掉不需要
					for (int j = list.size () > 0 ? 1 : 0; j <= sheet.getLastRowNum (); j++) {
						list = new ArrayList<String> ();
						//以表头的列为准
						for (int p = 0; p < sheet.getRow (0).getLastCellNum (); p++) {
							list.add (String.valueOf (sheet.getRow (j).getCell (p)));
						}
						data.add (list);
					}
				} catch (IOException e) {
					e.printStackTrace ();
				}
			}
		}
		return data;
	}


	public static String parseFileName(String fileName,Customer customer){
		if(!fileName.contains ("{")){
			return fileName;
		}
		fileName=fileName.replace ("{orgCode}",customer.getOrgcode ());
		fileName=fileName.replace ("{orgName}",customer.getOrgname ());
		String date = fileName.substring (fileName.indexOf ("{yyyy"),fileName.indexOf ("d}")+2);
		return fileName.replace (date,DateUtil.getBeforeDayAgainstToday (1,date));
	}
}
