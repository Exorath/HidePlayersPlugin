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

import com.exorath.service.hideplayers.api.HidePlayersServiceAPI;
import com.exorath.service.hideplayers.res.Success;
import com.exorath.service.hideplayers.res.VisibilityPlayer;
import com.exorath.service.hideplayers.res.VisibleState;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by toonsev on 5/9/2017.
 */
public class HidePlayersHandler implements Listener {
    private HidePlayersServiceAPI hidePlayersServiceAPI;
    private Map<Player, VisibilityPlayer> players = new HashMap<>();

    public HidePlayersHandler(String address) {
        this.hidePlayersServiceAPI = new HidePlayersServiceAPI(address);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        for (Map.Entry<Player, VisibilityPlayer> entry : players.entrySet())
            if (entry.getValue().getState() == VisibleState.NONE)
                entry.getKey().hidePlayer(event.getPlayer());

        String uuid = event.getPlayer().getUniqueId().toString();
        Observable.<VisibilityPlayer>create(sub -> hidePlayersServiceAPI.getVisibilityPlayer(uuid))
                .subscribeOn(Schedulers.io()).subscribe(visibilityPlayer -> {
            Bukkit.getScheduler().runTask(Main.getInstance(), () -> handlePlayer(event.getPlayer(), visibilityPlayer));
        });
    }

    public Map<Player, VisibilityPlayer> getPlayers() {
        return players;
    }

    public void setState(Player player, VisibilityPlayer visibilityPlayer) {
        String uuid = player.getUniqueId().toString();
        handlePlayer(player, visibilityPlayer);
        Schedulers.io().scheduleDirect(() -> {
            Success success = hidePlayersServiceAPI.setVisibilityPlayer(uuid, visibilityPlayer);
            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                if (success.isSuccess())
                    player.sendMessage(ChatColor.GREEN + "Visibility preference saved.");
                else
                    player.sendMessage(ChatColor.RED + "Error: " + success.getError());
            });
        });
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        players.remove(event.getPlayer());
    }

    private void handlePlayer(Player player, VisibilityPlayer visibilityPlayer) {
        if (players.containsKey(player) && players.get(player).getState() == visibilityPlayer.getState())
            return;
        if (visibilityPlayer.getState() == VisibleState.ALL && players.containsKey(player))
            Bukkit.getOnlinePlayers().forEach(toShow -> player.showPlayer(toShow));
        else if (visibilityPlayer.getState() == VisibleState.NONE)
            Bukkit.getOnlinePlayers().forEach(toShow -> player.hidePlayer(toShow));
        players.put(player, visibilityPlayer);
        Bukkit.getPluginManager().callEvent(new VisibilityChangeEvent(player, visibilityPlayer));
    }
}
