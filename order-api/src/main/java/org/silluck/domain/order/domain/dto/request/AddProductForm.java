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
public class AddProductForm {
    private String name;
    private String description;
    // 아이템들 리스트 (form을 따로 만들어서 혹은 아래 static class를 만들어서)
    // 아이템만 변경이 될 수 있기 때문에 폼으로
    private List<AddProductItemForm> items;
}
