import ru.redline.opprison.pickaxe.PickaxeEnchant;

import java.util.Map;

public class map {
    public static void main(String[] args) {
        PickaxeEnchant a = PickaxeEnchant.EFFICIENCY;
        System.out.println(a == null);
        a = null;
        System.out.println(a == null);
    }
}
