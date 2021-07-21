package ru.redline.opprison.command.admin;

import org.bukkit.entity.Player;
import ru.redline.core.bukkit.command.BukkitCommand;
import ru.redline.core.global.group.PermissionGroup;
import ru.redline.opprison.OpPrison;
import ru.redline.opprison.utils.PermUtil;

public class SaveCommand extends BukkitCommand<Player> {

    public SaveCommand() {
        super("save");
    }

    protected void onExecute(Player player, String[] strings) {
        if (!PermUtil.hasGroup(player, PermissionGroup.ADMIN)) OpPrison.send(player, "&cУ вас недостатачно прав");
        else {
            long start = System.currentTimeMillis();
            OpPrison.instance.getPlayerManager().run();
            OpPrison.send(player, "Игроки сохранены, времени потрачено &2%sms", System.currentTimeMillis() - start);
        }
    }

}