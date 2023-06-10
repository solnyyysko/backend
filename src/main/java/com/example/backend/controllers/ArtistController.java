package com.example.backend.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.example.models.Artist;
import com.example.models.Country;
import com.example.repositories.ArtistRepository;
import com.example.repositories.CountryRepository;
import com.example.tools.DataValidationException;


import java.util.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1")
public class ArtistController {
    @Autowired
    ArtistRepository artistRepository;
    @Autowired
    CountryRepository countryRepository;


    @GetMapping("/artists")
    public List
    getAllartists() {
        return artistRepository.findAll();
    }

    @PostMapping("/artists")
    public ResponseEntity<Object> createArtist(@RequestBody Artist artist)
            throws Exception {
        try {
            Optional<Country>
                    cc = countryRepository.findById(artist.country.id);
            if (cc.isPresent()) {
                artist.country = cc.get();
            }
            Artist nc = artistRepository.save(artist);
            return new ResponseEntity<Object>(nc, HttpStatus.OK);
        }
        catch(Exception ex) {
            String error;
            if (ex.getMessage().contains("artists.name_UNIQUE"))
                error = "artistalreadyexists";
            else
                error = "undefinederror";
            Map<String, String>
                    map =  new HashMap<>();
            map.put("error", error);
            return ResponseEntity.ok(map);
        }
    }

    @PutMapping("/artists/{id}")
    public ResponseEntity<Artist> updateArtist(@PathVariable(value = "id") Long ArtistId,
                                               @RequestBody Artist ArtistDetails) {
        Artist Artist = null;
        Optional<Artist>
                cc = artistRepository.findById(ArtistId);
        if (cc.isPresent()) {
            Artist = cc.get();
            Artist.name = ArtistDetails.name;
            Artist.century = ArtistDetails.century;
            Artist.country = ArtistDetails.country;

            artistRepository.save(Artist);
            return ResponseEntity.ok(Artist);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artist not found");
        }
    }

    @PostMapping("/deleteartists")
    public ResponseEntity deleteArtists(@Valid @RequestBody List<Artist> artists) {
        artistRepository.deleteAll(artists);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/artists/{id}")
    public ResponseEntity getArtist(@PathVariable(value = "id") Long artistId)
            throws DataValidationException {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(()->new DataValidationException("Художник с таким индексом не найден"));
        return ResponseEntity.ok(artist);
    }

}