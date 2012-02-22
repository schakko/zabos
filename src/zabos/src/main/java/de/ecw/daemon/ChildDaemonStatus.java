package de.ecw.daemon;

import de.ecw.zabos.types.UnixTime;

public class ChildDaemonStatus
{
    private static int instanceCounter = 0;

    private final static int getInstanceCounter()
    {
        return ++instanceCounter;
    }

    private UnixTime inactiveSince;

    private UnixTime lastReactivationPoint;

    private int totalReactivationTries = 0;

    private String name = "instance_" + ChildDaemonStatus.getInstanceCounter();

    public ChildDaemonStatus()
    {
        setInactiveSince(UnixTime.now());
    }
    
    public void incrementReactivationTries()
    {
        totalReactivationTries++;
    }

    public int getReactivationTries()
    {
        return totalReactivationTries;
    }

    public void resetReactivationTries()
    {
        totalReactivationTries = 0;
    }

    public void setInactiveSince(UnixTime inactiveSince)
    {
        this.inactiveSince = inactiveSince;
    }

    public UnixTime getInactiveSince()
    {
        return inactiveSince;
    }

    public void setLastReactivationPoint(UnixTime lastReactivationPoint)
    {
        this.lastReactivationPoint = lastReactivationPoint;
    }

    public UnixTime getLastReactivationPoint()
    {
        return lastReactivationPoint;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
