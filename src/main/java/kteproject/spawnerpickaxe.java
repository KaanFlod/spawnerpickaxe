package kteproject;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class spawnerpickaxe extends JavaPlugin implements CommandExecutor, Listener {
    public static int used;
    public String translateHexColorCodes(String message) {
        Pattern hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hexColor = matcher.group(1);
            StringBuilder builder = new StringBuilder("§x");
            for (char c : hexColor.toCharArray()) {
                builder.append('§').append(c);
            }
            matcher.appendReplacement(buffer, builder.toString());
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }
    @Override
    public void onEnable() {
        getLogger().info(ChatColor.GOLD + "[KteProject]" + ChatColor.GREEN + " Plugin Enabled!");
        getLogger().info("Thanks for using our plugin!!");
        getCommand("spawnerpickaxe").setExecutor(this);
        getCommand("spawnerreload").setExecutor(this);
        getCommand("spawnergive").setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);

        MessagesConfig.setup();

        MessagesConfig.get().addDefault("permission","&c&lYetkiniz Yok!");
        MessagesConfig.get().addDefault("use","&cKullanım: /spawnerpickaxe <oyuncu> <hak>");
        MessagesConfig.get().addDefault("use-hak","&cHak sayısı 1'den daha büyük olmalı.");
        MessagesConfig.get().addDefault("use-hak-number","&cGeçerli bir hak sayısı girin.");
        MessagesConfig.get().addDefault("pickaxe-targetplayer","&aSpawner Kazması &aVerildi!");
        MessagesConfig.get().addDefault("pickaxe-offlineplayer","&cOyuncu bulunamadı veya çevrimdışı.");
        MessagesConfig.get().addDefault("pickaxe-error","&cLütfen bir oyuncu ismi girin.");
        MessagesConfig.get().addDefault("pickaxe-give","&e%player% &aAdlı Oyuncuya &6%hak% &ahaklı Kazma Verildi!");
        MessagesConfig.get().addDefault("hak-finish","&cSpawner Kırma Hakınız Tükendi!");
        MessagesConfig.get().addDefault("spawner-place","&cBilinmeyen veya Yasaklı spawner türü!");
        MessagesConfig.get().addDefault("inventory-Full","&cEnvanterin dolu! Spawner'ı almak için yer açmalısın.");
        MessagesConfig.get().addDefault("spawnergive-use","&cKullanım: /spawnergive <oyuncuismi> <spawnerismi>");
        MessagesConfig.get().addDefault("spawnergive-offlineplayer","&cKullanıcı Aktif değil veya Yanlış isim girdiniz.");
        MessagesConfig.get().addDefault("spawnergive-give","&a%player% Adlı Oyuncuya %spawner% Spawnerı Verildi!");

        MessagesConfig.get().options().copyDefaults(true);
        MessagesConfig.save();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("spawnerpickaxe")) {
            if (sender.hasPermission("spawnerpickaxe.admin")) {
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("use"))));
                    return false;
                }

                if (args.length > 0) {
                    Player targetPlayer = Bukkit.getPlayer(args[0]);

                    if (targetPlayer != null && targetPlayer.isOnline()) {
                        try {
                            used = Integer.parseInt(args[1]);
                            if (used < 1) {
                                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("use-hak"))));
                                return true;
                            }
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("use-hak-number"))));
                            return true;
                        }

                        ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
                        ItemMeta meta = pickaxe.getItemMeta();
                        if (meta != null) {
                            String itemname = "&#FFA500S&#FF9E00p&#FE9800a&#FE9100w&#FE8A00n&#FE8300e&#FD7D00r &#FD6F00K&#FC6900a&#FC6200z&#FC5B00m&#FC5400a&#FB4E00s&#FB4700ı";
                            String coloredname = translateHexColorCodes(itemname);
                            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', coloredname));
                            meta.setLore(Arrays.asList(ChatColor.YELLOW + "Kalan Hak: " + used));

                            PersistentDataContainer data = meta.getPersistentDataContainer();
                            NamespacedKey key = new NamespacedKey(this, "spawner_pickaxe_uses");
                            data.set(key, PersistentDataType.INTEGER, used);
                        }

                        pickaxe.setItemMeta(meta);
                        targetPlayer.getInventory().addItem(pickaxe);
                        targetPlayer.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("pickaxe-targetplayer"))));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("pickaxe-give")).replace("%player%", targetPlayer.getName()).replace("%hak%", args[1])));
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("pickaxe-offlineplayer"))));
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("pickaxe-error"))));
                    return false;
                }
            }
        }

        else if (command.getName().equalsIgnoreCase("spawnerreload")) {
            if (sender.hasPermission("spawnerpickaxe.reload")) {
                MessagesConfig.reload();
                sender.sendMessage(ChatColor.GREEN + "SpawnerPickaxe yapılandırması yeniden yüklendi!");
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("permission"))));
            }
        }
        else if (command.getName().equalsIgnoreCase("spawnergive")) {
            if (sender.hasPermission("spawnerpickaxe.give")) {
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("spawnergive-use"))));
                    return false;
                }

                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("spawnergive-offlineplayer"))));
                    return true;
                }

                String spawnerType = args[1].toUpperCase();
                EntityType entityType;
                try {
                    entityType = EntityType.valueOf(spawnerType);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "Geçersiz spawner türü: " +ChatColor.BOLD+ChatColor.BLUE+ spawnerType);
                    return true;
                }

                ItemStack spawnerItem = new ItemStack(Material.SPAWNER);
                ItemMeta spawnerMeta = spawnerItem.getItemMeta();
                if (spawnerMeta != null) {
                    spawnerMeta.setDisplayName(ChatColor.GOLD + spawnerType + " Spawner");
                    spawnerItem.setItemMeta(spawnerMeta);
                }
                target.getInventory().addItem(spawnerItem);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("spawnergive-give")).replace("%player%", target.getName()).replace("%spawner%", spawnerType)));

                return true;
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("permission"))));
            }
        }
        return false;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.hasItemMeta()) {
            ItemMeta meta = itemInHand.getItemMeta();
            String itemname = "&#FFA500S&#FF9E00p&#FE9800a&#FE9100w&#FE8A00n&#FE8300e&#FD7D00r &#FD6F00K&#FC6900a&#FC6200z&#FC5B00m&#FC5400a&#FB4E00s&#FB4700ı";
            String coloredname = translateHexColorCodes(itemname);
            if (meta != null && meta.hasDisplayName() && meta.getDisplayName().equals(ChatColor.translateAlternateColorCodes('&',coloredname))) {
                if (block.getType() != Material.SPAWNER) {
                    event.setCancelled(true);
                    return;
                }

                boolean inventoryFull = true;
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item == null || item.getType() == Material.AIR) {
                        inventoryFull = false;
                        break;
                    }
                }

                if (inventoryFull) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("inventory-Full"))));
                    return;
                }

                event.setCancelled(true);

                BlockState state = block.getState();
                if (state instanceof CreatureSpawner) {
                    CreatureSpawner spawner = (CreatureSpawner) state;
                    String entityType = spawner.getSpawnedType().name();
                    block.setType(Material.AIR);

                    ItemStack spawnerItem = new ItemStack(Material.SPAWNER);
                    ItemMeta spawnerMeta = spawnerItem.getItemMeta();
                    if (spawnerMeta != null) {
                        spawnerMeta.setDisplayName(ChatColor.GOLD + entityType + " Spawner");
                        spawnerItem.setItemMeta(spawnerMeta);
                    }
                    player.getInventory().addItem(spawnerItem);
                }
                PersistentDataContainer data = meta.getPersistentDataContainer();
                NamespacedKey key = new NamespacedKey(this, "spawner_pickaxe_uses");
                int usesLeft = data.getOrDefault(key, PersistentDataType.INTEGER, used);

                usesLeft--;

                if (usesLeft <= 0) {
                    player.getInventory().remove(itemInHand);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("hak-finish"))));
                } else {
                    data.set(key, PersistentDataType.INTEGER, usesLeft);
                    meta.setLore(Arrays.asList(ChatColor.YELLOW + "Kalan Kullanım: " + usesLeft));
                    itemInHand.setItemMeta(meta);
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        ItemStack itemInHand = event.getItemInHand();
        if (block.getType() == Material.SPAWNER && itemInHand.hasItemMeta()) {
            ItemMeta meta = itemInHand.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                String displayName = meta.getDisplayName();

                EntityType entityType;
                if (displayName.equals(ChatColor.GOLD + "ZOMBIE Spawner")) {
                    entityType = EntityType.ZOMBIE;
                } else if (displayName.equals(ChatColor.GOLD + "SKELETON Spawner")) {
                    entityType = EntityType.SKELETON;
                } else if (displayName.equals(ChatColor.GOLD + "WITCH Spawner")) {
                    entityType = EntityType.WITCH;
                } else if (displayName.equals(ChatColor.GOLD + "CREEPER Spawner")) {
                    entityType = EntityType.CREEPER;
                } else if (displayName.equals(ChatColor.GOLD + "SPIDER Spawner")) {
                    entityType = EntityType.SPIDER;
                } else if (displayName.equals(ChatColor.GOLD + "BLAZE Spawner")) {
                    entityType = EntityType.BLAZE;
                } else if (displayName.equals(ChatColor.GOLD + "SHEEP Spawner")) {
                    entityType = EntityType.SHEEP;
                } else if (displayName.equals(ChatColor.GOLD + "CHICKEN Spawner")) {
                    entityType = EntityType.CHICKEN;
                } else if (displayName.equals(ChatColor.GOLD + "PIG Spawner")) {
                    entityType = EntityType.PIG;
                } else if (displayName.equals(ChatColor.GOLD + "GUARDIAN Spawner")) {
                    entityType = EntityType.GUARDIAN;
                } else if (displayName.equals(ChatColor.GOLD + "COW Spawner")) {
                    entityType = EntityType.COW;
                } else if (displayName.equals(ChatColor.GOLD + "SLIME Spawner")) {
                    entityType = EntityType.SLIME;
                } else if (event.getPlayer().hasPermission("spawnerpickaxe.bypass") && displayName.equals(ChatColor.GOLD + "ENDER_DRAGON Spawner")) {
                    entityType = EntityType.ENDER_DRAGON;
                } else if (event.getPlayer().hasPermission("spawnerpickaxe.bypass") && displayName.equals(ChatColor.GOLD + "WITHER Spawner")) {
                    entityType = EntityType.WITHER;
                } else if (event.getPlayer().hasPermission("spawnerpickaxe.bypass") && displayName.equals(ChatColor.GOLD + "GIANT Spawner")) {
                    entityType = EntityType.GIANT;
                } else if (event.getPlayer().hasPermission("spawnerpickaxe.bypass") && displayName.equals(ChatColor.GOLD + "RAVAGER Spawner")) {
                    entityType = EntityType.RAVAGER;
                } else {
                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(MessagesConfig.get().getString("spawner-place"))));
                    event.setCancelled(true);
                    return;
                }

                BlockState state = block.getState();
                if (state instanceof CreatureSpawner) {
                    CreatureSpawner spawner = (CreatureSpawner) state;
                    spawner.setSpawnedType(entityType);
                    spawner.update();
                    itemInHand.setAmount(itemInHand.getAmount() - 1);
                }
            }
        }
    }
    @Override
    public void onDisable() {
        getLogger().warning(ChatColor.GOLD + "[KteProject]" + ChatColor.RED + "Plugin Disabled!");
    }
}
