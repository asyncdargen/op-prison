package ru.redline.opprison.region;

import lombok.val;
import org.bukkit.Bukkit;
import ru.redline.opprison.region.mine.Mine;
import ru.redline.opprison.region.mine.ResourceBlock;
import ru.redline.opprison.region.point.PointPos;
import ru.redline.opprison.utils.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.Math.*;

public class RegionConfig extends Configuration {

    protected static RegionManager manager;

    public RegionConfig() {
        super("regions");
    }

    public void onLoad() {
        val regions = new ArrayList<Region>();
        val regionsSection = getConfigurationSection("regions");
        long start = System.currentTimeMillis();
        System.out.println("§7Loading regions...");
        regionsSection.getKeys(false).forEach(n -> {
            val cs = regionsSection.getConfigurationSection(n);
            int minX, minY, minZ, maxX, maxY, maxZ;
            try {
                val min = Arrays.stream(cs.getString("min").split(" "))
                        .map(Integer::parseInt).collect(Collectors.toList());
                val max = Arrays.stream(cs.getString("max").split(" "))
                        .map(Integer::parseInt).collect(Collectors.toList());
                minX = min.get(0);
                minY = min.get(1);
                minZ = min.get(2);
                maxX = max.get(0);
                maxY = max.get(1);
                maxZ = max.get(2);
            } catch (Throwable e) {
                return;
            }
            int weight = cs.getInt("weight");
            val world = Bukkit.getWorld(cs.getString("world"));
            val params = cs.getStringList("parameters");
            if (world == null) return;
            val parameters = RegionParameter.fromList(params);
            regions.add(Region.builder().id(n.toLowerCase()).parameters(parameters).world(world).weight(weight).max(
                    PointPos.builder().x(max(minX, maxX)).y(max(minY, maxY)).z(max(minZ, maxZ)).build()).min(
                    PointPos.builder().x(min(minX, maxX)).y(min(minY, maxY)).z(min(minZ, maxZ)).build()).build());
        });
        regions.add(new GlobalRegion(getInt("global.weight"), RegionParameter.fromList(getStringList("global.parameters"))));
        manager.clearRegions();
        manager.addRegions(regions);
        System.out.println("§7Loaded " + regions.size() + " regions! §2Took " + (System.currentTimeMillis() - start) + "ms");

        System.out.println("§7Loading mines...");
        start = System.currentTimeMillis();
        val mines = new ArrayList<Mine>();
        val minesSection = getConfigurationSection("mines");
        minesSection.getKeys(false).forEach(n -> {
            val ms = minesSection.getConfigurationSection(n);
            int update = ms.getInt("update");
            val mineRegions = ms.getStringList("regions")
                    .stream().map(manager::byId).filter(Objects::nonNull).collect(Collectors.toList());
            val content = ResourceBlock.fromList(ms.getStringList("content"));
            val upperContent = ResourceBlock.fromString(ms.getString("upperContent"));
            if (update == 0) return;

            mines.add(Mine.builder().id(n.toLowerCase()).regions(mineRegions).content(content).upperContent(upperContent).update(update).build());
        });
        manager.getMINES().clearMines();
        manager.getMINES().addMines(mines);
        System.out.println("§7Loaded " + mines.size() + " mines! §2Took " + (System.currentTimeMillis() - start) + "ms");
    }
}
