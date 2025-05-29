package org.dariaob.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.dariaob.security.users.UsersDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс, предоставляющий инструменты валидации и генерации JWT - токенов
 */
@Service
public class JwtService {

    /**
     * Экземпляр генерируемого при запуске приложения ключа шифрования токенов
     */
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    /**
     * Метод генрации JWT - токена
     *
     * @param details информация о пользователе {username, password, roles}
     * @return токен в строчном виде
     */
    public String generateToken(UserDetails details) {
        Map<String, Object> claims = new HashMap<>();
        if (details instanceof UsersDetails customUserDetails) {
            claims.put("username", customUserDetails.getUsername());

            List<String> roles = customUserDetails.getAuthorities().stream()
                    .map(grantedAuthority -> {
                        String role = grantedAuthority.getAuthority();
                        // Убираем префикс "ROLE_" (если есть)
                        return role.startsWith("ROLE_") ? role.substring(5) : role;
                    })
                    .toList();

            claims.put("roles", roles);
        }
        return generate(claims, details);
    }

    /**
     * Метод выделения имени пользователя из токена
     *
     * @param token полученный JWT - токен
     * @return имя пользователя, владеющего токеном
     * @throws SignatureException ошибка парсинга токена / неверны формат
     */
    public String extractUsername(String token) throws SignatureException {
        return Jwts.parser().setSigningKey(SECRET_KEY).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Метод выделения даты истечения срока валидность токена
     *
     * @param token полученный JWT - токен
     * @return время, до которого токен валиден
     */
    public Date extractExpiration(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).build()
                .parseClaimsJws(token).getBody().getExpiration();
    }

    /**
     * Метод проверки корректности данных о пользователе, что предоставляются в токене
     *
     * @param token   полученный JWT - токен
     * @param details информация о пользователе {username, password, roles}
     * @return вердикт валидности токена
     * @throws SignatureException ошибка парсинга токена / неверны формат
     */
    public boolean isTokenValid(String token, UserDetails details) throws SignatureException {
        String username = extractUsername(token);
        boolean isTokenExpired = extractExpiration(token).before(new Date());

        return (username.equals(details.getUsername())) && !isTokenExpired;
    }

    /**
     * Метод генерации нового токена
     * @param claims
     * @param details информация о пользователе {username, password, roles}
     * @return токен в строчном виде
     */
    private String generate(Map<String, Object> claims, UserDetails details) {
        return Jwts.builder().setClaims(claims).setSubject(details.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 100000 * 60 * 24))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS512).compact();
    }
}