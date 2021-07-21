package ru.redline.opprison.player;

import com.google.common.reflect.TypeToken;
import lombok.Getter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.redline.core.bukkit.CorePlugin;
import ru.redline.core.bukkit.util.JsonUtil;
import ru.redline.core.global.ApiManager;
import ru.redline.core.global.database.RemoteDatabaseConnectionHandler;
import ru.redline.core.global.database.RemoteDatabaseTable;
import ru.redline.core.global.database.query.RemoteDatabaseRowType;
import ru.redline.core.global.database.query.row.TypedQueryRow;
import ru.redline.core.global.database.query.row.ValueQueryRow;
import ru.redline.opprison.crate.CrateType;
import ru.redline.opprison.pickaxe.Pickaxe;
import ru.redline.opprison.player.event.PlayerLoadedEvent;
import ru.redline.opprison.utils.Base64Stack;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import static ru.redline.core.global.database.query.RemoteDatabaseRowType.*;

@Getter
public class PlayerManager extends BukkitRunnable {

    private final Map<String, OpPlayer> cache = new ConcurrentHashMap<>();

    private final Type mapType = new TypeToken<ConcurrentMap<CrateType, Integer>>(){}.getType();

    private final RemoteDatabaseTable table;
    private final RemoteDatabaseConnectionHandler handler;

    public PlayerManager(CorePlugin pl) {
        handler = ApiManager.getConnectionHandler("opprison");
        handler.newDatabaseQuery("players").createTableQuery().setCanCheckExists(true)
                .queryRow(queryRow("id", INT).index(TypedQueryRow.IndexType.PRIMARY).index(TypedQueryRow.IndexType.AUTO_INCREMENT))
                .queryRow(queryRow("name", TEXT))
                .queryRow(queryRow("prestige", DOUBLE))
                .queryRow(queryRow("blocks", DOUBLE))
                .queryRow(queryRow("money", DOUBLE))
                .queryRow(queryRow("multiplier", DOUBLE))
                .queryRow(queryRow("tokens", DOUBLE))
                .queryRow(queryRow("crystals", DOUBLE))
                .queryRow(queryRow("rank", INT))
                .queryRow(queryRow("pickaxe", TEXT))
                .queryRow(queryRow("crates", TEXT))
                .queryRow(queryRow("ender", TEXT))
                .queryRow(queryRow("inventory", TEXT))
                .executeSync(handler);
        table = handler.getTable("players");
        runTaskTimerAsynchronously(pl, 1, 20 * TimeUnit.MINUTES.toSeconds(5));
    }

    public void load(Player player) {
        table.newDatabaseQuery().selectQuery().queryRow(valueRow("name", player.getName())).executeQueryAsync(handler).thenAccept(rs -> {
            val builder = OpPlayer.builder();
            OpPlayer data;
            if (rs.next()) {
                Base64Stack.decodeInventory(player, rs.getString("inventory"));
                Base64Stack.decodeEndInventory(player, rs.getString("ender"));
                data = builder.handle(player)
                        .prestige(rs.getDouble("prestige"))
                        .blocks(rs.getDouble("blocks"))
                        .money(rs.getDouble("money"))
                        .multiplier(rs.getDouble("multiplier"))
                        .tokens(rs.getDouble("tokens"))
                        .crystals(rs.getDouble("crystals"))
                        .rank(Rank.fromOrdinal(rs.getInt("rank")))
                        .pickaxe(JsonUtil.fromJson(rs.getString("pickaxe"), Pickaxe.class))
                        .crates(JsonUtil.GSON.fromJson(rs.getString("crates"), mapType)).build();
            } else {
                data = builder.buildDefaults(player);
                insert(data);
            }
            cache.put(player.getName().toLowerCase(), data);
            Bukkit.getPluginManager().callEvent(new PlayerLoadedEvent(data));
        });
    }

    public void insert(OpPlayer data) {
        table.newDatabaseQuery().insertQuery()
                .queryRow(valueRow("name", data.getHandle().getName()))
                .queryRow(valueRow("prestige", data.getPrestige()))
                .queryRow(valueRow("blocks", data.getBlocks()))
                .queryRow(valueRow("money", data.getMoney()))
                .queryRow(valueRow("multiplier", data.getMultiplier()))
                .queryRow(valueRow("tokens", data.getTokens()))
                .queryRow(valueRow("crystals", data.getCrystals()))
                .queryRow(valueRow("rank", data.getRank().ordinal()))
                .queryRow(valueRow("pickaxe", JsonUtil.toJson(data.getPickaxe()).getBytes(StandardCharsets.UTF_8)))
                .queryRow(valueRow("crates", JsonUtil.toJson(data.getCrates())))
                .queryRow(valueRow("ender", Base64Stack.encodeEndInventory(data.getHandle())))
                .queryRow(valueRow("inventory", Base64Stack.encodeInventory(data.getHandle())))
                .executeAsync(handler);
    }

    public void unload(Player player) {
        save(cache.remove(player.getName().toLowerCase()));
    }

    public void save(OpPlayer data) {
        handler.getExecuteHandler().executeUpdate(true,//language=SQL
                "UPDATE " + table.getName() + " SET `prestige` = ?, `blocks` = ?, `money` = ?, `multiplier` = ?, `tokens` = ?, `crystals` = ?, `rank` = ?, `pickaxe` = ?, `crates` = ?, `ender` = ?, `inventory` = ? WHERE `name` = ?",
                data.getPrestige(), data.getBlocks(), data.getMoney(), data.getMultiplier(), data.getTokens(), data.getCrystals(), data.getRank().ordinal(), JsonUtil.toJson(data.getPickaxe()).getBytes(StandardCharsets.UTF_8), JsonUtil.toJson(data.getCrates()), Base64Stack.encodeEndInventory(data.getHandle()), Base64Stack.encodeInventory(data.getHandle()), data.getHandle().getName());
    }

    public OpPlayer getPlayer(Player player) {
        return getPlayer(player.getName());
    }

    public OpPlayer getPlayer(String name) {
        return cache.get(name.toLowerCase());
    }

    public void run() {
        cache.values().forEach(this::save);
    }

    private final TypedQueryRow queryRow(String name, RemoteDatabaseRowType type) {
        return new TypedQueryRow(type, name).index(TypedQueryRow.IndexType.NOT_NULL);
    }

    private final ValueQueryRow valueRow(String name, Object value) {
        return new ValueQueryRow(name, value);
    }



}
