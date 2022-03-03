package site.metacoding.blog.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@Controller
public class UserController {

    // 회원가입 페이지 이동(정적) - 로그인 x
    @GetMapping("/joinForm")
    public String joinForm() {
        return "user/joinForm";
    }

    // 회원가입 - 로그인 x
    // 로그인페이지로 redirect 해주어 UX 향상(로그인페이지 이동 메서드 재활용)
    @PostMapping("/join")
    public String join() {
        return "redirect:/user/loginForm";
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
    public String login() {
        return "redirect:/";
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
