package com.ajh.flow.controller;

import com.ajh.flow.common.constant.ItemUnit;
import com.ajh.flow.common.constant.LocationZone;
import com.ajh.flow.common.constant.StockStatus;
import com.ajh.flow.common.constant.UseYn;
import com.ajh.flow.domain.Item;
import com.ajh.flow.domain.Location;
import com.ajh.flow.domain.PrincipalDetails;
import com.ajh.flow.domain.Warehouse;
import com.ajh.flow.dto.history.HistorySearchCond;
import com.ajh.flow.dto.item.*;
import com.ajh.flow.dto.location.LocationDetailDto;
import com.ajh.flow.dto.location.LocationLevelCheckDto;
import com.ajh.flow.dto.location.LocationRegisterDto;
import com.ajh.flow.dto.location.LocationSearchCond;
import com.ajh.flow.dto.user.UserSearchCond;
import com.ajh.flow.dto.warehouse.WarehouseDetailDto;
import com.ajh.flow.dto.warehouse.WarehouseRegisterDto;
import com.ajh.flow.dto.warehouse.WarehouseSearchCond;
import com.ajh.flow.dto.warehouse.WarehouseUpdateDto;
import com.ajh.flow.service.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    private final UserService userService;
    private final WarehouseService warehouseService;
    private final LocationService locationService;
    private final ItemService itemService;
    private final StockService stockService;
    private final HistoryService historyService;

    //-----------------메인페이지-----------------
    @GetMapping("/home")
    public String home() {
        return "admin/home";
    }
    //-----------------마이페이지-----------------
    @GetMapping("/detail")
    public String userDetail(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        model.addAttribute("user", principalDetails.getUser());
        return "user/detail";
    }
    //-----------------유저-----------------
    //유저 목록 확인
    @GetMapping("/users")
    public String userList(@PageableDefault(size = 10) Pageable pageable, Model model, UserSearchCond cond){
        model.addAttribute("cond", cond);
        model.addAttribute("page",userService.findAllWithPaging(pageable,cond));
        return "admin/user/userList";
    }
    //유저 히스토리 api
    @PostMapping("/user/disable/{id}")
    @ResponseBody
    public ResponseEntity<String> disableUser(
            @PathVariable("id") Long userId,
            @RequestParam("reason") String reason,
            @AuthenticationPrincipal PrincipalDetails principalDetails){
        userService.stopUserByAdmin(userId,reason,principalDetails.getUser());
        return ResponseEntity.ok("사용자의 계정이 비활성화 되었으며 이력이 기록되었습니다.");
    }
    @PostMapping("/user/enable/{id}")
    @ResponseBody
    public ResponseEntity<String> enableUser(
            @PathVariable("id") Long userId,
            @RequestParam("reason") String reason,
            @AuthenticationPrincipal PrincipalDetails principalDetails){
        userService.resumeUserByAdmin(userId,reason,principalDetails.getUser());
        return ResponseEntity.ok("사용자의 계정이 활성화 되었으며 이력이 기록되었습니다.");
    }

    //-----------------창고-----------------
    @GetMapping("/warehouses")
    public String warehouses(@PageableDefault(size = 10)Pageable pageable, Model model, WarehouseSearchCond cond){
        model.addAttribute("cond", cond);
        model.addAttribute("page",warehouseService.findAllWithPaging(pageable,cond));
        return "admin/warehouse/warehouseList";
    }
    @GetMapping("/warehouse/new")
    public String createForm(@AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        model.addAttribute("user", principalDetails.getUser());
        model.addAttribute("warehouseForm",new WarehouseRegisterDto());
        return "warehouse/new";
    }
    @PostMapping("/warehouse/new")
    public String create(@Valid WarehouseRegisterDto wareHouseRegisterDto,BindingResult result,
                         @AuthenticationPrincipal PrincipalDetails principalDetails,Model model) {
        if(result.hasErrors()) {
            model.addAttribute("user", principalDetails.getUser());
            model.addAttribute("warehouseForm",new WarehouseRegisterDto());
            log.error("창고 등록 중 오류가 발생했습니다. 사유 :{}",result);
            return "warehouse/new";
        }
        warehouseService.registerWarehouse(wareHouseRegisterDto);
        return "redirect:/admin/warehouses";
    }
    @GetMapping("/warehouse/detail/{warehouseId}")
    public String warehouseDetail(@PathVariable Long warehouseId, Model model) {
        Warehouse warehouse = warehouseService.findById(warehouseId);
        WarehouseDetailDto warehouseDetailDto = new WarehouseDetailDto(warehouse);
        model.addAttribute("warehouseDetail",warehouseDetailDto);

        return "warehouse/detail";
    }
    @GetMapping("/warehouse/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Warehouse warehouse = warehouseService.findById(id);
        model.addAttribute("warehouse", warehouse);
        model.addAttribute("warehouseId",id);
        model.addAttribute("useYn", warehouse.getUseYn());

        return "warehouse/edit";
    }
    @PostMapping("/warehouse/edit/{id}")
    public String edit(@PathVariable Long id, @Valid WarehouseUpdateDto warehouseUpdateDto, BindingResult result) {
        if(result.hasErrors()) {
            return "warehouse/edit";
        }
        warehouseService.updateWarehouse(id, warehouseUpdateDto);

        return  "redirect:/admin/warehouse/detail/"+id;
    }
    //-----------------로케이션-----------------
    @GetMapping("/locations")
    public String locations(@PageableDefault(size = 10)Pageable pageable, Model model, LocationSearchCond cond){
        model.addAttribute("warehouseList", warehouseService.findAll());
        model.addAttribute("zoneList",LocationZone.values());
        model.addAttribute("cond", cond);
        model.addAttribute("page",locationService.findAllWidthPaging(pageable,cond));
        return "admin/location/locationList";
    }
    @GetMapping("/location/new")
    public String createLocationForm(Model model) {
        model.addAttribute("locationForm", new LocationRegisterDto());
        model.addAttribute("locationZone", LocationZone.values());
        model.addAttribute("useYn", UseYn.values());
        model.addAttribute("warehouseList", warehouseService.findInboundAbleWarehouses());

        return "location/new";
    }
    @PostMapping("/location/new")
    public String createLocation(@Valid @ModelAttribute("locationForm") LocationRegisterDto dto, BindingResult result, Model model) {
        if(result.hasErrors()){
            model.addAttribute("locationZone", LocationZone.values());
            model.addAttribute("useYn", UseYn.values());
            model.addAttribute("warehouseList", warehouseService.findInboundAbleWarehouses());
            return "location/new";
        }
        if (dto.getWarehouseId() == null || dto.getSelectedLevels() == null || dto.getSelectedLevels().isEmpty()) {
            result.reject("globalError", "등록할 단(Level)을 최소 하나 이상 선택해 주세요.");
            return "location/new"; // 로케이션 등록 폼 HTML 이름
        }
        locationService.registerLocation(dto);
        return  "redirect:/admin/location/new";
    }
    @GetMapping("/api/locations/check-loc-code")
    @ResponseBody
    public List<LocationLevelCheckDto> checkLocation(
            @RequestParam("warehouseId") Long warehouseId,
            @RequestParam("zone") String zone,
            @RequestParam("row") String row,
            @RequestParam("col") String col
    ){
        List<String> existingLevels = locationService.findExistingLevels(warehouseId,zone,row,col);
        Set<String> existingSet = new HashSet<>(existingLevels);

        List<LocationLevelCheckDto> result = new ArrayList<>();

        for(int level = 1; level <= 10; level++){
            String formattedLevel = String .format("%02d", level);

            boolean isRegistered = existingSet.contains(formattedLevel);

            result.add(new LocationLevelCheckDto(zone,row,col,formattedLevel,isRegistered));
        }

        return result;

    }
    @GetMapping("/location/detail/{id}")
    public String locationDetail(Model model, @PathVariable Long id){
        Location location = locationService.findById(id);
        LocationDetailDto dto = new LocationDetailDto(location);
        model.addAttribute("location", dto);

        return "admin/location/detail";
    }
    @GetMapping("/location/excel")
    public void downloadLocationExcel(LocationSearchCond cond, HttpServletResponse response) throws IOException {
        List<LocationDetailDto> locations = locationService.getExcelDownloadList(cond);


        String fileName = URLEncoder.encode("창고_로케이션_목록", StandardCharsets.UTF_8);
        response.setContentType("text/scv; charset=MS949");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".csv");

        PrintWriter writer = response.getWriter();

        writer.println("순번,창고명,구역(Zone),로케이션 코드,사용가능 여부");

        int index = 1;
        for (LocationDetailDto loc : locations) {
            writer.println(String.format("%d,%s,%s,%s,%s,",
                    index++,
                    loc.getWarehouseName(),
                    loc.getZone().name(),
                    loc.getLocCode(),
                    loc.getUseYn().name()
                    ));
        }

        writer.flush();
        writer.close();

    }

    //-----------------상품 및 재고 통합-----------------
    @GetMapping("/items")
    public String items(@PageableDefault(size = 10)Pageable pageable, Model model, ItemSearchCond cond){
        model.addAttribute("cond", cond);
        model.addAttribute("page",itemService.findAllDetailDto(pageable,cond));
        return "admin/item/itemList";
    }
    //상품 등록 페이지
    @GetMapping("/item/new")
    public String createItemForm(Model model) {
        model.addAttribute("itemForm", new ItemRegisterDto());
        model.addAttribute("itemUnits", ItemUnit.values());
        return "items/new";
    }
    //상품 등록 처리
    @PostMapping("/item/new")
    public String createItem(@Valid @ModelAttribute("itemForm") ItemRegisterDto form, BindingResult result,Model model) {
        if(result.hasErrors()) {
            model.addAttribute("itemUnits", ItemUnit.values());
            return "items/new";
        }
        itemService.registerItem(form);
        return "redirect:/admin/items";
    }
    //상품 상세 페이지
    @GetMapping("/item/detail/{id}")
    public String itemDetail(Model model,@PathVariable Long id) {
        Item item = itemService.findById(id);
        ItemDetailDto itemDetailDto = new ItemDetailDto(item);
        model.addAttribute("itemDetail", itemDetailDto);

        return "items/detail";
    }
    @GetMapping("/item/detail_list/{id}")
    public String stocks(@PathVariable("id") Long itemId, @PageableDefault Pageable pageable, ItemLocationSearchCond cond, Model model){
        model.addAttribute("cond", cond);
        model.addAttribute("item",itemService.findById(itemId));
        model.addAttribute("warehouses",warehouseService.findAll());
        model.addAttribute("locations",locationService.findAll());
        model.addAttribute("zones",LocationZone.values());
        model.addAttribute("status", StockStatus.values());
        model.addAttribute("page",stockService.findItemWidthPaging(itemId,pageable, cond));
        return "admin/item/itemDetail";
    }
    //상품 정보 수정 페이지
    @GetMapping("/item/edit/{id}")
    public String itemEditForm(@PathVariable Long id, Model model) {
        Item item = itemService.findById(id);
        ItemUpdateDto updateDto = new ItemUpdateDto(item);
        model.addAttribute("itemForm", updateDto);
        model.addAttribute("itemId", id);
        model.addAttribute("itemUnits", ItemUnit.values());
        return "items/edit";
    }

    //상품 정보 수정 처리
    @PostMapping("/item/edit/{id}")
    public String itemEdit(@PathVariable Long id, @Valid @ModelAttribute("itemForm") ItemUpdateDto form, BindingResult result) {
        if(result.hasErrors()) {
            return "items/edit";
        }
        itemService.updateItem(id,form);

        return "redirect:/admin/items";
    }

    //-----------------히스토리-----------------
    @GetMapping("/histories/user")
    public String historyList(@PageableDefault(size = 10)Pageable pageable, Model model, HistorySearchCond cond) {
        model.addAttribute("cond", cond);
        model.addAttribute("page",historyService.getUserHistoryList(cond,pageable));
        return "admin/history/userHistoryList";
    }
    @GetMapping("/histories/stock")
    public String stockList(@PageableDefault(size = 10) Pageable pageable, Model model, HistorySearchCond cond) {
        model.addAttribute("cond", cond);
        model.addAttribute("warehouses",warehouseService.findAll());
        model.addAttribute("page",historyService.getStockHistoryList(cond,pageable));
        return "admin/history/stockHistoryList";
    }
}
