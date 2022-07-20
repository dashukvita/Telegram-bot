package com.dash.telegram.entity;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Currency {
    USD(431), EUR(451), RUB(456), BYN(0);

    private final int id;
}
