package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.Tk;
import com.mycompany.myapp.repository.TkRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Tk}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TkResource {

    private final Logger log = LoggerFactory.getLogger(TkResource.class);

    private static final String ENTITY_NAME = "tkTk";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TkRepository tkRepository;

    public TkResource(TkRepository tkRepository) {
        this.tkRepository = tkRepository;
    }

    /**
     * {@code POST  /tks} : Create a new tk.
     *
     * @param tk the tk to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tk, or with status {@code 400 (Bad Request)} if the tk has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tks")
    public ResponseEntity<Tk> createTk(@RequestBody Tk tk) throws URISyntaxException {
        log.debug("REST request to save Tk : {}", tk);
        if (tk.getId() != null) {
            throw new BadRequestAlertException("A new tk cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Tk result = tkRepository.save(tk);
        return ResponseEntity
            .created(new URI("/api/tks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /tks/:id} : Updates an existing tk.
     *
     * @param id the id of the tk to save.
     * @param tk the tk to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tk,
     * or with status {@code 400 (Bad Request)} if the tk is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tk couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tks/{id}")
    public ResponseEntity<Tk> updateTk(@PathVariable(value = "id", required = false) final Long id, @RequestBody Tk tk)
        throws URISyntaxException {
        log.debug("REST request to update Tk : {}, {}", id, tk);
        if (tk.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tk.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Tk result = tkRepository.save(tk);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tk.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /tks/:id} : Partial updates given fields of an existing tk, field will ignore if it is null
     *
     * @param id the id of the tk to save.
     * @param tk the tk to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tk,
     * or with status {@code 400 (Bad Request)} if the tk is not valid,
     * or with status {@code 404 (Not Found)} if the tk is not found,
     * or with status {@code 500 (Internal Server Error)} if the tk couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tks/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Tk> partialUpdateTk(@PathVariable(value = "id", required = false) final Long id, @RequestBody Tk tk)
        throws URISyntaxException {
        log.debug("REST request to partial update Tk partially : {}, {}", id, tk);
        if (tk.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tk.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tkRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Tk> result = tkRepository
            .findById(tk.getId())
            .map(existingTk -> {
                if (tk.getField1() != null) {
                    existingTk.setField1(tk.getField1());
                }
                if (tk.getField2() != null) {
                    existingTk.setField2(tk.getField2());
                }

                return existingTk;
            })
            .map(tkRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tk.getId().toString())
        );
    }

    /**
     * {@code GET  /tks} : get all the tks.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tks in body.
     */
    @GetMapping("/tks")
    public List<Tk> getAllTks() {
        log.debug("REST request to get all Tks");
        return tkRepository.findAll();
    }

    /**
     * {@code GET  /tks/:id} : get the "id" tk.
     *
     * @param id the id of the tk to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tk, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tks/{id}")
    public ResponseEntity<Tk> getTk(@PathVariable Long id) {
        log.debug("REST request to get Tk : {}", id);
        Optional<Tk> tk = tkRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(tk);
    }

    /**
     * {@code DELETE  /tks/:id} : delete the "id" tk.
     *
     * @param id the id of the tk to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tks/{id}")
    public ResponseEntity<Void> deleteTk(@PathVariable Long id) {
        log.debug("REST request to delete Tk : {}", id);
        tkRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
