package lv.id.bonne.dragonfights.v1_15_R1;

import io.github.iltotore.customentity.CustomRegistry;
import io.github.iltotore.customentity.SpawnManager;

public class NMSHandler implements io.github.iltotore.customentity.NMSHandler {

    private CustomRegistry registry = new NMSEntityRegistry();
    private SpawnManager spawnManager = new NMSSpawnManager();

    @Override
    public CustomRegistry getRegistry() {
        return registry;
    }

    @Override
    public SpawnManager getSpawnManager() {
        return spawnManager;
    }
}
