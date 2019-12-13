package com.zcompany.example.domain.model;

import java.util.EnumSet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.xebia.util.export.Distinguishable;

public enum VDWType implements Distinguishable {

    // @formatter:off
    CASH("Cash"),
    TOM("Tom"),
    SPOT("Spot"),
    FUTURE("Future");
    // @formatter:on

    private final String description;

    private static final EnumSet<VDWType> ALL = EnumSet.allOf(VDWType.class);

    private VDWType(final String description) {
        this.description = description;
    }

    @Override
    public String label() {
        return this.description;
    }

    @Override
    public String descriminator() {
        return this.name();
    }

    @JsonCreator
    public static VDWType of(final String name) {
        for (VDWType e : values())
            if (e.name().equals(name))
                return e;
        throw new IllegalArgumentException();
    }

    public static EnumSet<VDWType> all() {
        return ALL;
    }

    public boolean isCash() {
        return this == CASH;
    }
}
