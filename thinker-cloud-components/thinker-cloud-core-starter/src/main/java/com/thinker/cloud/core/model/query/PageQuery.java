package com.thinker.cloud.core.model.query;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.thinker.cloud.core.constants.CommonConstants;
import com.thinker.cloud.core.utils.MyPageUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用分页参数
 *
 * @author admin
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class PageQuery extends Query implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前页
     */
    @Range(min = 1, message = "页数（page）必须大于0")
    private Integer page = 1;

    /**
     * 每页显示条数
     */
    @Range(min = 1, message = "显示条数（limit）必须大于0")
    private Integer limit = 10;

    /**
     * 排序字段
     */
    private String orderField;

    /**
     * 排序方式
     */
    private String order = CommonConstants.DESC;

    /**
     * 生成分页组件
     *
     * @return Mybatis-Plus分页插件
     */
    public <T> IPage<T> generatePage() {
        return MyPageUtil.generatePage(this);
    }
}
