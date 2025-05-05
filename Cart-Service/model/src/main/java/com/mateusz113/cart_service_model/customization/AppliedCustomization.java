package com.mateusz113.cart_service_model.customization;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AppliedCustomization {
    private Long id;
    private Long sourceId;
    private String name;
    private Boolean multipleChoice;
    private List<AppliedCustomizationOption> appliedOptions;
}
