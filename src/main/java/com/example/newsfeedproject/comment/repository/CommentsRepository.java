package com.example.newsfeedproject.comment.repository;

import com.example.newsfeedproject.comment.entity.Comments;
import com.example.newsfeedproject.feeds.entity.Feeds;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, Long> {

    // 특정 Feed에 속한 댓글들을 조회하는 쿼리 메서드 (최신순 정렬)
    List<Comments> findByFeedCommentsAndDeletedFalseOrderByCreatedAtDesc(Feeds feed);

    // 특정 Feed에 속한 특정 댓글 조회 (deleted가 아닌 경우)
    Optional<Comments> findByCommentIdAndFeedCommentsAndDeletedFalse(Long commentId, Feeds feed);

    // 사용자ID와 FeedID로 댓글 조회 (작성자 확인 및 삭제 여부 확인 시 유용)
    Optional<Comments> findByCommentIdAndUserCommentsUserIdAndFeedCommentsFeedIdAndDeletedFalse(Long commentId, Long userId, Long feedId);
}
