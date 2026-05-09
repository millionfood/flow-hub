package com.ajh.flow.controller;

import com.ajh.flow.common.constant.LocationZone;
import com.ajh.flow.common.constant.UseYn;
import com.ajh.flow.domain.Location;
import com.ajh.flow.dto.location.LocationDetailDto;
import com.ajh.flow.dto.location.LocationRegisterDto;
import com.ajh.flow.service.LocationService;
import com.ajh.flow.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("locationForm", new LocationRegisterDto());
        model.addAttribute("locationZone", LocationZone.values());
        model.addAttribute("useYn", UseYn.values());
        model.addAttribute("warehouseList", warehouseService.findAll());

        return "location/new";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("locationForm") LocationRegisterDto locationRegisterDto, BindingResult result, Model model) {
        if(result.hasErrors()){
            model.addAttribute("locationZone", LocationZone.values());
            model.addAttribute("useYn", UseYn.values());
            model.addAttribute("warehouseList", warehouseService.findAll());
            return "location/new";
        }
        locationService.registerLocation(locationRegisterDto);
        return  "redirect:/location/list";
    }


    //-----------------조회-----------------
    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("locationList", locationService.findAll());
        return  "location/list";
    }
    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable Long id){
        Location location = locationService.findById(id);
        LocationDetailDto dto = new LocationDetailDto(location);
        model.addAttribute("location", dto);

        return "location/detail";
    }


    //-----------------수정-----------------


    //-----------------상태 변경-----------------

}
