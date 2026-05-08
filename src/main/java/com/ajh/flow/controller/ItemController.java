package com.ajh.flow.controller;

import com.ajh.flow.common.constant.ItemUnit;
import com.ajh.flow.domain.Item;
import com.ajh.flow.dto.item.ItemDetailDto;
import com.ajh.flow.dto.item.ItemRegisterDto;
import com.ajh.flow.dto.item.ItemUpdateDto;
import com.ajh.flow.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    //-----------------등록-----------------
    //상품 등록 페이지
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("itemForm", new ItemRegisterDto());
        model.addAttribute("itemUnits", ItemUnit.values());
        return "items/new";
    }

    //상품 등록 처리
    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("itemForm") ItemRegisterDto form, BindingResult result) {
        if(result.hasErrors()) {
            return "items/new";
        }
        itemService.registerItem(form);
        return "redirect:/items/list";
    }


    //-----------------조회-----------------
    //상품 전체 조회 페이지
    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("items", itemService.findAll());
        return "items/list";
    }
    //상품 상세 페이지
    @GetMapping("/detail/{itemId}")
    public String detail(Model model,@PathVariable("itemId") Long itemId) {
        Item item = itemService.findById(itemId);
        ItemDetailDto itemDetailDto = new ItemDetailDto(item);
        model.addAttribute("itemDetail", itemDetailDto);

        return "items/detail";
    }

    //-----------------수정-----------------
    //상품 정보 수정 페이지
    @GetMapping("/edit/{itemId}")
    public String editForm(@PathVariable("itemId") Long ItemId, Model model) {
        Item item = itemService.findById(ItemId);
        ItemUpdateDto updateDto = new ItemUpdateDto(item);
        model.addAttribute("itemForm", updateDto);
        model.addAttribute("itemId", ItemId);
        model.addAttribute("itemUnits", ItemUnit.values());
        return "items/edit";
    }

    //상품 정보 수정 처리
    @PostMapping("/edit/{itemId}")
    public String edit(@PathVariable("itemId") Long itemId, @Valid @ModelAttribute("itemForm") ItemUpdateDto form, BindingResult result) {
        if(result.hasErrors()) {
            log.info("에러가 발생했습니다.");
            return "items/edit/";
        }
        log.info("상품 아이디입니다.: {}",itemId);
        itemService.updateItem(itemId,form);

        return "redirect:/items/list";
    }
}
