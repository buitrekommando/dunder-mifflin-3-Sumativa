package com.aiep.dunder3.web.rest;

import com.aiep.dunder3.domain.Jefe;
import com.aiep.dunder3.repository.JefeRepository;
import com.aiep.dunder3.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.aiep.dunder3.domain.Jefe}.
 */
@RestController
@RequestMapping("/api/jefes")
@Transactional
public class JefeResource {

    private static final Logger LOG = LoggerFactory.getLogger(JefeResource.class);

    private static final String ENTITY_NAME = "jefe";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final JefeRepository jefeRepository;

    public JefeResource(JefeRepository jefeRepository) {
        this.jefeRepository = jefeRepository;
    }

    /**
     * {@code POST  /jefes} : Create a new jefe.
     *
     * @param jefe the jefe to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new jefe, or with status {@code 400 (Bad Request)} if the jefe has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<Jefe>> createJefe(@Valid @RequestBody Jefe jefe) throws URISyntaxException {
        LOG.debug("REST request to save Jefe : {}", jefe);
        if (jefe.getId() != null) {
            throw new BadRequestAlertException("A new jefe cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return jefeRepository
            .save(jefe)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/jefes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /jefes/:id} : Updates an existing jefe.
     *
     * @param id the id of the jefe to save.
     * @param jefe the jefe to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated jefe,
     * or with status {@code 400 (Bad Request)} if the jefe is not valid,
     * or with status {@code 500 (Internal Server Error)} if the jefe couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Jefe>> updateJefe(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Jefe jefe
    ) throws URISyntaxException {
        LOG.debug("REST request to update Jefe : {}, {}", id, jefe);
        if (jefe.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, jefe.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return jefeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return jefeRepository
                    .save(jefe)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /jefes/:id} : Partial updates given fields of an existing jefe, field will ignore if it is null
     *
     * @param id the id of the jefe to save.
     * @param jefe the jefe to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated jefe,
     * or with status {@code 400 (Bad Request)} if the jefe is not valid,
     * or with status {@code 404 (Not Found)} if the jefe is not found,
     * or with status {@code 500 (Internal Server Error)} if the jefe couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Jefe>> partialUpdateJefe(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Jefe jefe
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Jefe partially : {}, {}", id, jefe);
        if (jefe.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, jefe.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return jefeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Jefe> result = jefeRepository
                    .findById(jefe.getId())
                    .map(existingJefe -> {
                        if (jefe.getNombre() != null) {
                            existingJefe.setNombre(jefe.getNombre());
                        }
                        if (jefe.getTelefono() != null) {
                            existingJefe.setTelefono(jefe.getTelefono());
                        }

                        return existingJefe;
                    })
                    .flatMap(jefeRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /jefes} : get all the jefes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of jefes in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<Jefe>> getAllJefes() {
        LOG.debug("REST request to get all Jefes");
        return jefeRepository.findAll().collectList();
    }

    /**
     * {@code GET  /jefes} : get all the jefes as a stream.
     * @return the {@link Flux} of jefes.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Jefe> getAllJefesAsStream() {
        LOG.debug("REST request to get all Jefes as a stream");
        return jefeRepository.findAll();
    }

    /**
     * {@code GET  /jefes/:id} : get the "id" jefe.
     *
     * @param id the id of the jefe to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the jefe, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Jefe>> getJefe(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Jefe : {}", id);
        Mono<Jefe> jefe = jefeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(jefe);
    }

    /**
     * {@code DELETE  /jefes/:id} : delete the "id" jefe.
     *
     * @param id the id of the jefe to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteJefe(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Jefe : {}", id);
        return jefeRepository
            .deleteById(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
