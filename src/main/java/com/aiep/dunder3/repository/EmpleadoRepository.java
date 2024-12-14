package com.aiep.dunder3.repository;

import com.aiep.dunder3.domain.Empleado;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Empleado entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EmpleadoRepository extends ReactiveCrudRepository<Empleado, Long>, EmpleadoRepositoryInternal {
    @Query("SELECT * FROM empleado entity WHERE entity.departamento_id = :id")
    Flux<Empleado> findByDepartamento(Long id);

    @Query("SELECT * FROM empleado entity WHERE entity.departamento_id IS NULL")
    Flux<Empleado> findAllWhereDepartamentoIsNull();

    @Query("SELECT * FROM empleado entity WHERE entity.jefe_id = :id")
    Flux<Empleado> findByJefe(Long id);

    @Query("SELECT * FROM empleado entity WHERE entity.jefe_id IS NULL")
    Flux<Empleado> findAllWhereJefeIsNull();

    @Override
    <S extends Empleado> Mono<S> save(S entity);

    @Override
    Flux<Empleado> findAll();

    @Override
    Mono<Empleado> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface EmpleadoRepositoryInternal {
    <S extends Empleado> Mono<S> save(S entity);

    Flux<Empleado> findAllBy(Pageable pageable);

    Flux<Empleado> findAll();

    Mono<Empleado> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Empleado> findAllBy(Pageable pageable, Criteria criteria);
}
