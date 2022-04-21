package vavr.talk.javamexico.jooq.factory;

import io.vavr.Tuple2;
import org.jooq.Condition;
import org.jooq.impl.DSL;

import java.util.List;
import java.util.function.Supplier;

public final class JooqQueryFactory {

    /**
     * Creates a conditional all-of Condition. It reduces a given list of tuples of the
     * form (conditionToFulfill, conditionToAddIfTrue). If conditionToFulfill evaluates to true,
     * the reduced value will be "and'd" with the conditionToAddIfTrue, for each tuple.
     *
     * @param elements to reduce.
     * @return the Condition that represents the whole chain.
     */
    @SafeVarargs
    public static Condition andChain(
        final Tuple2<Boolean, Supplier<Condition>>... elements
    ) {
        var condition = DSL.noCondition();
        for (var element : List.of(elements)) {
            if (element._1) {
                condition = condition.and(element._2.get());
            }
        }
        return condition;
    }

}
