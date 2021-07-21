package ru.redline.opprison.inventory;

import lombok.val;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import ru.redline.core.bukkit.ApiManager;
import ru.redline.core.bukkit.inventory.item.BaseInventoryClickItem;
import ru.redline.core.bukkit.util.NumberUtil;
import ru.redline.opprison.OpPrison;
import ru.redline.opprison.inventory.ItemBuilder;
import ru.redline.opprison.pickaxe.PickaxeEnchant;
import ru.redline.opprison.player.OpPlayer;
import ru.redline.opprison.player.Rank;
import ru.redline.opprison.utils.MathUtil;

import java.util.Arrays;
import java.util.stream.Collectors;

import static ru.redline.core.bukkit.util.ChatUtil.text;
import static ru.redline.opprison.player.Rank.fromOrdinal;
import static ru.redline.opprison.utils.formatter.DoubleFormatter.comma;

public class DefaultMenus {

    private final static ItemStack EMPTY =
            ItemBuilder.of(Material.STAINED_GLASS_PANE, 15).name(" ");

    public static void enchanter(OpPlayer player, PickaxeEnchant.EnchantType type) {
        ApiManager.createSimpleInventory(6, type.getName() + " чары", (p, i) -> {
            val pickaxe = player.getPickaxe();

            for (int j = 0; j < NumberUtil.toManyArray(0, 54).length; j++)
                i.addItem(new BaseInventoryClickItem(j, EMPTY, (c, e) -> e.setCancelled(true)));

            for (int j = 0; j < PickaxeEnchant.EnchantType.values().length; j++) {
                val cur = PickaxeEnchant.EnchantType.values()[j];
                val icon = ItemBuilder.of(cur.getMaterial()).addFlags(ItemFlag.values()).name(cur.getName());
                if (cur == type) icon.enchant(Enchantment.ARROW_DAMAGE, 1);
                i.addItem(new BaseInventoryClickItem(j * 8, icon, (c, e) -> {
                    e.setCancelled(true);
                    if (cur != type) enchanter(player, cur);
                }));
            }

            i.addItem(new BaseInventoryClickItem(4,
                            ItemBuilder.of(
                                    OpPrison.instance.getGameItems().getItem("pickaxe", player))
                                    .type(Material.ENCHANTED_BOOK).name("§2Информация о Кирке"),
                            (c, e) -> e.setCancelled(true)
                    )
            );

            for (int j = 0; j < type.getSlots().length; j++) {
                val ench = PickaxeEnchant.getFor(type).get(j);

                val tokens = type == PickaxeEnchant.EnchantType.DEFAULT;
                val tmp = tokens ? player.getTokens() : player.getCrystals();
                val format = tokens ? "§e۞%s§r" : "§b₪%s§r";

                val level = pickaxe.getEnchant(ench);
                val max = level >= ench.getMax();
                val can = ench.getNeed() <= pickaxe.getLevel();

                val canBuyMax = MathUtil.getCanBuyEnchant(level, tmp, ench);
                val buyMaxPrice = MathUtil.getEnchantPrice(level, canBuyMax, ench);

                val avaible = ench.getMax() - level;
                val canBuy10 = avaible > 10 ? 10 : avaible < 10 ? 0 : avaible;
                val buy10Price = MathUtil.getEnchantPrice(level, canBuy10, ench);

                val buyPrice = MathUtil.getEnchantPrice(level, 0, ench);

                val icon = ItemBuilder.of(ench.getMaterial())
                        .addFlags(ItemFlag.values()).name(ench.getName())
                        .lore("").addLore(Arrays.stream(ench.getDescription()).map("§7  "::concat).collect(Collectors.toList()))
                        .addLore("", text(" §fУровень§7: §a%s§7/§2%s", level, ench.getMax()), "");

                if (!max && can) {
                    icon.addLore(text(" §2ЛКМ §8- §7Купить §f1 уровень за %s", text(format, comma(buyPrice))));
                    if (canBuy10 != 0)
                        icon.addLore(text(" §2ПКМ §8- §7Купить §f10 уровней за %s", text(format, comma(buy10Price))));
                    if (canBuyMax != 0)
                        icon.addLore(text(" §2Q §8- §7Купить §f%s уровней за %s", ((int) canBuyMax), text(format, comma(buyMaxPrice))));
                    icon.addLore("");
                } else icon.addLore(max ? " §cМакс. Уровень" : " §cТребуется " + ench.getNeed() + " уровень кирки", "");

                i.addItem(new BaseInventoryClickItem(type.getSlots()[j], icon, (c, e) -> {
                    e.setCancelled(true);
                    if (max || !can) return;
                    val balance = tokens ? player.getTokens() : player.getCrystals();
                    switch (e.getClick()) {
                        case DROP:
                            if (canBuyMax > 0)
                                if (balance < buyMaxPrice)
                                    OpPrison.send(p, "Вам не хватает %s&r для покупки &c%s&f уровней %s",
                                            text(format, comma(buyMaxPrice - balance)), canBuyMax, ench.getName());
                                else {
                                    val set = balance - buyMaxPrice;
                                    if (tokens) player.setTokens(set);
                                    else player.setCrystals(set);
                                    pickaxe.getEnchants().put(ench, (int) (level + canBuyMax));
                                    OpPrison.send(p, "Вы успешно купили &a%s&r уровней &r%s&r за %s", ((int) canBuyMax), ench.getName(), text(format, comma(buyMaxPrice)));
                                    i.updateInventory(p);
                                }
                            break;
                        case RIGHT:
                            if (canBuy10 > 0)
                                if (balance < buy10Price)
                                    OpPrison.send(p, "Вам не хватает %s&r для покупки &c%s&f уровней %s",
                                            text(format, comma(buy10Price - balance)), canBuy10, ench.getName());
                                else {
                                    val set = balance - buy10Price;
                                    if (tokens) player.setTokens(set);
                                    else player.setCrystals(set);
                                    pickaxe.getEnchants().put(ench, level + canBuy10);
                                    OpPrison.send(p, "Вы успешно купили &a%s&r уровней &r%s&r за %s", canBuy10, ench.getName(), text(format, comma(buy10Price)));
                                    i.updateInventory(p);
                                }
                            break;
                        default:
                            if (balance < buyPrice)
                                OpPrison.send(p, "Вам не хватает %s&r для покупки &c%s&f уровня %s",
                                        text(format, comma(buyPrice - balance)), 1, ench.getName());
                            else {
                                val set = balance - buyPrice;
                                if (tokens) player.setTokens(set);
                                else player.setCrystals(set);
                                pickaxe.getEnchants().put(ench, level + 1);
                                OpPrison.send(p, "Вы успешно купили &a%s&r уровень &r%s&r за %s", 1, ench.getName(), text(format, comma(buyPrice)));
                                i.updateInventory(p);
                            }
                            break;
                    }
                }));
            }

        }).openInventory(player.getHandle());
    }

    public static void rank(OpPlayer player) {
        ApiManager.createSimpleInventory(1, "§cМеню Рангов", (p, i) -> {
            for (int j = 0; j < NumberUtil.toManyArray(0, 9).length; j++)
                i.addItem(new BaseInventoryClickItem(j, EMPTY, (c, e) -> e.setCancelled(true)));
            i.addItem(new BaseInventoryClickItem(2, ItemBuilder.of(Material.PAPER).name("§aСписок"), (c, e) -> {
                e.setCancelled(true);
                rankList(player);
            }));
            val rank = player.getRank();
            val rankOrdinal = rank.ordinal();

            val canBuyMax = MathUtil.getCanBuyRank(rankOrdinal, player.getMoney());
            val buyMaxPrice = MathUtil.getRankPrice(rankOrdinal, canBuyMax);

            val avaible = Rank.values().length - 1 - rankOrdinal;
            val canBuy10 = avaible > 10 ? 10 : avaible < 10 ? 0 : avaible;
            val buy10Price = MathUtil.getRankPrice(rankOrdinal, canBuy10);

            val buyPrice = rank.next().getPrice();

            val icon = ItemBuilder.of(Material.EXP_BOTTLE).name("§cПокупка").addFlags(ItemFlag.values());

            icon.lore("");
            if (rank.isMax()) icon.addLore(text(" §cМаксимальный ранг %s", rank.getName()));
            else {
                icon.addLore(text(" §2ЛКМ §8- §7Купить §f1 §8(%s§8)§7 ранг за §a$%s", rank.next().getName(), comma(buyPrice)));
                if (canBuy10 != 0)
                    icon.addLore(text(" §2ПКМ §8- §7Купить §f10 §8(до %s§8)§7 рангов за §a$%s", fromOrdinal(rankOrdinal + 10).getName(), comma(buy10Price)));
                if (canBuyMax != 0)
                    icon.addLore(text(" §2Q §8- §7Купить §f%s §8(до %s§8)§7 рангов за §a$%s", ((int) canBuyMax), fromOrdinal((int) (rankOrdinal + canBuyMax)).getName(), comma(buyMaxPrice)));
            }
            icon.addLore("");

            i.addItem(new BaseInventoryClickItem(6, icon, (c, e) -> {
                e.setCancelled(true);
                if (rank.isMax()) return;
                switch (e.getClick()) {
                    case DROP:
                        if (canBuyMax > 0)
                            if (player.getMoney() < buyMaxPrice)
                                OpPrison.send(p, "Вам не хватает §c$%s&r для покупки %s&f ранга§7(-ов)",
                                        comma(buyMaxPrice - player.getMoney()), canBuyMax);
                            else {
                                player.setMoney(player.getMoney() - buyMaxPrice);
                                player.setRank(fromOrdinal((int) (rankOrdinal + canBuyMax)));
                                OpPrison.send(p, "Вы успешно купили §a%s§f ранг§7(-ов)§f до %s§f за §a$%s", ((int) canBuyMax), player.getRank().getName(), comma(buyMaxPrice));
                                i.updateInventory(p);
                            }
                        break;
                    case RIGHT:
                        if (canBuy10 > 0)
                            if (player.getMoney() < buy10Price)
                                OpPrison.send(p, "Вам не хватает §c$%s&r для покупки 10 рангов",
                                        comma(buy10Price - player.getMoney()));
                            else {
                                player.setMoney(player.getMoney() - buy10Price);
                                player.setRank(fromOrdinal(rankOrdinal + 10));
                                OpPrison.send(p, "Вы успешно купили §a%s§f ранг§7(-ов)§f до %s§f за §a$%s", 10, player.getRank().getName(), comma(buy10Price));
                                i.updateInventory(p);
                            }
                        break;
                    default:
                        if (player.getMoney() < player.getRank().next().getPrice())
                            OpPrison.send(p, "Вам не хватает §c$%s&r для покупки ранга %s",
                                    comma(rank.next().getPrice() - player.getMoney()), rank.getName());
                        else {
                            player.setMoney(player.getMoney() - rank.next().getPrice());
                            player.setRank(rank.next());
                            OpPrison.send(p, "Вы успешно купили §a%s§f ранг§7(-ов)§f до %s§f за §a$%s", 1, player.getRank().getName(), comma(buyPrice));
                            i.updateInventory(p);
                        }
                        break;
                }
            }));
        }).openInventory(player.getHandle());
    }

    public static void rankList(OpPlayer player) {
        ApiManager.createSimpleInventory(5, text("&aСписок рангов &7(%s &7- %s&7)", Rank.A.getName(), Rank.Z.getName()), (p, i) -> {
            i.addItem(new BaseInventoryClickItem(
                    36, ItemBuilder.of(Material.ARROW)
                    .name("§cНазад").enchant(Enchantment.ARROW_DAMAGE, 1)
                    .addFlags(ItemFlag.values()), (c, e) -> {
                e.setCancelled(true);
                rank(player);
            }));
            for (int j = 0; j < Rank.values().length; j++) {
                val cur = Rank.values()[j];
                i.addItem(new BaseInventoryClickItem(j, ItemBuilder.of(!player.getRank().has(cur) ? Material.EMPTY_MAP : Material.PAPER)
                        .name(cur.getName()).lore("", " §fСтоимость§7: §a$" + comma(cur.getPrice()), ""), (c, e) -> {
                    e.setCancelled(true);
                }));
            }
        }).openInventory(player.getHandle());
    }

    public static void prestige(OpPlayer player) {
        ApiManager.createSimpleInventory(1, "§cМеню Престижей", (p, i) -> {
            for (int j = 0; j < NumberUtil.toManyArray(0, 9).length; j++)
                i.addItem(new BaseInventoryClickItem(j, EMPTY, (c, e) -> e.setCancelled(true)));

            val canBuyMax = MathUtil.getCanBuyPrestige(player.getPrestige(), player.getMoney());
            val buyMaxPrice = MathUtil.getPrestigePrice(player.getPrestige(), canBuyMax);

            val buy10Price = MathUtil.getPrestigePrice(player.getPrestige(), 10);

            val buyPrice = MathUtil.getPrestigePrice(player.getPrestige(), 0);

            val buy = ItemBuilder.of(Material.REDSTONE).name("§cПокупка");

            buy.lore(" ",
                    text(" §2ЛКМ §8- §7Купить §f1 престиж за §a$%s", comma(buyPrice)),
                    text(" §2ПКМ §8- §7Купить §f10 престижей за §a$%s", comma(buy10Price)));
            if (canBuyMax != 0)
                buy.addLore(text(" §2Q §8- §7Купить §f%s престижей за §a$%s", ((int) canBuyMax), comma(buyMaxPrice)));
            buy.addLore(" ");

            i.addItem(new BaseInventoryClickItem(4, buy, (c, e) -> {
                e.setCancelled(true);
                switch (e.getClick()) {
                    case DROP:
                        if (canBuyMax > 0)
                            if (player.getMoney() < buyMaxPrice)
                                OpPrison.send(p, "Вам не хватает §c$%s&r для покупки &c%s&f престижа&7(-ей)",
                                        comma(buyMaxPrice - player.getMoney()), canBuyMax);
                            else {
                                player.setMoney(player.getMoney() - buyMaxPrice);
                                player.setPrestige(player.getPrestige() + canBuyMax);
                                OpPrison.send(p, "Вы успешно купили §c%s§f престиж&7(-ей)&f за §a$%s", canBuyMax, comma(buyMaxPrice));
                                OpPrison.send(p, "Теперь ваш престиж &7- &c%s", player.getPrestige());
                                i.updateInventory(p);
                            }
                        break;
                    case RIGHT:
                        if (player.getMoney() < buy10Price)
                            OpPrison.send(p, "Вам не хватает §c$%s&r для покупки &c%s&f престижа&7(-ей)",
                                    comma(buy10Price - player.getMoney()), 10);
                        else {
                            player.setMoney(player.getMoney() - buy10Price);
                            player.setPrestige(player.getPrestige() + 10);
                            OpPrison.send(p, "Вы успешно купили §c%s§f престиж&7(-ей)&f за §a$%s", 10, comma(buy10Price));
                            OpPrison.send(p, "Теперь ваш престиж &7- &c%s", player.getPrestige());
                            i.updateInventory(p);
                        }
                        break;
                    default:
                        if (player.getMoney() < buyPrice)
                            OpPrison.send(p, "Вам не хватает §c$%s&r для покупки &c%s&f престижа&7(-ей)",
                                    comma(buyPrice - player.getMoney()), 1);
                        else {
                            player.setMoney(player.getMoney() - buyPrice);
                            player.setPrestige(player.getPrestige() + 1);
                            OpPrison.send(p, "Вы успешно купили §c%s§f престиж&7(-ей)&f за §a$%s", 1, comma(buyPrice));
                            OpPrison.send(p, "Теперь ваш престиж &7- &c%s", player.getPrestige());
                            i.updateInventory(p);
                        }
                        break;
                }
            }));
        }).openInventory(player.getHandle());
    }


}
