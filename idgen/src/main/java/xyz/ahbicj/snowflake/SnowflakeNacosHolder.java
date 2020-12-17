package xyz.ahbicj.snowflake;

import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.PreservedMetadataKeys;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CountDownLatch;
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
    private static final String METADATA_KEY_LAST_UPDATE_TIME = "usp.id.snowflake.matadata.lastUpdateTime";

    private int workerId = -1;
    private String ip;
    private int port;

    private String serverAddr;
    private String namespace;
    private String serviceName;
    private String serviceGroup;
    private String serviceCluster;

    private long lastUpdateTime;
    private NamingService namingService;

    public boolean init() {
        if (workerId != -1) return true;
        try {
            Properties namingServiceProperties = new Properties();
            namingServiceProperties.put("namespace", namespace);
            namingServiceProperties.put("serverAddr", serverAddr);
            namingService = NamingFactory.createNamingService(namingServiceProperties);
            if (findMyself() == null) {
                CountDownLatch latch = new CountDownLatch(1);
                // 这里考虑使用轮询
                namingService.subscribe(serviceName, serviceGroup, Collections.singletonList(serviceCluster), new EventListener() {
                    @Override
                    public void onEvent(Event event) {
                        NamingEvent namingEvent = (NamingEvent) event;
                        Instance myself = namingEvent.getInstances().stream().filter(instance -> Objects.equals(instance.getIp(), ip) && Objects.equals(instance.getPort(), port))
                                .findFirst().orElse(null);
                        if (myself != null) {
                            try {
                                namingService.unsubscribe(serviceName, serviceGroup, Collections.singletonList(serviceCluster), this);
                                latch.countDown();
                            } catch (NacosException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                register();
                if (!latch.await(10, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("Init workId failed: register lost");
                }
            }
            initWorkerId();
//            scheduledUploadData();
            return true;
        } catch (NacosException | InterruptedException e) {
            LOGGER.error("init error", e);
        }
        return false;
    }

    private void scheduledUploadData() {
        Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, "snowflake-schedule-upload");
            thread.setDaemon(true);
            return thread;
        }).scheduleWithFixedDelay(this::updateMeta, 1L, 3L, TimeUnit.SECONDS); //每3S上报数据
    }

    private void updateMeta() {
        // https://github.com/alibaba/nacos/issues/4467
        // register(); 在 1.4.1 之前 再次 register 同样的 ip:port 会得到一个新的instanceId ...
        // TODO update meta lastUpdateTime
    }

    private void initWorkerId() throws NacosException {
        Instance myself = findMyself();
        if (myself != null) {
            String lutStr = myself.getMetadata().get(METADATA_KEY_LAST_UPDATE_TIME);
            if (null != lutStr) {
                long lut = Long.parseLong(lutStr);
                if (lut < System.currentTimeMillis()) {
                    workerId = Integer.parseInt(myself.getInstanceId());
                    LOGGER.info("init workerId:{}", workerId);
                    return;
                }
                throw new IllegalStateException("Init workId failed: timestamp check error");
            }
            throw new IllegalStateException("Init workId failed: lutStr not found");
        }
        throw new IllegalStateException("Init workId failed: this instance may not register");
    }

    private void register() throws NacosException {
        Instance instance = initInstance();
        namingService.registerInstance(serviceName, serviceGroup, instance);
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
        instance.setServiceName(serviceName);
        instance.setClusterName(serviceCluster);

        return instance;
    }

    private Instance findMyself() throws NacosException {
        List<Instance> instances = namingService.getAllInstances(serviceName, serviceGroup, Collections.singletonList(serviceCluster));
        return instances.stream().filter(instance -> Objects.equals(instance.getIp(), ip) && Objects.equals(instance.getPort(), port))
                .findFirst().orElse(null);
    }


    public int getWorkerId() {
        return workerId;
    }

    public void setWorkerId(int workerId) {
        this.workerId = workerId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceGroup() {
        return serviceGroup;
    }

    public void setServiceGroup(String serviceGroup) {
        this.serviceGroup = serviceGroup;
    }

    public String getServiceCluster() {
        return serviceCluster;
    }

    public void setServiceCluster(String serviceCluster) {
        this.serviceCluster = serviceCluster;
    }

}
