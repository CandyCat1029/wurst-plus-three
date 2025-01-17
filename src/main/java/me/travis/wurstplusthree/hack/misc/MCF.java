package me.travis.wurstplusthree.hack.misc;

import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.hack.Hack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import org.lwjgl.input.Mouse;

@Hack.Registration(name = "Middle Click Friend", description = "middleclick adds friend", category = Hack.Category.MISC, isListening = false)
public class MCF extends Hack {

    private boolean isButtonDown = false;

    @Override
    public void onUpdate() {
        if (Mouse.isButtonDown(2)) {
            if (!this.isButtonDown && mc.currentScreen == null) {
                this.onClick();
            }
            this.isButtonDown = true;
        } else {
            this.isButtonDown = false;
        }
    }

    private void onClick() {
        Entity entity;
        RayTraceResult result = MCF.mc.objectMouseOver;
        if (result != null && result.typeOfHit == RayTraceResult.Type.ENTITY && (entity = result.entityHit) instanceof EntityPlayer) {
            WurstplusThree.FRIEND_MANAGER.toggleFriend(entity.getName());
        }
        this.isButtonDown = true;
    }

}
