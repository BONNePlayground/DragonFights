package lv.id.bonne.dragonfights.v1_15_R1;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import io.github.iltotore.customentity.BiomeSpawn;
import io.github.iltotore.customentity.type.CustomEntityRoot;
import io.github.iltotore.customentity.HashCustomRegistry;
import io.github.iltotore.customentity.util.ReflectUtil;
import io.github.iltotore.customentity.util.ServerVersion;
import io.github.iltotore.customentity.util.ThrowingConsumer;
import net.minecraft.server.v1_15_R1.*;
import org.apache.commons.lang.Validate;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class NMSEntityRegistry extends HashCustomRegistry {

    private Map<EntityTypes<?>, Object> positionMap = null;
    private Field biomeEntityType;

    @SuppressWarnings("unchecked")
    public NMSEntityRegistry() {
        try {
            Field field = EntityPositionTypes.class.getDeclaredField("a");
            field.setAccessible(true);
            this.positionMap = (Map<EntityTypes<?>, Object>) field.get(null);

            biomeEntityType = BiomeBase.BiomeMeta.class.getDeclaredField("b");
            ReflectUtil.setFinal(biomeEntityType, false);
        } catch(NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public void applyRegister(CustomEntityRoot<? extends Entity> type) {
        //Creating a minecraft key with the name
        MinecraftKey baseKey = MinecraftKey.a(type.getBaseKey());
        MinecraftKey minecraftKey = MinecraftKey.a(type.getKey());
        Validate.notNull(minecraftKey, "Using an invalid name for registering a custom entity. Name: " + type.getKey());

        if(IRegistry.ENTITY_TYPE.getOptional(minecraftKey).isPresent())
            throw new IllegalArgumentException("Entity with key " + type.getKey() + " already exists !");

        //Getting the data converter type for the default entity and adding that to the custom mob.
        Map<Object, Type<?>> typeMap = (Map<Object, Type<?>>) DataConverterRegistry.a().getSchema(DataFixUtils.makeKey(DataFixUtils.makeKey(SharedConstants.getGameVersion().getWorldVersion()))).findChoiceType(DataConverterTypes.ENTITY).types();
        typeMap.put(minecraftKey.toString(), typeMap.get(baseKey));
        try {
            Constructor<? extends net.minecraft.server.v1_15_R1.Entity> constructor = (Constructor<? extends net.minecraft.server.v1_15_R1.Entity>) type.getNMSClass(ServerVersion.v1_15).getConstructor(EntityTypes.class, World.class);


            EntityTypes.b<? extends net.minecraft.server.v1_15_R1.Entity> nmsCreator = (entityType, world) -> {
                net.minecraft.server.v1_15_R1.Entity entity = null;
                try {
                    entity = constructor.newInstance(entityType, world);
                } catch(InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                return entity;
            };

            EnumCreatureType nmsCreatureType = EnumCreatureType.valueOf(type.getCreatureType().name());

            EntityTypes<net.minecraft.server.v1_15_R1.Entity> customEntityNMSEntityType = EntityTypes.a.a(nmsCreator, nmsCreatureType)
                    .a(type.getKey());

            IRegistry.ENTITY_TYPE.a(type.getBaseID(ServerVersion.v1_15), minecraftKey, customEntityNMSEntityType);

            //Is an insentient entity? Also copy the EntityPositionTypes value.
            if(type.getNMSClass(ServerVersion.v1_15).isAssignableFrom(EntityInsentient.class)) {
                Object entityInformation = positionMap.get(customEntityNMSEntityType);
                positionMap.put(customEntityNMSEntityType, entityInformation);
            }

            type.setHandle(customEntityNMSEntityType);


            if(type.isVanilla(ServerVersion.v1_15)) overrideSpawn(nmsCreatureType, baseKey, customEntityNMSEntityType);

            for(BiomeSpawn spawn : type.getSpawns(ServerVersion.v1_16_1))
                registerSpawn(nmsCreatureType, customEntityNMSEntityType, spawn);

        } catch(NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerSpawn(EnumCreatureType creatureType, EntityTypes<? extends net.minecraft.server.v1_15_R1.Entity> customType, BiomeSpawn spawn) {
        Stream.of(spawn.getBiomes())
                .map(Biome::name)
                .map(String::toLowerCase)
                .map(MinecraftKey::a)
                .map(IRegistry.BIOME::get)
                .filter(Objects::nonNull)
                .forEach(biomeBase -> {
                    List<BiomeBase.BiomeMeta> meta = biomeBase.getMobs(creatureType);
                    if(meta == null) return;
                    meta.add(new BiomeBase.BiomeMeta(customType, spawn.getWeight(), spawn.getMin(), spawn.getMax()));
                });
    }

    private void overrideSpawn(EnumCreatureType creatureType, MinecraftKey baseKey, EntityTypes<? extends net.minecraft.server.v1_15_R1.Entity> customType) {
        IRegistry.BIOME.d()
                .map(biomeBase -> biomeBase.getMobs(creatureType))
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(meta -> Objects.equals(((IRegistry<EntityTypes<? extends net.minecraft.server.v1_15_R1.Entity>>) IRegistry.ENTITY_TYPE).getKey(meta.b), baseKey))
                .forEach((ThrowingConsumer<BiomeBase.BiomeMeta>) biomeMeta -> biomeEntityType.set(biomeMeta, customType));
    }
}