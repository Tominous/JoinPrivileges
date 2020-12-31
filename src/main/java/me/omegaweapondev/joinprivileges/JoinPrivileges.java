package me.omegaweapondev.joinprivileges;

import me.omegaweapondev.joinprivileges.commands.CoreCommand;
import me.omegaweapondev.joinprivileges.events.PlayerListener;
import me.ou.library.SpigotUpdater;
import me.ou.library.Utilities;
import me.ou.library.configs.ConfigCreator;
import me.ou.library.configs.ConfigUpdater;
import net.milkbowl.vault.chat.Chat;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Arrays;

public class JoinPrivileges extends JavaPlugin {
  public static JoinPrivileges instance;
  private final ConfigCreator configFile = new ConfigCreator("config.yml");
  private final ConfigCreator messagesFile = new ConfigCreator("messages.yml");

  private static Chat chat = null;

  @Override
  public void onEnable() {
    initialSetup();
    configSetup();
    configUpdater();
    setupChat();
    commandAndEventSetup();
    SpigotUpdater();
  }

  @Override
  public void onDisable() {
    instance = null;
    super.onDisable();
  }

  public void onReload() {
    getConfigFile().reloadConfig();
    getMessagesFile().reloadConfig();
  }

  private void initialSetup() {

    // Setup the instance for plugin and OU Library
    instance = this;
    Utilities.setInstance(this);

    // Make sure vault is installed
    if(Bukkit.getPluginManager().getPlugin("Vault") == null) {
      Utilities.logWarning(true,
        "JoinPrivileges has detected that Vault is not installed.",
        "Without it, the prefixes and suffixes for players will not work.",
        "To install vault, please go to https://www.spigotmc.org/resources/vault.34315/ and download it.",
        "Once vault is installed, restart the server and JoinPrivileges will work."
      );
    }

    // Setup bStats
    final int bstatsPluginId = 8969;
    Metrics metrics = new Metrics(getInstance(), bstatsPluginId);

    // Logs a message to console, saying that the plugin has enabled correctly.
    Utilities.logInfo(false,
      "   _________",
        "  |_  | ___ \\",
        "    | | |_/ /  JoinPrivileges v" + getInstance().getDescription().getVersion() + " By OmegaWeaponDev.",
        "    | |  __/   Handle the way players join and leave your server!",
        "/\\__/ / |      Currently supporting spigot 1.13 upto 1.16.3",
        "\\____/\\_|",
      ""
    );
  }

  private void configSetup() {
    getConfigFile().createConfig();
    getMessagesFile().createConfig();
  }

  private void commandAndEventSetup() {

    // Setup the events
    Utilities.registerEvents(new PlayerListener());

    // Setup the commands
    Utilities.logInfo(true, "Registering the commands...");

    Utilities.setCommand().put("joinprivileges", new CoreCommand());

    Utilities.registerCommands();

    if(!Utilities.setCommand().isEmpty()) {
      Utilities.logInfo(true, "Commands Successfully registered!");
    }
  }

  private void configUpdater() {
    Utilities.logInfo(true, "Attempting to update the config files....");

    try {
      if(getConfigFile().getConfig().getDouble("Config_Version") != 1.0) {
        getConfigFile().getConfig().set("Config_Version", 1.0);
        getConfigFile().saveConfig();
        ConfigUpdater.update(getInstance(), "config.yml", getConfigFile().getFile(), Arrays.asList("Join_Settings.Group_Join_Settings.Groups", "Quit_Settings.Group_Quit_Settings.Groups"));
        Utilities.logInfo(true, "The config.yml has successfully been updated!");
      }

      if(getMessagesFile().getConfig().getDouble("Config_Version") != 1.0) {
        getMessagesFile().getConfig().set("Config_Version", 1.0);
        getMessagesFile().saveConfig();
        ConfigUpdater.update(getInstance(), "messages.yml", getMessagesFile().getFile(), Arrays.asList("none"));
        Utilities.logInfo(true, "The messages.yml has successfully been updated!");
      }
      onReload();
    } catch(IOException ex) {
      ex.printStackTrace();
    }
  }

  private void SpigotUpdater() {
    new SpigotUpdater(this, 84563).getVersion(version -> {
      if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
        Utilities.logInfo(true, "You are already running the latest version");
      } else {
        PluginDescriptionFile pdf = this.getDescription();
        Utilities.logWarning(true,
          "A new version of " + pdf.getName() + " is avaliable!",
          "Current Version: " + pdf.getVersion() + " > New Version: " + version,
          "Grab it here: https://github.com/OmegaWeaponDev/JoinPrivileges"
        );
      }
    });
  }

  private boolean setupChat() {
    RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
    chat = rsp.getProvider();
    return chat != null;
  }


  public ConfigCreator getConfigFile() {
    return configFile;
  }

  public ConfigCreator getMessagesFile() {
    return messagesFile;
  }

  public Chat getChat() {
    return chat;
  }

  public static JoinPrivileges getInstance() {
    return instance;
  }
}
