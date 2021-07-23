package ru.redline.opprison.region;

import io.netty.util.internal.ConcurrentSet;
import lombok.Getter;
import lombok.val;
import org.bukkit.Location;
import ru.redline.core.bukkit.CorePlugin;
import ru.redline.opprison.region.mine.MineManager;
import ru.redline.opprison.region.math.V3;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class RegionManager {

    private final RegionGuard guard;
    private final RegionConfig config;
    private final MineManager mines;

    @Getter
    private final Set<Region> regions = new ConcurrentSet<>();

    public RegionManager(CorePlugin plugin) {
        guard = new RegionGuard(plugin, this);
        mines = new MineManager();
        RegionConfig.manager = this;
        config = new RegionConfig();
    }

    public void clearRegions() {
        regions.clear();
    }

    public void addRegions(Region... regions) {
        this.regions.addAll(Arrays.asList(regions));
    }

    public void addRegions(List<Region> regions) {
        this.regions.addAll(regions);
    }

    public Region byId(String id) {
        for (Region rg : regions) {
            if (rg.getId().equals(id)) return rg;
        }
        return null;
    }

    public List<Region> getContainRegions(Location loc) {
        val point = new V3(loc.getBlockX(), loc.getBlockZ(), loc.getBlockY());
        return regions.stream().filter(r -> r.inRegion(point, loc.getWorld())).sorted().collect(Collectors.toList());
    }

    public Region getPrimaryRegion(Location loc) {
        return getContainRegions(loc).get(0);
    }


}
