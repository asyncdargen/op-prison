package ru.redline.opprison.command;

import org.bukkit.entity.Player;
import ru.redline.core.bukkit.command.BukkitCommand;
import ru.redline.opprison.OpPrison;
import ru.redline.opprison.inventory.DefaultMenus;

public class RankCommand extends BukkitCommand<Player> {

    public RankCommand() {
        super("rank", "ранг", "ранги", "ranks");
    }

    protected void onExecute(Player player, String[] strings) {
        DefaultMenus.rank(OpPrison.instance.getPlayerManager().getPlayer(player));
    }
}
