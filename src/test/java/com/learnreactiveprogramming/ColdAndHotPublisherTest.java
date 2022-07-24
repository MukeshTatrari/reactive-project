package com.learnreactiveprogramming;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

@Slf4j
public class ColdAndHotPublisherTest {

    @Test
    void coldPublisherTest() {
        var rangFlux = Flux.range(1, 10);
        rangFlux.subscribe(i -> log.info("Subscriber 1 ::: {} ", i));
        rangFlux.subscribe(i -> log.info("Subscriber 2 ::: {} ", i));
    }

    @Test
    void hotPublisherTest() {
        var rangFlux = Flux.range(1, 10).delayElements(Duration.ofSeconds(1));
        final ConnectableFlux<Integer> connectableFlux = rangFlux.publish();
        connectableFlux.connect();
        connectableFlux.subscribe(i -> log.info("Subscriber 1 ::: {} ", i));
        delay(4000);
        connectableFlux.subscribe(i -> log.info("Subscriber 2 ::: {} ", i));
        delay(10000);
    }

    @Test
    void hotPublisherTest_AutoConnect() {
        var rangFlux = Flux.range(1, 10).delayElements(Duration.ofSeconds(1));
        var hotSource = rangFlux.publish().autoConnect(2);
        hotSource.subscribe(i -> log.info("Subscriber 1 ::: {} ", i));
        delay(2000);
        hotSource.subscribe(i -> log.info("Subscriber 2 ::: {} ", i));
        log.info("two subscribers are connected ::: ");
        delay(2000);
        hotSource.subscribe(i -> log.info("Subscriber 3 ::: {} ", i));
        delay(10000);
    }

    @Test
    void hotPublisherTest_RefCount() {
        var rangFlux = Flux.range(1, 10).delayElements(Duration.ofSeconds(1)).doOnCancel(() -> {
            System.out.println("Received cancel Signal");
        });
        var hotSource = rangFlux.publish().refCount(2);
        var disposable1 = hotSource.subscribe(i -> log.info("Subscriber 1 ::: {} ", i));
        delay(2000);
        var disposable2 = hotSource.subscribe(i -> log.info("Subscriber 2 ::: {} ", i));
        log.info("two subscribers are connected ::: ");
        delay(2000);
        disposable1.dispose();
        disposable2.dispose();
        hotSource.subscribe(i -> log.info("Subscriber 3 ::: {} ", i));
        hotSource.subscribe(i -> log.info("Subscriber 4 ::: {} ", i));
        delay(10000);
    }
}
