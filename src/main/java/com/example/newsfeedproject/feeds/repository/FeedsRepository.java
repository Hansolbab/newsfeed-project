package com.example.newsfeedproject.feeds.repository;

import com.example.newsfeedproject.users.entity.Users;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedsRepository extends JpaRepository<Users, Long> {
    @Transactional
    void deleteByUserName(String userName);
}
