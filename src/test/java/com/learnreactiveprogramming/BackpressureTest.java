package com.learnreactiveprogramming;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class BackpressureTest {

    @Test
    void testWithoutBackPressure() {
        var numberRange = Flux.range(1, 100);
        numberRange.subscribe(num -> log.info("Number is ::: {} " + num));
    }

    @Test
    void testBackPressure() {
        var numberRange = Flux.range(1, 100);
        numberRange.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(10);
            }

            @Override
            protected void hookOnNext(Integer value) {
                log.info("Number is ::: {} " + value);
                if (value == 5) {
                    cancel();
                }
            }

            @Override
            protected void hookOnComplete() {
                super.hookOnComplete();
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                super.hookOnError(throwable);
            }

            @Override
            protected void hookOnCancel() {
                log.info("inside on cancel :::: ");
            }

            @Override
            protected void hookFinally(SignalType type) {
                super.hookFinally(type);
            }
        });
    }

    @Test
    void testBackPressure_1() throws InterruptedException {
        var numberRange = Flux.range(1, 1000).log();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        numberRange.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(5);
            }

            @Override
            protected void hookOnNext(Integer value) {
                log.info("Number is ::: {} " + value);
                if (value < 60 || value % 5 == 0) {
                    request(5);
                } else {
                    cancel();
                }
            }

            @Override
            protected void hookOnComplete() {
                super.hookOnComplete();
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                super.hookOnError(throwable);
            }

            @Override
            protected void hookOnCancel() {
                log.info("inside on cancel :::: ");
                countDownLatch.countDown();
            }

            @Override
            protected void hookFinally(SignalType type) {
                super.hookFinally(type);
            }
        });
        assertTrue(countDownLatch.await(5L, TimeUnit.SECONDS));
    }

    @Test
    void testBackPressure_Drop() throws InterruptedException {
        var numberRange = Flux.range(1, 100).log();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        numberRange.onBackpressureDrop(item -> {
            log.info("drop Items ::: {} ", item);
        }).subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(4);
            }

            @Override
            protected void hookOnNext(Integer value) {
                log.info("Number is ::: {} " + value);
                if (value == 4) {
                    hookOnCancel();
                }
            }

            @Override
            protected void hookOnComplete() {
                super.hookOnComplete();
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                super.hookOnError(throwable);
            }

            @Override
            protected void hookOnCancel() {
                log.info("inside on cancel :::: ");
                countDownLatch.countDown();
            }

            @Override
            protected void hookFinally(SignalType type) {
                super.hookFinally(type);
            }
        });
        assertTrue(countDownLatch.await(5L, TimeUnit.SECONDS));
    }

    @Test
    void testBackPressure_Buffer() throws InterruptedException {
        var numberRange = Flux.range(1, 100).log();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        numberRange.onBackpressureBuffer(10, i -> {
            log.info("last buffered element is ::: {} ", i);
        }).subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(2);
            }

            @Override
            protected void hookOnNext(Integer value) {
                log.info("Number is ::: {} " + value);
                if (value < 50) {
                    request(1);
                } else {
                    hookOnCancel();
                }
            }

            @Override
            protected void hookOnComplete() {
                super.hookOnComplete();
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                super.hookOnError(throwable);
            }

            @Override
            protected void hookOnCancel() {
                log.info("inside on cancel :::: ");
                countDownLatch.countDown();
            }

            @Override
            protected void hookFinally(SignalType type) {
                super.hookFinally(type);
            }
        });
        assertTrue(countDownLatch.await(5L, TimeUnit.SECONDS));
    }

    @Test
    void testBackPressure_Error() throws InterruptedException {
        var numberRange = Flux.range(1, 100).log();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        numberRange.onBackpressureError()
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(2);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        log.info("Number is ::: {} " + value);
                        if (value < 50) {
                            request(1);
                        } else {
                            hookOnCancel();
                        }
                    }

                    @Override
                    protected void hookOnComplete() {
                        super.hookOnComplete();
                    }

                    @Override
                    protected void hookOnError(Throwable throwable) {
//                        super.hookOnError(throwable);
                        log.info("Error is  :::: " + throwable);
                    }

                    @Override
                    protected void hookOnCancel() {
                        log.info("inside on cancel :::: ");
                        countDownLatch.countDown();
                    }

                    @Override
                    protected void hookFinally(SignalType type) {
                        super.hookFinally(type);
                    }
                });
        assertTrue(countDownLatch.await(5L, TimeUnit.SECONDS));
    }
}
