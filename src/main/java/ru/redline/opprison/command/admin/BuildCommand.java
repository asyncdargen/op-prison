package ru.redline.opprison.command.admin;

import org.bukkit.entity.Player;
import ru.redline.core.bukkit.command.BukkitCommand;
import ru.redline.core.global.group.PermissionGroup;
import ru.redline.opprison.OpPrison;
import ru.redline.opprison.utils.PermUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BuildCommand extends BukkitCommand<Player> {

    public static List<UUID> builders = new ArrayList<>();

    public BuildCommand() {
        super("build");
    }

    protected void onExecute(Player player, String[] strings) {
        if (!PermUtil.hasGroup(player, PermissionGroup.ADMIN)) OpPrison.send(player, "&cУ вас недостатачно прав");
        else {
            if (!builders.remove(player.getUniqueId()))
                builders.add(player.getUniqueId());
            OpPrison.send(player,"Вы %s&r режим строительства", (builders.contains(player.getUniqueId()) ? "&aвключили" : "&cвыключили"));
        }
    }
}
