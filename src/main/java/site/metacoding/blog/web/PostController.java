package site.metacoding.blog.web;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import lombok.RequiredArgsConstructor;
import site.metacoding.blog.domain.post.Post;
import site.metacoding.blog.domain.post.PostRepository;
import site.metacoding.blog.domain.user.User;

@RequiredArgsConstructor
@Controller
public class PostController {

    private final HttpSession session;
    private final PostRepository postRepository;

    // 글쓰기 페이지 이동(정적) - 인증 o
    @GetMapping("/s/post/writeForm")
    public String writeForm() {

        if (session.getAttribute("principal") == null) {
            return "redirect:/loginForm";
        }
        return "post/writeForm";
    }

    // 글쓰기(정적) - 인증 o
    // 메인페이지로 redirect하여 UX 향상
    @PostMapping("/s/post")
    public String write(Post post) {
        // 유효성 검사
        // 인증 검사
        if (session.getAttribute("principal") == null) {
            return "redirect:/loginForm";
        }

        User principal = (User) session.getAttribute("principal");
        post.setUser(principal);

        postRepository.save(post);

        return "redirect:/";
    }

    // 글 목록 페이지(정적) - 인증 x
    // 메인페이지로 사용하기 위해 2개의 주소를 mapping
    @GetMapping({ "/", "/post/list" })
    public String list(Model model) {
        model.addAttribute("posts", postRepository.findAll(Sort.by(Sort.Direction.DESC, "id")));
        return "post/list";
    }

    // 글 상세보기(동적) - 인증 x
    @GetMapping("/post/{id}")
    public String detail(@PathVariable Integer id, Model model) {

        Optional<Post> postOp = postRepository.findById(id);

        if (postOp.isPresent()) {

            Post postEntity = postOp.get();
            model.addAttribute("post", postEntity);
            // Lazy전략 사용시 ============== 출력 후 user를 SELECT => 필요시 SELECT
            System.out.println("=========================");
            return "post/detail";
        } else {
            return "error/page1";
        }

    }

    // 글 수정 페이지 이동(동적) - 인증 o
    // 글 삭제, 글 수정 버튼 만들기
    @GetMapping("/s/post/{id}/updateForm")
    public String updateForm(@PathVariable Integer id) {
        return "post/updateForm";
    }

    // 글 수정(동적) - 인증 o
    // 글 상세보기 페이지로 redirect하여 UX 향상
    @PutMapping("/s/post/{id}")
    public String update(@PathVariable Integer id) {
        return "redirect:/post/" + id;
    }

    // 글 삭제(동적) - 인증 o
    // 메인페이지로 redirect하여 UX 향상
    @DeleteMapping("/s/post/{id}")
    public String delete(@PathVariable Integer id) {
        return "redirect:/";
    }
}
