package org.Little_100.liteitemshow;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class Liteitemshow extends JavaPlugin implements CommandExecutor {
    private FileConfiguration mainConfig;
    private FileConfiguration mapConfig;
    private ChatListener chatListener;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        mainConfig = getConfig();
        loadMapConfig();

        // 初始化 ChatListener
        chatListener = new ChatListener(this);
        getServer().getPluginManager().registerEvents(chatListener, this);

        // 注册命令
        getCommand("Liteitemshow").setExecutor(this);

        getLogger().info("" +
                "   __ _ _   _   _          _  ___   ___  \n" +
                "  / /(_) |_| |_| | ___    / |/ _ \\ / _ \\ \n" +
                " / / | | __| __| |/ _ \\   | | | | | | | |\n" +
                "/ /__| | |_| |_| |  __/   | | |_| | |_| |\n" +
                "\\____/_|\\__|\\__|_|\\___|___|_|\\___/ \\___/ \n" +
                "                     |_____|             ");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 检查命令权限
        if (!sender.hasPermission("liteitemshow.reload")) {
            sender.sendMessage("§c你没有权限使用此命令!");
            return true;
        }

        // 处理重载和版本命令
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                reloadAllConfigs();
                sender.sendMessage("§aLiteitemshow 配置文件已重新加载!");
                return true;
            } else if (args[0].equalsIgnoreCase("version")) {
                sender.sendMessage("§bLiteitemshow 版本: " + getDescription().getVersion());
                return true;
            }
        }

        // 显示帮助信息
        sender.sendMessage("§6Liteitemshow 命令帮助:");
        sender.sendMessage("§a/Liteitemshow reload - 重新加载配置文件");
        sender.sendMessage("§a/Liteitemshow version - 查看插件版本");
        return true;
    }

    public void reloadAllConfigs() {
        // 重新加载主配置文件
        reloadConfig();
        mainConfig = getConfig(); // 获取更新后的配置

        // 重新加载map配置文件
        loadMapConfig();

        // 重新初始化 ChatListener 的映射
        if (chatListener != null) {
            chatListener.reloadMappings();
        }

        getLogger().info("所有配置文件已重新加载!");

        // 打印关键字或其他配置项，确保配置已更新
        String keyword = mainConfig.getString("keyword", "默认关键字");
        getLogger().info("当前关键字: " + keyword);
    }

    // 加载 map.yml 配置
    private void loadMapConfig() {
        File mapFile = new File(getDataFolder(), "map.yml");
        if (!mapFile.exists()) {
            saveResource("map.yml", false); // 如果 map.yml 不存在，保存默认文件
        }
        mapConfig = YamlConfiguration.loadConfiguration(mapFile);
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