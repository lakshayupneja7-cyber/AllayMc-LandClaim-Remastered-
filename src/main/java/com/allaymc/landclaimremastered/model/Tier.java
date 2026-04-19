package com.allaymc.landclaimremastered.model;

public enum Tier {
    I(1, 200),
    II(2, 500),
    III(3, 1000),
    IV(4, 2000),
    V(5, 4000),
    VI(6, 7000),
    VII(7, 12000),
    VIII(8, 20000),
    IX(9, 30000),
    X(10, 40000);

    private final int level;
    private final int requiredBlocks;

    Tier(int level, int requiredBlocks) {
        this.level = level;
        this.requiredBlocks = requiredBlocks;
    }

    public int getLevel() { return level; }
    public int getRequiredBlocks() { return requiredBlocks; }
}
