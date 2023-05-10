package com.example.backend.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.example.backend.models.Museum;

import java.util.Optional;

public interface MuseumRepository extends CrudRepository<Museum, Long>{
    @Query("SELECT m FROM Museum m WHERE m.name = :name")
    public Optional<Museum> findByName(@Param("name") String name);

    @Query("SELECT m FROM Museum m WHERE m.id = :id")
    public Optional<Museum> findById(@Param("id") Long id);
}