package com.thinker.cloud.core.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 超级实体
 *
 * @author admin
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class SuperEntity extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 4081293917569080703L;

    /**
     * 版本号
     */
    @JsonIgnore
    @Version
    private Integer version;

    /**
     * 逻辑删除标识
     */
    @JsonIgnore
    @TableLogic
    private Boolean deleted;

    /**
     * 修改者id
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /**
     * 修改时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
