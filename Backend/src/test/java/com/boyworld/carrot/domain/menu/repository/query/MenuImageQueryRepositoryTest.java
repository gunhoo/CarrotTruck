package com.boyworld.carrot.domain.menu.repository.query;

import com.boyworld.carrot.IntegrationTestSupport;
import com.boyworld.carrot.domain.foodtruck.Category;
import com.boyworld.carrot.domain.foodtruck.FoodTruck;
import com.boyworld.carrot.domain.foodtruck.repository.command.CategoryRepository;
import com.boyworld.carrot.domain.foodtruck.repository.command.FoodTruckRepository;
import com.boyworld.carrot.domain.member.Member;
import com.boyworld.carrot.domain.member.Role;
import com.boyworld.carrot.domain.member.repository.command.MemberRepository;
import com.boyworld.carrot.domain.menu.Menu;
import com.boyworld.carrot.domain.menu.MenuImage;
import com.boyworld.carrot.domain.menu.MenuInfo;
import com.boyworld.carrot.domain.menu.repository.command.MenuImageRepository;
import com.boyworld.carrot.domain.menu.repository.command.MenuRepository;
import com.boyworld.carrot.file.UploadFile;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 메뉴 이미지 조회 레포지토리 테스트
 *
 * @author 최영환
 */
@Slf4j
class MenuImageQueryRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private MenuImageQueryRepository menuImageQueryRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FoodTruckRepository foodTruckRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuImageRepository menuImageRepository;

    @DisplayName("메뉴 식별키로 메뉴 이미지를 조회한다.")
    @Test
    void getMenuImageByMenuId() {
        // given
        Member vendor = createMember(Role.VENDOR, "ssafy@gmail.com");

        Category category = createCategory("고기/구이");

        FoodTruck foodTruck = createFoodTruck(vendor, category, "동현 된장삼겹", "010-1234-5678",
                "돼지고기(국산), 고축가루(국산), 참깨(중국산), 양파(국산), 대파(국산), 버터(프랑스)",
                "된장 삼겹 구이 & 삼겹 덮밥 전문 푸드트럭",
                40,
                10,
                true);

        Menu menu = Menu.builder()
                .foodTruck(foodTruck)
                .menuInfo(MenuInfo.builder().name("메뉴1").price(10000).description("메뉴 설명1").soldOut(false).build())
                .active(true)
                .build();
        menu = menuRepository.save(menu);

        MenuImage menuImage = MenuImage.builder()
                .menu(menu)
                .uploadFile(UploadFile.builder().storeFileName("testMenuImgFile").uploadFileName("test_menu.png").build())
                .active(true)
                .build();
        menuImageRepository.save(menuImage);

        // when
        MenuImage result = menuImageQueryRepository.getMenuImageByMenuId(menu.getId());

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUploadFile()).extracting("storeFileName", "uploadFileName")
                .containsExactly(menuImage.getUploadFile().getStoreFileName(),
                        menuImage.getUploadFile().getUploadFileName());
    }

    private Member createMember(Role role, String email) {
        Member member = Member.builder()
                .email(email)
                .nickname("매미킴")
                .encryptedPwd(passwordEncoder.encode("ssafy1234"))
                .name("김동현")
                .phoneNumber("010-1234-5678")
                .role(role)
                .active(true)
                .build();
        return memberRepository.save(member);
    }

    private Category createCategory(String name) {
        Category category = Category.builder()
                .name(name)
                .active(true)
                .build();
        return categoryRepository.save(category);
    }

    private FoodTruck createFoodTruck(Member member, Category category, String name, String phoneNumber,
                                      String content, String originInfo, Integer prepareTime,
                                      Integer waitLimits, Boolean selected) {
        FoodTruck foodTruck = FoodTruck.builder()
                .vendor(member)
                .category(category)
                .name(name)
                .phoneNumber(phoneNumber)
                .content(content)
                .originInfo(originInfo)
                .prepareTime(prepareTime)
                .waitLimits(waitLimits)
                .selected(selected)
                .active(true)
                .build();
        return foodTruckRepository.save(foodTruck);
    }
}