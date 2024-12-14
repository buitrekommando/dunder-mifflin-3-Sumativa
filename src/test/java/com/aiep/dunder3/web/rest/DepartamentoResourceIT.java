package com.aiep.dunder3.web.rest;

import static com.aiep.dunder3.domain.DepartamentoAsserts.*;
import static com.aiep.dunder3.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.aiep.dunder3.IntegrationTest;
import com.aiep.dunder3.domain.Departamento;
import com.aiep.dunder3.repository.DepartamentoRepository;
import com.aiep.dunder3.repository.EntityManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link DepartamentoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class DepartamentoResourceIT {

    private static final String DEFAULT_UBICACION = "AAAAAAAAAA";
    private static final String UPDATED_UBICACION = "BBBBBBBBBB";

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final Integer DEFAULT_PRESUPUESTO = 1;
    private static final Integer UPDATED_PRESUPUESTO = 2;

    private static final String ENTITY_API_URL = "/api/departamentos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Departamento departamento;

    private Departamento insertedDepartamento;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Departamento createEntity() {
        return new Departamento().ubicacion(DEFAULT_UBICACION).nombre(DEFAULT_NOMBRE).presupuesto(DEFAULT_PRESUPUESTO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Departamento createUpdatedEntity() {
        return new Departamento().ubicacion(UPDATED_UBICACION).nombre(UPDATED_NOMBRE).presupuesto(UPDATED_PRESUPUESTO);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Departamento.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        departamento = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedDepartamento != null) {
            departamentoRepository.delete(insertedDepartamento).block();
            insertedDepartamento = null;
        }
        deleteEntities(em);
    }

    @Test
    void createDepartamento() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Departamento
        var returnedDepartamento = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(departamento))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Departamento.class)
            .returnResult()
            .getResponseBody();

        // Validate the Departamento in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertDepartamentoUpdatableFieldsEquals(returnedDepartamento, getPersistedDepartamento(returnedDepartamento));

        insertedDepartamento = returnedDepartamento;
    }

    @Test
    void createDepartamentoWithExistingId() throws Exception {
        // Create the Departamento with an existing ID
        departamento.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(departamento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Departamento in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkUbicacionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        departamento.setUbicacion(null);

        // Create the Departamento, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(departamento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkNombreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        departamento.setNombre(null);

        // Create the Departamento, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(departamento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkPresupuestoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        departamento.setPresupuesto(null);

        // Create the Departamento, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(departamento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllDepartamentosAsStream() {
        // Initialize the database
        departamentoRepository.save(departamento).block();

        List<Departamento> departamentoList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Departamento.class)
            .getResponseBody()
            .filter(departamento::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(departamentoList).isNotNull();
        assertThat(departamentoList).hasSize(1);
        Departamento testDepartamento = departamentoList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertDepartamentoAllPropertiesEquals(departamento, testDepartamento);
        assertDepartamentoUpdatableFieldsEquals(departamento, testDepartamento);
    }

    @Test
    void getAllDepartamentos() {
        // Initialize the database
        insertedDepartamento = departamentoRepository.save(departamento).block();

        // Get all the departamentoList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(departamento.getId().intValue()))
            .jsonPath("$.[*].ubicacion")
            .value(hasItem(DEFAULT_UBICACION))
            .jsonPath("$.[*].nombre")
            .value(hasItem(DEFAULT_NOMBRE))
            .jsonPath("$.[*].presupuesto")
            .value(hasItem(DEFAULT_PRESUPUESTO));
    }

    @Test
    void getDepartamento() {
        // Initialize the database
        insertedDepartamento = departamentoRepository.save(departamento).block();

        // Get the departamento
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, departamento.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(departamento.getId().intValue()))
            .jsonPath("$.ubicacion")
            .value(is(DEFAULT_UBICACION))
            .jsonPath("$.nombre")
            .value(is(DEFAULT_NOMBRE))
            .jsonPath("$.presupuesto")
            .value(is(DEFAULT_PRESUPUESTO));
    }

    @Test
    void getNonExistingDepartamento() {
        // Get the departamento
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingDepartamento() throws Exception {
        // Initialize the database
        insertedDepartamento = departamentoRepository.save(departamento).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the departamento
        Departamento updatedDepartamento = departamentoRepository.findById(departamento.getId()).block();
        updatedDepartamento.ubicacion(UPDATED_UBICACION).nombre(UPDATED_NOMBRE).presupuesto(UPDATED_PRESUPUESTO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedDepartamento.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedDepartamento))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Departamento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDepartamentoToMatchAllProperties(updatedDepartamento);
    }

    @Test
    void putNonExistingDepartamento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        departamento.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, departamento.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(departamento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Departamento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchDepartamento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        departamento.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(departamento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Departamento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamDepartamento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        departamento.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(departamento))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Departamento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDepartamentoWithPatch() throws Exception {
        // Initialize the database
        insertedDepartamento = departamentoRepository.save(departamento).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the departamento using partial update
        Departamento partialUpdatedDepartamento = new Departamento();
        partialUpdatedDepartamento.setId(departamento.getId());

        partialUpdatedDepartamento.ubicacion(UPDATED_UBICACION).nombre(UPDATED_NOMBRE).presupuesto(UPDATED_PRESUPUESTO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDepartamento.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDepartamento))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Departamento in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDepartamentoUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDepartamento, departamento),
            getPersistedDepartamento(departamento)
        );
    }

    @Test
    void fullUpdateDepartamentoWithPatch() throws Exception {
        // Initialize the database
        insertedDepartamento = departamentoRepository.save(departamento).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the departamento using partial update
        Departamento partialUpdatedDepartamento = new Departamento();
        partialUpdatedDepartamento.setId(departamento.getId());

        partialUpdatedDepartamento.ubicacion(UPDATED_UBICACION).nombre(UPDATED_NOMBRE).presupuesto(UPDATED_PRESUPUESTO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedDepartamento.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedDepartamento))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Departamento in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDepartamentoUpdatableFieldsEquals(partialUpdatedDepartamento, getPersistedDepartamento(partialUpdatedDepartamento));
    }

    @Test
    void patchNonExistingDepartamento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        departamento.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, departamento.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(departamento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Departamento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchDepartamento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        departamento.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(departamento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Departamento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamDepartamento() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        departamento.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(departamento))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Departamento in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteDepartamento() {
        // Initialize the database
        insertedDepartamento = departamentoRepository.save(departamento).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the departamento
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, departamento.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return departamentoRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Departamento getPersistedDepartamento(Departamento departamento) {
        return departamentoRepository.findById(departamento.getId()).block();
    }

    protected void assertPersistedDepartamentoToMatchAllProperties(Departamento expectedDepartamento) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDepartamentoAllPropertiesEquals(expectedDepartamento, getPersistedDepartamento(expectedDepartamento));
        assertDepartamentoUpdatableFieldsEquals(expectedDepartamento, getPersistedDepartamento(expectedDepartamento));
    }

    protected void assertPersistedDepartamentoToMatchUpdatableProperties(Departamento expectedDepartamento) {
        // Test fails because reactive api returns an empty object instead of null
        // assertDepartamentoAllUpdatablePropertiesEquals(expectedDepartamento, getPersistedDepartamento(expectedDepartamento));
        assertDepartamentoUpdatableFieldsEquals(expectedDepartamento, getPersistedDepartamento(expectedDepartamento));
    }
}
