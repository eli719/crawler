package cn.eli486.controller;

import cn.eli486.config.ScheduledConfig;
import cn.eli486.config.VerifyConfig;
import cn.eli486.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;

/**
 * @author eli
 * 有验证码处理
 */
@Controller
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
    @GetMapping ("/verifycustomers")
    public String verList (Model model) {
        Collection<Customer> daoAll = VerifyConfig.map.values ();
        model.addAttribute ("customers", daoAll);
        return "customer/verifylist";
    }

    /**
     * 获取验证码图片
     * @param orgCode
     * @return
     */
    @ResponseBody
    @PostMapping ("/customer/get{orgCode}")
    public String verify (@PathVariable String orgCode) {
        try {
            if (config.isTodoListFull ()) {
             return "fail";
            }
            config.getVerifyCode (orgCode);
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return "success";
    }


    /**
     * 加入任务列表
     * @param verifyCode
     * @param merge
     * @param orgCode
     * @return
     */
    @ResponseBody
    @PostMapping ("/customer/add{orgCode}")
    public String addCode (String verifyCode, boolean merge, @PathVariable String orgCode) {
        System.out.println (verifyCode + merge + orgCode);
        try {
            config.addAction (orgCode, orgCode, merge);
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return "success";
    }


    /**
     * 执行任务
     * @return
     */
    @ResponseBody
    @PostMapping ("/customer/do")
    public String doExec () {
        config.exec ();
        return "success";
    }

}
