package org.acme.opentelemetry;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.opentelemetry.extension.annotations.WithSpan;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;

@Path("/hello")
public class TracedResource {

    @Inject
    TraceA traceA;

    @Inject
    TraceB traceB;

    @Inject
    TraceC traceC;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @WithSpan
    public Uni<String> hello() {
        return Uni.createFrom().item(0).call(() -> traceC.doSomething()
                .chain(ignore -> Multi.createFrom().iterable(List.of("a","b")).onItem().transformToUniAndMerge(value -> {
            if (value.equals("a")) {
                return traceA.doSomething();
            } else {
                return traceB.doSomething();
            }
        }).collect().last())).replaceWith("ignore");
    }
}