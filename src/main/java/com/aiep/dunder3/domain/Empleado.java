package com.aiep.dunder3.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Empleado.
 */
@Table("empleado")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Empleado implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("nombre")
    private String nombre;

    @NotNull(message = "must not be null")
    @Column("apellido")
    private String apellido;

    @NotNull(message = "must not be null")
    @Column("telefono")
    private String telefono;

    @NotNull(message = "must not be null")
    @Column("email")
    private String email;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "empleados" }, allowSetters = true)
    private Departamento departamento;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "empleados" }, allowSetters = true)
    private Jefe jefe;

    @Column("departamento_id")
    private Long departamentoId;

    @Column("jefe_id")
    private Long jefeId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Empleado id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return this.nombre;
    }

    public Empleado nombre(String nombre) {
        this.setNombre(nombre);
        return this;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return this.apellido;
    }

    public Empleado apellido(String apellido) {
        this.setApellido(apellido);
        return this;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return this.telefono;
    }

    public Empleado telefono(String telefono) {
        this.setTelefono(telefono);
        return this;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return this.email;
    }

    public Empleado email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Departamento getDepartamento() {
        return this.departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
        this.departamentoId = departamento != null ? departamento.getId() : null;
    }

    public Empleado departamento(Departamento departamento) {
        this.setDepartamento(departamento);
        return this;
    }

    public Jefe getJefe() {
        return this.jefe;
    }

    public void setJefe(Jefe jefe) {
        this.jefe = jefe;
        this.jefeId = jefe != null ? jefe.getId() : null;
    }

    public Empleado jefe(Jefe jefe) {
        this.setJefe(jefe);
        return this;
    }

    public Long getDepartamentoId() {
        return this.departamentoId;
    }

    public void setDepartamentoId(Long departamento) {
        this.departamentoId = departamento;
    }

    public Long getJefeId() {
        return this.jefeId;
    }

    public void setJefeId(Long jefe) {
        this.jefeId = jefe;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Empleado)) {
            return false;
        }
        return getId() != null && getId().equals(((Empleado) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Empleado{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", apellido='" + getApellido() + "'" +
            ", telefono='" + getTelefono() + "'" +
            ", email='" + getEmail() + "'" +
            "}";
    }
}
