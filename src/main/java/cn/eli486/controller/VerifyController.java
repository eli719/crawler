package cn.eli486.controller;

import cn.eli486.config.ScheduledConfig;
import cn.eli486.config.VerifyConfig;
import cn.eli486.entity.Customer;
import cn.eli486.utils.Result;
import cn.eli486.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * @author eli
 * 有验证码处理
 */
@Controller
@RequestMapping("/verifyCustomers")
public class VerifyController {
    @Autowired
    ScheduledConfig scheduledConfig;

    @Autowired
    VerifyConfig config;


    /**
     * 获取验证码页面
     * @param model
     * @return
     */
    @GetMapping ({"/",""})
    public String verList (Model model) {
        Collection<Customer> daoAll = VerifyConfig.map.values ();
        model.addAttribute ("customers", daoAll);
        model.addAttribute ("type","Y");
        return "customer/verifylist";
    }

    /**
     * 获取验证码图片
     * @param orgCode
     * @return
     */
    @ResponseBody
    @RequestMapping("/get/{orgCode}")
    public Result verify (@PathVariable String orgCode) {
        try {
            if (config.isTodoListFull ()) {
             return new Result<> (StatusCode.ERROR,"任务已满");
            }
            config.getVerifyCode (orgCode);
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return new Result<> ();
    }

    /**
     * 加入任务列表
     * @param verifyCode
     * @param merge
     * @param orgCode
     * @return
     */
    @ResponseBody
    @PostMapping ("/add/{orgCode}")
    public Result addCode (String verifyCode, boolean merge, @PathVariable String orgCode) {
        if ("".equals (verifyCode)){
            return new Result<> (StatusCode.ERROR,"验证码不能为空");
        }
        System.out.println (verifyCode + merge + orgCode);
        try {
            config.addAction (orgCode, verifyCode, merge);
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return new Result<> ();
    }


    /**
     * 执行任务
     * @return
     */
    @ResponseBody
    @PostMapping ("/do")
    public Result doExec () {
        if(VerifyConfig.toDoAction.size ()==0){
            return new Result<> (StatusCode.ERROR,"当前无任务");
        }
        config.exec ();
        return new Result<> ();
    }

}
