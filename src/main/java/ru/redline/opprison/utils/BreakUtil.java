package ru.redline.opprison.utils;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;
import ru.redline.opprison.OpPrison;
import ru.redline.opprison.pickaxe.PickaxeEnchant;
import ru.redline.opprison.player.OpPlayer;
import ru.redline.opprison.region.RegionParameter;
import ru.redline.opprison.region.mine.ResourceBlock;
import ru.redline.opprison.region.point.PointPos;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class BreakUtil {

    public static boolean breakLayer(OpPlayer player, BlockBreakEvent e) {
        if (player.getPickaxe().getEnchant(PickaxeEnchant.EXCAVATOR) == 0
                || MathUtil.rnd.nextInt(14000) > player.getPickaxe().getEnchant(PickaxeEnchant.EXCAVATOR)) return false;
        val rg= OpPrison.instance.getRegionManager().getPrimaryRegion(e.getBlock().getLocation());
        val min = rg.getMin();
        val max = rg.getMax();
        val world = rg.getWorld();
        val y = e.getBlock().getY();
        double blocks = 0;
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getZ(); z <= max.getZ(); z++) {
                val block = world.getBlockAt(x, y, z);
                if (block.getType() == Material.AIR) continue;
                blocks++;
                block.setType(Material.AIR);
            }
        }
        player.setTokens(player.getTokens() + MathUtil.getBlockTokens(player) * blocks / 3);
        player.setMoney(player.getMoney() + MathUtil.getBlockPrice(player, e.getBlock()) * blocks / 3);
        return true;
    }

    public static void breakCuboid(OpPlayer player, BlockBreakEvent e, BlockFace facing) {
        if (facing == null) return;
        if ((player.getPickaxe().getEnchant(PickaxeEnchant.EXPLOSIVE) == 0)
                || (MathUtil.rnd.nextInt(6000) > player.getPickaxe().getEnchant(PickaxeEnchant.EXPLOSIVE))
                && player.getPickaxe().getEnchant(PickaxeEnchant.EXPLOSIVE) < 600) return;
        val points = getCuboidPoints(e.getBlock().getLocation(), facing);
        val max = points[0];
        val min = points[1];
        val world = e.getBlock().getWorld();
        val rg = OpPrison.instance.getRegionManager();
        for (int y = min.getY(); y <= max.getY(); y++) {
            for (int x = min.getX(); x <= max.getX(); x++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == Material.AIR) continue;
                    if (!rg.getPrimaryRegion(block.getLocation()).getParameter(RegionParameter.BLOCK_BREAK)) continue;
                    player.setMoney(player.getMoney() + MathUtil.getBlockPrice(player, block));
                    player.setTokens(player.getTokens() + MathUtil.getBlockTokens(player));
                    block.setType(Material.AIR);
                }
            }
        }
    }

    private PointPos[] getCuboidPoints(Location loc, BlockFace blockFace) {
        Location loc2 = loc.clone();

        switch (blockFace) {
            case SOUTH: loc.add(-2, 2, -4); loc2.add(2, -2, 0); break;
            case WEST: loc.add(4, 2, -2); loc2.add(0, -2, 2); break;
            case EAST: loc.add(-4, 2, 2); loc2.add(0, -2, -2); break;
            case NORTH: loc.add(2, 2, 4); loc2.add(-2, -2, 0); break;
            case UP: loc.add(-2, -4, -2); loc2.add(2, 0, 2); break;
            case DOWN: loc.add(2, 4, 2); loc2.add(-2, 0, -2); break;
        }

        return new PointPos[]{
                PointPos.builder()
                        .x(Math.max(loc.getBlockX(), loc2.getBlockX()))
                        .y(Math.max(loc.getBlockY(), loc2.getBlockY()))
                        .z(Math.max(loc.getBlockZ(), loc2.getBlockZ())).build(),
                PointPos.builder()
                        .x(Math.min(loc.getBlockX(), loc2.getBlockX()))
                        .y(Math.min(loc.getBlockY(), loc2.getBlockY()))
                        .z(Math.min(loc.getBlockZ(), loc2.getBlockZ())).build()
        };
    }

}
