
package org.openautomaker.base.postprocessor;

/**
 *
 * @author Ian
 */
public class ExtruderMix
{

    double eFactor = 0;
    double dFactor = 0;
    int layerNumber = 0;

    /**
     *
     * @param eFactor
     * @param dFactor
     * @param layerNumber
     */
    public ExtruderMix(double eFactor, double dFactor, int layerNumber)
    {
        this.eFactor = eFactor;
        this.dFactor = dFactor;
        this.layerNumber = layerNumber;
    }

    /**
     *
     * @return
     */
    public double getEFactor()
    {
        return eFactor;
    }

    /**
     *
     * @param eFactor
     */
    public void setEFactor(double eFactor)
    {
        this.eFactor = eFactor;
    }

    /**
     *
     * @return
     */
    public double getDFactor()
    {
        return dFactor;
    }

    /**
     *
     * @param dFactor
     */
    public void setDFactor(double dFactor)
    {
        this.dFactor = dFactor;
    }

    /**
     *
     * @return
     */
    public int getLayerNumber()
    {
        return layerNumber;
    }

    /**
     *
     * @param layerNumber
     */
    public void setLayerNumber(int layerNumber)
    {
        this.layerNumber = layerNumber;
    }
}
