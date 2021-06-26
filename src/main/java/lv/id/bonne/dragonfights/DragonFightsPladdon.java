//
// Created by BONNe
// Copyright - 2021
//


package lv.id.bonne.dragonfights;


import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.plugin.Plugin;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.addons.Pladdon;


/**
 * @author tastybento
 */
@Plugin(name="DragonFightsPladdon", version="1.0")
@Dependency(value = "BentoBox")
public class DragonFightsPladdon extends Pladdon
{
    @Override
    public Addon getAddon()
    {
        return new DragonFightsAddon();
    }
}