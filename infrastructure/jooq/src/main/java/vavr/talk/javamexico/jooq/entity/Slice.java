package vavr.talk.javamexico.jooq.entity;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Slice<Result> {

    /**
     * The number of elements of the current slice.
     */
    int numberOfElements;
    /**
     * The list of elements contained in this slice.
     */
    List<Result> content;
    /**
     * The identifier to request the next slice
     */
    String nextSliceId;

    private static <Result> SliceBuilder<Result> partialBuild(final List<Result> content) {
        return Slice.<Result>builder()
            .content(content)
            .numberOfElements(content.size());
    }

    public static <Result> Slice<Result> ofContent(final List<Result> content) {
        return partialBuild(content).build();
    }

    public static <Result> Slice<Result> ofContent(final List<Result> content, String nextSliceId) {
        return partialBuild(content).nextSliceId(nextSliceId).build();
    }

}
