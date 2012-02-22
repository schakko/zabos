package de.ecw.zabos.sql.vo;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.sql.vo.properties.IPropertyName;
import de.ecw.zabos.types.id.AlarmQuelleId;
import de.ecw.zabos.types.id.BaseId;

/**
 * ValueObject für {@link Scheme#ALARM_QUELLE_TABLE}
 * 
 * @author bsp
 * 
 */
public class AlarmQuelleVO implements BaseIdVO, IPropertyName
{
    AlarmQuelleVO() {
        
    }
    
    private AlarmQuelleId id;

    private String name;

    public BaseId getBaseId()
    {
        return id;
    }

    public AlarmQuelleId getAlarmQuelleId()
    {
        return id;
    }

    public void setAlarmQuelleId(AlarmQuelleId _alarmQuelleId) throws StdException
    {
        if (_alarmQuelleId == null)
        {
            throw new StdException("primary key cannot be null");
        }
        id = _alarmQuelleId;
    }

    /**
     * Unique
     * 
     * @return
     */
    public String getName()
    {
        return name;
    }

    public void setName(String _name) throws StdException
    {
        if (_name == null)
        {
            throw new StdException("alarm quelle name cannot be null");
        }
        name = _name;
    }

}
