package org.silluck.domain.domain.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserType {
    CUSTOMER("ROLE_CUSTOMER"), SELLER("ROLE_SELLER");

    private final String key;
}
