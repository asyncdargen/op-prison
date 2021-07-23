package ru.redline.opprison.region;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import ru.redline.opprison.region.math.V3;

import java.util.List;
import java.util.Map;

public class GlobalRegion extends Region {

    public GlobalRegion(int weight, Map<RegionParameter, Boolean> parameters) {
        super("__global__", weight, null, null, null, parameters);
    }

    @Override
    public List<Player> getPlayers() {
        return ((List<Player>) Bukkit.getOnlinePlayers());
    }

    public boolean inRegion(V3 check, World world) {
        return true;
    }

    public boolean inRegion(Player player) {
        return true;
    }
}
