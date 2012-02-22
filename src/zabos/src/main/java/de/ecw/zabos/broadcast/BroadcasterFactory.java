package de.ecw.zabos.broadcast;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Factory-Bean zum Erstellen der neuen Broadcaster
 * 
 * @author ckl
 */
public class BroadcasterFactory implements ApplicationContextAware
{
    private ApplicationContext applicationContext;

    private String referenceName = null;

    public BroadcasterFactory(String referenceName)
    {
        this.referenceName = referenceName;
    }

    public void setApplicationContext(ApplicationContext arg0) throws BeansException
    {
        applicationContext = arg0;
    }

    public IBroadcaster create()
    {
        return (IBroadcaster) applicationContext.getBean(referenceName);
    }
}
