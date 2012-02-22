package de.ecw.zabos.broadcast;

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class BroadcasterAdapter implements ApplicationContextAware,
                IBroadcaster
{

    private IFinishBroadcastingListener finishBroadcastingListener = null;

    /**
     * Empfängerliste
     */
    private List<Recipient> recipients;

    public void run()
    {
        // TODO Auto-generated method stub

    }

    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext arg0) throws BeansException
    {
        applicationContext = arg0;
    }

    public ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.sms.gateway.broadcast.IBroadcaster#setRecipients(java.util
     * .List)
     */
    final public void setRecipients(List<Recipient> recipients)
    {
        this.recipients = recipients;
    }

    public List<Recipient> getRecipients()
    {
        return recipients;
    }

    public void setFinishBroadcastingListener(
                    IFinishBroadcastingListener listener)
    {
        finishBroadcastingListener = listener;
    }

    public IFinishBroadcastingListener getFinishBroadcastingListener()
    {
        return finishBroadcastingListener;
    }
}
