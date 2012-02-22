package de.ecw.zabos.bo;

import de.ecw.zabos.sql.resource.DBResource;

/**
 * Factory für die BOs
 * 
 * @author ckl
 * 
 */
public class BOFactory
{
    private DBResource dbResource;

    private PersonenMitRollenBO boPersonenMitRollen;

    private KontextMitRolleBO boKontextMitRolle;

    private BaumViewBO boBaumView;

    private SchleifeBO boSchleife;

    public BOFactory(DBResource _dbResource)
    {
        dbResource = _dbResource;
    }

    public DBResource getDBResource()
    {
        return dbResource;
    }

    /**
     * @return
     */
    public PersonenMitRollenBO getPersonenMitRollenBO()
    {
        if (boPersonenMitRollen == null)
        {
            boPersonenMitRollen = new PersonenMitRollenBO(getDBResource());
        }

        return boPersonenMitRollen;
    }

    /**
     * @return
     */
    public KontextMitRolleBO getKontextMitRolleBO()
    {
        if (boKontextMitRolle == null)
        {
            boKontextMitRolle = new KontextMitRolleBO(getDBResource());
        }

        return boKontextMitRolle;
    }

    /**
     * @return
     */
    public BaumViewBO getBaumViewBO()
    {
        if (boBaumView == null)
        {
            boBaumView = new BaumViewBO(getDBResource());
        }

        return boBaumView;
    }

    /**
     * @return
     */
    public SchleifeBO getSchleifeBO()
    {
        if (boSchleife == null)
        {
            boSchleife = new SchleifeBO(getDBResource());
        }

        return boSchleife;
    }
}
