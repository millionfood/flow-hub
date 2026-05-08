package com.ajh.flow.controller;

import com.ajh.flow.domain.Warehouse;
import com.ajh.flow.dto.warehouse.WarehouseRegisterDto;
import com.ajh.flow.dto.warehouse.WarehouseDetailDto;
import com.ajh.flow.service.ItemService;
import com.ajh.flow.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping ("/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final ItemService itemService;


    //-----------------등록-----------------
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("warehouseForm",new WarehouseRegisterDto());
        return "warehouse/new";
    }
    @PostMapping("/new")
    public String create(@Valid WarehouseRegisterDto wareHouseRegisterDto, BindingResult result) {
        if(result.hasErrors()) {
            return "warehouse/new";
        }
        warehouseService.registerWarehouse(wareHouseRegisterDto);
        return "redirect:/warehouses/list";
    }


    //-----------------조회-----------------
    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("warehouses",warehouseService.findAll());
        return "warehouse/list";
    }
    @GetMapping("/detail/{warehouseId}")
    public String detail(@PathVariable Long warehouseId, Model model) {
        Warehouse warehouse = warehouseService.findById(warehouseId);
        WarehouseDetailDto warehouseDetailDto = new WarehouseDetailDto(warehouse);
        model.addAttribute("warehouseDetail",warehouseDetailDto);

        return "warehouse/detail";
    }

    //-----------------수정-----------------


    //-----------------삭제-----------------
}
