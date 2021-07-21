package ru.redline.opprison.region;

import lombok.Builder;
import lombok.Data;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import ru.redline.opprison.region.point.PointPos;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder
@Data
public class Region implements Comparable<Region> {

    private final String id;
    private final int weight;
    private final PointPos min;
    private final PointPos max;
    private final World world;
    private final Map<RegionParameter, Boolean> parameters;

    public boolean getParameter(RegionParameter parameter) {
        return parameters.getOrDefault(parameter, parameter.isDefaultState());
    }

    public void toggleParameter(RegionParameter parameter, boolean state) {
        parameters.put(parameter, state);
    }

    public boolean inRegion(PointPos check, World world) {
        return world.equals(this.world) &&
                min.getX() <= check.getX() && check.getX() <= max.getX() &&
                min.getY() <= check.getY() && check.getY() <= max.getY() &&
                min.getZ() <= check.getZ() && check.getZ() <= max.getZ();
    }

    public boolean inRegion(Player player) {
        val loc = player.getLocation();
        return inRegion(PointPos.builder().x(loc.getBlockX()).y(loc.getBlockY()).z(loc.getBlockZ()).build(), loc.getWorld());
    }

    public List<Player> getPlayers() {
        return Bukkit.getOnlinePlayers().stream().filter(this::inRegion).collect(Collectors.toList());
    }

    public int compareTo(Region rg) {
        return (rg.weight < weight) ? -1 : ((weight == rg.weight) ? 0 : 1);
    }
}
