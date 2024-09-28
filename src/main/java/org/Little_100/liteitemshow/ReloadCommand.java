package org.Little_100.liteitemshow;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
    private final Liteitemshow plugin;
    private String keyword;

    public ReloadCommand(Liteitemshow plugin) {
        this.plugin = plugin;
        initConfig(); // 在构造函数中初始化配置
    }

    // 初始化或重载配置
    public void initConfig() {
        plugin.reloadAllConfigs(); // 使用 reloadAllConfigs 方法
        keyword = plugin.getMainConfig().getString("keyword");

        // 打印关键字以确认配置加载情况
        plugin.getLogger().info("当前关键字: " + keyword);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("liteitemshow.admin")) {
            sender.sendMessage(plugin.getConfig().getString("no-permission-message"));
            return true;
        }
        if (args.length == 0) {
            return false;
        }
        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.reloadAllConfigs(); // 调用重载方法
                sendMessage(sender, "§a配置文件重载完成。");
                break;
            case "version":
                sendMessage(sender, "§a当前版本: §b" + plugin.getDescription().getVersion());
                break;
            default:
                sendMessage(sender, "§6错误: 未知的命令参数。");
        }
        return true;
    }

    private void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(message);
    }

    // 获取当前关键字（使用时总是最新的）
    public String getKeyword() {
        return keyword; // 使用最新的值
    }
}
