package de.ecw.zabos.types.id;

/**
 * Immutable Id
 * 
 * @author bsp
 * 
 */
public class BaseId
{

    private long value;

    public BaseId(long _value)
    {
        value = _value;
    }

    public long getLongValue()
    {
        return value;
    }

    public String toString()
    {
        return "" + value;
    }

    public String getString()
    {
        return toString();
    }

    public boolean equals(Object _o)
    {
        if (_o instanceof BaseId)
        {
            return (((BaseId) _o).value == value);
        }
        return false;
    }

    public int hashCode()
    {
        return (int) ((value & 0xFFFFFFFF) ^ (value >> 32)) & 0xFFFFFFFF;
    }

}
