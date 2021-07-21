package ru.redline.opprison.utils;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.redline.opprison.OpPrison;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class Configuration extends YamlConfiguration {

    public static final Map<String, Configuration> configs = new ConcurrentHashMap<>();

    private File configFile;

    public Configuration(String path) {
        configs.put(path, this);
        configFile = new File(OpPrison.instance.getDataFolder(), path + ".yml");
        load();
    }

    @SneakyThrows
    public void load() {
        boolean exists;
        if (!(exists = configFile.exists())) {
            configFile.getParentFile().mkdirs();
            configFile.createNewFile();
        }
        load(configFile);
        if (!exists) {
            val defaultIS = OpPrison.instance.getResource(configFile.getName());
            if (defaultIS == null) setDefault();
            else load(new InputStreamReader(defaultIS, StandardCharsets.UTF_8));
            save();
        }
        onLoad();
    }

    @SneakyThrows
    public void save() {
        save(configFile);
    }

     public void onLoad() {

    }

    public void setDefault() {

    }
}
