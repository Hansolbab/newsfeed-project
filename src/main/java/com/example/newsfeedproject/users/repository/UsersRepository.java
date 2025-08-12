package com.example.newsfeedproject.users.repository;

import com.example.newsfeedproject.users.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
    boolean existsByUserName(String userName);
    boolean existsByPhoneNumber(String phoneNumber);

    Page<Users> findByUserNameContaining(String username, Pageable pageable);
}
