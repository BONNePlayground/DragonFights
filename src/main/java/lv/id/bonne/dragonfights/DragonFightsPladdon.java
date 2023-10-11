//
// Created by BONNe
// Copyright - 2021
//


package lv.id.bonne.dragonfights;


import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Plugin;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.addons.Pladdon;


/**
 * @author bonne
 */
@Plugin(name="DragonFights", version="1.7.0")
@ApiVersion(ApiVersion.Target.v1_19)
public class DragonFightsPladdon extends Pladdon
{
    @Override
    public Addon getAddon()
    {
        return new DragonFightsAddon();
    }
}