package com.thinker.cloud.rocketmq.config;

import com.thinker.cloud.rocketmq.annotation.MQProducer;
import com.thinker.cloud.rocketmq.annotation.MQTransactionProducer;
import com.thinker.cloud.rocketmq.base.AbstractMQProducer;
import com.thinker.cloud.rocketmq.base.AbstractMQTransactionProducer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自动装配消息生产者
 */
@Slf4j
@Configuration
@ConditionalOnBean(MQBaseAutoConfiguration.class)
public class MQProducerAutoConfiguration extends MQBaseAutoConfiguration implements InitializingBean {
    @Getter
    @Setter
    private static DefaultMQProducer producer;

    @Override
    public void afterPropertiesSet() {
        this.configCommonProducer();
        this.configTransactionProducer();
    }

    private void configCommonProducer() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(MQProducer.class);
        //对于仅仅只存在消息消费者的项目，无需构建生产者
        if (CollectionUtils.isEmpty(beans)) {
            return;
        }
        if (producer == null) {
            Assert.notNull(mqProperties.getProducerGroup(), "producer group must be defined");
            Assert.notNull(mqProperties.getNameServerAddress(), "name server address must be defined");
            producer = new DefaultMQProducer(mqProperties.getProducerGroup());
            producer.setNamesrvAddr(mqProperties.getNameServerAddress());
            producer.setSendMsgTimeout(mqProperties.getSendMsgTimeout());
            producer.setSendMessageWithVIPChannel(mqProperties.getVipChannelEnabled());
            try {
                producer.start();
            } catch (MQClientException e) {
                log.error("初始化MQProducer失败 : {}", e.getLocalizedMessage());
                throw new RuntimeException(e);
            }
        }

        beans.forEach((key, value) -> {
            AbstractMQProducer beanObj = (AbstractMQProducer) value;
            beanObj.setProducer(producer);
        });
    }

    private void configTransactionProducer() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(MQTransactionProducer.class);
        if (CollectionUtils.isEmpty(beans)) {
            return;
        }
        ExecutorService executorService = new ThreadPoolExecutor(beans.size()
                , beans.size() * 2
                , 100, TimeUnit.SECONDS
                , new ArrayBlockingQueue<>(2000), r -> {
            Thread thread = new Thread(r);
            thread.setName("client-transaction-msg-check-thread");
            return thread;
        });
        Environment environment = applicationContext.getEnvironment();
        beans.forEach((key, value) -> {
            try {
                AbstractMQTransactionProducer beanObj = (AbstractMQTransactionProducer) value;
                MQTransactionProducer anno = beanObj.getClass().getAnnotation(MQTransactionProducer.class);

                TransactionMQProducer producer = new TransactionMQProducer(environment.resolvePlaceholders(anno.producerGroup()));
                producer.setNamesrvAddr(mqProperties.getNameServerAddress());
                producer.setExecutorService(executorService);
                producer.setTransactionListener(beanObj);
                producer.start();
                beanObj.setProducer(producer);
            } catch (Exception e) {
                log.error("build transaction producer error : {}", e.getLocalizedMessage());
                throw new RuntimeException(e);
            }
        });
    }


}
