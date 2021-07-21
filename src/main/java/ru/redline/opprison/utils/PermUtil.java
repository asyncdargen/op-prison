package ru.redline.opprison.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import ru.redline.core.global.group.PermissionGroup;
import ru.redline.core.global.group.player.User;

@UtilityClass
public class PermUtil {

    public static boolean hasGroup(CommandSender sender, PermissionGroup group) {
        return sender instanceof ConsoleCommandSender || User.getUser(sender.getName()).hasGroup(group);
    }

}
