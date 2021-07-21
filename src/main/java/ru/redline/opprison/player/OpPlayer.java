package ru.redline.opprison.player;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.bukkit.entity.Player;
import ru.redline.opprison.OpPrison;
import ru.redline.opprison.crate.CrateType;
import ru.redline.opprison.pickaxe.Pickaxe;

import java.util.concurrent.ConcurrentMap;

@Data
@Builder
@AllArgsConstructor
public class OpPlayer {

    private Player handle;
    private ConcurrentMap<CrateType, Double> crates;
    private Pickaxe pickaxe;
    private Rank rank;
    private double prestige;
    private double blocks;
    private double money;
    private double multiplier;
    private double tokens;
    private double crystals;

    public double getFullMultiplier() {
        return getMultiplier() + 0; // TODO: 18.07.2021 add donate boost
    }

    public void setPrestige(double prestige) {
        int handred = (int) (this.prestige / 1000);
        this.prestige = prestige;
        int out = (int) (this.prestige / 1000 - handred);
        if (out > 0 && out != handred) {
            crystals += handred * 100;
            OpPrison.send(handle, "Вы докупили ещё &a%s &7тыс&r престижей и получили &b₪%s", handred, handred * 100);
        }
    }

    public static class OpPlayerBuilder {

        public OpPlayer buildDefaults(Player handle) {
            return handle(handle).pickaxe(new Pickaxe(handle.getPlayer())).crates(Maps.newConcurrentMap())
                    .rank(Rank.A).prestige(0).blocks(0).money(0).multiplier(0).tokens(0).crystals(0).build();
        }

    }
}
