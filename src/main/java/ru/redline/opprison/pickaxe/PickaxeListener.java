package ru.redline.opprison.pickaxe;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.val;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import ru.redline.opprison.OpPrison;
import ru.redline.opprison.utils.BreakUtil;
import ru.redline.opprison.utils.MathUtil;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PickaxeListener implements Listener {

    private Cache<UUID, BlockFace> facings = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES).build();

    @EventHandler
    public void handle(PlayerSwapHandItemsEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void handle(InventoryClickEvent e) {
        val hot = e.getHotbarButton() != -1 ? e.getWhoClicked().getInventory().getContents()[e.getHotbarButton()] : null;
        if (isPickaxe(e.getCurrentItem())
                || isPickaxe(e.getCursor())
                || isPickaxe(hot)
                || (e.getClickedInventory() != null && isPickaxe(e.getClickedInventory().getItem(e.getSlot())))
                || isPickaxe(e.getInventory().getItem(e.getSlot()))) e.setCancelled(true);
    }

    @EventHandler
    public void drop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    protected boolean isPickaxe(ItemStack item) {
        return OpPrison.instance.getGameItems().getItemName(item).equals("pickaxe");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void block(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_BLOCK && isPickaxe(e.getItem())) {
           facings.put(e.getPlayer().getUniqueId(), e.getBlockFace());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void block(BlockBreakEvent e) {
        if (e.isCancelled()) return;
        e.setDropItems(false);
        e.setExpToDrop(0);
        val data = OpPrison.instance.getPlayerManager().getPlayer(e.getPlayer());
        if (!OpPrison.instance.getGameItems().getItemName(e.getPlayer().getItemInHand()).equals("pickaxe")) {
            data.setBlocks(data.getBlocks() + 1);
            return;
        }
        val pickaxe = data.getPickaxe();
        val facing = facings.asMap().remove(e.getPlayer().getUniqueId());
        if (MathUtil.procBooster(data))
            OpPrison.send(data.getHandle(), "Вам повезло и вы получили бустер х2 на &a%s сек.",
                    (data.getPickaxe().getEnchant(PickaxeEnchant.BOOSTER) * 5));
        pickaxe.addExp(MathUtil.getDropExp(data));
        data.setBlocks(data.getBlocks() + 1);
        data.setMoney(MathUtil.getBlockPrice(data, e.getBlock()) + data.getMoney());
        data.setTokens(MathUtil.getBlockTokens(data) + data.getTokens());
        data.setPrestige(MathUtil.getBlockPrestige(data) + data.getPrestige());
        if (!BreakUtil.breakLayer(data, e)) BreakUtil.breakCuboid(data, e, facing);
    }

    @EventHandler
    public void held(PlayerItemHeldEvent e) {
        if (e.getNewSlot() == 0) UpdaterTask.effects(OpPrison.instance.getPlayerManager().getPlayer(e.getPlayer()));
    }
}
