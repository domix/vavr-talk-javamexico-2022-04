package vavr.talk.javamexico.jooq.api;

import org.jooq.DSLContext;

/**
 * Simple contract for any jOOQ Context
 */
public interface JooqContext {

    String DEFAULT_DOMAIN_NAME = "unnamed-domain";

    DSLContext getContext();

    String getDomainName();

}
