package de.ecw.interceptors;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * Delegiert je nach Aktion an den passenden Interceptor
 * 
 * @author ckl
 */
public class InterceptorDelegator implements IInterceptor
{
    private final static Logger log = Logger
                    .getLogger(InterceptorDelegator.class);

    private List<IInterceptor> listInterceptors;

    /**
     * Setzt einen Interceptor
     * 
     * @param _type
     * @param _handler
     */
    public void setInterceptors(List<IInterceptor> _listInterceptors)
    {
        listInterceptors = _listInterceptors;
    }

    /**
     * Führt einen Interceptor für eine gegebene Aktion aus
     */
    public void intercept(Object _o)
    {
        log.debug("InterceptorDelegator aufgerufen");

        if (listInterceptors == null)
        {
            log.warn("Es sind keine Interceptoren definiert.");
            return;

        }

        for (int i = 0, m = listInterceptors.size(); i < m; i++)
        {
            listInterceptors.get(i).intercept(_o);
        }
    }
}
