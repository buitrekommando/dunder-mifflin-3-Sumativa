package com.aiep.dunder3.web.rest;

import static com.aiep.dunder3.domain.JefeAsserts.*;
import static com.aiep.dunder3.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.aiep.dunder3.IntegrationTest;
import com.aiep.dunder3.domain.Jefe;
import com.aiep.dunder3.repository.EntityManager;
import com.aiep.dunder3.repository.JefeRepository;
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
 * Integration tests for the {@link JefeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class JefeResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_TELEFONO = "AAAAAAAAAA";
    private static final String UPDATED_TELEFONO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/jefes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private JefeRepository jefeRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Jefe jefe;

    private Jefe insertedJefe;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Jefe createEntity() {
        return new Jefe().nombre(DEFAULT_NOMBRE).telefono(DEFAULT_TELEFONO);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Jefe createUpdatedEntity() {
        return new Jefe().nombre(UPDATED_NOMBRE).telefono(UPDATED_TELEFONO);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Jefe.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        jefe = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedJefe != null) {
            jefeRepository.delete(insertedJefe).block();
            insertedJefe = null;
        }
        deleteEntities(em);
    }

    @Test
    void createJefe() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Jefe
        var returnedJefe = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(jefe))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Jefe.class)
            .returnResult()
            .getResponseBody();

        // Validate the Jefe in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertJefeUpdatableFieldsEquals(returnedJefe, getPersistedJefe(returnedJefe));

        insertedJefe = returnedJefe;
    }

    @Test
    void createJefeWithExistingId() throws Exception {
        // Create the Jefe with an existing ID
        jefe.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(jefe))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Jefe in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNombreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        jefe.setNombre(null);

        // Create the Jefe, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(jefe))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTelefonoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        jefe.setTelefono(null);

        // Create the Jefe, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(jefe))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllJefesAsStream() {
        // Initialize the database
        jefeRepository.save(jefe).block();

        List<Jefe> jefeList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Jefe.class)
            .getResponseBody()
            .filter(jefe::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(jefeList).isNotNull();
        assertThat(jefeList).hasSize(1);
        Jefe testJefe = jefeList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertJefeAllPropertiesEquals(jefe, testJefe);
        assertJefeUpdatableFieldsEquals(jefe, testJefe);
    }

    @Test
    void getAllJefes() {
        // Initialize the database
        insertedJefe = jefeRepository.save(jefe).block();

        // Get all the jefeList
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
            .value(hasItem(jefe.getId().intValue()))
            .jsonPath("$.[*].nombre")
            .value(hasItem(DEFAULT_NOMBRE))
            .jsonPath("$.[*].telefono")
            .value(hasItem(DEFAULT_TELEFONO));
    }

    @Test
    void getJefe() {
        // Initialize the database
        insertedJefe = jefeRepository.save(jefe).block();

        // Get the jefe
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, jefe.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(jefe.getId().intValue()))
            .jsonPath("$.nombre")
            .value(is(DEFAULT_NOMBRE))
            .jsonPath("$.telefono")
            .value(is(DEFAULT_TELEFONO));
    }

    @Test
    void getNonExistingJefe() {
        // Get the jefe
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingJefe() throws Exception {
        // Initialize the database
        insertedJefe = jefeRepository.save(jefe).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the jefe
        Jefe updatedJefe = jefeRepository.findById(jefe.getId()).block();
        updatedJefe.nombre(UPDATED_NOMBRE).telefono(UPDATED_TELEFONO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedJefe.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedJefe))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Jefe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedJefeToMatchAllProperties(updatedJefe);
    }

    @Test
    void putNonExistingJefe() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        jefe.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, jefe.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(jefe))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Jefe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchJefe() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        jefe.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(jefe))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Jefe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamJefe() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        jefe.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(jefe))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Jefe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateJefeWithPatch() throws Exception {
        // Initialize the database
        insertedJefe = jefeRepository.save(jefe).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the jefe using partial update
        Jefe partialUpdatedJefe = new Jefe();
        partialUpdatedJefe.setId(jefe.getId());

        partialUpdatedJefe.nombre(UPDATED_NOMBRE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedJefe.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedJefe))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Jefe in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertJefeUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedJefe, jefe), getPersistedJefe(jefe));
    }

    @Test
    void fullUpdateJefeWithPatch() throws Exception {
        // Initialize the database
        insertedJefe = jefeRepository.save(jefe).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the jefe using partial update
        Jefe partialUpdatedJefe = new Jefe();
        partialUpdatedJefe.setId(jefe.getId());

        partialUpdatedJefe.nombre(UPDATED_NOMBRE).telefono(UPDATED_TELEFONO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedJefe.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedJefe))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Jefe in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertJefeUpdatableFieldsEquals(partialUpdatedJefe, getPersistedJefe(partialUpdatedJefe));
    }

    @Test
    void patchNonExistingJefe() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        jefe.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, jefe.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(jefe))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Jefe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchJefe() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        jefe.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(jefe))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Jefe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamJefe() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        jefe.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(jefe))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Jefe in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteJefe() {
        // Initialize the database
        insertedJefe = jefeRepository.save(jefe).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the jefe
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, jefe.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return jefeRepository.count().block();
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

    protected Jefe getPersistedJefe(Jefe jefe) {
        return jefeRepository.findById(jefe.getId()).block();
    }

    protected void assertPersistedJefeToMatchAllProperties(Jefe expectedJefe) {
        // Test fails because reactive api returns an empty object instead of null
        // assertJefeAllPropertiesEquals(expectedJefe, getPersistedJefe(expectedJefe));
        assertJefeUpdatableFieldsEquals(expectedJefe, getPersistedJefe(expectedJefe));
    }

    protected void assertPersistedJefeToMatchUpdatableProperties(Jefe expectedJefe) {
        // Test fails because reactive api returns an empty object instead of null
        // assertJefeAllUpdatablePropertiesEquals(expectedJefe, getPersistedJefe(expectedJefe));
        assertJefeUpdatableFieldsEquals(expectedJefe, getPersistedJefe(expectedJefe));
    }
}
