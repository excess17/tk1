package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Tk;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Tk entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TkRepository extends JpaRepository<Tk, Long> {}
