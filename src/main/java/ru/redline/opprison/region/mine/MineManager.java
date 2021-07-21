package ru.redline.opprison.region.mine;

import io.netty.util.internal.ConcurrentSet;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import ru.redline.opprison.OpPrison;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class MineManager {

    @Getter
    private final Set<Mine> mines = new ConcurrentSet<>();
    private final BukkitTask updater = Bukkit.getScheduler().runTaskTimer(OpPrison.instance, this::update, 20, 20);

    public void update() {
        if (mines.isEmpty()) return;
        mines.stream().filter(Mine::expired).forEach(Mine::update);
    }

    public void clearMines() {
        mines.clear();
    }

    public void addMines(Mine... mines) {
        addMines(Arrays.asList(mines));
    }

    public void addMines(List<Mine> mines) {
        this.mines.addAll(mines);
        mines.forEach(Mine::update);
    }

    public Mine byId(String id) {
        for (Mine mine : mines) {
            if (mine.getId().equals(id)) return mine;
        }
        return null;
    }

}
