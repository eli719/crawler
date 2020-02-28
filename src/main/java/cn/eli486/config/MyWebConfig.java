package cn.eli486.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author eli
 */
@Configuration
public class MyWebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers (ViewControllerRegistry registry) {
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
