//package bymrshocker.swp.commands;

//import org.apache.maven.model.PluginManagement;
//import org.bukkit.Bukkit;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandMap;
//import org.bukkit.command.defaults.BukkitCommand;
//import org.bukkit.plugin.PluginManager;
//import org.bukkit.plugin.SimplePluginManager;

//public class BaseCommand extends BukkitCommand implements CommandExecutor {
//
//    private final int minArguments;
//    private final int maxArguments;
//    private final boolean playerOnly;
//
//    public  BaseCommand(String command) {
//        this(command, 0);
//    }
//
//    public BaseCommand(String command, boolean playerOnly) {
//        this(command, 0, playerOnly);
//    }
//
//    public  BaseCommand(String command, int requiredArguments) {
//        this(command, requiredArguments, requiredArguments);
//    }
//
//    public BaseCommand(String command, int minArguments, int maxArguments) {
//        this(command, minArguments, maxArguments, false);
//    }
//
//    public BaseCommand(String command, int requiredArguments, boolean playerOnly) {
//        this(command, requiredArguments, requiredArguments, playerOnly);
//    }
//
//    public  BaseCommand(String command, int minArguments, int maxArguments, boolean playerOnly) {
//        super(command);
//
//        this.minArguments = minArguments;
//        this.maxArguments = maxArguments;
//        this.playerOnly = playerOnly;
//    }
//
//    public CommandMap getCommandMap() {
//        try {
//            if (Bukkit.getPluginManager() instanceof SimplePluginManager) {
//                Field field = SimplePluginManager.class.getDeclaredField("commandMap");
//                field.setAccessible(true);
//
//                return (CommandMap) field.(Bukkit.getPluginManager());
//                field.
//            }
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//}
//