package com.aiep.dunder3.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class JefeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Jefe getJefeSample1() {
        return new Jefe().id(1L).nombre("nombre1").telefono("telefono1");
    }

    public static Jefe getJefeSample2() {
        return new Jefe().id(2L).nombre("nombre2").telefono("telefono2");
    }

    public static Jefe getJefeRandomSampleGenerator() {
        return new Jefe().id(longCount.incrementAndGet()).nombre(UUID.randomUUID().toString()).telefono(UUID.randomUUID().toString());
    }
}
