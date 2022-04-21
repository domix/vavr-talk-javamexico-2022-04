package vavr.talk.javamexico.jooq.entity;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Pageable<Result> {

    /**
     * Defines the amount of rows to be grab with every page
     */
    @Builder.Default
    int pageSize = 10;

    /**
     * Defines which page should be grabbed from the paginated information
     */
    int pageNumber;

    /**
     * Returns the offset to be taken according to the underlying page and page size.
     */
    public int getOffset() {
        return pageSize * (pageNumber - 1);
    }

    /**
     * The return type
     */
    Class<Result> resultClass;
}
