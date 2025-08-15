package com.example.newsfeedproject.follow.service;


import com.example.newsfeedproject.auth.impl.UserDetailsImpl;

import com.example.newsfeedproject.common.exception.follow.FollowErrorException;
import com.example.newsfeedproject.common.exception.users.UsersErrorCode;
import com.example.newsfeedproject.common.exception.users.UsersErrorException;
import com.example.newsfeedproject.follow.dto.FollowResponseDto;
import com.example.newsfeedproject.follow.dto.ReadFollowUsersDto;
import com.example.newsfeedproject.follow.entity.Follows;
import com.example.newsfeedproject.follow.repository.FollowsRepository;
import com.example.newsfeedproject.requestfollow.dto.RequestFollowResponseDto;
import com.example.newsfeedproject.requestfollow.entity.RequestFollows;
import com.example.newsfeedproject.requestfollow.repository.RequestFollowRepository;
import com.example.newsfeedproject.users.entity.AccessAble;
import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import jakarta.transaction.Transactional;;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import static com.example.newsfeedproject.common.exception.follow.FollowErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowsService {

    private final FollowsRepository followsRepository;
    private final UsersRepository usersRepository;
    private final RequestFollowRepository requestFollowRepository;

    // 팔로우 언팔 토글 되는거 같기도?

//    public FollowResponseDto follow(UserDetailsImpl userDetails, Long userId) {
//
//        Long meId = userDetails.getUserId();
//        validId(meId, userId);
//
//        Follows relation = getRelation(meId, userId); // getRelation => 없으면 관계 생성
//        if (usersRepository.existsByUserIdAndVisibility(meId, AccessAble.NONE_ACCESS)){throw new UsersErrorException(UsersErrorCode.NO_SUCH_USER);}
//        visibility = All_ACCESS
//        if(usersRepository.existsByUserIdAndVisibility(meId, visibility)){
//            if(relation.isFollowed()){
//                relation.unfollow();
//            } else {
//                relation.follow();
//            }
//        } else{
//            if(relation.isFollowed()) {
//
//            }
//            else{
//                RequestFollowResponseDto followStatus = new RequestFollowResponseDto(requestFollowService.requestFollow(userDetails, userId));
//            }
//        }
//
//        followsRepository.save(relation);
//        return new FollowResponseDto(relation.isFollowed());
//    }

    public FollowResponseDto follow(UserDetailsImpl userDetails, Long userId) {

        Long meId = userDetails.getUserId();

        validId(meId, userId);

        Follows relation = getRelation(meId, userId); // getRelation => 없으면 관계 생성

        if(relation.isFollowed()){
            throw new FollowErrorException(ALREADY_FOLLOW);
        }

        relation.follow();
        followsRepository.save(relation);


        return new FollowResponseDto(relation.isFollowed());
    }



    //언팔로우 서비스 //내 팔로잉 목록에서 삭제 // 내가 팔로워를 안하겠다.
    public FollowResponseDto unfollow(UserDetailsImpl userDetails, Long userId) {

        Long meId = userDetails.getUserId();

        validId(meId, userId);

        Follows relation = readRelation(meId, userId); // 없으면 return 팔로우 관계 없음

        RequestFollows requestRelation =getRelationOrThrow(meId,userId);

        requestRelation.cancel();
        relation.unfollow();

        return new FollowResponseDto(relation.isFollowed());
    }

    //팔로우 삭제 // 내 팔로워 목록에서 삭제 // 너는 내 팔로워가 아니다.
    public FollowResponseDto deleteFollow(UserDetailsImpl userDetails, Long userId) {

        Long meId = userDetails.getUserId();

        validId(meId, userId);


        Follows relation = readRelation(userId,meId);

        RequestFollows requestRelation = getRelationOrThrow(userId,meId);

        requestRelation.cancel();
        relation.unfollow();

        relation.unfollow();

        return new FollowResponseDto(relation.isFollowed());
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
                        ReadFollowUsersDto.toDto(follows.getFollower(),
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

        Page<Follows> followeePage = followsRepository.findByFollower(follower, pageable);

        Set<Long> myFollowerSet = followsRepository.findFolloweeIdsOf(meId);

        List<ReadFollowUsersDto> followeeList = followeePage.getContent().stream()
                .map(follows-> ReadFollowUsersDto.toDto(follows.getFollowee(),
                        myFollowerSet.contains(follows.getFollowee().getUserId())))
                .toList();

        return new PageImpl<>( followeeList , pageable, followeePage.getTotalElements());

    }

    //메서드 정리
    private void validId( Long meId, Long userId) {


        if (meId.equals(userId)) {
            throw new FollowErrorException(SELF_FOLLOW_NOT);
        }

        if (!usersRepository.existsById(userId)) {
            throw new FollowErrorException(USER_NOT_FOUND);
        }
    }

    //팔로우 이력 확인 메서드 -> 없으면 Exception 반환
    public Follows readRelation(Long followerId , Long followeeId){

        Users follower = usersRepository.getReferenceById(followerId);

        Users followee = usersRepository.getReferenceById(followeeId);

        return followsRepository
                .findByFollowerAndFollowee(follower, followee)
                .orElseThrow( () -> new FollowErrorException(RELATION_NOT_FOUND));
    }


    //팔로우 이력 확인 메서드 -> 없으면 생성
    private Follows getRelation(Long followerId, Long followeeId) {

        Users follower = usersRepository.getReferenceById(followerId);

        Users followee = usersRepository.getReferenceById(followeeId);

        return  followsRepository
                .findByFollowerAndFollowee(follower, followee)
                .orElseGet(() -> new Follows(follower, followee));
    }


    //팔로우 요청 확인 메서드
    private RequestFollows getRelationOrThrow(Long requesterId, Long targetId) {

        Users requester =  usersRepository.getReferenceById(requesterId);

        Users target = usersRepository.getReferenceById(targetId);

        return requestFollowRepository.findByRequesterAndTarget(requester, target) // follower = requester, followee = me
                .orElseThrow(() -> new FollowErrorException(NOT_REQUEST));
    }




}
