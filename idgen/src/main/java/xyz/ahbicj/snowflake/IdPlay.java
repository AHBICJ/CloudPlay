package xyz.ahbicj.snowflake;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Class info
 *
 * @author yefeng
 * @since 2020/12/9
 */

@SpringBootApplication
public class IdPlay {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(IdPlay.class, args);
        System.out.println(run.getEnvironment().getProperty("spring.cloud.nacos.discovery.server-addr"));
        SnowflakeNacosHolder holder = run.getBean("snowflakeNacosHolder",SnowflakeNacosHolder.class);
        holder.init();
    }
}
