package com.boyworld.carrot.api.service.cart;

import com.boyworld.carrot.api.controller.cart.response.CartOrderResponse;
import com.boyworld.carrot.api.controller.cart.response.CartResponse;
import com.boyworld.carrot.api.service.cart.dto.CreateCartMenuDto;
import com.boyworld.carrot.domain.cart.Cart;
import com.boyworld.carrot.domain.cart.CartMenu;
import com.boyworld.carrot.domain.cart.CartMenuOption;
import com.boyworld.carrot.domain.cart.CartType;
import com.boyworld.carrot.domain.cart.repository.CartRepository;
import com.boyworld.carrot.domain.foodtruck.FoodTruck;
import com.boyworld.carrot.domain.foodtruck.repository.command.FoodTruckRepository;
import com.boyworld.carrot.domain.member.Member;
import com.boyworld.carrot.domain.member.repository.command.MemberRepository;
import com.boyworld.carrot.domain.menu.Menu;
import com.boyworld.carrot.domain.menu.MenuOption;
import com.boyworld.carrot.domain.menu.repository.command.MenuOptionRepository;
import com.boyworld.carrot.domain.menu.repository.command.MenuRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.boyworld.carrot.domain.cart.CartType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final FoodTruckRepository foodTruckRepository;
    private final MemberRepository memberRepository;
    private final MenuRepository menuRepository;
    private final MenuOptionRepository menuOptionRepository;

    public Long createCart(CreateCartMenuDto createCartMenuDto, String email) throws JsonProcessingException {

//        Member member = getMemberByEmail(email);
//
//        FoodTruck foodTruck = getFoodTruckById(createCartMenuDto.getFoodTruckId());
//
//        Menu menu = getMenuById(createCartMenuDto.getMenuId());

        if (checkFieldKey(CART.getText(), email)) {
            // 회원 카트가 존재하는 경우
            Cart cart = getData(CART.getText(), email, Cart.class);
            if (cart.getFoodTruckId().equals(createCartMenuDto.getFoodTruckId())) {
                // 푸드트럭이 같은 경우
                // 장바구니의 총 금액 및 cartMenuIds 업데이트
                String cartMenuId = saveCartMenuAndMenuOption(createCartMenuDto, email);
                saveUpdateCart(createCartMenuDto, cartMenuId, cart, email);

            } else {
                // 장바구니에 추가한 메뉴의 푸드트럭이 다른 경우
                // 관련 메뉴 및 메뉴옵션 들 삭제하기
                for (String cartMenuPk : cart.getCartMenuIds()) {
                    CartMenu cartMenu = getData(CARTMENU.getText(), cartMenuPk, CartMenu.class);
                    log.debug("CartService#createCart#getCartMenu#getCartId: {}", cartMenu.getCartId());
                    log.debug("CartService#createCart#getCartMenu#getId: {}", cartMenu.getId());
                    for (String cartMenuOptionPk : cartMenu.getCartMenuOptionIds()) {
                        // null
                        deleteCartMenuOption(cartMenuOptionPk);
                    }
                    deleteCartMenu(cartMenuPk);
                }
                String cartMenuId = saveCartMenuAndMenuOption(createCartMenuDto, email);
                saveNewCart(createCartMenuDto, cartMenuId, email);
            }
        } else {
            // 회원 카트가 존재하지 않으면 카트 추가, 메뉴 추가, 메뉴 옵션 추가
            String cartMenuId = saveCartMenuAndMenuOption(createCartMenuDto, email);
            saveNewCart(createCartMenuDto, cartMenuId, email);
        }

        return createCartMenuDto.getMenuId();
    }

    public CartResponse getCart(String email) {
        return null;
    }

    public Long editCartMenu(Long cartMenuId, String email) {
        return null;
    }

    public Long removeCartMenu(Long cartMenuId, String email) {
        return null;
    }

    public CartOrderResponse getCartOrder(String email) {
        return null;
    }


    public String saveCartMenuAndMenuOption(CreateCartMenuDto createCartMenuDto, String email) {
        RedisAtomicLong cartMenuIndex = new RedisAtomicLong("cartMenuId", redisTemplate.getConnectionFactory());
        String cartMenuId = String.valueOf(cartMenuIndex.incrementAndGet());
        // 메뉴 고유 pk값 생성
        List<String> cartMenuOptionIds = new ArrayList<>();
        for (Long menuOptionId : createCartMenuDto.getMenuOptionIds()) {
            RedisAtomicLong cartMenuOptionIndex = new RedisAtomicLong("cartMenuOptionId", redisTemplate.getConnectionFactory());
            String cartMenuOptionId = String.valueOf(cartMenuOptionIndex.incrementAndGet());
            // 메뉴 옵션 고유 pk값 생성
            cartMenuOptionIds.add(cartMenuOptionId);

            CartMenuOption cartMenuOption = CartMenuOption.builder()
                    .id(cartMenuOptionId)
                    .cartMenuId(cartMenuId)
                    .menuOptionId(menuOptionId)
                    .name("name은 find한 메뉴옵션 이름을 넣어야함")
                    .price(999)
                    .build();

            saveData(CARTMENUOPTION.getText(), cartMenuOptionId, cartMenuOption);
            log.debug("saveCartMenuAndMenuOption#save MenuOption: {}", cartMenuOption.getMenuOptionId());
        }
        CartMenu cartMenu = CartMenu.builder()
                .id(cartMenuId)
                .cartId(email)
                .menuId(createCartMenuDto.getMenuId())
                .name("name은 find한 메뉴 이름을 넣어야함")
                .price(999)
                .quantity(createCartMenuDto.getCartMenuQuantity())
                .menuImageUrl("menuImageUrl은 find한 메뉴이미지url을 넣어야함")
                .cartMenuOptionIds(cartMenuOptionIds)
                .build();

        saveData(CARTMENU.getText(), cartMenuId, cartMenu);
        log.debug("saveCartMenuAndMenuOption#save Menu: {}", cartMenu.getMenuId());
        return cartMenuId;
    }

    public void saveNewCart(CreateCartMenuDto createCartMenuDto, String cartMenuId, String email) {
        Cart cart = Cart.builder()
                .id(email)
                .foodTruckId(createCartMenuDto.getFoodTruckId())
                .foodTruckName("foodTruckName은 find한 푸드트럭 이름을 넣어야함")
                .totalPrice(createCartMenuDto.getCartMenuPrice())
                .cartMenuIds(Arrays.asList(cartMenuId))
                .build();
        saveData(CART.getText(), email, cart);
        log.debug("save Menu in Cart: {}", createCartMenuDto.getMenuOptionIds());
    }

    public void saveUpdateCart(CreateCartMenuDto createCartMenuDto, String CartMemberId, Cart cart, String email) {
        log.debug("before updateCart: {}", cart.getCartMenuIds().toString());
        cart.updateCartTotalPrice(createCartMenuDto.getCartMenuPrice());
        cart.updateCartMenuIds(CartMemberId);
        log.debug("after updateCart: {}", cart.getCartMenuIds().toString());
        saveData(CART.getText(), email, cart);
    }


    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));
    }

    private FoodTruck getFoodTruckById(Long foodTruckId) {
        return foodTruckRepository.findById(foodTruckId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 푸드트럭입니다."));
    }

    private Menu getMenuById(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 메뉴입니다."));
    }

    private MenuOption getMenuOptionById(Long menuOptionId) {
        return menuOptionRepository.findById(menuOptionId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 메뉴옵션입니다."));
    }


    public <T> boolean saveData(String key, String field, T data) {
        try {
            String value = objectMapper.writeValueAsString(data);
            redisTemplate.opsForHash().put(key, field, value);
            return true;
        } catch (Exception e) {
//            log.error(e);
            return false;
        }
    }

    public <T> T getData(String key, String field, Class<T> classType) throws JsonProcessingException {

        String jsonResult = (String) redisTemplate.opsForHash().get(key, field);
        if (jsonResult.isEmpty()) {
            return null;
        } else {
            T obj = objectMapper.readValue(jsonResult, classType);
            return obj;
        }
    }

    public Set getField(String key) throws JsonProcessingException {

        Set jsonResult = redisTemplate.opsForHash().keys(key);
        if (jsonResult.isEmpty()) {
            return null;
        } else {
            return jsonResult;
        }
    }

    public Map getEntries(String key) throws JsonProcessingException {

        Map jsonResult = redisTemplate.opsForHash().entries(key);
        if (jsonResult.isEmpty()) {
            return null;
        } else {
            return jsonResult;
        }
    }

    public boolean checkFieldKey(String hashKey, String fieldkey) {
        return redisTemplate.opsForHash().hasKey(hashKey, fieldkey);
    }

    public void deleteCartMenuOption(String fieldkey) {
        redisTemplate.opsForHash().delete(CARTMENUOPTION.getText(), fieldkey);
    }

    public void deleteCartMenu(String fieldkey) {
        redisTemplate.opsForHash().delete(CARTMENU.getText(), fieldkey);
    }

    public void deleteCart(String fieldkey) {
        redisTemplate.opsForHash().delete(CART.getText(), fieldkey);
    }

}
