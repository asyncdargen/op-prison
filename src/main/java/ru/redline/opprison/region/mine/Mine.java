package ru.redline.opprison.region.mine;

import lombok.Builder;
import lombok.Data;
import lombok.val;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import ru.redline.opprison.region.Region;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Data
@Builder
public class Mine {

    private final static Random RND = new Random();

    private final String id;
    private final List<Region> regions;
    private final List<ResourceBlock> content;
    @Builder.Default
    private final ResourceBlock upperContent = null;

    private final int update;
    private long nextUpdate = 0;

    public void update() {
        fillAll();
        nextUpdate = System.currentTimeMillis() + update * 1000L;
    }

    public boolean expired() {
        return System.currentTimeMillis() >= nextUpdate;
    }

    public void fill(Region rg) {
        val world = rg.getWorld();
        val min = rg.getMin();
        val max = rg.getMax();

        List<Integer> ys = new ArrayList<>();

        for (int y = min.getY(); y <= max.getY(); y++)
            xf:for (int x = min.getX(); x <= max.getX(); x++)
                for (int z = min.getZ(); z <= max.getZ(); z++)
                    if (world.getBlockAt(x, y, z).getType() == Material.AIR) {
                        ys.add(y);
                        break xf;
                    }

        for (int y = min.getY(); y <= max.getY(); y++)
            for (int x = min.getX(); x <= max.getX(); x++)
                for (int z = min.getZ(); z <= max.getZ(); z++)
                    if (ys.contains(y)) {
                        Block block = world.getBlockAt(x, y, z);
                        for (int i = 0; i < content.size(); i++) {
                            ResourceBlock resourceBlock = content.get(i);
                            if (100 - resourceBlock.getChance() <= RND.nextInt(101) || i == content.size() - 1) {
                                block.setTypeIdAndData(resourceBlock.getMaterial().getId(), (byte) resourceBlock.getData(), false);
                                break;
                            }
                        }
                        if (upperContent != null && y == max.getY())
                            block.setTypeIdAndData(upperContent.getMaterial().getId(), (byte) upperContent.getData(), false);
                    }
        rg.getPlayers().forEach(p -> {
            p.teleport(new Location(p.getWorld(), p.getLocation().getX(), max.getY() + 1, p.getLocation().getZ()));
            p.setVelocity(p.getVelocity().setY(p.getVelocity().getY() + 0.7));
        });
    }

    public void fillAll() {
        regions.forEach(this::fill);
    }

}
