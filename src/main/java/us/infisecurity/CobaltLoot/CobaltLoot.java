// This section is used to declare project group id.
package us.infisecurity.CobaltLoot;

// This section is used to declare project imports.
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import us.infisecurity.CobaltLoot.commands.LootboxCommand;
import us.infisecurity.CobaltLoot.listeners.LootboxListener;

public class CobaltLoot extends JavaPlugin {

    // This section is used to declare plugin instance.
    @Getter
    public static CobaltLoot instance;

    // This section is used to get plugin's instance.
    public static CobaltLoot getInstance() {
        return instance;
    }

    public void onEnable() {
        // Register Commands
        registerCommands();

        // Register Listeners
        registerListeners();

        // Register Settings
        registerSettings();
    }

    // This section is used to register plugin settings.
    public void registerSettings(){
        registerConfiguration();
        instance = this;
    }

    // This section is used to register plugin commands.
    public void registerCommands(){
        getCommand("lootbox").setExecutor(new LootboxCommand());
    }

    // This section is used to register plugin listeners.
    public void registerListeners(){
        getServer().getPluginManager().registerEvents(new LootboxListener(), this);
    }

    // This section is used to register plugin configuration.
    public void registerConfiguration() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
}
