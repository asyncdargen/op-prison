package ru.redline.opprison.pickaxe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum PickaxeEnchant {

    EFFICIENCY("§7Эффективность", EnchantType.DEFAULT,
            0, 10, 300, 26000, Material.ANVIL,
            new String[]{"Увеличивает скорость копания"}),
    MINER("§7Копатель", EnchantType.DEFAULT,
            0, 0, 800000, 1000, Material.DIAMOND,
            new String[]{"Увеличивает количство добываемых", "денег, в зависимости от уровня"}),
    //Effects
    HASTE("§dСпешка", EnchantType.DEFAULT,
            0, 0, 7, 30000, Material.GLOWSTONE_DUST,
            new String[]{"Даёт эффект ускоренного копания,", "когда кирка в руке"}),
    SPEED("§aСкорость", EnchantType.DEFAULT,
            0, 0, 7, 30000, Material.SUGAR,
            new String[]{"Даёт эффект ускоренной ходьбы,", "когда кирка в руке"}),
    N_VISION("§3Ночное зрение", EnchantType.DEFAULT,
            0, 0, 1, 30000, Material.REDSTONE,
            new String[]{"Даёт эффект ночного зрения,", "когда кирка в руке"}),
    //Other
    TOKEN_MINER("§eДобытчик Токенов", EnchantType.DEFAULT,
            0, 0, 25000, 25000, Material.DOUBLE_PLANT,
            new String[]{"Увеличивает количство добываемых", "токенов, в зависимости от уровня"}),
    LUCKY("§9Везунчик", EnchantType.DEFAULT,
            0, 0, 35, 2200000, Material.EMERALD,
            new String[]{"С некоторым шансом можно получить:", "ключи, чек или токены"}),
    KEY_FINDER("§4Добытчик Ключей", EnchantType.DEFAULT,
            0, 0, 25, 1000000000, Material.TRIPWIRE_HOOK,
            new String[]{"С некоторым шансом можно получить", "ключи разных типов"}),
    EXPLOSIVE("§cПодрывник", EnchantType.DEFAULT,
            0, 0, 600, 7050000, Material.TNT,
            new String[]{"С некоторым шансом, можно сломать", "блоки в радиусе 5x5x5"}),
    BOOSTER("§9Бустер", EnchantType.PRESTIGE,
            20, 0, 30, 100, Material.GOLD_INGOT,
            new String[]{"С некоторым шансом, можно получить бустер х2", "на всё, время зависит от уровня"}),
    EXCAVATOR("§4Экскаватор", EnchantType.DEFAULT,
            25, 0, 700, 900000000, Material.DIAMOND_PICKAXE,
            new String[]{"С каждым уровенем, шанс на ломание", "слоя шахты - увеличивается"}),
    PRESTIGE_FINDER("§5Добытчик Престижей", EnchantType.DEFAULT,
            50, 0, 1500, 7000000000d, Material.BEACON,
            new String[]{"С некоторым шансом, можно найти", "престижи"}),
    TOKEN_MERCHANT("§eЖадность Токенов", EnchantType.DEFAULT,
            65, 0, 3000, 850000000, Material.ENDER_PEARL,
            new String[]{"С некоторым шансом, можно умножить", "добытые токены"}),
    PRESTIGE_MERCHANT("§2Жадность Престижей", EnchantType.DEFAULT,
            100, 0, 6000, 5000000000d, Material.EYE_OF_ENDER,
            new String[]{"С некоторым шансом, можно умножить", "добытые пристижи"}),
    DONATE("§d§lЧто-то", EnchantType.DEFAULT,
            90, 0, 0, 0, Material.BOOK,
            new String[]{"§fДарген: §7дум, придумай донат чар, до завтра", "§fДум: §7до завтра"})
    ;

    private final String name;
    private final EnchantType type;
    private final int need;
    private final int level;
    private final int max;
    private final double price;
    private final Material material;
    private final String[] description;

    public static List<PickaxeEnchant> getFor(EnchantType type) {
        return Arrays.stream(values()).filter(e -> e.getType() == type).collect(Collectors.toList());
    }

    @Getter
    @AllArgsConstructor
    public
    enum EnchantType {

        DEFAULT("§bОбычные", Material.EXP_BOTTLE,
                new int[]{
                        19, 20, 21, 22, 23, 24, 25,
                            29, 30, 31, 32, 33,
                                39, /*40,*/ 41
        }),
        PRESTIGE("§cПрестиж", Material.REDSTONE, new int[]{31});

        private final String name;
        private final Material material;
        private final int[] slots;

    }
}
