package com.example.newsfeedproject.users.entity;

import com.example.newsfeedproject.follow.entity.Follows;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long userId;    // User Id

    @NotBlank
    @Column(nullable = false)
    private String userName;   // User 이름

    @NotBlank
    @Column(nullable = false)
    private String phoneNumber; // 전화번호

    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;  // Email 주소

    @NotBlank
    @Column(nullable = false)
    private String password;  // 비밀번호

    @Builder.Default//초기값 유지용
    @Column(name="deleted")
    private Boolean deleted= false;// 소프트 삭제 여부 확인

    @Column
    @CreatedDate
    private LocalDateTime createdAt;    // 생성일

    @Column
    @LastModifiedDate
    private LocalDateTime updatedAt;    // 수정일

    @Column(nullable = false, name="profileImageUrl", length = 255)
    @Builder.Default//초기값 유지용
    private String profileImageUrl = "https://via.placeholder.com/150";   // 외부 이미지 가져오기, 랜덤으로 이미지 제공해주는 사이트입니다.

//  followings
    @Builder.Default//초기값 유지용
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follows> followingList = new ArrayList<>();

//   followers
    @Builder.Default//초기값 유지용
    @OneToMany(mappedBy = "followee", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Follows> followerList = new ArrayList<>();

    public Users ( String userName, String phoneNumber, String email,String encode){
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = encode;
        this.profileImageUrl = "https://via.placeholder.com/150";
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPassword(String encode) {
        this.password = encode;
    }

    //회원탈퇴 처리 메서드
    public void softDelete(String scrambledPassword){
        this.password = scrambledPassword;//재로그인 원천 차단
        this.deleted = true;
    }

    //유저프로필 처리 메서드(수정/삭제)
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
