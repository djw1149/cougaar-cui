package org.cougaar.lib.uiframework.ui.models;

/**
 * This class encapsulates the thresholds associated with a stoplight chart.
 */
public class StoplightThresholdModel
{
    private float yellowMin = 0;
    private float greenMin = 0;
    private float greenMax = 0;
    private float yellowMax = 0;

    /**
     * Default constructor.  Creates new stoplight thresholds with all
     * thresholds set to zero.
     */
    public StoplightThresholdModel() {}

    /**
     * Create new stoplight thresholds based on given parameters.
     *
     * @param yellowMin minimum value for yellow encoding
     * @param greenMin mimimum value for green encoding
     * @param greenMax maximum value for green encoding
     * @param yellowMax maximum value for yellow encoding
     */
    public StoplightThresholdModel(float yellowMin, float greenMin,
                                   float greenMax, float yellowMax)
    {
        this.yellowMin = yellowMin;
        this.greenMin = greenMin;
        this.greenMax = greenMax;
        this.yellowMax = yellowMax;
    }

    /**
     * Set the minimum yellow threshold
     *
     * @param yellowMin new minimum yellow threshold
     */
    public void setYellowMin(float yellowMin)
    {
        this.yellowMin = yellowMin;
    }

    /**
     * Get the current minimum yellow threshold
     *
     * @return the current minimum yellow threshold
     */
    public float getYellowMin()
    {
        return yellowMin;
    }

    /**
     * Set the minimum greem threshold
     *
     * @param greenMin new minimum green threshold
     */
    public void setGreenMin(float greenMin)
    {
        this.greenMin = greenMin;
    }

    /**
     * Get the current minimum green threshold
     *
     * @return the current minimum green threshold
     */
    public float getGreenMin()
    {
        return greenMin;
    }

    /**
     * Set the maximum green threshold
     *
     * @param greenMax new maximum green threshold
     */
    public void setGreenMax(float greenMax)
    {
        this.greenMax = greenMax;
    }

    /**
     * Get the current maximum green threshold
     *
     * @return the current maximum green threshold
     */
    public float getGreenMax()
    {
        return greenMax;
    }

    /**
     * Set the maximum yellow threshold
     *
     * @param yellowMax new maximum yellow threshold
     */
    public void setYellowMax(float yellowMax)
    {
        this.yellowMax = yellowMax;
    }

    /**
     * Get the current maximum yellow threshold
     *
     * @return the current maximum yellow threshold
     */
    public float getYellowMax()
    {
        return yellowMax;
    }

    /**
     * Check for equality between this object and another
     *
     * @param obj the object to compare to
     * @return true if objects are equal
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof StoplightThresholdModel)
        {
            StoplightThresholdModel slt = (StoplightThresholdModel)obj;
            if ((yellowMin == slt.yellowMin) &&
                (greenMin == slt.greenMin) &&
                (greenMax == slt.greenMax) &&
                (yellowMax == slt.yellowMax))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns string representation of the thresholds
     *
     * @return string representation of the thresholds
     */
    public String toString()
    {
        return  "YellowMin: " + yellowMin + ", GreenMin: " + greenMin +
                ", GreenMax: " + greenMax + ", YellowMax: " + yellowMax;
    }
}