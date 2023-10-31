package com.thinker.cloud.core.model.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 分页对象
 *
 * @author admin
 */
@Data
@Accessors(chain = true)
public class PageVO<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private Integer total;

    /**
     * 每页记录数
     */
    private Integer size;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 当前页数
     */
    private Integer current;

    /**
     * 列表数据
     */
    private List<T> records;

    public PageVO(List<T> list, int total, int size, int current) {
        this.records = list;
        this.total = total;
        this.size = size;
        this.current = current;
        this.pages = (int) Math.ceil((double) total / size);
    }

    public PageVO(IPage<T> page) {
        if (page == null) {
            return;
        }

        this.records = page.getRecords();
        this.total = (int) page.getTotal();
        this.size = (int) page.getSize();
        this.current = (int) page.getCurrent();
        this.pages = (int) page.getPages();
    }
}
