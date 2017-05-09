/*
 * Copyright 2017 Exorath
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.exorath.plugin.hideplayers;

import com.exorath.service.hideplayers.res.VisibilityPlayer;
import com.exorath.service.hideplayers.res.VisibleState;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by toonsev on 5/9/2017.
 */
public class ItemHandler implements Listener {
    private HidePlayersHandler hidePlayersHandler;
    private Integer itemSlot;

    private Set<Player> antiSpam = new HashSet<>();

    public ItemHandler(HidePlayersHandler hidePlayersHandler, YamlConfigProvider yamlConfigProvider) {
        this.hidePlayersHandler = hidePlayersHandler;
        itemSlot = yamlConfigProvider.getItemSlot();
    }


    @EventHandler
    public void onVisibilityChangeEvent(VisibilityChangeEvent event) {
        if (itemSlot != null)
            event.getPlayer().getInventory().setItem(itemSlot, getItem(event.getPlayer(), event.getVisibilityPlayer()));
    }

    private ItemStack getItem(Player player, VisibilityPlayer visibilityPlayer) {
        ItemStack itemStack = new ItemStack(Material.INK_SACK);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (visibilityPlayer.getState() == VisibleState.ALL) {
            itemStack.setData(new Dye(DyeColor.LIME));
            itemMeta.setDisplayName(ChatColor.GREEN + "Players VISIBLE " + ChatColor.GRAY + "(Click to hide)");
        } else {
            itemStack.setData(new Dye(DyeColor.GRAY));
            itemMeta.setDisplayName(ChatColor.GREEN + "Players HIDDEN " + ChatColor.GRAY + "(Click to show)");
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (itemSlot == null)
            return;
        if (event.getPlayer().getInventory().getHeldItemSlot() != itemSlot)
            return;
        event.setCancelled(true);
        if (antiSpam.contains(event.getPlayer())) {
            event.getPlayer().sendMessage(ChatColor.RED + "Wait a couple of seconds...");
            return;
        }
        antiSpam.add(event.getPlayer());
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> antiSpam.remove(event.getPlayer()), 60l);
        if (!hidePlayersHandler.getPlayers().containsKey(event.getPlayer())) {
            event.getPlayer().sendMessage(ChatColor.RED + "Failed to set visibility, please wait or relog...");
            return;
        }
        VisibleState state = hidePlayersHandler.getPlayers().get(event.getPlayer()).getState();
        hidePlayersHandler.setState(event.getPlayer(), new VisibilityPlayer(state == VisibleState.ALL ? VisibleState.NONE : VisibleState.ALL));
    }
}
