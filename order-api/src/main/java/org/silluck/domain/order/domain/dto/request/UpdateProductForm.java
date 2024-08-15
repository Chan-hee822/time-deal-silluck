package org.silluck.domain.order.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductForm {
    private String id;  // product Id
    private String name;
    private String description;
    private List<UpdateProductItemForm> items;
}