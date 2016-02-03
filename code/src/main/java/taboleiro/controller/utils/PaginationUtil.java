package taboleiro.controller.utils;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class PaginationUtil {

        public Integer getPreviousPage(Page<?> pages) {
            if (pages.hasPrevious()) {
                Integer currentPage = pages.getNumber();
                return currentPage-1;
            }
            else return -1;
        }

    public Integer getNextPage(Page<?> pages) {
        if (pages.hasNext()) {
            Integer currentPage = pages.getNumber();
            return currentPage+1;
        }
        else return -1;
    }
}
