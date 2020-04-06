package cn.eli486.controller;

import cn.eli486.config.ScheduledConfig;
import cn.eli486.config.VerifyConfig;
import cn.eli486.dto.PageInfo;
import cn.eli486.dto.PageMessage;
import cn.eli486.entity.Customer;
import cn.eli486.utils.FileUtil;
import cn.eli486.utils.Result;
import cn.eli486.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author eli
 * 有验证码处理
 */
@Controller
@RequestMapping ("/verifyCustomers")
public class VerifyController {
    @Autowired
    ScheduledConfig scheduledConfig;

    @Autowired
    VerifyConfig config;


    /**
     * 获取验证码页面
     *
     * @param model 返回页面属性
     * @return 页面
     */
    @GetMapping ({"/", ""})
    public String verList (Model model) {
        Collection<Customer> daoAll = VerifyConfig.map.values ();
        model.addAttribute ("customers", daoAll);
        model.addAttribute ("type", "Y");
        return "customer/verifylist";
    }

    @ResponseBody
    @GetMapping ("/list")
    public Result taskList () {
        System.out.println (config.toDoAction.values ());
        return new Result<> (200, "成功", config.toDoAction.values ());
    }

    @GetMapping ("/refresh")
    public String refresh (Model model) {
        Collection<Customer> list = PageInfo.getVerifyMap ();
        for (Customer c :
                list) {
            List<Boolean> booleans = FileUtil.hasExistFileList (c);
            c.setExist (booleans);
        }
        model.addAttribute ("customers", list);
        return "customer/verifylist::table";
    }


    /**
     * 获取验证码图片
     *
     * @param orgCode 代码
     * @return 结果集
     */
    @ResponseBody
    @RequestMapping ("/get/{orgCode}")
    public Result verify (@PathVariable String orgCode) {
        try {
            if (config.isTodoListFull ()) {
                return new Result<> (StatusCode.ERROR, "任务已满");
            }
            config.getVerifyCode (orgCode);
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return new Result<> ();
    }


    private Map<String, List<Integer>> doStatus = new HashMap<> ();

    public static PageMessage pageMessage = new PageMessage ();

    /**
     * 加入任务列表
     *
     * @param verifyCode 验证码
     * @param merge      合并
     * @param orgCode    代码
     * @return 结果
     */
    @ResponseBody
    @PostMapping ("/add/{orgCode}")
    public Result addCode (String verifyCode, boolean merge, @PathVariable String orgCode) {
        if ("".equals (verifyCode)) {
            return new Result<> (StatusCode.ERROR, "验证码不能为空");
        }
        System.out.println (verifyCode + merge + orgCode);
        try {
            config.addAction (orgCode, verifyCode, merge);

        } catch (Exception e) {
            e.printStackTrace ();
        }
        return new Result<> (200, "成功", config.toDoAction.keySet ());
    }


    /**
     * 执行任务
     *
     * @return 结果
     */
    @ResponseBody
    @PostMapping ("/do")
    public Result doExec () {
        if (config.toDoAction.size () == 0) {
            return new Result<> (StatusCode.ERROR, "当前无任务");
        }
        Set<String> orgCodes = config.toDoAction.keySet ();
        for (String orgCode : orgCodes
        ) {
            doStatus.put (orgCode, config.toDoAction.get (orgCode).getCustomer ().getDoStatus ());
            pageMessage.setDoStatus (doStatus);
        }

        config.exec ();
        return new Result<> ();
    }

}
