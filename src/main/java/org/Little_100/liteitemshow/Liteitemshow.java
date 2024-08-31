package org.Little_100.liteitemshow;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
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

        // Plugin startup logic
        getLogger().info("" +
                "   __ _ _   _   _          _  ___   ___  \n" +
                "  / /(_) |_| |_| | ___    / |/ _ \\ / _ \\ \n" +
                " / / | | __| __| |/ _ \\   | | | | | | | |\n" +
                "/ /__| | |_| |_| |  __/   | | |_| | |_| |\n" +
                "\\____/_|\\__|\\__|_|\\___|___|_|\\___/ \\___/ \n" +
                "                     |_____|             ");
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
    }

    private void loadMapConfig() {
        File mapFile = new File(getDataFolder(), "map.yml");
        if (!mapFile.exists()) {
            saveResource("map.yml", false); // 从资源目录复制默认map.yml文件
        }
        mapConfig = YamlConfiguration.loadConfiguration(mapFile);
    }

    public FileConfiguration getMapConfig() {
        return mapConfig;
    }

    public FileConfiguration getMainConfig() {
        return mainConfig;
    }

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
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("reload")) {
            // 如果发送者是控制台，直接允许执行
            if (sender instanceof ConsoleCommandSender) {
                reloadConfig();
                loadMapConfig();
                // 重新注册监听器
                getServer().getPluginManager().registerEvents(new ChatListener(this), this);
                sender.sendMessage("配置文件已重新加载(没做好 似乎还不能重载文件(悲))。");
                return true;
            }

            // 如果发送者是玩家，检查是否有权限
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.isOp() || player.hasPermission("liteitemshow.reload")) {
                    reloadConfig();
                    loadMapConfig();
                    // 重新注册监听器
                    getServer().getPluginManager().registerEvents(new ChatListener(this), this);
                    sender.sendMessage("配置文件已重新加载(没做好 似乎还不能重载文件(悲))。");
                    return true;
                } else {
                    sender.sendMessage("你没有权限执行此命令。");
                    return false;
                }
            }

            // 处理其他情况（比如命令发送者不是玩家或控制台）
            sender.sendMessage("该命令只能由玩家或控制台执行。");
            return false;
        }
        return false;
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
