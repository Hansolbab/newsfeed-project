package com.example.newsfeedproject.requestfollow.service;


import com.example.newsfeedproject.auth.impl.UserDetailsImpl;
import com.example.newsfeedproject.common.exception.FollowErrorException;
import com.example.newsfeedproject.follow.entity.FollowStatus;
import com.example.newsfeedproject.follow.entity.Follows;
import com.example.newsfeedproject.follow.repository.FollowsRepository;
import com.example.newsfeedproject.requestfollow.dto.ReadMyRequestResponseDto;
import com.example.newsfeedproject.requestfollow.dto.ReadRequestFollowUsersDto;
import com.example.newsfeedproject.requestfollow.dto.RequestFollowResponseDto;
import com.example.newsfeedproject.requestfollow.entity.RequestFollows;
import com.example.newsfeedproject.requestfollow.repository.RequestFollowRepository;
import com.example.newsfeedproject.users.entity.Users;
import com.example.newsfeedproject.users.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.example.newsfeedproject.common.exception.FollowErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestFollowService {

    private final UsersRepository usersRepository;
    private final RequestFollowRepository requestFollowRepository;
    private final FollowsRepository followsRepository;



    //팔로우 요청
    public RequestFollowResponseDto requestFollow(UserDetailsImpl userDetails, Long userId) {

        Long meId =userDetails.getUserId();

        if (meId.equals(userId)) {
            throw new FollowErrorException(SELF_FOLLOW_NOT);
        }

        if (!usersRepository.existsById(userId)) {
            throw new FollowErrorException(USER_NOT_FOUND);
        }

        Users me =  usersRepository.getReferenceById(meId);

        Users requester = usersRepository.getReferenceById(userId);

        RequestFollows requestRelation = requestFollowRepository.findByRequesterAndTarget(requester, me)
                .orElseGet( () -> new RequestFollows(requester, me));

        if(requestRelation.getFollowStatus().equals(FollowStatus.REQUESTED))
        {
            throw new FollowErrorException(ALREADY_REQUEST);
        }

        requestRelation.request();

        requestFollowRepository.save(requestRelation);

        return  new RequestFollowResponseDto(requestRelation.getFollowStatus());
    }

    //팔로우 승인
    public RequestFollowResponseDto acceptFollow (UserDetailsImpl userDetails, Long userId) {


        RequestFollows requestRelation =getRelationOrThrow(userDetails, userId);

        //승인이 되었으니, 팔로우 테이블에 팔로우 여부를 true 값으로 변경하여 넣어준다.

        Follows relation = getRelation(userDetails, userId);

        requestRelation.accept();


        relation.follow();
        followsRepository.save(relation);

        return new RequestFollowResponseDto(requestRelation.getFollowStatus());
    }


    //팔로우 거절
    public RequestFollowResponseDto rejectedFollow (UserDetailsImpl userDetails, Long userId) {


        RequestFollows requestRelation = getRelationOrThrow(userDetails, userId);

        requestRelation.reject();

        return new RequestFollowResponseDto(requestRelation.getFollowStatus());
    }


    //팔로우 취소
    public RequestFollowResponseDto cancelFollow (UserDetailsImpl userDetails, Long userId) {


        RequestFollows requestRelation = getRelationOrThrow(userDetails, userId);
        requestRelation.cancel();

        return new RequestFollowResponseDto(requestRelation.getFollowStatus());
    }

    //나에게 온 요청을 보는 아이디
    public Page<ReadRequestFollowUsersDto> readRequestMe(UserDetailsImpl userDetails, Pageable pageable) {

        Long meId = userDetails.getUserId();
        
        Users me = usersRepository.getReferenceById(meId);

        Page<RequestFollows> requestMePage = requestFollowRepository.findByTargetAndFollowStatus(me , FollowStatus.REQUESTED, pageable);


        return requestMePage.map(ReadRequestFollowUsersDto::todto);
    }

    //내가 요청을 보낸 아이디
    public Page<ReadMyRequestResponseDto> readMyRequest(UserDetailsImpl userDetails, Pageable pageable) {
        
        Long meId = userDetails.getUserId();
        
        Users me = usersRepository.getReferenceById(meId);

        Page<RequestFollows> myRequestPage = requestFollowRepository.findByRequesterAndFollowStatus(me, FollowStatus.REQUESTED, pageable);

        return  myRequestPage.map(ReadMyRequestResponseDto::todto);
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

   
}
