package com.chertiavdev.bookingapp.repository.user.telegram;

import com.chertiavdev.bookingapp.model.Role.RoleName;
import com.chertiavdev.bookingapp.model.UserTelegram;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserTelegramRepository extends JpaRepository<UserTelegram, Long> {
    @EntityGraph(attributePaths = {"user"})
    Optional<UserTelegram> findByUserId(Long userId);

    @Modifying
    @Query("UPDATE UserTelegram ut SET ut.isDeleted = false, ut.chatId = :chatId WHERE ut.id = :id")
    void restoreUserTelegram(@Param("id") Long id, @Param("chatId") Long chatId);

    boolean existsByUserId(Long userId);

    void deleteByChatId(Long chatId);

    void deleteByUserId(Long userId);

    @Query("SELECT ut FROM UserTelegram ut "
            + "JOIN FETCH ut.user u "
            + "JOIN FETCH ut.user.roles ur "
            + "WHERE ur.name = :roleName AND ut.isDeleted = false")
    List<UserTelegram> findAllByUserRoles(RoleName roleName);
}
