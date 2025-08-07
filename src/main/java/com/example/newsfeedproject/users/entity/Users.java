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
    @Column
    private String userName;

    @NotBlank
    @Column
    private String phoneNumber;

    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    @Column
    private String password;

    @Column
    private Boolean isDeleted;

    @Column
    @CreatedDate
    private LocalDateTime created_at;

    @Column
    @LastModifiedDate
    private LocalDateTime updated_at;

//    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn(name = "profileImgId")
//    private ProfileImg profileImg;

//      followings
//      followers

}
