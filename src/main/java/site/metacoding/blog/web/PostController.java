package site.metacoding.blog.web;

import javax.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import site.metacoding.blog.domain.post.Post;
import site.metacoding.blog.domain.user.User;
import site.metacoding.blog.service.PostService;
import site.metacoding.blog.web.dto.ResponseDto;

@RequiredArgsConstructor
@Controller
public class PostController {

    private final HttpSession session;
    private final PostService postService;

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
        postService.글쓰기(post, principal);

        return "redirect:/";
    }

    // 글 목록 페이지(정적) - 인증 x
    // 메인페이지로 사용하기 위해 2개의 주소를 mapping
    @GetMapping({ "/", "/post/list" })
    public String list(@RequestParam(defaultValue = "0") Integer page, Model model) {

        Page<Post> pagePosts = postService.글목록보기(page);

        model.addAttribute("posts", pagePosts);
        model.addAttribute("prevPage", page - 1);
        model.addAttribute("nextPage", page + 1);

        return "post/list";
    }

    // 글 상세보기(동적) - 인증 x
    @GetMapping("/post/{id}")
    public String detail(@PathVariable Integer id, Model model) {

        User principal = (User) session.getAttribute("principal");

        Post postEntity = postService.글상세보기(id);

        if (postEntity == null) {
            return "error/page1";
        }

        if (principal != null) {
            // 권한 확인해서 view로 값을 넘김
            if (principal.getUserId() == postEntity.getUser().getUserId()) {
                model.addAttribute("pageOwner", true);
            } else {
                model.addAttribute("pageOwner", false);
            }
        }

        // 스크립트 방어(내용에 태그가 있다면 바꿔서 돌려주기)
        String rawContent = postEntity.getContent();
        String encContent = rawContent
                .replaceAll("<script>", "&lt;script&gt;")
                .replaceAll("</script>", "&lt;/script&gt;");
        postEntity.setContent(encContent);

        model.addAttribute("post", postEntity);
        return "post/detail";
    }

    // 글 수정 페이지 이동(동적) - 인증 o
    // 글 삭제, 글 수정 버튼 만들기
    @GetMapping("/s/post/{id}/updateForm")
    public String updateForm(@PathVariable Integer id, Model model) {

        User principal = (User) session.getAttribute("principal");

        // 로그인(인증) 확인
        if (principal == null) {
            return "error/page1";
        }

        Post postEntitiy = postService.글상세보기(id);

        // 글의 권한 확인
        if (principal.getUserId() != postEntitiy.getUser().getUserId()) {
            return "error/page1";
        }

        model.addAttribute("post", postEntitiy);

        return "post/updateForm";
    }

    // 글 수정(동적) - 인증 o
    // 글 상세보기 페이지로 redirect하여 UX 향상
    @PutMapping("/s/post/{id}")
    public @ResponseBody ResponseDto<String> update(@PathVariable Integer id, @RequestBody Post post) {

        postService.글수정하기(post, id);

        return new ResponseDto<String>(1, "성공", null);
    }

    // 글 삭제(동적) - 인증 o
    // 메인페이지로 redirect하여 UX 향상
    @DeleteMapping("/s/post/{id}")
    public @ResponseBody ResponseDto<String> delete(@PathVariable Integer id) {

        User principal = (User) session.getAttribute("principal");

        // 로그인(인증) 확인
        if (principal == null) {
            return new ResponseDto<String>(-1, "로그인이 되지않았습니다", null);
        }

        Post postEntitiy = postService.글상세보기(id);

        // 글의 권한 확인
        if (principal.getUserId() != postEntitiy.getUser().getUserId()) {
            return new ResponseDto<String>(-1, "해당 글을 삭제할 권한이 없습니다.", null);
        }

        postService.글삭제하기(id);

        return new ResponseDto<String>(1, "성공", null);
    }
}
