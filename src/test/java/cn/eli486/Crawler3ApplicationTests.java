package cn.eli486;

import cn.eli486.dao.CustomerDao;
import cn.eli486.util.HttpsUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@SpringBootTest
class Crawler3ApplicationTests {
    @Autowired
    CustomerDao customerDao;
    @Test
    void contextLoads () throws IOException, NoSuchAlgorithmException, KeyManagementException {
        String send = HttpsUtil.send ("https://www.ahhuakang.com/Supplier/login.aspx", null, "utf-8");
        System.out.println (send);
    }


}
