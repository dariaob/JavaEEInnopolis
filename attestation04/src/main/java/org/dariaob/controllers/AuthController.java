package org.dariaob.controllers;

import org.dariaob.dto.users.UserDto;
import org.dariaob.security.jwt.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dariaob.security.users.UsersDetailsServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для аутентификации пользователей
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "API для входа в систему и получения токена")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsersDetailsServiceImpl userService;
    private final JwtService jwtService;

    /**
     * Аутентификация пользователя и выдача JWT токена
     *
     * @param authDto DTO с учетными данными (username и password)
     * @return JWT токен или сообщение об ошибке
     */
    @Operation(summary = "Вход в систему", description = "Аутентифицирует пользователя и возвращает JWT токен")
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody UserDto authDto) {
        try {
            // Аутентификация пользователя
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authDto.getUsername(),
                            authDto.getPassword()
                    )
            );

            // Генерация токена
            var userDetails = userService.loadUserByUsername(authDto.getUsername());;
            var token = jwtService.generateToken((UserDetails) userDetails);

            return ResponseEntity.ok(token);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(401).body("Неверные учетные данные");
        }
    }
}