package com.ajh.flow.service;

import com.ajh.flow.common.constant.ItemUnit;
import com.ajh.flow.common.constant.LocationZone;
import com.ajh.flow.common.constant.StockStatus;
import com.ajh.flow.common.constant.UserRole;
import com.ajh.flow.common.exception.EntityNotFoundException;
import com.ajh.flow.common.exception.InsufficientStockException;
import com.ajh.flow.common.exception.InvalidStockException;
import com.ajh.flow.domain.Stock;
import com.ajh.flow.domain.User;
import com.ajh.flow.dto.item.ItemRegisterDto;
import com.ajh.flow.dto.location.LocationRegisterDto;
import com.ajh.flow.dto.stock.StockMoveDto;
import com.ajh.flow.dto.stock.StockRegisterDto;
import com.ajh.flow.dto.stock.StockUpdateDto;
import com.ajh.flow.dto.user.UserRegisterDto;
import com.ajh.flow.dto.warehouse.WarehouseRegisterDto;
import com.ajh.flow.repository.StockRepository;
import com.ajh.flow.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class StockServiceTest {
    @Autowired private StockService stockService;
    @Autowired private WarehouseService warehouseService;
    @Autowired private LocationService locationService;
    @Autowired private ItemService itemService;
    @Autowired private UserService userService;

    @Autowired private StockRepository stockRepository;
    @Autowired private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    private Long locationId;
    private Long locationId2;
    private Long locationId3;
    private Long itemId;
    private Long itemId2;
    private Long itemId3;

    private User user;

    Long userId;
    Long warehouseId;
    List<String> levels1 = new ArrayList<>();
    List<String> levels2 = new ArrayList<>();

    @BeforeEach
    void setUp() {
        levels1.add("01");
        levels2.add("02");
        //유저 등록
        userId = userService.registerUser(new UserRegisterDto("millionfood@naver.com","12312312312","안진혁", UserRole.USER,"01038041915"));
        user = userRepository.findById(userId).get();
        //창고 생성
        warehouseId = warehouseService.registerWarehouse(new WarehouseRegisterDto("통영창고", "통영시 구닥로", userId));
        //로케이션 생성
        locationId = locationService.registerLocationOne(new LocationRegisterDto(warehouseId, LocationZone.COLD,"01","01",levels1));
        locationId2 = locationService.registerLocationOne(new LocationRegisterDto(warehouseId, LocationZone.COLD,"01","01",levels2));
        //상품 생성
        itemId = itemService.registerItem(new ItemRegisterDto("사과",1000L,ItemUnit.EA,"두쫀쿠사과"));
        itemId2 = itemService.registerItem(new ItemRegisterDto("배",1500L,ItemUnit.EA,"두바이배"));
    }

    @Test
    @DisplayName("최초 입고시 Stock Entity 생성")
    public void addNewStock() throws Exception{
        //Given - 상품 등록
        Long stockId = stockService.registerStock(new StockRegisterDto(warehouseId,itemId, locationId, 1000L, StockStatus.AVAILABLE));
        //When
        em.flush();
        em.clear();
        //Then
        assertThat(stockService.findById(stockId).getQuantity()).isNotEqualTo(100L);
        assertThat(stockService.findById(stockId).getQuantity()).isEqualTo(1000L);

    }
    @Test
    @DisplayName("같은 상태의 아이템 입고시 재고 추가,다른 상태의 아이템 입고시 신규입고")
    public void addNewStock2() throws Exception{
        //Given - 상품 등록
        Long oldStatusStockId = stockService.registerStock(new StockRegisterDto(warehouseId,itemId, locationId, 1000L, StockStatus.AVAILABLE));
        Long sameStatusStockId = stockService.registerStock(new StockRegisterDto(warehouseId,itemId, locationId, 1000L, StockStatus.AVAILABLE));
        Long diffStatusStockId = stockService.registerStock(new StockRegisterDto(warehouseId,itemId, locationId, 1000L, StockStatus.DAMAGED));
        //When
        em.flush();
        em.clear();
        //Then
        assertThat(oldStatusStockId).isEqualTo(sameStatusStockId);
        assertThat(stockService.findById(oldStatusStockId).getQuantity()).isEqualTo(2000L);
        assertThat(stockService.findById(diffStatusStockId).getQuantity()).isEqualTo(1000L);

    }

    @Test
    @DisplayName("stock 업데이트가 정상적으로 이루어져야 한다.")
    public void updateStock() throws Exception{
        //Given
        Long stockId = stockService.registerStock(new StockRegisterDto(warehouseId,itemId, locationId, 1000L, StockStatus.AVAILABLE));
        em.flush();
        em.clear();
        //When
        stockService.updateStock(stockId,new StockUpdateDto(2000L,StockStatus.AVAILABLE),user);
        em.flush();
        em.clear();
        //Then
        assertThat(stockService.findById(stockId).getQuantity()).isEqualTo(2000L);

    }


    @Test
    @DisplayName("재고의 수량보다 이동하려는 수량이 많으면 실패해야 한다.")
    public void moveStock_fail_insufficientQuantity() throws Exception{
        //Given
        //Stock 객체 하나 등록
        Long oldStockId = stockService.registerStock(new StockRegisterDto(warehouseId,itemId, locationId, 1000L, StockStatus.AVAILABLE));
        StockMoveDto dto =  new StockMoveDto(itemId,locationId,1100L,StockStatus.AVAILABLE,"재고 이동");
        //When
        em.flush();
        em.clear();
        //Then
        assertThrows(InsufficientStockException.class,()->stockService.moveStock(oldStockId,dto,user));

    }
    @Test
    @DisplayName("목적지 로케이션 또는 아이템이 사용 불가이면 이동은 실패해야 한다.")
    public void moveStock_fail_InvalidItemOrLocation() throws Exception{
        //Given - Stock 등록 후 moveDto 생성
        Long oldStockId = stockService.registerStock(new StockRegisterDto(warehouseId,itemId, locationId, 1000L, StockStatus.AVAILABLE));
        StockMoveDto dto1 =  new StockMoveDto(itemId,locationId2,1000L,StockStatus.AVAILABLE,"재고 이동");
        StockMoveDto dto2 =  new StockMoveDto(itemId2,locationId,1000L,StockStatus.AVAILABLE,"재고 이동");
        //item2,location2의 useYn = UseYn.N
        itemService.stopUseItem(itemId2);
        locationService.stopUseLocation(locationId2);
        //When
        em.flush();
        em.clear();
        //Then
        //존재하지 않는 로케이션으로 이동
        assertThrows(InvalidStockException.class,()->stockService.moveStock(oldStockId,dto1,user));
        //존재하지 않는 아이템을 이동
        assertThrows(InvalidStockException.class,()->stockService.moveStock(oldStockId,dto2,user));

    }
    @Test
    @DisplayName("목적지 로케이션에 다른 아이템이 있으면 재고 이동은 실패해야 한다.")
    public void moveStock_fail_otherItemExist() throws Exception{
        //Given - Stock 객체 두개 등록(서로 다른 로케이션, 서로 다른 아이템)
        Long oldStockId = stockService.registerStock(new StockRegisterDto(warehouseId,itemId, locationId, 1000L, StockStatus.AVAILABLE));
        stockService.registerStock(new StockRegisterDto(warehouseId,itemId2, locationId2, 1000L, StockStatus.AVAILABLE));
        //다른 아이템이 있는 로케이션으로 이동하는 dto 생성
        StockMoveDto dto =  new StockMoveDto(itemId,locationId2,1000L,StockStatus.AVAILABLE,"재고 이동");
        //When
        em.flush();
        em.clear();
        //Then
        assertThrows(InvalidStockException.class,()->stockService.moveStock(oldStockId,dto,user));

    }


    @Test
    @DisplayName("목적지 로케이션에 같은 상태의 아이템이 있으면 해당 재고의 수량이 추가되어야 한다.")
    public void moveStock_success_sameStatusAndSameItemExist() throws Exception{
        //Given - Stock 객체 두개 등록(서로 다른 로케이션, 서로 다른 아이템)
        Long oldStockId = stockService.registerStock(new StockRegisterDto(warehouseId,itemId, locationId, 1000L, StockStatus.AVAILABLE));
        Long newStockId = stockService.registerStock(new StockRegisterDto(warehouseId,itemId, locationId2, 1000L, StockStatus.AVAILABLE));
        //같은 아이템이 있는 로케이션으로 이동하는 dto
        StockMoveDto dto =  new StockMoveDto(itemId,locationId2,900L,StockStatus.AVAILABLE,"재고 이동");
        StockMoveDto dto2 =  new StockMoveDto(itemId,locationId2,100L,StockStatus.AVAILABLE,"재고 이동");

        //When
        em.flush();
        em.clear();
        //Then
        //1000L +/- 900L = 1900L / 100L
        stockService.moveStock(oldStockId,dto,user);
        Stock oldStock = stockRepository.findById(oldStockId).orElse(null);
        Stock newStock = stockRepository.findById(newStockId).orElse(null);
        assertThat(oldStock.getQuantity()).isEqualTo(100L);
        assertThat(newStock.getQuantity()).isEqualTo(1900L);
        em.flush();
        em.clear();
        //100L -/+ 100L = 2000L / null
        stockService.moveStock(oldStockId,dto2,user);
        Stock oldStock2 = stockRepository.findById(oldStockId).orElse(null);
        Stock newStock2 = stockRepository.findById(newStockId).orElse(null);
        assertThat(oldStock2).isNull();
        assertThat(newStock2.getQuantity()).isEqualTo(2000L);
    }
    @Test
    @DisplayName("목적지 로케이션에 다른 상태의 아이템이 있으면 새로운 재고가 추가되어야 한다.")
    public void moveStock_success_diffStatusAndSameItemExist() throws Exception{
        //Given - Stock 객체 두개 등록(서로 다른 로케이션, 서로 다른 상태의 같은 아이템)
        Long oldStockId = stockService.registerStock(new StockRegisterDto(warehouseId,itemId, locationId, 1000L, StockStatus.AVAILABLE));
        Long newStockId = stockService.registerStock(new StockRegisterDto(warehouseId,itemId, locationId2, 1000L, StockStatus.DAMAGED));
        //다른 아이템이 있는 로케이션으로 이동하는 dto 생성
        StockMoveDto dto =  new StockMoveDto(itemId,locationId2,1000L,StockStatus.AVAILABLE,"재고 이동");
        //When
        em.flush();
        em.clear();
        //Then
        Long moveStockId = stockService.moveStock(oldStockId,dto,user);
        //기존 재고는 null
        assertThrows(EntityNotFoundException.class,()->stockService.findById(oldStockId));
        //다른 로케이션의 기존 재고는 기존과 동일한 양
        Stock diffLocationOldStock = stockService.findById(newStockId);
        assertThat(diffLocationOldStock.getQuantity()).isEqualTo(1000L);
        //다른 로케이션의 새로운 재고는 기존의 재고와 동일한 양
        Stock diffLocationNewStock = stockService.findById(moveStockId);
        assertThat(diffLocationNewStock.getQuantity()).isEqualTo(1000L);


    }
    @Test
    @DisplayName("목적지 로케이션에 상품이 없으면 새로운 재고가 추가되어야 한다.")
    public void moveStock_success_emptyLocation() throws Exception{
        //Given - Stock 객체 두개 등록(서로 다른 로케이션, 서로 다른 상태의 같은 아이템)
        Long oldStockId = stockService.registerStock(new StockRegisterDto(warehouseId,itemId, locationId, 1000L, StockStatus.AVAILABLE));
        //다른 아이템이 있는 로케이션으로 이동하는 dto 생성
        StockMoveDto dto =  new StockMoveDto(itemId,locationId2,500L,StockStatus.AVAILABLE,"재고 이동");
        //When
        em.flush();
        em.clear();
        Long newStockId = stockService.moveStock(oldStockId,dto,user);
        //Then
        Stock oldStock = stockService.findById(oldStockId);
        Stock newStock = stockService.findById(newStockId);
        assertThat(oldStock.getQuantity()).isEqualTo(500L);
        assertThat(newStock.getQuantity()).isEqualTo(500L);
    }

    @Test
    @DisplayName("재고는 정상적으로 삭제되어야 한다.")
    public void deleteStock() throws Exception{
        //Given
        Long stockId = stockService.registerStock(new StockRegisterDto(warehouseId,itemId, locationId, 1000L, StockStatus.AVAILABLE));
        //When
        em.flush();
        em.clear();
        //Then
        stockService.deleteStock(stockId,user);
        assertThrows(EntityNotFoundException.class,()->stockService.findById(stockId));

    }
}