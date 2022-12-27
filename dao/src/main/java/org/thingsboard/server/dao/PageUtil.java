package org.thingsboard.server.dao;


import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageDataAndTotalValue;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.vo.resultvo.cap.AppDeviceCapVo;
import org.thingsboard.server.dao.model.ToData;

import java.util.ArrayList;
import java.util.List;

public interface PageUtil {

    /**
     * Create a {@link org.springframework.data.domain.Page} from a {@link java.util.List} of objects
     *
     * @param list     List数据
     * @param pageLink 分页参数.
     * @param <T>     包含数据
     * @return page
     */
   public static <T> Page<T> createPageFromList(List<T> list, PageLink pageLink) {
        Pageable pageable = DaoUtil.toPageable(pageLink);

        if (list == null) {
            throw new IllegalArgumentException("list不能为空");
        }

        int startOfPage = pageable.getPageNumber() * pageable.getPageSize();
        if (startOfPage > list.size()) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }

        int endOfPage = Math.min(startOfPage + pageable.getPageSize(), list.size());
        return new PageImpl<>(list.subList(startOfPage, endOfPage), pageable, list.size());
    }


   static PageData<T> createPageDataFromList(List<T> list, PageLink pageLink) {
      Page page= createPageFromList(list,pageLink);
       return new PageData<>(page.getContent(), page.getTotalPages(),page.getTotalElements(),page.hasNext());
    }


}

