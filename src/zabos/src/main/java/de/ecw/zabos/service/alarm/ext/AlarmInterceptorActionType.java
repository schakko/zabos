package de.ecw.zabos.service.alarm.ext;

/**
 * Aktionen, die beim Vearbeiten eines Alarms auftreten können
 * 
 * @author ckl
 * 
 */
public enum AlarmInterceptorActionType
{
    BEFORE_ALARM_AUSLOESEN, AFTER_ALARM_AUSLOESEN, BEFORE_ALARM_ENTWARNEN, AFTER_ALARM_ENTWARNEN, BEFORE_ALARM_DEAKTIVIEREN, AFTER_ALARM_DEAKTIVIEREN
}
