package org.dariaob.security.users;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.dariaob.exceptions.DataNotFoundException;
import org.dariaob.models.Users;
import org.dariaob.repositories.UsersRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с пользователями.
 */
@Service
@RequiredArgsConstructor
public class UsersDetailsServiceImpl implements UserDetailsService {

    private final UsersRepository usersRepository;

    /**
     * Найти активного пользователя по имени.
     *
     * @param username имя пользователя
     * @return найденный пользователь
     * @throws DataNotFoundException если пользователь не найден или помечен как удалённый
     */
    public Users getActiveByUsername(String username) {
        return usersRepository.findActiveByUsername(username)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с именем " + username + " не найден или удалён."));
    }

    /**
     * Мягко удалить пользователя (установить флаг isDeleted = true).
     *
     * @param username имя пользователя
     * @throws DataNotFoundException если пользователь не найден или уже удалён
     */
    @Transactional
    public void softDelete(String username) {
        if (usersRepository.findActiveByUsername(username).isEmpty()) {
            throw new DataNotFoundException("Пользователь с именем " + username + " не найден или уже удалён.");
        }
        usersRepository.softDelete(username);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersRepository.findActiveByUsername(username)
                .orElseThrow(() -> new DataNotFoundException("Пользователь не найден: " + username));
        return new UsersDetails(user);
    }
}
