package xyz.ahbicj.autoconfigure.idgen;

import com.alibaba.cloud.nacos.registry.NacosServiceRegistryAutoConfiguration;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.ahbicj.snowflake.IDGen;
import xyz.ahbicj.snowflake.SnowflakeIDGenerator;
import xyz.ahbicj.snowflake.SnowflakeNacosHolder;

@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(IDGen.class)
@EnableConfigurationProperties(IDGenProperties.class)
@AutoConfigureAfter(NacosServiceRegistryAutoConfiguration.class)
public class IDGenAutoConfiguration {

    @Bean
    public SnowflakeNacosHolder snowflakeNacosHolder(IDGenProperties idgenProperties){
        SnowflakeNacosHolder holder = new SnowflakeNacosHolder();
        BeanUtils.copyProperties(idgenProperties,holder);
        holder.setServiceName(idgenProperties.getServicePrefix()+idgenProperties.getServiceName());
        return holder;
    }


    @Bean
    @ConditionalOnMissingBean(name = "IDGen")
    public SnowflakeIDGenerator SnowflakeIDGenerator(SnowflakeNacosHolder snowflakeNacosHolder)
    {
        return new SnowflakeIDGenerator(snowflakeNacosHolder);
    }

}
