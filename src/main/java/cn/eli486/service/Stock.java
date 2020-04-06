package cn.eli486.service;

import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.List;

/**
 * @author eli
 */
public interface Stock {
    /**
     * 获取库存数据
     * @param client 客户端
     * @param title 表头
     * @return 库存数据
     */
    List<List<String>> getStock (CloseableHttpClient client, List<String> title) throws IOException;

    /**
     * 创建库存文件表头
     * @return 表头
     */
//
    List<String> createStock ();
}
