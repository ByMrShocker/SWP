package bymrshocker.swp.commands.args;

import bymrshocker.swp.ShockerWeaponsPlugin;
import bymrshocker.swp.commands.BaseCommandArg;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class swd_list implements BaseCommandArg {

    @Override
    public String[] getTabList(String key) {
        argListMap.put("list", new String[] {"arg1", "arg2"});


        if (!argListMap.containsKey(key)) return new String[0];
        return argListMap.get(key);
    }

    @Override
    public String getName() { return "list"; }

    @Override
    public String getDescription() { return "List of SWD items"; }

    @Override
    public String getSyntax() { return "/swd list"; }

    @Override
    public String getPermission() {
        return "swd.list";
    }

    @Override
    public void execute(ShockerWeaponsPlugin plugin, Player player, String[] args) {
        if (player.isValid()) {
            //execute code


            plugin.getFunctionLibrary().displaySystemMessage(player, "Weapon: &e" + plugin.getConfig().getConfigurationSection("weapons").getKeys(false).toString());
            plugin.getFunctionLibrary().displaySystemMessage(player, "Mag: &e" + plugin.getConfig().getConfigurationSection("mag").getKeys(false).toString());

            Set<String> allcals = plugin.getConfig().getConfigurationSection("ammo").getKeys(false);

            List<String> ammos = new ArrayList<>();

            for (String curcal : allcals) {

                ammos.addAll(plugin.getConfig().getConfigurationSection("ammo").getConfigurationSection(curcal).getKeys(false));
                System.out.println(curcal);

            }



            plugin.getFunctionLibrary().displaySystemMessage(player, "Ammo: &e" + ammos.toString());
        }
    }
}
