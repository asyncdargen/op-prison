package ru.redline.opprison.inventory;

import com.google.common.collect.Maps;
import lombok.val;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import ru.redline.core.bukkit.Main;
import ru.redline.opprison.OpPrison;
import ru.redline.opprison.pickaxe.Pickaxe;
import ru.redline.opprison.pickaxe.PickaxeEnchant;
import ru.redline.opprison.player.OpPlayer;
import ru.redline.opprison.utils.NMSStack;
import ru.redline.opprison.utils.formatter.DoubleFormatter;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GameItems {

    private final static String KEY = "game_item";

    private final Map<String, BiConsumer<PlayerInteractEvent, ItemStack>> interacts = Maps.newConcurrentMap();
    private final Map<String, Function<Object[], ItemStack>> creators = Maps.newConcurrentMap();

    public GameItems(OpPrison pl) {
        Main.getInstance().getEventManager().newBuilder(PlayerInteractEvent.class)
                .setEventApplicable(this::interact).setEventPriority(EventPriority.LOWEST).setPlugin(pl).build();
        registerDefaults();
    }

    private void registerDefaults() {
        registerItem("pickaxe", o -> {
            OpPlayer owner = (OpPlayer) o[0];
            Pickaxe pickaxe = owner.getPickaxe();
            return ItemBuilder.of(Material.DIAMOND_PICKAXE)
                    .enchant(Enchantment.DIG_SPEED, pickaxe.getEnchant(PickaxeEnchant.EFFICIENCY))
                    .name((pickaxe.getName())).addFlags(ItemFlag.values()).unbreakable(true).lore("")
                    .addLore(pickaxe.getEnchants().entrySet().stream().filter(e -> e.getValue() > 0)
                            .map(e -> " " + e.getKey().getName() + " " + e.getValue()).collect(Collectors.toList()))
                    .addLore("",
                            "§f Владелец§7: §2" + owner.getHandle().getName(),
                            "§f Уровень§7: §a" + pickaxe.getLevel(),
                            "§f Опыт§7: §b" + DoubleFormatter.comma(pickaxe.getExp()) + "§7/§3" + DoubleFormatter.comma(pickaxe.getNeedExp()), "");
        }, (e, i) -> {
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
                DefaultMenus.enchanter(OpPrison.instance.getPlayerManager().getPlayer(e.getPlayer()), PickaxeEnchant.EnchantType.DEFAULT);
        });
    }


    public void registerItem(String name, Function<Object[], ItemStack> creator, BiConsumer<PlayerInteractEvent, ItemStack> interact) {
        creators.put(name.toLowerCase(), creator);
        if (interact != null)
            interacts.put(name.toLowerCase(), interact);
    }

    public void registerItem(String name, Function<Object[], ItemStack> creator) {
        registerItem(name, creator, null);
    }

    public ItemStack getItem(String name, Object... params) {
        Function<Object[], ItemStack> creator = creators.get(name.toLowerCase());
        if (creator == null) return null;
        return setTagName(creator.apply(params), name.toLowerCase());
    }

    public void registerItem(String name, ItemStack item, BiConsumer<PlayerInteractEvent, ItemStack> interact) {
        registerItem(name, (o) -> item.clone(), interact);
    }

    public void registerItem(String name, ItemStack item) {
        registerItem(name, item, null);
    }

    public ItemStack setTagName(ItemStack item, String name) {
        return NMSStack.setTag(item, KEY, new NBTTagString(name));
    }

    public String getItemName(ItemStack item) {
        if (item != null) {
            val nbt = NMSStack.<NBTTagString>getTag(item, KEY);
            String name;
            if (nbt != null && (name = nbt.c_()) != null) {
                if (creators.containsKey(name.toLowerCase()))
                    return name;
            }
        }
        return "";
    }

    public void interact(PlayerInteractEvent event) {
        if (event.getItem() != null && event.getItem().getType() != Material.AIR) {
            String name;
            val nbt = NMSStack.<NBTTagString>getTag(event.getItem(), KEY);
            if (nbt != null && (name = nbt.c_()) != null) {
                if (!creators.containsKey(name.toLowerCase())) return;
                val interact = interacts.get(name.toLowerCase());
                if (interact == null)
                    return;
                interact.accept(event, event.getItem());
            }
        }
    }

}
