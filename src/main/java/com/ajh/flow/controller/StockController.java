package com.ajh.flow.controller;

import com.ajh.flow.common.constant.LocationZone;
import com.ajh.flow.common.constant.StockStatus;
import com.ajh.flow.domain.Item;
import com.ajh.flow.domain.PrincipalDetails;
import com.ajh.flow.domain.Stock;
import com.ajh.flow.dto.location.LocationDetailDto;
import com.ajh.flow.dto.location.LocationSearchCond;
import com.ajh.flow.dto.stock.*;
import com.ajh.flow.service.ItemService;
import com.ajh.flow.service.LocationService;
import com.ajh.flow.service.StockService;
import com.ajh.flow.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;
    private final LocationService locationService;
    private final WarehouseService warehouseService;
    private final ItemService itemService;


    //-----------------등록-----------------
    //입고 등록 페이지 호출
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("stock", new StockRegisterDto());
        model.addAttribute("warehouses",warehouseService.findAll());
        model.addAttribute("items", itemService.findAll());
        model.addAttribute("locations", locationService.findAll());
        model.addAttribute("stockStatus", StockStatus.values());
        return "stocks/new";
    }
    //입고 실행 처리
    @PostMapping("/new")
    public String create(@ModelAttribute("inboundForm") StockRegisterDto form, @AuthenticationPrincipal PrincipalDetails principalDetails, BindingResult bindingResult) {

        stockService.registerStockWithSecurity(form, principalDetails.getUser());
        return "redirect:/stocks/list";
    }


    //-----------------조회-----------------
    @GetMapping("/list")
    public String list(@PageableDefault Pageable pageable, StockSearchCond cond ,Model model) {
        model.addAttribute("warehouses",warehouseService.findAll());
        model.addAttribute("stockStatus", StockStatus.values());
        model.addAttribute("locationZones", LocationZone.values());
        model.addAttribute("cond", cond);
        model.addAttribute("page",stockService.findAllDetailWithPaging(pageable,cond));

        return "stocks/list";
    }

    //-----------------상태변경-----------------
    //단순 수정
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        StockUpdateDto dto = new StockUpdateDto(stockService.findById(id));
        model.addAttribute("stockId",id);
        model.addAttribute("stockUpdateDto", dto);
        model.addAttribute("stockDetailDto",stockService.findDetailDtoById(id));
        model.addAttribute("statusList", StockStatus.values());

        return "stocks/edit";
    }
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @ModelAttribute("stockUpdateDto") StockUpdateDto dto, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        stockService.updateStock(id, dto, principalDetails.getUser());
        return "redirect:/stocks/list";
    }
    //재고 이동
    @GetMapping("/move/{id}")
    public String moveForm(@PathVariable Long id, Model model) {
        StockDetailDto dto = stockService.findDetailDtoById(id);
        model.addAttribute("stockId",id);
        model.addAttribute("stockMoveDto", new StockMoveDto(dto));
        model.addAttribute("stockDetailDto",dto);
        model.addAttribute("moveableLocations",locationService.findMoveableLocations(dto.getItemId(),dto.getLocationId()));
        return "stocks/move";
    }
    @PostMapping("/move/{id}")
    public String move(@PathVariable Long id, @Valid @ModelAttribute("stockMoveDto") StockMoveDto dto,
                       BindingResult result, Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        StockDetailDto detailDto = stockService.findDetailDtoById(id);
        if (result.hasErrors()) {
            log.error("Stock Move Error : {}",result.getAllErrors());
            model.addAttribute("stockId",id);
            model.addAttribute("stockMoveDto", dto);
            model.addAttribute("stockDetailDto",detailDto);
            model.addAttribute("moveableLocations",locationService.findMoveableLocations(detailDto.getItemId(),detailDto.getLocationId()));
            return "stocks/move";
        }
        stockService.moveStock(id,dto,principalDetails.getUser());
        return "redirect:/stocks/list";
    }
    //-----------------삭제-----------------
    @PostMapping("delete/{id}")
    public String delete(@PathVariable Long id, Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        stockService.deleteStock(id, principalDetails.getUser());
        return "redirect:/stocks/list";
    }

    //-----------------기타-----------------
    @ResponseBody
    @GetMapping("/api/locations-itemAndWarehouse")
    public List<LocationDetailDto> getInboundAbleLocationsByStock(@RequestParam("itemId") Long itemId, @RequestParam("warehouseId") Long warehouseId) {
       return locationService.findInboundAbleALlLocationByItem(itemId,warehouseId);
    }
    @ResponseBody
    @GetMapping("/api/locations-warehouse")
    public List<LocationDetailDto> getInboundAbleLocationsByWarehouse(@RequestParam("warehouseId") Long warehouseId) {
        return locationService.findInboundAbleALlLocationByWareHouse(warehouseId);
    }




}
