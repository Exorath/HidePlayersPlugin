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

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by toonsev on 5/9/2017.
 */
public class Main extends JavaPlugin {
    private static Main instance;

    private HidePlayersHandler hidePlayersHandler;
    private ItemHandler itemHandler;

    @Override
    public void onEnable() {
        String address = getHidePlayersServiceAddress();
        Main.instance = this;
        this.hidePlayersHandler = new HidePlayersHandler(address);
        this.itemHandler = new ItemHandler(hidePlayersHandler, new YamlConfigProvider(getConfig()));

        Bukkit.getPluginManager().registerEvents(hidePlayersHandler, this);
        Bukkit.getPluginManager().registerEvents(itemHandler, this);
    }

    public String getHidePlayersServiceAddress() {
        String address = System.getenv("HIDEPLAYERS_SERVICE_ADDRESS");
        if (address == null) {
            System.out.println("No HIDEPLAYERS_SERVICE_ADDRESS env found.");
            System.exit(1);
        }
        return address;
    }

    public static Main getInstance() {
        return instance;
    }
}
