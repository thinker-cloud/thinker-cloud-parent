package com.thinker.cloud.rocketmq.constant;

/**
 * MqTag
 */
public interface MqConstants {
	/**
	 * 支付中心TOPIC
	 */
	String TOPIC_PAY = "TOPIC_PAY";
	/**
	 * 支付中心支付结算TOPIC
	 */
	String TOPIC_PAY_SETTLEMENT = "TOPIC_PAY_SETTLEMENT";
	/**
	 * 支付中心退款结算TOPIC
	 */
	String TOPIC_REFUND_SETTLEMENT = "TOPIC_REFUND_SETTLEMENT";
	/**
	 * 数据中心TOPIC
	 */
	String TOPIC_MDC = "TOPIC_MDC";
	/**
	 * 数据中心日志TOPIC
	 */
	String TOPIC_MDC_LOG = "TOPIC_MDC_LOG";
	/**
	 * 推送中心TOPIC
	 */
	String TOPIC_PUSH = "TOPIC_PUSH";
	/**
	 * 会员积分TOPIC
	 */
	String TOPIC_MEMBER_INTEGRAL = "TOPIC_MEMBER_INTEGRAL";
	/**
	 * SMS短信通知
	 */
	String PUSH_NOTICE_SMS = "NOTICE_SMS";

	/**
	 * EMAIL消息通知
	 */
	String PUSH_NOTICE_EMAIL = "NOTICE_EMAIL";
	/**
	 * 公众号模板消息
	 */
	String PUSH_NOTICE_WXMP = "NOTICE_WXMP";
	/**
	 * 小程序模板消息
	 */
	String PUSH_NOTICE_WXMINI = "NOTICE_WXMINI";
	/**
	 * 日志
	 */
	String MDC_LOG = "MDC_LOG";

	/**
	 * 支付成功TOPIC
	 */
	String PAY_SUCCEED = "pay-succeed-";


	/**
	 * 退款成功TOPIC
	 */
	String PAY_REFUND = "pay-refund-";

	/**
	 * 初始化租户TOPIC
	 */
	String TOPIC_TENANT_INIT = "TOPIC_TENANT_INIT";

	/**
	 * 初始化用户TOPIC
	 */
	String TOPIC_USER_INIT = "TOPIC_USER_INIT";

	String TOPIC_PAY_USER = "TOPIC_PAY_USER";
}
