package ru.redline.opprison.command.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ru.redline.core.bukkit.command.BukkitCommand;
import ru.redline.core.bukkit.command.annotation.CommandGroup;
import ru.redline.core.bukkit.util.BungeeUtil;
import ru.redline.core.bukkit.util.ChatUtil;
import ru.redline.core.global.group.PermissionGroup;
import ru.redline.opprison.OpPrison;

import static org.bukkit.Bukkit.getScheduler;

@CommandGroup(group = PermissionGroup.ADMIN)
public class StopCommand extends BukkitCommand<CommandSender> {

    private BukkitTask task;
    private int counter = 5;

    public StopCommand() {
        super("stop", "стоять", "стоп");
    }

    protected void onExecute(CommandSender sender, String[] strings) {
        if (task != null) ChatUtil.sendMessage(sender, "&cСервер уже останавливается");
        else {
            task = getScheduler().runTaskTimer(OpPrison.instance, () -> {
                ChatUtil.broadcast("\n    &8[&cСервер&8] &fПерезагрузка через &c%s&f сек...", counter--);
                ChatUtil.broadcast("§r");
                if (counter <= 0) task.cancel();
                else return;
                ChatUtil.broadcast("\n    &8[&cСервер&8] &6Сохранение игровых данных...");
                ChatUtil.broadcast("§r");
                OpPrison.instance.getPlayerManager().getCache().values().forEach(data -> {
                    OpPrison.instance.getPlayerManager().save(data);
                    BungeeUtil.sendToRandomLobby(data.getHandle());
                });
                getScheduler().runTaskLater(OpPrison.instance, () -> {
                    Bukkit.shutdown();
                    ChatUtil.broadcast("\n    &8[&cСервер&8] &4&lОстановка...");
                    ChatUtil.broadcast("§r");
                }, 30);
            }, 0, 20);
        }
    }

}
