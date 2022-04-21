package vavr.talk.javamexico.jooq.entity;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * A page is a slice of objects. It allows gain information about the position of it in the containing entire list.
 *
 * @param <Result> the type of which the page consists.
 */
@Value
@Builder
public class Page<Result> {

    /**
     * The total number of elements on the main list.
     */
    long totalElements;
    /**
     * The total number of pages of the main list.
     */
    int totalPages;

    /**
     * The slice of elements contained in this page.
     */
    private Slice<Result> elements;

    public int getNumber() {
        return elements.getNumberOfElements();
    }

    public List<Result> getContent() {
        return elements.getContent();
    }

    public static <Result> PageBuilder<Result> ofSlice(Slice<Result> slice) {
        return Page.<Result>builder()
            .elements(slice);
    }

    public static <Result> Page<Result> empty() {
        return Page.<Result>ofSlice(Slice.ofContent(List.of())).build();
    }

}
