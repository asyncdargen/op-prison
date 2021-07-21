package ru.redline.opprison.command;

import lombok.val;
import org.bukkit.entity.Player;
import ru.redline.core.bukkit.command.BukkitCommand;
import ru.redline.opprison.OpPrison;
import ru.redline.opprison.inventory.DefaultMenus;
import ru.redline.opprison.utils.MathUtil;
import ru.redline.opprison.utils.formatter.DoubleFormatter;

public class PrestigeCommand extends BukkitCommand<Player> {


    public PrestigeCommand() {
        super("prestige", "престиж");
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        if (args.length > 0)
            if (args[0].equalsIgnoreCase("menu")) DefaultMenus.prestige(OpPrison.instance.getPlayerManager().getPlayer(player));
            else OpPrison.send(player, "&a/prestige menu &7- &fМеню покупки престижа");
        else {
            val data = OpPrison.instance.getPlayerManager().getPlayer(player);
            val price = MathUtil.getPrestigePrice(data.getPrestige(), 0);
            if (data.getMoney() < price)
                OpPrison.send(player, "Вам не хватает &c$%s&r для покупки престижа", DoubleFormatter.comma(price - data.getMoney()));
            else {
                data.setMoney(data.getMoney() - price);
                data.setPrestige(data.getPrestige() + 1);
                OpPrison.send(player, "Вы купили престиж %s&r за &a$%s", DoubleFormatter.comma(data.getPrestige()), DoubleFormatter.comma(price));
            }
        }
    }
}
