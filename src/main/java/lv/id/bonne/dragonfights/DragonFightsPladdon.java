//
// Created by BONNe
// Copyright - 2021
//


package lv.id.bonne.dragonfights;


import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Plugin;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.addons.Pladdon;


/**
 * @author tastybento
 */
@Plugin(name="Pladdon", version="1.0")
@ApiVersion(ApiVersion.Target.v1_17)
@Dependency(value = "BentoBox")
public class DragonFightsPladdon extends Pladdon
{
    @Override
    public Addon getAddon()
    {
        return new DragonFightsAddon();
    }
}