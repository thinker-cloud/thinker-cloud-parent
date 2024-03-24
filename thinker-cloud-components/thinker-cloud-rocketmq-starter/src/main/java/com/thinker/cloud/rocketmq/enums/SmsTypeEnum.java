package com.thinker.cloud.rocketmq.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * SMS类型
 */
@Getter
@AllArgsConstructor
public enum SmsTypeEnum {
	/**
	 * 手机登录验证码类型
	 */
	LOGIN_SMS_CODE("SMS_151771943", "智动云");

	/**
	 * 类型
	 */
	private final String templateCode;
	/**
	 * 描述
	 */
	private final String signName;
}
