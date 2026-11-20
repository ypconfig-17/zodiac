package com.tcsl.zodiac.server.model;

import java.util.Collections;
import java.util.List;

/**
 * @program: dashbord
 * @description:自定义内存分页
 * @author: Yang Pan
 * @create: 2021-04-08 17:12
 */
public class PageInfo<T> {

    private long total;

    private Integer pageNum;

    private Integer pageSize;

    private List<T> list;

    public PageInfo(List<T> list, Integer pageNum, Integer pageSize) {
        if (list.isEmpty()) {
            this.total = 0L;
            this.pageNum = 1;
            this.pageSize = 10;
            this.list = Collections.emptyList();
        } else {
            this.total = list.size();
            this.list = list;
            this.pageNum = (pageNum == null || 0 == pageNum) ? 1 : pageNum;
            this.pageSize = (pageSize == null || 0 == pageSize) ? 10 : pageSize;
            paging();
        }
    }

    private void paging() {
        Integer totalNum = list.size();
        Integer totalPage = 0;
        if (totalNum > 0) {
            totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        }
        if (pageNum > totalPage) {
            pageNum = totalPage;
        }
        int startPoint = (pageNum - 1) * pageSize;
        int endPoint = startPoint + pageSize;
        if (totalNum <= endPoint) {
            endPoint = totalNum;
        }
        //分页处理
        this.list = list.subList(startPoint, endPoint);
    }

    public List<T> getList() {
        return this.list;
    }

    public long getTotal() {
        return this.total;
    }
}

