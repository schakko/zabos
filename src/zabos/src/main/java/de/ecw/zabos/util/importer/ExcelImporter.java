package de.ecw.zabos.util.importer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import de.ecw.daemon.IDaemon;
import de.ecw.zabos.bo.SchleifeBO;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.BereichDAO;
import de.ecw.zabos.sql.dao.BereichInSchleifeDAO;
import de.ecw.zabos.sql.dao.FunktionstraegerDAO;
import de.ecw.zabos.sql.dao.OrganisationDAO;
import de.ecw.zabos.sql.dao.OrganisationsEinheitDAO;
import de.ecw.zabos.sql.dao.PersonDAO;
import de.ecw.zabos.sql.dao.RechtDAO;
import de.ecw.zabos.sql.dao.SchleifenDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.tao.BenutzerVerwaltungTAO;
import de.ecw.zabos.sql.tao.RechteTAO;
import de.ecw.zabos.sql.tao.RolleTAO;
import de.ecw.zabos.sql.tao.TaoFactory;
import de.ecw.zabos.sql.vo.BereichInSchleifeVO;
import de.ecw.zabos.sql.vo.BereichVO;
import de.ecw.zabos.sql.vo.FunktionstraegerVO;
import de.ecw.zabos.sql.vo.ObjectFactory;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.RechtVO;
import de.ecw.zabos.sql.vo.RolleVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.sql.vo.TelefonVO;
import de.ecw.zabos.types.TelefonNummer;
import de.ecw.zabos.types.id.BereichId;
import de.ecw.zabos.types.id.FunktionstraegerId;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.types.id.SchleifeId;
import de.ecw.zabos.util.StringUtils;

public class ExcelImporter implements IDaemon
{
    /**
     * Mapping der Bereiche innerhalb der Excel-Tabelle auf die Datenbank
     * 
     * @author ckl
     * 
     */
    public class BereichExcelModel
    {
        private String bereichName;

        private int sollstaerke = 0;

        public BereichExcelModel(int _sollstaerke, String _bereichName)
        {
            setSollstaerke(_sollstaerke);
            setBereichName(_bereichName);
        }

        public String getBereichName()
        {
            return bereichName;
        }

        public int getSollstaerke()
        {
            return sollstaerke;
        }

        public void setBereichName(String bereichName)
        {
            this.bereichName = bereichName;
        }

        public void setSollstaerke(int sollstaerke)
        {
            this.sollstaerke = sollstaerke;
        }

        public String toString()
        {
            return getBereichName() + " (Soll: " + getSollstaerke() + ")";
        }
    }

    /**
     * Mapping der Excel-Primary Keys auf die Datenbank
     * 
     * @author ckl
     * 
     */
    public class ExcelToModelMapping
    {
        private char kuerzelCharCounter = 'a';

        private Map<String, BereichId> mapBereichMapping = new HashMap<String, BereichId>();

        private Map<String, String> mapBerufsgruppenKuerzelToBerufsgruppenName = new HashMap<String, String>();

        private Map<String, FunktionstraegerId> mapBerufsgruppeToFunnktionstraegerId = new HashMap<String, FunktionstraegerId>();

        private Map<SchleifeExcelModel, SchleifeExcelModel> mapHauptVerstaerkungsSchleife = new HashMap<SchleifeExcelModel, SchleifeExcelModel>();

        private Map<String, SchleifeId> mapSchleifenMapping = new HashMap<String, SchleifeId>();

        public void setFunktionstraegerMapping(String _funktionstraegerName,
                        FunktionstraegerId _funktionstraegerId)
        {
            mapBerufsgruppeToFunnktionstraegerId.put(_funktionstraegerName,
                            _funktionstraegerId);
        }

        public void addSchleifenNummerToSchleifenIdMapping(
                        String _schleifenNummer, SchleifeId _schleifeId)
        {
            log.info("  Schleife mit der Excel-Nummer '" + _schleifenNummer
                            + "' wird auf Datenbank-Id '" + _schleifeId
                            + "' gemappt");

            mapSchleifenMapping.put(_schleifenNummer, _schleifeId);
        }

        public void mapBerufsgruppenKuerzel(String _berufsgruppenName)
        {
            if (!mapBerufsgruppeToFunnktionstraegerId
                            .containsKey(_berufsgruppenName))
            {
                log.info("  Funktionstraegername '" + _berufsgruppenName
                                + "' wird auf Kürzel '" + kuerzelCharCounter
                                + "' gemappt");

                mapBerufsgruppenKuerzelToBerufsgruppenName.put(""
                                + kuerzelCharCounter, _berufsgruppenName);
                kuerzelCharCounter++;
            }
        }

        public FunktionstraegerId getFunktionstraegerIdFromBerufsgruppenKuerzel(
                        String _funktionstraegerKuerzel)
        {
            return mapBerufsgruppeToFunnktionstraegerId
                            .get(mapBerufsgruppenKuerzelToBerufsgruppenName
                                            .get(_funktionstraegerKuerzel));
        }

        public Map<String, BereichId> getMapBereichMapping()
        {
            return mapBereichMapping;
        }

        public Map<SchleifeExcelModel, SchleifeExcelModel> getMapHauptVerstaerkungsSchleife()
        {
            return mapHauptVerstaerkungsSchleife;
        }

        public SchleifeId getSchleifeIdFromMapping(String _schleifenNummer)
        {
            return mapSchleifenMapping.get(_schleifenNummer);
        }
    }

    /**
     * Mapping der Mitarbeiter der Excel-Tabelle auf die Datenbank
     * 
     * @author ckl
     * 
     */
    public class MitarbeiterExcelModel
    {
        private String berufsgruppenKuerzel;

        private String decryptedPasswort = "";

        private TelefonNummer festnetzNummer;

        private TelefonNummer handyNummer;

        private String klinikumBereich;

        private String name;

        private PersonVO personVO;

        private String schleifeNummer;

        private String vorname = "Unbekannt";

        public String getBerufsgruppenKuerzel()
        {
            return berufsgruppenKuerzel;
        }

        public String getDecryptedPasswort()
        {
            return decryptedPasswort;
        }

        public TelefonNummer getFestnetzNummer()
        {
            return festnetzNummer;
        }

        public TelefonNummer getHandyNummer()
        {
            return handyNummer;
        }

        public String getKlinikumBereich()
        {
            return klinikumBereich;
        }

        public String getName()
        {
            return name;
        }

        public PersonVO getPersonVO()
        {
            return personVO;
        }

        public String getSchleifeNummer()
        {
            return schleifeNummer;
        }

        public String getVorname()
        {
            return vorname;
        }

        public void setBerufsgruppenKuerzel(String berufsgruppenKuerzel)
        {
            this.berufsgruppenKuerzel = berufsgruppenKuerzel;
        }

        public void setDecryptedPasswort(String decryptedPasswort)
        {
            this.decryptedPasswort = decryptedPasswort;
        }

        public void setFestnetzNummer(TelefonNummer festnetzNummer)
        {
            this.festnetzNummer = festnetzNummer;
        }

        public void setHandyNummer(TelefonNummer handyNummer)
        {
            this.handyNummer = handyNummer;
        }

        public void setKlinikumBereich(String klinikumBereich)
        {
            this.klinikumBereich = klinikumBereich;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public void setPersonVO(PersonVO personVO)
        {
            this.personVO = personVO;
        }

        public void setSchleifeNummer(String schleifeNummer)
        {
            this.schleifeNummer = schleifeNummer;
        }

        public void setVorname(String vorname)
        {
            this.vorname = vorname;
        }

        public String toString()
        {
            return getName() + ", " + getVorname() + " ("
                            + getBerufsgruppenKuerzel() + " / "
                            + getKlinikumBereich() + ") - Handy: '"
                            + getHandyNummer() + "', Festznetz: '"
                            + getFestnetzNummer() + "' - Schleife: "
                            + getSchleifeNummer();
        }

    }

    /**
     * Mapping der Schleifeninformationen aus der Excel-Tabelle auf die
     * Datenbank
     * 
     * @author ckl
     * 
     */
    public class SchleifeExcelModel
    {
        private String alarmFallKuerzel;

        private String alarmFallName;

        private Map<String, ArrayList<BereichExcelModel>> mapBerufsgruppenBereiche = new HashMap<String, ArrayList<BereichExcelModel>>();

        private String oeName;

        private String schleifeNummer;

        private String schleifeTyp;

        public void addBereichInBerufsgruppe(String _berufsgruppe,
                        BereichExcelModel _bereich)
        {
            if (!getMapBerufsgruppenBereiche().containsKey(_berufsgruppe))
            {
                getMapBerufsgruppenBereiche().put(_berufsgruppe,
                                new ArrayList<BereichExcelModel>());
            }

            log.info("Bereich '" + _bereich + "' der Berufgsgruppe '"
                            + _berufsgruppe + "' fuer Schleife '" + this
                            + "' hinzugefuegt");
            getMapBerufsgruppenBereiche().get(_berufsgruppe).add(_bereich);
        }

        public String getAlarmFallKuerzel()
        {
            return alarmFallKuerzel;
        }

        public String getAlarmFallName()
        {
            return alarmFallName;
        }

        public Map<String, ArrayList<BereichExcelModel>> getMapBerufsgruppenBereiche()
        {
            return mapBerufsgruppenBereiche;
        }

        public String getOeName()
        {
            return oeName;
        }

        public String getSchleifeKuerzel()
        {
            return getAlarmFallKuerzel() + "-"
                            + getSchleifeTyp().substring(0, 1) + "-"
                            + getOeName();
        }

        public String getSchleifeName()
        {
            return getAlarmFallKuerzel() + " " + getSchleifeNummer() + " "
                            + getOeName() + " " + getSchleifeTyp();
        }

        public String getSchleifeNummer()
        {
            return schleifeNummer;
        }

        public String getSchleifeTyp()
        {
            return schleifeTyp;
        }

        public void setAlarmFallKuerzel(String alarmFallKuerzel)
        {
            this.alarmFallKuerzel = alarmFallKuerzel;
        }

        public void setAlarmFallName(String alarmFallName)
        {
            this.alarmFallName = alarmFallName;
        }

        public void setOeName(String oeName)
        {
            this.oeName = oeName;
        }

        public void setSchleifeNummer(String schleifeNummer)
        {
            this.schleifeNummer = schleifeNummer;
        }

        public void setSchleifeTyp(String schleifeTyp)
        {
            this.schleifeTyp = schleifeTyp;
        }

        public String toString()
        {
            return getOeName() + " -> " + getSchleifeName() + " ("
                            + getSchleifeKuerzel() + ")";
        }
    }

    private final static Logger log = Logger
                    .getLogger(ExcelImporter.class);

    public final static int MITARBEITER_CELL_BERUFGSGRUPPE = 1;

    public final static int MITARBEITER_CELL_KLINIKBEREICH = 2;

    public final static int MITARBEITER_CELL_NACHNAME = 4;

    public final static int MITARBEITER_CELL_SCHLEIFE = 0;

    public final static int MITARBEITER_CELL_TELEFON_1 = 6;

    public final static int MITARBEITER_CELL_TELEFON_2 = 7;

    public final static int MITARBEITER_CELL_VORNAME = 5;

    public final static int SCHLEIFE_CELL_BERUFSGRUPPE_HS = 2;

    public final static int SCHLEIFE_CELL_BERUFSGRUPPE_VS = 9;

    public final static int SCHLEIFE_CELL_KLINIKBEREICH_HS = 3;

    public final static int SCHLEIFE_CELL_KLINIKBEREICH_VS = 10;

    public final static int SCHLEIFE_CELL_SCHLEIFE_HS = 0;

    public final static int SCHLEIFE_CELL_SCHLEIFE_VS = 7;

    public final static int SCHLEIFE_CELL_SOLL_HS = 5;

    /**
     * Injiziert die übergebene Telefonnummer in den Mitarbeiter. Es wird
     * überprüft, was für ein Telefonnummern-Typ der übergebene String ist
     * 
     * @param _nummer
     * @param _mitarbeiter
     */
    public static void injectTelefonNummer(String _nummer,
                    MitarbeiterExcelModel _mitarbeiter)
    {
        if (isTelefonNummer(_nummer))
        {
            if (isHandyNummer(_nummer))
            {
                _mitarbeiter.setHandyNummer(new TelefonNummer(_nummer));
            }
            else
            {
                _mitarbeiter.setFestnetzNummer(new TelefonNummer(_nummer));
            }
        }
    }

    /**
     * Konvertiert einen String der Form "0.0" in einen String "0"
     * 
     * @param _s
     * @return
     */
    public static String doubleValueToString(String _s)
    {
        return "" + Double.valueOf(_s).intValue();
    }

    /**
     * Liefert zurück, ob der übergebene String eine Handynummer ist
     * 
     * @param _nummer
     * @return
     */
    public static boolean isHandyNummer(String _nummer)
    {
        return (_nummer.startsWith("01"));
    }

    /**
     * Liefert zurück, ob die übergebene Telefonnummer eine Handynummer ist
     * 
     * @param _nummer
     * @return
     */
    public static boolean isHandyNummer(TelefonNummer _nummer)
    {
        return (_nummer.getNummer().startsWith("00491"));
    }

    /**
     * Liefert zurück, ob der übergebene String eine Telefonnummer ist
     * 
     * @param _nummer
     * @return
     */
    public static boolean isTelefonNummer(String _nummer)
    {
        return ((_nummer != null) && (_nummer.length() > 2)
                        && (!_nummer.equals("0")) && (!_nummer.equals("0.0")));
    }

    /**
     * Datenbankverbindung
     */
    private DBResource dbResource;

    /**
     * Liste mit den Mitarbeiter-Sheets.
     */
    private List<String> listMitarbeiterSheets;

    /**
     * Offene {@link InputStream}s
     */
    private Map<String, InputStream> mapStreams = new HashMap<String, InputStream>();

    /**
     * Name der Organisation, in der die Daten importiert werden
     */
    private String organisationName = "";

    /**
     * Pfad zur Excel-Datei, in der die Organisationsstrukturen hinterlegt sind
     */
    private String pathOrgastrukturSchleifenXls = "";

    /**
     * Pfad zur Excel-Datei, in der die Mitarbeiter hinterlegt sind
     */
    private String pathSchleifenMitarbeiterXls = "";

    /**
     * Pfad zur Datei, in der die Benutzer-Accounts als CSV gespeichert werden
     */
    private String pathTargetBenutzerAccountCsv = "";

    /**
     * Pfad zur Datei, in der die Soll-/Iststärke gespeichert wird
     */
    private String pathTargetSollIstStaerkeCsv = "";

    /**
     * Legt fest, dass nach dem Import die Dateien umbenannt werden
     */
    private boolean renameXlsFilesAfterImport = true;

    private String rolleDerMitarbeiter = "";

    private boolean simulateImport = true;

    /**
     * Konstruktor. Die Mitarbeiter-Sheets werden mit "MANV", "Kind" und
     * "Infekt" vorbelegt
     * 
     * @param _dbResource
     */
    public ExcelImporter(DBResource _dbResource)
    {
        setDbResource(_dbResource);

        List<String> mitarbeiterSheets = new ArrayList<String>();
        mitarbeiterSheets.add("MANV");
        mitarbeiterSheets.add("Kind");
        mitarbeiterSheets.add("Infekt");

        setListMitarbeiterSheets(mitarbeiterSheets);
    }

    /**
     * Schließt die geöffneten {@link InputStream}s
     * 
     * @throws StdException
     */
    private void closeStreams() throws StdException
    {
        Iterator<String> iterator = mapStreams.keySet().iterator();

        while (iterator.hasNext())
        {
            String path = iterator.next();
            InputStream is = mapStreams.get(path);

            try
            {
                is.close();
            }
            catch (Exception e)
            {
                log.error(e.getMessage());
            }

            if (isRenameXlsFilesAfterImport())
            {
                String pathNew = path + ".old";
                new File(path).renameTo(new File(pathNew));

                log.info("Import-Datei von '" + path + "' nach '" + pathNew
                                + "' umbenannt");
            }
        }
    }

    /**
     * Erstellt einen freien Benutzernamen
     * 
     * @param _person
     * @author ckl
     */
    public void createUnusedBenutzername(PersonVO _person) throws StdException
    {
        PersonDAO daoPerson = getDbResource().getDaoFactory().getPersonDAO();

        String vorname = _person.getVorname().toLowerCase();
        String nachname = _person.getNachname().toLowerCase();
        String name;

        int idxVorname = 1;
        int idxNachname = 2;
        int idxNumber = 1;

        do
        {
            name = vorname.substring(0, idxVorname);
            name += nachname.substring(0, idxNachname);

            if (idxNachname >= nachname.length())
            {
                if (idxVorname >= vorname.length())
                {
                    name += idxNumber;
                    idxNumber++;
                }
                else
                {
                    idxVorname++;
                }
            }
            else
            {
                idxNachname++;
            }

        }
        while (daoPerson.findPersonByName(name) != null);

        _person.setName(name);
    }

    /**
     * Exportiert die Mitarbeiter in eine CSV-Datei
     * 
     * @param _mitarbeiter
     * @param _pathToCsv
     */
    public void exportMitarbeiter(List<MitarbeiterExcelModel> _mitarbeiter,
                    String _pathToCsv)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("ID;Name;Vorname;Benutzername;Passwort;Handynummer;Festnetznummer;HANDY_FEHLT");
        sb.append(System.getProperty("line.separator"));

        List<Long> listAlreadyExported = new ArrayList<Long>();

        for (int i = 0, m = _mitarbeiter.size(); i < m; i++)
        {
            MitarbeiterExcelModel mitarbeiter = _mitarbeiter.get(i);
            PersonVO personVO = mitarbeiter.getPersonVO();

            if (listAlreadyExported.contains(personVO.getPersonId()
                            .getLongValue()))
            {
                continue;
            }

            listAlreadyExported.add(personVO.getPersonId().getLongValue());

            sb.append(personVO.getPersonId().getLongValue());
            sb.append(";");
            sb.append(personVO.getNachname());
            sb.append(";");
            sb.append(personVO.getVorname());
            sb.append(";");
            sb.append(personVO.getName());
            sb.append(";");
            sb.append(mitarbeiter.getDecryptedPasswort());
            sb.append(";");
            sb.append(mitarbeiter.getHandyNummer());
            sb.append(";");
            sb.append(mitarbeiter.getFestnetzNummer());
            sb.append(";");

            if (mitarbeiter.getHandyNummer() != null)
            {
                sb.append("Nein");
            }
            else
            {
                sb.append("Ja");
            }
            sb.append(System.getProperty("line.separator"));
        }

        writeStringBufferToFile(pathTargetBenutzerAccountCsv, sb);
    }

    /**
     * Exportiert die Informationen über Soll- und Isstaerke der einzelnen
     * Bereichs/Funktionsträger-Kombinationen als CSV
     * 
     * @param _mapping
     * @param _pathToCsv
     * @throws StdException
     */
    public void exportSchleifenStatistik(ExcelToModelMapping _mapping,
                    String _pathToCsv)
    {
        OrganisationDAO oDAO = getDbResource().getDaoFactory()
                        .getOrganisationDAO();
        OrganisationsEinheitDAO oeDAO = getDbResource().getDaoFactory()
                        .getOrganisationsEinheitDAO();
        SchleifenDAO sDAO = getDbResource().getDaoFactory().getSchleifenDAO();
        BereichInSchleifeDAO bisDAO = getDbResource().getDaoFactory()
                        .getBereichInSchleifeDAO();
        BereichDAO bereichDAO = getDbResource().getDaoFactory().getBereichDAO();
        FunktionstraegerDAO funktionstraegerDAO = getDbResource()
                        .getDaoFactory().getFunktionstraegerDAO();

        SchleifeBO schleifeBO = getDbResource().getBoFactory().getSchleifeBO();

        StringBuffer sb = new StringBuffer();
        sb.append("O;OE;Schleifename;Schleifenkuerzel;Ist_Folgeschleife;Berufsgruppe;Klinikbereich;Soll;Ist");
        sb.append(System.getProperty("line.separator"));

        try
        {
            OrganisationVO[] oVOs = oDAO.findAll();

            if (oVOs != null)
            {
                for (int i = 0, m = oVOs.length; i < m; i++)
                {
                    OrganisationVO oVO = oVOs[i];

                    OrganisationsEinheitVO[] oeVOs = oeDAO
                                    .findOrganisationsEinheitenByOrganisationId(oVO
                                                    .getOrganisationId());

                    if (oeVOs != null)
                    {
                        for (int j = 0, n = oeVOs.length; j < n; j++)
                        {
                            OrganisationsEinheitVO oeVO = oeVOs[j];

                            SchleifeVO[] sVOs = sDAO
                                            .findSchleifenByOrganisationsEinheitId(oeVO
                                                            .getOrganisationsEinheitId());

                            if (sVOs != null)
                            {
                                for (int k = 0, p = sVOs.length; k < p; k++)
                                {
                                    SchleifeVO sVO = sVOs[k];

                                    BereichInSchleifeVO[] bisVOs = bisDAO
                                                    .findBereicheInSchleifeBySchleifeId(sVO
                                                                    .getSchleifeId());

                                    if (bisVOs != null)
                                    {
                                        for (int l = 0, q = bisVOs.length; l < q; l++)
                                        {
                                            BereichInSchleifeVO bisVO = bisVOs[l];

                                            FunktionstraegerVO fVO = funktionstraegerDAO
                                                            .findFunktionstraegerById(bisVO
                                                                            .getFunktionstraegerId());
                                            BereichVO bVO = bereichDAO
                                                            .findBereichById(bisVO
                                                                            .getBereichId());

                                            sb.append(oVO.getName());
                                            sb.append(";");
                                            sb.append(oeVO.getName());
                                            sb.append(";");
                                            sb.append(sVO.getName());
                                            sb.append(";");
                                            sb.append(sVO.getKuerzel());
                                            sb.append(";");

                                            if (sVO.getFolgeschleifeId() != null)
                                            {
                                                sb.append("Nein");
                                            }
                                            else
                                            {
                                                sb.append("Ja");
                                            }

                                            sb.append(";");
                                            sb.append(fVO.getBeschreibung());
                                            sb.append(";");
                                            sb.append(bVO.getName());
                                            sb.append(";");
                                            sb.append(bisVO.getSollstaerke());
                                            sb.append(";");

                                            PersonVO[] personen = schleifeBO
                                                            .findPersonenMitEmpfangsberechtigungBySchleifeAndBereichAndFunktionstraeger(
                                                                            bisVO.getSchleifeId(),
                                                                            bisVO.getBereichId(),
                                                                            bisVO.getFunktionstraegerId());

                                            int istStaerke = 0;

                                            if (personen != null)
                                            {
                                                istStaerke = personen.length;
                                            }

                                            sb.append(istStaerke);
                                            sb.append(System.getProperty("line.separator"));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (StdException e)
        {
            log.error("Fehler beim Erzeugen der CSV-Datei: " + e.getMessage());
        }

        writeStringBufferToFile(_pathToCsv, sb);
    }

    public void free()
    {
    }

    public DBResource getDbResource()
    {
        return dbResource;
    }

    public List<String> getListMitarbeiterSheets()
    {
        return listMitarbeiterSheets;
    }

    public String getOrganisationName()
    {
        return organisationName;
    }

    public String getPathOrgastrukturSchleifenXls()
    {
        return pathOrgastrukturSchleifenXls;
    }

    public String getPathSchleifenMitarbeiterXls()
    {
        return pathSchleifenMitarbeiterXls;
    }

    public String getPathTargetBenutzerAccountCsv()
    {
        return pathTargetBenutzerAccountCsv;
    }

    public String getPathTargetSollIstStaerkeCsv()
    {
        return pathTargetSollIstStaerkeCsv;
    }

    public String getRolleDerMitarbeiter()
    {
        return rolleDerMitarbeiter;
    }

    /**
     * Liefert zurück, ob der String "0" oder "0.0" übergeben worde
     * 
     * @param _s
     * @return
     */
    public static boolean isNull(String _s)
    {
        return (_s.equals("0") || _s.equals("0.0"));
    }

    /**
     * Importiert die aus der Excel-Tabelle gelesenen Mitarbeiter in die
     * Datenbank
     * 
     * @param _mapping
     * @param _mitarbeiter
     * @throws StdException
     */
    public void importMitarbeiter(ExcelToModelMapping _mapping,
                    List<MitarbeiterExcelModel> _mitarbeiter) throws StdException
    {
        log.info("Default-Rolle der Mitarbeiter: " + getRolleDerMitarbeiter());

        PersonDAO personDAO = getDbResource().getDaoFactory().getPersonDAO();
        BenutzerVerwaltungTAO bvTAO = getDbResource().getTaoFactory()
                        .getBenutzerVerwaltungTAO();
        SchleifenDAO schleifenDAO = getDbResource().getDaoFactory()
                        .getSchleifenDAO();
        ObjectFactory objectFactory = getDbResource().getObjectFactory();

        RechteTAO rechteTAO = getDbResource().getTaoFactory().getRechteTAO();
        RechtDAO rechteDAO = getDbResource().getDaoFactory().getRechtDAO();
        RolleTAO rolleTAO = getDbResource().getTaoFactory().getRolleTAO();

        RolleVO rolleVO = getDbResource().getDaoFactory().getRolleDAO()
                        .findRolleByName(getRolleDerMitarbeiter());

        if (rolleVO == null)
        {
            rolleVO = objectFactory.createRolle();
            rolleVO.setName(getRolleDerMitarbeiter());
            rolleVO.setBeschreibung(getRolleDerMitarbeiter());

            if (!isSimulateImport())
            {
                rolleVO = bvTAO.createRolle(rolleVO);
            }
        }

        RechtVO[] rechteInRolle = rechteDAO.findRechteByRolleId(rolleVO
                        .getRolleId());
        boolean hatEmpfangsberechtigung = false;

        for (int i = 0, m = rechteInRolle.length; i < m; i++)
        {
            if (rechteInRolle[i].getRechtId().getLongValue() == RechtId.ALARMBENACHRICHTIGUNG_EMPFANGEN
                            .getLongValue())
            {
                hatEmpfangsberechtigung = true;
                break;
            }
        }

        if (!hatEmpfangsberechtigung)
        {
            log.info("Fuege das Recht 'Alarmbenachrichtigung Empfangen' der Rolle hinzu");
            rolleTAO.addRechtToRolle(RechtId.ALARMBENACHRICHTIGUNG_EMPFANGEN,
                            rolleVO.getRolleId());
        }

        for (int i = 0, m = _mitarbeiter.size(); i < m; i++)
        {
            MitarbeiterExcelModel mitarbeiter = _mitarbeiter.get(i);

            SchleifeId schleifeId = _mapping
                            .getSchleifeIdFromMapping(mitarbeiter
                                            .getSchleifeNummer());

            SchleifeVO schleifeVO = schleifenDAO.findSchleifeById(schleifeId);

            if (schleifeVO == null)
            {
                log.error("Die Person '"
                                + mitarbeiter
                                + "' konnte nicht importiert werden, da kein Mapping Excel-ID '"
                                + mitarbeiter.getSchleifeNummer()
                                + "' in der Datenbank existiert");
                continue;
            }

            log.info("Versuche Mitarbeiter " + mitarbeiter
                            + " zu importieren...");

            TelefonNummer telefonNummerReferenz = mitarbeiter.getHandyNummer();

            if (telefonNummerReferenz == null)
            {
                telefonNummerReferenz = mitarbeiter.getFestnetzNummer();
            }

            PersonVO personVO = null;

            if (telefonNummerReferenz != null)
            {
                personVO = personDAO
                                .findPersonByTelefonNummer(telefonNummerReferenz);
            }

            if (personVO == null)
            {
                personVO = objectFactory.createPerson();
                personVO.setNachname(mitarbeiter.getName());
                personVO.setVorname(mitarbeiter.getVorname());

                personVO.setOEKostenstelle(schleifeVO
                                .getOrganisationsEinheitId());

                FunktionstraegerId funktionstraegerId = _mapping
                                .getFunktionstraegerIdFromBerufsgruppenKuerzel(mitarbeiter
                                                .getBerufsgruppenKuerzel());
                personVO.setFunktionstraegerId(funktionstraegerId);

                BereichId bereichId = _mapping.getMapBereichMapping().get(
                                mitarbeiter.getKlinikumBereich());

                personVO.setBereichId(bereichId);
                createUnusedBenutzername(personVO);

                mitarbeiter.setDecryptedPasswort(StringUtils.randomString(7,
                                "", true, true, true, true, true, true, true,
                                true));

                personVO.setPassword(StringUtils.md5(mitarbeiter
                                .getDecryptedPasswort()));

                if (!isSimulateImport())
                {
                    personVO = bvTAO.createPerson(personVO);
                }

                log.info("Person " + personVO + " importiert");

                if (mitarbeiter.getHandyNummer() != null)
                {
                    TelefonVO handy = objectFactory.createTelefon();
                    handy.setAktiv(true);
                    handy.setNummer(mitarbeiter.getHandyNummer());
                    handy.setPersonId(personVO.getPersonId());

                    bvTAO.createTelefon(handy);
                }

                if (mitarbeiter.getFestnetzNummer() != null)
                {
                    TelefonVO festnetz = objectFactory.createTelefon();
                    festnetz.setNummer(mitarbeiter.getFestnetzNummer());
                    festnetz.setPersonId(personVO.getPersonId());
                    festnetz.setAktiv((mitarbeiter.getHandyNummer() == null));

                    bvTAO.createTelefon(festnetz);
                }
            }
            else
            {
                log.info("Mitarbeiter mit dieser Telefonnummer ist bereits eingetragen");
            }

            mitarbeiter.setPersonVO(personVO);

            if (personVO != null)
            {
                if (!isSimulateImport())
                {
                    if (!rechteTAO.addPersonInRolleToSchleife(
                                    personVO.getPersonId(),
                                    rolleVO.getRolleId(), schleifeId))
                    {
                        log.error("Die Person " + personVO
                                        + " konnte der Rolle '" + rolleVO
                                        + "' in der Schleife '" + schleifeId
                                        + "' nicht hinzugefuegt werden.");
                    }
                    else
                    {
                        log.info("Person " + personVO + " wurde der Schleife "
                                        + schleifeVO + " zugeordnet");
                    }
                }
            }
        }
    }

    /**
     * Importiert die Organisationsstruktur aus der Excel-Tabelle in die
     * Datenbank
     * 
     * @param _mapping
     * @return
     * @throws StdException
     */
    public ExcelToModelMapping importOrganisationStruktur(
                    ExcelToModelMapping _mapping) throws StdException
    {
        log.info("Alle Schleifen werden in die Organisation '"
                        + getOrganisationName() + "' importiert");

        TaoFactory taoFactory = getDbResource().getTaoFactory();
        BenutzerVerwaltungTAO bvTAO = taoFactory.getBenutzerVerwaltungTAO();
        OrganisationsEinheitDAO daoOE = getDbResource().getDaoFactory()
                        .getOrganisationsEinheitDAO();
        BereichDAO daoBereich = getDbResource().getDaoFactory().getBereichDAO();
        FunktionstraegerDAO daoFunktionstraeger = getDbResource()
                        .getDaoFactory().getFunktionstraegerDAO();
        ObjectFactory objectFactory = getDbResource().getObjectFactory();

        if (getOrganisationName() == null
                        || getOrganisationName().length() == 0)
        {
            throw new StdException(
                            "Es muss ein Name für die Organisation festgelegt werden!");
        }

        OrganisationVO oVO = getDbResource().getDaoFactory()
                        .getOrganisationDAO()
                        .findOrganisationByName(getOrganisationName());

        if (oVO == null)
        {
            oVO = objectFactory.createOrganisation();
            oVO.setBeschreibung(getOrganisationName());
            oVO.setName(getOrganisationName());

            log.info("Erstelle neue Organisation '" + oVO + "'");

            if (!isSimulateImport())
            {
                oVO = bvTAO.createOrganisation(oVO);
            }
        }
        else
        {
            log.info("Organisation '" + oVO + "' existiert bereits");
        }

        Iterator<SchleifeExcelModel> it = _mapping
                        .getMapHauptVerstaerkungsSchleife().keySet().iterator();

        while (it.hasNext())
        {
            log.info("-------------------------------");
            SchleifeExcelModel schleifeHaupt = it.next();

            SchleifeExcelModel schleifeVerstaerkung = _mapping
                            .getMapHauptVerstaerkungsSchleife().get(
                                            schleifeHaupt);

            // OE erzeugen falls noch nicht existent
            OrganisationsEinheitVO oeVO = daoOE
                            .findOrganisationsEinheitByName(schleifeHaupt
                                            .getOeName());

            if (oeVO == null)
            {
                oeVO = objectFactory.createOrganisationsEinheit();
                oeVO.setName(schleifeHaupt.getOeName());
                oeVO.setBeschreibung(schleifeHaupt.getOeName());
                oeVO.setOrganisationId(oVO.getOrganisationId());

                log.info("Erstelle neue Organisationseinheit '" + oeVO + "'");

                if (!isSimulateImport())
                {
                    oeVO = bvTAO.createOrganisationseinheit(oeVO);
                }
            }

            // Hauptschleife und Verstärkungsschleife erzeugen

            log.info("Versuche " + schleifeHaupt + " zu importieren...");
            SchleifeVO schleifeHauptVO = addSchleife(schleifeHaupt, "Haupt",
                            oVO, oeVO);

            log.info("Versuche " + schleifeVerstaerkung + " zu importieren...");
            SchleifeVO schleifeVerstaerkungVO = addSchleife(
                            schleifeVerstaerkung, "Verstaerkung", oVO, oeVO);

            if (!isSimulateImport())
            {
                schleifeHauptVO.setFolgeschleifeId(schleifeVerstaerkungVO
                                .getSchleifeId());
                schleifeHauptVO = bvTAO.updateSchleife(schleifeHauptVO);
            }

            _mapping.addSchleifenNummerToSchleifenIdMapping(
                            schleifeHaupt.getSchleifeNummer(),
                            schleifeHauptVO.getSchleifeId());
            _mapping.addSchleifenNummerToSchleifenIdMapping(
                            schleifeVerstaerkung.getSchleifeNummer(),
                            schleifeVerstaerkungVO.getSchleifeId());

            Iterator<String> itBerufsgruppen = schleifeHaupt
                            .getMapBerufsgruppenBereiche().keySet().iterator();

            while (itBerufsgruppen.hasNext())
            {
                String berufsgruppe = itBerufsgruppen.next();
                ArrayList<BereichExcelModel> listBereiche = schleifeHaupt
                                .getMapBerufsgruppenBereiche()
                                .get(berufsgruppe);

                FunktionstraegerVO funktionstraegerVO = daoFunktionstraeger
                                .findFunktionstraegerByBeschreibung(berufsgruppe);

                // Funktionsträger existiert noch nicht => neuen erstellen
                if (funktionstraegerVO == null)
                {
                    funktionstraegerVO = objectFactory.createFunktionstraeger();
                    funktionstraegerVO.setBeschreibung(berufsgruppe);
                    String kuerzel = berufsgruppe.substring(0, 1);
                    kuerzel = kuerzel.toUpperCase();
                    int counter = 1;

                    // Kürzel generieren
                    while (daoFunktionstraeger
                                    .findFunktionstraegerByKuerzel(kuerzel) != null)
                    {
                        kuerzel = kuerzel + counter;
                        counter++;
                    }

                    funktionstraegerVO.setKuerzel(kuerzel);

                    log.info("Berufsgruppe '" + funktionstraegerVO
                                    + "' erstellt");
                    if (!isSimulateImport())
                    {
                        funktionstraegerVO = bvTAO
                                        .createFunktionstraeger(funktionstraegerVO);
                    }
                }

                log.info("  [" + funktionstraegerVO
                                + "] (Berufsgruppe) enthält '"
                                + listBereiche.size() + "' Klinikbereiche");

                _mapping.setFunktionstraegerMapping(
                                funktionstraegerVO.getBeschreibung(),
                                funktionstraegerVO.getFunktionstraegerId());

                for (int i = 0, m = listBereiche.size(); i < m; i++)
                {
                    BereichExcelModel bereichModel = listBereiche.get(i);

                    BereichVO bereichVO = daoBereich
                                    .findBereichByName(bereichModel
                                                    .getBereichName());

                    if (bereichVO == null)
                    {
                        log.info("    Erstelle neuen Klinikbereich: "
                                        + bereichModel);

                        bereichVO = objectFactory.createBereich();
                        bereichVO.setName(bereichModel.getBereichName());
                        bereichVO.setBeschreibung(bereichModel.getBereichName());

                        if (!isSimulateImport())
                        {
                            bereichVO = bvTAO.createBereich(bereichVO);
                        }

                        _mapping.getMapBereichMapping().put(
                                        bereichModel.getBereichName(),
                                        bereichVO.getBereichId());
                    }

                    BereichInSchleifeVO bisHauptVO = objectFactory
                                    .createBereichInSchleife();
                    bisHauptVO.setBereichId(bereichVO.getBereichId());
                    bisHauptVO.setFunktionstraegerId(funktionstraegerVO
                                    .getFunktionstraegerId());
                    bisHauptVO.setSollstaerke(bereichModel.getSollstaerke());
                    bisHauptVO.setSchleifeId(schleifeHauptVO.getSchleifeId());

                    BereichInSchleifeVO bisVerstaerkungVO = objectFactory
                                    .createBereichInSchleife();
                    bisVerstaerkungVO.setBereichId(bereichVO.getBereichId());
                    bisVerstaerkungVO.setFunktionstraegerId(funktionstraegerVO
                                    .getFunktionstraegerId());
                    bisVerstaerkungVO.setSchleifeId(schleifeVerstaerkungVO
                                    .getSchleifeId());

                    log.info("    [" + bereichVO + "] in Berufsgruppe "
                                    + funktionstraegerVO
                                    + " zugeordnet (Haupt & Verstaerkung)");

                    if (!isSimulateImport())
                    {
                        bisHauptVO = bvTAO.createBereichInSchleife(bisHauptVO);
                        bisVerstaerkungVO = bvTAO
                                        .createBereichInSchleife(bisVerstaerkungVO);
                    }
                }
            }
        }

        return _mapping;
    }

    private SchleifeVO addSchleife(SchleifeExcelModel _excelSchleife,
                    String _type, OrganisationVO oVO,
                    OrganisationsEinheitVO oeVO) throws StdException
    {
        SchleifenDAO daoSchleife = getDbResource().getDaoFactory()
                        .getSchleifenDAO();
        BenutzerVerwaltungTAO bvTAO = getDbResource().getTaoFactory()
                        .getBenutzerVerwaltungTAO();

        SchleifeVO r = daoSchleife.findSchleifeByName(_excelSchleife
                        .getSchleifeName());

        if (r == null)
        {
            r = daoSchleife.findSchleifeByKuerzel(_excelSchleife
                            .getSchleifeNummer());

            if (r == null)
            {
                r = daoSchleife.getObjectFactory().createSchleife();
                r.setOrganisationsEinheitId(oeVO.getOrganisationsEinheitId());
                r.setName(_excelSchleife.getSchleifeName());
                r.setKuerzel(_excelSchleife.getSchleifeNummer());
                r.setBeschreibung(_excelSchleife.getAlarmFallName());
                r.setAbrechenbar(true);

                if (!isSimulateImport())
                {
                    r = bvTAO.createSchleife(r);
                }

                log.info("[" + r + "] (" + _type + ") in " + oVO + " -> "
                                + oeVO);
            }
            else
            {
                log.warn("Schleife "
                                + r
                                + " konnte nicht erzeugt werden, Schleife mit diesem Kuerzel existiert bereits.");
            }
        }
        else
        {
            log.warn("Schleife "
                            + r
                            + " konnte nicht erzeugt werden, Schleife mit diesem Namen existiert bereits.");
        }

        return r;
    }

    public void init() throws StdException
    {
        if (isSimulateImport())
        {
            log.info("Simuliere Import");
        }
        else
        {
            log.warn("Fuehre Import durch!");
        }

        try
        {
            ExcelToModelMapping mapping = readOrganisationStruktur(readExcelFile(getPathOrgastrukturSchleifenXls()));
            importOrganisationStruktur(mapping);

            try
            {
                List<MitarbeiterExcelModel> mitarbeiter = readMitarbeiter(readExcelFile(getPathSchleifenMitarbeiterXls()));
                importMitarbeiter(mapping, mitarbeiter);

                if (getPathTargetBenutzerAccountCsv() != null
                                && getPathTargetBenutzerAccountCsv().length() > 0)
                {
                    exportMitarbeiter(mitarbeiter,
                                    getPathTargetBenutzerAccountCsv());
                }

                if (getPathTargetSollIstStaerkeCsv() != null
                                && getPathTargetSollIstStaerkeCsv().length() > 0)
                {
                    exportSchleifenStatistik(mapping,
                                    getPathTargetSollIstStaerkeCsv());
                }
            }
            catch (StdException e)
            {
                log.error("Fehler beim Importieren der Mitarbeiter: "
                                + e.getMessage());
            }

        }
        catch (StdException e)
        {
            log.error("Fehler beim Importieren der Organisationssturktur: "
                            + e.getMessage());
        }

        try
        {
            closeStreams();
        }
        catch (StdException e)
        {
            log.error("Geoeffnete Streams konnte nicht geschlossen werden: "
                            + e.getMessage());
        }
    }

    public boolean isRenameXlsFilesAfterImport()
    {
        return renameXlsFilesAfterImport;
    }

    public DAEMON_STATUS getDaemonStatus()
    {
        return DAEMON_STATUS.ONLINE;
    }

    public boolean isSimulateImport()
    {
        return simulateImport;
    }

    public HSSFWorkbook readExcelFile(String _path) throws StdException
    {
        try
        {
            InputStream is = new FileInputStream(_path);
            mapStreams.put(_path, is);
            HSSFWorkbook workbook = new HSSFWorkbook(is);

            return workbook;
        }
        catch (Exception e)
        {
            throw new StdException("Konnte Excel-Datei '" + _path
                            + "' nicht oeffnen");
        }
    }

    /**
     * Liest die Excel-Tabelle mit den Mitarbeitern ein
     * 
     * @return
     */
    public List<MitarbeiterExcelModel> readMitarbeiter(HSSFWorkbook _workbook)
    {
        List<MitarbeiterExcelModel> list = new ArrayList<MitarbeiterExcelModel>();

        if (getListMitarbeiterSheets() != null)
        {
            for (int i = 0, m = getListMitarbeiterSheets().size(); i < m; i++)
            {
                String sheet = getListMitarbeiterSheets().get(i);
                log.info("Lese Mitarbeiter aus Worksheet '" + sheet + "'");

                HSSFSheet worksheet = _workbook.getSheet(sheet);

                if (worksheet == null)
                {
                    log.error("Worksheet mit dem Namen '" + sheet
                                    + "' konnte nicht gefunden werden");
                    continue;
                }

                readMitarbeiterWorksheet(worksheet, list);
            }
        }

        return list;
    }

    /**
     * Liest das Worksheet der Excel-Tabelle mit den Mitarbeitern eines
     * Alarmfalls ein
     * 
     * @param _worksheet
     * @param _listMitarbeiter
     */
    public void readMitarbeiterWorksheet(HSSFSheet _worksheet,
                    List<MitarbeiterExcelModel> _listMitarbeiter)
    {
        int m = _worksheet.getLastRowNum();
        log.info("Insgesamt '" + m + "' Zeilen im aktuellen Worksheet");

        for (int i = 1; i <= m; i++)
        {
            HSSFRow row = _worksheet.getRow(i);

            MitarbeiterExcelModel mitarbeiter = new MitarbeiterExcelModel();

            mitarbeiter.setSchleifeNummer(doubleValueToString(row.getCell(
                            MITARBEITER_CELL_SCHLEIFE).toString()));

            assert mitarbeiter.getSchleifeNummer() != null
                            && mitarbeiter.getSchleifeNummer().length() > 0;

            mitarbeiter.setBerufsgruppenKuerzel(row.getCell(
                            MITARBEITER_CELL_BERUFGSGRUPPE).toString());

            assert mitarbeiter.getBerufsgruppenKuerzel() != null
                            && mitarbeiter.getBerufsgruppenKuerzel().length() > 0;

            mitarbeiter.setKlinikumBereich(row.getCell(
                            MITARBEITER_CELL_KLINIKBEREICH).toString());

            assert mitarbeiter.getKlinikumBereich() != null
                            && mitarbeiter.getKlinikumBereich().length() > 0;

            String contentNachname = row.getCell(MITARBEITER_CELL_NACHNAME)
                            .toString();
            String contentVorname = row.getCell(MITARBEITER_CELL_VORNAME)
                            .toString();

            if (isNull(contentNachname) && isNull(contentVorname))
            {
                log.info("Ueberspringe Zeile "
                                + i
                                + ": Vorname und Nachname sind nicht hinterlegt -> fehlender Eintrag");
                continue;
            }

            mitarbeiter.setName(contentNachname.replaceAll("[0-9]", ""));

            assert mitarbeiter.getName() != null
                            && mitarbeiter.getName().length() > 0;

            if (contentVorname.length() > 0 && !isNull(contentVorname))
            {
                mitarbeiter.setVorname(contentVorname);
            }

            String telefonNummer1 = row.getCell(MITARBEITER_CELL_TELEFON_1)
                            .toString();
            String telefonNummer2 = row.getCell(MITARBEITER_CELL_TELEFON_2)
                            .toString();

            injectTelefonNummer(telefonNummer1, mitarbeiter);
            injectTelefonNummer(telefonNummer2, mitarbeiter);

            log.info("Mitarbeiter aus Excel-Datei gelesen: " + mitarbeiter);

            _listMitarbeiter.add(mitarbeiter);
        }
    }

    /**
     * Liest die Daten aus der Excel-Tabelle in eine Java-Struktur ein
     * 
     * @return
     * @throws StdException
     */
    public ExcelToModelMapping readOrganisationStruktur(HSSFWorkbook _workbook) throws StdException
    {
        ExcelToModelMapping r = new ExcelToModelMapping();
        HSSFSheet worksheet = _workbook.getSheetAt(0);

        boolean bInSchleifenStruktur = false;
        SchleifeExcelModel modelSchleifeHaupt = null;
        SchleifeExcelModel modelSchleifeVerstaerkung = null;
        String letzteBerufsgruppe = "";

        int m = worksheet.getLastRowNum();
        log.info("Organisationsstruktur enthält " + m + " Zeilen");

        for (int i = 1; i < m; i++)
        {
            HSSFRow row = worksheet.getRow(i);

            if (row == null)
            {
                continue;
            }
            HSSFCell cellSpalteHS = row.getCell(SCHLEIFE_CELL_SCHLEIFE_HS);
            HSSFCell cellSpalteHSKlinikbereich = row
                            .getCell(SCHLEIFE_CELL_KLINIKBEREICH_HS);
            HSSFCell cellSpalteHSBerufsgruppe = row
                            .getCell(SCHLEIFE_CELL_BERUFSGRUPPE_HS);

            String contentSpalteHS = "";
            String contentSpalteHSBerufsgruppe = "";
            String contentSpalteHSKlinikbereich = "";

            if (cellSpalteHS != null)
            {
                contentSpalteHS = cellSpalteHS.toString();
            }

            if (cellSpalteHSBerufsgruppe != null)
            {
                contentSpalteHSBerufsgruppe = cellSpalteHSBerufsgruppe
                                .toString();
            }

            if (cellSpalteHSKlinikbereich != null)
            {
                contentSpalteHSKlinikbereich = cellSpalteHSKlinikbereich
                                .toString();
            }

            String alarmFallKuerzel = "";
            String alarmFallName = "";

            // Header "Alarmfall" gefunden
            if (contentSpalteHS.startsWith("Alarmfall"))
            {
                alarmFallKuerzel = contentSpalteHS.substring(
                                contentSpalteHS.indexOf('(') + 1,
                                (contentSpalteHS.indexOf(')')));
                alarmFallName = contentSpalteHS;

                letzteBerufsgruppe = "";
            }

            // Schleife beginnt
            if (contentSpalteHS.equals("Schleife"))
            {
                bInSchleifenStruktur = true;

                modelSchleifeHaupt = new SchleifeExcelModel();
                modelSchleifeHaupt.setAlarmFallName(alarmFallName);
                modelSchleifeHaupt.setAlarmFallKuerzel(alarmFallKuerzel);

                modelSchleifeVerstaerkung = new SchleifeExcelModel();
                modelSchleifeVerstaerkung.setAlarmFallName(alarmFallName);
                modelSchleifeVerstaerkung.setAlarmFallKuerzel(alarmFallKuerzel);

                modelSchleifeHaupt.setOeName(worksheet.getRow((i + 2))
                                .getCell(SCHLEIFE_CELL_SCHLEIFE_HS).toString());

                assert modelSchleifeHaupt.getOeName() != null
                                && modelSchleifeHaupt.getOeName().length() > 0;

                modelSchleifeVerstaerkung.setOeName(worksheet.getRow((i + 2))
                                .getCell(SCHLEIFE_CELL_SCHLEIFE_VS).toString());
                assert modelSchleifeVerstaerkung.getOeName() != null
                                && modelSchleifeVerstaerkung.getOeName()
                                                .length() > 0;

                modelSchleifeHaupt
                                .setSchleifeNummer(doubleValueToString(worksheet
                                                .getRow((i + 1))
                                                .getCell(SCHLEIFE_CELL_SCHLEIFE_HS)
                                                .toString()));

                assert modelSchleifeHaupt.getSchleifeNummer() != null
                                && modelSchleifeHaupt.getSchleifeNummer()
                                                .length() > 0;

                modelSchleifeVerstaerkung
                                .setSchleifeNummer(doubleValueToString(worksheet
                                                .getRow((i + 1))
                                                .getCell(SCHLEIFE_CELL_SCHLEIFE_VS)
                                                .toString()));

                assert modelSchleifeVerstaerkung.getSchleifeNummer() != null
                                && modelSchleifeVerstaerkung
                                                .getSchleifeNummer().length() > 0;

                modelSchleifeHaupt.setSchleifeTyp(worksheet.getRow((i + 3))
                                .getCell(SCHLEIFE_CELL_SCHLEIFE_HS).toString());

                assert modelSchleifeHaupt.getSchleifeTyp() != null
                                && modelSchleifeHaupt.getSchleifeTyp().length() > 0;

                modelSchleifeVerstaerkung.setSchleifeTyp(worksheet
                                .getRow((i + 3))
                                .getCell(SCHLEIFE_CELL_SCHLEIFE_VS).toString());

                assert modelSchleifeVerstaerkung.getSchleifeTyp() != null
                                && modelSchleifeVerstaerkung.getSchleifeTyp()
                                                .length() > 0;

            }

            if (bInSchleifenStruktur)
            {
                if (contentSpalteHSKlinikbereich.length() == 0)
                {

                    log.info("Schleife (Haupt) gelesen: " + modelSchleifeHaupt);
                    log.info("Schleife (Verstaerkung) gelesen: "
                                    + modelSchleifeVerstaerkung);

                    r.getMapHauptVerstaerkungsSchleife().put(
                                    modelSchleifeHaupt,
                                    modelSchleifeVerstaerkung);

                    bInSchleifenStruktur = false;
                }
                // Klinikbereich ist immer gesetzt
                else
                {
                    if (contentSpalteHSBerufsgruppe.length() > 0)
                    {
                        letzteBerufsgruppe = contentSpalteHSBerufsgruppe;
                        r.mapBerufsgruppenKuerzel(letzteBerufsgruppe);
                    }

                    int contentSpalteHSSoll = Integer
                                    .valueOf(doubleValueToString(row.getCell(
                                                    SCHLEIFE_CELL_SOLL_HS)
                                                    .toString()));

                    modelSchleifeHaupt
                                    .addBereichInBerufsgruppe(
                                                    letzteBerufsgruppe,
                                                    new BereichExcelModel(
                                                                    contentSpalteHSSoll,
                                                                    contentSpalteHSKlinikbereich));
                    modelSchleifeVerstaerkung
                                    .addBereichInBerufsgruppe(
                                                    letzteBerufsgruppe,
                                                    new BereichExcelModel(0,
                                                                    contentSpalteHSKlinikbereich));
                }
            }
        }
        return r;
    }

    public void setDbResource(DBResource dbResource)
    {
        this.dbResource = dbResource;
    }

    public void setListMitarbeiterSheets(List<String> listMitarbeiterSheets)
    {
        this.listMitarbeiterSheets = listMitarbeiterSheets;
    }

    public void setOrganisationName(String organisationName)
    {
        this.organisationName = organisationName;
    }

    public void setPathOrgastrukturSchleifenXls(
                    String pathOrgastrukturSchleifenXls)
    {
        this.pathOrgastrukturSchleifenXls = pathOrgastrukturSchleifenXls;
    }

    public void setPathSchleifenMitarbeiterXls(
                    String pathSchleifenMitarbeiterXls)
    {
        this.pathSchleifenMitarbeiterXls = pathSchleifenMitarbeiterXls;
    }

    public void setPathTargetBenutzerAccountCsv(
                    String pathTargetBenutzerAccountCsv)
    {
        this.pathTargetBenutzerAccountCsv = pathTargetBenutzerAccountCsv;
    }

    public void setPathTargetSollIstStaerkeCsv(
                    String pathTargetSollIstStaerkeCsv)
    {
        this.pathTargetSollIstStaerkeCsv = pathTargetSollIstStaerkeCsv;
    }

    public void setRenameXlsFilesAfterImport(boolean renameXlsFilesAfterImport)
    {
        this.renameXlsFilesAfterImport = renameXlsFilesAfterImport;
    }

    public void setRolleDerMitarbeiter(String rolleDerMitarbeiter)
    {
        this.rolleDerMitarbeiter = rolleDerMitarbeiter;
    }

    public void setSimulateImport(boolean simulateImport)
    {
        this.simulateImport = simulateImport;
    }

    /**
     * Exportiert den Stringbuffer in eine Datei
     * 
     * @param _file
     * @param _sb
     * @throws StdException
     */
    private void writeStringBufferToFile(String _file, StringBuffer _sb)
    {
        try
        {
            FileWriter fstream = new FileWriter(_file);
            BufferedWriter writer = new BufferedWriter(fstream);
            writer.write(_sb.toString());
            writer.close();

            log.info("Datei '" + _file + "' geschrieben");
        }
        catch (Exception e)
        {
            log.error("Konnte CSV-Datei '" + _file + "' nicht schreiben: "
                            + e.getMessage());
        }

    }

}
