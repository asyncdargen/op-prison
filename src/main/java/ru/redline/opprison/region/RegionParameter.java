package ru.redline.opprison.region;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum RegionParameter {

    BLOCK_BREAK("block-break"),
    BLOCK_PLACE("block-place"),
    BLOCK_USE("block-use"),
    ENTITY_USE("entity-use"),
    FOOD_CHANGE("food-change"),
    SPAWN_ENTITY("spawn-entity"),
    DAMAGE("damage"),
    PHYSIC("physic"),
    PVP("pvp");

    private final String id;
    private final boolean defaultState;

    RegionParameter(String id) {
        this(id, false);
    }

    public static RegionParameter byId(String id) {
        for (RegionParameter param : values()) {
            if (param.getId().equals(id)) return param;
        }
        return null;
    }

    public String toString() {
        return id;
    }

    public static List<String> toList(Map<RegionParameter, Boolean> params) {
        return params.keySet().stream().map(p -> p + ":" + params.get(p)).collect(Collectors.toList());
    }

    public static Map<RegionParameter, Boolean> fromList(List<String> in) {
        val params = new HashMap<RegionParameter, Boolean>();
        in.stream()
                .map(p -> p.toLowerCase().split(":"))
                .filter(p -> p.length == 2)
                .filter(p -> byId(p[0]) != null)
                .forEach(p -> params.put(byId(p[0]), Boolean.parseBoolean(p[1])));
        return params;
    }

}
