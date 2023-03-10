package com.team8.shop.tomatomarket.dto;

import com.team8.shop.tomatomarket.entity.SellerRequestForm;
import lombok.Getter;

@Getter
public class GetSellerWaitingsRespDto {
    private final Long id;
    private final String username;
    private final String introduce;
    private final boolean isApproval;

    public GetSellerWaitingsRespDto(SellerRequestForm sellerRequestForm){
        this.id = sellerRequestForm.getId();
        this.username = sellerRequestForm.getUser().getUsername();
        this.introduce = sellerRequestForm.getIntroduce();
        this.isApproval = sellerRequestForm.isApproval();
    }
}
