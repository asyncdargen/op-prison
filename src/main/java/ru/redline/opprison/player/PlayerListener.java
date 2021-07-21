package ru.redline.opprison.player;

import lombok.val;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import ru.redline.core.bukkit.ApiManager;
import ru.redline.core.bukkit.scoreboard.BaseScoreboardBuilder;
import ru.redline.core.bukkit.scoreboard.BaseScoreboardScope;
import ru.redline.core.bukkit.util.ChatUtil;
import ru.redline.core.global.group.player.User;
import ru.redline.opprison.OpPrison;
import ru.redline.opprison.player.OpPlayer;
import ru.redline.opprison.player.event.PlayerLoadedEvent;
import ru.redline.opprison.utils.NMSStack;
import ru.redline.opprison.utils.formatter.DoubleFormatter;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static ru.redline.opprison.utils.formatter.DoubleFormatter.*;

public class PlayerListener implements Listener {

    @EventHandler
    public void join(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        e.getPlayer().teleport(OpPrison.instance.getLocationManager().byId("spawn"));
        applyScoreboard(e.getPlayer());
        OpPrison.instance.getPlayerManager().load(e.getPlayer());
    }

    @EventHandler
    public void loaded(PlayerLoadedEvent e) {
        val player = e.getPlayer();
        val data = e.getData();

        if (!OpPrison.instance.getGameItems().getItemName(player.getInventory().getItem(0)).equals("pickaxe")) {
            player.getInventory().setItem(0, OpPrison.instance.getGameItems().getItem("pickaxe", data));
        }
    }

    @EventHandler
    public void quit(PlayerQuitEvent e) {
        OpPrison.instance.getPlayerManager().unload(e.getPlayer());
    }

    @EventHandler
    public void death(PlayerDeathEvent e) {
        e.setDeathMessage(null);
        e.setKeepInventory(true);
        e.getEntity().spigot().respawn();
    }

    @EventHandler
    public void respawn(PlayerRespawnEvent e) {
        e.setRespawnLocation(OpPrison.instance.getLocationManager().byId("spawn"));
    }

    @EventHandler
    public void interact(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK
                && e.getClickedBlock() != null
                && e.getClickedBlock().getType() == Material.ENDER_CHEST)
            e.getPlayer().openInventory(e.getPlayer().getEnderChest());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void chat(AsyncPlayerChatEvent e) {
        if (e.isCancelled()) return;
        e.setCancelled(true);
        if (e.getPlayer().hasPermission("chat.color"))
            e.setMessage(ChatUtil.color(e.getMessage()));
        val data = OpPrison.instance.getPlayerManager().getPlayer(e.getPlayer());
        val user = User.getUser(e.getPlayer().getName());
        val prefix = setNullEvents(new TextComponent("§7[§c" + DoubleFormatter.chars(data.getPrestige()) + "§7] §r" + ChatUtil.color(user.getPrefix())));
        val player = new TextComponent(e.getPlayer().getName());
        player.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {generatePlayerInfo(data)}));
        player.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + e.getPlayer().getName()));
        prefix.addExtra(player);
        val message = setNullEvents(new TextComponent("§7:§f"));
        prefix.addExtra(message);
        if ((e.getMessage().contains("#рука") || e.getMessage().contains("#hand"))
                && e.getPlayer().getItemInHand() != null
                && e.getPlayer().getItemInHand().getType() != Material.AIR) {
            for (String s : e.getMessage().split(" ")) {
                if (s.isEmpty()) continue;
                prefix.addExtra(" ");
                if (s.equalsIgnoreCase("#рука") || s.equalsIgnoreCase("#hand"))
                    prefix.addExtra(NMSStack.toTextComponent(e.getPlayer().getItemInHand()));
                else
                    prefix.addExtra(setNullEvents(new TextComponent(s)));
            }
        } else prefix.addExtra((setNullEvents(new TextComponent(" " + e.getMessage()))));
        Bukkit.getOnlinePlayers().stream().map(Player::spigot).forEach(s -> s.sendMessage(prefix));
        Bukkit.getConsoleSender().sendMessage(ChatUtil.text(e.getFormat(), e.getPlayer().getName(), e.getMessage()));
    }

    protected TextComponent generatePlayerInfo(OpPlayer player) {
        return new TextComponent(String.join("\n",
                "§cИнформация о игроке",
                "", " §fПрестиж§7: §c" + comma(player.getPrestige()),
                " §fРанг§7: §r" + player.getRank().getName(),
                " §fТокены§7: §e۞" + chars(player.getTokens()),
                " §fКристаллы§7: §b₪" + chars(player.getCrystals()),
                " §fБаланс§7: §a$" + chars(player.getMoney()),
                " §fБлоки§7: §6" + comma(player.getCrystals()), "",
                "§7§oНажмите, чтобы написать в §8ЛС"
        ));
    }

    protected TextComponent setNullEvents(TextComponent in) {
        in.setClickEvent(null);
        in.setHoverEvent(null);
        return in;
    }

    protected void applyScoreboard(Player own) {
        BaseScoreboardBuilder builder = ApiManager.newScoreboardBuilder();

        builder.scoreboardDisplay(ChatUtil.color("&2&lOpPrison"));

        builder.scoreboardScope(BaseScoreboardScope.PROTOTYPE);

        builder.scoreboardLine(14, "§e§l" + own.getName());
        builder.scoreboardLine(13, "  §fПрестиж§7: §cЗагрузка...");
        builder.scoreboardLine(12, "  §fБлоки§7: §6Загрузка...");
        builder.scoreboardLine(11, "  §fРанг§7: §rЗагрузка...");
        builder.scoreboardLine(10, " ");
        builder.scoreboardLine(9, "§e§lСтатистика");
        builder.scoreboardLine(8, "  §fТокены§7: §eЗагрузка...");
        builder.scoreboardLine(7, "  §fБаланс§7: §aЗагрузка...");
        builder.scoreboardLine(6, "  §fКристаллы§7: §bЗагрузка...");
        builder.scoreboardLine(5, "  §fМножитель§7: §dЗагрузка...");
        builder.scoreboardLine(4, " ");
        builder.scoreboardLine(3, "§e§lСервер");
        builder.scoreboardLine(2, "  §fОнлайн§7: §a" + Bukkit.getOnlinePlayers().size());
        builder.scoreboardLine(1, "  §fСайт§7: §credline.pw");

        builder.scoreboardUpdater((board, player) -> {
            val data = OpPrison.instance.getPlayerManager().getPlayer(player);
            if (data == null) return;

            board.updateScoreboardLine(2, player, "  §fОнлайн§7: §a" + Bukkit.getOnlinePlayers().size());
            board.updateScoreboardLine(5, player, "  §fМножитель§7: §d" + fix(data.getFullMultiplier()) + "x");
            board.updateScoreboardLine(6, player, "  §fКристаллы§7: §b₪" + chars(data.getCrystals()));
            board.updateScoreboardLine(7, player, "  §fБаланс§7: §a$" + chars(data.getMoney()));
            board.updateScoreboardLine(8, player, "  §fТокены§7: §e۞" + chars(data.getTokens()));
            board.updateScoreboardLine(11, player, "  §fРанг§7: §r" + data.getRank().getName());
            board.updateScoreboardLine(12, player, "  §fБлоки§7: §6" + clear(data.getBlocks()));
            board.updateScoreboardLine(13, player, "  §fПрестиж§7: §c" + clear(data.getPrestige()));

        }, 20);

        builder.build().setScoreboardToPlayer(own);
    }

}
