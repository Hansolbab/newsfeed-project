package com.example.newsfeedproject.follow.service;


import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.common.dto.ReadFollowUsersDto;
import com.example.newsfeedproject.common.exception.FollowErrorException;
import com.example.newsfeedproject.follow.entity.Follows;
import com.example.newsfeedproject.follow.repository.FollowsRepository;
import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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



    //팔로우 서비스
    public void follow(UserDetailsImpl userDetails, Long userId) {

        Long meId = userDetails.getUserId();

        validId(meId, userId);

        //팔로우 할 사람이 있는지
        Users me = usersRepository.getReferenceById(meId);

        //팔로우 당할 사람이 있는지
        Users followee = usersRepository.getReferenceById(userId);


        Follows relation = followsRepository
                .findByFollowerAndFollowee(me, followee) //할 사람이 당할 사람을 팔로우 했던 이력이 있는가.
                .orElseGet(() -> { // 없다면? => 처음 팔로우 하는 경우
                    Follows f = new Follows(); // 객체를 생성
                    f.setFollower(me); // 할 사람을 넣어주고
                    f.setFollowee(followee); // 당할 사람을 넣어주고
                    f.setFollowed(true); // 팔로우한다라고 반환
                    return f; // 값을 넣어주고 반환
                });

        if(!relation.isFollowed()) { //만약 했었는데 언팔로우 상태라면?
            relation.setFollowed(true); // 지금은 다시 팔로우라고 반환
        }

        //값을 반전처리!!!

        followsRepository.save(relation);

    }
    //언팔로우 서비스
    public void unfollow(UserDetailsImpl userDetails, Long userId) {

        Long meId = userDetails.getUserId();

        validId(meId, userId);

        Users me = usersRepository.getReferenceById(meId); //  Id 값만 저장한 프록시 객체

        Users followee = usersRepository.getReferenceById(userId);

        Follows relation = readRelation(me, followee);

        if(!relation.isFollowed()) return; // 이력이 있는데 언팔로우 상태이면 그대로 둠

        relation.setFollowed(false); // 이력이 있는데 팔로우 상태면 언팔로우 변경
    }

    //팔로우 삭제
    public void deleteFollow(UserDetailsImpl userDetails, Long userId) {

        Long meId = userDetails.getUserId();

        validId(meId, userId);

        Users me = usersRepository.getReferenceById(meId);

        Users followerMe = usersRepository.getReferenceById(userId);

        Follows relation = readRelation(followerMe, me);

        if(!relation.isFollowed()) return;

        relation.setFollowed(false);
    }


    //팔로우 이력 확인 메서드
    public Follows readRelation(Users follower , Users followee){

        return followsRepository
                .findByFollowerAndFollowee(follower, followee)
                .orElseThrow( () -> new FollowErrorException(RELATION_NOT_FOUND));
    }

    //팔로우 목록 조회


    //공통 검증
    private void validId(Long meId, Long userId) {

        //자기 자신 관련 팔로우/언팔로우
        if (meId.equals(userId)) {
            throw new FollowErrorException(SELF_FOLLOW_NOT);
        }

        if (!usersRepository.existsById(userId)) {
            throw new FollowErrorException(USER_NOT_FOUND);
        }
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


}
