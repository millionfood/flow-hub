package com.ajh.flow;

import com.ajh.flow.common.constant.ItemUnit;
import com.ajh.flow.common.constant.LocationZone;
import com.ajh.flow.common.constant.UserRole;
import com.ajh.flow.domain.Item;
import com.ajh.flow.domain.Location;
import com.ajh.flow.domain.User;
import com.ajh.flow.domain.Warehouse;
import com.ajh.flow.repository.ItemRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class InitDbRunner implements CommandLineRunner {

    private final InitService initService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initService.dbInit();
    }


    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService{
        private final EntityManager em;
        private final PasswordEncoder passwordEncoder;

        private final ItemRepository itemRepository;

        public void dbInit(){
            Long userCount = em.createQuery("select count(u) from User u", Long.class).getSingleResult();
            if(userCount>0){
                log.info("기존 데이터가 존재하여 더미 데이터 생성을 생략합니다.");
                return;
            }
            log.info("WMS 더미 데이터 생성을 시작합니다...");

            String rawPw = "1111";
            String encodedPw = passwordEncoder.encode(rawPw);
            //--------------------사용자 대량 생성----------------------
            //관리자 생성
            User adminUser = User.builder()
                    .email("admin@gmail.com")
                    .password(encodedPw)
                    .name("관리자")
                    .role(UserRole.ADMIN)
                    .tel("01038041915")
                    .build();
            em.persist(adminUser);
            //일반 유저 생성
            List<User> userList = new ArrayList<>();
            for(int i = 1; i<=20;i++){
                User user = User.builder()
                        .email("user"+i+"@gmail.com")
                        .password(encodedPw)
                        .name("일반유저"+i)
                        .role(UserRole.USER)
                        .tel("0101234"+String.format("%04d",i))
                        .build();
                em.persist(user);
                userList.add(user);
            }

            //--------------------창고 대량 생성----------------------
            List<Warehouse> warehouseList = new ArrayList<>();
            String[] regions = {"부산", "서울", "인천", "대구", "광주", "대전", "울산", "경기", "강원", "충북"};

            for(int i = 0; i < regions.length; i++){
                Warehouse warehouse = Warehouse.builder()
                        .name(regions[i])
                        .address(regions[i]+"시 외곽 물류단지" + (i+1)+"로")
                        .register(adminUser)
                        .build();
                em.persist(warehouse);
                warehouseList.add(warehouse);
            }
            //--------------------로케이션 대량 생성----------------------
            // 구역 4개 리스트를 배열로 미리 땡겨옵니다.
            LocationZone[] zones = LocationZone.values(); // [COLD, FRIDGE, ROOM, HAZARD] 순서

            for (Warehouse wh : warehouseList) {
                int locCount = 0; // 창고별 로케이션 카운터 (0 ~ 19)

                // 2행 * 2열 * 5단 = 창고당 총 20개 조합 생성
                for (int r = 1; r <= 2; r++) {     // 1행 ~ 2행
                    for (int c = 1; c <= 2; c++) { // 1열 ~ 2열
                        for (int l = 1; l <= 5; l++) { // 1단 ~ 5단

                            LocationZone zone = zones[locCount % 4];

                            Location loc = Location.builder()
                                    .warehouse(wh)
                                    .zone(zone)
                                    .row(String.format("%02d", r))
                                    .col(String.format("%02d", c))
                                    .level(String.format("%02d", l))
                                    .build();

                            em.persist(loc);
                            locCount++;
                        }
                    }
                }
            }

            //--------------------아이템 대량 생성----------------------
            List<Item> itemList = new ArrayList<>();
            String[] productNames = {
                    "새우깡", "양파링", "감자칩", "콘칩", "고래밥",
                    "빼빼로", "초코파이", "카스타드", "몽쉘", "꼬북칩"
            };
            for (int i = 1; i <= 20; i++) {
                String name = productNames[(i - 1) % productNames.length] + " " + ((i - 1) / 10 + 1) + "호";
                Item item = Item.builder()
                        .name(name)
                        .price(1000L + (i * 200L))
                        .unit(ItemUnit.BOX) // 🎯 엔티티에 정의된 Unit 명세 적용
                        .description(name + " 대량 보관용 박스 상품 상품 설명입니다.")
                        .build();

                String productCode = itemRepository.findLastProductCode();
                item.createFullBarcode(productCode);

                em.persist(item);
                itemList.add(item);
            }
            log.info("WMS 더미 데이터 생성이 완료되었습니다. [유저21명], [창고 10개], [아이템20개], [로케이션200개]");
        }
    }
}
