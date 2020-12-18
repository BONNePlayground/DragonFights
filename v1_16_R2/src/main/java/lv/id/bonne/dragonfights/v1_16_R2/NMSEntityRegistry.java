package lv.id.bonne.dragonfights.v1_16_R2;


import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import io.github.iltotore.customentity.HashCustomRegistry;
import io.github.iltotore.customentity.type.CustomEntityRoot;
import io.github.iltotore.customentity.util.ServerVersion;
import net.minecraft.server.v1_16_R2.DataConverterRegistry;
import net.minecraft.server.v1_16_R2.DataConverterTypes;
import net.minecraft.server.v1_16_R2.EntityInsentient;
import net.minecraft.server.v1_16_R2.EntityPositionTypes;
import net.minecraft.server.v1_16_R2.EntityTypes;
import net.minecraft.server.v1_16_R2.EnumCreatureType;
import net.minecraft.server.v1_16_R2.IRegistry;
import net.minecraft.server.v1_16_R2.MinecraftKey;
import net.minecraft.server.v1_16_R2.SharedConstants;
import net.minecraft.server.v1_16_R2.World;


public class NMSEntityRegistry extends HashCustomRegistry
{

    private Map<EntityTypes<?>, Object> positionMap = null;


    @SuppressWarnings("unchecked")
    public NMSEntityRegistry()
    {
        try
        {
            Field field = EntityPositionTypes.class.getDeclaredField("a");
            field.setAccessible(true);
            this.positionMap = (Map<EntityTypes<?>, Object>) field.get(null);
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public void applyRegister(CustomEntityRoot<? extends Entity> type)
    {
        //Creating a minecraft key with the name
        MinecraftKey baseKey = MinecraftKey.a(type.getBaseKey());
        MinecraftKey minecraftKey = MinecraftKey.a(type.getKey());
        Validate.notNull(minecraftKey, "Using an invalid name for registering a custom entity. Name: " + type.getKey());

        if (IRegistry.ENTITY_TYPE.getOptional(minecraftKey).isPresent())
        {
            throw new IllegalArgumentException("Entity with key " + type.getKey() + " already exists !");
        }

        //Getting the data converter type for the default entity and adding that to the custom mob.
        Map<Object, Type<?>> typeMap = (Map<Object, Type<?>>) DataConverterRegistry.a().
            getSchema(DataFixUtils.makeKey(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion()))).
            findChoiceType(DataConverterTypes.ENTITY).types();

        if (!typeMap.containsKey(baseKey.toString()))
        {
            Bukkit.getLogger().warning("Cannot find vanilla mob named " + baseKey);
        }

        typeMap.put(minecraftKey.toString(), typeMap.get(baseKey.toString()));

        try
        {
            Constructor<? extends net.minecraft.server.v1_16_R2.Entity> constructor =
                (Constructor<? extends net.minecraft.server.v1_16_R2.Entity>) type.getNMSClass(ServerVersion.v1_16_2).
                    getConstructor(EntityTypes.class, World.class);

            EntityTypes.b<? extends net.minecraft.server.v1_16_R2.Entity> nmsCreator = (entityType, world) -> {
                try
                {
                    return constructor.newInstance(entityType, world);
                }
                catch (InstantiationException | IllegalAccessException | InvocationTargetException e)
                {
                    throw new RuntimeException(e);
                }
            };

            EnumCreatureType nmsCreatureType = EnumCreatureType.valueOf(type.getCreatureType().name());

            EntityTypes<net.minecraft.server.v1_16_R2.Entity> customEntityNMSEntityType =
                EntityTypes.Builder.a(nmsCreator, nmsCreatureType).a(type.getKey());

            IRegistry.a(IRegistry.ENTITY_TYPE,
                type.getBaseID(ServerVersion.v1_16_2),
                type.getKey(),
                customEntityNMSEntityType);

            //Is an insentient entity? Also copy the EntityPositionTypes value.
            if (type.getNMSClass(ServerVersion.v1_16_2).isAssignableFrom(EntityInsentient.class))
            {
                Object entityInformation = positionMap.get(customEntityNMSEntityType);
                positionMap.put(customEntityNMSEntityType, entityInformation);
            }

            type.setHandle(customEntityNMSEntityType);
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
    }
}