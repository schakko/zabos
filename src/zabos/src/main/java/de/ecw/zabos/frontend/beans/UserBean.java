package de.ecw.zabos.frontend.beans;

import java.util.HashMap;

import de.ecw.zabos.frontend.types.KontextType;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.types.UnixTime;

/**
 * Repräsentiert das Objekt des gerade eingeloggten Users.
 * 
 * @author ckl
 */
public class UserBean
{
    // Benutzer ist eingeloggt
    private boolean isLoggedIn = false;

    // Zeitpunkt des Logins
    private UnixTime loginTimestamp = null;

    // Benutzerdaten
    private PersonVO voPerson = null;

    // Organisation des aktuellen Kontext
    private OrganisationVO kontextOrganisation = null;

    // Organisationseinheit des aktuellen Kontext
    private OrganisationsEinheitVO kontextOrganisationsEinheit = null;

    // Schleife des aktuellen Kontext
    private SchleifeVO kontextSchleife = null;

    // Person des aktuellen Kontext
    private PersonVO kontextPerson = null;

    // Verfuegbare Os im aktuellen Kontext
    private DataBean kontextVerfuegbareOrganisationen = null;

    // Verfuegbare OEs im aktuellen Kontext
    private DataBean kontextVerfuegbareOrganisationsEinheiten = null;

    // Verfuegbare Schleifen im aktuellen Kontext
    private DataBean kontextVerfuegbareSchleifen = null;

    // Verfuegbare Personen im aktuellen Kontext
    private DataBean kontextVerfuegbarePersonen = null;

    // Rechte Allgemein
    private ACLBean beanACL = new ACLBean();

    // Kontext-Type
    private KontextType aktuellerKontext = new KontextType(KontextType.SYSTEM);

    // Optionen
    private HashMap<String, String> optionen = new HashMap<String, String>();
    
    /**
     * User ist eingeloggt?
     * 
     * @return true|false
     */
    public boolean isLoggedIn()
    {
        return isLoggedIn;
    }

    /**
     * Setzt, ob der User eingeloggt ist
     * 
     * @param _isLoggedIn
     */
    public void isLoggedIn(boolean _isLoggedIn)
    {
        this.isLoggedIn = _isLoggedIn;

        if (true == _isLoggedIn)
        {
            setLoginTimestamp(UnixTime.now());
        }
    }

    /**
     * Setzt das PersonVO des ggw. Users
     * 
     * @param _person
     */
    public void setPerson(PersonVO _person)
    {
        this.voPerson = _person;
    }

    /**
     * Liefert das Personen-Objekt des ggw. Users
     * 
     * @return Person
     */
    public PersonVO getPerson()
    {
        return this.voPerson;
    }

    /**
     * Liefert den Timestamp des Logins
     * 
     * @return Timestamp
     */
    public long getLoginTimestamp()
    {
        return this.loginTimestamp.getTimeStamp();
    }

    /**
     * Setzt den Login-Zeitstempel
     * 
     * @param _loginTimestamp
     */
    protected void setLoginTimestamp(UnixTime _loginTimestamp)
    {
        this.loginTimestamp = _loginTimestamp;
    }

    /**
     * Liefert die Organisation im Kontext
     * 
     * @return OrganisationVO
     */
    public OrganisationVO getCtxO()
    {
        return kontextOrganisation;
    }

    /**
     * Setzt die Organisation im ggw. Kontext Die OE und ggw. Schleife im
     * Kontext wird auf null gesetzt
     * 
     * @param _ctxO
     *            Organisation
     */
    public void setCtxO(OrganisationVO _ctxO)
    {
        /*
         * Entweder ist das uebergebene Objekt NULL (es soll ein neues Objekt
         * erzeugt werden). Oder aber die �bergebene ID stimmt nicht mit der
         * ggw. ID ueberein
         */
        // Objekt null => Neue Organisation, Kontext: System
        if (_ctxO == null)
        {
            setCtxOE(null);
            setKontextType(new KontextType(KontextType.SYSTEM));
        }
        else if (this.kontextOrganisation != null)
        { // Objekt != null => Kontext ist Organisatin
            if (this.kontextOrganisation.getBaseId().equals(_ctxO.getBaseId()) == false)
            {
                setCtxOE(null);
            }

            setKontextType(new KontextType(KontextType.ORGANISATION));
        }

        this.kontextOrganisation = _ctxO;
    }

    /**
     * Liefert die Organisationseinheit im Kontext
     * 
     * @return OrganisationsEinheitVO
     */
    public OrganisationsEinheitVO getCtxOE()
    {
        return kontextOrganisationsEinheit;
    }

    /**
     * Setzt die Organisationseinheit im ggw. Kontext Die Schleife im ggw.
     * Kontext wird auf null gesetzt
     * 
     * @param _ctxOE
     *            Organisationseinheit
     */
    public void setCtxOE(OrganisationsEinheitVO _ctxOE)
    {

        /*
         * Entweder ist das uebergebene Objekt NULL (es soll ein neues Objekt
         * erzeugt werden. Oder aber die uebergebene ID stimmt nicht mit der
         * ggw. ID ueberein
         */
        if (_ctxOE == null)
        {
            setCtxSchleife(null);
            setKontextType(new KontextType(KontextType.ORGANISATION));
        }
        else if (this.kontextOrganisationsEinheit != null)
        {
            if (this.kontextOrganisationsEinheit.getBaseId().equals(
                            _ctxOE.getBaseId()) == false)
            {
                setCtxSchleife(null);
            }
            this.setKontextType(new KontextType(
                            KontextType.ORGANISATIONSEINHEIT));
        }

        this.kontextOrganisationsEinheit = _ctxOE;
    }

    /**
     * Liefert die Schleife im ggw. Kontext zur�ck
     * 
     * @return SchleifeVO
     */
    public SchleifeVO getCtxSchleife()
    {
        return kontextSchleife;
    }

    /**
     * Setzt die Schleife im ggw. Kontext
     * 
     * @param _ctxSchleife
     */
    public void setCtxSchleife(SchleifeVO _ctxSchleife)
    {
        if (_ctxSchleife == null)
        {
            setKontextType(new KontextType(KontextType.ORGANISATIONSEINHEIT));
        }
        else
        {
            setKontextType(new KontextType(KontextType.SCHLEIFE));
        }

        this.kontextSchleife = _ctxSchleife;
    }

    /**
     * Liefert die Person im ggw. Kontext
     * 
     * @return PersonVO
     */
    public PersonVO getCtxPerson()
    {
        return kontextPerson;
    }

    /**
     * Setzt die Person im ggw. Kontext
     * 
     * @param _ctxPerson
     */
    public void setCtxPerson(PersonVO _ctxPerson)
    {
        this.kontextPerson = _ctxPerson;
    }

    /**
     * Liefert die verfuegbaren Oragnisationen im aktuellen Kontext. Liefert
     * i.a.R. immer ALLE Organisationen.
     * 
     * @return DataBean
     */
    public DataBean getCtxOrganisationenAvailable()
    {
        return kontextVerfuegbareOrganisationen;
    }

    /**
     * Setzt die verfügbaren Organisationen im aktuellen Kontext.
     * 
     * @param _organisationen
     */
    public void setCtxOrganisationenAvailable(DataBean _organisationen)
    {
        this.kontextVerfuegbareOrganisationen = _organisationen;

        if (_organisationen == null)
        {
            setCtxOrganisationseinheitenAvailable(null);
        }
    }

    /**
     * Liefert die verfügbaren Organisationseinheiten im aktuellen Kontext
     * 
     * @return DataBean
     */
    public DataBean getCtxOrganisationseinheitenAvailable()
    {
        return kontextVerfuegbareOrganisationsEinheiten;
    }

    /**
     * Setzt die verfügbaren Organisationseinheiten für den aktuellen Kontext.
     * 
     * @param _organisationseinheiten
     */
    public void setCtxOrganisationseinheitenAvailable(
                    DataBean _organisationseinheiten)
    {
        this.kontextVerfuegbareOrganisationsEinheiten = _organisationseinheiten;

        if (_organisationseinheiten == null)
        {
            setCtxSchleifenAvailable(null);
        }
    }

    /**
     * Liefert die verfügbaren Personen des aktuellen Kontext
     * 
     * @return DataBean
     */
    public DataBean getCtxPersonenAvailable()
    {
        return kontextVerfuegbarePersonen;
    }

    /**
     * Setzt die verfügbaren Personen im aktuellen Kontext
     * 
     * @param _personen
     */
    public void setCtxPersonenAvailable(DataBean _personen)
    {
        this.kontextVerfuegbarePersonen = _personen;
    }

    /**
     * Liefert die verfügbaren Schleifen im aktuellen Kontext
     * 
     * @return DataBean
     */
    public DataBean getCtxSchleifenAvailable()
    {
        return kontextVerfuegbareSchleifen;
    }

    /**
     * Setzt die verfügbaren Schleifen im aktuellen Kontext
     * 
     * @param _schleifen
     */
    public void setCtxSchleifenAvailable(DataBean _schleifen)
    {
        this.kontextVerfuegbareSchleifen = _schleifen;

        if (_schleifen == null)
        {
            setCtxPersonenAvailable(null);
        }
    }

    /**
     * Liefert die ACL
     * 
     * @return ACLBean
     */
    public ACLBean getAccessControlList()
    {
        return this.beanACL;
    }

    /**
     * Liefert den Kontext-Typen
     * 
     * @return
     */
    public KontextType getKontextType()
    {
        return aktuellerKontext;
    }

    /**
     * Setzt den Kontext-Typen
     * 
     * @param kontextType
     */
    public void setKontextType(KontextType _kontextType)
    {
        this.aktuellerKontext = _kontextType;
    }

    public void setOptionen(HashMap<String, String> optionen)
    {
        this.optionen = optionen;
    }

    public HashMap<String, String> getOptionen()
    {
        return optionen;
    }
}
