package cn.eli486;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan ()
public class Crawler3Application {

    public static void main (String[] args) {
        SpringApplication.run (Crawler3Application.class, args);
    }

}
