package com.ajh.flow.controller;

import com.ajh.flow.common.constant.ItemUnit;
import com.ajh.flow.dto.ItemRegisterForm;
import com.ajh.flow.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    //상품 등록 폼 이동
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("itemForm", new ItemRegisterForm());
        model.addAttribute("itemUnits", ItemUnit.values());
        return "items/new";
    }

    //상품 등록 실행
    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("itemForm")ItemRegisterForm form, BindingResult result) {
        if(result.hasErrors()) {
            return "items/new";
        }
        itemService.registerItem(form);
        return "redirect:/items/list";
    }
}
