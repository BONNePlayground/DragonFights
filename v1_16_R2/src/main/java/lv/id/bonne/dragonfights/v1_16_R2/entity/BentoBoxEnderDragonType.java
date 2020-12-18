package lv.id.bonne.dragonfights.v1_16_R2.entity;


import org.bukkit.entity.EnderDragon;

import io.github.iltotore.customentity.type.DefaultEntityType;
import io.github.iltotore.customentity.util.ServerVersion;
import net.minecraft.server.v1_16_R2.EntityTypes;
import net.minecraft.server.v1_16_R2.IRegistry;


/**
 * This class allows to define BentoBox Ender Dragon into NMS.
 */
public class BentoBoxEnderDragonType implements DefaultEntityType<EnderDragon>
{
    /**
     * Returns ID for current entity based on server version?
     * @param version ServerVersion instance.
     * @return entity id number.
     */
    @Override
    public int getBaseID(ServerVersion version)
    {
        // Add as next entity.
        return IRegistry.ENTITY_TYPE.a(EntityTypes.ENDER_DRAGON);
    }


    @Override
    public boolean isVanilla(ServerVersion version)
    {
        return false;
    }


    /**
     * Returns new entity NMS class.
     * @param version the version of the server.
     * @return BentoBoxEnderDragon class.
     */
    @Override
    public Class<?> getNMSClass(ServerVersion version)
    {
        return BentoBoxEnderDragon.class;
    }
}
