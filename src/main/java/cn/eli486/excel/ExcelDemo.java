package cn.eli486.excel;

import cn.eli486.service.Purchase;
import cn.eli486.service.Sale;
import cn.eli486.service.Stock;
import cn.eli486.util.DateUtil;
import cn.eli486.util.FileUtil;
import cn.eli486.util.Title;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.eli486.util.FileUtil.getSumInfo;
import static cn.eli486.util.FileUtil.hasCreateDir;

/**
 * @author eli
 * 任务模板类
 */
public abstract class ExcelDemo implements Stock, Sale, Purchase {
    protected boolean merge = false;
    protected Logger logger = LoggerFactory.getLogger (ExcelDemo.class);
    private String dir = "D:/XJPFile/auto17/" + DateUtil.getBeforeDayAgainstToday (1, "yyyy-MM-dd");
    private Title<String> title = null;
    protected List<List<String>> content = new ArrayList<> ();
    public boolean isMerge () {
        return merge;
    }

    public void setMerge (boolean merge) {
        this.merge = merge;
    }



    /**
     * 模拟登录
     * @param client 客户端
     * @param params 登录参数
     * @throws Exception 登录异常
     */
    protected abstract void login(CloseableHttpClient client, Map<String, String> params) throws Exception;

    /**
     * 标准Stock表头
     * @return 链表表头
     */
    public static Title<String> defaultStock () {
        Title<String> stock = new Title<> ();
        stock.append ("库存日期");
        stock.append ("产品名称");
        stock.append ("产品规格");
        stock.append ("数量");
        stock.append ("批号");
        stock.append ("单位");
        stock.append ("有效日期");
        return stock;
    }
    /**
     * 标准Sale表头
     * @return 链表表头
     */
    public static Title<String> defaultSale () {
        Title<String> sale = new Title<> ();
        sale.append ("销售日期");
        sale.append ("客户名称");
        sale.append ("客户编码");
        sale.append ("产品名称");
        sale.append ("产品规格");
        sale.append ("数量");
        sale.append ("产品价格");
        sale.append ("收货地址");
        sale.append ("收获邮编");
        sale.append ("客户电话");
        sale.append ("客户联系人");
        sale.append ("批号");
        sale.append ("单位");
        sale.append ("有效日期");
        return sale;
    }
    /**
     * 标准Purchase表头
     * @return 链表表头
     */
    public static Title<String> defaultPurchase () {
        Title<String> purchase = new Title<> ();
        purchase.append ("入库时间");
        purchase.append ("供应商名称");
        purchase.append ("产品名称");
        purchase.append ("产品规格");
        purchase.append ("数量");
        purchase.append ("批号");
        purchase.append ("单位");
        purchase.append ("有效日期");
        return purchase;
    }

    /**
     * 执行任务
     * @param client 客户端
     * @param loginParams 登录参数
     * @param orgCode 商业code
     * @param orgName 商业name
     * @throws Exception 异常
     */
    public void exec (CloseableHttpClient client, Map<String, String> loginParams, String orgCode, String orgName) throws Exception {
        logger.info (orgName + "  " + DateUtil.getBeforeDayAgainstToday (1, "yyyy-MM-dd") + "  日报");
        try {
            login (client, loginParams);
        } catch (Exception e) {
            logger.error (orgName + "的网站不可访问！");
            throw new Exception (e.getMessage ());
        }

        stock (client,orgCode,orgName);
        sale (client,orgCode,orgName);
        purchase (client,orgCode,orgName);

        logger.info(orgName + "  " + DateUtil.getBeforeDayAgainstToday (1,"yyyy-MM-dd") + "  日报完成");
        logger.info("------------------------------------------------------------------------------------------------------");
    }

    /**
     * 库存
     * @param client 客户端
     * @param orgCode 商业code
     * @param orgName 商业name
     */
    protected void stock (CloseableHttpClient client, String orgCode, String orgName){
        try {
            String stockFile = "D:\\XJPFile\\auto17\\" + DateUtil.getBeforeDayAgainstToday (1, "yyyy-MM-dd") + "/V"
                    + orgCode + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + orgName.split ("-")[0] + ".xls";
            String bakFileV = "D:\\XJPFile\\bak\\" + DateUtil.getBeforeDayAgainstToday (1, "yyyy-MM-dd") + "/V" + orgCode
                    + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + orgName.split ("-")[0] + ".xls";
            title = createStock ();
            if (title == null) {
                title=defaultStock ();
            }
            if (!merge) {

                if ((FileUtil.checkFile (stockFile)) || (FileUtil.checkFile (bakFileV))) {
                    logger.info ("库存已生成 ");
                } else {
                    List<List<String>> stockInfo = getStock (client, orgName, title);
                    if (stockInfo != null) {
                        if (stockInfo.size () == 1) {
                            logger.info (orgName + "库存数据为空！");
                        }

                        hasCreateDir (stockFile);
                        FileUtil.createExcel (stockInfo, stockFile, title);
                        logger.info ("生成库存 " + stockFile);
                    }
                }

            } else {
                // 创建子文件
                String childDir = dir + "/" + orgCode.concat ("_").concat (orgName.split ("-")[0]);
                // 创建子文件名称
                String stockChildFile = childDir + "/V" + orgCode + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyy-MM-dd") + "_" + orgName + ".xls";
                File dirFile = new File (childDir);
                // 判断子文件路径是否存在
                if (!dirFile.exists ()) {
                    dirFile.mkdirs ();
                }
                // 若子文件已生成时不作处理
                if ((FileUtil.checkFile (stockChildFile))) {
                    logger.info ("库存已生成 ");
                } else {
                    // 获取库存信息
                    List<List<String>> stockInfo = getStock (client, orgName, title);
                    if (stockInfo != null) {
                        // 若果某个账号的数据不为空时
                        if (stockInfo.size () != 1) {
                            FileUtil.createExcel (stockInfo, stockChildFile, title);
                            logger.info ("生成库存 " + stockChildFile);
                            // 删除现有库存文件
                            if (FileUtil.checkFile (stockFile)) {
                                new File (stockFile).delete ();
                            }
                            List<List<String>> sumStockInfo = getSumInfo (childDir, "V");
                            FileUtil.createExcel (sumStockInfo, stockFile.trim (), null);
                            logger.info ("生成库存合并文件 " + sumStockInfo);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error ("获取" + orgName + "库存数据过程中发生错误！");
            e.printStackTrace ();
        }
    }

    /**
     * 销售
     * @param client 客户端
     * @param orgCode 商业code
     * @param orgName 商业name
     */
    protected void sale (CloseableHttpClient client, String orgCode, String orgName){
        try {
            String saleFile = "D:\\XJPFile\\auto17\\" + DateUtil.getBeforeDayAgainstToday (1, "yyyy-MM-dd") + "/S" + orgCode + "_" + DateUtil.getBeforeDayAgainstToday (0, "yyyyMMdd") + "_" + orgName.split ("-")[0] + ".xls";
            String bakFileS = "D:\\XJPFile\\bak\\" + DateUtil.getBeforeDayAgainstToday (1, "yyyy-MM-dd") + "/S" + orgCode + "_" + DateUtil.getBeforeDayAgainstToday (0, "yyyyMMdd") + "_" + orgName.split ("-")[0] + ".xls";
            title = createSale ();
            if (title == null) {
                title=defaultSale ();
            }
            if (!merge) {
                if ((FileUtil.checkFile (saleFile)) || (FileUtil.checkFile (bakFileS))) {
                    logger.info ("流向已生成");
                } else {

                    List<List<String>> saleInfo = getSale (client, orgName, title);
                    if (saleInfo != null) {
                        if (saleInfo.size () == 1) {
                            logger.info (orgName + "流向数据为空！");
                        }

                        hasCreateDir (saleFile);
                        FileUtil.createExcel (saleInfo, saleFile, title);
                        logger.info ("生成流向" + saleFile);
                    }
                }
                // 多账号
            } else {
                // 创建子文件
                String childDir = dir + "/" + orgCode.concat ("_").concat (orgName.split ("-")[0]);
                String saleChildFile = childDir + "/S" + orgCode + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + orgName + ".xls";
                File dirFile = new File (childDir);
                if (!dirFile.exists ()) {
                    dirFile.mkdirs ();
                }
                if ((FileUtil.checkFile (saleChildFile))) {
                    logger.info ("流向已生成 ");
                } else {
                    List<List<String>> saleInfo = getSale (client, orgName, title);
                    if (saleInfo != null) {
                        // 如果某个账号的数据为空时
                        if (saleInfo.size () != 1) {
                            FileUtil.createExcel (saleInfo, saleChildFile, title);
                            logger.info ("生成流向" + saleChildFile);
                            if (FileUtil.checkFile (saleFile)) {
                                new File (saleFile).delete ();
                                List<List<String>> sumStockInfo = getSumInfo (childDir, "S");
                                FileUtil.createExcel (sumStockInfo, saleFile.trim (), null);
                                logger.info ("生成流向合并文件" + saleFile);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error ("获取" + orgName + "流向数据过程中发生错误！");
            e.printStackTrace ();
        }
    }

    /**
     * 采购
     * @param client 客户端
     * @param orgCode 商业code
     * @param orgName 商业name
     */
    protected void  purchase (CloseableHttpClient client, String orgCode, String orgName){
        try {


            String purchasFile = "D:\\XJPFile\\auto17\\" + DateUtil.getBeforeDayAgainstToday (1, "yyyy-MM-dd") + "/P" + orgCode + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + orgName.split ("-")[0] + ".xls";

            String bakFileP = "D:\\XJPFile\\bak\\" + DateUtil.getBeforeDayAgainstToday (1, "yyyy-MM-dd") + "/P" + orgCode + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + orgName.split ("-")[0] + ".xls";
            title = createPurchase ();
            if (title == null) {
                title=defaultPurchase ();
            }
            if (!merge) {
                if ((FileUtil.checkFile (purchasFile)) || (FileUtil.checkFile (bakFileP))) {
                    logger.info ("采购已生成 ");
                } else {
                    List<List<String>> purchasInfo = getPurchase (client, orgName, title);
                    if (purchasInfo != null) {
                        if (purchasInfo.size () == 1) {
                            logger.info (orgName + "采购数据为空！");
                        }

                        hasCreateDir (purchasFile);
                        FileUtil.createExcel (purchasInfo, purchasFile, title);
                        logger.info ("生成采购 " + purchasFile);
                    }
                }

            } else {
                // 创建子文件
                String childDir = dir + "/" + orgCode.concat ("_").concat (orgName.split ("-")[0]);
                // 创建子文件名称
                String purchasChildFile = childDir + "/P" + orgCode + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + orgName + ".xls";
                File dirFile = new File (childDir);
                // 判断子文件路径是否存在
                if (!dirFile.exists ()) {
                    dirFile.mkdirs ();
                }
                // 若子文件已生成时不作处理
                if ((FileUtil.checkFile (purchasChildFile))) {
                    logger.info ("采购已生成 ");
                } else {
                    // 获取采购信息
                    List<List<String>> purchaseInfo = getPurchase (client, orgName, title);
                    if (purchaseInfo != null) {
                        // 若果某个账号的数据不为空时
                        if (purchaseInfo.size () != 1) {
                            FileUtil.createExcel (purchaseInfo, purchasChildFile, title);
                            logger.info ("生成采购 " + purchasChildFile);
                            // 删除现有采购文件
                            if (FileUtil.checkFile (purchasFile)) {
                                new File (purchasFile).delete ();
                            }
                            List<List<String>> sumPurchasInfo = getSumInfo (childDir, "P");
                            FileUtil.createExcel (sumPurchasInfo, purchasFile.trim (), null);
                            logger.info ("生成采购合并文件 " + purchasFile);
                        }
                    }
                }
            }
        } catch (Exception e){
            logger.error ("获取" + orgName + "采购数据过程中发生错误！");
            e.printStackTrace ();
        }
    }


    /*
     * 子类用父类的content、rows,每一个子类会new新的content和rows吗？
     * 添加excel内容
     */

    /**
     * 根据表头对应位置，修改每行对应的单元格内容
     *
     * @param rows 行
     * @param cell 单元格
     * @param a    表头对应位置
     */
    protected void addCell (List<String> rows, String cell, int a) {
        rows.set (a, cell);
    }

    /**
     * 先根据title列数添加列数相同的空元素的行
     * @param rows 行
     * @param title 表头
     * @return 返回行
     */
    protected List<String> addRows (List<String> rows, Title<String> title) {
        for (int i = 0; i < title.len (); i++) {
            rows.add ("");
            rows.toArray ()[getKey (title.local (), title.local ().get (i))] = "";
        }
        content.add (rows);
        return rows;
    }

    /**
     * 根据value取key值,
     * 因为title是不重复的,可以这么取
     */
    protected static Integer getKey (Map<Integer, String> map, String value) {
        Integer key = null;
        //Map,HashMap并没有实现Iteratable接口.不能用于增强for循环.
        for (Integer getKey : map.keySet ()) {
            if (map.get (getKey).equals (value)) {
                key = getKey;
            }
        }
        return key;
    }
}
