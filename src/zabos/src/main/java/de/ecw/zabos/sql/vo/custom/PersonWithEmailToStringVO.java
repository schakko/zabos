package de.ecw.zabos.sql.vo.custom;

import de.ecw.zabos.sql.vo.PersonVO;

public class PersonWithEmailToStringVO extends PersonVO
{
    public String toString()
    {
        if (getEmail() != null && getEmail().length() > 0)
        {
            return getEmail();
        }

        return super.toString();
    }

    public String getDisplayName()
    {
        return toString();
    }
}
