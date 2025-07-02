package org.dariaob.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.dariaob.security.users.UsersDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT-фильтр, перехватывающий каждый запрос для извлечения и проверки токена.
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Метод фильтрации каждого входящего запроса
     *
     * @param request     the request
     * @param response    the response
     * @param filterChain the filter chain
     * @throws ServletException the servlet exception
     * @throws IOException      the io exception
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        logger.info("Заголовок Authorization: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        final String username;

        logger.info("JWT токен: " + token);

        try {
            username = jwtService.extractUsername(token);
            logger.info("Извлечённый username: " + username);
        } catch (Exception e) {
            logger.error("Ошибка парсинга токена: ", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Невалидный JWT токен");
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsersDetails userDetails = (UsersDetails) userDetailsService.loadUserByUsername(username);

            if (jwtService.isTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("Аутентификация успешна для пользователя: " + username);
            } else {
                logger.warn("Токен невалиден для пользователя: " + username);
            }
        }

        filterChain.doFilter(request, response);
    }
}
