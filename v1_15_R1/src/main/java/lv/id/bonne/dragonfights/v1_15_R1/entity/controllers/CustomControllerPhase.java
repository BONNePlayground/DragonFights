package lv.id.bonne.dragonfights.v1_15_R1.entity.controllers;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import lv.id.bonne.dragonfights.v1_15_R1.entity.BentoBoxEnderDragon;


public class CustomControllerPhase<T extends ICustomController>
{
    private CustomControllerPhase(int i, Class<? extends ICustomController> pClass, String s)
    {
        this.index = i;
        this.constructor = pClass;
        this.name = s;
    }


    public ICustomController createInstance(BentoBoxEnderDragon enderDragon)
    {
        try
        {
            return this.createClass().newInstance(enderDragon);
        }
        catch (Exception exception)
        {
            throw new Error(exception);
        }
    }


    protected Constructor<? extends ICustomController> createClass() throws NoSuchMethodException
    {
        return this.constructor.getConstructor(BentoBoxEnderDragon.class);
    }


    public int phaseIndex()
    {
        return this.index;
    }


    public String toString()
    {
        return this.name + " (#" + this.index + ")";
    }


    public static CustomControllerPhase<?> getById(int i)
    {
        return i >= 0 && i < CustomControllerPhase.phases.length ? CustomControllerPhase.phases[i] :
            CustomControllerPhase.HOLDING_PATTERN;
    }


    public static int numberOfPhases()
    {
        return CustomControllerPhase.phases.length;
    }


    private static <T extends ICustomController> CustomControllerPhase<T> createClass(Class<T> pClass, String s)
    {
        CustomControllerPhase<T> phase = new CustomControllerPhase<>(CustomControllerPhase.phases.length, pClass, s);

        CustomControllerPhase.phases = Arrays.copyOf(CustomControllerPhase.phases, CustomControllerPhase.phases.length + 1);
        CustomControllerPhase.phases[phase.phaseIndex()] = phase;
        return phase;
    }


    /**
     * Phase constructor.
     */
    private final Class<? extends ICustomController> constructor;

    /**
     * Phase index
     */
    private final int index;

    /**
     * Phase name
     */
    private final String name;

    /**
     * This array stores are phases.
     */
    private static CustomControllerPhase<?>[] phases = new CustomControllerPhase[0];

    public static final CustomControllerPhase<CustomControllerHold> HOLDING_PATTERN =
        createClass(CustomControllerHold.class, "HoldingPattern");

    public static final CustomControllerPhase<CustomControllerStrafe> STRAFE_PLAYER =
        createClass(CustomControllerStrafe.class, "StrafePlayer");

    public static final CustomControllerPhase<CustomControllerLandingFly> LANDING_APPROACH =
        createClass(CustomControllerLandingFly.class, "LandingApproach");

    public static final CustomControllerPhase<CustomControllerLanding> LANDING =
        createClass(CustomControllerLanding.class, "Landing");

    public static final CustomControllerPhase<CustomControllerFly> TAKEOFF =
        createClass(CustomControllerFly.class, "Takeoff");

    public static final CustomControllerPhase<CustomControllerLandedFlame> SITTING_FLAMING =
        createClass(CustomControllerLandedFlame.class, "SittingFlaming");

    public static final CustomControllerPhase<CustomControllerLandedSearch> SITTING_SCANNING =
        createClass(CustomControllerLandedSearch.class, "SittingScanning");

    public static final CustomControllerPhase<CustomControllerLandedAttack> SITTING_ATTACKING =
        createClass(CustomControllerLandedAttack.class, "SittingAttacking");

    public static final CustomControllerPhase<CustomControllerCharge> CHARGING_PLAYER =
        createClass(CustomControllerCharge.class, "ChargingPlayer");

    public static final CustomControllerPhase<CustomControllerDying> DYING =
        createClass(CustomControllerDying.class, "Dying");

    public static final CustomControllerPhase<CustomControllerHover> HOVER =
        createClass(CustomControllerHover.class, "Hover");
}
