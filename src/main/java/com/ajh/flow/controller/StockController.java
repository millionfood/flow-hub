package com.ajh.flow.controller;

import com.ajh.flow.common.constant.StockStatus;
import com.ajh.flow.dto.stock.StockRegisterDto;
import com.ajh.flow.service.ItemService;
import com.ajh.flow.service.LocationService;
import com.ajh.flow.service.StockService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;
    private final LocationService locationService;
    private final ItemService itemService;


    //-----------------등록-----------------
    //입고 등록 페이지 호출
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("stock", new StockRegisterDto());
        model.addAttribute("items", itemService.findAll());
        model.addAttribute("locations", locationService.findAll());
        model.addAttribute("stockStatus", StockStatus.values());
        return "stocks/new";
    }
    //입고 실행 처리
    @PostMapping("/new")
    public String create(@ModelAttribute("inboundForm") StockRegisterDto form) {

        stockService.registerStock(form);
        return "redirect:/stocks/list";
    }


    //-----------------조회-----------------
    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("stocks",stockService.findAll());

        return "stocks/list";
    }

    //-----------------상태변경-----------------


}
