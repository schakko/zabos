package de.ecw.zabos.sql.vo;

import java.util.Map;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.sql.vo.properties.IPropertyName;
import de.ecw.zabos.types.Pin;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.BereichId;
import de.ecw.zabos.types.id.FunktionstraegerId;
import de.ecw.zabos.types.id.OrganisationsEinheitId;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.util.StringUtils;

/**
 * ValueObject für {@link Scheme#PERSON_TABLE}
 * 
 * @author bsp
 * 
 */
public class PersonVO extends DeletableBaseIdDescVO implements IPropertyName
{
    private PersonId id;

    private String benutzername;

    private String vorname;

    private String nachname;

    private String email;

    private Pin pin;

    private String passwd;

    private UnixTime abwesendBis;

    private UnixTime abwesendVon;

    private boolean flashSms;

    private OrganisationsEinheitId oeKostenstelleId;

    private FunktionstraegerId funktionstraegerId;

    private BereichId bereichId;

    private Map<String, String> reportOptionen;

    private boolean isInFolgeschleife;

    private PersonId erstelltVon;

    public BaseId getBaseId()
    {
        return id;
    }

    public PersonId getPersonId()
    {
        return id;
    }

    public void setPersonId(PersonId _id) throws StdException
    {
        if (_id == null)
        {
            throw new StdException("primary key id darf nicht null sein");
        }
        id = _id;
    }

    public String getName()
    {
        return benutzername;
    }

    public void setName(String _benutzername) throws StdException
    {
        if (StringUtils.isNullOrEmpty(_benutzername))
        {
            throw new StdException("name darf nicht null oder leer sein");
        }

        benutzername = _benutzername;
    }

    public String getVorname()
    {
        return vorname;
    }

    public void setVorname(String _vorname) throws StdException
    {
        if (StringUtils.isNullOrEmpty(_vorname))
        {
            throw new StdException("vorname darf nicht null oder leer sein");
        }
        vorname = _vorname;
    }

    public String getNachname()
    {
        return nachname;
    }

    public void setNachname(String _nachname) throws StdException
    {
        if (StringUtils.isNullOrEmpty(_nachname))
        {
            throw new StdException("nachname darf nicht null sein");
        }
        nachname = _nachname;
    }

    public void setEmail(String _email)
    {
        email = _email;
    }

    public String getEmail()
    {
        return email;
    }

    public Pin getPin()
    {
        return pin;
    }

    public void setPin(Pin _pin)
    {
        pin = _pin;
    }

    public String getPassword()
    {
        return passwd;
    }

    public void setPassword(String _passwd)
    {
        passwd = _passwd;
    }

    public UnixTime getAbwesendBis()
    {
        return abwesendBis;
    }

    public void setAbwesendBis(UnixTime _unixTime)
    {
        abwesendBis = _unixTime;
    }

    public UnixTime getAbwesendVon()
    {
        return abwesendVon;
    }

    public void setAbwesendVon(UnixTime _unixTime)
    {
        abwesendVon = _unixTime;
    }

    /**
     * @deprecated
     * @return
     */
    public boolean getFlashSms()
    {
        return flashSms;
    }

    /**
     * Liefert den anzuzeigenden Namen einer Person zurück in der Form Nachname,
     * Vorname (Benutzername)
     * 
     * @return
     */
    public String getDisplayName()
    {
        String retVorname = "???";
        String retNachname = "???";
        String retUsername = "???";

        if (!StringUtils.isNullOrEmpty(this.benutzername))
        {
            retUsername = this.benutzername;
        }

        if (!StringUtils.isNullOrEmpty(this.vorname))
        {
            retVorname = this.vorname;
        }

        if (!StringUtils.isNullOrEmpty(this.nachname))
        {
            retNachname = this.nachname;
        }

        return retNachname + ", " + retVorname + " (" + retUsername + ")";
    }

    /**
     * Prueft ob die Person zu dem ggb. Zeitpunkt anwesend ist.
     * 
     * Hinweis: Eigentlich haben "Business-Methoden" in VOs nichts zu suchen
     * aber da wir keine BO-Schicht haben und diese Methode keine DB-Anfragen
     * macht geht das ausnahmsweise klar :P
     * 
     * 2006-07-12 CKL: Abwesend Von / Bis 2006-10-20 CKL: UnixTime.now() durch
     * _ut-Parameter ersetzt 2006-10-20 CKL: Rückgabewert wird invertiert,
     * ansonsten ist der Benutzer immer anwesend, Issue #330
     * 
     * @param _ut
     * @return
     */
    public boolean isAnwesend(UnixTime _ut)
    {
        if (abwesendBis != null && abwesendVon != null)
        {
            // 2006-09-28 CKL: Fix fuer Ueberpruefung auf 0
            if (abwesendBis.getTimeStamp() == 0
                            && abwesendVon.getTimeStamp() == 0)
            {
                return true;
            }

            // 2006-07-12 CKL: Es wird nun nach abwesend_von / abwesend_bis
            // überprüft
            // 2006-10-20 CKL: Rückgabewert wird invertiert, ansonsten ist der
            // Benutzer immer anwesend
            return !(_ut.isBetween(abwesendVon, abwesendBis));
        }
        else
        {
            return true;
        }
    }

    public OrganisationsEinheitId getOEKostenstelle()
    {
        return oeKostenstelleId;
    }

    public void setOEKostenstelle(OrganisationsEinheitId _oeId)
    {
        oeKostenstelleId = _oeId;
    }

    /**
     * Sortiert ein Array von Personen nach "Nachname Vorname"
     * 
     * @param _personen
     */
    public static void sortPersonenByNachnameVorname(PersonVO[] _personen)
    {
        if (_personen.length > 1)
        {
            boolean bSwapped;
            do
            {
                bSwapped = false;
                PersonVO l = _personen[0];
                // String lname = l.getNachname()+ " " + l.getVorname();
                String lname = (l.getNachname().toLowerCase() + " " + l
                                .getVorname().toLowerCase()).replace('ä', 'a')
                                .replace('ö', 'o').replace('ü', 'u')
                                .replace('ß', 's');

                for (int i = 1; i < _personen.length; i++)
                {
                    PersonVO t = _personen[i];
                    // String rname = t.getNachname()+ " " + t.getVorname();
                    String rname = (t.getNachname().toLowerCase() + " " + t
                                    .getVorname().toLowerCase())
                                    .replace('ä', 'a').replace('ö', 'o')
                                    .replace('ü', 'u').replace('ß', 's');
                    if (lname.compareTo(rname) > 0)
                    {
                        _personen[i] = l;
                        _personen[i - 1] = t;
                        bSwapped = true;
                    }
                    else
                    {
                        l = t;
                        lname = rname;
                    }
                }
            }
            while (bSwapped);
        }

    }

    /**
     * ID des Funktionsträgers
     * 
     * @since 2007-06-07
     * @return Returns the funktionstraeger_id.
     */
    public FunktionstraegerId getFunktionstraegerId()
    {
        return funktionstraegerId;
    }

    /**
     * ID des Funktionsträgers
     * 
     * @since 2007-06-07
     * @param funktionstraegerId
     *            The funktionstraeger_id to set.
     */
    public void setFunktionstraegerId(FunktionstraegerId _funktionstraeger_id)
    {
        funktionstraegerId = _funktionstraeger_id;
    }

    /**
     * ID des Bereichs
     * 
     * @since 1.2.0
     * @param _bereich_id
     */
    public void setBereichId(BereichId _bereich_id)
    {
        this.bereichId = _bereich_id;
    }

    /**
     * ID des Bereichs
     * 
     * @since 1.2.0
     * @return
     */
    public BereichId getBereichId()
    {
        return bereichId;
    }

    public void setReportOptionen(Map<String, String> reportOptionen)
    {
        this.reportOptionen = reportOptionen;
    }

    public Map<String, String> getReportOptionen()
    {
        return reportOptionen;
    }

    public String toString()
    {
        return getNachname() + ", " + getVorname() + " (" + getName() + ")";
    }

    public void setInFolgeschleife(boolean isInFolgeschleife)
    {
        this.isInFolgeschleife = isInFolgeschleife;
    }

    public boolean isInFolgeschleife()
    {
        return isInFolgeschleife;
    }

    public void setErstelltVon(PersonId erstelltVon)
    {
        this.erstelltVon = erstelltVon;
    }

    public PersonId getErstelltVon()
    {
        return erstelltVon;
    }

    public PersonVO()
    {

    }
}
