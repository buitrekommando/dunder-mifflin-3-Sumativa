package com.aiep.dunder3.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DepartamentoTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Departamento getDepartamentoSample1() {
        return new Departamento().id(1L).ubicacion("ubicacion1").nombre("nombre1").presupuesto(1);
    }

    public static Departamento getDepartamentoSample2() {
        return new Departamento().id(2L).ubicacion("ubicacion2").nombre("nombre2").presupuesto(2);
    }

    public static Departamento getDepartamentoRandomSampleGenerator() {
        return new Departamento()
            .id(longCount.incrementAndGet())
            .ubicacion(UUID.randomUUID().toString())
            .nombre(UUID.randomUUID().toString())
            .presupuesto(intCount.incrementAndGet());
    }
}
