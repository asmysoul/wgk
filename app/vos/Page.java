package vos;

import java.util.Collections;
import java.util.List;

public class Page<T> {

    public static Page EMPTY = new Page();
    
    public int pageNo = 1;
    public int pageSize = 1;

    public int startIndex;
    public int totalCount;
    public int totalPageCount;
    public List<T> items = Collections.EMPTY_LIST;

    public static <T> Page<T> newInstance(int pageNo, int pageSize, int totalCount) {
        Page page = new Page();
        page.pageNo = pageNo;
        page.pageSize = pageSize;
        page.totalCount = totalCount;
        page.startIndex = page.getStartIndex();
        return page;
    }

    public int getStartIndex() {
        if (pageNo > 0) {
            return pageSize * (pageNo - 1);
        }
        return 0;
    }

}
