package de.ecw.zabos.sql.dao;

import java.util.ArrayList;
import java.util.List;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.sql.util.DBConnection;
import de.ecw.zabos.sql.util.PreparedStatement;
import de.ecw.zabos.sql.util.ResultSet;
import de.ecw.zabos.sql.vo.BaseIdVO;
import de.ecw.zabos.sql.vo.ObjectFactory;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.TelefonVO;
import de.ecw.zabos.types.TelefonNummer;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.TelefonId;

/**
 * DataAccessObject für {@link Scheme#TELEFON_TABLE}
 * 
 * 2006-056-01 CST: Sortierung in Prepared Statements eingebaut
 * 
 * @author bsp
 * 
 */
public class TelefonDAO extends AbstractCreateUpdateDeleteDAO
{
    public TelefonDAO(DBConnection _dbconnection, ObjectFactory _objectFactory)
    {
        super(_dbconnection, _objectFactory);
    }

    private TelefonVO nextToVO(ResultSet _rs, boolean _keepResultSet) throws StdException
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

    public TelefonVO toVO(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        TelefonVO vo = getObjectFactory().createTelefon();
        vo.setTelefonId(_rs.getTelefonId(Scheme.COLUMN_ID));
        vo.setPersonId(_rs.getPersonId(Scheme.TELEFON_COLUMN_PERSON_ID));
        vo.setNummer(_rs.getTelefonNummer(Scheme.TELEFON_COLUMN_NUMMER));
        vo.setAktiv(_rs.getBooleanNN(Scheme.TELEFON_COLUMN_AKTIV));
        vo.setZeitfensterStart(_rs
                        .getUnixTime(Scheme.TELEFON_COLUMN_ZEITFENSTER_START));
        vo.setZeitfensterEnde(_rs
                        .getUnixTime(Scheme.TELEFON_COLUMN_ZEITFENSTER_ENDE));
        vo.setGeloescht(_rs.getBooleanNN(Scheme.COLUMN_GELOESCHT));
        vo.setFlashSms(_rs.getBooleanNN(Scheme.TELEFON_COLUMN_FLASH_SMS));

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return vo;
    }

    private TelefonVO[] toVOs(ResultSet _rs, boolean _keepResultSet) throws StdException
    {
        List<TelefonVO> al = new ArrayList<TelefonVO>();
        while (_rs.next())
        {
            al.add(toVO(_rs, true));
        }
        TelefonVO[] vos = new TelefonVO[al.size()];
        al.toArray(vos);

        if (!_keepResultSet)
        {
            _rs.close();
        }

        return vos;
    }

    public BaseIdVO create(BaseIdVO _vo) throws StdException
    {
        return createTelefon((TelefonVO) _vo);
    }

    public BaseIdVO update(BaseIdVO _vo) throws StdException
    {
        return updateTelefon((TelefonVO) _vo);
    }

    public void delete(BaseId _id) throws StdException
    {
        deleteTelefon((TelefonId) _id);
    }

    /*
     * 
     * 
     * queries
     */
    /**
     * Legt ein neues Telefon an. Wenn ein Datensatz mit der Rufnummer bereits
     * als gelöscht markiert in der Datenbank gefunden wird dann wird dieser
     * reaktiviert und das "gelöscht" Flag zurückgesetzt.
     * 
     */
    public TelefonVO createTelefon(TelefonVO _vo) throws StdException
    {
        TelefonVO vo = findTelefonByNummer(_vo.getNummer());
        if (vo != null)
        {
            if (vo.getGeloescht())
            {
                undeleteTelefon(vo.getTelefonId());
                _vo.setTelefonId(vo.getTelefonId());
                return updateTelefon(_vo);
            }
            else
            {
                throw new StdException("telefon id=" + vo.getTelefonId()
                                + " nummer=" + vo.getNummer()
                                + " existiert bereits");
            }
        }
        else
        {
            TelefonId id = new TelefonId(dbconnection.nextId());
            PreparedStatement pst = getPstCreateTelefon();
            pst.setTelefonId(1, id);
            pst.setPersonId(2, _vo.getPersonId());
            pst.setTelefonNummer(3, _vo.getNummer());
            pst.setBoolean(4, _vo.getAktiv());
            pst.setUnixTime(5, _vo.getZeitfensterStart());
            pst.setUnixTime(6, _vo.getZeitfensterEnde());
            pst.setBoolean(7, _vo.getFlashSms());
            pst.execute();

            pst.close();
            return findTelefonById(id);
        }
    }

    /**
     * Löscht ein Telefon. Der Datensatz wird *nicht* in der Datenbank gelöscht
     * sondern nur als "gelöscht" markiert.
     * 
     * @param _id
     * @throws StdException
     */
    public void deleteTelefon(TelefonId _id) throws StdException
    {
        PreparedStatement pst = getPstDeleteTelefon();
        pst.setTelefonId(1, _id);
        pst.execute();
        pst.close();
    }

    /**
     * Liefert das Telefon mit der ggb. TelefonId.
     * 
     * @param _telefonId
     * @return
     * @throws StdException
     */
    public TelefonVO findTelefonById(TelefonId _telefonId) throws StdException
    {
        PreparedStatement pst = getPstFindTelefonById();

        pst.setTelefonId(1, _telefonId);

        TelefonVO r = nextToVO(pst.executeQuery(), false);
        pst.close();

        return r;
    }

    /**
     * Liefert das zuerst genutzte, aktive Telefon zurück
     * 
     * @param _personId
     * @return
     * @throws StdException
     */
    public TelefonVO findCurrentTelefonByPersonId(PersonId _personId) throws StdException
    {
        TelefonVO[] telefonVOs = findAktiveTelefoneByPersonId(_personId);

        if (telefonVOs.length > 0)
        {
            return telefonVOs[0];
        }
        else
        {
            return null;
        }
    }

    /**
     * Liefert alle aktiven *und* inaktiven Telefone, die einer Person
     * zugewiesen sind. Wenn keine Telefone gefunden wurden liefert diese
     * Methode ein leeres Array.
     * 
     * @param _personId
     * @return
     * @throws StdException
     */
    public TelefonVO[] findTelefoneByPersonId(PersonId _personId) throws StdException
    {
        PreparedStatement pst = getPstFindTelefoneByPersonId();

        pst.setPersonId(1, _personId);

        TelefonVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Liefert alle aktiven Telefone, die einer Person zugewiesen sind. Wenn
     * keine Telefone gefunden wurden liefert diese Methode ein leeres Array.
     * 
     * @param _personId
     * @return
     * @throws StdException
     */
    public TelefonVO[] findAktiveTelefoneByPersonId(PersonId _personId) throws StdException
    {
        PreparedStatement pst = getPstFindAktiveTelefoneByPersonId();

        pst.setPersonId(1, _personId);

        TelefonVO[] r = toVOs(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Sucht ein Telefon anhand der Telefonnummer.
     * 
     * @param _nummer
     * @return
     * @throws StdException
     */
    public TelefonVO findTelefonByNummer(TelefonNummer _nummer) throws StdException
    {
        PreparedStatement pst = getPstFindTelefonByNummer();
        pst.setTelefonNummer(1, _nummer);
        TelefonVO r = nextToVO(pst.executeQuery(), false);

        pst.close();
        return r;
    }

    /**
     * Filtert aus den angegebenen Personen die heraus, die keine aktive
     * Handynummer besitzen
     * 
     * @param _personVO
     * @return
     * @throws StdException
     */
    public PersonVO[] filterPersonenMitAktiverHandyNummer(PersonVO[] _personVO) throws StdException
    {
        List<PersonVO> list = new ArrayList<PersonVO>();

        for (int i = 0, m = _personVO.length; i < m; i++)
        {
            PersonVO p = _personVO[i];
            if (hatPersonAktiveHandyNummer(p.getPersonId()))
            {
                list.add(p);
            }
        }

        PersonVO[] r = new PersonVO[list.size()];
        list.toArray(r);

        return r;
    }

    /**
     * Setzt das "gelöscht" Flag für ein Telefon zurück.
     * 
     * @param _id
     * @return
     * @throws StdException
     */
    public TelefonVO undeleteTelefon(TelefonId _id) throws StdException
    {
        PreparedStatement pst = getPstUndeleteTelefon();
        pst.setTelefonId(1, _id);
        pst.execute();

        pst.close();
        return findTelefonById(_id);
    }

    /**
     * Ändert ein Telefon.
     * 
     * @param _vo
     * @return
     * @throws StdException
     */
    public TelefonVO updateTelefon(TelefonVO _vo) throws StdException
    {
        PreparedStatement pst = getPstUpdateTelefon();
        pst.setPersonId(1, _vo.getPersonId());
        pst.setTelefonNummer(2, _vo.getNummer());
        pst.setBoolean(3, _vo.getAktiv());
        pst.setUnixTime(4, _vo.getZeitfensterStart());
        pst.setUnixTime(5, _vo.getZeitfensterEnde());
        pst.setBoolean(6, _vo.getFlashSms());
        pst.setTelefonId(7, _vo.getTelefonId());
        pst.execute();

        pst.close();
        return findTelefonById(_vo.getTelefonId());
    }

    /**
     * Liefert zurück, ob die Person mit der angegeben ID 1 oder n aktive (nicht
     * gelöschte und mit 015x, 016x oder 017x beginnende) Handynummern besitzt.
     * 
     * @return
     * @throws StdException
     */
    public boolean hatPersonAktiveHandyNummer(PersonId _personId) throws StdException
    {
        PreparedStatement pst = getPstHatPersonAktiveHandyNummer();
        pst.setPersonId(1, _personId);
        ResultSet rs = pst.executeQuery();

        long r = 0;

        if (rs.next())
        {
            r = rs.getLong(1).longValue();
        }

        rs.close();
        pst.close();

        return (r > 0);
    }

    /*
     * 
     * 
     * 
     * prepared statements
     */
    private PreparedStatement getPstCreateTelefon() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "INSERT INTO "
                        + Scheme.TELEFON_TABLE + " (" + Scheme.COLUMN_ID + ","
                        + Scheme.TELEFON_COLUMN_PERSON_ID + ","
                        + Scheme.TELEFON_COLUMN_NUMMER + ","
                        + Scheme.TELEFON_COLUMN_AKTIV + ","
                        + Scheme.TELEFON_COLUMN_ZEITFENSTER_START + ","
                        + Scheme.TELEFON_COLUMN_ZEITFENSTER_ENDE + ","
                        + Scheme.TELEFON_COLUMN_FLASH_SMS
                        + ") VALUES(?,?,?,?,?,?,?);");
    }

    private PreparedStatement getPstFindTelefonById() throws StdException
    {
        return new PreparedStatement(getDBConnection(),
                        "SELECT * FROM telefon WHERE " + Scheme.COLUMN_ID
                                        + "=?;");
    }

    private PreparedStatement getPstFindTelefonByNummer() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.TELEFON_TABLE + " WHERE "
                        + Scheme.TELEFON_COLUMN_NUMMER + "=?;");
    }

    private PreparedStatement getPstFindTelefoneByPersonId() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.TELEFON_TABLE + " WHERE "
                        + Scheme.TELEFON_COLUMN_PERSON_ID + "=? " + "ORDER BY "
                        + Scheme.TELEFON_COLUMN_NUMMER + ";");
    }

    private PreparedStatement getPstFindAktiveTelefoneByPersonId() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT * FROM "
                        + Scheme.TELEFON_TABLE + " WHERE "
                        + Scheme.TELEFON_COLUMN_PERSON_ID + "=? AND "
                        + Scheme.TELEFON_COLUMN_AKTIV
                        + "=true AND "
                        // 2006-05-08 CST: geloeschte
                        // Telefonnummern nicht verwenden
                        + Scheme.COLUMN_GELOESCHT + "=false " + "ORDER BY "
                        + Scheme.TELEFON_COLUMN_NUMMER + ";");

    }

    private PreparedStatement getPstDeleteTelefon() throws StdException // evtl.
    // auch
    // aktiv=false
    // setzen?!
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.TELEFON_TABLE + " SET "
                        + Scheme.COLUMN_GELOESCHT + "=true WHERE "
                        + Scheme.COLUMN_ID + "=?;");
    }

    private PreparedStatement getPstUndeleteTelefon() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.TELEFON_TABLE + " SET "
                        + Scheme.COLUMN_GELOESCHT + "=false WHERE "
                        + Scheme.COLUMN_ID + "=?;");
    }

    private PreparedStatement getPstUpdateTelefon() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "UPDATE "
                        + Scheme.TELEFON_TABLE + " SET "
                        + Scheme.TELEFON_COLUMN_PERSON_ID + "=?,"
                        + Scheme.TELEFON_COLUMN_NUMMER + "=?,"
                        + Scheme.TELEFON_COLUMN_AKTIV + "=?,"
                        + Scheme.TELEFON_COLUMN_ZEITFENSTER_START + "=?,"
                        + Scheme.TELEFON_COLUMN_ZEITFENSTER_ENDE + "=?, "
                        + Scheme.TELEFON_COLUMN_FLASH_SMS + "=? WHERE "
                        + Scheme.COLUMN_ID + "=?;");
    }

    private PreparedStatement getPstHatPersonAktiveHandyNummer() throws StdException
    {
        return new PreparedStatement(getDBConnection(), "SELECT COUNT(*) FROM "
                        + Scheme.TELEFON_TABLE + "  WHERE "
                        + Scheme.TELEFON_COLUMN_PERSON_ID + " = ? AND ("
                        + Scheme.TELEFON_COLUMN_NUMMER + " LIKE '004915%'  OR "
                        + Scheme.TELEFON_COLUMN_NUMMER + " LIKE '004916%' OR "
                        + Scheme.TELEFON_COLUMN_NUMMER
                        + " LIKE '004917%') AND " + Scheme.COLUMN_GELOESCHT
                        + " = false");
    }
}
