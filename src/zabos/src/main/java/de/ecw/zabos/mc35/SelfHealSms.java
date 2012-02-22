package de.ecw.zabos.mc35;

import de.ecw.interceptors.IInterceptor;

/**
 * Sendet eine SMS an sich selbst
 * 
 * @author ckl
 * 
 */
public class SelfHealSms implements IInterceptor
{
    public void intercept(Object o)
    {
        if (!(o instanceof MC35))
        {
            return;
        }

        MC35 mc35 = (MC35) o;

        ShortMessage shortMessage = new ShortMessage(mc35
                        .getAssignedPhoneNumber().toString(), "I am online");
        mc35.scheduleOutgoingSms(shortMessage);
    }
}
