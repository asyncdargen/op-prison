package ru.redline.opprison.command.admin;

import org.bukkit.entity.Player;
import ru.redline.core.bukkit.command.BukkitCommand;
import ru.redline.core.global.group.PermissionGroup;
import ru.redline.opprison.OpPrison;
import ru.redline.opprison.utils.PermUtil;

public class UpdateTopCommand extends BukkitCommand<Player> {

    public UpdateTopCommand() {
        super("top");
    }

    protected void onExecute(Player player, String[] strings) {
        if (!PermUtil.hasGroup(player, PermissionGroup.ADMIN)) OpPrison.send(player, "&cУ вас недостатачно прав");
        else {
            OpPrison.instance.getLeaderboardManager().update();
            OpPrison.send(player, "Топы обновлены");
        }
    }

}