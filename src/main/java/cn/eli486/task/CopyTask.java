package cn.eli486.task;

import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;

/**
 * @author eli
 * 拷贝任务
 */
@Configuration
@EnableScheduling
public class CopyTask {

    @Scheduled(cron = "* * 10 * * ?")
    public void copy () {
        try {
            String str = "D:\\XJPFile\\auto17\\";
            String bak = "D:\\XJPFile\\bak";
            File f = new File (str);
            String[] list = f.list ();
            if (list.length==0){
                return;
            }
            for (String file :
                    list) {
                FileUtils.copyDirectoryToDirectory (new File (str + file), new File (bak));
                FileUtils.deleteDirectory (new File (str + file));
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    public static void main (String[] args) {
        CopyTask copyTask = new CopyTask ();
        copyTask.copy ();
    }

}

