package cn.eli486.task;

import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;

/**
 * @author eli
 * 拷贝任务
 */
@Configuration
@EnableScheduling
public class CopyTask {

//    @Scheduled(cron = "* * 15 * * ?")
    public void copy () {
        try {
            String str = "D:\\XJPFile\\auto17\\";
            String bak = "D:\\XJPFile\\bak17";
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
        String s = "<td align=\"left\"><a target=\"_blank\" href=\"/ws/628.php/Storage/index?stdid=483828\">新泰林(注射用五水头孢唑林钠)</a> </td>";
        s=s.replaceAll ("<td align=\"left\"><a target=\"_blank\" href\\S*>","b");
        System.out.println (s);
    }

}

