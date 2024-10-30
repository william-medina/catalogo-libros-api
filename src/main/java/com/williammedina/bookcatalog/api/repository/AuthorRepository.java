package com.williammedina.bookcatalog.api.repository;

import com.williammedina.bookcatalog.api.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    Optional<Author> findByNameContainsIgnoreCase(String name);
    Author findByBirthYear(Integer birthYear);
    List<Author> findAllByOrderByNameAsc();

    @Query("SELECT a FROM Author a WHERE a.birthYear <= :year AND (a.deathYear IS NULL OR a.deathYear >= :year) ORDER BY a.name ASC")
    List<Author> findActiveAuthorsInYear(Integer year);

}
