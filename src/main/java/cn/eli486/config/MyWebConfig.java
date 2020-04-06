package cn.eli486.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author eli
 */
@Configuration
public class MyWebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors (InterceptorRegistry registry) {
        registry.addInterceptor (new HandlerInterceptor () {
            @Override
            public boolean preHandle (HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                Object user = request.getSession ().getAttribute ("user");
                if (user == null) {
                    request.setAttribute("msg","无权限请先登录");
                    request.getRequestDispatcher("/").forward(request, response);
                    return false;
                }
                return true;
            }
        }).addPathPatterns ("/**").excludePathPatterns ("/login","/index.html","/","/asserts/**");
    }

    @Override
    public void addResourceHandlers (ResourceHandlerRegistry registry) {
        String path = System.getProperty("user.dir")+"\\src\\main\\resources\\static\\verifyCode\\";
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            registry.addResourceHandler("/verifyCode/**").
                    addResourceLocations("file:"+path);
        }else{
            registry.addResourceHandler("/verifyCode/**").
                    addResourceLocations("file:"+path);
        }
    }
}
