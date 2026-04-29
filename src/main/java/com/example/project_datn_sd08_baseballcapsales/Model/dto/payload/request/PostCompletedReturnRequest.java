package com.example.project_datn_sd08_baseballcapsales.Model.dto.payload.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostCompletedReturnRequest {

    private String note;
    private List<Item> items;

    @Getter
    @Setter
    public static class Item {
        private Integer orderDetailId;
        private Integer quantity;
        private String note;
    }
}