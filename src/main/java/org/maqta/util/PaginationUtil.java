package org.maqta.util;

import lombok.NoArgsConstructor;
import org.maqta.model.Pagination;

@NoArgsConstructor
public class PaginationUtil {
    public Pagination standadizePagination(Integer page, Integer perPage) {
        Pagination pagination = new Pagination();
        if (page == null || page < 1) {
            pagination.setPage(1);
        } else {
            pagination.setPage(page);
        }

        if (perPage == null || perPage < 1) {
            pagination.setPerPage(25);
        } else {
            pagination.setPerPage(Math.min(perPage, 25));
        }

        return pagination;
    }
}
