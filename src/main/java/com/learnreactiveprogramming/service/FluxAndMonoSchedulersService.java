package com.learnreactiveprogramming.service;


import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

public class FluxAndMonoSchedulersService {

    static List<String> namesList = List.of("alex", "ben", "chloe");
    static List<String> namesList1 = List.of("adam", "jill", "jack");

    private String upperCase(String name) {
        delay(1000);
        return name.toUpperCase();
    }

    public Flux<String> explore_WithoutPublisherOn() {
        var namesFlux = getNamedFlux(Flux.fromIterable(namesList)).log();
        var namesFlux1 = getNamedFlux(Flux.fromIterable(namesList1)).log();

        return namesFlux.mergeWith(namesFlux1);
    }

    public Flux<String> explore_PublisherOn() {
        var namesFlux = getNamedFlux(Flux.fromIterable(namesList).publishOn(Schedulers.parallel())).log();
        var namesFlux1 = getNamedFlux(Flux.fromIterable(namesList1).publishOn(Schedulers.parallel())).log();

        return namesFlux.mergeWith(namesFlux1);
    }

    public Flux<String> explore_BoundedElastic() {
        var namesFlux = getNamedFlux(Flux.fromIterable(namesList).publishOn(Schedulers.boundedElastic())).log();
        var namesFlux1 = getNamedFlux(Flux.fromIterable(namesList1).publishOn(Schedulers.boundedElastic())).log();

        return namesFlux.mergeWith(namesFlux1);
    }

    public Flux<String> explore_SubscribeOn() {
        var namesFlux = getNamedFlux(Flux.fromIterable(namesList)).subscribeOn(Schedulers.boundedElastic()).log();
        var namesFlux1 = getNamedFlux(Flux.fromIterable(namesList1)).subscribeOn(Schedulers.boundedElastic()).log();

        return namesFlux.mergeWith(namesFlux1);
    }

    /**
     *
     * @param namesList
     * @return
     *
     * this code is blocking which could be a 3rd party library code
     * which u can not change , so the execution of main thread will be blocked.
     * to unblock the main thread and execute the whole stream in parallel thread
     * we can use the SubscribeON
     * so that the whole stream will be nonblocking.
     */

    private Flux<String> getNamedFlux(Flux<String> namesList) {
        return namesList.map(this::upperCase);
    }


}
