package de.ecw.zabos.util;

import de.ecw.zabos.types.UnixTime;

/**
 * Updatet in regelmässigen Abständen eine Datei, die von einem externen
 * WatchDog Script überprüft wird.
 * 
 * 
 * @author bsp
 * 
 */
public class TraceDog implements IWatchdog
{

    // private static Log log = Logger.getLog(TraceDog.class);

    private static final UnixTime INTERVAL = new UnixTime(1000 * 30);

    private String file_name;

    private UnixTime nextWatchDogCheckpoint;

    public TraceDog(String _filename)
    {
        setWatchdogFile(_filename);
        setCheckpoint();
    }

    private void setCheckpoint()
    {
        FileUtils.touch(file_name);
        nextWatchDogCheckpoint = UnixTime.now();
        nextWatchDogCheckpoint.add(INTERVAL);
        // log.debug("CHECKPOINT");
    }

    public void trace()
    {
        // log.debug(" now=" + UnixTime.now() + " next=" +
        // nextWatchDogCheckpoint);
        if (UnixTime.now().isLaterThan(nextWatchDogCheckpoint))
        {
            setCheckpoint();
        }
    }

    final public String getWatchdogFile()
    {
        return file_name;
    }

    final public void setWatchdogFile(String file)
    {
        file_name = file;
    }

}
