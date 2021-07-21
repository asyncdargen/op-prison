package ru.redline.opprison.leaderboard;

import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;
import ru.redline.core.bukkit.ApiManager;
import ru.redline.core.bukkit.holographic.ProtocolHolographic;
import ru.redline.core.bukkit.util.ChatUtil;
import ru.redline.core.global.database.RemoteDatabaseConnectionHandler;
import ru.redline.core.global.database.RemoteDatabaseTable;
import ru.redline.opprison.OpPrison;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Builder(buildMethodName = "construct")
@Getter @Setter
public class Leaderboard {

    private final Leader type;

    private String title;
    private String format;
    private Location location;

    @Builder.Default
    private List<LeaderComponent> context = new ArrayList<>();
    private ProtocolHolographic hologram;

    public void updateContents(List<LeaderComponent> contents) {
        setContext(contents);
        updateHologram();
    }

    public void updateLocation(Location loc) {
        setLocation(loc);
        hologram.teleport(location);
        hologram.update();
    }

    public void updateTitle(String title) {
        setTitle(title);
        updateHologram();
    }

    public void updateFormat(String format) {
        setFormat(format);
        updateHologram();
    }

    public void updateHologram() {
        hologram.setTextLine(0, ChatUtil.color(title));
        for (int i = 1; i < 11; i++) {
            hologram.setTextLine(
                    i, context.size() < i
                    ? "§f§oЗагрузка..."
                    : ChatUtil.text(
                            format, (i == 1 ? "§c" : i == 2 ? "§e" : i == 3 ? "§6" : "§7"),
                            i, context.get(i - 1).getPlayer(), context.get(i - 1).getValue()
                    ));
        }
        hologram.setDropLine(14, type.getDrop());
    }

    static class LeaderboardBuilder {

        public Leaderboard build() {
            hologram(ApiManager.createHologram(location));
            hologram.addTextLine(ChatUtil.color(title));
            for (int i = 0; i < 10; i++) hologram.addTextLine("§f§oЗагрузка...");
            hologram.addEmptyLine();
            hologram.addEmptyLine();
            hologram.spawn();
            val lb = construct();
            lb.updateHologram();
            return lb;
        }

    }



}
