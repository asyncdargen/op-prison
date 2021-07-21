package ru.redline.opprison.command.admin;

import lombok.val;
import org.bukkit.entity.Player;
import ru.redline.core.bukkit.command.BukkitCommand;
import ru.redline.core.global.group.PermissionGroup;
import ru.redline.opprison.OpPrison;
import ru.redline.opprison.player.Rank;
import ru.redline.opprison.utils.PermUtil;

public class EcoCommand extends BukkitCommand<Player> {

    public EcoCommand() {
        super("eco");
    }

    protected void onExecute(Player player, String[] args) {
        if (!PermUtil.hasGroup(player, PermissionGroup.ADMIN)) OpPrison.send(player, "&cУ вас недостатачно прав");
        else {
            boolean execute = true;
            if (args.length < 3)
                execute = false;
            else {
                String type = args[0].toLowerCase();
                val target = OpPrison.instance.getPlayerManager().getPlayer(args[1].toLowerCase());
                Double count;
                try {
                    count = Double.parseDouble(args[2]);
                } catch (Exception e) {
                    execute = false;
                    return;
                }
                if (target == null) {
                    OpPrison.send(player, "Игрок &c%s&f оффлайн", args[1]);
                    return;
                }
                switch (type) {
                    case "crystal":
                        target.setCrystals(count); break;
                    case "money":
                        target.setMoney(count); break;
                    case "prestige":
                        target.setPrestige(count); break;
                    case "token":
                        target.setTokens(count); break;
                    case "rank":
                        target.setRank(Rank.fromOrdinal(count.intValue())); break;
                    case "multiplier":
                        target.setMultiplier(count); break;
                    default:
                        execute = false;
                }
                if (execute) OpPrison.send(player, "Вы установили значение &a%s&f на &a%s&f игроку &a%s",
                        type, type.equals("rank") ? Rank.fromOrdinal(count.intValue()) : count, target.getHandle().getName());
            }
            if (!execute)
                OpPrison.send(player, "&a/eco &7<&acrystal,token,prestige,money,rank,multiplier&7> <&aигрок&7> <&aкол-во&7> &7- &f Установить значение игроку");
        }
    }
}
