package com.example.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backend.models.Museum;
import com.example.backend.repositories.MuseumRepository;

import java.util.*;

@RestController
@RequestMapping("/api")
public class MuseumController {

    private final MuseumRepository museumRepository;

    public MuseumController(MuseumRepository museumRepository) {
        this.museumRepository = museumRepository;
    }

    @GetMapping("/museums")
    public List<Museum> getAllMuseums() {
        return (List<Museum>) museumRepository.findAll();
    }

    @PostMapping("/museums")
    public ResponseEntity<Object> createMuseum(@RequestBody Museum museum) {

        // Проверка на пустое значение поля name  в запросе
        if (Objects.equals(museum.getName(), "")) {
            return new ResponseEntity<>("name must be not null", HttpStatus.BAD_REQUEST);
        }

        // Проверяем, существует ли уже пользователь с таким именем
        if (museumRepository.findByName(museum.getName()).isPresent()) {
            return new ResponseEntity<>("this name already exists", HttpStatus.BAD_REQUEST);
        }

        Museum mm = museumRepository.save(museum);
        return new ResponseEntity<>(mm, HttpStatus.OK);
    }
}