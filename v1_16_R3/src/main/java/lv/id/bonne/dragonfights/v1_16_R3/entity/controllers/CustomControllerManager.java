package lv.id.bonne.dragonfights.v1_16_R3.entity.controllers;


import lv.id.bonne.dragonfights.v1_16_R3.entity.BentoBoxEnderDragon;
import net.minecraft.server.v1_16_R3.EntityEnderDragon;


/**
 * This manager controls dragon phases.
 */
public class CustomControllerManager
{
    public CustomControllerManager(BentoBoxEnderDragon enderDragon)
    {
        this.enderDragon = enderDragon;
        this.setControllerPhase(CustomControllerPhase.HOVER);
    }


    public void setControllerPhase(CustomControllerPhase<?> controllerPhase)
    {
        if (this.currentDragonController == null ||
            controllerPhase != this.currentDragonController.getControllerPhase())
        {
            if (this.currentDragonController != null)
            {
                this.currentDragonController.stop();
            }

            this.currentDragonController = this.getPhase(controllerPhase);
            this.enderDragon.getDataWatcher().set(EntityEnderDragon.PHASE, controllerPhase.phaseIndex());

            this.currentDragonController.init();
        }
    }


    public ICustomController getCurrentPhase()
    {
        return this.currentDragonController;
    }


    public <T extends ICustomController> T getPhase(CustomControllerPhase<T> dragoncontrollerphase)
    {
        int i = dragoncontrollerphase.phaseIndex();

        if (this.dragonControllers[i] == null)
        {
            this.dragonControllers[i] = dragoncontrollerphase.createInstance(this.enderDragon);
        }

        return (T) this.dragonControllers[i];
    }


    private final BentoBoxEnderDragon enderDragon;

    private final ICustomController[] dragonControllers = new ICustomController[CustomControllerPhase.numberOfPhases()];

    private ICustomController currentDragonController;
}
