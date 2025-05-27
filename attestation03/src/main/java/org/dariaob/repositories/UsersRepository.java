package org.dariaob.repositories;

import jakarta.transaction.Transactional;
import org.dariaob.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с пользователями.
 */
@Repository
public interface UsersRepository extends JpaRepository<Users, String> {
    Optional<Users> findActiveByUsername(String username);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
            "FROM Users u WHERE u.username = ?1 AND u.isDeleted = false")
    boolean existsActiveByUsername(String username);

    @Modifying
    @Query("UPDATE Users u SET u.isDeleted = true WHERE u.username = ?1")
    void softDelete(String username);

    @Modifying
    @Query("UPDATE Users u SET u.isDeleted = false WHERE u.username = ?1")
    void restore(String username);
}
