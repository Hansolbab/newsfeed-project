package com.example.newsfeedproject.follow.service;


import com.example.newsfeedproject.follow.entity.Follows;
import com.example.newsfeedproject.follow.repository.FollowsRepository;
import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowsService {

    private final FollowsRepository followsRepository;
    private final UsersRepository usersRepository;



    //팔로우 서비스
    public void follow(Long meId, Long userId) {

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

        if(!relation.isIsFollowed()) { //만약 했었는데 언팔로우 상태라면?
            relation.setFollowed(true); // 지금은 다시 팔로우라고 반환
        }

        //값을 반전처리!!!

        followsRepository.save(relation);

    }
    //언팔로우 서비스
    public void unfollow(Long meId, Long userId) {

        validId(meId, userId);

        Users me = usersRepository.getReferenceById(meId); //  Id 값만 저장한 프록시 객체

        Users followee = usersRepository.getReferenceById(userId);

        Follows relation = followsRepository
                    .findByFollowerAndFollowee(me, followee) // 팔로우 이력 확인 -> Users로 보냈지만, id값으로 판별해서 프록시라도 괜찮
                    .orElseThrow( () -> new IllegalArgumentException("팔로우를 한 적이 없습니다.")); // 없으면 이미 언팔로우

        if(!relation.isIsFollowed()) return; // 이력이 있는데 언팔로우 상태이면 그대로 둠

        relation.setFollowed(false); // 이력이 있는데 팔로우 상태면 언팔로우 변경
    }


    //공통 검증
    private void validId(Long meId, Long userId) {
        if (meId == null || userId == null) {
            throw new IllegalArgumentException("해당 유저들이 null 입니다.");
        }

        if (meId.equals(userId)) {
            throw new IllegalArgumentException("자기 자신을 팔로우할 수 없습니다.");
        }

        if (!usersRepository.existsById(meId)) {
            throw new IllegalArgumentException("현재 유저가 없습니다.");
        }

        if (!usersRepository.existsById(userId)) {
            throw new IllegalArgumentException("대상 유저가 없습니다.");
        }
    }

}
