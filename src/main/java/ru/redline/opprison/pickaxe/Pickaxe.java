package ru.redline.opprison.pickaxe;

import lombok.Data;
import lombok.val;
import org.bukkit.entity.Player;
import ru.redline.opprison.utils.MathUtil;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Data
public class Pickaxe {

    private String name;
    private int level = 1;
    private double exp = 0;
    private ConcurrentMap<PickaxeEnchant, Integer> enchants = new ConcurrentHashMap<>();

    public Pickaxe(Player own) {
        name = "§fКирка §2§l" + own.getName();
        Arrays.stream(PickaxeEnchant.values()).filter(e -> e.getLevel() > 0).forEach(e -> enchants.put(e, e.getLevel()));
    }

    public int getEnchant(PickaxeEnchant e) {
       return enchants.getOrDefault(e, 0);
    }

    public void addExp(double add) {
        if ((exp += add) >= getNeedExp()) level++;
    }

    public double getNeedExp() {
        return MathUtil.getPickaxeExpPrice(getNextLevel());
    }

    public int getNextLevel() {
        return level < 1 && level != 0 ? 1 : level + 1;
    }

}
