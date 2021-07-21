package ru.redline.opprison.command.admin;

import lombok.val;
import org.bukkit.command.CommandSender;
import ru.redline.core.bukkit.command.BukkitMegaCommand;
import ru.redline.core.global.group.PermissionGroup;
import ru.redline.opprison.OpPrison;
import ru.redline.opprison.utils.Configuration;
import ru.redline.opprison.utils.PermUtil;

public class ConfigCommand extends BukkitMegaCommand<CommandSender> {

    public ConfigCommand() {
        super("config", "конфиги", "cfgs", "configs");
    }

    public void onExecute(CommandSender commandSender, String[] args) {
        if (!PermUtil.hasGroup(commandSender, PermissionGroup.ADMIN)) {
            OpPrison.send(commandSender, "&cУ вас недостатачно прав");
            return;
        }
        super.onExecute(commandSender, args);
    }

    protected void onUsage(CommandSender commandSender) {
        OpPrison.send(commandSender, String.join("\n",
                "&fСписок доступных команд &8/config",
                " &7- &a/config help &7- &fСписок доступных под-команд",
                " &7- &a/config list &7- &fСписок конфигов",
                " &7- &a/config reload &7[&aконфиг&7] &7- &fПерезагрузка конфига &8(all - перезагрузит все)"
        ));
    }

    @CommandArgument(aliases = {"help", "помощь", "h"})
    public void help(CommandSender commandSender, String... args) {
        onUsage(commandSender);
    }

    @CommandArgument(aliases = {"list", "список", "l"})
    public void list(CommandSender commandSender, String... args) {
        OpPrison.send(commandSender, "Список конфигов &7- &a%s",
                String.join("&7, &a", Configuration.configs.keySet()));
    }

    @CommandArgument(aliases = {"reload", "rl"})
    public void fill(CommandSender commandSender, String... args) {
        if (args.length >= 1) {
            String name = args[0];
            if (name.contains("all")) {
                Configuration.configs.values().forEach(c -> {
                    c.load();
                    OpPrison.send(commandSender, "Конфиг &a%s&r перезагружен", c.getConfigFile().getName());
                });
            } else {
                val cfg = Configuration.configs.get(args[0]);
                if (cfg == null)
                    OpPrison.send(commandSender, "Конфиг &c%s&r не найден", args[0]);
                else {
                    cfg.load();
                    OpPrison.send(commandSender, "Конфиг &a%s&r перезагружен", cfg.getConfigFile().getName());
                }
            }
        } else OpPrison.send(commandSender, "Укажите название конфига");
    }

}
