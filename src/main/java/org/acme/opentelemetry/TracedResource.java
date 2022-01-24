package org.acme.opentelemetry;

import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.acme.hibernate.reactive.Fruit;
import org.hibernate.reactive.mutiny.Mutiny;

import io.opentelemetry.extension.annotations.WithSpan;
import io.quarkus.vertx.ConsumeEvent;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.eventbus.EventBus;

@Path("/hello")
public class TracedResource {

    @Inject
    EventBus eventBus;

    @Inject
    Mutiny.SessionFactory sessionFactory;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> hello() {
        return Uni.createFrom().item("Hello RESTEasy")
                .onItem().invoke(() -> this.eventBus.send("bus", "Hello EventBus"));
    }

    @ConsumeEvent("bus")
    public Uni<Void> onBusEvent(final String msg) {
        System.out.println("Received " + msg);
        return getFruits() //
            .onItem().invoke((Consumer<List<Fruit>>) System.out::println) //
            .replaceWithVoid();
    }

    public Uni<List<Fruit>> getFruits() {
       return sessionFactory.withTransaction((s, t) -> s.createNamedQuery("Fruits.findAll", Fruit.class).getResultList());
    }
}