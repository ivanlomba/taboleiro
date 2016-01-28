package taboleiro.controller.utils;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PaginationUtil<T> {

        public Integer getPreviousPage(Page<T> pages) {
            if (pages.hasPrevious()) {
                Integer currentPage = pages.getNumber();
                return currentPage-1;
            }
            else return -1;
        }

    public Integer getNextPage(Page<T> pages) {
        if (pages.hasNext()) {
            Integer currentPage = pages.getNumber();
            return currentPage+1;
        }
        else return -1;
    }
}
