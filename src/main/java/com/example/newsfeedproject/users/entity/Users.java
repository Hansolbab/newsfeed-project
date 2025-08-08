package com.example.newsfeedproject.users.entity;

import com.example.newsfeedproject.follow.entity.Follows;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table
//@Table(name="users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
//    @Column(nullable = false, name="userId")
    // User Id
    private Long userId;

    @NotBlank
    @Column(nullable = false)
//    @Column(nullable = false, name="userName")
    // User 이름
    private String userName;

    @NotBlank
    @Column(nullable = false)
//    @Column(nullable = false, name="phoneNumber")
    // 전화번호
    private String phoneNumber;

    @NotBlank
    @Column(nullable = false, unique = true)
//    @Column(unique = true, nullable = false, name="email")
    // Email 주소
    private String email;

    @NotBlank
    @Column(nullable = false)
//    @Column(nullable = false, name="password")
    // 비밀번호
    private String password;

    @Column
//    @Column(name="isDeleted")
    // 소프트 삭제 여부 확인
    private Boolean isDeleted;

    @Column
//    @Column(name="created_at")
    @CreatedDate
    // 생성일
    private LocalDateTime created_at;

    @Column
//    @Column(name="updateed_at")
    @LastModifiedDate
    // 수정일
    private LocalDateTime updated_at;

    @Column
//    @Column(nullable = false, name="profileImg")
    // 외부 이미지 가져오기, 랜덤으로 이미지 제공해주는 사이트입니다.
    private String profileImg = "https://via.placeholder.com/150";

//  followings
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follows> followings = new ArrayList<>();
//   followers

    @OneToMany(mappedBy = "followee", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Follows> followers = new ArrayList<>();

    public Users ( String userName, String phoneNumber, String email,String encode){
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = encode;
    }

}
