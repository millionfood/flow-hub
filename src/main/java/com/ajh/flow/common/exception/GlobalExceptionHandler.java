package com.ajh.flow.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, HttpServletRequest request, RedirectAttributes rttr) {
        log.error("예상하지 못한 오류: {}",e.getMessage());

        rttr.addFlashAttribute("error", e.getMessage());
        return "redirect:/";
    }

}
