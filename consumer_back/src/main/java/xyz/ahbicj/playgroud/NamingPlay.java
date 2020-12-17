package xyz.ahbicj.playgroud;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;

import java.util.Properties;

/**
 * Class info
 *
 * @author yefeng
 * @since 2020/12/10
 */

public class NamingPlay {

    public static String TEST_CLUSTER_NAME = "TEST_SERVICE_NAME";
    public static void main(String[] args) throws NacosException {
        Properties properties = new Properties();
        properties.setProperty("serverAddr", "192.168.31.115:8848");
        properties.setProperty("namespace", "public");

        NamingService naming = NamingFactory.createNamingService(properties);

        naming.registerInstance("snowflake_id_new", "11.11.11.11", 8888, "TEST1");

        naming.registerInstance("snowflake_id_new", "2.2.2.2", 9999, "DEFAULT");

        System.out.println(naming.getAllInstances("snowflake_id_new"));

        naming.deregisterInstance("snowflake_id_new", "2.2.2.2", 9999, "DEFAULT");

        System.out.println(naming.getAllInstances("snowflake_id_new"));

        naming.subscribe("snowflake_id_new", event -> {
            System.out.println(((NamingEvent)event).getServiceName());
            System.out.println(((NamingEvent)event).getInstances());
        });
    }
}
