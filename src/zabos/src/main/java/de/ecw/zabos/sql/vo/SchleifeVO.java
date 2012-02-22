package de.ecw.zabos.sql.vo;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.sql.vo.properties.IPropertyKuerzel;
import de.ecw.zabos.sql.vo.properties.IPropertyName;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.OrganisationsEinheitId;
import de.ecw.zabos.types.id.SchleifeId;
import de.ecw.zabos.util.StringUtils;

/**
 * ValueObject für {@link Scheme#SCHLEIFE_TABLE}
 * 
 * @author bsp
 * 
 */
public class SchleifeVO extends DeletableBaseIdDescVO implements IPropertyName,
                IPropertyKuerzel
{
    SchleifeVO()
    {

    }

    private SchleifeId id;

    private String name;

    private String kuerzel;

    private String fuenfton;

    private OrganisationsEinheitId organisationseinheitId;

    // 2006-06-09 CKL: Statusreport-Fuenfton
    private boolean statusreportFuenfton;

    // 2007-06-07 CKL: Ist die Schleife abrechenbar
    private boolean istAbrechenbar;

    // 2010-01-01 CKL: Folgeschleife für das Klinikum
    private SchleifeId folgeschleifeId;

    // 2010-01-01 CKL: Rückmeldeintervall für das Klinikum
    private long rueckmeldeintervall;

    // 2010-01-01 CKL: Druckerkürzel für das Klinikum
    private String druckerKuerzel;

    public BaseId getBaseId()
    {
        return id;
    }

    public SchleifeId getSchleifeId()
    {
        return id;
    }

    public void setSchleifeId(SchleifeId _id) throws StdException
    {
        if (_id == null)
        {
            throw new StdException("primary key darf nicht null");
        }
        id = _id;
    }

    public String getName()
    {
        return name;
    }

    public String getDisplayName()
    {
        return name + " (" + kuerzel + ")";
    }

    public void setName(String _name) throws StdException
    {
        if (StringUtils.isNullOrEmpty(_name))
        {
            throw new StdException("name darf nicht null oder leer sein");
        }
        name = _name;
    }

    public String getKuerzel()
    {
        return kuerzel;
    }

    public void setKuerzel(String _kuerzel) throws StdException
    {
        if (StringUtils.isNullOrEmpty(_kuerzel))
        {
            throw new StdException("kuerzel darf nicht null oder leer sein");
        }
        kuerzel = _kuerzel;
    }

    public String getFuenfton()
    {
        return fuenfton;
    }

    public void setFuenfton(String _fuenfton)
    {
        fuenfton = _fuenfton;
    }

    public OrganisationsEinheitId getOrganisationsEinheitId()
    {
        return organisationseinheitId;
    }

    public void setOrganisationsEinheitId(OrganisationsEinheitId _id) throws StdException
    {
        if (_id == null)
        {
            throw new StdException(
                            "organisationseinheit_id darf nicht null sein");
        }
        organisationseinheitId = _id;
    }

    /**
     * Statusreport?
     * 
     * @return
     * @author ckl
     * @since 2006-06-09
     */
    public boolean getStatusreportFuenfton()
    {
        return statusreportFuenfton;
    }

    /**
     * Setzt den Statusreport
     * 
     * @return
     * @author ckl
     * @since 2006-06-09
     */
    public void setStatusreportFuenfton(boolean _statusreport_fuenfton)
    {
        statusreportFuenfton = _statusreport_fuenfton;
    }

    /**
     * Ist die Schleife abrechenbar im System?
     * 
     * @return
     * @author ckl
     * @since 2007-06-07
     */
    public boolean getAbrechenbar()
    {
        return istAbrechenbar;
    }

    /**
     * Ist die Schleife abrechenbar im System?
     * 
     * @return
     * @author ckl
     * @since 2007-06-07
     */
    public void setAbrechenbar(boolean _ist_abrechenbar)
    {
        istAbrechenbar = _ist_abrechenbar;
    }

    public void setFolgeschleifeId(SchleifeId folgeschleife_id)
    {
        this.folgeschleifeId = folgeschleife_id;
    }

    public SchleifeId getFolgeschleifeId()
    {
        return folgeschleifeId;
    }

    public void setRueckmeldeintervall(long rueckmeldeintervall)
    {
        this.rueckmeldeintervall = rueckmeldeintervall;
    }

    public long getRueckmeldeintervall()
    {
        return rueckmeldeintervall;
    }

    public void setDruckerKuerzel(String druckerKuerzel)
    {
        this.druckerKuerzel = druckerKuerzel;
    }

    public String getDruckerKuerzel()
    {
        return druckerKuerzel;
    }

    public String toString()
    {
        return name + " (" + kuerzel + ")";
    }
}
