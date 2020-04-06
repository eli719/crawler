package cn.eli486.excel;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author eli
 */
@Deprecated
@Configuration
@ConfigurationProperties(prefix = "excel")
@EnableConfigurationProperties(ExcelConfig.class)
public class ExcelConfig {
    private List<List> titles;

    public List<List> getTitles () {
        return titles;
    }

    public void setTitles (List<List> titles) {
        this.titles = titles;
    }
}
