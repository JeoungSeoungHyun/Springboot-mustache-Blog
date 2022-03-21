package site.metacoding.blog.web;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import lombok.RequiredArgsConstructor;
import site.metacoding.blog.domain.user.User;
import site.metacoding.blog.domain.user.UserReposiotory;

@RequiredArgsConstructor
@Controller
public class UserController {

    // 컴포지션
    private final UserReposiotory userReposiotory;
    private final HttpSession session;

    // 회원가입 페이지 이동(정적) - 로그인 x
    @GetMapping("/joinForm")
    public String joinForm(User user) {

        return "user/joinForm";
    }

    // 회원가입 - 로그인 x
    // 로그인페이지로 redirect 해주어 UX 향상(로그인페이지 이동 메서드 재활용)
    // 공백이나 null값이 들어오는 경우의 유효성 검사가 필요
    @PostMapping("/join")
    public String join(User user) {

        // 1. username, password, eamil의 null과 공백 체크
        if (user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
            return "redirect:/joinForm";
        }

        if (user.getUsername().equals("") || user.getPassword().equals("") || user.getEmail().equals("")) {
            return "redirect:/joinForm";
        }

        // username 중복 확인
        User userEntity = userReposiotory.mCheck(user.getUsername());

        // 2. 핵심로직
        if (userEntity == null) {
            userReposiotory.save(user);

            return "redirect:/loginForm";
        } else {

            return "redirect:/joinForm";
        }

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
    public String login(User user) {

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
    public String detail(@PathVariable Integer id, Model model) {

        // 유효성 검사 (엄청 많지만 인증과 권한만 확인해보도록하자.)

        // 1. 인증체크
        User principal = (User) session.getAttribute("principal");

        if (principal == null) {
            return "error/page1";
        }

        // 2. 권한체크
        if (principal.getUserId() != id) {
            return "error/page1";
        }

        // 3. 핵심로직
        Optional<User> userOp = userReposiotory.findById(id);

        if (userOp.isPresent()) {
            User userEntity = userOp.get();
            model.addAttribute("user", userEntity);
            return "user/detail";
        } else {
            return "error/page1";
        }

        // DB에 로그 남기기(로그인 한 아이디)
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
    @GetMapping("/logout")
    public String logout() {
        session.invalidate();
        return "redirect:/loginForm";
    }
}
