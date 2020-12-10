package xyz.ahbicj.snowflake;

import com.alibaba.cloud.nacos.registry.NacosRegistration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Class info
 *
 * @author yefeng
 * @since 2020/12/10
 */

@Configuration(proxyBeanMethods = false)
public class AutoWiredTest {

    @Value("${server.port:8080}")
    private int port;

    @Bean
    public SnowflakeNacosHolder snowflakeNacosHolder(NacosRegistration registration){
        System.out.println(port);
        return new SnowflakeNacosHolder("127.0.0.1:8848",registration.getHost(),port);
    }

}
