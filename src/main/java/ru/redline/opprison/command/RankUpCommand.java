package ru.redline.opprison.command;

import lombok.val;
import org.bukkit.entity.Player;
import ru.redline.core.bukkit.command.BukkitCommand;
import ru.redline.opprison.OpPrison;
import ru.redline.opprison.utils.formatter.DoubleFormatter;

public class RankUpCommand extends BukkitCommand<Player> {

    public RankUpCommand() {
        super("rankup", "ru");
    }

    protected void onExecute(Player player, String[] strings) {
        val data = OpPrison.instance.getPlayerManager().getPlayer(player);
        val rank = data.getRank();
        if (rank.isMax()) OpPrison.send(player, "Вы достигли максимального ранга! §7(%s§7)", rank.getName());
        else if (data.getMoney() < rank.next().getPrice())
            OpPrison.send(player, "Вам не хватает &c$%s&r для покупки ранга %s", DoubleFormatter.comma(rank.next().getPrice() - data.getMoney()),  rank.next().getName());
        else {
            data.setMoney(data.getMoney() - rank.next().getPrice());
            data.setRank(rank.next());
            OpPrison.send(player, "Вы купили ранг %s§r за §a$%s", rank.next().getName(), DoubleFormatter.comma(rank.next().getPrice()));
        }
    }

}
