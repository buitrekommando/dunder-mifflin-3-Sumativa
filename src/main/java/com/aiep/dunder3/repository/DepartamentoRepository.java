package com.aiep.dunder3.repository;

import com.aiep.dunder3.domain.Departamento;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Departamento entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DepartamentoRepository extends ReactiveCrudRepository<Departamento, Long>, DepartamentoRepositoryInternal {
    @Override
    <S extends Departamento> Mono<S> save(S entity);

    @Override
    Flux<Departamento> findAll();

    @Override
    Mono<Departamento> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface DepartamentoRepositoryInternal {
    <S extends Departamento> Mono<S> save(S entity);

    Flux<Departamento> findAllBy(Pageable pageable);

    Flux<Departamento> findAll();

    Mono<Departamento> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Departamento> findAllBy(Pageable pageable, Criteria criteria);
}
