package com.ajh.flow.controller;

import com.ajh.flow.domain.Warehouse;
import com.ajh.flow.dto.warehouse.WarehouseDetailDto;
import com.ajh.flow.dto.warehouse.WarehouseSearchCond;
import com.ajh.flow.dto.warehouse.WarehouseUpdateDto;
import com.ajh.flow.service.ItemService;
import com.ajh.flow.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    //adminController로 이동
    //-----------------조회-----------------
    @GetMapping("/list")
    public String list(Model model, @PageableDefault(size = 10) Pageable pageable, WarehouseSearchCond cond) {
        model.addAttribute("cond",cond);
        model.addAttribute("page",warehouseService.findAllWithPaging(pageable, cond));
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
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Warehouse warehouse = warehouseService.findById(id);
//        WarehouseUpdateDto warehouseUpdateDto = new WarehouseUpdateDto(warehouse);
//        model.addAttribute("warehouseForm",warehouseUpdateDto);
        model.addAttribute("warehouseId",id);
        model.addAttribute("useYn", warehouse.getUseYn());

        return "warehouse/edit";
    }
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @Valid WarehouseUpdateDto warehouseUpdateDto, BindingResult result) {
        if(result.hasErrors()) {
            return "warehouse/edit";
        }
        warehouseService.updateWarehouse(id, warehouseUpdateDto);

        return  "redirect:/warehouses/detail/"+id;
    }

    //-----------------사용 상태 변경-----------------
    @PostMapping("/stopUse/{id}")
    public String stopUse(@PathVariable Long id){
        warehouseService.stopUseWarehouse(id);
        return  "redirect:/warehouses/detail/"+id;
    }

    @PostMapping("/reUse/{id}")
    public String reUse(@PathVariable Long id){
        warehouseService.reUseWarehouse(id);
        return  "redirect:/warehouses/detail/"+id;
    }



}
