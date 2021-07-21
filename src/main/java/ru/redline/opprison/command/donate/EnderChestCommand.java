package ru.redline.opprison.command.donate;

import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.redline.core.bukkit.command.BukkitCommand;
import ru.redline.core.global.group.PermissionGroup;
import ru.redline.opprison.OpPrison;
import ru.redline.opprison.utils.PermUtil;

public class EnderChestCommand extends BukkitCommand<Player> {

    public EnderChestCommand() {
        super("ec", "enderchest");
    }

    protected void onExecute(Player player, String[] args) {
        if (!player.hasPermission("opprison.enderchest")) {
            OpPrison.send(player, "&cУ вас не куплен этот донат. &6/donate");
            return;
        }
        if (args.length < 1)
            player.openInventory(player.getEnderChest());
        else if (PermUtil.hasGroup(player, PermissionGroup.ADMIN)) {
            val target = Bukkit.getPlayer(args[0]);
            if (target != null)
                player.openInventory(target.getEnderChest());
        } else player.openInventory(player.getEnderChest());
    }

}
