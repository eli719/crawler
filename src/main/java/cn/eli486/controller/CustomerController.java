package cn.eli486.controller;

import cn.eli486.config.ScheduledConfig;
import cn.eli486.dao.CustomerDao;
import cn.eli486.entity.Customer;
import cn.eli486.task.DailyTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/**
 * @author eli
 * 无验证码处理
 */
@Controller
public class CustomerController {

    @Autowired
    CustomerDao customerDao;

    @Autowired
    ScheduledConfig scheduledConfig;

    /**
     * 获取所有定时任务
     * @param model 页面传递参数
     * @return
     */
    @GetMapping ("/customers")
    public String list (Model model) {
        Collection<Customer> daoAll = customerDao.getAll ();
        model.addAttribute ("customers", daoAll);
        return "customer/list";
    }

    //添加任务
    /**
     * 临时任务列表
     */
    private List<DailyTask> tempTask = new ArrayList<> ();
    @ResponseBody
    @PostMapping ("/customers/add{orgCode}")
    public String add (@PathVariable String orgCode) {
        Customer customer = customerDao.get (orgCode);
        DailyTask mySchedule=new DailyTask (customer);
        tempTask.add (mySchedule);
        return "success";
    }

    /**
     * 立即执行当前任务
     * @param orgCode
     * @return
     */
    @ResponseBody
    @PostMapping ("/customers/do{orgCode}")
    public String exec (@PathVariable String orgCode) {
        if(tempTask.size ()!=0){
            for (DailyTask m:tempTask
            ) {
                scheduledConfig.startNow (tempTask,m.getClassname ());
            }
            tempTask.clear ();
            return "success";
        }
        return "fail";
    }


//    //修改
//    @GetMapping ("/customer/{orgcode}")
//    public String edit (@PathVariable String orgcode, Model model) {
//        Customer customer = customerDao.get (orgcode);
//        model.addAttribute ("customer", customer);
//        return "customer/add";
//    }


    /**
     * 添加页面
     */
    @GetMapping ("/customer")
    public String addPage (Model model,String orgCode) {
        model.addAttribute ("customer",customerDao.get (orgCode));
        return "customer/add";
    }

    @PostMapping ("/customer")
    public String add (Customer customer) {
        customerDao.save (customer);
        return "redirect:/customers";
    }



    @DeleteMapping ("/customer/{orgcode}")
    public String delete (@PathVariable String orgcode) {
        customerDao.delete (orgcode);
        return "redirect:/customers";
    }


}
