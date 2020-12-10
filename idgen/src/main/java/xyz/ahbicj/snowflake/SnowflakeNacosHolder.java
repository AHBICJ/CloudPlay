package xyz.ahbicj.snowflake;

import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.PreservedMetadataKeys;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Class info
 *
 * @author yefeng
 * @since 2020/12/10
 */

public class SnowflakeNacosHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SnowflakeNacosHolder.class);
    private static final String METADATA_KEY_LAST_UPDATE_TIME = "ahbicj.id.snowflake.matadata.lastUpdateTime";

    private static final String SNOWFLAKE_ID_PREFIX = "snowflake_id_";
    private static final String idName = "default";
    private static final String idGroup = "SNOWFLAKE_NODE_GROUP";

    private String nacosAddress;
    private String ip;
    private int port;

    public SnowflakeNacosHolder(String nacosAddress, String ip, int port) {
        this.nacosAddress = nacosAddress;
        this.port = port;
        this.ip = ip;
    }

    private long lastUpdateTime;

    private int workerId;

    private NamingService namingService;

    public boolean init() {
        try {
            Properties properties = new Properties();
            properties.setProperty("serverAddr", nacosAddress);
            namingService = NamingFactory.createNamingService(properties);
            List<Instance> instances = namingService.getAllInstances(SNOWFLAKE_ID_PREFIX + idName, idGroup);
            // if (not exist) {
            // 主要是注册会刷新时间，如果已经存在节点，直接进行初始化，对比时间
            if (instances == null || getInstance(instances) == null) {
                register(namingService);
            }
            initWorkerId(namingService);
            scheduledUploadData();
            return true;
        } catch (NacosException e) {
            LOGGER.error("init error", e);
        }
        return false;
    }

    private void scheduledUploadData() {
        Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "snowflake-schedule-upload");
            thread.setDaemon(true);
            return thread;
        }).scheduleWithFixedDelay(this::updateNewData, 1L, 3L, TimeUnit.SECONDS); //每3S上报数据
    }

    private void updateNewData() {
        System.out.println("BEEP");
        if (System.currentTimeMillis() < lastUpdateTime) {
            return;
        }
        try {
            register(namingService);
            List<Instance> instances;
            instances = namingService.getAllInstances(SNOWFLAKE_ID_PREFIX + idName, idGroup);
            Instance exsitInstance = getInstance(instances);
            LOGGER.info(exsitInstance.getInstanceId());
        } catch (NacosException e) {
            LOGGER.error("updateNewData has error", e);
        }
    }

    private void initWorkerId(NamingService naming) throws NacosException {
        List<Instance> instances;
        instances = naming.getAllInstances(SNOWFLAKE_ID_PREFIX + idName, idGroup);
        Instance exsitInstance = getInstance(instances);
        if (exsitInstance != null) {
            String lutStr = exsitInstance.getMetadata().get(METADATA_KEY_LAST_UPDATE_TIME);
            if (null != lutStr) {
                long lut = Long.parseLong(lutStr);
                if (lut < System.currentTimeMillis()) {
                    workerId = Integer.parseInt(exsitInstance.getInstanceId());
                    LOGGER.info("init workerId:{}", workerId);
                    return;
                }
                throw new IllegalStateException("Init workId failed: timestamp check error");
            }
            throw new IllegalStateException("Init workId failed: lutStr not found");
        }
//        throw new IllegalStateException("Init workId failed: this instance may not register");
    }

    private void register(NamingService naming) throws NacosException {
        Instance instance = initInstance();
        naming.registerInstance(SNOWFLAKE_ID_PREFIX + idName, idGroup, instance);
    }

    private Instance initInstance() {
        Instance instance = new Instance();
        instance.setIp(ip);
        instance.setPort(port);
        instance.setEphemeral(false); // Ephemeral: 短暂，临时
        instance.setHealthy(true);
        instance.setEnabled(true);

        Map<String, String> metaData = new HashMap<>();
        metaData.put(PreservedMetadataKeys.INSTANCE_ID_GENERATOR, Constants.SNOWFLAKE_INSTANCE_ID_GENERATOR);
        lastUpdateTime = System.currentTimeMillis();
        metaData.put(METADATA_KEY_LAST_UPDATE_TIME, Objects.toString(lastUpdateTime));
        instance.setMetadata(metaData);
        return instance;
    }

    private Instance getInstance(List<Instance> instances) {
        return instances.stream().filter(instance -> Objects.equals(instance.getIp(), ip) && Objects.equals(instance.getPort(), port))
                .findFirst().orElse(null);
    }

}
