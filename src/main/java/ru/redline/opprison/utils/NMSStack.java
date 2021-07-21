package ru.redline.opprison.utils;

import lombok.experimental.UtilityClass;
import lombok.val;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_12_R1.NBTBase;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

import static ru.redline.opprison.utils.ReflectUtil.*;

@UtilityClass
public class NMSStack {

    private static final FieldAccessor<Map<String, NBTBase>> mapReflect
            = fieldAccessor(getField(NBTTagCompound.class, "map"));

    public static ItemStack setTag(ItemStack item, String key, NBTBase value) {
        val nms = CraftItemStack.asNMSCopy(item);
        NBTTagCompound nmsTag = nms.getTag();
        if (nmsTag == null) nmsTag = new NBTTagCompound();
        nmsTag.set(key, value);
        nms.setTag(nmsTag);
        return CraftItemStack.asBukkitCopy(nms);
    }

    public static ItemStack setTags(ItemStack item, Map<String, NBTBase> tags){
        val nms = CraftItemStack.asNMSCopy(item);
        NBTTagCompound nmsTag = nms.getTag();
        if (nmsTag == null) nmsTag = new NBTTagCompound();
        tags.forEach((k, v) -> setTag(item, k, v));
        for (String key : tags.keySet()) {
            nmsTag.set(key, tags.get(key));
        }
        return CraftItemStack.asBukkitCopy(nms);
    }

    public static <T extends NBTBase> T getTag(ItemStack item, String key){
        val nms = CraftItemStack.asNMSCopy(item);
        NBTTagCompound nmsTag = nms.getTag();
        if (nmsTag == null) nmsTag = new NBTTagCompound();
        return nmsTag.get(key) == null ? null : (T) nmsTag.get(key);
    }

    public static Map<String, NBTBase> getTags(ItemStack item){
        net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(item);
        NBTTagCompound nmsTag = nms.getTag();
        if (nmsTag == null) nmsTag = new NBTTagCompound();
        return mapReflect.get(nmsTag);
    }

    public static TextComponent toTextComponent(ItemStack item) {
        val tag = new NBTTagCompound();
        CraftItemStack.asNMSCopy(item).save(tag);
        TextComponent tc = new TextComponent("§7[" + getI18NDisplayName(item) + (item.getAmount() > 1 ? " §fx" + item.getAmount() : "") + "§7]");
        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new BaseComponent[]{new TextComponent(tag.toString())}));
        return tc;
    }

    public static String getI18NDisplayName(ItemStack item) {
        val nms = CraftItemStack.asNMSCopy(item);
        return nms != null ? nms.getName() : null;
    }
}
