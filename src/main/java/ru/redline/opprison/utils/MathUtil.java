package ru.redline.opprison.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.bukkit.block.Block;
import ru.redline.opprison.pickaxe.PickaxeEnchant;
import ru.redline.opprison.player.OpPlayer;
import ru.redline.opprison.player.Rank;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

@UtilityClass
public class MathUtil {


    protected static final Random rnd = ThreadLocalRandom.current();

    public final static Function<Double, Double> PRESTIGE = l -> 1_000_000_000D * (l == 1 ? 1 : l * 1.07);
    public final static Function<Double, Double> PICKAXE = l -> 2500D * (l == 2 ? 1 : l * 1.12);
    public final static Function<Double, Double> RANK = l -> l > Rank.values().length - 1 ? 0 : Rank.fromOrdinal(l.intValue()).getPrice();
    public final static BiFunction<Double, Double, Double> TOKEN_ENCHANT = (l, p) -> p * (l == 1 ? 1 : l * 1.057);
    public final static BiFunction<Double, Double, Double> CRYSTAL_ENCHANT = (l, p) -> p * (l == 1 ? 1 : l * 0.89);


    public static double getPickaxeExpPrice(double level) {
        return getPrice(level, 0, PICKAXE);
    }

    public static double getPrestigePrice(double level, double offset) {
        return getPrice(level, offset, PRESTIGE);
    }

    public static double getCanBuyPrestige(double level, double balance) {
        double price = 0;
        int levels = 0;
        while (true) {
            price += getPrestigePrice(levels + level + 1, 1);
            if (price > balance) break;
            levels++;
        }
        return levels;
    }

    public static double getRankPrice(double level, double offset) {
        return getPrice(level, offset, RANK);
    }

    public static double getCanBuyRank(int level, double balance) {
        double price = 0;
        int levels = 0;
        while (levels + level + 1 < Rank.values().length) {
            price += Rank.fromOrdinal(levels + 1 + level).getPrice();
            if (price > balance) break;
            levels++;
        }
        return levels;
    }

    public static double getEnchantPrice(double level, double offset, PickaxeEnchant ench) {
        double price = 0;
        level += 1;
        double count = level;
        val func = ench.getType() == PickaxeEnchant.EnchantType.DEFAULT ? TOKEN_ENCHANT : CRYSTAL_ENCHANT;
        do price += func.apply(count++, ench.getPrice());
        while (count < level + offset);
        return price;
    }

    public static double getCanBuyEnchant(double level, double balance, PickaxeEnchant ench) {
        double price = 0;
        double levels = 0;
        while (levels + level + 1 < ench.getMax() + 1) {
            price += getEnchantPrice(levels + level, 0, ench);
            if (price > balance) break;
            levels++;
        }
        return levels;
    }

    public static double getPrice(double level, double offset, Function<Double, Double> generator) {
        double price = 0;
        level += 1;
        double count = level;
        do price += generator.apply(count++);
        while (count < level + offset);
        return price;
    }

    private final static double token = 20;

    public static double getBlockTokens(OpPlayer player) {
        double miner = player.getPickaxe().getEnchant(PickaxeEnchant.TOKEN_MINER);
        double merchant = player.getPickaxe().getEnchant(PickaxeEnchant.TOKEN_MERCHANT);
        return (hasBooster(player) ? 2 : 1) * ((merchant > 0 && rnd.nextInt(200) <= 0
                ? merchant * 0.7 + (merchant < 2 ? 1 : 0) : 1) * (token + miner * 0.85 * token));
    }

    private final static double block = 400;

    public static double getBlockPrice(OpPlayer player, Block breaked) {
        double fortune = player.getPickaxe().getEnchant(PickaxeEnchant.MINER);
        double price = (fortune * (player.getRank().ordinal() * 56 + block) + block);
        return (hasBooster(player) ? 2 : 1) * (price + player.getFullMultiplier() * price);
    }

    public static double getDropExp(OpPlayer data) {
        return (hasBooster(data) ? 2 : 1) * (rnd.nextInt(3) + 2 + rnd.nextDouble());
    }

    public static double getBlockPrestige(OpPlayer data) {
        double miner = data.getPickaxe().getEnchant(PickaxeEnchant.PRESTIGE_FINDER);
        double merchant = data.getPickaxe().getEnchant(PickaxeEnchant.PRESTIGE_MERCHANT);
        return miner == 0 ? 0 : (int) (rnd.nextInt(7000) <= 0
                ? (miner * 0.1) * ((int) (rnd.nextInt(5000) <= 0 ? (merchant * 0.1) < 1 ? 1 : (merchant * 0.1) : 1)) : 0);
    }

    private static final Cache<UUID, Long> boosters = CacheBuilder.newBuilder()
            .expireAfterWrite(3, TimeUnit.MINUTES).build();

    public static boolean procBooster(OpPlayer data) {
        val is = data.getPickaxe().getEnchant(PickaxeEnchant.BOOSTER) > 0 && rnd.nextInt(3500) <= 0 && !hasBooster(data);
        if (is)
            boosters.put(data.getHandle().getUniqueId(),
                    data.getPickaxe().getEnchant(PickaxeEnchant.BOOSTER) * 5000L + System.currentTimeMillis());
        return is;
    }

    public static boolean hasBooster(OpPlayer data) {
        return boosters.asMap().getOrDefault(data.getHandle().getUniqueId(), 0l) > System.currentTimeMillis();
    }
}
