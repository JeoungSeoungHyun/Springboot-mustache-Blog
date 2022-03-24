package site.metacoding.blog.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import site.metacoding.blog.domain.user.User;
import site.metacoding.blog.service.UserService;
import site.metacoding.blog.web.dto.ResponseDto;

@RequiredArgsConstructor
@Controller
public class UserController {

    // 컴포지션
    private final UserService userService;
    private final HttpSession session;

    @GetMapping("/api/user/username/same-check")
    public @ResponseBody ResponseDto<String> sameCheck(String username) {

        String data = userService.유저네임중복검사(username);
        return new ResponseDto<String>(1, "통신성공", data);
    }

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

        userService.회원가입(user);

        return "redirect:/loginForm";

    }

    // 로그인 페이지 이동(정적) - 로그인 x
    @GetMapping("/loginForm")
    public String loginForm(HttpServletRequest request, Model model) {

        if (request.getCookies() != null) {

            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("remember")) {
                    model.addAttribute("remember", cookie.getValue());
                }
            }
        }

        return "user/loginForm";
    }

    // 로그인 - 로그인 x
    // 개인정보 유출을 막기 위해 Post요청
    // 메인페이지로 redirect 해주어 UX 향상
    @PostMapping("/login")
    public String login(User user, HttpServletResponse response) {

        User userEntity = userService.로그인(user);

        if (userEntity != null) {
            session.setAttribute("principal", userEntity);

            if (user.getRemember() != null && user.getRemember().equals("on")) {
                response.addHeader("Set-Cookie", "remember=" + user.getUsername());
            }

            return "redirect:/";
        } else {
            return "redirect:/loginForm";
        }
    }

    // 회원정보 페이지(동적) - 로그인 o
    @GetMapping("/s/user/{id}")
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

        User userEntity = userService.유저정보보기(id);
        if (userEntity == null) {
            return "error/page1";
        } else {
            model.addAttribute("user", userEntity);
            return "user/detail";
        }

    }

    // 회원정보 수정 페이지(동적) - 로그인 o
    @GetMapping("/s/user/updateForm")
    public String updateForm() {
        session.getAttribute("principal");
        return "user/updateForm";
    }

    // username(X), password(O), email(O)
    // password=1234&email=ssar@nate.com (x-www-form-urlencoded)
    // { "password" : "1234", "email" : "ssar@nate.com" } (application/json)
    // json을 받을 것이기 때문에 Spring이 데이터 받을 때 파싱전략을 변경!!
    // Put 요청은 Http Body가 있다. Http Header의 Content-Type에 MIME타입을 알려줘야 한다.

    // @RequestBody -> BufferedReader + JSON 파싱(자바 오브젝트)
    // @ResponseBody -> BufferedWriter + JSON 파싱(자바 오브젝트)

    // 회원정보 수정 - 로그인 o
    // 회원정보 페이지로 redirect 해주어 UX 향상
    @PutMapping("/s/user/{id}")
    public @ResponseBody ResponseDto<User> update(@PathVariable Integer id, @RequestBody User user) {

        User principal = (User) session.getAttribute("principal");

        // 1. 인증 체크
        if (principal == null) {
            return new ResponseDto<User>(-1, "인증안됨", null);
        }

        // 2. 권한 체크
        if (principal.getUserId() != id) {
            return new ResponseDto<User>(-1, "권한없음", null);
        }

        User userEntity = userService.유저수정(id, user);

        session.setAttribute("principal", userEntity); // 세션변경 - 덮어쓰기

        return new ResponseDto<User>(1, "성공", null);
    }

    // 로그아웃 페이지(정적) - 로그인 o
    // 메인페이지로 redirect 해주어 UX 향상
    @GetMapping("/logout")
    public String logout() {
        session.invalidate();
        return "redirect:/loginForm";
    }
}
