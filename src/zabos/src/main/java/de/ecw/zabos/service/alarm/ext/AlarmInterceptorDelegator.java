package de.ecw.zabos.service.alarm.ext;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import de.ecw.zabos.sql.vo.AlarmVO;

/**
 * Delegiert je nach Aktion an den passenden Interceptor
 * 
 * @author ckl
 */
public class AlarmInterceptorDelegator implements IAlarmInterceptor
{
    private final static Logger log = Logger
                    .getLogger(AlarmInterceptorDelegator.class);

    private Map<AlarmInterceptorActionType, List<IAlarmInterceptor>> mapInterceptors;

    /**
     * Setzt einen Interceptor
     * 
     * @param _type
     * @param _handler
     */
    public void setInterceptors(
                    Map<AlarmInterceptorActionType, List<IAlarmInterceptor>> _hmInterceptors)
    {
        mapInterceptors = _hmInterceptors;
    }

    /**
     * Führt einen Interceptor für eine gegebene Aktion aus
     */
    public void intercept(AlarmInterceptorActionType _type, AlarmVO alarmVO)
    {
        log.debug("InterceptorDelegator aufgerufen fuer ActionType \""
                        + _type.toString() + "\"");

        if (mapInterceptors != null && mapInterceptors.containsKey(_type))
        {
            List<IAlarmInterceptor> listInterceptoren = mapInterceptors
                            .get(_type);

            log.info("Fuer den ActionType \"" + _type.toString() + "\" sind \""
                            + listInterceptoren.size()
                            + "\" Interceptoren definiert");

            for (int i = 0, m = listInterceptoren.size(); i < m; i++)
            {
                listInterceptoren.get(i).intercept(_type, alarmVO);
            }
        }
        else
        {
            log.info("InterceptorDelegator existiert *nicht*");
        }
    }
}
