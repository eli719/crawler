package cn.eli486.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpSession;

/**
 * @author eli
 */
@Controller
public class LoginController {

    @PostMapping("/login")
    public  String login(@RequestParam("username") String username,
                         @RequestParam("password")String password,
                         HttpSession session, Model model) {
        if(!StringUtils.isEmpty (username)&&"1234".equals (password)){
            session.setAttribute ("user",username);
            return "redirect:/customers";
        }
        model.addAttribute ("msg","用户名或密码错误");
        return "index";
    }

    @GetMapping ("/")
    public String index(){
        return "index";
    }



}
