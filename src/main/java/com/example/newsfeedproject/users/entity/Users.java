package com.example.newsfeedproject.users.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank
    @Column(nullable = false)
    private String userName;

    @NotBlank
    @Column(nullable = false)
    private String phoneNumber;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Column
    private Boolean isDeleted;

    @Column
    @CreatedDate
    private LocalDateTime created_at;

    @Column
    @LastModifiedDate
    private LocalDateTime updated_at;

    @Column(nullable = false)
    // 외부 이미지 가져오기, 랜덤으로 이미지 제공해주는 사이틍입니다.
    private String profileImg = "https://via.placeholder.com/150";

//      followings
//      followers

}
