package com.aiep.dunder3.domain;

import static com.aiep.dunder3.domain.DepartamentoTestSamples.*;
import static com.aiep.dunder3.domain.EmpleadoTestSamples.*;
import static com.aiep.dunder3.domain.JefeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.aiep.dunder3.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EmpleadoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Empleado.class);
        Empleado empleado1 = getEmpleadoSample1();
        Empleado empleado2 = new Empleado();
        assertThat(empleado1).isNotEqualTo(empleado2);

        empleado2.setId(empleado1.getId());
        assertThat(empleado1).isEqualTo(empleado2);

        empleado2 = getEmpleadoSample2();
        assertThat(empleado1).isNotEqualTo(empleado2);
    }

    @Test
    void departamentoTest() {
        Empleado empleado = getEmpleadoRandomSampleGenerator();
        Departamento departamentoBack = getDepartamentoRandomSampleGenerator();

        empleado.setDepartamento(departamentoBack);
        assertThat(empleado.getDepartamento()).isEqualTo(departamentoBack);

        empleado.departamento(null);
        assertThat(empleado.getDepartamento()).isNull();
    }

    @Test
    void jefeTest() {
        Empleado empleado = getEmpleadoRandomSampleGenerator();
        Jefe jefeBack = getJefeRandomSampleGenerator();

        empleado.setJefe(jefeBack);
        assertThat(empleado.getJefe()).isEqualTo(jefeBack);

        empleado.jefe(null);
        assertThat(empleado.getJefe()).isNull();
    }
}
