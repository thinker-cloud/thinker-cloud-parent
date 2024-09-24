package com.thinker.cloud.db.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.thinker.cloud.security.userdetail.AuthUser;
import com.thinker.cloud.security.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;
import java.util.Objects;

/**
 * Mybatis Plus 自定义 【公共字段】 自动填充策略接口实现
 *
 * @author admin
 */
@Slf4j
public class BaseMetaObjectHandler implements MetaObjectHandler {

    private static final String CREATE_TIME = "createTime";
    private static final String CREATE_BY = "createBy";
    private static final String UPDATE_TIME = "updateTime";
    private static final String UPDATE_BY = "updateBy";
    private static final String ORGANIZATION_ID = "organizationId";

    @Override
    public void insertFill(MetaObject metaObject) {
        Date date = new Date();
        AuthUser user = SecurityUtils.getUser();
        try {
            if (metaObject.hasGetter(CREATE_TIME)) {
                if (Objects.isNull(metaObject.getValue(CREATE_TIME))) {
                    this.setFieldValByName(CREATE_TIME, date, metaObject);
                }
            }

            Long userId = user.getId();
            if (metaObject.hasGetter(CREATE_BY)) {
                if (Objects.isNull(metaObject.getValue(CREATE_BY))) {
                    this.setFieldValByName(CREATE_BY, userId, metaObject);
                }
            }

            if (metaObject.hasGetter(UPDATE_TIME)) {
                if (Objects.isNull(metaObject.getValue(UPDATE_TIME))) {
                    this.setFieldValByName(UPDATE_TIME, date, metaObject);
                }
            }

            if (metaObject.hasGetter(UPDATE_BY)) {
                if (Objects.isNull(metaObject.getValue(UPDATE_BY))) {
                    this.setFieldValByName(UPDATE_BY, userId, metaObject);
                }
            }

            if (metaObject.hasGetter(ORGANIZATION_ID)) {
                if (Objects.isNull(metaObject.getValue(ORGANIZATION_ID))) {
                    this.setFieldValByName(ORGANIZATION_ID, user.getOrganizationId(), metaObject);
                }
            }
        } catch (Exception e) {
            log.error("Mybatis Plus 公共字段自动填充策略触发失败，ex={}", e.getMessage());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        try {
            if (metaObject.hasGetter(UPDATE_TIME)) {
                if (Objects.isNull(metaObject.getValue(UPDATE_TIME))) {
                    this.setFieldValByName(UPDATE_TIME, new Date(), metaObject);
                }
            }

            if (metaObject.hasGetter(UPDATE_BY)) {
                if (Objects.isNull(metaObject.getValue(UPDATE_BY))) {
                    this.setFieldValByName(UPDATE_BY, SecurityUtils.getUserId(), metaObject);
                }
            }
        } catch (Exception e) {
            log.error("Mybatis Plus 公共字段字段自动填充策略触发失败，ex={}", e.getMessage());
        }
    }
}
