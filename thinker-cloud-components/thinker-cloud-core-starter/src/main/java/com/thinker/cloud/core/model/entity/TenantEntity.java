package com.thinker.cloud.core.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 * 租户实体类
 *
 * @author admin
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class TenantEntity extends SuperEntity {

    @Serial
    private static final long serialVersionUID = 4881237897629891458L;

    /**
     * 租户id
     */
    @TableField(fill = FieldFill.INSERT)
    private Long tenantId;
}
