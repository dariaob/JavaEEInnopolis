package org.dariaob.security.users;

import lombok.RequiredArgsConstructor;
import org.dariaob.models.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Класс, реализующий UserDetails, содержит данные о пользователе,
 * такие как имя пользователя, пароль и роли (уровни доступа).
 */
@RequiredArgsConstructor
public class UsersDetails implements UserDetails {

    private final Users user;

    /**
     * Получить список ролей пользователя в виде GrantedAuthority.
     *
     * @return коллекция прав доступа пользователя
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    /**
     * Получить пароль пользователя.
     *
     * @return пароль
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Получить имя пользователя.
     *
     * @return имя пользователя (логин)
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Проверка, не истёк ли срок действия аккаунта.
     *
     * @return всегда true (срок действия аккаунта не истёк)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Проверка, не заблокирован ли аккаунт.
     *
     * @return всегда true (аккаунт не заблокирован)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Проверка, не истёк ли срок действия учётных данных.
     *
     * @return всегда true (учётные данные не просрочены)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Проверка, активен ли пользователь.
     *
     * @return true, если пользователь не удалён (активен)
     */
    @Override
    public boolean isEnabled() {
        return !user.isDeleted();
    }
}
