package me.omegaweapondev.joinprivileges.utilities;

import me.omegaweapondev.joinprivileges.JoinPrivileges;
import me.ou.library.Utilities;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class SettingsHandler {
  private final FileConfiguration configFile;
  private final String configName;
  
  public SettingsHandler(final FileConfiguration configFile) {
    this.configFile = configFile;
    this.configName = JoinPrivileges.getInstance().getConfigFile().getFileName();
  }

  public String getString(final String key) {
    if(configFile.getString(key) == null) {
      getErrorMessage(key);
      return "";
    }

    return configFile.getString(key);
  }

  public List<String> getStringList(final String key) {
    if(configFile.getStringList(key).size() == 0) {
      getErrorMessage(key);
      return null;
    }
    return configFile.getStringList(key);
  }

  public ConfigurationSection getSection(final String key) {
    if (!configFile.isConfigurationSection(key)) {
      getErrorMessage(key);
      return null;
    }

    return configFile.getConfigurationSection(key);
  }

  public boolean getBoolean(final String key) {
    return configFile.getBoolean(key, false);
  }

  public int getInt(final String key) {
    return configFile.getInt(key, 0);
  }

  public double getDouble(final String key) {
    return configFile.getDouble(key, 0);
  }

  private void getErrorMessage(final String key) {
    Utilities.logInfo(true,
      "There was an error getting the " + key + " message from the " + configName + ".",
      "I have set a fallback message to take it's place until the issue is fixed.",
      "To resolve this, please locate " + key + " in the " + configName + " and fix the issue."
    );
  }
}
