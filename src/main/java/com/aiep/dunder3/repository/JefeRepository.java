package com.aiep.dunder3.repository;

import com.aiep.dunder3.domain.Jefe;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Jefe entity.
 */
@SuppressWarnings("unused")
@Repository
public interface JefeRepository extends ReactiveCrudRepository<Jefe, Long>, JefeRepositoryInternal {
    @Override
    <S extends Jefe> Mono<S> save(S entity);

    @Override
    Flux<Jefe> findAll();

    @Override
    Mono<Jefe> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface JefeRepositoryInternal {
    <S extends Jefe> Mono<S> save(S entity);

    Flux<Jefe> findAllBy(Pageable pageable);

    Flux<Jefe> findAll();

    Mono<Jefe> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Jefe> findAllBy(Pageable pageable, Criteria criteria);
}
