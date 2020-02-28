package cn.eli486.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author eli
 */
@Controller
public class LoginController {

    @RequestMapping ("/login")
    public  String login(String username, String password, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(username!=null&&"1234".equals (password)){
            return "redirect:/customers";
        }
        return "redirect:login.html";
    }

    @RequestMapping("/")
    public String index(){
        return "login";
    }



}
