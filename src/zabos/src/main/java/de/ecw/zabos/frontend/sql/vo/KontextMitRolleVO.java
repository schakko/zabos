package de.ecw.zabos.frontend.sql.vo;

import de.ecw.zabos.frontend.types.KontextType;

/**
 * Stellt ein Objekt aus dem Kontext dar
 * 
 * @author ckl
 */
public class KontextMitRolleVO
{
    private String kontextName = "";

    private String rollenName = "";

    private KontextType kontextType = null;

    private long idRolle = 0;

    private long idKontext = 0;

    /**
     * Konstruktor
     */
    public KontextMitRolleVO()
    {
    }

    /**
     * Liefert die Id des Kontext
     * 
     * @return
     */
    public long getKontextId()
    {
        return idKontext;
    }

    /**
     * Setzt die Id des Kontext
     * 
     * @param id
     */
    public void setKontextId(long _idKontext)
    {
        this.idKontext = _idKontext;
    }

    /**
     * Liefert den Kontext
     * 
     * @return
     */
    public KontextType getKontextType()
    {
        return this.kontextType;
    }

    /**
     * Setzt den Kontext
     * 
     * @param kontext
     */
    public void setKontextType(KontextType _kontextType)
    {
        this.kontextType = _kontextType;
    }

    /**
     * Liefert den Namen des Kontext
     * 
     * @return
     */
    public String getKontextName()
    {
        return kontextName;
    }

    /**
     * Setzt den Namen des Kontext
     * 
     * @param name
     */
    public void setName(String _kontextName)
    {
        this.kontextName = _kontextName;
    }

    /**
     * Liefert die Id der Rolle
     * 
     * @return
     */
    public long getRolleId()
    {
        return idRolle;
    }

    /**
     * Setzt die Id der Rolle
     * 
     * @param idRolle
     */
    public void setRolleId(long _rolleId)
    {
        this.idRolle = _rolleId;
    }

    /**
     * Liefert den Namen der Rolle
     * 
     * @return
     */
    public String getRollenName()
    {
        return rollenName;
    }

    /**
     * Setzt den Namen der Rolle
     * 
     * @param rollenName
     */
    public void setRollenName(String _rollenName)
    {
        this.rollenName = _rollenName;
    }

}
