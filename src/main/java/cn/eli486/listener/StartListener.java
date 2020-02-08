package cn.eli486.listener;

import cn.eli486.config.GlobalInfo;
import cn.eli486.config.ScheduledConfig;
import cn.eli486.dao.CustomerDao;
import cn.eli486.entity.Customer;
import cn.eli486.task.DailyTask;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author eli
 */
@javax.servlet.annotation.WebListener
public class StartListener  implements ServletContextListener {
    @Autowired
    CustomerDao customerDao;

    @Autowired
    ScheduledConfig config;

    /**
     * 项目启动加载定时任务
     * @param sce
     */
    @Override
    public void contextInitialized (ServletContextEvent sce) {
        try {
            String property = System.getProperty ("user.dir");
            System.out.println (property+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"static");
            property =property+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"static";
            GlobalInfo.verifyStorePath = property+File.separator+"verifyCode";
            FileUtils.forceMkdir (new File (GlobalInfo.verifyStorePath));

            List<DailyTask> taskList = new ArrayList<> ();
            Collection<Customer> all = customerDao.getAll ();

            for (Customer next : all) {
                DailyTask task = new DailyTask (next);
                taskList.add (task);
            }
            config.startCron (taskList);
            System.out.println ("定时任务加载完成——————————");
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }
}
