package org.Little_100.liteitemshow;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class Liteitemshow extends JavaPlugin {
    private FileConfiguration mainConfig;
    private FileConfiguration mapConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        mainConfig = getConfig();
        loadMapConfig();

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getCommand("Liteitemshow").setExecutor(new ReloadCommand(this));

        getLogger().info("" +
                "   __ _ _   _   _          _  ___   ___  \n" +
                "  / /(_) |_| |_| | ___    / |/ _ \\ / _ \\ \n" +
                " / / | | __| __| |/ _ \\   | | | | | | | |\n" +
                "/ /__| | |_| |_| |  __/   | | |_| | |_| |\n" +
                "\\____/_|\\__|\\__|_|\\___|___|_|\\___/ \\___/ \n" +
                "                     |_____|             ");
    }

    public void reloadAllConfigs() {
        // 重新加载主配置文件
        reloadConfig();
        mainConfig = getConfig(); // 获取更新后的配置

        // 重新加载map配置文件
        loadMapConfig(); // 调用之前定义的loadMapConfig方法

        getLogger().info("所有配置文件已重新加载!");

        // 打印关键字或其他配置项，确保配置已更新
        String keyword = mainConfig.getString("keyword");
        getLogger().info("当前关键字: " + keyword);
    }

    // 加载 map.yml 配置
    private void loadMapConfig() {
        File mapFile = new File(getDataFolder(), "map.yml");
        if (!mapFile.exists()) {
            saveResource("map.yml", false); // 如果 map.yml 不存在，保存默认文件
        }
        mapConfig = YamlConfiguration.loadConfiguration(mapFile); // 正确加载map配置文件
    }

    public FileConfiguration getMapConfig() {
        return mapConfig;
    }

    public FileConfiguration getMainConfig() {
        return mainConfig;
    }

    // 获取映射数据
    public Map<String, String> getMappings(String path) {
        Map<String, String> mappings = new HashMap<>();
        if (mapConfig.isConfigurationSection(path)) {
            for (String key : mapConfig.getConfigurationSection(path).getKeys(false)) {
                mappings.put(key, mapConfig.getString(path + "." + key));
            }
        }
        return mappings;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
