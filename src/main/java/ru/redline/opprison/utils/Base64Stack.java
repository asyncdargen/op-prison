package ru.redline.opprison.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@UtilityClass
public class Base64Stack {

    public static String encodeInventory(Player player) {
        return encodeItems(player.getInventory().getContents());
    }

    public static String encodeAndClearInventory(Player player) {
        String result = encodeInventory(player);
        player.getInventory().clear();
        return result;
    }

    public static void decodeInventory(Player player, String data) {
        player.getInventory().clear();
        player.getInventory().setContents(decodeItems(data));
    }

    public static String encodeEndInventory(Player player) {
        return encodeItems(player.getEnderChest().getContents());
    }

    public static String encodeAndClearEndInventory(Player player) {
        String result = encodeEndInventory(player);
        player.getEnderChest().clear();
        return result;
    }

    public static void decodeEndInventory(Player player, String data) {
        player.getEnderChest().clear();
        player.getEnderChest().setContents(decodeItems(data));
    }

    @SneakyThrows
    public static String encodeItems(ItemStack[] contents) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

        dataOutput.writeInt(contents.length);

        for (ItemStack content : contents) {
            dataOutput.writeObject(content);
        }

        dataOutput.close();
        return Base64Coder.encodeLines(outputStream.toByteArray());
    }

    @SneakyThrows
    public static String encodeItem(ItemStack item) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
        dataOutput.writeObject(item);

        dataOutput.close();
        return Base64Coder.encodeLines(outputStream.toByteArray());
    }

    @SneakyThrows
    public static ItemStack[] decodeItems(String data){
        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
        BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

        ItemStack[] itemStacks = new ItemStack[dataInput.readInt()];

        for (int i = 0; i < itemStacks.length; i++) {
            ItemStack is = (ItemStack) dataInput.readObject();
            itemStacks[i] = is;
        }
        dataInput.close();
        return itemStacks;
    }

    @SneakyThrows
    public static ItemStack decodeItem(String data) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
        BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

        ItemStack is = (ItemStack) dataInput.readObject();

        dataInput.close();
        return is;
    }
}
