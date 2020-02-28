package cn.eli486;

import cn.eli486.utils.Result;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author eli
 */
@SpringBootApplication
@Controller
@ServletComponentScan ()
public class Crawler3Application {
    private static String[] args;
    private static ConfigurableApplicationContext context;

    public static void main (String[] args) {
        Crawler3Application.args=args;
        Crawler3Application.context=SpringApplication.run (Crawler3Application.class, args);

    }

    @ResponseBody
    @RequestMapping("/restart")
    public Result restart(){
        try {
            ExecutorService threadPool = new ThreadPoolExecutor (1,1,0, TimeUnit.SECONDS,new ArrayBlockingQueue<> (1),new ThreadPoolExecutor.DiscardOldestPolicy ());
            threadPool.execute (()->{
                context.close ();
                context=SpringApplication.run (Crawler3Application.class,args);
            });
            threadPool.shutdown ();
        }catch (Exception e){
            e.printStackTrace ();
        }
        return new Result ();
    }

}
