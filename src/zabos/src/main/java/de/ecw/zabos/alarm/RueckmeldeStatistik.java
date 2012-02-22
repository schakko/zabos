package de.ecw.zabos.alarm;

/**
 * Wird für das Generieren der Schleifenreports verwendet
 * 
 * @author bsp
 * 
 */
public class RueckmeldeStatistik
{
    protected int numJa = 0;

    protected int numNein = 0;

    protected int numSpaeter = 0;

    protected int numUnbekannt = 0; // nicht zurueckgemeldet

    protected int numTotal;

    public RueckmeldeStatistik(int _numTotal)
    {
        numTotal = _numTotal;
    }

    public void incJa()
    {
        numJa++;
    }

    public void incNein()
    {
        numNein++;
    }

    public void incSpaeter()
    {
        numSpaeter++;
    }

    public void incUnbekannt()
    {
        numUnbekannt++;
    }

    public void incTotal()
    {
        numTotal++;
    }

    public int getNumJa()
    {
        return numJa;
    }

    public int getNumNein()
    {
        return numNein;
    }

    public int getNumSpaeter()
    {
        return numSpaeter;
    }

    public int getNumUnbekannt()
    {
        return numUnbekannt;
    }

    public int getNumTotal()
    {
        return numTotal;
    }
}