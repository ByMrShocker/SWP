package bymrshocker.swp.commands.args;

import bymrshocker.swp.ShockerWeaponsPlugin;
import bymrshocker.swp.commands.BaseCommandArg;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class swd_sn implements BaseCommandArg {

    @Override
    public String[] getTabList(String key) {
        argListMap.put("argument", new String[] {"arg1", "arg2"});
        if (!argListMap.containsKey(key)) return new String[0];
        return argListMap.get(key);
    }

    @Override
    public String getName() { return "sn"; }

    @Override
    public String getDescription() { return "view sn"; }

    @Override
    public String getSyntax() { return "/swd sn"; }

    @Override
    public String getPermission() {
        return "swd.checksn";
    }

    @Override
    public void execute(ShockerWeaponsPlugin plugin, Player player, String[] args) {
        if (player.isValid()) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (!plugin.getFunctionLibrary().isWeapon(item)) {
                plugin.getFunctionLibrary().displaySystemMessage(player, "&eВы должны держать оружие в основной руке");
                return;
            }
            int sn = plugin.getFunctionLibrary().getItemNBTInt(item, "SN");

            plugin.getFunctionLibrary().displaySystemMessage(player, "&fСерийный номер: &e" + String.valueOf(sn));

        }
    }
}
