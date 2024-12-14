package com.aiep.dunder3.domain;

import static com.aiep.dunder3.domain.EmpleadoTestSamples.*;
import static com.aiep.dunder3.domain.JefeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.aiep.dunder3.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class JefeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Jefe.class);
        Jefe jefe1 = getJefeSample1();
        Jefe jefe2 = new Jefe();
        assertThat(jefe1).isNotEqualTo(jefe2);

        jefe2.setId(jefe1.getId());
        assertThat(jefe1).isEqualTo(jefe2);

        jefe2 = getJefeSample2();
        assertThat(jefe1).isNotEqualTo(jefe2);
    }

    @Test
    void empleadoTest() {
        Jefe jefe = getJefeRandomSampleGenerator();
        Empleado empleadoBack = getEmpleadoRandomSampleGenerator();

        jefe.addEmpleado(empleadoBack);
        assertThat(jefe.getEmpleados()).containsOnly(empleadoBack);
        assertThat(empleadoBack.getJefe()).isEqualTo(jefe);

        jefe.removeEmpleado(empleadoBack);
        assertThat(jefe.getEmpleados()).doesNotContain(empleadoBack);
        assertThat(empleadoBack.getJefe()).isNull();

        jefe.empleados(new HashSet<>(Set.of(empleadoBack)));
        assertThat(jefe.getEmpleados()).containsOnly(empleadoBack);
        assertThat(empleadoBack.getJefe()).isEqualTo(jefe);

        jefe.setEmpleados(new HashSet<>());
        assertThat(jefe.getEmpleados()).doesNotContain(empleadoBack);
        assertThat(empleadoBack.getJefe()).isNull();
    }
}
