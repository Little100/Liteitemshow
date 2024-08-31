package org.Little_100.liteitemshow;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

public class ChatListener implements Listener {
    private final Liteitemshow plugin;
    private final Map<String, String> itemNameMap;
    private final Map<String, String> enchantmentNameMap;
    private final Map<String, String> potionEffectNameMap;

    public ChatListener(Liteitemshow plugin) {
        this.plugin = plugin;
        this.itemNameMap = plugin.getMappings("item");
        this.enchantmentNameMap = plugin.getMappings("enchant");
        this.potionEffectNameMap = plugin.getMappings("potions");
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        String message = event.getMessage();
        String placeholder = "[item]"; // 可以配置化
        if (message.contains(placeholder)) {
            ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();
            if (itemInHand != null && itemInHand.getType() != Material.AIR) {
                String itemName = getItemDisplayName(itemInHand);
                message = message.replace(placeholder, itemName);
                event.setMessage(message);
            }
        }
    }

    private String getItemDisplayName(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return "";

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return "";

        // 检查是否有自定义名称
        String displayName = meta.hasDisplayName()
                ? ChatColor.LIGHT_PURPLE + meta.getDisplayName() + ChatColor.RESET
                : getLocalizedItemName(item.getType().name()); // 使用映射表

        // 获取物品的类型名称并映射
        String itemType = item.getType().name();
        String localizedItemType = getLocalizedItemName(itemType);
        String additionalInfo = String.format(ChatColor.GRAY + "本体名字是: %s", localizedItemType + ChatColor.RESET);

        // 处理附魔
        Map<Enchantment, Integer> enchantments = meta.getEnchants();
        if (!enchantments.isEmpty()) {
            StringBuilder enchants = new StringBuilder("含有附魔(");
            for (Map.Entry<Enchantment, Integer> enchant : enchantments.entrySet()) {
                String enchantKey = enchant.getKey().getKey().toString();
                String enchantName = enchantmentNameMap.getOrDefault(enchantKey, enchant.getKey().getName());
                String enchantLevelColor = getEnchantLevelColor(enchant.getValue());
                enchants.append(enchantLevelColor)
                        .append(enchantName).append(" ").append(enchant.getValue())
                        .append(ChatColor.RESET) // 重置颜色
                        .append(", ");
            }
            // 移除尾部的逗号和空格
            if (enchants.length() > 4) {
                enchants.setLength(enchants.length() - 2);
            }
            enchants.append(")");
            // 格式化显示名称和附魔信息
            displayName = String.format("{[%s][%s][%s]}", displayName, enchants.toString(), additionalInfo);
        } else {
            // 没有附魔时格式化显示名称和附加信息
            displayName = String.format("{[%s][%s]}", displayName, additionalInfo);
        }

        return displayName;
    }

    private String getPotionDisplayName(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return "";

        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof PotionMeta)) return "";

        PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
        PotionType potionType = potionMeta.getBasePotionData().getType();

        String potionName = potionType.name();
        StringBuilder potionInfo = new StringBuilder("{[药水[抱歉,但是");

        for (PotionEffect effect : potionMeta.getCustomEffects()) {
            PotionEffectType effectType = effect.getType();
            int duration = effect.getDuration() / 20; // 持续时间转换为秒
            int amplifier = effect.getAmplifier() + 1; // 等级从0开始

            String effectName = potionEffectNameMap.getOrDefault(effectType.getName(), effectType.getName());
            potionInfo.append(effectName).append(" ").append(duration).append("s ").append(amplifier).append(", ");
        }

        if (potionInfo.length() > 7) {
            potionInfo.setLength(potionInfo.length() - 2); // 移除尾部逗号
        }
        potionInfo.append("暂时不支持药水]}");

        return potionInfo.toString();
    }

    private String getLocalizedItemName(String itemName) {
        return itemNameMap.getOrDefault(itemName, itemName);
    }

    private String getEnchantLevelColor(int level) {
        if (level >= 5) {
            return ChatColor.RED.toString(); // 红色
        } else if (level >= 3) {
            return ChatColor.GOLD.toString(); // 金色
        } else if (level >= 1) {
            return ChatColor.AQUA.toString(); // 青色
        } else {
            return ChatColor.GRAY.toString(); // 默认颜色
        }
    }
}