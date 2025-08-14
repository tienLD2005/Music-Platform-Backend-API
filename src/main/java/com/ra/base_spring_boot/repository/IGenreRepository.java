package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IGenreRepository extends JpaRepository<Genre, Long>{
    Page<Genre> findAll(Pageable pageable);
}
