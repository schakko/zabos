package de.ecw.zabos.sql.cvo;

import java.util.ArrayList;
import java.util.List;

import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.RolleVO;

/**
 * Stellt eine Person mit einer Rolle dar
 * 
 * @author ckl
 */
public class PersonMitRollenCVO
{
    PersonVO voPerson = null;

    // Rollen der Person
    List<RolleVO> listRollen = new ArrayList<RolleVO>();

    /**
     * Setzt die Person
     * 
     * @param _person
     */
    public void setPerson(PersonVO _person)
    {
        voPerson = _person;
    }

    /**
     * Liefert die Person
     * 
     * @return PersonVO
     */
    public PersonVO getPerson()
    {
        return voPerson;
    }

    /**
     * Setzt eine rolle
     * 
     * @param _rolle
     */
    public void setRolle(RolleVO _rolle)
    {
        listRollen.add(_rolle);
    }

    /**
     * Liefert die Rollen der Person
     * 
     * @return List
     */
    public List<RolleVO> getRollen()
    {
        return listRollen;
    }

    /**
     * Liefert die Anzahl der Rollen der Person
     * 
     * @retur
     */
    public int getSizeOfRollen()
    {
        return listRollen.size();
    }
}
