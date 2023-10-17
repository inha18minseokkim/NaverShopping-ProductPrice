package com.example.navershoppingproductprice.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiReceiveResponse {
    private String lastBuildDate;
    private String total;
    private String start;
    private String display;
    private List<ApiReceiveItem> items;
}
