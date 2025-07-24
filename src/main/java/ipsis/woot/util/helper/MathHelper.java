package ipsis.woot.util.helper;

public class MathHelper {

    public static int clampLooting(int looting) {
        return Math.clamp(looting, 0, 3);
    }
}
