package com.ajh.flow.controller;

import com.ajh.flow.common.constant.ItemUnit;
import com.ajh.flow.domain.Item;
import com.ajh.flow.dto.item.ItemDetailDto;
import com.ajh.flow.dto.item.ItemRegisterDto;
import com.ajh.flow.dto.item.ItemSearchCond;
import com.ajh.flow.dto.item.ItemUpdateDto;
import com.ajh.flow.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    //admin페이지로 이동
    //-----------------조회-----------------
    //상품 전체 조회 페이지
    @GetMapping("/list")
    public String list(Model model, @PageableDefault Pageable pageable, ItemSearchCond cond) {
        model.addAttribute("cond", cond);
        model.addAttribute("page", itemService.findAllDetailDto(pageable, cond));
        return "items/list";
    }
    //상품 상세 페이지
    @GetMapping("/detail/{id}")
    public String detail(Model model,@PathVariable Long id) {
        Item item = itemService.findById(id);
        ItemDetailDto itemDetailDto = new ItemDetailDto(item);
        model.addAttribute("itemDetail", itemDetailDto);

        return "items/detail";
    }


    //-----------------수정-----------------
    //상품 정보 수정 페이지
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Item item = itemService.findById(id);
        ItemUpdateDto updateDto = new ItemUpdateDto(item);
        model.addAttribute("itemForm", updateDto);
        model.addAttribute("itemId", id);
        model.addAttribute("itemUnits", ItemUnit.values());
        return "items/edit";
    }

    //상품 정보 수정 처리
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute("itemForm") ItemUpdateDto form, BindingResult result) {
        if(result.hasErrors()) {
            return "items/edit";
        }
        itemService.updateItem(id,form);

        return "redirect:/items/list";
    }


    //-----------------삭제-----------------
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        itemService.deleteItem(id);
        return  "redirect:/items/list";
    }
}
