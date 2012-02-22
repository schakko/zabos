package de.ecw.zabos.service.alarm.ext;

import de.ecw.zabos.sql.vo.AlarmVO;

/**
 * Interface zum Erweitern der Funktionalität beim Verarbeiten von Alarmen
 * 
 * @author ckl
 * 
 */
public interface IAlarmInterceptor
{
    /**
     * Wird aufgerufen, sobald ein Interceptor ausgeführt werden soll
     * 
     * @param _type
     * @param _alarmVO
     */
    public void intercept(AlarmInterceptorActionType _type, AlarmVO _alarmVO);
}
