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

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Created by toonsev on 5/9/2017.
 */
public class YamlConfigProvider {
    private FileConfiguration fileConfiguration;

    public YamlConfigProvider(FileConfiguration fileConfiguration) {
        this.fileConfiguration = fileConfiguration;
    }

    public Integer getItemSlot() {
        if (fileConfiguration.contains("itemSlot"))
            return fileConfiguration.getInt("itemSlot");
        return null;
    }
}