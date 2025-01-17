package me.travis.wurstplusthree.hack.combat;

import com.mojang.authlib.GameProfile;
import me.travis.wurstplusthree.WurstplusThree;
import me.travis.wurstplusthree.event.events.PacketEvent;
import me.travis.wurstplusthree.event.events.Render3DEvent;
import me.travis.wurstplusthree.event.events.UpdateWalkingPlayerEvent;
import me.travis.wurstplusthree.hack.Hack;
import me.travis.wurstplusthree.hack.chat.AutoEz;
import me.travis.wurstplusthree.setting.type.*;
import me.travis.wurstplusthree.util.*;
import me.travis.wurstplusthree.util.elements.Colour;
import me.travis.wurstplusthree.util.elements.CrystalPos;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

@Hack.Registration(name = "Crystal Aura", description = "the goods", category = Hack.Category.COMBAT, isListening = false)
public class CrystalAura extends Hack {

    public static CrystalAura INSTANCE;

    public CrystalAura() {
        INSTANCE = this;
    }

    BooleanSetting place = new BooleanSetting("Place", true, this);
    BooleanSetting breaK = new BooleanSetting("Break", true, this);
    BooleanSetting antiWeakness = new BooleanSetting("Anti Weakness", true, this);

    DoubleSetting breakRange = new DoubleSetting("Break Range", 5.0, 0.0, 6.0, this);
    DoubleSetting placeRange = new DoubleSetting("Place Range", 5.0, 0.0, 6.0, this);
    DoubleSetting breakRangeWall = new DoubleSetting("Break Range Wall", 3.0, 0.0, 6.0, this);
    DoubleSetting placeRangeWall = new DoubleSetting("Place Range Wall", 3.0, 0.0, 6.0, this);

    IntSetting placeDelay = new IntSetting("Place Delay", 0, 0, 10, this);
    IntSetting breakDelay = new IntSetting("Break Delay", 1, 0, 10, this);

    IntSetting minHpPlace = new IntSetting("HP Enemy Place", 9, 0, 36, this);
    IntSetting minHpBreak = new IntSetting("HP Enemy Break", 8, 0, 36, this);
    IntSetting maxSelfDamage = new IntSetting("Max Self Damage", 5, 0, 36, this);

    EnumSetting rotateMode = new EnumSetting("Rotate", "Off", Arrays.asList("Off", "Packet", "Full"), this);

    BooleanSetting raytrace = new BooleanSetting("Raytrace", false, this);
    EnumSetting swing = new EnumSetting("Swing", "Mainhand", Arrays.asList("Mainhand", "Offhand", "None"), this);
    BooleanSetting placeSwing = new BooleanSetting("Place Swing", true, this);

    EnumSetting autoSwitch = new EnumSetting("Switch", "None", Arrays.asList("Mainhand", "None"), this);
    BooleanSetting antiSuicide = new BooleanSetting("Anti Suicide", true, this);

    BooleanSetting packetSafe = new BooleanSetting("Packet Safe", false, this);
    BooleanSetting predictCrystal = new BooleanSetting("Predict Crystal", true, this);
    BooleanSetting predictBlock = new BooleanSetting("Predict Block", true, this);
    BooleanSetting entityPredict = new BooleanSetting("Entity Motion Predict", true, this);
    IntSetting predictedTicks = new IntSetting("Predicted Ticks", 3, 0, 20, this);

    BooleanSetting palceObiFeet = new BooleanSetting("Place Feet Obi", false, this);
    BooleanSetting ObiYCheck = new BooleanSetting("Place Obi Y Check", false, this);
    BooleanSetting rotateObiFeet = new BooleanSetting("Place Feet Rotate", false, this);
    IntSetting timeoutTicksObiFeet = new IntSetting("Place Feet Timeout", 3, 0, 5, this);

    EnumSetting fastMode = new EnumSetting("Fast", "Ghost", Arrays.asList("Off", "Ignore", "Ghost", "Sound"), this);

    BooleanSetting thirteen = new BooleanSetting("1.13", false, this);

    BooleanSetting faceplace = new BooleanSetting("Tabbott", true, this);
    IntSetting facePlaceHP = new IntSetting("Tabbott HP", 8, 0, 36, this);
    IntSetting facePlaceDelay = new IntSetting("Tabbott Delay", 5, 0, 10, this);
    KeySetting fpbind = new KeySetting("Tabbott Bind", -1, this);

    BooleanSetting fuckArmour = new BooleanSetting("Armour Fucker", true, this);
    IntSetting fuckArmourHP = new IntSetting("Armour%", 20, 0, 100, this);

    BooleanSetting stopFPWhenSword = new BooleanSetting("Stop Faceplace Sword", false, this);
    BooleanSetting ignoreTerrain = new BooleanSetting("TerrainTrace", true, this);
    BooleanSetting crystalLogic = new BooleanSetting("CrystalCheck", false, this);

    BooleanSetting attackPacket = new BooleanSetting("AttackPacket", true, this);

    BooleanSetting chainMode = new BooleanSetting("Chain Mode", false, this);
    IntSetting chainCounter = new IntSetting("Chain Counter", 3, 0, 10, this);
    IntSetting chainStep = new IntSetting("Chain Step", 2, 0, 5, this);
    EnumSetting mode = new EnumSetting("Render", "Pretty", Arrays.asList("Pretty", "Solid", "Outline", "Circle"), this);
    BooleanSetting flat = new BooleanSetting("Flat", false, this);
    DoubleSetting hight = new DoubleSetting("FlatHeight", 0.2, -2.0, 0.0, this);
    IntSetting width = new IntSetting("Width", 1, 1, 10, this);
    DoubleSetting radius = new DoubleSetting("Radius", 0.7, 0.0, 5.0, this);
    ColourSetting renderFillColour = new ColourSetting("Fill Colour", new Colour(0, 0, 0, 255), this);
    ColourSetting renderBoxColour = new ColourSetting("Box Colour", new Colour(255, 255, 255, 255), this);
    BooleanSetting renderDamage = new BooleanSetting("RenderDamage", true, this);

    private final List<EntityEnderCrystal> attemptedCrystals = new ArrayList<>();

    public EntityPlayer ezTarget = null;
    public BlockPos renderBlock = null;

    private double renderDamageVal = 0;

    private float yaw;
    private float pitch;

    private boolean alreadyAttacking;
    private boolean placeTimeoutFlag;
    private boolean hasPacketBroke;
    private boolean isRotating;
    private boolean didAnything;
    private boolean facePlacing;

    private int currentChainCounter;
    private int chainCount;
    private int placeTimeout;
    private int breakTimeout;
    private int breakDelayCounter;
    private int placeDelayCounter;
    private int facePlaceDelayCounter;
    private int obiFeetCounter;

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public void onUpdateWalkingPlayerEvent(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 0 && this.rotateMode.is("Full")) {
            if (this.isRotating) {
                WurstplusThree.ROTATION_MANAGER.setPlayerRotations(yaw, pitch);
            }
            this.doCrystalAura();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && isRotating && rotateMode.is("Packet")) {
            final CPacketPlayer p = event.getPacket();
            p.yaw = yaw;
            p.pitch = pitch;
        }
        CPacketUseEntity packet;
        if (event.getStage() == 0 && event.getPacket() instanceof CPacketUseEntity && (packet = event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK
                && packet.getEntityFromWorld(mc.world) instanceof EntityEnderCrystal) {
            if (this.fastMode.is("Ghost")) {
                Objects.requireNonNull(packet.getEntityFromWorld(mc.world)).setDead();
                mc.world.removeEntityFromWorld(packet.entityId);
            }
            EntityEnderCrystal crystal = (EntityEnderCrystal) packet.getEntityFromWorld(mc.world);
            if (this.predictBlock.getValue() && place.getValue()) {
                if (crystal != null && mc.player.getHeldItemMainhand().getItem() instanceof ItemEndCrystal || mc.player.getHeldItemMainhand().getItem() instanceof ItemEndCrystal) {
                    for (EntityPlayer player : mc.world.playerEntities) {
                        if (player == null || crystal == null) return;
                        if (this.isBlockGood(crystal.getPosition().down(), player) != 0) {
                            BlockUtil.placeCrystalOnBlock(crystal.getPosition().down(), EnumHand.MAIN_HAND, true);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public void onPacketReceive(PacketEvent.Receive event) {
        SPacketSpawnObject packet;
        if (event.getPacket() instanceof SPacketSpawnObject && (packet = event.getPacket()).getType() == 51) {
            this.hasPacketBroke = false;
            try { // minecraft may update player list during us looping through it
                for (EntityPlayer target : mc.world.playerEntities) {
                    if (this.isCrystalGood(new EntityEnderCrystal(mc.world, packet.getX(), packet.getY(), packet.getZ()), target) != 0) {
                        if (this.predictCrystal.getValue()) {
                            CPacketUseEntity predict = new CPacketUseEntity();
                            predict.entityId = packet.getEntityID();
                            predict.action = CPacketUseEntity.Action.ATTACK;
                            mc.player.connection.sendPacket(predict);
                            if (!this.swing.is("None")) {
                                BlockUtil.swingArm(swing);
                            }
                            if (packetSafe.getValue()) {
                                this.hasPacketBroke = true;
                            }
                        }
                        break;
                    }
                }
            } catch (ConcurrentModificationException e) {
                e.printStackTrace();
            }
        }
        if (event.getPacket() instanceof SPacketDestroyEntities) {
            SPacketDestroyEntities packet_ = event.getPacket();
            for (int id : packet_.getEntityIDs()) {
                try {
                    Entity entity = mc.world.getEntityByID(id);
                    if (!(entity instanceof EntityEnderCrystal)) continue;
                    this.attemptedCrystals.remove(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (event.getPacket() instanceof SPacketSoundEffect && fastMode.getValue().equals("Sound")) {
            if (((SPacketSoundEffect) event.getPacket()).getCategory() == SoundCategory.BLOCKS && ((SPacketSoundEffect) event.getPacket()).getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                for (Entity crystal : mc.world.loadedEntityList) {
                    if (crystal instanceof EntityEnderCrystal)
                        if (crystal.getDistance(((SPacketSoundEffect) event.getPacket()).getX(), ((SPacketSoundEffect) event.getPacket()).getY(), ((SPacketSoundEffect) event.getPacket()).getZ()) <= breakRange.getValue()) {
                            crystal.setDead();
                        }
                }
            }
        }
    }

    @Override
    public void onUpdate() {
        if (!this.rotateMode.is("Full")) {
            this.doCrystalAura();
        }
    }

    private void doCrystalAura() {
        if (nullCheck()) {
            this.disable();
            return;
        }

        didAnything = false;
        if (HackUtil.shouldPause(this)) return;

        if (this.place.getValue() && placeDelayCounter > placeTimeout && (facePlaceDelayCounter >= facePlaceDelay.getValue() || !facePlacing)) {
            this.placeCrystal();
        }
        if (this.breaK.getValue() && breakDelayCounter > breakTimeout && !hasPacketBroke) {
            this.breakCrystal();
        }

        if (!didAnything) {
            ezTarget = null;
            isRotating = false;
            chainCount = chainStep.getValue();
            currentChainCounter = 0;
        }

        currentChainCounter++;
        breakDelayCounter++;
        placeDelayCounter++;
        facePlaceDelayCounter++;
        obiFeetCounter++;
    }

    private void placeCrystal() {
        BlockPos targetBlock = this.getBestBlock();
        if (targetBlock == null) return;

        placeDelayCounter = 0;
        facePlaceDelayCounter = 0;
        alreadyAttacking = false;
        boolean offhandCheck = false;

        if (mc.player.getHeldItemOffhand().getItem() != Items.END_CRYSTAL) {
            if (mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && autoSwitch.getValue().equals("Mainhand")) {
                if (this.findCrystalsHotbar() == -1) return;
                mc.player.inventory.currentItem = this.findCrystalsHotbar();
                mc.playerController.syncCurrentPlayItem();
            }
        } else {
            offhandCheck = true;
        }

        didAnything = true;
        if (mc.player.getHeldItemMainhand().getItem() instanceof ItemEndCrystal || mc.player.getHeldItemOffhand().getItem() instanceof ItemEndCrystal) {
            setYawPitch(targetBlock);
            BlockUtil.placeCrystalOnBlock(targetBlock, offhandCheck ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, placeSwing.getValue());
        }
    }

    private void breakCrystal() {
        EntityEnderCrystal crystal = this.getBestCrystal();
        if (crystal == null) return;
        if (antiWeakness.getValue() && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
            boolean shouldWeakness = true;
            if (mc.player.isPotionActive(MobEffects.STRENGTH)) {
                if (Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.STRENGTH)).getAmplifier() == 2) {
                    shouldWeakness = false;
                }
            }
            if (shouldWeakness) {
                if (!alreadyAttacking) {
                    this.alreadyAttacking = true;
                }
                int newSlot = -1;
                for (int i = 0; i < 9; i++) {
                    ItemStack stack = mc.player.inventory.getStackInSlot(i);
                    if (stack.getItem() instanceof ItemSword || stack.getItem() instanceof ItemTool) {
                        newSlot = i;
                        mc.playerController.updateController();
                        break;
                    }
                }
                if (newSlot != -1) {
                    mc.player.inventory.currentItem = newSlot;
                }
            }
        }
        didAnything = true;
        setYawPitch(crystal);
        EntityUtil.attackEntity(crystal, this.attackPacket.getValue());
        if (!this.swing.is("None")) {
            BlockUtil.swingArm(swing);
        }
        breakDelayCounter = 0;
    }

    private EntityEnderCrystal getBestCrystal() {
        double bestDamage = 0;
        EntityEnderCrystal bestCrystal = null;
        for (Entity e : mc.world.loadedEntityList) {
            if (!(e instanceof EntityEnderCrystal)) continue;
            EntityEnderCrystal crystal = (EntityEnderCrystal) e;
            for (EntityPlayer target : mc.world.playerEntities) {
                double targetDamage = this.isCrystalGood(crystal, target);
                if (targetDamage == 0) continue;
                if (targetDamage > bestDamage) {
                    bestDamage = targetDamage;
                    this.ezTarget = target;
                    bestCrystal = crystal;
                }
            }
        }
        if (this.ezTarget != null) {
            AutoEz.INSTANCE.targets.put(this.ezTarget.getName(), 20);
        }
        return bestCrystal;
    }


    public static Entity getPredictedPosition(Entity entity, double x) {
        if (x == 0) return entity;
        EntityPlayer e = null;
        double motionX = entity.posX - entity.lastTickPosX;
        double motionY = entity.posY - entity.lastTickPosY;
        double motionZ = entity.posZ - entity.lastTickPosZ;
        boolean shouldPredict = false;
        boolean shouldStrafe = false;
        double motion = Math.sqrt(Math.pow(motionX, 2) + Math.pow(motionZ, 2) + Math.pow(motionY, 2));
        if (motion > 0.1) {
            shouldPredict = true;
        }
        if (!shouldPredict) {
            return entity;
        }
        if (motion > 0.31) {
            shouldStrafe = true;
        }
        for (int i = 0; i < x; i++) {
            if (e == null) {
                if (isOnGround(0, 0, 0, entity)) {
                    motionY = shouldStrafe ? 0.4 : -0.07840015258789;
                }else {
                    motionY -= 0.08;
                    motionY *= 0.9800000190734863D;
                }
                e = placeValue(motionX, motionY, motionZ, (EntityPlayer) entity);
            }else {
                if (isOnGround(0, 0, 0, e)) {
                    motionY = shouldStrafe ? 0.4 : -0.07840015258789;
                }else {
                    motionY -= 0.08;
                    motionY *= 0.9800000190734863D;
                }
                e = placeValue(motionX, motionY, motionZ, e);
            }
        }
        return e;
    }
    public static boolean isOnGround(double x, double y, double z, Entity entity) {
        double d3 = y;
        List<AxisAlignedBB> list1 = Minecraft.getMinecraft().world.getCollisionBoxes(entity, entity.getEntityBoundingBox().expand(x, y, z));
        if (y != 0.0D) {
            int k = 0;
            for (int l = list1.size(); k < l; ++k) {
                y = (list1.get(k)).calculateYOffset(entity.getEntityBoundingBox(), y);
            }
        }
        return d3 != y && d3 < 0.0D;
    }

    public static EntityPlayer placeValue(double x, double y, double z, EntityPlayer entity) {
        List<AxisAlignedBB> list1 = Minecraft.getMinecraft().world.getCollisionBoxes(entity, entity.getEntityBoundingBox().expand(x, y, z));
        if (y != 0.0D) {
            int k = 0;
            for (int l = list1.size(); k < l; ++k)
            {
                y = (list1.get(k)).calculateYOffset(entity.getEntityBoundingBox(), y);
            }
            if (y != 0.0D) {
                entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(0.0D, y, 0.0D));
            }
        }
        if (x != 0.0D)
        {
            int j5 = 0;

            for (int l5 = list1.size(); j5 < l5; ++j5)
            {
                x = calculateXOffset(entity.getEntityBoundingBox(), x, list1.get(j5));
            }

            if (x != 0.0D) {
                entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(x, 0.0D, 0.0D));
            }
        }


        if (z != 0.0D)
        {
            int k5 = 0;

            for (int i6 = list1.size(); k5 < i6; ++k5)
            {
                z = calculateZOffset(entity.getEntityBoundingBox(), z, list1.get(k5));
            }

            if (z != 0.0D)
            {
                entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(0.0D, 0.0D, z));
            }
        }
        return entity;
    }
    public static double calculateXOffset(AxisAlignedBB other, double offsetX, AxisAlignedBB this1)
    {
        if (other.maxY > this1.minY && other.minY < this1.maxY && other.maxZ > this1.minZ && other.minZ < this1.maxZ)
        {
            if (offsetX > 0.0D && other.maxX <= this1.minX)
            {
                double d1 = (this1.minX - 0.3) - other.maxX;

                if (d1 < offsetX)
                {
                    offsetX = d1;
                }
            }
            else if (offsetX < 0.0D && other.minX >= this1.maxX)
            {
                double d0 = (this1.maxX + 0.3) - other.minX;

                if (d0 > offsetX)
                {
                    offsetX = d0;
                }
            }

        }
        return offsetX;
    }
    public static double calculateZOffset(AxisAlignedBB other, double offsetZ, AxisAlignedBB this1)
    {
        if (other.maxX > this1.minX && other.minX < this1.maxX && other.maxY > this1.minY && other.minY < this1.maxY)
        {
            if (offsetZ > 0.0D && other.maxZ <= this1.minZ)
            {
                double d1 = (this1.minZ - 0.3) - other.maxZ;

                if (d1 < offsetZ)
                {
                    offsetZ = d1;
                }
            }
            else if (offsetZ < 0.0D && other.minZ >= this1.maxZ)
            {
                double d0 = (this1.maxZ + 0.3) - other.minZ;

                if (d0 > offsetZ)
                {
                    offsetZ = d0;
                }
            }

        }
        return offsetZ;
    }


    private BlockPos getBestBlock() {
        if (getBestCrystal() != null && fastMode.is("Off")) {
            placeTimeoutFlag = true;
            return null;
        }

        if (placeTimeoutFlag) {
            placeTimeoutFlag = false;
            return null;
        }
        double bestDamage = 0;
        BlockPos bestPos = null;

        ArrayList<CrystalPos> validPos = new ArrayList<>();

        for (EntityPlayer target : mc.world.playerEntities) {
            if (target.getDistance(mc.player) > 15) continue;
            float f = target.width / 2.0F, f1 = target.height;
            target.setEntityBoundingBox(new AxisAlignedBB(target.posX - (double) f, target.posY, target.posZ - (double) f, target.posX + (double) f, target.posY + (double) f1, target.posZ + (double) f));
            Entity y = getPredictedPosition(target, predictedTicks.getValue());
            target.setEntityBoundingBox(y.getEntityBoundingBox());
            for (BlockPos blockPos : CrystalUtil.possiblePlacePositions(this.placeRange.getValue().floatValue(), !crystalLogic.getValue(), this.thirteen.getValue())) {
                double targetDamage = isBlockGood(blockPos, target);
                if (targetDamage == 0) continue;
                if (chainMode.getValue() && currentChainCounter >= chainCounter.getValue()) {
                    validPos.add(new CrystalPos(blockPos, targetDamage));
                } else {
                    if (targetDamage > bestDamage) {
                        bestDamage = targetDamage;
                        bestPos = blockPos;
                        ezTarget = target;
                    }
                }

            }
        }

        if (this.ezTarget != null) {
            AutoEz.INSTANCE.targets.put(this.ezTarget.getName(), 20);
        }

        /*
        a while ago someone told me that the reason crystals don't do max damage is bc NCP blocks
        crystal damage after so much has been done in a short time frame, but it works in a sort
        of counter way so that if you take less damage than before the counter will reset and youll
        be taking full damage again.. this basically does that (no idea how well it works in practice
        bc i feel chinese every time i turn it on)
         */
        if (chainMode.getValue() && currentChainCounter >= chainCounter.getValue()) {
            currentChainCounter = 0;
            validPos.sort(Comparator.comparing(CrystalPos::getDamage));
            Collections.reverse(validPos);
            if (validPos.size() <= chainCount) {
                if (validPos.isEmpty()) {
                    renderDamageVal = 0;
                    renderBlock = null;
                    return null;
                }
                CrystalPos pos = validPos.get(0);
                renderDamageVal = pos.getDamage();
                renderBlock = pos.getPos();
                return pos.getPos();
            }
            CrystalPos pos = validPos.get(chainCount);
            renderDamageVal = pos.getDamage();
            renderBlock = pos.getPos();
            bestPos = renderBlock;
            if (chainCount == 0) {
                chainCount = chainStep.getValue();
            } else {
                chainCount--;
            }
        } else {
            renderDamageVal = bestDamage;
            renderBlock = bestPos;
        }

        return bestPos;
    }

    private double isCrystalGood(EntityEnderCrystal crystal, EntityPlayer target) {
        if (this.isPlayerValid(target)) {
            if (mc.player.canEntityBeSeen(crystal)) {
                if (mc.player.getDistanceSq(crystal) > MathsUtil.square(this.breakRange.getValue().floatValue())) {
                    return 0;
                }
            } else {
                if (mc.player.getDistanceSq(crystal) > MathsUtil.square(this.breakRangeWall.getValue().floatValue())) {
                    return 0;
                }
            }
            if (crystal.isDead) return 0;
            if (attemptedCrystals.contains(crystal)) return 0;

            // set min damage to 2 if we want to kill the dude fast
            double miniumDamage;
            if (CrystalUtil.calculateDamage(crystal, target, ignoreTerrain.getValue()) >= minHpPlace.getValue()) {
                facePlacing = false;
                miniumDamage = this.minHpBreak.getValue();
            } else if ((EntityUtil.getHealth(target) <= facePlaceHP.getValue() && faceplace.getValue()) || (CrystalUtil.getArmourFucker(target, fuckArmourHP.getValue()) && fuckArmour.getValue()) || fpbind.isDown()) {
                miniumDamage = EntityUtil.isInHole(target) ? 0.5 : 2;
                facePlacing = true;
            } else {
                facePlacing = false;
                miniumDamage = this.minHpBreak.getValue();
            }

            double targetDamage = CrystalUtil.calculateDamage(crystal, target, ignoreTerrain.getValue());
            if (targetDamage < miniumDamage && EntityUtil.getHealth(target) - targetDamage > 0) return 0;
            double selfDamage = CrystalUtil.calculateDamage(crystal, mc.player, ignoreTerrain.getValue());
            if (selfDamage > maxSelfDamage.getValue()) return 0;
            if (EntityUtil.getHealth(mc.player) - selfDamage <= 0 && this.antiSuicide.getValue()) return 0;

            return targetDamage;
        }

        return 0;
    }

    private double isBlockGood(BlockPos blockPos, EntityPlayer target) {
        if (this.isPlayerValid(target)) {
            // if raytracing and cannot see block
            if (!CrystalUtil.canSeePos(blockPos) && raytrace.getValue()) return 0;
            // if cannot see pos use wall range, else use normal
            if (!CrystalUtil.canSeePos(blockPos)) {
                if (mc.player.getDistanceSq(blockPos) > MathsUtil.square(this.placeRangeWall.getValue().floatValue())) {
                    return 0;
                }
            } else {
                if (mc.player.getDistanceSq(blockPos) > MathsUtil.square(this.placeRange.getValue().floatValue())) {
                    return 0;
                }
            }

            // set min damage to 2/.5 if we want to kill the dude fast
            double miniumDamage;
            if (CrystalUtil.calculateDamage(blockPos, target, ignoreTerrain.getValue()) >= minHpPlace.getValue()) {
                facePlacing = false;
                miniumDamage = this.minHpBreak.getValue();
            } else if ((EntityUtil.getHealth(target) <= facePlaceHP.getValue() && faceplace.getValue()) ||
                    (CrystalUtil.getArmourFucker(target, fuckArmourHP.getValue()) && fuckArmour.getValue()) || fpbind.isDown()) {
                miniumDamage = EntityUtil.isInHole(target) ? 0.5 : 2;
                facePlacing = true;
            } else {
                miniumDamage = this.minHpPlace.getValue();
                facePlacing = false;
            }

            double targetDamage = CrystalUtil.calculateDamage(blockPos, target, ignoreTerrain.getValue());
            if (targetDamage < miniumDamage && EntityUtil.getHealth(target) - targetDamage > 0) return 0;
            double selfDamage = CrystalUtil.calculateDamage(blockPos, mc.player, ignoreTerrain.getValue());
            if (selfDamage > maxSelfDamage.getValue()) return 0;
            if (EntityUtil.getHealth(mc.player) - selfDamage <= 0 && this.antiSuicide.getValue()) return 0;

            return targetDamage;
        }

        return 0;
    }

    private boolean isPlayerValid(EntityPlayer player) {
        if (player.getHealth() + player.getAbsorptionAmount() <= 0 || player == mc.player) return false;
        if (WurstplusThree.FRIEND_MANAGER.isFriend(player.getName())) return false;
        if (player.getName().equals(mc.player.getName())) return false;
        if (player.getDistanceSq(mc.player) > 13 * 13) return false;
        if (this.palceObiFeet.getValue() && obiFeetCounter >= timeoutTicksObiFeet.getValue() && mc.player.getDistance(player) < 5) {
            this.blockObiNextToPlayer(player);
        }
        return !stopFPWhenSword.getValue() || mc.player.getHeldItemMainhand().getItem() != Items.DIAMOND_SWORD;
    }

    private void blockObiNextToPlayer(EntityPlayer player) {
        if(ObiYCheck.getValue() && Math.floor(player.posY) == Math.floor(mc.player.posY))return;
        obiFeetCounter = 0;
        BlockPos pos = EntityUtil.getFlooredPos(player).down();
        if (EntityUtil.isInHole(player) || mc.world.getBlockState(pos).getBlock() == Blocks.AIR) return;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                BlockPos checkPos = pos.add(i, 0, j);
                if (mc.world.getBlockState(checkPos).getMaterial().isReplaceable()) {
                    BlockUtil.placeBlock(checkPos, PlayerUtil.findObiInHotbar(), rotateObiFeet.getValue(), rotateObiFeet.getValue(), swing);
                }
            }
        }
    }

    private int findCrystalsHotbar() {
        for (int i = 0; i < 9; i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.END_CRYSTAL) {
                return i;
            }
        }
        return -1;
    }

    private void setYawPitch(EntityEnderCrystal crystal) {
        float[] angle = MathsUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), crystal.getPositionEyes(mc.getRenderPartialTicks()));
        this.yaw = angle[0];
        this.pitch = angle[1];
        this.isRotating = true;
    }

    private void setYawPitch(BlockPos pos) {
        float[] angle = MathsUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((float) pos.getX() + 0.5f, (float) pos.getY() + 0.5f, (float) pos.getZ() + 0.5f));
        this.yaw = angle[0];
        this.pitch = angle[1];
        this.isRotating = true;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (this.renderBlock == null) return;

        boolean outline = false;
        boolean solid = false;
        if (!mode.is("Circle")) {
            switch (mode.getValue()) {
                case "Pretty":
                    outline = true;
                    solid = true;
                    break;
                case "Solid":
                    outline = false;
                    solid = true;
                    break;
                case "Outline":
                    outline = true;
                    solid = false;
                    break;
            }
            RenderUtil.drawBoxESP((flat.getValue()) ? new BlockPos(renderBlock.getX(), renderBlock.getY()+1, renderBlock.getZ()) : renderBlock, renderFillColour.getValue(), renderBoxColour.getValue(), width.getValue(), outline, solid, true, (flat.getValue()) ? hight.getValue() : 0f, false, false, false, false, 0);
        } else {
            RenderUtil.drawCircle(renderBlock.getX(), (flat.getValue()) ? renderBlock.getY() + 1: renderBlock.getY(), renderBlock.getZ(), radius.getValue().floatValue(), renderBoxColour.getValue());
        }
        if (renderDamage.getValue()) {
            RenderUtil.drawText(renderBlock, ((Math.floor(this.renderDamageVal) == this.renderDamageVal) ? Integer.valueOf((int) this.renderDamageVal) : String.format("%.1f", this.renderDamageVal)) + "");
        }
    }

    @Override
    public void onEnable() {
        placeTimeout = this.placeDelay.getValue();
        breakTimeout = this.breakDelay.getValue();
        placeTimeoutFlag = false;
        isRotating = false;
        ezTarget = null;
        facePlacing = false;
        chainCount = chainStep.getValue();
        attemptedCrystals.clear();
        hasPacketBroke = false;
        placeTimeoutFlag = false;
        alreadyAttacking = false;
        currentChainCounter = 0;
        obiFeetCounter = 0;
    }

    @Override
    public String getDisplayInfo() {
        return (facePlacing ? "FacePlacing " : "Chasing ") + (this.ezTarget != null ? this.ezTarget.getName() : "");
    }

    // terrain ignoring raytrace stuff made by wallhacks_ and node3112
    // moved to CyrstalUtil ~travis
}
