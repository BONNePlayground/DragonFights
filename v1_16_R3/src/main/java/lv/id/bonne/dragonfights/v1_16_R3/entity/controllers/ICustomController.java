package lv.id.bonne.dragonfights.v1_16_R3.entity.controllers;


import net.minecraft.server.v1_16_R3.Vec3D;


/**
 * Controller interface for dragon phases.
 */
public interface ICustomController
{
    /**
     * Process for phase initialization.
     */
    default void init()
    {
    }


    /**
     * This method process tick for controller.
     */
    void movementTick();


    /**
     * Process for phase stopping.
     */
    default void stop()
    {
    }


    /**
     * Default value for indication that dragon is not moving, either fly or on portal.
     * @return {@code true} if dragon is not moving, {@code false} otherwise.
     */
    default boolean isLanded()
    {
        return false;
    }


    /**
     * This method returns some kind of constant.
     * @return constant for controller.
     */
    float getConstant();


    /**
     * This method returns some kind of angle.
     * @return rotation angle.
     */
    float getRotation();


    /**
     * This method returns controller target location.
     * @return Target location for interface.
     */
    Vec3D getTargetLocation();


    /**
     * Returns current phase controller.
     * @return Current phase controller.
     */
    CustomControllerPhase<? extends ICustomController> getControllerPhase();
}
