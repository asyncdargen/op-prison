package ru.redline.opprison.region;

import lombok.val;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.redline.core.bukkit.ApiManager;
import ru.redline.core.bukkit.CorePlugin;
import ru.redline.opprison.command.admin.BuildCommand;

public class RegionGuard implements Listener {

    private RegionManager manager;

    public RegionGuard(CorePlugin plugin, RegionManager manager) {
        this.manager = manager;
        ApiManager.registerListeners(plugin, this);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void handle(EntityDamageEvent e) {
        val rg = manager.getPrimaryRegion(e.getEntity().getLocation());
        e.setCancelled(!rg.getParameter(RegionParameter.DAMAGE));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void handle(FoodLevelChangeEvent e) {
        val rg = manager.getPrimaryRegion(e.getEntity().getLocation());
        e.setCancelled(!rg.getParameter(RegionParameter.FOOD_CHANGE));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void handle(EntitySpawnEvent e) {
        val rg = manager.getPrimaryRegion(e.getEntity().getLocation());
        e.setCancelled(!rg.getParameter(RegionParameter.SPAWN_ENTITY));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void handle(PlayerInteractEntityEvent e) {
        val entityRg = manager.getPrimaryRegion(e.getRightClicked().getLocation());
        val playerRg = manager.getPrimaryRegion(e.getPlayer().getLocation());
        e.setCancelled(!entityRg.getParameter(RegionParameter.ENTITY_USE) || !playerRg.getParameter(RegionParameter.ENTITY_USE));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void handle(EntityDamageByEntityEvent e) {
        if (!BuildCommand.builders.contains(e.getDamager().getUniqueId())) return;
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            val entityRg = manager.getPrimaryRegion(e.getEntity().getLocation());
            val damagerRg = manager.getPrimaryRegion(e.getDamager().getLocation());
            e.setCancelled(!entityRg.getParameter(RegionParameter.PVP) || !damagerRg.getParameter(RegionParameter.PVP));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void handle(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        if (e.getAction() == Action.LEFT_CLICK_BLOCK) return;
        val rg = manager.getPrimaryRegion(e.getClickedBlock().getLocation());
        e.setCancelled(!BuildCommand.builders.contains(e.getPlayer().getUniqueId()) && !rg.getParameter(RegionParameter.BLOCK_USE));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void handle(BlockPhysicsEvent e) {
        val rg = manager.getPrimaryRegion(e.getBlock().getLocation());
        e.setCancelled(!rg.getParameter(RegionParameter.PHYSIC));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void handle(BlockPlaceEvent e) {
        val rg = manager.getPrimaryRegion(e.getBlock().getLocation());
        e.setCancelled(!BuildCommand.builders.contains(e.getPlayer().getUniqueId()) && !rg.getParameter(RegionParameter.BLOCK_PLACE));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(BlockBreakEvent e) {
        val rg = manager.getPrimaryRegion(e.getBlock().getLocation());
        e.setCancelled(!BuildCommand.builders.contains(e.getPlayer().getUniqueId()) && !rg.getParameter(RegionParameter.BLOCK_BREAK));
    }

}
