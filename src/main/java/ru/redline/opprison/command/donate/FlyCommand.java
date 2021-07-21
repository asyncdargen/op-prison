package ru.redline.opprison.command.donate;

import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.redline.core.bukkit.command.BukkitCommand;
import ru.redline.core.global.group.PermissionGroup;
import ru.redline.opprison.OpPrison;
import ru.redline.opprison.utils.PermUtil;

public class FlyCommand extends BukkitCommand<Player> {

    public FlyCommand() {
        super("fly", "полёт");
    }

    protected void onExecute(Player player, String[] args) {
        if (!player.hasPermission("opprison.fly")) {
            OpPrison.send(player, "&cУ вас не куплен этот донат. &6/donate");
            return;
        }
        if (args.length < 1) {
            player.setAllowFlight(player.getAllowFlight());
            OpPrison.send(player, "Вы %s&f режим полёта", player.getAllowFlight() ? "&aвключили" : "&cвыключили");
        } else if (PermUtil.hasGroup(player, PermissionGroup.ADMIN)) {
            val target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                OpPrison.send(player, "Игрок &c%s&f оффлайн", args[0]);
            } else {
                target.setAllowFlight(target.getAllowFlight());
                OpPrison.send(player, "Вы %s&f режим полёта игроку &a%s",
                        target.getName(), player.getAllowFlight() ? "&aвключили" : "&cвыключили");
            }
        } else {
            player.setAllowFlight(player.getAllowFlight());
            OpPrison.send(player, "Вы %s&f режим полёта", player.getAllowFlight() ? "&aвключили" : "&cвыключили");
        }
    }
}
