package edu.prog2.ejsecurity.security.dto;

import lombok.Builder;

@Builder
public record AuthResponse (
        String token
){
}
