package com.mateusz113.order_service_model.product;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Customization {
    private Long id;
    private String name;
    private Boolean multipleChoice;
    private List<CustomizationOption> options;
}
