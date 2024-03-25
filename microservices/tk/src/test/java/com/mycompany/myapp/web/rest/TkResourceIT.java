package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Tk;
import com.mycompany.myapp.repository.TkRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TkResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TkResourceIT {

    private static final String DEFAULT_FIELD_1 = "AAAAAAAAAA";
    private static final String UPDATED_FIELD_1 = "BBBBBBBBBB";

    private static final Integer DEFAULT_FIELD_2 = 1;
    private static final Integer UPDATED_FIELD_2 = 2;

    private static final String ENTITY_API_URL = "/api/tks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TkRepository tkRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTkMockMvc;

    private Tk tk;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tk createEntity(EntityManager em) {
        Tk tk = new Tk().field1(DEFAULT_FIELD_1).field2(DEFAULT_FIELD_2);
        return tk;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tk createUpdatedEntity(EntityManager em) {
        Tk tk = new Tk().field1(UPDATED_FIELD_1).field2(UPDATED_FIELD_2);
        return tk;
    }

    @BeforeEach
    public void initTest() {
        tk = createEntity(em);
    }

    @Test
    @Transactional
    void createTk() throws Exception {
        int databaseSizeBeforeCreate = tkRepository.findAll().size();
        // Create the Tk
        restTkMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tk))
            )
            .andExpect(status().isCreated());

        // Validate the Tk in the database
        List<Tk> tkList = tkRepository.findAll();
        assertThat(tkList).hasSize(databaseSizeBeforeCreate + 1);
        Tk testTk = tkList.get(tkList.size() - 1);
        assertThat(testTk.getField1()).isEqualTo(DEFAULT_FIELD_1);
        assertThat(testTk.getField2()).isEqualTo(DEFAULT_FIELD_2);
    }

    @Test
    @Transactional
    void createTkWithExistingId() throws Exception {
        // Create the Tk with an existing ID
        tk.setId(1L);

        int databaseSizeBeforeCreate = tkRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTkMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tk))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tk in the database
        List<Tk> tkList = tkRepository.findAll();
        assertThat(tkList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTks() throws Exception {
        // Initialize the database
        tkRepository.saveAndFlush(tk);

        // Get all the tkList
        restTkMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tk.getId().intValue())))
            .andExpect(jsonPath("$.[*].field1").value(hasItem(DEFAULT_FIELD_1)))
            .andExpect(jsonPath("$.[*].field2").value(hasItem(DEFAULT_FIELD_2)));
    }

    @Test
    @Transactional
    void getTk() throws Exception {
        // Initialize the database
        tkRepository.saveAndFlush(tk);

        // Get the tk
        restTkMockMvc
            .perform(get(ENTITY_API_URL_ID, tk.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tk.getId().intValue()))
            .andExpect(jsonPath("$.field1").value(DEFAULT_FIELD_1))
            .andExpect(jsonPath("$.field2").value(DEFAULT_FIELD_2));
    }

    @Test
    @Transactional
    void getNonExistingTk() throws Exception {
        // Get the tk
        restTkMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTk() throws Exception {
        // Initialize the database
        tkRepository.saveAndFlush(tk);

        int databaseSizeBeforeUpdate = tkRepository.findAll().size();

        // Update the tk
        Tk updatedTk = tkRepository.findById(tk.getId()).get();
        // Disconnect from session so that the updates on updatedTk are not directly saved in db
        em.detach(updatedTk);
        updatedTk.field1(UPDATED_FIELD_1).field2(UPDATED_FIELD_2);

        restTkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTk.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTk))
            )
            .andExpect(status().isOk());

        // Validate the Tk in the database
        List<Tk> tkList = tkRepository.findAll();
        assertThat(tkList).hasSize(databaseSizeBeforeUpdate);
        Tk testTk = tkList.get(tkList.size() - 1);
        assertThat(testTk.getField1()).isEqualTo(UPDATED_FIELD_1);
        assertThat(testTk.getField2()).isEqualTo(UPDATED_FIELD_2);
    }

    @Test
    @Transactional
    void putNonExistingTk() throws Exception {
        int databaseSizeBeforeUpdate = tkRepository.findAll().size();
        tk.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tk.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tk))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tk in the database
        List<Tk> tkList = tkRepository.findAll();
        assertThat(tkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTk() throws Exception {
        int databaseSizeBeforeUpdate = tkRepository.findAll().size();
        tk.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTkMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tk))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tk in the database
        List<Tk> tkList = tkRepository.findAll();
        assertThat(tkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTk() throws Exception {
        int databaseSizeBeforeUpdate = tkRepository.findAll().size();
        tk.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTkMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tk))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tk in the database
        List<Tk> tkList = tkRepository.findAll();
        assertThat(tkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTkWithPatch() throws Exception {
        // Initialize the database
        tkRepository.saveAndFlush(tk);

        int databaseSizeBeforeUpdate = tkRepository.findAll().size();

        // Update the tk using partial update
        Tk partialUpdatedTk = new Tk();
        partialUpdatedTk.setId(tk.getId());

        restTkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTk.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTk))
            )
            .andExpect(status().isOk());

        // Validate the Tk in the database
        List<Tk> tkList = tkRepository.findAll();
        assertThat(tkList).hasSize(databaseSizeBeforeUpdate);
        Tk testTk = tkList.get(tkList.size() - 1);
        assertThat(testTk.getField1()).isEqualTo(DEFAULT_FIELD_1);
        assertThat(testTk.getField2()).isEqualTo(DEFAULT_FIELD_2);
    }

    @Test
    @Transactional
    void fullUpdateTkWithPatch() throws Exception {
        // Initialize the database
        tkRepository.saveAndFlush(tk);

        int databaseSizeBeforeUpdate = tkRepository.findAll().size();

        // Update the tk using partial update
        Tk partialUpdatedTk = new Tk();
        partialUpdatedTk.setId(tk.getId());

        partialUpdatedTk.field1(UPDATED_FIELD_1).field2(UPDATED_FIELD_2);

        restTkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTk.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTk))
            )
            .andExpect(status().isOk());

        // Validate the Tk in the database
        List<Tk> tkList = tkRepository.findAll();
        assertThat(tkList).hasSize(databaseSizeBeforeUpdate);
        Tk testTk = tkList.get(tkList.size() - 1);
        assertThat(testTk.getField1()).isEqualTo(UPDATED_FIELD_1);
        assertThat(testTk.getField2()).isEqualTo(UPDATED_FIELD_2);
    }

    @Test
    @Transactional
    void patchNonExistingTk() throws Exception {
        int databaseSizeBeforeUpdate = tkRepository.findAll().size();
        tk.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tk.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tk))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tk in the database
        List<Tk> tkList = tkRepository.findAll();
        assertThat(tkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTk() throws Exception {
        int databaseSizeBeforeUpdate = tkRepository.findAll().size();
        tk.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTkMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tk))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tk in the database
        List<Tk> tkList = tkRepository.findAll();
        assertThat(tkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTk() throws Exception {
        int databaseSizeBeforeUpdate = tkRepository.findAll().size();
        tk.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTkMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tk))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tk in the database
        List<Tk> tkList = tkRepository.findAll();
        assertThat(tkList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTk() throws Exception {
        // Initialize the database
        tkRepository.saveAndFlush(tk);

        int databaseSizeBeforeDelete = tkRepository.findAll().size();

        // Delete the tk
        restTkMockMvc
            .perform(delete(ENTITY_API_URL_ID, tk.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Tk> tkList = tkRepository.findAll();
        assertThat(tkList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
