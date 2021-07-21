package ru.redline.opprison;

import lombok.Getter;
import lombok.val;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;
import ru.redline.core.bukkit.ApiManager;
import ru.redline.core.bukkit.CorePlugin;
import ru.redline.core.bukkit.inventory.BaseInventoryListener;
import ru.redline.core.bukkit.util.ChatUtil;
import ru.redline.opprison.command.PrestigeCommand;
import ru.redline.opprison.command.RankCommand;
import ru.redline.opprison.command.RankUpCommand;
import ru.redline.opprison.command.admin.*;
import ru.redline.opprison.command.donate.EnderChestCommand;
import ru.redline.opprison.command.donate.FlyCommand;
import ru.redline.opprison.inventory.GameItems;
import ru.redline.opprison.leaderboard.LeaderboardManager;
import ru.redline.opprison.pickaxe.PickaxeListener;
import ru.redline.opprison.pickaxe.UpdaterTask;
import ru.redline.opprison.player.LocationManager;
import ru.redline.opprison.player.PlayerListener;
import ru.redline.opprison.player.PlayerManager;
import ru.redline.opprison.region.RegionManager;
import ru.redline.opprison.utils.ReflectUtil;

import java.util.Arrays;
import java.util.Map;

@Author("dargen")
@Getter
@Dependency("ApiManager")
@Plugin(name = "PrisonOp", version = "1.0")
public class OpPrison extends CorePlugin {

    public static OpPrison instance;
    public static final String prefix = "&2OpPrison &7» &f";

    private RegionManager regionManager;
    private PlayerManager playerManager;
    private LeaderboardManager leaderboardManager;
    private LocationManager locationManager;
    private GameItems gameItems;

    protected void onPluginEnable() {
        instance = this;

        Bukkit.clearRecipes();

        locationManager = new LocationManager();
        regionManager = new RegionManager(this);
        gameItems = new GameItems(this);
        playerManager = new PlayerManager(this);
        leaderboardManager = new LeaderboardManager();

        new UpdaterTask(this);

        removeCommand("apimanager:fly");

        ApiManager.registerCommands(
                new MineCommand(), new ConfigCommand(), new BuildCommand(),
                new StopCommand(), new EcoCommand(), new MapCommand(),
                new SaveCommand(), new UpdateTopCommand(),
                new RankUpCommand(), new RankCommand(), new EnderChestCommand(),
                new FlyCommand(), new PrestigeCommand()
        );

        ApiManager.registerListeners(
                this, new PlayerListener(), new PickaxeListener(),
                new BaseInventoryListener()
        );
    }

    protected void onPluginDisable() {
        Bukkit.getWorlds().forEach(w -> w.getEntities().forEach(e -> {
            if (e instanceof EntityPlayer) return;
            e.remove();
        }));

    }

    public String toString() {
        return prefix;
    }

    public static void send(CommandSender sender, String message, Object... params) {
        ChatUtil.sendMessage(sender, String.format(prefix + message, params));
    }

    public static void removeCommand(String... to) {
        val commandMap = ReflectUtil.<CommandMap>getValue(Bukkit.getServer(),
                ReflectUtil.getField(Bukkit.getServer().getClass(), "commandMap"));
        val knownCommands = ReflectUtil.<Map<String, Command>>getValue(commandMap, ReflectUtil.getField(commandMap.getClass(), "knownCommands"));
        Arrays.asList(to).forEach(knownCommands.keySet()::remove);
    }

}
