package com.aiep.dunder3.repository.rowmapper;

import com.aiep.dunder3.domain.Jefe;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Jefe}, with proper type conversions.
 */
@Service
public class JefeRowMapper implements BiFunction<Row, String, Jefe> {

    private final ColumnConverter converter;

    public JefeRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Jefe} stored in the database.
     */
    @Override
    public Jefe apply(Row row, String prefix) {
        Jefe entity = new Jefe();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNombre(converter.fromRow(row, prefix + "_nombre", String.class));
        entity.setTelefono(converter.fromRow(row, prefix + "_telefono", String.class));
        return entity;
    }
}
