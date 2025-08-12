package com.example.newsfeedproject.users.entity;

import com.example.newsfeedproject.follow.entity.Follows;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

//    @Column
    @Builder.Default//초기값 유지용
    @Column(name="deleted")
    // 소프트 삭제 여부 확인
    private Boolean deleted= false;

    @Column
//    @Column(name="createdAt")
    @CreatedDate
    // 생성일
    private LocalDateTime createdAt;

    @Column
//    @Column(name="updatedAt")
    @LastModifiedDate
    // 수정일
    private LocalDateTime updatedAt;

    @Column
//    @Column(nullable = false, name="profileImageUrl")
    // 외부 이미지 가져오기, 랜덤으로 이미지 제공해주는 사이트입니다.
    @Builder.Default//초기값 유지용
    private String profileImageUrl = "https://via.placeholder.com/150";

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
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPassword(String encode) {
        this.password = encode;
    }

    //회원탈퇴 처리 메서드
    public void softDelete(String scrambledPassword, LocalDateTime when){
        //재로그인 원천 차단
        this.password = scrambledPassword;
        this.deleted = true;
    }

    //유저프로필 처리 메서드(수정/삭제)
    //profileImageUrl-> 표준식
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
