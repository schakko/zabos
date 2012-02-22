package de.ecw.zabos.frontend.sql.ho;

import org.apache.log4j.Logger;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.license.License;
import de.ecw.zabos.sql.dao.PersonDAO;
import de.ecw.zabos.sql.dao.TelefonDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.tao.BenutzerVerwaltungTAO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.TelefonVO;
import de.ecw.zabos.types.TelefonNummer;

public class ImportUserHO
{
    private int maxLengthUsername = 10;

    private String csvList = "";

    private String[] csvLines;

    private int totalEntries = 0;

    private int totalUsersImported = 0;

    private int totalTelefonsImported = 0;

    private PersonDAO daoPerson = null;

    private TelefonDAO daoTelefon = null;

    private DBResource db = null;

    private BenutzerVerwaltungTAO taoBV = null;

    private final static Logger log = Logger.getLogger(ImportUserHO.class);

    private License license;

    public ImportUserHO(DBResource _db, License _license)
    {
        this.db = _db;
        this.daoPerson = db.getDaoFactory().getPersonDAO();
        this.taoBV = db.getTaoFactory().getBenutzerVerwaltungTAO();
        this.daoTelefon = db.getDaoFactory().getTelefonDAO();
        license = _license;
    }

    /**
     * Setzt die maximale Laenge eines Benutzernamens
     * 
     * @param _maxLengthUsername
     */
    public void setMaxLengthUsername(int _maxLengthUsername)
    {
        this.maxLengthUsername = _maxLengthUsername;
    }

    /**
     * Liefert die max. Anzahl von Zeichen fuer einen Benutzernamen zurueck
     * 
     * @return
     */
    public int getMaxLengthUsername()
    {
        return this.maxLengthUsername;
    }

    /**
     * Startet den Import-Run
     * 
     * @throws StdException
     */
    public void run() throws StdException
    {
        this.csvLines = this.csvList.split("\n");

        if (this.csvLines.length == 0)
        {
            throw new StdException(
                            "Es wurden keine Zeilen zum Einlesen uebergeben.");
        }

        this.totalEntries = this.csvLines.length;

        for (int i = 0, m = this.csvLines.length; i < m; i++)
        {
            importLine(this.csvLines[i]);
        }
    }

    /**
     * Importiert die CSV-Zeile
     * 
     * @param _line
     */
    private void importLine(String _line) throws StdException
    {
        String[] lineEntries = _line.split(";");

        if (lineEntries.length < 3)
        {
            log.error("Die Zeile "
                            + _line
                            + " konnte nicht importiert werden. Es muessen mindestens drei CSV-Eintraege vorhanden sein (Vorname, Nachname, Telefonnummer)");
            return;
        }

        if (daoPerson.countPersonen() >= license.getPersonen())
        {
            throw new StdException(
                            "Die maximale Anzahl der zu erstellenden Personen in dieser Lizenz ist erreicht.");
        }

        PersonVO voPerson = daoPerson.getObjectFactory().createPerson();
        TelefonNummer telefonNummer = new TelefonNummer(lineEntries[2].trim());

        voPerson.setVorname(lineEntries[0].trim());
        voPerson.setNachname(lineEntries[1].trim());
        PersonVO voTempPerson = null;

        try
        {
            voTempPerson = daoPerson.findPersonByTelefonNummer(telefonNummer);
        }
        catch (StdException e)
        {
            log.error(e);
        }

        if (voTempPerson != null)
        {
            log.debug("Telefonnummer " + telefonNummer.getNummer()
                            + " ist bereits der Person "
                            + voTempPerson.getDisplayName() + " zugewiesen");
            if (voTempPerson.getVorname().equals(voPerson.getVorname())
                            && voTempPerson.getNachname().equals(
                                            voPerson.getNachname()))
            {
                log.error("Person " + voPerson.getDisplayName()
                                + " ist bereits mit der Telefonnummer '"
                                + telefonNummer.getNummer()
                                + "' in der Datenbank eingetragen");
                return;
            }
        }

        if (lineEntries.length == 4)
        {
            voPerson.setName(lineEntries[3].trim());
        }
        else
        {
            voPerson.setName(getSuggestedUsername(voPerson.getVorname(),
                            voPerson.getNachname()));
        }

        log.debug("Benutzer wird mit folgenden Benutzerdaten importiert: "
                        + voPerson.getDisplayName());

        voPerson = taoBV.createPerson(voPerson);

        // Person konnte erstellt werden
        if (voPerson != null)
        {

            log.debug("Person " + voPerson.getDisplayName()
                            + " wurde mit der Id "
                            + voPerson.getPersonId().getLongValue()
                            + " importiert");

            TelefonVO voTelefon = daoTelefon.getObjectFactory().createTelefon();
            voTelefon.setNummer(telefonNummer);
            voTelefon.setPersonId(voPerson.getPersonId());
            voTelefon.setAktiv(true);

            if (daoTelefon.findTelefonByNummer(voTelefon.getNummer()) != null)
            {
                log.error("Telefon mit der Nummer "
                                + telefonNummer.getNummer()
                                + " konnte nicht hinzugefuegt werden. Dieses Telefon existiert bereits");
                return;
            }

            log.debug("Fuege Telefon mit der Nummer '"
                            + telefonNummer.getNummer() + "' Person "
                            + voPerson.getDisplayName() + " hinzu");

            voTelefon = taoBV.createTelefon(voTelefon);

            if (voTelefon != null)
            {
                log.debug("Telefon " + telefonNummer.getNummer()
                                + " hinzugefuegt");
            }
            else
            {
                log.error("Telefon " + telefonNummer.getNummer()
                                + " konnte nicht hinzugefuegt werden");
            }

            totalUsersImported++;
        }
        else
        {
            log.error("Benutzer konnte nicht hinzugefuegt werden");
        }
    }

    /**
     * ueberprueft, ob der Benutzername bereits existiert
     * 
     * @param _username
     * @return
     * @throws StdException
     */
    protected boolean isUsernameExistent(String _username) throws StdException
    {
        try
        {
            if (daoPerson.findPersonByName(_username) == null)
            {
                return false;
            }
        }
        catch (StdException e)
        {
            log.error(e.getMessage());
        }

        return true;
    }

    /**
     * schlaegt einen Benutzernamen fuer die Kombination Vorname / Nachname vor
     * 
     * @param _forename
     * @param _surname
     * @return
     */
    protected String getSuggestedUsername(String _forename, String _surname) throws StdException
    {
        String usernamePrefix = "";
        String usernamePostfix = "";
        String rUsername = "";

        // Benutze ersten Buchstbaen vom Vornamen und ersten beiden vom
        // Nachnamen
        int idxForename = 1;
        int idxSurname = 2;
        int idxNextUsername = 2;

        // toLowerCase
        _forename = _forename.toLowerCase();
        _surname = _surname.toLowerCase();

        // Anzahl der Buchstaben von Vor- und Nachname
        int totalForenameChars = _forename.length();
        int totalSurnameChars = _surname.length();

        if (totalForenameChars < 2 || totalSurnameChars < 2)
        {
            throw new StdException(
                            "Vorname und Nachname muessen jeweils mindestens zwei Buchstaben besitzen");
        }

        int retries = 0;

        // Benutzername gefunden?
        boolean bFreeUsernameFound = false;

        // solange kein Benutzername gefunden wurde
        while (!bFreeUsernameFound)
        {
            // max. Zeichen des Benutzernamens wurden noch nicht erreicht
            if (rUsername.length() <= maxLengthUsername
                            && idxForename <= totalForenameChars
                            && idxSurname <= totalSurnameChars)
            {
                usernamePrefix = _forename.substring(0, idxForename);
                usernamePostfix = _surname.substring(0, idxSurname);

                // abwechselnd Vor- und Nachname aendern
                if (retries % 2 != 0)
                {
                    idxSurname++;
                }
                else
                {
                    idxForename++;
                }

                rUsername = usernamePrefix + usernamePostfix;
            }
            else
            {
                rUsername = usernamePrefix + usernamePostfix + idxNextUsername;
                idxNextUsername++;
            }

            // Username ist frei
            if (!isUsernameExistent(rUsername))
            {
                log.debug("Freier Benutzername " + rUsername + " gefunden");
                bFreeUsernameFound = true;
            }

            retries++;
        }

        return rUsername;
    }

    /**
     * Setzt die CSV-Liste
     * 
     * @return
     */
    public String getCsvList()
    {
        return csvList;
    }

    /**
     * Lieferte die CSV-Liste zurueck
     * 
     * @param csvList
     * @author ckl
     * @since 200623.11.2006_14:54:52
     */
    public void setCsvList(String csvList)
    {
        this.csvList = csvList;
    }

    /**
     * Liefert die Anzahl der Zeilen zurueck
     * 
     * @return
     */
    public int getTotalEntries()
    {
        return totalEntries;
    }

    /**
     * Liefert die Anzahl der importierten Benutzer zurueck
     * 
     * @return
     */
    public int getTotalUsersImported()
    {
        return totalUsersImported;
    }

    /**
     * Liefert die Anzahl der importierten Telefone zurueck
     * 
     * @return
     */
    public int getTotalTelefonsImported()
    {
        return totalTelefonsImported;
    }
}
