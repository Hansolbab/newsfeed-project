package com.example.newsfeedproject.users.repository;

import com.example.newsfeedproject.users.entity.AccessAble;
import com.example.newsfeedproject.users.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
    boolean existsByUserName(String userName);

    boolean existsByUserIdAndVisibility(Long userId, AccessAble visibility);

    @Query("SELECT u FROM Users u " +
            "WHERE u.userName LIKE %:username% " +
            "AND (u.deleted = false or u.deleted IS NULL) AND u.visibility <> 'NONE_ACCESS' ")
    Page<Users> findByUserNameContainingAndDeletedFalseAndNOTNoneAccess(@Param("username") String username,
                                                                        Pageable pageable);




}
