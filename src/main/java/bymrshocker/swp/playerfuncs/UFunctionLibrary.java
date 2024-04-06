package bymrshocker.swp.playerfuncs;

import bymrshocker.swp.ShockerWeaponsPlugin;
import bymrshocker.swp.data.SWeapon;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTType;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.joml.Matrix4d;
import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//Библиотека всяких функций, которые можно вызвать откуда угодно. pluginInstance.getFunctionLibrary().функция ниже
public class UFunctionLibrary {
    ShockerWeaponsPlugin pluginInstance;


    public UFunctionLibrary(ShockerWeaponsPlugin plugin) {
        this.pluginInstance = plugin;
        NamespacedKey key = new NamespacedKey(pluginInstance, "our-custom-key");
    }


    //Выполнение консольной комманды от консоли
    public void executeConsoleCommand(String command){
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),command);

    }

    public boolean isWeapon(ItemStack item) {
        if (item != null) {
            if (getItemNBTString(item, "weaponID") != "null") {
                return pluginInstance.getConfig().getConfigurationSection("weapons").contains(getItemNBTString(item, "weaponID"));
            }
            return false;
        }
        return false;

    }

    public boolean isMag(ItemStack item) {
        if (item != null) {
            if (getItemNBTString(item, "magID") != "null") {
                return pluginInstance.getConfig().getConfigurationSection("mag").contains(getItemNBTString(item, "magID"));
            }
            return false;
        }
        return false;

    }



    public void weaponReloadTimer(SWeapon inweaponClass, Player inplayer, int incd, String inoldMag, String innewMag, String cal) {
        weaponReloadClass reloadClass = new weaponReloadClass();
        reloadClass.construct(inweaponClass, inplayer, cal, inoldMag, innewMag);
        reloadClass.runWeaponReloadTimer(incd, incd);
    }

    private class weaponReloadClass {

        public void construct(SWeapon weapon, Player inplayer, String incal, String inoldMag, String innewMag) {

            weaponClass = weapon;
            player = inplayer;
            cal = incal;
            oldMag = inoldMag;
            newMag = innewMag;
            if (getItemNBTString(inplayer.getInventory().getItemInMainHand(), "mag").equals("null")) step = 1;
            if (getItemNBTString(inplayer.getInventory().getItemInMainHand(), "mag").equals("null")) weaponClass.weaponReloadSound(player, 1);
            else weaponClass.weaponReloadSound(player, 0);

        }

        private SWeapon weaponClass;
        private Player player;
        private String oldMag;
        private String newMag;
        private int step = 2;
        private String cal;

        private int oldmagslot = -1;


        public void runWeaponReloadTimer(int period, int initDelay) {

            BukkitScheduler scheduler = Bukkit.getScheduler();

            scheduler.runTaskTimer(pluginInstance, BukkitTask -> {
                if (!weaponClass.isStillHolding(player)) {
                    BukkitTask.cancel();
                    return;
                }
                switch (step) {
                    case 0: {
                        BukkitTask.cancel();
                        return;
                    }
                    case 1:
                        if (!weaponClass.weaponInsertMag(newMag, player, cal, oldmagslot)) {
                            //System.out.println("w_reloadTimer: case 1: false");
                            BukkitTask.cancel();
                            return;
                        }
                    case 2: {
                        //System.out.println(oldMag);
                        if (oldMag != null && !oldMag.equals("null")) oldmagslot = weaponClass.weaponRemoveMag(player, oldMag);
                        if (!newMag.equals(null)) weaponClass.weaponReloadSound(player, 1);
                        else if (newMag.equals(null)) {
                            BukkitTask.cancel();
                            return;
                        }
                    }
                }
                step--;

                //BukkitTask.cancel();
            }, initDelay, period);
        }
    }



    //public void weaponReloadTimer(SWeapon inweaponClass, Player inplayer, int incd, String inoldMag, String innewMag, String cal) {
//
    //    new BukkitRunnable(){
//
    //        final private SWeapon weaponClass = inweaponClass;
    //        final private Player player = inplayer;
    //        final private int cd = incd;
    //        final private String oldMag = inoldMag;
    //        final private String newMag = innewMag;
//
    //        private int step = 2;
//
//
//
    //        @Override
    //        public void run() {
//
    //            if (!weaponClass.isStillHolding(player)) {
    //                this.cancel();
    //                return;
    //            }
//
//
    //            switch (step) {
    //                case 0: {
    //                    this.cancel();
    //                    return;
    //                }
    //                case 1:
    //                    if (!weaponClass.weaponInsertMag(newMag, player, cal)) {
    //                        //System.out.println("w_reloadTimer: case 1: false");
    //                        this.cancel();
    //                        return;
    //                    }
    //                case 2: {
    //                    //System.out.println(oldMag);
    //                    if (oldMag != null && !oldMag.equals("null")) weaponClass.weaponRemoveMag(player, oldMag);
    //                    else if (newMag.equals(null)) {
    //                        this.cancel();
    //                        return;
    //                    }
    //                }
//
    //            }
//
    //            step--;
//
    //        }
    //    }.runTaskTimer(pluginInstance, incd, incd);
//
    //}

    public void weaponFireTimer(SWeapon inweaponClass, Player inPlayer, int incount, int period, int initDelay) {
        weaponFireClass fireClass = new weaponFireClass();
        fireClass.construct(inweaponClass, inPlayer, incount);
        fireClass.runWeaponFireTimer(period, initDelay);
    }

    private class weaponFireClass {

        public void construct(SWeapon weapon, Player inPlayer, int inCount){

            weaponClass = weapon;
            player = inPlayer;
            this.remainCount = inCount;
            ammo = pluginInstance.getFunctionLibrary().getItemNBTInt(weaponClass.getWeaponItem(), "ammo");
            sn = pluginInstance.getFunctionLibrary().getSerialNumber(player);

        }

        private SWeapon weaponClass;
        private Player player;
        private int remainCount;
        private int ammo;
        private int sn;


        public void runWeaponFireTimer(int period, int initDelay) {

            BukkitScheduler scheduler = Bukkit.getScheduler();

            scheduler.runTaskTimer(pluginInstance, BukkitTask -> {
                int cursn = getSerialNumber(player);

                pluginInstance.getFunctionLibrary().setItemNBTInt(weaponClass.getSelectedItem(player), player, "ammo", ammo);
                if(remainCount <= 0 || !player.isOnline() || !weaponClass.isFirePossible(player) || ammo <= 0 || sn != cursn){

                    if (!weaponClass.isFirePossible(player) || ammo <= 0) weaponClass.onFireInterrupted(player);
                    pluginInstance.getFunctionLibrary().setItemNBTString(weaponClass.getSelectedItem(player), player, "state", "idle");
                    BukkitTask.cancel(); //cancel the repeating task
                    return; //exit the method
                }
                remainCount--; //decrement
                ammo--;
                weaponClass.weaponFire(player);

                //BukkitTask.cancel();
            }, initDelay, period);
        }
    }



        //Автоматическая/BURST стрельба
    //public void weaponFireTimer(SWeapon inweaponClass, Player inPlayer, int incount, int period, int initDelay){
//
    //    new BukkitRunnable(){
//
    //        final private SWeapon weaponClass = inweaponClass;
    //        final private Player player = inPlayer;
    //        final private int count = incount;
    //        private int remainCount = count;
    //        private int ammo = pluginInstance.getFunctionLibrary().getItemNBTInt(weaponClass.getWeaponItem(), "ammo");
    //        private int sn = pluginInstance.getFunctionLibrary().getSerialNumber(player);
//
    //        @Override
    //        public void run(){
//
    //            int cursn = getSerialNumber(player);
    //            pluginInstance.getFunctionLibrary().setItemNBTInt(weaponClass.getSelectedItem(player), player, "ammo", ammo);
//
    //            if(remainCount <= 0 || !player.isOnline() || !weaponClass.isFirePossible(player) || ammo <= 0 || sn != cursn){
//
    //                //System.out.println(sn);
    //                //System.out.println(cursn);
//
//
    //                weaponClass.onFireInterrupted(player);
    //                pluginInstance.getFunctionLibrary().setItemNBTString(weaponClass.getSelectedItem(player), player, "state", "idle");
    //                this.cancel(); //cancel the repeating task
    //                return; //exit the method
//
    //            }
    //            remainCount--; //decrement
    //            ammo--;
    //            weaponClass.weaponFire(player);
//
//
    //        }
    //    }.runTaskTimer(pluginInstance, initDelay, period);
//
    //}



    public void tryAddAmmo(Player player) {
        ItemStack magItem = player.getInventory().getItemInMainHand();
        ItemStack ammoItem = player.getInventory().getItemInOffHand();
        String materialString = pluginInstance.getConfig().getConfigurationSection("Global").getString("ammoMaterial");

        if (!ammoItem.getType().toString().equalsIgnoreCase(materialString)) return;

        String magID = pluginInstance.getFunctionLibrary().getItemNBTString(magItem, "magID");
        String ammoID = pluginInstance.getFunctionLibrary().getItemNBTString(ammoItem, "ammoID");

        if (pluginInstance.getFunctionLibrary().getItemNBTInt(magItem, "ammo") != 0) {
            if (!pluginInstance.getFunctionLibrary().getItemNBTString(magItem, "ammoType").equalsIgnoreCase(ammoID)) {
                return;
            }
        }

        int capacity = pluginInstance.getConfigMagInt(magID,"capacity");
        int curammo = pluginInstance.getFunctionLibrary().getItemNBTInt(magItem, "ammo");
        if (curammo >= capacity) return;

        ReadWriteNBT nbt = NBT.itemStackToNBT(player.getInventory().getItemInMainHand());
        nbt.getCompound("tag").getCompound("ShockerWeapons").setString("ammoType", ammoID);
        nbt.getCompound("tag").getCompound("ShockerWeapons").setInteger("ammo", curammo + 1);
        magItem = NBT.itemStackFromNBT(nbt);

        ItemMeta meta = magItem.getItemMeta();
        List<String> loreToAdd = pluginInstance.getConfig().getConfigurationSection("mag").getConfigurationSection(magID).getStringList("lore");
        meta = pluginInstance.getFunctionLibrary().setItemLore(meta, loreToAdd, magItem);

        magItem.setItemMeta(meta);
        ammoItem.setAmount(ammoItem.getAmount() - 1);

        player.getInventory().setItemInMainHand(magItem);
    }




//какого хуя GPT предлагает хуйню, а чел с обсуждения 10летней давности решает проблема сразу-же?
    private static final double EPSILON = Math.ulp(1.0d) * 2d;

    private static boolean epsilonCheck(double a, double b) {
        return Math.abs(b - a) <= EPSILON;
    }
    public Location getRelativeLocation(Player ply, Location inLoc, Vector vector) { // +x is forward, +z is right, +y is up
        Location origin = inLoc;
        double vx = vector.getX();
        double vy = vector.getY();
        double vz = vector.getZ();
        boolean zeroZ = epsilonCheck(vz, 0);
        if (zeroZ && epsilonCheck(vx, 0)) {
            return origin.add(0, vy, 0);
        } else {
            float yaw = origin.getYaw();
            double yawRad = yaw * (Math.PI / 180);
            Vector forward = new Vector(
                    -Math.sin(yawRad),
                    0,
                    Math.cos(yawRad)
            ); // I know I promised no trig but its faster than the alternative
            if (zeroZ) {
                return origin.add(forward.multiply(vx));
            } else {
                Vector right = new Vector(forward.getZ(), 0, -forward.getX());
                return origin.add(forward.multiply(vx)).add(right.multiply(vz));
            }
        }
    }





    public int getSerialNumber(Player player){
        ItemStack item = player.getInventory().getItemInMainHand();
        int sn = getItemNBTInt(item, "SN");

        if (sn != -1) return sn;

        if (isWeapon(item)) {
            sn = pluginInstance.getConfig().getConfigurationSection("saved").getInt("lastSN");
            sn++;
            pluginInstance.getConfig().getConfigurationSection("saved").set("lastSN", sn);
            setItemNBTInt(item, player, "SN", sn);
            pluginInstance.saveConfig();
            return sn;
        }
        return -1;

    }



    public ItemMeta setItemLore(ItemMeta meta, List<String> loreStrings, ItemStack itemStack) {

        List<Component> loreComponents = new ArrayList<>();
        for (String loreString : loreStrings) {
            //Component loreComponent = GsonComponentSerializer.gson().deserialize(loreString);
            String modifiedString = replacePlaceholders(loreString, itemStack);
            Component loreComponent = LegacyComponentSerializer.legacy('&').deserialize(modifiedString).decoration(TextDecoration.ITALIC, false);
            loreComponents.add(loreComponent);
        }
        meta.lore(loreComponents);

        return meta;

    }

    public void displaySystemMessage(Player player, String string){
        player.sendMessage(LegacyComponentSerializer.legacy('&').deserialize("&6[SWD] &f" + string).decoration(TextDecoration.ITALIC, false));

    }



    public String replacePlaceholders(String inString, ItemStack itemStack) {
        String string = inString;
        ReadWriteNBT nbt = NBT.itemStackToNBT(itemStack);
        Set<String> swdKeys = nbt.getCompound("tag").getCompound("ShockerWeapons").getKeys();

        for(String keyString : swdKeys) {
            if (isValidCompound(nbt, keyString)) {
                String placeholder = "%swd_" + keyString + "%";
                NBTType type = nbt.getCompound("tag").getCompound("ShockerWeapons").getType(keyString);
                if (type != null) switch (type) {
                    case NBTTagString: string = string.replace(placeholder, nbt.getCompound("tag").getCompound("ShockerWeapons").getString(keyString));
                    case NBTTagInt: string = string.replace(placeholder, String.valueOf(nbt.getCompound("tag").getCompound("ShockerWeapons").getInteger(keyString)));
                }
            }
        }

        if (nbt.getCompound("tag") != null && nbt.getCompound("tag").getCompound("ShockerWeapons") != null)
        {
            if (nbt.getCompound("tag").getCompound("ShockerWeapons").getKeys().contains("cal") && nbt.getCompound("tag").getCompound("ShockerWeapons").getKeys().contains("ammoType"))
            {
                if (nbt.getCompound("tag").getCompound("ShockerWeapons").getString("ammoType") != "null" && nbt.getCompound("tag").getCompound("ShockerWeapons").getString("cal") != "null") {

                    string = string.replace("%swd_ammoTypeName%", pluginInstance.getConfig().getConfigurationSection("ammo").getConfigurationSection(nbt.getCompound("tag").getCompound("ShockerWeapons").getString("cal")).getConfigurationSection(nbt.getCompound("tag").getCompound("ShockerWeapons").getString("ammoType")).getString("name"));

                } else if (nbt.getCompound("tag").getCompound("ShockerWeapons").getString("ammoType") == "null" && nbt.getCompound("tag").getCompound("ShockerWeapons").getString("cal") != "null") {
                    string = string.replace("%swd_ammoTypeName%", pluginInstance.getConfig().getConfigurationSection("Global").getString("nullItemName"));
                }
            }
        }

        return string;
    }




    //разраб NBTApi наговнокодил, поэтому приходится делать так, чтоб не срало в консоль
    public boolean isValidCompound(ReadWriteNBT nbt, String key) {
        if (!nbt.getKeys().contains("tag")) return false;
        if (!nbt.getCompound("tag").getKeys().contains("ShockerWeapons")) return false;
        if (!nbt.getCompound("tag").getCompound("ShockerWeapons").getKeys().contains(key)) return false;

        return true;
    }




    //получение NBT тега типа STRING.
    public String getItemNBTString(ItemStack item, String key) {

        ReadWriteNBT nbt = NBT.itemStackToNBT(item);
        if (!isValidCompound(nbt, key)) return "null";

        return nbt.getCompound("tag").getCompound("ShockerWeapons").getString(key);


        //NamespacedKey nkey = new NamespacedKey(pluginInstance, key);
        //ItemMeta itemMeta = item.getItemMeta();
        //PersistentDataContainer container = itemMeta.getPersistentDataContainer();
//
        //if(container.has(nkey, PersistentDataType.STRING)) {
        //    return container.get(nkey, PersistentDataType.STRING);
        //}
        //return null;
    }


    //установка NBT тега типа STRING.
    public void setItemNBTString(ItemStack item, Player player, String key, String value) {

        ReadWriteNBT nbt = NBT.itemStackToNBT(item);
        nbt.getCompound("tag").getCompound("ShockerWeapons").setString(key, value);
        player.getInventory().setItemInMainHand(NBT.itemStackFromNBT(nbt));

        //NamespacedKey nkey = new NamespacedKey(pluginInstance, key);
        //ItemMeta itemMeta = item.getItemMeta();
        //itemMeta.getPersistentDataContainer().set(nkey, PersistentDataType.STRING, value);
        //item.setItemMeta(itemMeta);
    }

    //получение NBT тега типа DOUBLE.
    public Double getItemNBTDouble(ItemStack item, String key) {

        ReadWriteNBT nbt = NBT.itemStackToNBT(item);
        if (!isValidCompound(nbt, key)) return -1.0;

        return nbt.getCompound("tag").getCompound("ShockerWeapons").getDouble(key);
    }

    //установка NBT тега типа DOUBLE.
    public void setItemNBTDouble(ItemStack item, Player player, String key, Double value) {

        ReadWriteNBT nbt = NBT.itemStackToNBT(item);
        nbt.getCompound("tag").getCompound("ShockerWeapons").setDouble(key, value);
        player.getInventory().setItemInMainHand(NBT.itemStackFromNBT(nbt));
    }

    //получение NBT тега типа BYTE.
    public Byte getItemNBTByte(ItemStack item, String key) {

        ReadWriteNBT nbt = NBT.itemStackToNBT(item);
        if (!isValidCompound(nbt, key)) return -1;

        return nbt.getCompound("tag").getCompound("ShockerWeapons").getByte(key);
    }

    //установка NBT тега типа BYTE.
    public void setItemNBTByte(ItemStack item, Player player, String key, Byte value) {

        ReadWriteNBT nbt = NBT.itemStackToNBT(item);
        nbt.getCompound("tag").getCompound("ShockerWeapons").setByte(key, value);
        player.getInventory().setItemInMainHand(NBT.itemStackFromNBT(nbt));
    }

    //получение NBT тега типа INT.
    public int getItemNBTInt(ItemStack item, String key) {

        ReadWriteNBT nbt = NBT.itemStackToNBT(item);
        if (!isValidCompound(nbt, key)) return -1;

        return nbt.getCompound("tag").getCompound("ShockerWeapons").getInteger(key);
    }

    //установка NBT тега типа INT.
    public void setItemNBTInt(ItemStack item, Player player, String key, int value) {

        ReadWriteNBT nbt = NBT.itemStackToNBT(item);
        nbt.getCompound("tag").getCompound("ShockerWeapons").setInteger(key, value);
        player.getInventory().setItemInMainHand(NBT.itemStackFromNBT(nbt));
    }



    //получение NBT тега типа IntArray.
    public int[] getItemNBTIntArray(ItemStack item, String key) {

        ReadWriteNBT nbt = NBT.itemStackToNBT(item);
        int[] ints = new int[0];
        if (!isValidCompound(nbt, key)) return ints;

        return nbt.getCompound("tag").getCompound("ShockerWeapons").getIntArray(key);
    }

    //установка NBT тега типа IntArray.
    public void setItemNBTIntArray(ItemStack item, Player player, String key, int[] value) {

        ReadWriteNBT nbt = NBT.itemStackToNBT(item);
        nbt.getCompound("tag").getCompound("ShockerWeapons").setIntArray(key, value);
        player.getInventory().setItemInMainHand(NBT.itemStackFromNBT(nbt));
    }




}
