package edu.prog2.ejsecurity.security.auth;

import edu.prog2.ejsecurity.dto.UsuarioDTO;
import edu.prog2.ejsecurity.enums.Role;
import edu.prog2.ejsecurity.models.Usuario;
import edu.prog2.ejsecurity.repositories.UsuarioRepository;
import edu.prog2.ejsecurity.security.dto.AuthRequest;
import edu.prog2.ejsecurity.security.dto.AuthResponse;
import edu.prog2.ejsecurity.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UsuarioRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthResponse register(UsuarioDTO request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El usuario ya existe: " + request.username());   // 409, no 500
        }

        // 1) Primero se crea y guarda el usuario
        Usuario user = Usuario.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        userRepository.save(user);
        // 2) DESPUÉS se arman los claims (ya existe el usuario con sus roles)
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", user.getAuthorities().iterator().next().getAuthority());

        // 3)generamos token con claims
        return new AuthResponse(jwtService.generateToken(claims, user));
    }


    // para loguearse
    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        Usuario user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuario no encontrado: " + request.username()));

        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", user.getAuthorities().iterator().next().getAuthority());

        return new AuthResponse(jwtService.generateToken(claims, user));
    }
}


