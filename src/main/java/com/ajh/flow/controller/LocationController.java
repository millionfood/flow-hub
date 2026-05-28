package com.ajh.flow.controller;

import com.ajh.flow.common.constant.LocationZone;
import com.ajh.flow.common.constant.UseYn;
import com.ajh.flow.domain.Location;
import com.ajh.flow.dto.location.LocationDetailDto;
import com.ajh.flow.dto.location.LocationRegisterDto;
import com.ajh.flow.dto.location.LocationSearchCond;
import com.ajh.flow.dto.location.LocationUpdateDto;
import com.ajh.flow.service.LocationService;
import com.ajh.flow.service.WarehouseService;
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
@RequestMapping("/location")
@Slf4j
public class LocationController {

    private final LocationService locationService;
    private final WarehouseService warehouseService;


    //-----------------등록-----------------
    //adminController로 이전
    //-----------------조회-----------------
    @GetMapping("/list")
    public String list(@PageableDefault(size = 10) Pageable pageable, LocationSearchCond cond, Model model) {
        model.addAttribute("cond", cond);
        model.addAttribute("zoneList",LocationZone.values());
        model.addAttribute("warehouseList", warehouseService.findAll());
        model.addAttribute("page", locationService.findAllWidthPaging(pageable, cond));
        return  "location/list";
    }

    //-----------------수정-----------------
    //정보 수정은 없음 - 재고가 있는 상태에서 수정할수 없기때문
    //기존의 로케이션을 미사용으로두고, 새로운 로케이션을 추가해서 재고를 이동하는 방식으로 사용

    //-----------------상태 변경-----------------
    @PostMapping("/stopUse/{id}")
    public String stopUse(@PathVariable Long id){
        locationService.stopUseLocation(id);
        return "redirect:/location/detail/"+id;
    }
    @PostMapping("/reUse/{id}")
    public String reUse(@PathVariable Long id){
        locationService.reUseLocation(id);
        return "redirect:/location/detail/"+id;
    }
}
