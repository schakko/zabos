package de.ecw.zabos.service.alarm.ext.interceptors;

import de.ecw.zabos.service.alarm.ext.AlarmInterceptorActionType;
import de.ecw.zabos.service.alarm.ext.IAlarmInterceptor;
import de.ecw.zabos.sql.vo.AlarmVO;

/**
 * Interceptor, der zum Ausführen von Shell-Kommandos benutzt werden kann
 * 
 * @author ckl
 * 
 */
public class ShellExecutionInterceptor extends
                de.ecw.interceptors.ShellExecutionInterceptor implements
                IAlarmInterceptor
{
    /**
     * Generischer Handler zum Ausführen von Shell-Kommando
     * 
     * @see de.ecw.zabos.service.alarm.ext.IAlarmInterceptor#intercept(de.ecw.zabos.service.alarm.ext.AlarmInterceptorActionType,
     *      de.ecw.zabos.sql.vo.AlarmVO)
     */
    public void intercept(AlarmInterceptorActionType type, AlarmVO alarmVO)
    {
        intercept(alarmVO);
    }
}
