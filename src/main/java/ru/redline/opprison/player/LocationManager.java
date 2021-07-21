package ru.redline.opprison.player;

import lombok.Getter;
import lombok.val;
import org.bukkit.Location;
import ru.redline.opprison.utils.Configuration;
import ru.redline.opprison.utils.LocationUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class LocationManager extends Configuration {

    private Map<String, Location> locationMap;

    public LocationManager() {
        super("locations");
    }

    public void onLoad() {
        val tmp = new ConcurrentHashMap<String, Location>();
        getConfigurationSection("locations").getKeys(false).forEach(l -> {
            val location = LocationUtil.fromString(getString("locations." + l), null);
            if (location == null)
                return;
            tmp.put(l, location);
        });
        locationMap = tmp;
    }

    public Location byId(String id) {
        return locationMap.get(id) == null ? byId("spawn") : locationMap.get(id);
    }
}
