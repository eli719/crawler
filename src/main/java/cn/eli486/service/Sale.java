package cn.eli486.service;

import cn.eli486.util.Title;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.List;

/**
 * @author eli
 */
public interface Sale {
    /**
     * 获取销售数据
     * @param client 客户端
     * @param orgName 商家名称
     * @param title 表头
     * @return 销售数据
     */
    List<List<String>> getSale (CloseableHttpClient client, String orgName, Title<String> title) throws IOException;

    /**
     * 创建销售文件表头
     * @return 表头
     */
    Title<String> createSale ();
}
