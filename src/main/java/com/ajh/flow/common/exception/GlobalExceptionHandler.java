package com.ajh.flow.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(BusinessException e, HttpServletRequest request, RedirectAttributes rttr) {
        log.error("비즈니스 로직 오류: {}",e.getMessage());

        rttr.addFlashAttribute("error", e.getMessage());

        String referer = request.getHeader("Referer");
        return "redirect:"+(referer != null ? referer : "/item/list");
    }
    ///.well-known/appspecific/com.chrome.devtools.json 요청 방어
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleNoResourceFoundException(NoResourceFoundException e) {

        log.error("브라우저의 불필요한 요청 방어 로직: {}",e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("정적 리소스를 찾을 수 없습니다: " + e.getResourcePath());
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, HttpServletRequest request, RedirectAttributes rttr) {

        log.error("예상하지 못한 오류: {}",e.getMessage());

        rttr.addFlashAttribute("error", e.getMessage());
        return "redirect:/";
    }

}
