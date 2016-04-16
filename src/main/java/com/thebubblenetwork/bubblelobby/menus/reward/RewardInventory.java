package com.thebubblenetwork.bubblelobby.menus.reward;

import com.thebubblenetwork.api.framework.player.BukkitBubblePlayer;
import com.thebubblenetwork.api.framework.plugin.util.BubbleRunnable;
import com.thebubblenetwork.api.framework.util.mc.items.ItemStackBuilder;
import com.thebubblenetwork.api.framework.util.mc.menu.Menu;
import com.thebubblenetwork.bubblelobby.BubbleLobby;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * The Bubble Network 2016
 * BubbleLobby
 * 16/04/2016 {16:53}
 * Created April 2016
 */
public class RewardInventory extends Menu{
    private static List<RewardItem> itemList = Arrays.asList(
            new RewardItem("Daily Tokens","daily", TimeUnit.DAYS.toMillis(1), null, new String[]{"Redeem tokens every day!"}){
                public void giveReward(BukkitBubblePlayer bubblePlayer, Player player) {
                    super.giveReward(bubblePlayer, player);
                    player.sendMessage(ChatColor.GOLD + "+100 Tokens");
                    bubblePlayer.setTokens(bubblePlayer.getTokens() + 100);
                }
            },
            new RewardItem("Weekly Tokens","weeklydonator", TimeUnit.DAYS.toMillis(1), "You need a donator rank to redeem this", new String[]{"Get a huge weekly token package"}){
                public void giveReward(BukkitBubblePlayer bubblePlayer, Player player) {
                    super.giveReward(bubblePlayer, player);
                    player.sendMessage(ChatColor.GOLD + "+2000 Tokens");
                    bubblePlayer.setTokens(bubblePlayer.getTokens() + 2000);
                }
            }


    );

    private static Map<UUID, RewardInventory> inventoryMap = new HashMap<>();

    public static void startRunnable(){
        new BubbleRunnable(){
            @Override
            public void run() {
                for(RewardInventory inventory: inventoryMap.values()){
                    inventory.update();
                }
            }
        }.runTaskTimerAsynchronously(BubbleLobby.getInstance(), TimeUnit.SECONDS, 30);
    }

    public static RewardInventory getInventory(Player p){
        if(inventoryMap.containsKey(p.getUniqueId())){
            return inventoryMap.get(p.getUniqueId());
        }
        RewardInventory inventory = new RewardInventory(BukkitBubblePlayer.getObject(p.getUniqueId()));
        inventoryMap.put(p.getUniqueId(), inventory);
        return inventory;
    }

    public static void removeInventory(Player p){
        inventoryMap.remove(p.getUniqueId());
    }

    public static void removeAll(){
        inventoryMap.clear();
    }

    private BukkitBubblePlayer player;

    public RewardInventory(BukkitBubblePlayer player) {
        super(ChatColor.GREEN + "Rewards", getRoundedInventorySize(itemList.size()));
        this.player = player;
        update();
    }

    @Override
    public void click(Player player, ClickType clickType, int i, ItemStack itemStack) {
        if(i < itemList.size()){
            RewardItem item = itemList.get(i);
            if(item.hasPermission(this.player)){
                if(item.canUseReward(this.player)){
                    item.use(this.player);
                    item.giveReward(this.player, player);
                }
                else{
                    player.sendMessage(ChatColor.BLUE + "You need to wait " + ChatColor.AQUA + this.player.getWaitTime(item.getName(), item.getTime()) + ChatColor.BLUE + " till you can use this");
                }
            }
            else{
                player.playSound(player.getLocation(), Sound.BLAZE_DEATH, 1f, 1f);
                player.sendMessage(ChatColor.RED + item.getDenymsg());
            }
        }
    }

    @Override
    public ItemStack[] generate() {
        ItemStack[] itemStacks = new ItemStack[getInventory().getSize()];
        int i = 0;
        for(RewardItem item: itemList){
            ItemStackBuilder builder = new ItemStackBuilder();
            if(item.hasPermission(player)){
                if(item.canUseReward(player)){
                    builder.withType(Material.EMERALD);
                    builder.withName(ChatColor.GREEN + "Click to redeem");
                }
                else{
                    builder.withType(Material.WATCH);
                    builder.withName(ChatColor.RED + "Wait " + player.getWaitTime(item.getName(), item.getTime()));
                }
            }
            else{
                builder.withType(Material.BARRIER);
                builder.withName(ChatColor.DARK_RED + item.getDenymsg());
            }
            for(String s: item.getDescription()){
                builder.withLore(ChatColor.GRAY + s);
            }
            itemStacks[i] = builder.build();
            i++;
        }
        return itemStacks;
    }

    @Override
    public void show(Player player) {
        super.show(player);
    }
}