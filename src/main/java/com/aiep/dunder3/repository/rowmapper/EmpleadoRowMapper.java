package com.aiep.dunder3.repository.rowmapper;

import com.aiep.dunder3.domain.Empleado;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Empleado}, with proper type conversions.
 */
@Service
public class EmpleadoRowMapper implements BiFunction<Row, String, Empleado> {

    private final ColumnConverter converter;

    public EmpleadoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Empleado} stored in the database.
     */
    @Override
    public Empleado apply(Row row, String prefix) {
        Empleado entity = new Empleado();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNombre(converter.fromRow(row, prefix + "_nombre", String.class));
        entity.setApellido(converter.fromRow(row, prefix + "_apellido", String.class));
        entity.setTelefono(converter.fromRow(row, prefix + "_telefono", String.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setDepartamentoId(converter.fromRow(row, prefix + "_departamento_id", Long.class));
        entity.setJefeId(converter.fromRow(row, prefix + "_jefe_id", Long.class));
        return entity;
    }
}
