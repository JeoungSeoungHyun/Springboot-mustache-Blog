package site.metacoding.blog.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import site.metacoding.blog.domain.user.User;
import site.metacoding.blog.domain.user.UserReposiotory;

@Controller
public class UserController {

    // 컴포지션
    private UserReposiotory userReposiotory;

    // 의존성 주입
    public UserController(UserReposiotory userReposiotory) {
        this.userReposiotory = userReposiotory;
    }

    // 회원가입 페이지 이동(정적) - 로그인 x
    @GetMapping("/joinForm")
    public String joinForm() {
        return "user/joinForm";
    }

    // 회원가입 - 로그인 x
    // 로그인페이지로 redirect 해주어 UX 향상(로그인페이지 이동 메서드 재활용)
    @PostMapping("/join")
    public String join(User user) {
        System.out.println("user : " + user);

        // 회원가입 요청 데이터 DB에 insert하기
        userReposiotory.save(user);

        User userEntity = user;
        System.out.println("userEntity : " + userEntity);
        return "redirect:/loginForm";
    }

    // 로그인 페이지 이동(정적) - 로그인 x
    @GetMapping("/loginForm")
    public String loginForm() {
        return "user/loginForm";
    }

    // 로그인 - 로그인 x
    // 개인정보 유출을 막기 위해 Post요청
    // 메인페이지로 redirect 해주어 UX 향상
    @PostMapping("/login")
    public String login(User user, HttpServletRequest request) {

        HttpSession session = request.getSession();

        // 1. DB로부터 로그인요청 데이터와 맞은 데이터 가져오기
        User userEntity = userReposiotory.mLogin(user.getUsername(), user.getPassword());

        // 2. 로그인 상태 확인(if문 사용)
        if (userEntity == null) {
            System.out.println("아이디 또는 패스워드가 잘못되었습니다.");
            return "redirect:/loginForm";
        } else {
            System.out.println("로그인되었습니다.");

            // 3. 세션에 담기(키 값 principal 기억!!)
            session.setAttribute("principal", userEntity);

            return "redirect:/";
        }
    }

    // 회원정보 페이지(동적) - 로그인 o
    @GetMapping("/user/{id}")
    public String detail(@PathVariable Integer id) {
        return "user/detail";
    }

    // 회원정보 수정 페이지(동적) - 로그인 o
    @GetMapping("/user/{id}/updateForm")
    public String updateForm(@PathVariable Integer id) {
        return "user/updateForm";
    }

    // 회원정보 수정 - 로그인 o
    // 회원정보 페이지로 redirect 해주어 UX 향상
    @PutMapping("/user/{id}")
    public String update(@PathVariable Integer id) {
        return "redirect:/user/" + id;
    }

    // 로그아웃 페이지(정적) - 로그인 o
    // 메인페이지로 redirect 해주어 UX 향상
    @GetMapping("/user/logout")
    public String logout() {
        return "redirect:/";
    }
}
