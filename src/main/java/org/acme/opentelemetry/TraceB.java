package org.acme.opentelemetry;

import javax.enterprise.context.ApplicationScoped;

import io.opentelemetry.extension.annotations.WithSpan;
import io.smallrye.mutiny.Uni;

/**
 * @author Christopher Holomek (christopher.holomek@bmw.de)
 * @since 18.07.22
 */
@ApplicationScoped
public class TraceB {

    @WithSpan
    public Uni<String> doSomething() {
        return Uni.createFrom().item("b");
    }
}
