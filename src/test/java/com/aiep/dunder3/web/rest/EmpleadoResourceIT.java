package com.aiep.dunder3.web.rest;

import static com.aiep.dunder3.domain.EmpleadoAsserts.*;
import static com.aiep.dunder3.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.aiep.dunder3.IntegrationTest;
import com.aiep.dunder3.domain.Empleado;
import com.aiep.dunder3.repository.EmpleadoRepository;
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
 * Integration tests for the {@link EmpleadoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EmpleadoResourceIT {

    private static final String DEFAULT_NOMBRE = "AAAAAAAAAA";
    private static final String UPDATED_NOMBRE = "BBBBBBBBBB";

    private static final String DEFAULT_APELLIDO = "AAAAAAAAAA";
    private static final String UPDATED_APELLIDO = "BBBBBBBBBB";

    private static final String DEFAULT_TELEFONO = "AAAAAAAAAA";
    private static final String UPDATED_TELEFONO = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/empleados";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Empleado empleado;

    private Empleado insertedEmpleado;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Empleado createEntity() {
        return new Empleado().nombre(DEFAULT_NOMBRE).apellido(DEFAULT_APELLIDO).telefono(DEFAULT_TELEFONO).email(DEFAULT_EMAIL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Empleado createUpdatedEntity() {
        return new Empleado().nombre(UPDATED_NOMBRE).apellido(UPDATED_APELLIDO).telefono(UPDATED_TELEFONO).email(UPDATED_EMAIL);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Empleado.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void initTest() {
        empleado = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedEmpleado != null) {
            empleadoRepository.delete(insertedEmpleado).block();
            insertedEmpleado = null;
        }
        deleteEntities(em);
    }

    @Test
    void createEmpleado() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Empleado
        var returnedEmpleado = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(empleado))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Empleado.class)
            .returnResult()
            .getResponseBody();

        // Validate the Empleado in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertEmpleadoUpdatableFieldsEquals(returnedEmpleado, getPersistedEmpleado(returnedEmpleado));

        insertedEmpleado = returnedEmpleado;
    }

    @Test
    void createEmpleadoWithExistingId() throws Exception {
        // Create the Empleado with an existing ID
        empleado.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(empleado))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Empleado in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNombreIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        empleado.setNombre(null);

        // Create the Empleado, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(empleado))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkApellidoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        empleado.setApellido(null);

        // Create the Empleado, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(empleado))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTelefonoIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        empleado.setTelefono(null);

        // Create the Empleado, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(empleado))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        empleado.setEmail(null);

        // Create the Empleado, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(empleado))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllEmpleadosAsStream() {
        // Initialize the database
        empleadoRepository.save(empleado).block();

        List<Empleado> empleadoList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Empleado.class)
            .getResponseBody()
            .filter(empleado::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(empleadoList).isNotNull();
        assertThat(empleadoList).hasSize(1);
        Empleado testEmpleado = empleadoList.get(0);

        // Test fails because reactive api returns an empty object instead of null
        // assertEmpleadoAllPropertiesEquals(empleado, testEmpleado);
        assertEmpleadoUpdatableFieldsEquals(empleado, testEmpleado);
    }

    @Test
    void getAllEmpleados() {
        // Initialize the database
        insertedEmpleado = empleadoRepository.save(empleado).block();

        // Get all the empleadoList
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
            .value(hasItem(empleado.getId().intValue()))
            .jsonPath("$.[*].nombre")
            .value(hasItem(DEFAULT_NOMBRE))
            .jsonPath("$.[*].apellido")
            .value(hasItem(DEFAULT_APELLIDO))
            .jsonPath("$.[*].telefono")
            .value(hasItem(DEFAULT_TELEFONO))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL));
    }

    @Test
    void getEmpleado() {
        // Initialize the database
        insertedEmpleado = empleadoRepository.save(empleado).block();

        // Get the empleado
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, empleado.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(empleado.getId().intValue()))
            .jsonPath("$.nombre")
            .value(is(DEFAULT_NOMBRE))
            .jsonPath("$.apellido")
            .value(is(DEFAULT_APELLIDO))
            .jsonPath("$.telefono")
            .value(is(DEFAULT_TELEFONO))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL));
    }

    @Test
    void getNonExistingEmpleado() {
        // Get the empleado
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingEmpleado() throws Exception {
        // Initialize the database
        insertedEmpleado = empleadoRepository.save(empleado).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the empleado
        Empleado updatedEmpleado = empleadoRepository.findById(empleado.getId()).block();
        updatedEmpleado.nombre(UPDATED_NOMBRE).apellido(UPDATED_APELLIDO).telefono(UPDATED_TELEFONO).email(UPDATED_EMAIL);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedEmpleado.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(updatedEmpleado))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Empleado in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEmpleadoToMatchAllProperties(updatedEmpleado);
    }

    @Test
    void putNonExistingEmpleado() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        empleado.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, empleado.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(empleado))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Empleado in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEmpleado() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        empleado.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(empleado))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Empleado in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEmpleado() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        empleado.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(empleado))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Empleado in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEmpleadoWithPatch() throws Exception {
        // Initialize the database
        insertedEmpleado = empleadoRepository.save(empleado).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the empleado using partial update
        Empleado partialUpdatedEmpleado = new Empleado();
        partialUpdatedEmpleado.setId(empleado.getId());

        partialUpdatedEmpleado.nombre(UPDATED_NOMBRE).apellido(UPDATED_APELLIDO).telefono(UPDATED_TELEFONO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEmpleado.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEmpleado))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Empleado in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEmpleadoUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedEmpleado, empleado), getPersistedEmpleado(empleado));
    }

    @Test
    void fullUpdateEmpleadoWithPatch() throws Exception {
        // Initialize the database
        insertedEmpleado = empleadoRepository.save(empleado).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the empleado using partial update
        Empleado partialUpdatedEmpleado = new Empleado();
        partialUpdatedEmpleado.setId(empleado.getId());

        partialUpdatedEmpleado.nombre(UPDATED_NOMBRE).apellido(UPDATED_APELLIDO).telefono(UPDATED_TELEFONO).email(UPDATED_EMAIL);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEmpleado.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEmpleado))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Empleado in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEmpleadoUpdatableFieldsEquals(partialUpdatedEmpleado, getPersistedEmpleado(partialUpdatedEmpleado));
    }

    @Test
    void patchNonExistingEmpleado() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        empleado.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, empleado.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(empleado))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Empleado in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEmpleado() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        empleado.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(empleado))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Empleado in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEmpleado() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        empleado.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(empleado))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Empleado in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEmpleado() {
        // Initialize the database
        insertedEmpleado = empleadoRepository.save(empleado).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the empleado
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, empleado.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return empleadoRepository.count().block();
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

    protected Empleado getPersistedEmpleado(Empleado empleado) {
        return empleadoRepository.findById(empleado.getId()).block();
    }

    protected void assertPersistedEmpleadoToMatchAllProperties(Empleado expectedEmpleado) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEmpleadoAllPropertiesEquals(expectedEmpleado, getPersistedEmpleado(expectedEmpleado));
        assertEmpleadoUpdatableFieldsEquals(expectedEmpleado, getPersistedEmpleado(expectedEmpleado));
    }

    protected void assertPersistedEmpleadoToMatchUpdatableProperties(Empleado expectedEmpleado) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEmpleadoAllUpdatablePropertiesEquals(expectedEmpleado, getPersistedEmpleado(expectedEmpleado));
        assertEmpleadoUpdatableFieldsEquals(expectedEmpleado, getPersistedEmpleado(expectedEmpleado));
    }
}
