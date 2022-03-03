package site.metacoding.blog.domain.post;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import site.metacoding.blog.domain.user.User;

// Post 테이블 모델
@Entity
public class Post {

    // PrimaryKey(DB 전략 따라 생성 )
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // 길이 설정, null 설정
    @Column(length = 20, nullable = false)
    private String title;

    // 타입 설정(길이), null 설정
    @Lob
    @Column(nullable = false)
    private String content;

    // ORM 설정
    @JoinColumn(name = "userId")
    @ManyToOne
    private User user;

    private LocalDateTime creatDate;
}
