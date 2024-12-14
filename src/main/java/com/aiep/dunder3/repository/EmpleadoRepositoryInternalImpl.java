package com.aiep.dunder3.repository;

import com.aiep.dunder3.domain.Empleado;
import com.aiep.dunder3.repository.rowmapper.DepartamentoRowMapper;
import com.aiep.dunder3.repository.rowmapper.EmpleadoRowMapper;
import com.aiep.dunder3.repository.rowmapper.JefeRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC custom repository implementation for the Empleado entity.
 */
@SuppressWarnings("unused")
class EmpleadoRepositoryInternalImpl extends SimpleR2dbcRepository<Empleado, Long> implements EmpleadoRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final DepartamentoRowMapper departamentoMapper;
    private final JefeRowMapper jefeMapper;
    private final EmpleadoRowMapper empleadoMapper;

    private static final Table entityTable = Table.aliased("empleado", EntityManager.ENTITY_ALIAS);
    private static final Table departamentoTable = Table.aliased("departamento", "departamento");
    private static final Table jefeTable = Table.aliased("jefe", "jefe");

    public EmpleadoRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        DepartamentoRowMapper departamentoMapper,
        JefeRowMapper jefeMapper,
        EmpleadoRowMapper empleadoMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Empleado.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.departamentoMapper = departamentoMapper;
        this.jefeMapper = jefeMapper;
        this.empleadoMapper = empleadoMapper;
    }

    @Override
    public Flux<Empleado> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Empleado> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = EmpleadoSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(DepartamentoSqlHelper.getColumns(departamentoTable, "departamento"));
        columns.addAll(JefeSqlHelper.getColumns(jefeTable, "jefe"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(departamentoTable)
            .on(Column.create("departamento_id", entityTable))
            .equals(Column.create("id", departamentoTable))
            .leftOuterJoin(jefeTable)
            .on(Column.create("jefe_id", entityTable))
            .equals(Column.create("id", jefeTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Empleado.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Empleado> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Empleado> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Empleado process(Row row, RowMetadata metadata) {
        Empleado entity = empleadoMapper.apply(row, "e");
        entity.setDepartamento(departamentoMapper.apply(row, "departamento"));
        entity.setJefe(jefeMapper.apply(row, "jefe"));
        return entity;
    }

    @Override
    public <S extends Empleado> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
