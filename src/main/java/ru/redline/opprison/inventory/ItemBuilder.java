package ru.redline.opprison.inventory;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder extends ItemStack {

    public static ItemBuilder of(ItemStack item) {
        return new ItemBuilder(item);
    }

    public static ItemBuilder of(Material m) {
        return new ItemBuilder(m);
    }

    public static ItemBuilder of(int id) {
        return new ItemBuilder(id);
    }

    public static ItemBuilder of(Material material, int durability) {
        return new ItemBuilder(material, durability);
    }

    public static ItemBuilder of(Material material, int amount, int durability) {
        return new ItemBuilder(material, amount, durability);
    }

    public ItemBuilder(ItemStack item) {
        super(item);
    }

    public ItemBuilder(Material material) {
        super(material);
    }

    public ItemBuilder(int typeid) {
        super(typeid);
    }

    public ItemBuilder(Material material, int durability) {
        super(material, 1, (short) durability);
    }

    public ItemBuilder(Material material, int amount, int durability) {
        super(material, amount, (short) durability);
    }

    public ItemBuilder name(String iname) {
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(iname);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder durability(int durability) {
        setDurability((short) durability);
        return this;
    }

    public ItemBuilder unbreakable(boolean is) {
        ItemMeta meta = getItemMeta();
        meta.setUnbreakable(is);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder amount(int amount) {
        setAmount(amount);
        return this;
    }

    public ItemBuilder type(Material type) {
        setType(type);
        return this;
    }

    public ItemBuilder typeId(int type) {
        setTypeId(type);
        return this;
    }

    public ItemBuilder addLoreLine(String line) {
        ItemMeta meta = getItemMeta();
        List<String> lore = new ArrayList<>();
        if (meta.hasLore())
            lore = new ArrayList<>(meta.getLore());
        lore.add(line);
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setLoreLine(int index, String line) {
        ItemMeta meta = getItemMeta();
        List<String> lore = new ArrayList<>();
        if (meta.hasLore())
            lore = new ArrayList<>(meta.getLore());
        lore.set(index, line);
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeLoreLine(String line) {
        ItemMeta meta = getItemMeta();
        List<String> lore = new ArrayList<>();
        if (meta.hasLore())
            lore = new ArrayList<>(meta.getLore());
        lore.remove(line);
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeLoreLine(int index) {
        ItemMeta meta = getItemMeta();
        List<String> lore = new ArrayList<>();
        if (meta.hasLore())
            lore = new ArrayList<>(meta.getLore());
        lore.remove(index);
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        ItemMeta meta = getItemMeta();
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(String... lore) {
        lore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder addLore(List<String> collect) {
        ItemMeta meta = getItemMeta();
        List<String> lore = new ArrayList<>();
        if (meta.hasLore())
            lore = new ArrayList<>(meta.getLore());
        lore.addAll(collect);
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeLore() {
        ItemMeta meta = getItemMeta();
        List<String> lore = meta.getLore();
        lore.clear();
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder addLore(String... collect) {
        return addLore(Arrays.asList(collect));
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder addFlags(ItemFlag... flags) {
        ItemMeta meta = getItemMeta();
        meta.addItemFlags(flags);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder removeFlag(ItemFlag... flags) {
        ItemMeta meta = getItemMeta();
        meta.removeItemFlags(flags);
        setItemMeta(meta);
        return this;
    }

    public List<String> getLore() {
        ItemMeta meta = getItemMeta();
        return meta.hasLore() ? meta.getLore() : new ArrayList<>();
    }

    public ItemBuilder dyeColor(DyeColor color) {
        setDurability(color.getDyeData());
        return this;
    }

    public ItemBuilder woolColor(DyeColor color) {
        if (!getType().equals(Material.WOOL))
            return this;
        setDurability(color.getDyeData());
        return this;
    }

    public ItemBuilder leatherArmorColor(Color color) {
        try {
            LeatherArmorMeta im = (LeatherArmorMeta) getItemMeta();
            im.setColor(color);
            setItemMeta(im);
        } catch (ClassCastException classCastException) {
        }
        return this;
    }

    public ItemBuilder skullOwner(String owner) {
        try {
            SkullMeta im = (SkullMeta) getItemMeta();
            im.setOwner(owner);
            setItemMeta(im);
        } catch (ClassCastException classCastException) {
        }
        return this;
    }

}
