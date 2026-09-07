package edu.prog2.ejsecurity.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class Mensaje {

    @GetMapping("/auth")
    public ResponseEntity<String> MensajeBienvenida() {
        return  ResponseEntity.ok("Bienvenido al mensaje");
    }

    @GetMapping("/admin/mensaje")
    public ResponseEntity<String> MensajeBienvenidaAdmin() {
        return  ResponseEntity.ok("Bienvenido al mensaje Admin");
    }
}
