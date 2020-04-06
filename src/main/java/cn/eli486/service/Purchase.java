package cn.eli486.service;

import org.apache.http.impl.client.CloseableHttpClient;

import java.util.List;

/**
 * @author eli
 */
public interface Purchase {
    /**
     * 获取采购数据
     * @param client 客户端
     * @param title 表头
     * @return 采购数据
     */
    List<List<String>> getPurchase (CloseableHttpClient client, List<String> title) throws Exception;

    /**
     * 创建采购文件表头
     * @return 表头
     */
    List<String> createPurchase ();
}
