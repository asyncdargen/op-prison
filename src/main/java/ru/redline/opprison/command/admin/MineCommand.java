package ru.redline.opprison.command.admin;

import org.bukkit.command.CommandSender;
import ru.redline.core.bukkit.command.BukkitMegaCommand;
import ru.redline.core.global.group.PermissionGroup;
import ru.redline.opprison.OpPrison;
import ru.redline.opprison.region.mine.Mine;
import ru.redline.opprison.utils.PermUtil;

import java.util.stream.Collectors;

public class MineCommand extends BukkitMegaCommand<CommandSender> {

    public MineCommand() {
        super("mine", "шахты");
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
                "&fСписок доступных команд &8/mine",
                " &7- &a/mine help &7- &fСписок доступных под-команд",
                " &7- &a/mine list &7- &fСписок шахт",
                " &7- &a/mine fill &7[&aшахта&7] &7- &fЗаполнение шахты",
                " &7- &a/mine update &7[&aшахта&7] &7- &fЗаполнение шахты &8(с сбросом таймера)"
        ));
    }

    @CommandArgument(aliases = {"help", "помощь", "h"})
    public void help(CommandSender commandSender, String... args) {
        onUsage(commandSender);
    }

    @CommandArgument(aliases = {"list", "список", "l"})
    public void list(CommandSender commandSender, String... args) {
        OpPrison.send(commandSender, "Список шахт &7- &a%s",
                OpPrison.instance.getRegionManager().getMines().getMines().stream().map(Mine::getId).collect(Collectors.joining("&7, &a")));
    }

    @CommandArgument(aliases = {"fill", "заполнить", "f"})
    public void fill(CommandSender commandSender, String... args) {
        Mine m;
        if (args.length >= 1 && (m = OpPrison.instance.getRegionManager().getMines().byId(args[0].toLowerCase())) != null) {
            m.fillAll();
            OpPrison.send(commandSender, "Шахта &a%s&r перезагружена", m.getId());
        } else if (args.length < 1) OpPrison.send(commandSender, "Укажите название шахты");
        else OpPrison.send(commandSender, "Шахта &c%s&r не найдена", args[0]);
    }

    @CommandArgument(aliases = {"update", "обновить", "u"})
    public void update(CommandSender commandSender, String... args) {
        Mine m;
        if (args.length >= 1 && (m = OpPrison.instance.getRegionManager().getMines().byId(args[0].toLowerCase())) != null) {
            m.fillAll();
            OpPrison.send(commandSender, "Шахта &a%s&r заполнена", m.getId());
        } else if (args.length < 1) OpPrison.send(commandSender, "Укажите название шахты");
        else OpPrison.send(commandSender, "Шахта &c%s&r не найдена", args[0]);
    }

}
