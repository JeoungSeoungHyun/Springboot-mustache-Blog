package site.metacoding.blog.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@Controller
public class PostController {

    // 글쓰기 페이지 이동(정적)
    @GetMapping("/post/writeForm")
    public String writeForm() {
        return "post/writeForm";
    }

    // 글쓰기(정적)
    // 메인페이지로 redirect하여 UX 향상
    @PostMapping("/post")
    public String write() {
        return "redirect:/";
    }

    // 글 목록 페이지(정적)
    // 메인페이지로 사용하기 위해 2개의 주소를 mapping
    @GetMapping({ "/", "/post/list" })
    public String list() {
        return "post/list";
    }

    // 글 상세보기(동적)
    @GetMapping("/post/{id}")
    public String detail(@PathVariable Integer id) {
        return "post/detail";
    }

    // 글 수정 페이지 이동(동적)
    // 글 삭제, 글 수정 버튼 만들기
    @GetMapping("/post/{id}/updateForm")
    public String updateForm(@PathVariable Integer id) {
        return "post/updateForm";
    }

    // 글 수정(동적)
    // 글 상세보기 페이지로 redirect하여 UX 향상
    @PutMapping("/post/{id}")
    public String update(@PathVariable Integer id) {
        return "redirect:/post/" + id;
    }

    // 글 삭제(동적)
    // 메인페이지로 redirect하여 UX 향상
    @DeleteMapping("/post/{id}")
    public String delete(@PathVariable Integer id) {
        return "redirect:/";
    }
}
