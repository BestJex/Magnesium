package net.onebean.spring;

import net.onebean.core.extend.ApolloConfInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;


@ComponentScan(
        basePackages = {
                "net.onebean.**.service",
                "net.onebean.**.api",
                "net.onebean.core",
                "net.onebean.config",
                "net.onebean.component"
        },
        includeFilters = {
                @ComponentScan.Filter(value = Service.class, type = FilterType.ANNOTATION),
                @ComponentScan.Filter(value = Component.class, type = FilterType.ANNOTATION)
        })
@ComponentScan(
        basePackages = {
                "net.onebean.**.action",
        },
        includeFilters = {
                @ComponentScan.Filter(value = Controller.class, type = FilterType.ANNOTATION)
        })
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class Main {
    public static void main(String[] args) {
        ApolloConfInitializer.init();
        SpringApplication.run(Main.class, args);
    }
}

