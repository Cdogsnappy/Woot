package ipsis.woot.setup;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;


public class ClientProxy implements IProxy {

    @Override
    public Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public Level getClientWorld() {
        return Minecraft.getInstance().level;
    }
}
