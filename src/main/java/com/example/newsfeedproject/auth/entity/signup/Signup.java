package com.example.newsfeedproject.auth.entity.signup;


import com.example.newsfeedproject.auth.dto.signup.SignupRequestDto;
import com.example.newsfeedproject.users.entity.Users;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.catalina.User;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Signup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true, nullable = false, length = 10)
    private String userName;

    @Column(nullable = false)
    private String password;


    @Column( nullable = false,length = 20)
    private String phoneNumber;

     @Column(nullable = false, length = 225)
    private String email;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime created_at;

    @LastModifiedDate
    private LocalDateTime updated_at;

    public static Users of(SignupRequestDto dto, String encodedPwd){
        return new Users();
    }

}
