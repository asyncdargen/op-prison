package ru.redline.opprison.player;

import lombok.Getter;

@Getter
public enum Rank {

    A("§a", 0), B("§a", 100_000),
    C("§a", 250_000), D("§2", 500_000),
    E("§2", 750_000), F("§2", 1_000_000),
    G("§b", 2_500_000), H("§b", 5_000_000),
    I("§b", 7_500_000), J("§3", 10_000_000),
    K("§3", 15_000_000), L("§3", 20_000_000),
    M("§9", 25_000_000), N("§9", 30_000_000),
    O("§9", 55_000_000), P("§e", 70_000_000),
    Q("§e", 100_000_000), R("§e", 250_000_000),
    S("§6", 500_000_000), T("§6", 750_000_000),
    U("§6", 1_000_000_000), V("§c", 2_500_000_000d),
    W("§c", 5_000_000_000d), X("§c", 7_500_000_000d),
    Y("§4", 10_000_000_000d), Z("§4§l", 15_000_000_000d);

    private final String color;
    private final String name;
    private final double price;

    Rank(String color, double price) {
        this.color = color;
        name = color + super.name();
        this.price = price;
    }

    public boolean has(Rank rank) {
        return ordinal() >= rank.ordinal();
    }

    public static Rank fromOrdinal(int ordinal) {
        return (ordinal < 0 || ordinal > values().length - 1) ? A : values()[ordinal];
    }

    public static Rank fromNameOrOrdinal(String noo) {
        try {
            return fromOrdinal(Integer.parseInt(noo));
        } catch (NumberFormatException e) {
            try {
                return valueOf(noo.toUpperCase());
            } catch (IllegalArgumentException t) {
                return A;
            }
        }
    }

    public boolean isMax() {
        return ordinal() == values().length - 1;
    }

    public boolean isMin() {
        return ordinal() == 0;
    }

    public Rank next() {
        return fromOrdinal(ordinal() + 1);
    }

}
