package site.metacoding.blog.domain.post;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.metacoding.blog.domain.user.User;

// Post 테이블 모델
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
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

    @CreatedDate
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime updateDate;
}
