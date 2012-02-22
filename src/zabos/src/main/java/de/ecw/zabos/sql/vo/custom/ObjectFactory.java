package de.ecw.zabos.sql.vo.custom;

import de.ecw.zabos.sql.vo.PersonVO;

public class ObjectFactory extends de.ecw.zabos.sql.vo.ObjectFactory
{
    public PersonVO createPerson()
    {
        return new PersonWithEmailToStringVO();
    }
}
