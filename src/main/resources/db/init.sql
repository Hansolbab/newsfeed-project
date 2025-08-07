# -- 유저 테이블
# CREATE TABLE users (
#     userId BIGINT AUTO_INCREMENT PRIMARY KEY,
#     userName VARCHAR(10) NOT NULL,
#     phone_number VARCHAR(11),
#     email VARCHAR(255) NOT NULL UNIQUE,
#     password VARCHAR(255) NOT NULL,
#     followings VARCHAR(255) NULL,
#     followers VARCHAR(255) NULL,
#     isDeleted BOOLEAN DEFAULT FALSE,
#     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
#     modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
# );
#
# -- 카테고리 테이블
# CREATE TABLE categories (
#     categoryId BIGINT AUTO_INCREMENT PRIMARY KEY,
#     category ENUM('한식', '중식', '일식', '양식') NOT NULL,
#     feedId BIGINT NULL,
#
#     CONSTRAINT fk_category_feed
#         FOREIGN KEY (feedId) REFERENCES feeds(feedId)
#             ON DELETE SET NULL
# );
#
# -- 게시글 테이블
# CREATE TABLE feeds (
#     feedId BIGINT AUTO_INCREMENT PRIMARY KEY,
#     userId BIGINT NOT NULL,
#     contents TEXT NOT NULL,
#     categoryId BIGINT,
#     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
#     modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
#     FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE CASCADE,
#     FOREIGN KEY (categoryId) REFERENCES categories(categoryId)
# );
#
# -- 게시글 이미지 테이블
# CREATE TABLE feedImg (
#     feedId BIGINT AUTO_INCREMENT PRIMARY KEY,
#     imageUrl TEXT NOT NULL,
#     isDeleted BOOLEAN NOT NULL,
#     FOREIGN KEY (feedId) REFERENCES feeds(feedId) ON DELETE CASCADE
# );
#
# -- 댓글 테이블
# CREATE TABLE comments (
#     commentsId BIGINT AUTO_INCREMENT PRIMARY KEY,
#     userId BIGINT NOT NULL,
#     feedId BIGINT NOT NULL,
#     contents VARCHAR(500) NOT NULL,
#     isDeleted BOOLEAN NOT NULL,
#     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
#     modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
#     FOREIGN KEY (feedId) REFERENCES feeds(feedId) ON DELETE CASCADE,
#     FOREIGN KEY (userId) REFERENCES users(userId)
# );
#
# -- 좋아요 테이블
# CREATE TABLE likes (
#     likeId BIGINT AUTO_INCREMENT PRIMARY KEY,
#     userId BIGINT NOT NULL,
#     feedId BIGINT NOT NULL,
#     isLiked BOOLEAN NOT NULL,
#     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
#     UNIQUE KEY uq_feed_user (feedId, userId),
#     FOREIGN KEY (feedId) REFERENCES feeds(feedId) ON DELETE CASCADE,
#     FOREIGN KEY (userId) REFERENCES users(userId)
# );
#
# -- 팔로우 테이블
# CREATE TABLE follows (
#     followerId BIGINT AUTO_INCREMENT PRIMARY KEY,
#     userId BIGINT NOT NULL,
#     followee_id BIGINT NOT NULL,
#     isFollowed BOOLEAN NOT NULL,
#     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
#     UNIQUE KEY uq_user_followee (userId, followee_id),
#     FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE CASCADE,
#     FOREIGN KEY (followee_id) REFERENCES users(userId) ON DELETE CASCADE
# );
#
# -- 유저 토큰 테이블
# CREATE TABLE user_tokens (
#     tokenId BIGINT AUTO_INCREMENT PRIMARY KEY,
#     userId BIGINT NOT NULL,
#     refresh_token VARCHAR(500) NOT NULL,
#     expiry_date DATETIME NOT NULL,
#     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
#     modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
#
#     CONSTRAINT fk_user_token_user
#         FOREIGN KEY (userId) REFERENCES users(userId)
#             ON DELETE CASCADE
# );
#
# -- 프로필 이미지 테이블
# CREATE TABLE profileImg (
#     pro_id BIGINT AUTO_INCREMENT PRIMARY KEY,
#     userId BIGINT NOT NULL,
#     profileImg TEXT NULL,
#     isDeleted BOOLEAN NOT NULL,
#     uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
#
#     CONSTRAINT fk_profile_img_user
#         FOREIGN KEY (userId) REFERENCES users(userId)
#             ON DELETE CASCADE
# );
