package xyz.ahbicj.playgroud;

import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.PreservedMetadataKeys;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.*;

/**
 * Class info
 *
 * @author yefeng
 * @since 2020/12/10
 */

public class NamingTest {

    private static final String METADATA_KEY_LAST_UPDATE_TIME = "usp.id.snowflake.matadata.lastUpdateTime";
    private static final String SNOWFLAKE_ID_PREFIX = "snowflake_id_";
    private static final String idName = "default";
    private static final String idGroup = "SNOWFLAKE_NODE_GROUP";
    private static final String CLUSTER_NAME = "SNOWFLAKE";

    public static void main(String[] args) throws NacosException {
        Properties namingServiceProperties = new Properties();
        namingServiceProperties.put("namespace","namespace-develop");
        namingServiceProperties.put("serverAddr","192.168.31.115:8848,192.168.31.115:8849");
        NamingService namingService = NamingFactory.createNamingService(namingServiceProperties);

        Instance instance = new Instance();
        instance.setIp("55.55.55.55");
        instance.setPort(9999);
        instance.setHealthy(true);
        instance.setEnabled(true);
        instance.setEphemeral(false);
        Map<String, String> metaData = new HashMap<>();

        metaData.put(PreservedMetadataKeys.INSTANCE_ID_GENERATOR, Constants.SNOWFLAKE_INSTANCE_ID_GENERATOR);
        metaData.put(METADATA_KEY_LAST_UPDATE_TIME, Objects.toString(System.currentTimeMillis()));
        instance.setMetadata(metaData);

        String serviceName = SNOWFLAKE_ID_PREFIX + idName;

        instance.setServiceName(serviceName);
        instance.setClusterName(CLUSTER_NAME);
        namingService.registerInstance(serviceName,idGroup,instance);
        List<Instance> instances = namingService.getAllInstances(serviceName, idGroup, Collections.singletonList(CLUSTER_NAME));
        System.out.println(instances);
        System.out.println("Hello");
    }
}
