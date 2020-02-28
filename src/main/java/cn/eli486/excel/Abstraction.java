package cn.eli486.excel;

import cn.eli486.config.TitleConfig;
import cn.eli486.service.Purchase;
import cn.eli486.service.Sale;
import cn.eli486.service.Stock;
import cn.eli486.utils.DateUtil;
import cn.eli486.utils.FileUtil;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;

import static cn.eli486.utils.FileUtil.getSumInfo;
import static cn.eli486.utils.FileUtil.hasCreateDir;

/**
 * @author eli
 * 任务模板类
 */
public abstract class Abstraction implements Stock, Sale, Purchase {
    protected boolean merge = false;
    protected Logger logger = LoggerFactory.getLogger (Abstraction.class);
    private String dir = "D:/XJPFile/auto17/" + DateUtil.getBeforeDayAgainstToday (1, "yyyy-MM-dd");
    private String bakDir = "D:/XJPFile/bak/" + DateUtil.getBeforeDayAgainstToday (1, "yyyy-MM-dd");
    private List<String> title = null;

    public boolean isMerge () {
        return merge;
    }

    public void setMerge (boolean merge) {
        this.merge = merge;
    }


    /**
     * 模拟登录
     *
     * @param client 客户端
     * @param params 登录参数
     * @throws Exception 登录异常
     */
    protected abstract void login (CloseableHttpClient client, Map<String, String> params) throws Exception;

    /**
     * 执行任务
     *
     * @param client      客户端
     * @param loginParams 登录参数
     * @param orgCode     商业code
     * @param orgName     商业name
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

        stock (client, orgCode, orgName);
        sale (client, orgCode, orgName);
        purchase (client, orgCode, orgName);

        logger.info (orgName + "  " + DateUtil.getBeforeDayAgainstToday (1, "yyyy-MM-dd") + "  日报完成");
        logger.info ("------------------------------------------------------------------------------------------------------");
    }

    /**
     * 库存
     *
     * @param client  客户端
     * @param orgCode 商业code
     * @param orgName 商业name
     */


    protected void stock (CloseableHttpClient client, String orgCode, String orgName) {
        try {
            String stockFile = dir + "\\V"
                    + orgCode.split ("-")[0] + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + orgName + ".xls";
            String bakFile = bakDir + "\\V" + orgCode.split ("-")[0]
                    + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + orgName + ".xls";
            title = createStock ();
            if (title == null) {
                title = TitleConfig.getTitle ().get (0);
            }
            if (!merge) {

                if ((FileUtil.checkFile (stockFile)) || (FileUtil.checkFile (bakFile))) {
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
                String childDir = dir + "/" + orgCode.split ("-")[0].concat (orgName);
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
                        if (stockInfo.size () != 0) {
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
     *
     * @param client  客户端
     * @param orgCode 商业code
     * @param orgName 商业name
     */
    protected void sale (CloseableHttpClient client, String orgCode, String orgName) {
        try {
            String saleFile = dir + "\\S"
                    + orgCode.split ("-")[0] + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + orgName + ".xls";
            String bakFile = bakDir + "\\S" + orgCode.split ("-")[0]
                    + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + orgName + ".xls";
            title = createSale ();
            if (title == null) {
                title = TitleConfig.getTitle ().get (1);
            }
            if (!merge) {
                if ((FileUtil.checkFile (saleFile)) || (FileUtil.checkFile (bakFile))) {
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
                String childDir = dir + "/" + orgCode.split ("-")[0].concat (orgName);
                // 创建子文件名称
                String saleChildFile = childDir + "/S" + orgCode + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyy-MM-dd") + "_" + orgName + ".xls";
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
     *
     * @param client  客户端
     * @param orgCode 商业code
     * @param orgName 商业name
     */
    protected void purchase (CloseableHttpClient client, String orgCode, String orgName) {
        try {


            String purchasFile = dir + "\\P"
                    + orgCode.split ("-")[0] + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + orgName + ".xls";
            String bakFile = bakDir + "\\P" + orgCode.split ("-")[0]
                    + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + orgName + ".xls";
            title = createPurchase ();
            if (title == null) {
                title = TitleConfig.getTitle ().get (2);
            }
            if (!merge) {
                if ((FileUtil.checkFile (purchasFile)) || (FileUtil.checkFile (bakFile))) {
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
                String childDir = dir + "/" + orgCode.split ("-")[0].concat (orgName);
                // 创建子文件名称
                String purchasChildFile = childDir + "/P" + orgCode + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyy-MM-dd") + "_" + orgName + ".xls";
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
        } catch (Exception e) {
            logger.error ("获取" + orgName + "采购数据过程中发生错误！");
            e.printStackTrace ();
        }
    }

    /**
     * 根据表头对应位置，修改每行对应的单元格内容
     *
     * @param rows 行
     * @param cell 单元格
     * @param a    表头对应位置
     */
    protected void addCell (List<String> rows, String cell, int a) {
        rows.set (a - 1, cell);
    }

    /**
     * 先根据title列数添加列数相同的空元素的行
     *
     * @param content 表空间
     * @param rows    行
     * @param title   表头
     * @return 返回行
     */
    protected List<List<String>> addRows (List<List<String>> content, List<String> rows, List<String> title) {
        for (int i = 0; i < title.size (); i++) {
            rows.add ("");
            rows.toArray ()[i] = "";
        }
        content.add (rows);
        return content;
    }
}
