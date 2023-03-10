package com.team8.shop.tomatomarket.controller;

import com.team8.shop.tomatomarket.dto.*;
import com.team8.shop.tomatomarket.entity.User;
import com.team8.shop.tomatomarket.security.UserDetailsImpl;
import com.team8.shop.tomatomarket.service.SellerServiceImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SellerController {
    private final SellerServiceImpl sellerServiceImpl;

    // 판매자 전체목록 조회
    @GetMapping("/sellers")
    public List<GetSellerRespDto> getSellerList(@RequestParam Integer page, @RequestParam Integer size){
        PageableServiceReqDto serviceReqDto = new PageableServiceReqDto(page, size);
        return sellerServiceImpl.getSellerList(serviceReqDto);
    }

    // 판매자 정보 조회
    @GetMapping("/sellers/{sellerId}")
    public GetSellerRespDto getSellerBySellerId(@PathVariable Long sellerId){
        return sellerServiceImpl.getSellerBySellerId(sellerId);
    }

    @GetMapping("/sellers/users/{userId}")
    public GetSellerRespDto getSellerByUserId(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        if(!userDetails.isValidId(userId)){
            throw new IllegalArgumentException("본인의 정보만 조회가 가능합니다.");
        }
        return sellerServiceImpl.getSellerByUserId(userId);
    }

    // (판매자)나의 판매상품 조회
    @GetMapping("/sellers/products")
    public List<ProductResponseDto> getMyProductList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        // getMyProductList에서 user에 해당하는 productList를 반환해 준다
        return sellerServiceImpl.getMyProductList(userDetails.getUserId());
    }

    // #12 (판매자)판매 상품 등록
    @PostMapping("/sellers/products")
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductRequestDto productRequestDto,
                              @AuthenticationPrincipal UserDetailsImpl userDetails){
        CreateProductReqDto dto = new CreateProductReqDto(userDetails.getUserId(), productRequestDto.getName(), productRequestDto.getPrice(), productRequestDto.getDescription(), productRequestDto.getProductCategory());
        sellerServiceImpl.createProduct(dto);
    }

    // #12 (판매자)판매 상품 수정
    @PatchMapping("/sellers/products/{productId}")
    public void updateProduct(@PathVariable Long productId,
                              @RequestBody ProductRequestDto productRequestDto){
        sellerServiceImpl.updateProduct(productId, productRequestDto);
    }

    // #12(판매자)판매 상품 삭제
    @DeleteMapping("/sellers/products/{productId}")
    public void deleteProduct(@PathVariable Long productId) {
        sellerServiceImpl.deleteProduct(productId);
    }


    // #18 (판매자) 고객 요청 목록 조회
    @GetMapping("/sellers/quotations")
    public List<QuotationResponseDto> getQuotation(int page, int size, @AuthenticationPrincipal UserDetailsImpl userDetails){
        GetQuotationReqDto serviceReqDto = new GetQuotationReqDto(userDetails.getUserId(), page, size);
        return sellerServiceImpl.getQuotation(serviceReqDto);
    }

    // #18 (판매자) 고객 구매 요청 승인
    @PatchMapping("/sellers/quotations/{requestId}")
    public void approveQuotation(@PathVariable Long requestId){
        sellerServiceImpl.approveQuotation(requestId);
    }

    //(판매자) 프로필 설정
    @PatchMapping("/sellers/{userId}")
    public GetSellerRespDto setSellerProfile(@PathVariable Long userId, @RequestBody GetSellerReqDto getSellerReqDto , @AuthenticationPrincipal UserDetailsImpl userDetails){
        if(!userDetails.isValidId(userId)){
            throw new IllegalArgumentException("프로필 작성자와 일치하지 않습니다.");
        }
        SellerServiceDto sellerServiceDto = new SellerServiceDto(userId, getSellerReqDto.getIntroduce());
        return sellerServiceImpl.sellerUpdate(sellerServiceDto);
    }

    // 내부 함수 : {sellerId}와 로그인한 유저가 같은 사람인지 검증
    private void _checkId(Long sellerId, UserDetailsImpl userDetails){
        Long checkSellerUserId = sellerServiceImpl.getSellerBySellerId(sellerId).getUser().getId();
        if(!userDetails.isValidId(checkSellerUserId)){
            throw new IllegalArgumentException("등록된 정보와 일치하지 않습니다.");
        }
    }


}
