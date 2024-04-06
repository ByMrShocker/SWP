package bymrshocker.swp.data;

import bymrshocker.swp.ShockerWeaponsPlugin;
import bymrshocker.swp.playerfuncs.UFunctionLibrary;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class SWeapon {

    private ItemStack weaponItem;
    private String weaponID;
    private String reqAmmoType;
    private int firingMode = 1;
    private int shotsDelay = 10;
    private int shotsPerClick = 3;
    private int clickCooldown = 10;
    private int slotID;


    private ShockerWeaponsPlugin plugin;


    private class inventoryCheckResult {
        public boolean result = false;
        public String magID = "null";
        public int slot = -1;
        public ItemStack item;
    }


    public void playerRightClick(PlayerInteractEvent event, ShockerWeaponsPlugin plugin) {
        this.plugin = plugin;
        weaponItem = event.getItem();
        UFunctionLibrary functionLibrary = plugin.getFunctionLibrary();

        if (!canFire(event)) {
            onFireInterrupted(event.getPlayer());
            return;
        }
        weaponID = plugin.getFunctionLibrary().getItemNBTString(weaponItem, "weaponID");
        slotID = event.getPlayer().getInventory().getHeldItemSlot();

        shotsPerClick = plugin.getConfigWeaponInt(weaponID, "burst");
        shotsDelay = plugin.getConfigWeaponInt(weaponID, "burstPeriod");
        clickCooldown = plugin.getConfigWeaponInt(weaponID, "burstCD");
        firingMode = plugin.getConfigWeaponInt(weaponID, "firingMode");

        functionLibrary.setItemNBTString(getSelectedItem(event.getPlayer()), event.getPlayer(), "state", "fire");
        functionLibrary.weaponFireTimer(this, event.getPlayer(), shotsPerClick, shotsDelay, 0);
    }

    public void playerLeftClick(Player player, ShockerWeaponsPlugin plugin) {
        this.plugin = plugin;
        weaponItem = player.getInventory().getItemInMainHand();
        UFunctionLibrary functionLibrary = plugin.getFunctionLibrary();
        slotID = player.getInventory().getHeldItemSlot();

        if (!canReload(player)) return;


        weaponReload(player);
        //functionLibrary.setItemNBTInt(getSelectedItem(player), player, "ammo", 30);

    }


    public void weaponReloadSound(Player player, int step) {
        String weaponID = plugin.getFunctionLibrary().getItemNBTString(getSelectedItem(player), "weaponID");
        String sound = plugin.getConfigWeaponString(weaponID, "soundReload" + String.valueOf(step));
        player.playSound(player.getLocation(), sound, SoundCategory.PLAYERS, 1, 1);
    }


    private void weaponReload(Player player) {

        String prevMag;
        String newMag;
        String weaponID = plugin.getFunctionLibrary().getItemNBTString(getSelectedItem(player), "weaponID");
        String cal = plugin.getConfigWeaponString(weaponID, "cal");
        int reloadTime = plugin.getConfigWeaponInt(weaponID, "reloadTime");

        prevMag = plugin.getFunctionLibrary().getItemNBTString(getSelectedItem(player), "mag");

        inventoryCheckResult inventoryCheckResult = findMagInventory(player, cal, false, -99);

        newMag = inventoryCheckResult.magID;

        //System.out.println("prevMag = " + prevMag);
        //System.out.println("newMag = " + newMag);


        if (!newMag.equals("null") || !prevMag.equals("null")) {
            plugin.getFunctionLibrary().weaponReloadTimer(this, player, reloadTime, prevMag, newMag, cal);
        }

    }

//разрядить магазин и выдать
    public int weaponRemoveMag(Player player, String oldMag) {

        //("weaponRemoveMag");

        if (oldMag == null || oldMag.equals("null")) return -1;
        if (plugin.getFunctionLibrary().getItemNBTString(getSelectedItem(player),"mag").equals("null")) return -1;

        String ammoType = plugin.getFunctionLibrary().getItemNBTString(getSelectedItem(player), "ammoType");
        String cal = plugin.getConfigMagString(oldMag, "cal");

        int hadAmmo = plugin.getFunctionLibrary().getItemNBTInt(getSelectedItem(player), "ammo");
        plugin.getFunctionLibrary().setItemNBTString(getSelectedItem(player), player, "mag", "null");
        plugin.getFunctionLibrary().setItemNBTString(getSelectedItem(player), player, "ammoType", "null");
        plugin.getFunctionLibrary().setItemNBTInt(getSelectedItem(player), player, "ammo", 0);


        Material material;
        String loadedMaterial = plugin.getConfig().getConfigurationSection("Global").getString("magMaterial");
        material = Material.valueOf(loadedMaterial);
        ItemStack itemToGive = new ItemStack(material);

        ReadWriteNBT nbt = NBT.itemStackToNBT(itemToGive);
        nbt.getOrCreateCompound("tag").getOrCreateCompound("ShockerWeapons");
        nbt.getCompound("tag").getCompound("ShockerWeapons").setString("magID", oldMag);
        nbt.getCompound("tag").getCompound("ShockerWeapons").setString("cal", cal);
        nbt.getCompound("tag").getCompound("ShockerWeapons").setInteger("ammo", hadAmmo);
        if (hadAmmo > 0) nbt.getCompound("tag").getCompound("ShockerWeapons").setString("ammoType", ammoType);
        else nbt.getCompound("tag").getCompound("ShockerWeapons").setString("ammoType", "null");



        itemToGive = NBT.itemStackFromNBT(nbt);


        String displayName = plugin.getConfigMagString(oldMag, "name");

        ItemMeta meta = itemToGive.getItemMeta();

        meta.setCustomModelData(plugin.getConfigMagInt(oldMag, "modelData"));
        //meta.displayName(Component.text(plugin.getConfigMagString(oldMag, "name"), styleMagItem()));
        meta.displayName(LegacyComponentSerializer.legacy('&').deserialize(plugin.getConfigMagString(oldMag, "name")).decoration(TextDecoration.ITALIC, false));

        List<String> loreToAdd = plugin.getConfig().getConfigurationSection("mag").getConfigurationSection(oldMag).getStringList("lore");

        meta = plugin.getFunctionLibrary().setItemLore(meta, loreToAdd, itemToGive);

        itemToGive.setItemMeta(meta);

        int newslot = findEmptySlotForMag(player);

        if (newslot != -1) {
            player.getInventory().setItem(newslot, itemToGive);
            return newslot;
        }
        else {
        player.getWorld().dropItemNaturally(player.getLocation(), itemToGive);
        }
        return -1;
        //player.getInventory().addItem(itemToGive);
    }

    public boolean weaponInsertMag(String newMag, Player player, String cal, int oldmagslot) {

        //System.out.println("weaponInsertMag");
        inventoryCheckResult inventoryCheckResult = findMagInventory(player, cal, true, oldmagslot);
        String removedMag = inventoryCheckResult.magID;
        loadDataFromMag(inventoryCheckResult.item, player);

        if (inventoryCheckResult.item == null) return false;

        if (inventoryCheckResult.item.getAmount() > 0) {

            ItemStack itemStack = inventoryCheckResult.item;
            int amount = itemStack.getAmount();
            amount--;
            itemStack.setAmount(amount);
            player.getInventory().setItem(inventoryCheckResult.slot, itemStack);

        }
        else {
            player.getInventory().removeItem(inventoryCheckResult.item);
        }


        plugin.getFunctionLibrary().setItemNBTString(player.getInventory().getItemInMainHand(), player, "mag", removedMag);
        //System.out.println("removedMag: " + removedMag);
        return (removedMag.equals("null"));


    }


    private void loadDataFromMag(ItemStack item, Player player){
        int newAmmo = plugin.getFunctionLibrary().getItemNBTInt(item, "ammo");
        String newAmmoType = plugin.getFunctionLibrary().getItemNBTString(item, "ammoType");

        ReadWriteNBT nbt = NBT.itemStackToNBT(player.getInventory().getItemInMainHand());
        nbt.getCompound("tag").getCompound("ShockerWeapons").setString("ammoType", newAmmoType);
        nbt.getCompound("tag").getCompound("ShockerWeapons").setInteger("ammo", newAmmo);

        player.getInventory().setItemInMainHand(NBT.itemStackFromNBT(nbt));

    }


    private String getMagCal(Player player, ItemStack item) {

        String magID = plugin.getFunctionLibrary().getItemNBTString(item, "magID");
        if (magID.equals("null")) return "null";

        return plugin.getConfigMagString(magID, "cal");


    }

    private int findEmptySlotForMag(Player player) {
        Inventory inv = player.getInventory();

        for(int i = 0; i <= 35; i++) {

            ItemStack item = inv.getItem(i);

            if (item == null) {

                return i;

            }
        }

        return -1;

    }



    private inventoryCheckResult findMagInventory(Player player, String cal, boolean removeItem, int ignoreSlot){

        Inventory inv = player.getInventory();
        String material = plugin.getConfig().getConfigurationSection("Global").getString("magMaterial");

        inventoryCheckResult toReturn = new inventoryCheckResult();


        for(int i = 0; i < inv.getSize(); i++) {
            if (i == ignoreSlot) continue;

            ItemStack item = inv.getItem(i);

            if (item != null) {


                if (!item.getType().toString().equals(material)) continue;

                String itemCal = getMagCal(player, item);

                if (itemCal.equals(cal)) {


                    if (plugin.getFunctionLibrary().getItemNBTInt(item, "ammo") == 0) continue;

                    String magID = plugin.getFunctionLibrary().getItemNBTString(item, "magID");

                    //if (removeItem) {
//
                    //    //разберись со стаком предметов или запрети магазинам стакаться
//
                    //    loadDataFromMag(item, player);
                    //    player.getInventory().clear(i);
//
                    //    toReturn.magID = magID;
                    //    toReturn.slot = i;
                    //    toReturn.item = item;
                    //    toReturn.result = true;
//
                    //    return toReturn;
//
                    //}

                    if (!magID.equals("null")) {
                        toReturn.magID = magID;
                        toReturn.slot = i;
                        toReturn.item = item;
                        toReturn.result = true;
                        return toReturn;
                    }
                    return toReturn;

                }
            }
        }
        //System.out.println("FindMagInv: last return");
        return toReturn;
    }





    public void weaponFire(Player player){
        //plugin.getFunctionLibrary().executeConsoleCommand("say FIRE");
        weaponRaycast(player);
        player.getWorld().playSound(player.getLocation(), "minecraft:swd.ak47_shoot", SoundCategory.PLAYERS, 1, 1);



    }




    private void weaponRaycast(Player player) {

        Vector hitPosition = new Vector(0, 0, 0);
        RayTraceResult hitResultE = new RayTraceResult(hitPosition);
        RayTraceResult hitResultB = new RayTraceResult(hitPosition);

        hitResultE = player.rayTraceEntities(30,true);
        hitResultB = player.rayTraceBlocks(30, FluidCollisionMode.NEVER);

        applyRaycastResult(hitResultB, player);
        applyRaycastResult(hitResultE, player);
        visualFireTrace(player);


    }

    private void applyRaycastResult(RayTraceResult hitResult, Player player){
        if (hitResult != null) {
            if (hitResult.getHitBlock() != null) {

                Location hitLocation = hitResult.getHitPosition().toLocation(player.getWorld());
                weaponRaycastHitBlock(player, hitLocation, hitResult.getHitBlock().getBlockData());

            } else if (hitResult.getHitEntity() != null) {

                weaponRaycastHitEntity(player, hitResult, hitResult.getHitEntity());

            }
        }

    }




    private void weaponRaycastHitBlock(Player player, Location hitLocation, BlockData blockData) {


        //Location location = hitLocation;
        //location.add(-0.5, -0.5, -0.5);

        //Vector direction = new Vector(0, 0, 0);
        //direction = player.getEyeLocation().toVector().subtract(hitLocation.toVector());
        //direction.normalize();
        //location.add(direction);


        player.getWorld().spawnParticle(Particle.BLOCK_CRACK, hitLocation, 10, blockData);


    }

    private void weaponRaycastHitEntity(Player player, RayTraceResult hitResult, Entity hitEntity) {
        if (hitEntity instanceof  LivingEntity) {
            int toDamage = selectDamage(player, (LivingEntity)hitEntity);
            if (toDamage > 0) ((LivingEntity)hitEntity).damage(toDamage, player);
            else if (toDamage == -1) {
                System.out.println("WARNING! " + player.displayName() + "uses ammo with invalid caliber!");
            } else if (toDamage == -2) {
                System.out.println("WARNING! " + player.displayName() + "uses ammo with invalid ammoType!");
            }
        }
    }


    private int selectDamage(Player player, LivingEntity entity) {
        //тут считать урон от брони

        String ammoType = plugin.getFunctionLibrary().getItemNBTString(player.getInventory().getItemInMainHand(), "ammoType");

        String weaponID = plugin.getFunctionLibrary().getItemNBTString(player.getInventory().getItemInMainHand(), "weaponID");

        String cal = plugin.getConfigWeaponString(weaponID, "cal");

        ItemStack armorItem = entity.getEquipment().getChestplate();

        int armorType = plugin.getFunctionLibrary().getItemNBTInt(armorItem, "armorClass");

        //System.out.println("cal = " + cal);
        //System.out.println("weaponID = " + weaponID);
        //System.out.println("ammoType = " + ammoType);

        if (!plugin.getConfig().getConfigurationSection("ammo").getKeys(false).contains(cal)) return -1;
        if (!plugin.getConfig().getConfigurationSection("ammo").getConfigurationSection(cal).getKeys(false).contains(ammoType)) return -2;

        if (armorType >= 2 && armorType <= 5) {
            return plugin.getConfig().getConfigurationSection("ammo").getConfigurationSection(cal).getConfigurationSection(ammoType).getInt("damage" + armorType);
        }

        return plugin.getConfig().getConfigurationSection("ammo").getConfigurationSection(cal).getConfigurationSection(ammoType).getInt("damage0");




    }


    private void visualFireTrace(Player player) {

        Location startLocation = player.getLocation();

        Vector startOffset = plugin.getConfig().getConfigurationSection("Global").getVector("bulletParticleOffset");


        Location eyeLocation = player.getEyeLocation();

        Vector direction = eyeLocation.getDirection().normalize();

        Location offsetLocation = plugin.getFunctionLibrary().getRelativeLocation(player, eyeLocation, startOffset);
        
        String particleName = plugin.getConfig().getConfigurationSection("Global").getString("bulletParticle");
        double speed = plugin.getConfig().getConfigurationSection("Global").getDouble("bulletParticleSpeed");

        player.spawnParticle(Particle.valueOf(particleName), offsetLocation, 0, direction.getX() * speed, direction.getY() * speed, direction.getZ() * speed);


        //player.spawnParticle(Particle.FLAME, startLocation, 1, direction.getX(), direction.getY(), direction.getZ(), 0);


        //player.spawnParticle(Particle.FLAME, player.getEyeLocation().add(0, 1.5, 0), 1, 0, 0, 0,5);

        ///execute as @p at @s anchored eyes run particle minecraft:flame ~ ~1 ~ ^ ^ ^10000 0.001 0

    }

    public boolean canReload(Player player) {
        if (!player.isOnline()) return false;
        if (plugin.getFunctionLibrary().getItemNBTString(player.getInventory().getItemInMainHand(), "state") == "fire") return false;
        if (plugin.getFunctionLibrary().getItemNBTString(player.getInventory().getItemInMainHand(), "state") == "reload") return false;
        return true;
    }


    public boolean canFire(PlayerInteractEvent event) {
        if (!event.getPlayer().isOnline()) return false;
        if (plugin.getFunctionLibrary().getItemNBTString(event.getItem(), "state") == "fire") return false;
        if (plugin.getFunctionLibrary().getItemNBTString(event.getItem(), "state") == "reload") return false;
        if (!isFirePossible(event.getPlayer())) return false;
        return true;
    }

    public boolean isFirePossible(Player player) {
        if (!plugin.getFunctionLibrary().isWeapon(player.getInventory().getItemInMainHand())) return false;
        if (plugin.getFunctionLibrary().getItemNBTInt(player.getInventory().getItemInMainHand(), "durability") <= 0) return false;
        if (plugin.getFunctionLibrary().getItemNBTInt(player.getInventory().getItemInMainHand(), "ammo") <= 0) return false;
        if (player.isDead()) return false;
        return true;

    }



    public void onFireInterrupted(Player player){

        String soundname = plugin.getConfig().getConfigurationSection("Global").getString("noAmmoSound");

        player.playSound(player.getLocation(), soundname, 1, 1);
    }





    private void loadWeaponData(String weaponID, ShockerWeaponsPlugin plugin) {
        FileConfiguration config = plugin.getConfig();
        this.weaponID = weaponID;
        this.reqAmmoType = plugin.getConfig().getString("weapons." + weaponID + ".reqAmmoType");
    }




    public void chargeCrossbow(ItemStack item) {
        if (item.getType() != Material.CROSSBOW) {
            System.out.println("предмет не является CROSSBOW");
            return;
        };

        ReadWriteNBT nbt = NBT.itemStackToNBT(item);
        nbt.getCompound("tag").setByte("Charged", Byte.valueOf("1"));
        nbt.getCompound("tag").getCompoundList("ChargedProjectiles").get(0).setString("id", "minecraft:air");
        System.out.println(nbt.getCompound("tag").getCompoundList("ChargedProjectiles").get(0));
        item.setItemMeta(item.getItemMeta());
        Bukkit.getPlayer("ByMrShocker").getInventory().setItemInMainHand(item);


//        CrossbowMeta crossbowMeta = (CrossbowMeta) item.getItemMeta();
//        System.out.println (crossbowMeta.getPersistentDataContainer().getKeys());
//        ReadWriteNBT nbt = NBT.itemStackToNBT(item);
//        nbt.getCompound("tag").setByte("Charged", Byte.valueOf("1"));
//        System.out.println(nbt.getCompound("tag").getByte("Charged"));
//        //crossbowMeta.addChargedProjectile(new ItemStack(Material.FIREWORK_ROCKET));
//        item.setItemMeta(crossbowMeta);
//        Bukkit.getPlayer("ByMrShocker").getInventory().setItemInMainHand(item);
//        //System.out.println("out = " + (CrossbowMeta) item.getItemMeta());

    }


    public boolean isStillHolding(Player player){
        if (player.getInventory().getItemInMainHand() == null) return false;
        if (!plugin.getFunctionLibrary().isWeapon(player.getInventory().getItemInMainHand())) return false;
        if (player.getInventory().getHeldItemSlot() != slotID) {
            //System.out.println("IsStillHolding: False. SlotID");
            return false;
        }
        //if (player.getInventory().getItemInMainHand() != weaponItem) {
        //    System.out.println("IsStillHolding: False. weaponReference");
        //    return false;
        //}
        return true;
    }


    private @NotNull Style styleWeaponItem(){
        return Style.style(TextColor.color(255, 180, 0), TextDecoration.ITALIC.withState(false));
    }

    private @NotNull Style styleMagItem(){
        return Style.style(TextColor.color(255, 255, 0), TextDecoration.ITALIC.withState(false));
    }

    private @NotNull Style styleDefaultItem(){
        return Style.style(TextColor.color(255, 255, 255), TextDecoration.ITALIC.withState(false));
    }


    public ItemStack getSelectedItem(Player player){
        return player.getInventory().getItemInMainHand();
    }


    public String getWeaponID(){
        return weaponID;
    }

    public ItemStack getWeaponItem(){
        return weaponItem;
    }





}
