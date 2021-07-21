package ru.redline.opprison.pickaxe;

import lombok.val;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import ru.redline.core.bukkit.CorePlugin;
import ru.redline.opprison.OpPrison;
import ru.redline.opprison.player.OpPlayer;

public class UpdaterTask extends BukkitRunnable {

    public UpdaterTask(CorePlugin pl) {
        runTaskTimer(pl, 10, 40);
    }

    public void run() {
        val items = OpPrison.instance.getGameItems();
        OpPrison.instance.getPlayerManager().getCache().values().forEach(d -> {
            val handle = d.getHandle();
            val pickaxe = items.getItem("pickaxe", d);
            if (!items.getItemName(handle.getInventory().getItem(0)).equals("pickaxe"))
                handle.getInventory().setItem(0, pickaxe);
            else handle.getInventory().getItem(0).setItemMeta(pickaxe.getItemMeta());
            if (handle.getInventory().getHeldItemSlot() == 0) effects(d);
        });
    }

    public static void effects(OpPlayer player) {
        val handle = player.getHandle();
        val pickaxe = player.getPickaxe();
        if (pickaxe.getEnchant(PickaxeEnchant.HASTE) > 1)
            handle.addPotionEffect(
                    new PotionEffect(
                            PotionEffectType.FAST_DIGGING, 100,
                            pickaxe.getEnchant(PickaxeEnchant.HASTE), false, false
                    ), true
            );
        if (pickaxe.getEnchant(PickaxeEnchant.N_VISION) > 1)
            handle.addPotionEffect(
                    new PotionEffect(
                            PotionEffectType.NIGHT_VISION, 100,
                            pickaxe.getEnchant(PickaxeEnchant.N_VISION), false, false
                    ), true
            );
        if (pickaxe.getEnchant(PickaxeEnchant.SPEED) > 1)
            handle.addPotionEffect(
                    new PotionEffect(
                            PotionEffectType.SPEED, 100,
                            pickaxe.getEnchant(PickaxeEnchant.SPEED), false, false
                    ), true
            );
    }

}
