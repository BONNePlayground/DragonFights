package lv.id.bonne.dragonfights.v1_16_R2.entity.controllers;

import lv.id.bonne.dragonfights.v1_16_R2.entity.BentoBoxEnderDragon;


/**
 * This method process landed attachk phase.
 */
public class CustomControllerLandedAttack extends AbstractCustomController
{
    public CustomControllerLandedAttack(BentoBoxEnderDragon enderDragon)
    {
        super(enderDragon);
    }


    @Override
    public void init()
    {
        this.counter = 0;
    }


    @Override
    public void movementTick()
    {
        if (this.counter++ >= 40)
        {
            this.enderDragon.getCustomControllerManager().setControllerPhase(CustomControllerPhase.SITTING_FLAMING);
        }
    }


    @Override
    public boolean isLanded()
    {
        return true;
    }


    @Override
    public CustomControllerPhase<CustomControllerLandedAttack> getControllerPhase()
    {
        return CustomControllerPhase.SITTING_ATTACKING;
    }


    /**
     * Some kind of counter.
     */
    private int counter;
}
