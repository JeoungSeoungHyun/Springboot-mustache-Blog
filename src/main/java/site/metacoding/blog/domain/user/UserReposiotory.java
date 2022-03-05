package site.metacoding.blog.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// repository는 인터페이스로 만들어 콤포지션
@Repository // 어노테이션이 JpaRepository에 있어 필요없긴 하다.
public interface UserReposiotory extends JpaRepository<User, Integer> {

    @Query(value = "SELECT * FROM user WHERE username=:username AND password=:password", nativeQuery = true)
    User mLogin(@Param("username") String username, @Param("password") String password);

}
