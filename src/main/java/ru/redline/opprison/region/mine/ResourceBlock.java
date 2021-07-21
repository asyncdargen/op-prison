package ru.redline.opprison.region.mine;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Material;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Builder
@Data
public class ResourceBlock {

    private final Material material;
    private final int data;
    private final int chance;

    public static ResourceBlock fromString(String in) {
        try {
            String[] spl = in.split(":");
            if (spl.length < 2 || spl.length > 3) return null;
            else if (spl.length == 2) spl = (String[]) ArrayUtils.add(spl, "100");
            int chance = parseInt(spl[2]);
            int data = parseInt(spl[1]);
            Material material = Material.matchMaterial(spl[0]);

            if (chance == -1 || data == -1 || material == null) return null;
            return ResourceBlock.builder()
                    .material(material).chance(chance).data(data).build();
        } catch (Throwable e) {
            return null;
        }
    }

    public static List<ResourceBlock> fromList(List<String> blocks) {
        return blocks.stream().map(ResourceBlock::fromString)
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    protected static int parseInt(String in) {
        int result = 0;

        try {
            result = Integer.parseInt(in);
        } catch (Throwable e) {
            result = -1;
        }

        return result;
    }

}
