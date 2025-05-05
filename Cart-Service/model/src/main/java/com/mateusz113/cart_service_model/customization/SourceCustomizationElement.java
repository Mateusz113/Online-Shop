package com.mateusz113.cart_service_model.customization;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SourceCustomizationElement {
    private Long id;
    private String name;
    private Boolean multipleChoice;
    private List<SourceCustomizationOption> options;
}
