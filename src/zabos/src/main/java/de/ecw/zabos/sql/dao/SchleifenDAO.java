package de.ecw.zabos.sql.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.sql.dao.cache.CacheMultipleAdapter;
import de.ecw.zabos.sql.dao.cache.ICacheMultiple;
import de.ecw.zabos.sql.util.DBConnection;
import de.ecw.zabos.sql.util.PreparedStatement;
import de.ecw.zabos.sql.util.ResultSet;
import de.ecw.zabos.sql.vo.BaseIdVO;
import de.ecw.zabos.sql.vo.ObjectFactory;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.AlarmId;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.OrganisationId;
import de.ecw.zabos.types.id.OrganisationsEinheitId;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.types.id.RolleId;
import de.ecw.zabos.types.id.SchleifeId;
import de.ecw.zabos.util.StringUtils;

/**
 * DataAccessObject für {@link Scheme#SCHLEIFE_TABLE} <br />
 * 2006-06-01 CST: Sortierungen in den Prepared Statements eingeführt
 * 
 * @author bsp
 */
public class SchleifenDAO extends AbstractCreateUpdateDeleteDAO
{
    /**
     * Cache
     */
    public final ICacheMultiple<SchleifeVO> CACHE_FIND_ALL = new CacheMultipleAdapter<SchleifeVO>()
    {
        public void updateAfterElementReleasement() throws StdException
        {
            setElements(findAll());
        }
    };

    public SchleifenDAO(DBConnection _dbconnection, ObjectFactory _objectFactory)
    {
        super(_dbconnection, _objectFactory);
    }

    private SchleifeVO nextToVO(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        if (_rs.next())
        {
            return toVO(_rs, _keepResultSet);
        }
        else
        {
            return null;
        }
    }

    private SchleifeVO[] toVOs(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        List<SchleifeVO> al = new ArrayList<SchleifeVO>();
        while (_rs.next())
        {
            al.add(toVO(_rs, true));
        }
        SchleifeVO[] vos = new SchleifeVO[al.size()];
        al.toArray(vos);

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return vos;
    }

    public SchleifeVO toVO(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        SchleifeVO vo = getObjectFactory().createSchleife();
        vo.setSchleifeId(_rs.getSchleifeId(Scheme.COLUMN_ID));
        vo.setBeschreibung(_rs.getString(Scheme.COLUMN_BESCHREIBUNG));
        vo.setGeloescht(_rs.getBooleanNN(Scheme.COLUMN_GELOESCHT));
        vo.setName(_rs.getString(Scheme.SCHLEIFE_COLUMN_NAME));
        vo.setKuerzel(_rs.getString(Scheme.COLUMN_KUERZEL));
        vo.setFuenfton(_rs.getString(Scheme.SCHLEIFE_COLUMN_FUENFTON));

        // 2006-06-09 CKL: Statusreport-Fünfton
        vo.setStatusreportFuenfton(_rs
                        .getBooleanNN(Scheme.SCHLEIFE_COLUMN_STATUSREPORT_FUENFTON));
        vo.setOrganisationsEinheitId(_rs
                        .getOrganisationsEinheitId(Scheme.SCHLEIFE_COLUMN_ORGANISATIONSEINHEIT_ID));

        // 2007-06-07 CKL: Ist Schleife abrechenbar
        vo.setAbrechenbar(_rs
                        .getBooleanNN(Scheme.SCHLEIFE_COLUMN_IST_ABRECHENBAR));

        // 2009-11-23 CKL: ZABOS 1.2.0
        vo.setFolgeschleifeId(_rs
                        .getSchleifeId(Scheme.SCHLEIFE_COLUMN_FOLGESCHLEIFE_ID));
        vo.setRueckmeldeintervall(_rs
                        .getIntegerNN(Scheme.SCHLEIFE_COLUMN_RUECKMELDE_INTERVALL));
        vo.setDruckerKuerzel(_rs
                        .getString(Scheme.SCHLEIFE_COLUMN_DRUCKER_KUERZEL));

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return vo;
    }

    public BaseIdVO create(BaseIdVO _vo) throws StdException
    {
        return createSchleife((SchleifeVO) _vo);
    }

    public BaseIdVO update(BaseIdVO _vo) throws StdException
    {
        return updateSchleife((SchleifeVO) _vo);
    }

    public void delete(BaseId _id) throws StdException
    {
        deleteSchleife((SchleifeId) _id);
    }

    /*
     * queries
     */

    /**
     * Liefert alle Schleifen zurück, in denen die Person eingetragen wurde. Die
     * Schleifen, die als gelöscht gekennzeichnet sind, werden dabei nicht
     * berücksichtigt. Wenn der Benutzer keiner Schleife direkt zugewiesen ist,
     * liefert die Methode ein leeres Array zurück.
     * 
     * @param _id
     * @return
     * @throws StdException
     */
    public SchleifeVO[] findMitgliedschaftInSchleifenVonPerson(PersonId _id) throws StdException
    {
        PreparedStatement pst = getPstFindMitgliedschaftInSchleifenVonPerson();
        pst.setPersonId(1, _id);

        SchleifeVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Zählt die Anzahl der bisher angelegten Schleifen
     */
    public long countSchleifen() throws StdException
    {
        PreparedStatement pst = getPstCountSchleifen();
        ResultSet rs = pst.executeQuery();

        if (rs.next())
        {
            return rs.getLong(1).longValue();
        }

        rs.close();
        pst.close();

        return 0;
    }

    /**
     * Weist einer Person die Berechtigungen der ggb. Rolle in Bezug auf eine
     * Schleife zu.
     */
    public void addPersonInRolleToSchleife(PersonId _personId,
                    RolleId _rolleId, SchleifeId _schleifeId) throws StdException
    {
        BaseId id = dbconnection.nextBaseId();
        PreparedStatement pst = getPstAddPersonInRolleToSchleife();
        pst.setBaseId(1, id);
        pst.setPersonId(2, _personId);
        pst.setRolleId(3, _rolleId);
        pst.setSchleifeId(4, _schleifeId);
        pst.execute();
        pst.close();
    }

    /**
     * Legt eine neue Schleife an. Wenn die Schleife mit dem ggb. Namen bereits
     * als gelöscht in der Datenbank existiert dann wird der alte Datensatz
     * reaktiviert und das "gelöscht" Flag zurückgesetzt.
     * 
     * @param _vo
     * @return
     * @throws StdException
     */
    public SchleifeVO createSchleife(SchleifeVO _vo) throws StdException
    {
        SchleifeVO vo = findSchleifeByName(_vo.getName());

        if (vo != null)
        {
            if (vo.getGeloescht())
            {
                undeleteSchleife(vo.getSchleifeId());
                _vo.setSchleifeId(vo.getSchleifeId());
                return updateSchleife(_vo);
            }
            else
            {
                throw new StdException("schleife id=" + vo.getSchleifeId()
                                + " name=" + vo.getName()
                                + " existiert bereits");
            }
        }
        else
        {
            SchleifeId id = new SchleifeId(dbconnection.nextId());
            PreparedStatement pst = getPstCreateSchleife();
            pst.setSchleifeId(1, id);
            pst.setString(2, _vo.getBeschreibung());
            pst.setString(3, _vo.getName());
            pst.setString(4, _vo.getKuerzel());
            pst.setString(5, _vo.getFuenfton());
            pst.setOrganisationsEinheitId(6, _vo.getOrganisationsEinheitId());
            pst.setBoolean(7, _vo.getStatusreportFuenfton());
            pst.setBoolean(8, _vo.getAbrechenbar());
            pst.setString(9, _vo.getDruckerKuerzel());
            pst.setSchleifeId(10, _vo.getFolgeschleifeId());
            pst.setLong(11, _vo.getRueckmeldeintervall());
            pst.execute();
            pst.close();

            return findSchleifeById(id);
        }
    }

    /**
     * Löscht eine Schleife. Der Datensatz wird in der Datenbank nicht entfernt
     * sondern nur als "gelöscht" markiert.
     * 
     * @param _id
     * @throws StdException
     */
    public void deleteSchleife(SchleifeId _id) throws StdException
    {
        PreparedStatement pst = getPstDeleteSchleife();
        pst.setSchleifeId(1, _id);
        pst.execute();
        pst.close();
    }

    /**
     * Liefert alle Schleifen, die nicht als gelöscht markiert sind. Wenn keine
     * Schleifen gefunden wurden dann liefert diese Methode eine leeres Array.
     * 
     * @return
     * @throws StdException
     */
    public SchleifeVO[] findAll() throws StdException
    {
        if (CACHE_FIND_ALL.findMultiple() != null)
        {
            return CACHE_FIND_ALL.findMultiple();
        }

        PreparedStatement pst = getPstFindAll();
        SchleifeVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Sucht alle Schleifen, auf die die Personen _personId das Recht _rechtId
     * hat. Es wird eine Hashmap der Form SchleifeId => Schleifenkuerzel
     * zur?ckgegeben. Diese Methode benutzt die
     * getPstFindSchleifenByPersonAndRechtForJS() und ist für die AJAX-Methode
     * findSchleifenInSystemMitAusloeseberechtigung() gedacht.
     * 
     * @param _personId
     * @param _rechtId
     * @return
     * @throws StdException
     */
    public Map<String, String> findSchleifenByPersonAndRechtForJSAsHashMap(
                    PersonId _personId, RechtId _rechtId) throws StdException
    {
        PreparedStatement pst = getPstFindSchleifenByPersonAndRechtForJS();
        pst.setRechtId(1, _rechtId);
        pst.setPersonId(2, _personId);
        pst.setRechtId(3, _rechtId);
        pst.setPersonId(4, _personId);
        pst.setRechtId(5, _rechtId);
        pst.setPersonId(6, _personId);
        pst.setRechtId(7, _rechtId);
        pst.setPersonId(8, _personId);
        ResultSet rs = pst.executeQuery();
        HashMap<String, String> hmReturn = new HashMap<String, String>();

        while (rs.next())
        {
            // o.id,o.name,oe.id,oe.name,s.id,s.kuerzel,s.name
            // OrganisationId oid = rs.getOrganisationId(1);
            // //String oname = rs.getString(2);
            // /OrganisationsEinheitId oeid = rs.getOrganisationsEinheitId(3);
            // //String oename = rs.getString(4);
            SchleifeId sid = rs.getSchleifeId(5);
            String skuerzel = rs.getString(6);
            // //String sname = rs.getString(7);
            hmReturn.put("" + sid.getLongValue(), skuerzel);
        }

        rs.close();
        pst.close();

        return hmReturn;
    }

    /**
     * Sucht alle Schleifen, auf die die Personen _personId das Recht _rechtId
     * hat. Das Ergebnis wird in Form eines CSV-Strings geliefert. Der Delimiter
     * ist '?'. Jede Ergebniszeile wird mit LF abgeschlossen. Diese Routine kann
     * verwendet werden um die gesamte Schleifeliste nebst
     * Organisation(seinheit)-Information an den Client zu schicken, der die
     * Liste wiederum mittels JavaScript auswerten kann.
     * 
     * @param _personId
     * @param _rechtId
     * @return
     * @throws StdException
     */
    public String findSchleifenByPersonAndRechtForJS(PersonId _personId,
                    RechtId _rechtId) throws StdException
    {
        PreparedStatement pst = getPstFindSchleifenByPersonAndRechtForJS();
        pst.setRechtId(1, _rechtId);
        pst.setPersonId(2, _personId);
        pst.setRechtId(3, _rechtId);
        pst.setPersonId(4, _personId);
        pst.setRechtId(5, _rechtId);
        pst.setPersonId(6, _personId);
        pst.setRechtId(7, _rechtId);
        pst.setPersonId(8, _personId);
        ResultSet rs = pst.executeQuery();
        StringBuffer sb = new StringBuffer();
        while (rs.next())
        {
            // o.id,o.name,oe.id,oe.name,s.id,s.kuerzel,s.name
            OrganisationId oid = rs.getOrganisationId(1);
            String oname = rs.getString(2);
            OrganisationsEinheitId oeid = rs.getOrganisationsEinheitId(3);
            String oename = rs.getString(4);
            SchleifeId sid = rs.getSchleifeId(5);
            String skuerzel = rs.getString(6);
            String sname = rs.getString(7);

            sb.append(oid);
            sb.append('?');
            sb.append(oname);
            sb.append('?');
            sb.append(oeid);
            sb.append('?');
            sb.append(oename);
            sb.append('?');
            sb.append(sid);
            sb.append('?');
            sb.append(skuerzel);
            sb.append('?');
            sb.append(sname);
            sb.append('\n');
        }

        rs.close();
        pst.close();
        return sb.toString();
    }

    /**
     * Liefert die Schleife mit der ggb. SchleifeId. Wenn die Schleife nicht
     * gefunden wurde dann liefert diese Methode null.
     * 
     * @param _schleifeId
     * @return
     * @throws StdException
     */
    public SchleifeVO findSchleifeById(SchleifeId _schleifeId) throws StdException
    {
        PreparedStatement pst = getPstFindSchleifeById();
        pst.setSchleifeId(1, _schleifeId);
        SchleifeVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert die Schleife mit dem ggb. Namen. Wenn die Schleife nicht gefunden
     * wurde dann liefert diese Methode null. <br />
     * Der Name wird <strong>case-insensitive</strong> behandelt.
     * 
     * @param _name
     * @return
     * @throws StdException
     */
    public SchleifeVO findSchleifeByName(String _name) throws StdException
    {
        PreparedStatement pst = getPstFindSchleifeByName();
        pst.setString(1, _name);
        SchleifeVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert die Schleife mit dem ggb. Kürzel. Wenn die Schleife nicht
     * gefunden wurde dann liefert diese Methode null. <br />
     * Das Kürzel wird <strong>case-sensitive</strong> behandelt.
     * 
     * @param _kuerzel
     * @return
     * @throws StdException
     */
    public SchleifeVO findSchleifeByKuerzelCaseSensitive(String _kuerzel) throws StdException
    {
        PreparedStatement pst = getPstFindSchleifeByKuerzelCaseSensitive();
        pst.setString(1, _kuerzel);
        SchleifeVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert die Schleife mit dem ggb. Kürzel. Die Groß-/Kleinschreibung wird
     * dabei *NICHT* berücksichtigt! Wenn die Schleife nicht gefunden wurde dann
     * liefert diese Methode null.
     * 
     * @author ckl
     * @param _kuerzel
     * @return
     * @throws StdException
     */
    public SchleifeVO findSchleifeByKuerzel(String _kuerzel) throws StdException
    {
        PreparedStatement pst = getPstFindSchleifeByKuerzel();
        pst.setString(1, _kuerzel);
        SchleifeVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert die Schleife mit dem ggb. Fuenfton. Wenn die Schleife nicht
     * gefunden wurde dann liefert diese Methode null. Schleifen, die als
     * "gelöscht" markiert wurden, werden nicht geladen.
     * 
     * @param _fuenfton
     * @return
     * @throws StdException
     */
    public SchleifeVO findSchleifeByFuenfton(String _fuenfton) throws StdException
    {
        PreparedStatement pst = getPstFindSchleifeByFuenfton();
        pst.setString(1, _fuenfton);
        SchleifeVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert alle Schleifen, die dem ggb. Alarm zugewiesen wurden. Wenn keine
     * Schleifen gefunden wurden dann liefert diese Methode eine leeres Array.
     * 
     * @param _alarmId
     * @return
     * @throws StdException
     */
    public SchleifeVO[] findSchleifenByAlarmId(AlarmId _alarmId) throws StdException
    {
        PreparedStatement pst = getPstFindSchleifenByAlarmId();
        pst.setAlarmId(1, _alarmId);
        SchleifeVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert alle Schleifen, die der ggb. Organisationseinheit zugewiesen
     * wurden. Wenn keine Schleifen gefunden wurden dann liefert diese Methode
     * eine leeres Array.
     * 
     * @param _orgEinheitId
     * @return
     * @throws StdException
     */
    public SchleifeVO[] findSchleifenByOrganisationsEinheitId(
                    OrganisationsEinheitId _orgEinheitId) throws StdException
    {
        PreparedStatement pst = getPstFindSchleifenByOrganisationsEinheitId();
        pst.setOrganisationsEinheitId(1, _orgEinheitId);
        SchleifeVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Sucht alle Schleifen, die auf die ggb. Patterns passen (wildcards sind %
     * und _)
     * 
     * @param _fuenfton
     * @param _kuerzel
     * @param _name
     * @return
     * @throws StdException
     */
    public SchleifeVO[] findSchleifenByPattern(String _fuenfton,
                    String _kuerzel, String _name) throws StdException
    {
        PreparedStatement pst = getPstFindSchleifenByPattern();
        pst.setString(1, StringUtils.removeSpecialCharsForSQLRegExp(_fuenfton));
        pst.setString(2, StringUtils.removeSpecialCharsForSQLRegExp(_kuerzel));
        pst.setString(3, StringUtils.removeSpecialCharsForSQLRegExp(_name));
        SchleifeVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert die zuerst ausgelöste Schleife in dem Alarm zurück. Es ist
     * möglich, dass diese Schleife bereits gelöscht ist.
     * 
     * @param _alarmId
     * @return
     */
    public SchleifeVO findZuerstAusgeloesteSchleifeInAlarm(AlarmId _alarmId) throws StdException
    {
        PreparedStatement pst = getFindZuerstAusgeloesteSchleifeInAlarm();
        pst.setAlarmId(1, _alarmId);
        SchleifeVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert die Schleifen zurück, die in einem Alarm als erstes ausgelöst
     * worden sind.
     * 
     * @param _alarmId
     * @return
     * @throws StdException
     */
    public SchleifeVO[] findZuerstAusgeloesteSchleifenInAlarm(AlarmId _alarmId) throws StdException
    {
        PreparedStatement pst = getFindZuerstAusgeloesteSchleifenInAlarm();
        pst.setAlarmId(1, _alarmId);
        SchleifeVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Testet, ob für diese Schleife momentan ein Probealarm stattfindet
     * 
     * @return true=Schleife wird gerade getestet
     * @throws StdException
     */
    public boolean istSchleifeInProbeAlarm(SchleifeId _schleifeId) throws StdException
    {
        UnixTime t = UnixTime.now();

        PreparedStatement pst = getPstIstSchleifeInProbeAlarm();
        pst.setSchleifeId(1, _schleifeId);
        pst.setUnixTime(2, t);
        pst.setUnixTime(3, t);
        ResultSet rs = pst.executeQuery();
        boolean r = rs.next();
        rs.close();

        pst.close();
        return r;
    }

    /**
     * Entzieht allen Personen die Berechtigungen auf die ggb. Schleife
     */
    public void removeAllPersonenFromSchleife(SchleifeId _schleifeId) throws StdException
    {
        PreparedStatement pst = getPstRemoveAllPersonenFromSchleife();
        pst.setSchleifeId(1, _schleifeId);
        pst.execute();
        pst.close();
    }

    /**
     * Entzieht der ggb. Person alle Berechtigungen aller Rollen in Bezug auf
     * eine Schleife.
     * 
     * @param _personId
     * @param _schleifeId
     * @throws StdException
     */
    public void removePersonFromSchleife(PersonId _personId,
                    SchleifeId _schleifeId) throws StdException
    {
        PreparedStatement pst = getPstRemovePersonFromSchleife();
        pst.setPersonId(1, _personId);
        pst.setSchleifeId(2, _schleifeId);
        pst.execute();
        pst.close();
    }

    /**
     * Entzieht der ggb. Person alle Berechtigungen aller Rollen in Bezug auf
     * *alle* Schleifen.
     * 
     * @param _personId
     * @throws StdException
     */
    public void removePersonFromAllSchleifen(PersonId _personId) throws StdException
    {
        PreparedStatement pst = getPstRemovePersonFromAllSchleifen();
        pst.setPersonId(1, _personId);
        pst.execute();
        pst.close();
    }

    /**
     * Entzieht einer Person die Berechtigungen der ggb. Rolle in Bezug auf eine
     * Schleife.
     * 
     * @param _personId
     * @param _rolleId
     * @param _schleifeId
     * @throws StdException
     */
    public void removePersonInRolleFromSchleife(PersonId _personId,
                    RolleId _rolleId, SchleifeId _schleifeId) throws StdException
    {
        PreparedStatement pst = getPstRemovePersonInRolleFromSchleife();
        pst.setPersonId(1, _personId);
        pst.setRolleId(2, _rolleId);
        pst.setSchleifeId(3, _schleifeId);
        pst.execute();
        pst.close();
    }

    /**
     * Entzieht allen Personen die Berechtigungen der ggb. Rolle in Bezug auf
     * eine Schleife.
     * 
     * @param _rolleId
     * @param _schleifeId
     * @throws StdException
     */
    public void removeRolleFromSchleife(RolleId _rolleId, SchleifeId _schleifeId) throws StdException
    {
        PreparedStatement pst = getPstRolleFromSchleife();
        pst.setRolleId(1, _rolleId);
        pst.setSchleifeId(2, _schleifeId);
        pst.execute();
        pst.close();
    }

    /**
     * Entzieht allen Personen die Berechtigungen der ggb. Rolle in Bezug auf
     * *alle* Schleifen.
     * 
     * @param _rolleId
     * @throws StdException
     */
    public void removeRolleFromAllSchleifen(RolleId _rolleId) throws StdException
    {
        PreparedStatement pst = getPstRolleFromAllSchleifen();
        pst.setRolleId(1, _rolleId);
        pst.execute();
        pst.close();
    }

    /**
     * Setzt das "gelöscht" Flag f?r eine Schleife zur?ck.
     * 
     * @param _id
     * @return
     * @throws StdException
     */
    public SchleifeVO undeleteSchleife(SchleifeId _id) throws StdException
    {
        PreparedStatement pst = getPstUndeleteSchleife();
        pst.setSchleifeId(1, _id);
        pst.execute();
        pst.close();

        return findSchleifeById(_id);
    }

    /**
     * Ändert eine Schleife.
     * 
     * @param _vo
     * @return
     * @throws StdException
     */
    public SchleifeVO updateSchleife(SchleifeVO _vo) throws StdException
    {
        PreparedStatement pst = getPstUpdateSchleife();
        pst.setString(1, _vo.getBeschreibung());
        pst.setString(2, _vo.getName());
        pst.setString(3, _vo.getKuerzel());
        pst.setString(4, _vo.getFuenfton());
        pst.setOrganisationsEinheitId(5, _vo.getOrganisationsEinheitId());
        pst.setBoolean(6, _vo.getStatusreportFuenfton());
        pst.setBoolean(7, _vo.getAbrechenbar());
        pst.setString(8, _vo.getDruckerKuerzel());
        pst.setSchleifeId(9, _vo.getFolgeschleifeId());
        pst.setLong(10, _vo.getRueckmeldeintervall());
        pst.setSchleifeId(11, _vo.getSchleifeId());
        pst.execute();
        pst.close();

        return findSchleifeById(_vo.getSchleifeId());
    }

    /*
     * prepared statements
     */
    private PreparedStatement getPstAddPersonInRolleToSchleife() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "INSERT INTO "
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_TABLE + " ("
                        + Scheme.COLUMN_ID + ","
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_PERSON_ID
                        + ","
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_ROLLE_ID
                        + ","
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_SCHLEIFE_ID
                        + ") VALUES(?,?,?,?);");
    }

    private PreparedStatement getPstCountSchleifen() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT COUNT("
                        + Scheme.COLUMN_ID + ") FROM " + Scheme.SCHLEIFE_TABLE
                        + " WHERE " + Scheme.COLUMN_GELOESCHT + "=false;");
    }

    private PreparedStatement getPstCreateSchleife() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "INSERT INTO "
                        + Scheme.SCHLEIFE_TABLE + " (" + Scheme.COLUMN_ID + ","
                        + Scheme.COLUMN_BESCHREIBUNG + ","
                        + Scheme.SCHLEIFE_COLUMN_NAME + ","
                        + Scheme.COLUMN_KUERZEL + ","
                        + Scheme.SCHLEIFE_COLUMN_FUENFTON + ","
                        + Scheme.SCHLEIFE_COLUMN_ORGANISATIONSEINHEIT_ID + ","
                        + Scheme.SCHLEIFE_COLUMN_STATUSREPORT_FUENFTON + ","
                        + Scheme.SCHLEIFE_COLUMN_IST_ABRECHENBAR + ","
                        + Scheme.SCHLEIFE_COLUMN_DRUCKER_KUERZEL + ","
                        + Scheme.SCHLEIFE_COLUMN_FOLGESCHLEIFE_ID + ","
                        + Scheme.SCHLEIFE_COLUMN_RUECKMELDE_INTERVALL
                        + ") VALUES(?,?,?,?,?,?,?,?,?,?,?);");
    }

    private PreparedStatement getPstFindAll() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.SCHLEIFE_TABLE + " WHERE "
                        + Scheme.COLUMN_GELOESCHT + "=false ORDER BY "
                        + Scheme.SCHLEIFE_COLUMN_NAME + ";");
    }

    private PreparedStatement getPstFindSchleifenByPersonAndRechtForJS() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "SELECT DISTINCT o."
                                        + Scheme.COLUMN_ID
                                        + ",o."
                                        + Scheme.ORGANISATION_COLUMN_NAME
                                        + ",oe.id,oe."
                                        + Scheme.ORGANISATIONSEINHEIT_COLUMN_NAME
                                        + ",s."
                                        + Scheme.COLUMN_ID
                                        + ",s."
                                        + Scheme.COLUMN_KUERZEL
                                        + ",s."
                                        + Scheme.SCHLEIFE_COLUMN_NAME
                                        + " AS schleifen_name"
                                        + " FROM "
                                        + Scheme.ORGANISATION_TABLE
                                        + " o,"
                                        + Scheme.ORGANISATIONSEINHEIT_TABLE
                                        + " oe,"
                                        + Scheme.SCHLEIFE_TABLE
                                        + " s,"
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_TABLE
                                        + " piriosys,"
                                        + Scheme.RECHT_IN_ROLLE_TABLE
                                        + " rir WHERE rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID
                                        + "=? AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID
                                        + "=piriosys."
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_ROLLE_ID
                                        + " AND piriosys."
                                        + Scheme.PERSON_IN_ROLLE_IN_SYSTEM_COLUMN_PERSON_ID
                                        + "=? AND oe."
                                        + Scheme.ORGANISATIONSEINHEIT_COLUMN_ORGANISATION_ID
                                        + "=o."
                                        + Scheme.COLUMN_ID
                                        + " AND s."
                                        + Scheme.SCHLEIFE_COLUMN_ORGANISATIONSEINHEIT_ID
                                        + "=oe."
                                        + Scheme.COLUMN_ID
                                        + " AND s."
                                        + Scheme.COLUMN_GELOESCHT
                                        + "=false"
                                        + " AND o."
                                        + Scheme.COLUMN_GELOESCHT
                                        + "=false"
                                        + " AND oe."
                                        + Scheme.COLUMN_GELOESCHT
                                        + "=false"
                                        + " UNION SELECT DISTINCT o."
                                        + Scheme.COLUMN_ID
                                        + ",o."
                                        + Scheme.ORGANISATION_COLUMN_NAME
                                        + ",oe."
                                        + Scheme.COLUMN_ID
                                        + ",oe."
                                        + Scheme.ORGANISATIONSEINHEIT_COLUMN_NAME
                                        + ",s."
                                        + Scheme.COLUMN_ID
                                        + ",s."
                                        + Scheme.COLUMN_KUERZEL
                                        + ",s."
                                        + Scheme.SCHLEIFE_COLUMN_NAME
                                        + " FROM "
                                        + Scheme.ORGANISATION_TABLE
                                        + " o,"
                                        + Scheme.ORGANISATIONSEINHEIT_TABLE
                                        + " oe,"
                                        + Scheme.SCHLEIFE_TABLE
                                        + " s,"
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_TABLE
                                        + " pirio,"
                                        + Scheme.RECHT_IN_ROLLE_TABLE
                                        + " rir WHERE rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID
                                        + "=? AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID
                                        + "=pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ROLLE_ID
                                        + " AND pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_PERSON_ID
                                        + "=? AND o."
                                        + Scheme.COLUMN_ID
                                        + "=pirio."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATION_COLUMN_ORGANISATION_ID
                                        + " AND oe."
                                        + Scheme.ORGANISATIONSEINHEIT_COLUMN_ORGANISATION_ID
                                        + "=o."
                                        + Scheme.COLUMN_ID
                                        + " AND s."
                                        + Scheme.SCHLEIFE_COLUMN_ORGANISATIONSEINHEIT_ID
                                        + "=oe."
                                        + Scheme.COLUMN_ID
                                        + " AND s."
                                        + Scheme.COLUMN_GELOESCHT
                                        + "=false"
                                        + " AND o."
                                        + Scheme.COLUMN_GELOESCHT
                                        + "=false"
                                        + " AND oe."
                                        + Scheme.COLUMN_GELOESCHT
                                        + "=false"
                                        + " UNION SELECT DISTINCT o."
                                        + Scheme.COLUMN_ID
                                        + ",o."
                                        + Scheme.ORGANISATION_COLUMN_NAME
                                        + ",oe."
                                        + Scheme.COLUMN_ID
                                        + ",oe."
                                        + Scheme.ORGANISATIONSEINHEIT_COLUMN_NAME
                                        + ",s."
                                        + Scheme.COLUMN_ID
                                        + ",s."
                                        + Scheme.COLUMN_KUERZEL
                                        + ",s."
                                        + Scheme.SCHLEIFE_COLUMN_NAME
                                        + " FROM "
                                        + Scheme.ORGANISATION_TABLE
                                        + " o,"
                                        + Scheme.ORGANISATIONSEINHEIT_TABLE
                                        + " oe,"
                                        + Scheme.SCHLEIFE_TABLE
                                        + " s,"
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_TABLE
                                        + " pirioe,"
                                        + Scheme.RECHT_IN_ROLLE_TABLE
                                        + " rir WHERE rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID
                                        + "=? AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID
                                        + "=pirioe."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ROLLE_ID
                                        + " AND pirioe."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_PERSON_ID
                                        + "=? AND oe."
                                        + Scheme.COLUMN_ID
                                        + "=pirioe."
                                        + Scheme.PERSON_IN_ROLLE_IN_ORGANISATIONSEINHEIT_COLUMN_ORGANISATIONSEINHEIT_ID
                                        + " AND oe."
                                        + Scheme.ORGANISATIONSEINHEIT_COLUMN_ORGANISATION_ID
                                        + "=o."
                                        + Scheme.COLUMN_ID
                                        + " AND s."
                                        + Scheme.SCHLEIFE_COLUMN_ORGANISATIONSEINHEIT_ID
                                        + "=oe."
                                        + Scheme.COLUMN_ID
                                        + " AND s."
                                        + Scheme.COLUMN_GELOESCHT
                                        + "=false"
                                        + " UNION SELECT DISTINCT o."
                                        + Scheme.COLUMN_ID
                                        + ",o."
                                        + Scheme.ORGANISATION_COLUMN_NAME
                                        + ",oe."
                                        + Scheme.COLUMN_ID
                                        + ",oe."
                                        + Scheme.ORGANISATIONSEINHEIT_COLUMN_NAME
                                        + ",s."
                                        + Scheme.COLUMN_ID
                                        + ",s."
                                        + Scheme.COLUMN_KUERZEL
                                        + ",s."
                                        + Scheme.SCHLEIFE_COLUMN_NAME
                                        + " FROM "
                                        + Scheme.ORGANISATION_TABLE
                                        + " o,"
                                        + Scheme.ORGANISATIONSEINHEIT_TABLE
                                        + " oe,"
                                        + Scheme.SCHLEIFE_TABLE
                                        + " s,"
                                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_TABLE
                                        + " pirios,"
                                        + Scheme.RECHT_IN_ROLLE_TABLE
                                        + " rir WHERE rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_RECHT_ID
                                        + "=? AND rir."
                                        + Scheme.RECHT_IN_ROLLE_COLUMN_ROLLE_ID
                                        + "=pirios."
                                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_ROLLE_ID
                                        + " AND pirios."
                                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_PERSON_ID
                                        + "=? AND s."
                                        + Scheme.COLUMN_ID
                                        + "=pirios."
                                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_SCHLEIFE_ID
                                        + " AND oe."
                                        + Scheme.ORGANISATIONSEINHEIT_COLUMN_ORGANISATION_ID
                                        + "=o."
                                        + Scheme.COLUMN_ID
                                        + " AND s."
                                        + Scheme.SCHLEIFE_COLUMN_ORGANISATIONSEINHEIT_ID
                                        + "=oe." + Scheme.COLUMN_ID + " AND s."
                                        + Scheme.COLUMN_GELOESCHT + "=false"
                                        + " AND o." + Scheme.COLUMN_GELOESCHT
                                        + "=false" + " AND oe."
                                        + Scheme.COLUMN_GELOESCHT + "=false "
                                        + " ORDER BY schleifen_name;"); // 2006-06-08
        // CST:
        // Issue #292 -
        // Sortierung nach
        // Schleifenname
    }

    private PreparedStatement getPstFindSchleifeById() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.SCHLEIFE_TABLE + " WHERE " + Scheme.COLUMN_ID
                        + "=?;");
    }

    private PreparedStatement getPstFindSchleifeByName() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.SCHLEIFE_TABLE + " WHERE "
                        + Scheme.SCHLEIFE_COLUMN_NAME + " ILIKE ?;");
    }

    private PreparedStatement getPstFindSchleifeByKuerzelCaseSensitive() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.SCHLEIFE_TABLE + " WHERE "
                        + Scheme.COLUMN_KUERZEL + "=?;");
    }

    private PreparedStatement getPstFindSchleifeByKuerzel() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.SCHLEIFE_TABLE + " WHERE "
                        + Scheme.COLUMN_KUERZEL + " ILIKE ?;");
    }

    private PreparedStatement getPstFindSchleifeByFuenfton() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.SCHLEIFE_TABLE + " WHERE "
                        + Scheme.SCHLEIFE_COLUMN_FUENFTON + "=? AND "
                        + Scheme.COLUMN_GELOESCHT + "=false;");
    }

    private PreparedStatement getPstFindSchleifenByAlarmId() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT s.* FROM "
                        + Scheme.SCHLEIFE_TABLE + " s,"
                        + Scheme.SCHLEIFE_IN_ALARM_TABLE + " sia WHERE s."
                        + Scheme.COLUMN_ID + "=sia."
                        + Scheme.SCHLEIFE_IN_ALARM_COLUMN_SCHLEIFE_ID
                        + " AND sia."
                        + Scheme.SCHLEIFE_IN_ALARM_COLUMN_ALARM_ID + "=? "
                        + "ORDER BY " + Scheme.SCHLEIFE_COLUMN_NAME + ";");
    }

    private PreparedStatement getPstFindSchleifenByOrganisationsEinheitId() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.SCHLEIFE_TABLE + " WHERE "
                        + Scheme.SCHLEIFE_COLUMN_ORGANISATIONSEINHEIT_ID
                        + "=? AND " + Scheme.COLUMN_GELOESCHT + "=false "
                        + "ORDER BY " + Scheme.SCHLEIFE_COLUMN_NAME + ";");
    }

    private PreparedStatement getPstFindSchleifenByPattern() throws StdException
    {
        // 2006-23-05 CKL: Schleifen werden nun Case-Insensitiv durchsucht
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.SCHLEIFE_TABLE + " WHERE ("
                        + Scheme.SCHLEIFE_COLUMN_FUENFTON + " ~* ? OR "
                        + Scheme.COLUMN_KUERZEL + " ~* ? OR "
                        + Scheme.SCHLEIFE_COLUMN_NAME + " ~* ?) AND "
                        + Scheme.COLUMN_GELOESCHT + "=false " + "ORDER BY "
                        + Scheme.SCHLEIFE_COLUMN_NAME + ";");
    }

    private PreparedStatement getPstDeleteSchleife() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.SCHLEIFE_TABLE + " SET "
                        + Scheme.COLUMN_GELOESCHT + "=true WHERE "
                        + Scheme.COLUMN_ID + "=?;");
    }

    private PreparedStatement getPstIstSchleifeInProbeAlarm() throws StdException
    {
        // 2006-07-04 CKL: SQL-Statement gefixt
        return new PreparedStatement(getDBConnection(), "SELECT s."
                        + Scheme.COLUMN_ID + " FROM " + Scheme.SCHLEIFE_TABLE
                        + " s," + Scheme.PROBE_TERMIN_TABLE + " t WHERE s."
                        + Scheme.SCHLEIFE_COLUMN_ORGANISATIONSEINHEIT_ID
                        + "=t."
                        + Scheme.PROBE_TERMIN_COLUMN_ORGANISATIONSEINHEIT_ID
                        + " AND s." + Scheme.COLUMN_ID + "=? AND ("
                        + Scheme.PROBE_TERMIN_COLUMN_START + " <= ? AND "
                        + Scheme.PROBE_TERMIN_COLUMN_ENDE + " >= ?);");
    }

    private PreparedStatement getPstRemoveAllPersonenFromSchleife() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "DELETE FROM "
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_TABLE + " WHERE "
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_SCHLEIFE_ID
                        + "=?;");
    }

    private PreparedStatement getPstRemovePersonFromSchleife() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "DELETE FROM "
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_TABLE + " WHERE "
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_PERSON_ID
                        + "=? AND "
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_SCHLEIFE_ID
                        + "=?;");
    }

    private PreparedStatement getPstRemovePersonFromAllSchleifen() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "DELETE FROM "
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_TABLE + " WHERE "
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_PERSON_ID
                        + "=?;");
    }

    private PreparedStatement getPstRemovePersonInRolleFromSchleife() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "DELETE FROM "
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_TABLE + " WHERE "
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_PERSON_ID
                        + "=? AND "
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_ROLLE_ID
                        + "=? AND "
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_SCHLEIFE_ID
                        + "=?;");
    }

    private PreparedStatement getPstRolleFromSchleife() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "DELETE FROM "
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_TABLE + " WHERE "
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_ROLLE_ID
                        + "=? AND "
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_SCHLEIFE_ID
                        + "=?;");
    }

    private PreparedStatement getPstRolleFromAllSchleifen() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "DELETE FROM "
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_TABLE + " WHERE "
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_ROLLE_ID
                        + "=?;");
    }

    private PreparedStatement getPstUndeleteSchleife() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.SCHLEIFE_TABLE + " SET "
                        + Scheme.COLUMN_GELOESCHT + "=false WHERE "
                        + Scheme.COLUMN_ID + "=?;");
    }

    private PreparedStatement getPstUpdateSchleife() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.SCHLEIFE_TABLE + " SET "
                        + Scheme.COLUMN_BESCHREIBUNG + "=?,"
                        + Scheme.SCHLEIFE_COLUMN_NAME + "=?,"
                        + Scheme.COLUMN_KUERZEL + "=?,"
                        + Scheme.SCHLEIFE_COLUMN_FUENFTON + "=?,"
                        + Scheme.SCHLEIFE_COLUMN_ORGANISATIONSEINHEIT_ID
                        + "=?," + Scheme.SCHLEIFE_COLUMN_STATUSREPORT_FUENFTON
                        + "=?," + Scheme.SCHLEIFE_COLUMN_IST_ABRECHENBAR
                        + "=?," + Scheme.SCHLEIFE_COLUMN_DRUCKER_KUERZEL
                        + "=?," + Scheme.SCHLEIFE_COLUMN_FOLGESCHLEIFE_ID
                        + "=?," + Scheme.SCHLEIFE_COLUMN_RUECKMELDE_INTERVALL
                        + "=? WHERE " + Scheme.COLUMN_ID + "=?;");
    }

    private PreparedStatement getPstFindMitgliedschaftInSchleifenVonPerson() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.SCHLEIFE_TABLE + " WHERE " + Scheme.COLUMN_ID
                        + " IN (" + "SELECT "
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_SCHLEIFE_ID
                        + " FROM " + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_TABLE
                        + " WHERE "
                        + Scheme.PERSON_IN_ROLLE_IN_SCHLEIFE_COLUMN_PERSON_ID
                        + " = ?)  AND " + Scheme.COLUMN_GELOESCHT + " = false;");
    }

    private PreparedStatement getFindZuerstAusgeloesteSchleifeInAlarm() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.SCHLEIFE_TABLE + " WHERE " + Scheme.COLUMN_ID
                        + " = (SELECT "
                        + Scheme.SCHLEIFE_IN_ALARM_COLUMN_SCHLEIFE_ID
                        + " FROM " + Scheme.SCHLEIFE_IN_ALARM_TABLE + " WHERE "
                        + Scheme.SCHLEIFE_IN_ALARM_COLUMN_ALARM_ID
                        + "= ? ORDER BY " + Scheme.COLUMN_ID + " LIMIT 1)");
    }

    private PreparedStatement getFindZuerstAusgeloesteSchleifenInAlarm() throws StdException
    {
        return new PreparedStatement(
                        getDBConnection(),
                        "SELECT * FROM "
                                        + Scheme.SCHLEIFE_TABLE
                                        + " WHERE "
                                        + Scheme.COLUMN_ID
                                        + " IN (SELECT "
                                        + Scheme.VIEW_BEREICH_REPORT_DETAIL_COLUMN_HAUPTSCHLEIFE_ID
                                        + " FROM "
                                        + Scheme.VIEW_BEREICH_REPORT_DETAIL
                                        + " WHERE "
                                        + Scheme.VIEW_BEREICH_REPORT_DETAIL_COLUMN_SCHLEIFE_ID
                                        + " = "
                                        + Scheme.VIEW_BEREICH_REPORT_DETAIL_COLUMN_HAUPTSCHLEIFE_ID
                                        + " AND "
                                        + Scheme.VIEW_BEREICH_REPORT_DETAIL_COLUMN_ALARM_ID
                                        + " =?)");
    }
}
