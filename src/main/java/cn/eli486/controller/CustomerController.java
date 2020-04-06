package cn.eli486.controller;

import cn.eli486.config.ScheduledConfig;
import cn.eli486.dto.PageInfo;
import cn.eli486.dto.PageMessage;
import cn.eli486.entity.Customer;
import cn.eli486.task.DailyTask;
import cn.eli486.utils.DateUtil;
import cn.eli486.utils.FileUtil;
import cn.eli486.utils.Result;
import cn.eli486.utils.StatusCode;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author eli
 * 无验证码处理
 */
@Controller
@RequestMapping ("/customers")
public class CustomerController {

    @Autowired
    ScheduledConfig scheduledConfig;

    /**
     * 获取所有定时任务
     *
     * @param model 页面传递参数
     * @return
     */
    @GetMapping ({"/", ""})
    public String list (Model model) {
        Collection<Customer> list = PageInfo.getMap ();
        model.addAttribute ("customers", list);
        return "customer/list";
    }

    @ResponseBody
    @GetMapping ("/list")
    public Result taskList () {
        return new Result<> (200, "成功", tempTask);
    }

    @GetMapping ("/refresh")
    public String refresh (Model model) {
        Collection<Customer> list = PageInfo.getMap ();
        for (Customer c :
                list) {
            List<Boolean> booleans = FileUtil.hasExistFileList (c);
            c.setExist (booleans);
        }
        model.addAttribute ("customers", list);
        return "customer/list::table";
    }


    //添加任务
    /**
     * 临时任务列表
     */
    public static List<DailyTask> tempTask = new ArrayList<> ();
    private Map<String,List<Integer>> doStatus = new HashMap<> ();

    public static PageMessage pageMessage = new PageMessage ();

    @ResponseBody
    @PostMapping ("/add/{orgCode}")
    public Result add (@PathVariable String orgCode) {
        Customer customer = PageInfo.getOne (orgCode);
        DailyTask mySchedule = new DailyTask (customer);
        for (DailyTask d :
                tempTask) {
            if (d.getOrgname () == mySchedule.getOrgname ()) {
                return new Result<> (500, "不要重复添加同一任务");
            }
        }
        tempTask.add (mySchedule);
        return new Result<> (200, "成功", tempTask);
    }

    /**
     * 立即执行当前任务
     *
     * @return
     */
    @ResponseBody
    @PostMapping ("/do")
    public Result exec () {
        try {
            if (tempTask.size () != 0) {
                for (DailyTask m : tempTask
                ) {
                    scheduledConfig.startNow (tempTask, m.getClassname ());
                    doStatus.put (m.getOrgcode (),m.getCustomer ().getDoStatus ());
                }
                pageMessage.setDoStatus (doStatus);
                tempTask.clear ();
                return new Result<> ();
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }

        return new Result<> (StatusCode.ERROR, "当前无任务");
    }

    @ResponseBody
    @PostMapping ("/retry")
    public Result retry () {
        if (tempTask.size () != 0) {
            for (DailyTask m : tempTask
            ) {
                String[] str = new String[]{"V", "S", "P"};
                try {
                    for (int i = 0; i < str.length; i++) {
                        String fileName = "D:\\XJPFile\\bak\\" + DateUtil.getBeforeDayAgainstToday (1, "yyyy-MM-dd") + "\\" + str[i] + m.getOrgcode ().split ("-")[0]
                                + "_" + DateUtil.getBeforeDayAgainstToday (1, "yyyyMMdd") + "_" + m.getOrgname () + ".xls";
                        if (new File (fileName).exists ()) {
                            FileUtils.forceDelete (new File (fileName));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace ();
                }
            }
            exec ();
            return new Result<> ();
        }
        return new Result<> (StatusCode.ERROR, "当前无任务");
    }

}
