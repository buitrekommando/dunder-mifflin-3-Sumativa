package com.aiep.dunder3.repository.rowmapper;

import com.aiep.dunder3.domain.Departamento;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Departamento}, with proper type conversions.
 */
@Service
public class DepartamentoRowMapper implements BiFunction<Row, String, Departamento> {

    private final ColumnConverter converter;

    public DepartamentoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Departamento} stored in the database.
     */
    @Override
    public Departamento apply(Row row, String prefix) {
        Departamento entity = new Departamento();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setUbicacion(converter.fromRow(row, prefix + "_ubicacion", String.class));
        entity.setNombre(converter.fromRow(row, prefix + "_nombre", String.class));
        entity.setPresupuesto(converter.fromRow(row, prefix + "_presupuesto", Integer.class));
        return entity;
    }
}
