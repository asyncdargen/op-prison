package ru.redline.opprison.command.admin;

import javafx.scene.transform.Scale;
import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapFont;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import ru.redline.core.bukkit.command.BukkitCommand;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.bukkit.Bukkit.*;

public class MapCommand extends BukkitCommand<Player> {

    public MapCommand() {
        super("map");
    }

    @Override
    protected void onExecute(Player player, String[] strings) {
        val map = createMap(getWorlds().get(0));
        map.getRenderers().forEach(map::removeRenderer);
        map.addRenderer(new PictureRenderer(MapView.Scale.FARTHEST, strings[0]));
        player.getInventory().addItem(new ItemStack(Material.MAP, 1, map.getId()));
    }

    public static class PictureRenderer extends MapRenderer {

        private File picture;
        private MapView.Scale scale;
        private String url;
        private boolean drawed = false;
        private int x;
        private int y;

        public PictureRenderer(MapView.Scale scale, String url) {
            this.scale = scale;
            this.url = url;
            this.x = 0;
            this.y = 0;
        }

        @SneakyThrows
        public void render(MapView map, MapCanvas canvas, Player p) {
            if (drawed) return;
            map.setScale(scale);
            canvas.drawImage(x,y, ImageIO.read(new URL(url)));
            drawed = true;

        }
    }
}
