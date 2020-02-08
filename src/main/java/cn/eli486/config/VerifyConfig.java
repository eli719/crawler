package cn.eli486.config;

import cn.eli486.entity.Customer;
import cn.eli486.imp.VerifyDemo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author eli
 * 验证码任务配置管理类
 */
@Component
public class VerifyConfig {
    /**
     * busy是用来lock验证码任务执行过程的
     */
    private  Boolean busy = false;
    /**
     *  以下几个变量是类变量，用来处理多账号合并的，因为类变量共享状态，所以要加锁处理
    */
    public static String MultiVerCode = "";
    public static String[] MultiStockInfo = null;
    public static String[] MultiSaleInfo = null;
    public static String[] MultiPurchaseInfo = null;
    public static int MStockArray = 0;
    public static int MSaleArray = 0;
    public static int MPurchaseArray = 0;
    /**
     * verifyAction存储orgCode和对应所有执行类，在项目启动时就已加载
     */
    private static Map<String, VerifyDemo> verifyAction= new HashMap<> ();
    /**
     * toDoAction是当前执行任务列表
     */
    public static Map<String, VerifyDemo> toDoAction = new HashMap<> ();
    /**
     * map是为了得到对应Customer属性值
     */
    public  static Map<String,Customer> map = new HashMap<> ();

    public VerifyConfig () {
        try {
            //加载json文件中所有Customer,因为打成jar访问不到classes目录下json文件，要换成resource.getInputStream ()读取resource.getFile()也不行
//            File f = new File (VerifyConfig.class.getResource ("/Verifycustomer.json").getPath ());
            ClassPathResource resource = new ClassPathResource ("Verifycustomer.json");
            StringBuilder builder =new StringBuilder ();
            InputStreamReader inputStreamReader=new InputStreamReader (resource.getInputStream (),"utf-8");
            BufferedReader bufferedReader=new BufferedReader (inputStreamReader);
            String s = null;
            while ((s=bufferedReader.readLine ())!=null) {
                builder.append (s);
            }
            bufferedReader.close ();
            JSONObject parse = JSON.parseObject (builder.toString ());
            JSONArray customerArray = parse.getJSONArray ("customers");
            String jsonString = customerArray.toJSONString ();

            List<Customer> customers = JSON.parseArray (jsonString, Customer.class);
            System.out.println ("------------加载验证码列表----------");
            for (Customer customer : customers
            ) {
                map.put (customer.getOrgcode (), customer);
                VerifyDemo verifyDemo = (VerifyDemo) Class.forName (customer.getAction ()).newInstance ();
                verifyDemo.setOrgCode (customer.getOrgcode ());
                System.out.println (customer.getOrgcode ()+" -----"+customer.getAction ());
                verifyDemo.setOrgName (customer.getOrgname ());
                verifyDemo.addLoginParam (customer.getParams ());
                verifyAction.put (customer.getOrgcode (),verifyDemo);
            }
            System.out.println ("------------加载完成--------------");
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    /**
     * 获取对应验证码
     * @param orgCode 对应Customer代码
     * @throws Exception
     */
    public  void getVerifyCode (String orgCode) throws Exception {
        VerifyDemo verifyActor = verifyAction.get (orgCode);
        verifyActor.getVerifyCode ();

    }


    /**
     * 添加任务执行列表
     * @param orgCode
     * @param verifyCode
     * @param merge
     * @throws Exception
     */
    public  void addAction (String orgCode, String verifyCode, boolean merge) throws Exception {
        VerifyDemo demo = verifyAction.get (orgCode);
        demo.setMerge (merge);
        demo.addVerifyCodeParam (verifyCode);
        toDoAction.put (orgCode + "-" + merge, demo);
    }

    public boolean isTodoListFull(){
        //限制列表执行数量
        int todoMax = 5;
        return toDoAction.size() > todoMax;
    }

    /**
     * 执行任务
     */
    public  void exec () {
        synchronized (busy) {
            busy = true;
        }
        try {
            for (String orgCode :
                    toDoAction.keySet ()) {
                String[] info = orgCode.split ("-");
                VerifyConfig.MultiVerCode = info[1];
                toDoAction.get (orgCode).doExec ();
            }
            VerifyConfig.MultiSaleInfo = null;
            VerifyConfig.MultiStockInfo = null;
            VerifyConfig.MultiPurchaseInfo = null;
            VerifyConfig.MSaleArray = 0;
            VerifyConfig.MStockArray = 0;
            VerifyConfig.MPurchaseArray = 0;
        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            synchronized (busy) {
                busy = false;
            }
            toDoAction.clear ();
            System.out.println ("Current Tasks Complete!");
        }

    }


}
