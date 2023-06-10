package com.example.backend.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.backend.models.Museum;
import com.example.backend.models.User;
import com.example.backend.repositories.MuseumRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.tools.DataValidationException;

import java.util.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    MuseumRepository museumRepository;

    @GetMapping("/users")
    public List  getAllusers() {
        return userRepository.findAll();
    }

    @PostMapping("/users")
    public ResponseEntity<Object> createUser(@RequestBody User user)
            throws Exception {
        try {
            User nc = userRepository.save(user);
            return new ResponseEntity<Object>(nc, HttpStatus.OK);
        }
        catch(Exception ex) {
            String error;
            if (ex.getMessage().contains("users.name_UNIQUE"))
                error = "useralreadyexists";
            else
                error = "undefinederror";
            Map<String, String>
                    map =  new HashMap<>();
            map.put("error", error);
            return ResponseEntity.ok(map);
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable(value = "id") Long userId) {
        Optional<User>
                user = userRepository.findById(userId);
        Map<String, Boolean>
                resp = new HashMap<>();
        if (user.isPresent()) {
            userRepository.delete(user.get());
            resp.put("deleted", Boolean.TRUE);
        }
        else
            resp.put("deleted", Boolean.FALSE);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/users/{id}/museums")
    public ResponseEntity<Set<Museum>> getUserUsers(@PathVariable(value = "id") Long userId) {
        Optional<User> cc = userRepository.findById(userId);
        if (cc.isPresent()) {
            return ResponseEntity.ok(cc.get().museums);
        }
        return ResponseEntity.ok(new HashSet<Museum>() {
        });
    }

    @PostMapping("/users/{id}/addmuseums")
    public ResponseEntity<Object> addMuseums(@PathVariable(value = "id") Long userId,
                                             @Valid @RequestBody Set<Museum> museums) {
        Optional<User> uu = userRepository.findById(userId);
        int cnt = 0;
        if (uu.isPresent()) {
            User u = uu.get();
            for (Museum m : museums) {
                Optional<Museum> mm = museumRepository.findById(m.id);
                if (mm.isPresent()) {
                    u.addMuseum(mm.get());
                    cnt++;
                }
            }
            userRepository.save(u);
        }
        Map<String, String> response = new HashMap<>();
        response.put("count", String.valueOf(cnt));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{id}/removemuseums")
    public ResponseEntity<Object> removeMuseums(@PathVariable(value = "id") Long userId,
                                                @Valid @RequestBody Set<Museum> museums) {
        Optional<User> uu = userRepository.findById(userId);
        int cnt = 0;
        if (uu.isPresent()) {
            User u = uu.get();
            for (Museum m : u.museums) {
                u.removeMuseum(m);
                cnt++;
            }
            userRepository.save(u);
        }
        Map<String, String> response = new HashMap<>();
        response.put("count", String.valueOf(cnt));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity updateUser(@PathVariable(value = "id") Long userId,
                                     @Valid @RequestBody User userDetails)
            throws DataValidationException
    {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new DataValidationException(" Пользователь с таким индексом не найден"));
            user.email = userDetails.email;
            String np = userDetails.np;
            if (np != null  && !np.isEmpty()) {
                user.password = new BCryptPasswordEncoder().encode(np);
            }
            userRepository.save(user);
            return ResponseEntity.ok(user);
        }
        catch (Exception ex) {
            String error;
            if (ex.getMessage().contains("users.email_UNIQUE"))
                throw new DataValidationException("Пользователь с такой почтой уже есть в базе");
            else
                throw new DataValidationException("Неизвестная ошибка");
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity getUser(@PathVariable(value = "id") Long userId)
            throws DataValidationException {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new DataValidationException("Пользователь с таким индексом не найден"));
        return ResponseEntity.ok(user);
    }
}