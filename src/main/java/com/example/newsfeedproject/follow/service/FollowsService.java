package com.example.newsfeedproject.follow.service;


import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.common.dto.ReadFollowUsersDto;
import com.example.newsfeedproject.common.exception.FollowErrorCode;
import com.example.newsfeedproject.common.exception.FollowErrorException;
import com.example.newsfeedproject.follow.dto.FollowResponseDto;
import com.example.newsfeedproject.follow.entity.FollowStatus;
import com.example.newsfeedproject.follow.entity.Follows;
import com.example.newsfeedproject.follow.repository.FollowsRepository;
import com.example.newsfeedproject.requestfollow.entity.RequestFollows;
import com.example.newsfeedproject.requestfollow.repository.RequestFollowRepository;
import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import static com.example.newsfeedproject.common.exception.FollowErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowsService {

    private final FollowsRepository followsRepository;
    private final UsersRepository usersRepository;
    private final RequestFollowRepository requestFollowRepository;

    public FollowResponseDto follow(UserDetailsImpl userDetails, Long userId) {

        Follows relation = getRelation(userDetails, userId);

        if(relation.isFollowed()){
            throw new FollowErrorException(ALREADY_FOLLOW);
        }

        relation.follow();
        followsRepository.save(relation);


        return new FollowResponseDto(relation.isFollowed());
    }



    //언팔로우 서비스 //내 팔로잉 목록에서 삭제
    public FollowResponseDto unfollow(UserDetailsImpl userDetails, Long userId) {

        Long meId = userDetails.getUserId();

        validId(meId, userId);

        Users me = usersRepository.getReferenceById(meId); //  Id 값만 저장한 프록시 객체

        Users followee = usersRepository.getReferenceById(userId);

        Follows relation = readRelation(me, followee);

        RequestFollows requestRelation =getRelationOrThrow(userDetails,userId);

        requestRelation.cancel();
        relation.unfollow();

        return new FollowResponseDto(relation.isFollowed());
    }

    //팔로우 삭제 // 내 팔로워 목록에서 삭제
    public FollowResponseDto deleteFollow(UserDetailsImpl userDetails, Long userId) {

        Long meId = userDetails.getUserId();

        validId(meId, userId);

        Users me = usersRepository.getReferenceById(meId);

        Users followerMe = usersRepository.getReferenceById(userId);

        Follows relation = readRelation(followerMe, me);

        RequestFollows requestRelation = getRelationOrThrow(userDetails,userId);

        requestRelation.cancel();
        relation.unfollow();


        relation.unfollow();

        return new FollowResponseDto(relation.isFollowed());
    }


    //팔로우 이력 확인 메서드
    public Follows readRelation(Users follower , Users followee){

        return followsRepository
                .findByFollowerAndFollowee(follower, followee)
                .orElseThrow( () -> new FollowErrorException(RELATION_NOT_FOUND));
    }


    //이 사람을 팔로우 하는 사람
    public Page<ReadFollowUsersDto> readFollowerList(Long meId, Long userId , Pageable pageable) {

        if (!usersRepository.existsById(userId)) {
            throw new FollowErrorException(USER_NOT_FOUND);
        }
        //이 사람의 팔로워 목록은 이 사람 기준 팔로우 당하는 것입니다.
        Users followee = usersRepository.getReferenceById(userId);

        Page<Follows> followerPage = followsRepository.findByFollowee(followee, pageable);//당하는 사람으로 하는 사람들을 찾아온다.

        //meId 내가 팔로우 하는 사람들 목록
        Set<Long> myFollowerSet = followsRepository.findFolloweeIdsOf(meId);

        List<ReadFollowUsersDto> followerList = followerPage.getContent().stream()//페이지에 해당하는 내용 즉 Followers
                .map(follows -> // follows 엔티티를 ReadFollowUsersDto로 매핑
                        ReadFollowUsersDto.todto(follows.getFollower(),
                        myFollowerSet.contains(follows.getFollower().getUserId())))
                .toList(); // 리스트 반환

        return new PageImpl<>(
                followerList, // 목록
                pageable,
                followerPage.getTotalElements());
    }

    //이 사람이 팔로우 하는 사람
    public Page<ReadFollowUsersDto> readFolloweeList(Long meId, Long userId ,Pageable pageable) {

        if (!usersRepository.existsById(userId)) {
            throw new FollowErrorException(USER_NOT_FOUND);
        }

        Users follower = usersRepository.getReferenceById(userId);

        Page<Follows> followeePage = followsRepository.findByFollower( follower, pageable);

        Set<Long> myFollowerSet = followsRepository.findFolloweeIdsOf(meId);

        List<ReadFollowUsersDto> followeeList = followeePage.getContent().stream()
                .map(follows-> ReadFollowUsersDto.todto(follows.getFollowee(),
                        myFollowerSet.contains(follows.getFollowee().getUserId())))
                .toList();

        return new PageImpl<>( followeeList , pageable, followeePage.getTotalElements());

    }

    //메서드 정리
    private void validId(Long meId, Long userId) {

        //자기 자신 관련 팔로우/언팔로우
        if (meId.equals(userId)) {
            throw new FollowErrorException(SELF_FOLLOW_NOT);
        }

        if (!usersRepository.existsById(userId)) {
            throw new FollowErrorException(USER_NOT_FOUND);
        }
    }


    private Follows getRelation(UserDetailsImpl userDetails, Long userId) {

        Long meId = userDetails.getUserId();

        if (meId.equals(userId)) {
            throw new FollowErrorException(SELF_FOLLOW_NOT);
        }

        if (!usersRepository.existsById(userId)) {
            throw new FollowErrorException(USER_NOT_FOUND);
        }

        Users me = usersRepository.getReferenceById(meId);

        Users followee = usersRepository.getReferenceById(userId);

        return  followsRepository
                .findByFollowerAndFollowee(me, followee)
                .orElseGet(() -> new Follows(me, followee));
    }


    private RequestFollows getRelationOrThrow(UserDetailsImpl userDetails, Long userId) {
        Long meId =userDetails.getUserId();

        if (meId.equals(userId)) {
            throw new FollowErrorException(SELF_FOLLOW_NOT);
        }

        if (!usersRepository.existsById(userId)) {
            throw new FollowErrorException(USER_NOT_FOUND);
        }

        Users me =  usersRepository.getReferenceById(meId);

        Users requester = usersRepository.getReferenceById(userId);

        return requestFollowRepository.findByRequesterAndTarget(requester, me)
                .orElseThrow(() -> new FollowErrorException(NOT_REQUEST));
    }


}
