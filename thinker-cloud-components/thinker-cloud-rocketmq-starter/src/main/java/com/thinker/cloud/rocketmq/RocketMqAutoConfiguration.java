package com.thinker.cloud.rocketmq;


import com.thinker.cloud.rocketmq.component.RocketMQProducer;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动化配置类
 *
 * @author hjl
 */
@Configuration
@AllArgsConstructor
public class RocketMqAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public RocketMQProducer rocketMqProducer() {
		return new RocketMQProducer();
	}
}
