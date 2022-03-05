package site.metacoding.blog.domain.user;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// User 테이블 모델
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
public class User {

    // PrimaryKey(DB 전략 따라 생성)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    // 길이 설정, 중복 불가 설정
    @Column(length = 20, unique = true)
    private String username;

    // 길이 설정, null 설정
    @Column(length = 12, nullable = false)
    private String password;

    // 길이 설정
    @Column(length = 16000000)
    private String email;

    @CreatedDate
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime updateDate;
}
