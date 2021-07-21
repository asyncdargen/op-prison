package ru.redline.opprison.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import ru.redline.core.global.database.query.RemoteDatabaseQueryResult;
import ru.redline.opprison.OpPrison;
import ru.redline.opprison.inventory.ItemBuilder;
import ru.redline.opprison.player.Rank;
import ru.redline.opprison.utils.formatter.DoubleFormatter;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Getter
@AllArgsConstructor
public enum Leader {

    BLOCKS(l -> {
        order("blocks").thenAccept(rs -> {
           val contents = new LinkedList<LeaderComponent>();
           int pos = 0;
           while (rs.next() && pos < 10)
               contents.add(new LeaderComponent(++pos, rs.getString("name"), DoubleFormatter.comma(rs.getDouble("blocks"))));
           l.updateContents(contents);
        });
    }, ItemBuilder.of(Material.GOLD_BLOCK).enchant(Enchantment.ARROW_DAMAGE, 1)),
    PRESTIGE(l -> {
        order("prestige").thenAccept(rs -> {
            val contents = new LinkedList<LeaderComponent>();
            final int[] pos = {0};
            while (rs.next() && pos[0] < 10)
                if (rs.getDouble("prestige") > 0 && Rank.fromOrdinal(rs.getInt("rank")).isMax())
                    contents.add(new LeaderComponent(++pos[0], rs.getString("name"), DoubleFormatter.comma(rs.getDouble("prestige"))));

            if (contents.size() < 10)
                order("rank").thenAccept(rrs -> {
                    while (rrs.next() && pos[0] < 10)
                        if (!contents.stream().anyMatch(c -> c.getPlayer().equalsIgnoreCase(rrs.getString("name"))))
                            contents.add(new LeaderComponent(++pos[0], rrs.getString("name"), Rank.fromOrdinal(rrs.getInt("rank")).getName()));
                    l.updateContents(contents);
                });
            else l.updateContents(contents);
        });
    }, ItemBuilder.of(Material.GOLD_INGOT).enchant(Enchantment.ARROW_DAMAGE, 1));

    private final Consumer<Leaderboard> matcher;
    private final ItemStack drop;

    public void match(Leaderboard board) {
        if (matcher != null) matcher.accept(board);
    }

    protected static CompletableFuture<RemoteDatabaseQueryResult> order(String by) {
        return OpPrison.instance
                .getPlayerManager()
                .getHandler()
                .getExecuteHandler()
                .executeQuery(true, "SELECT `name`," + (by.equalsIgnoreCase("prestige") ? " `rank`," : "") + " `" + by + "` FROM `players` ORDER BY `" + by + "` DESC LIMIT 10");
    }
}
