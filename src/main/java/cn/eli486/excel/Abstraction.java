package cn.eli486.excel;

import cn.eli486.config.GlobalInfo;
import cn.eli486.config.TitleConfig;
import cn.eli486.controller.CustomerController;
import cn.eli486.entity.Customer;
import cn.eli486.service.Purchase;
import cn.eli486.service.Sale;
import cn.eli486.service.Stock;
import cn.eli486.utils.DateUtil;
import cn.eli486.utils.FileUtil;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.eli486.utils.FileUtil.getSumInfo;
import static cn.eli486.utils.FileUtil.hasCreateDir;

/**
 * @author eli
 * Excel模板类
 */
public abstract class Abstraction implements Stock, Sale, Purchase {
    protected boolean merge = false;
    protected Logger logger = LoggerFactory.getLogger (Abstraction.class);

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
     * @throws Exception 异常
     */
    public void exec (CloseableHttpClient client, Map<String, String> loginParams, Customer customer) throws Exception {
        logger.info (customer.getOrgname () + "  " + DateUtil.getBeforeDayAgainstToday (1, "yyyy-MM-dd") + "  日报");
        try {
            login (client, loginParams);
        } catch (Exception e) {
            logger.error (customer.getOrgname () + "的网站不可访问！");
            throw new Exception (e.getMessage ());
        }


        try {
            Map<String, List<Integer>> doStatus = CustomerController.pageMessage.getDoStatus ();
            execTasks (doStatus,client,customer);
        } catch (Exception e) {
            e.printStackTrace ();
        }
        logger.info (customer.getOrgname () + "  " + DateUtil.getBeforeDayAgainstToday (1, "yyyy-MM-dd") + "  日报完成");
        logger.info ("------------------------------------------------------------------------------------------------------");
    }

    /**
     * 库存
     *
     * @param client 客户端
     */


    protected void stock (CloseableHttpClient client, Customer customer) {
        try {
            String stockFile;
            String bakFile;
            String fileName;
            if (customer.getFilesName () == null) {
                stockFile = GlobalInfo.DIR + "/V"
                        + customer.getOrgcode ().split ("-")[0] + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + customer.getOrgname () + ".xls";
                bakFile = GlobalInfo.BAK_DIR + "/V" + customer.getOrgcode ().split ("-")[0]
                        + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + customer.getOrgname () + ".xls";
                fileName = "/V" + customer.getOrgcode () + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + customer.getOrgname () + ".xls";
            } else {
                fileName = customer.getFilesName ().get (0) + ".xls";
                fileName = FileUtil.parseFileName (fileName, customer);
                stockFile = GlobalInfo.DIR + fileName;
                bakFile = GlobalInfo.BAK_DIR + fileName;
            }

            List<String> title  = createStock ();
            if (title == null) {
                title = TitleConfig.getTitle ().get (0);
            }
            if (!merge) {

                if ((FileUtil.checkFile (stockFile)) || (FileUtil.checkFile (bakFile))) {
                    logger.info ("库存已生成 ");
                } else {
                    List<List<String>> stockInfo = getStock (client, title);
                    if (stockInfo != null) {
                        if (stockInfo.size () == 1) {
                            logger.info (customer.getOrgname () + "库存数据为空！");
                        }

                        hasCreateDir (stockFile);
                        FileUtil.createExcel (stockInfo, stockFile, title);
                        logger.info ("生成库存 " + stockFile);
                    }
                }

            } else {
                // 创建子文件夹
                String childDir = GlobalInfo.DIR + "/" + customer.getOrgcode ().split ("-")[0].concat (customer.getOrgname ());
                // 创建子文件名称
                String stockChildFile = childDir + fileName;
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
                    List<List<String>> stockInfo = getStock (client, title);
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
                            logger.info ("生成库存合并文件 " + stockFile);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error ("获取" + customer.getOrgname () + "库存数据过程中发生错误！");
            e.printStackTrace ();
        }
    }

    /**
     * 销售
     *
     * @param client 客户端
     */
    protected void sale (CloseableHttpClient client, Customer customer) {
        try {
            String saleFile;
            String bakFile;
            String fileName;
            if (customer.getFilesName () == null) {
                saleFile = GlobalInfo.DIR + "/S"
                        + customer.getOrgcode ().split ("-")[0] + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + customer.getOrgname () + ".xls";
                bakFile = GlobalInfo.BAK_DIR + "/S" + customer.getOrgcode ().split ("-")[0]
                        + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + customer.getOrgname () + ".xls";
                fileName = "/S" + customer.getOrgcode () + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + customer.getOrgname () + ".xls";
            } else {
                fileName = customer.getFilesName ().get (1) + ".xls";
                fileName = FileUtil.parseFileName (fileName, customer);
                saleFile = GlobalInfo.DIR + fileName;
                bakFile = GlobalInfo.BAK_DIR + fileName;
            }

            List<String> title = createSale ();
            if (title == null) {
                title = TitleConfig.getTitle ().get (1);
            }
            if (!merge) {
                if ((FileUtil.checkFile (saleFile)) || (FileUtil.checkFile (bakFile))) {
                    logger.info ("流向已生成");
                } else {

                    List<List<String>> saleInfo = getSale (client, title);
                    if (saleInfo != null) {
                        if (saleInfo.size () == 1) {
                            logger.info (customer.getOrgname () + "流向数据为空！");
                        }

                        hasCreateDir (saleFile);
                        FileUtil.createExcel (saleInfo, saleFile, title);
                        logger.info ("生成流向" + saleFile);
                    }
                }
                // 多账号
            } else {
                // 创建子文件
                String childDir = GlobalInfo.DIR + "/" + customer.getOrgcode ().split ("-")[0].concat (customer.getOrgname ());
                // 创建子文件名称
                String saleChildFile = childDir + fileName;
                File dirFile = new File (childDir);
                if (!dirFile.exists ()) {
                    dirFile.mkdirs ();
                }
                if ((FileUtil.checkFile (saleChildFile))) {
                    logger.info ("流向已生成 ");
                } else {
                    List<List<String>> saleInfo = getSale (client, title);
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
            logger.error ("获取" + customer.getOrgname () + "流向数据过程中发生错误！");
            e.printStackTrace ();
        }
    }

    /**
     * 采购
     *
     * @param client 客户端
     */
    protected void purchase (CloseableHttpClient client, Customer customer) {
        try {
            String purchaseFile;
            String bakFile;
            String fileName;
            if (customer.getFilesName () == null) {
                purchaseFile = GlobalInfo.DIR + "/P"
                        + customer.getOrgcode ().split ("-")[0] + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + customer.getOrgname () + ".xls";
                bakFile = GlobalInfo.BAK_DIR + "/P" + customer.getOrgcode ().split ("-")[0]
                        + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + customer.getOrgname () + ".xls";
                fileName = "/P" + customer.getOrgcode () + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + customer.getOrgname () + ".xls";
            } else {
                fileName = customer.getFilesName ().get (2) + ".xls";
                fileName = FileUtil.parseFileName (fileName, customer);
                purchaseFile = GlobalInfo.DIR + fileName;
                bakFile = GlobalInfo.BAK_DIR + fileName;
            }

            List<String> title = createPurchase ();
            if (title == null) {
                title = TitleConfig.getTitle ().get (2);
            }
            if (!merge) {
                if ((FileUtil.checkFile (purchaseFile)) || (FileUtil.checkFile (bakFile))) {
                    logger.info ("采购已生成 ");
                } else {
                    List<List<String>> purchaseInfo = getPurchase (client, title);
                    if (purchaseInfo != null) {
                        if (purchaseInfo.size () == 1) {
                            logger.info (customer.getOrgname () + "采购数据为空！");
                        }

                        hasCreateDir (purchaseFile);
                        FileUtil.createExcel (purchaseInfo, purchaseFile, title);
                        logger.info ("生成采购 " + purchaseFile);
                    }
                }

            } else {
                // 创建子文件
                String childDir = GlobalInfo.DIR + "/" + customer.getOrgcode ().split ("-")[0].concat (customer.getOrgname ());
                // 创建子文件名称
                String purchasChildFile = childDir + fileName;
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
                    List<List<String>> purchaseInfo = getPurchase (client, title);
                    if (purchaseInfo != null) {
                        // 若果某个账号的数据不为空时
                        if (purchaseInfo.size () != 1) {
                            FileUtil.createExcel (purchaseInfo, purchasChildFile, title);
                            logger.info ("生成采购 " + purchasChildFile);
                            // 删除现有采购文件
                            if (FileUtil.checkFile (purchaseFile)) {
                                new File (purchaseFile).delete ();
                            }
                            List<List<String>> sumPurchasInfo = getSumInfo (childDir, "P");
                            FileUtil.createExcel (sumPurchasInfo, purchaseFile.trim (), null);
                            logger.info ("生成采购合并文件 " + purchaseFile);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error ("获取" + customer.getOrgname () + "采购数据过程中发生错误！");
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
    protected  void addCell (List<String> rows, String cell, int a) {
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


    protected void execTasks( Map<String, List<Integer>> doStatus,CloseableHttpClient client,Customer customer){
        if (doStatus==null){
            doStatus = new HashMap<> (3);
            doStatus.put (customer.getOrgcode (), Arrays.asList (0,0,0));
        }
        stock (client, customer);
        doStatus.get (customer.getOrgcode ()).set (0,1);
        sale (client, customer);
        doStatus.get (customer.getOrgcode ()).set (1,1);
        purchase (client, customer);
        doStatus.get (customer.getOrgcode ()).set (2,1);
    }

}
