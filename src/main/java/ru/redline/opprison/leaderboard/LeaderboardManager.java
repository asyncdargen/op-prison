package ru.redline.opprison.leaderboard;

import lombok.val;
import org.bukkit.Bukkit;
import ru.redline.opprison.OpPrison;
import ru.redline.opprison.utils.Configuration;
import ru.redline.opprison.utils.LocationUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class LeaderboardManager extends Configuration {

    private Map<Leader, Leaderboard> boards;

    public LeaderboardManager() {
        super("leaders");
        Bukkit.getScheduler().runTaskTimer(OpPrison.instance, this::update, 0, 20 * TimeUnit.MINUTES.toSeconds(5));
    }

    public void onLoad() {
        if (boards == null) boards = new ConcurrentHashMap<>();
        Arrays.stream(Leader.values()).forEach(l -> {
            val section = getConfigurationSection("leaderboards." + l.name());
            Leaderboard board = boards.get(l);
            if (section == null) {
                if (board != null) boards.remove(l).getHologram().remove();
                return;
            }

            if (board == null)
                boards.put(l, Leaderboard.builder()
                        .type(l)
                        .format(section.getString("format"))
                        .title(section.getString("title"))
                        .location(LocationUtil.fromString(section.getString("location"),
                                Bukkit.getWorlds().get(0).getSpawnLocation()))
                        .build());
            else {
                board.setFormat(section.getString("format"));
                board.setTitle(section.getString("title"));
                board.updateLocation(LocationUtil.fromString(section.getString("location"),
                        Bukkit.getWorlds().get(0).getSpawnLocation()));
                board.updateHologram();
            }
        });
    }

    public void update() {
        boards.values().forEach(l -> l.getType().match(l));
    }

}
