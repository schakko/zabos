package de.ecw.zabos.bo;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.PersonDAO;
import de.ecw.zabos.sql.dao.SchleifenDAO;
import de.ecw.zabos.sql.dao.TelefonDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.BereichInSchleifeVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.types.id.BereichId;
import de.ecw.zabos.types.id.FunktionstraegerId;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.types.id.SchleifeId;

/**
 * BO-Klasse für die Schleifen
 * 
 * @author ckl
 * 
 */
public class SchleifeBO
{
    // Serial
    public final static long serialVersionUID = 1208312249;

    protected SchleifenDAO daoSchleife = null;

    protected TelefonDAO daoTelefon = null;

    protected DBResource dbResource = null;

    private final static Logger log = Logger.getLogger(SchleifeBO.class);

    public SchleifeBO(final DBResource _dbResource)
    {
        this.dbResource = _dbResource;

        daoSchleife = dbResource.getDaoFactory().getSchleifenDAO();
        daoTelefon = dbResource.getDaoFactory().getTelefonDAO();
    }

    /**
     * Liefert zurück, ob die Schleife oder Folgeschleifen-Liste sich in einer
     * zirkulären Abhängigkeit befindet.
     * 
     * @param _schleifeId
     * @return
     */
    public boolean istSchleifeInZirkulaererAbhaengigkeit(SchleifeId _schleifeId)
    {
        return (findFolgeSchleifenListe(_schleifeId,
                        new ArrayList<SchleifeVO>()) == FOLGESCHLEIFEN_STATUS.OK);
    }

    /**
     * <ul>
     * <li>{@link #OK} Die Schleifen befinden sich in keiner zirkulären
     * Abhängigkeit</li>
     * <li>{@link #ERSTES_ELEMENT_ZIRKULAER} Die Schleife mit der übergebenen ID
     * ist zirkulär und wird wo anders aufgerufen</li>
     * <li>{@link #UNTER_ELEMENT_ZIRKULAER} Eines der Elemente innerhalb der
     * Schleife ist zirkulär</li>
     * </ul
     * 
     * @author ckl
     * 
     */
    public static enum FOLGESCHLEIFEN_STATUS
    {
        OK, ERSTES_ELEMENT_ZIRKULAER, UNTER_ELEMENT_ZIRKULAER
    };

    /**
     * Liefert eine Liste mit den Schleifen zurück, wie sie nacheinander
     * alarmiert werden würden. Wenn die Schleife sich in einer zirkulären
     * Abhängigkeit befindet, ist das letzte Element der Liste die Schleife mit
     * der übergebenen SchleifenId.<br />
     * 
     * @param _schleifeId
     * @return
     */
    public FOLGESCHLEIFEN_STATUS findFolgeSchleifenListe(
                    SchleifeId _schleifeId,
                    List<SchleifeVO> alSchleifenReihenfolge)
    {
        if (alSchleifenReihenfolge == null)
        {
            alSchleifenReihenfolge = new ArrayList<SchleifeVO>();
        }

        try
        {
            SchleifeVO schleifeVO = daoSchleife.findSchleifeById(_schleifeId);

            while ((schleifeVO != null)
                            && (schleifeVO.getFolgeschleifeId() != null))
            {
                schleifeVO = daoSchleife.findSchleifeById(schleifeVO
                                .getFolgeschleifeId());

                if (schleifeVO.getSchleifeId().equals(_schleifeId))
                {
                    return FOLGESCHLEIFEN_STATUS.ERSTES_ELEMENT_ZIRKULAER;
                }

                if (alSchleifenReihenfolge.contains(schleifeVO))
                {
                    log
                                    .error("Die Schleife "
                                                    + schleifeVO
                                                                    .getDisplayName()
                                                    + " befindet sich in einer zirkulären Abhängigkeit!");

                    return FOLGESCHLEIFEN_STATUS.UNTER_ELEMENT_ZIRKULAER;
                }

                alSchleifenReihenfolge.add(schleifeVO);
            }
        }
        catch (StdException e)
        {
            log.error(e);
        }

        return FOLGESCHLEIFEN_STATUS.OK;
    }

    /**
     * Interface zum Überprüfen, ob eine Person hinzufügbar ist
     * 
     * @author ckl
     */
    public interface IIsPersonAddable
    {
        /**
         * Liefert zurück, ob die Person hinzufügbar ist
         * 
         * @param _personVO
         * @return
         */
        public boolean isAddable(PersonVO _personVO);
    }

    /**
     * Methode zum Finden von Personen mit Empfangsberechtigung innerhalb einer
     * Schleife. Wenn {@link IIsPersonAddable} true liefert, wird diese Person
     * der Liste hinzugefügt.
     * 
     * @param _schleifeId
     * @param _cb
     * @return
     * @throws StdException
     */
    public PersonVO[] findPersonenMitEmpfangsberechtigungInSchleifeCallback(
                    SchleifeId _schleifeId, IIsPersonAddable _cb) throws StdException
    {
        PersonDAO personDAO = dbResource.getDaoFactory().getPersonDAO();
        ArrayList<PersonVO> alPersonen = new ArrayList<PersonVO>();

        PersonVO[] personen = personDAO.findPersonenByRechtInSchleife(
                        RechtId.ALARMBENACHRICHTIGUNG_EMPFANGEN, _schleifeId);

        for (int i = 0, m = personen.length; i < m; i++)
        {
            PersonVO personVO = personen[i];

            if (_cb.isAddable(personVO))
            {
                alPersonen.add(personVO);
            }
        }

        PersonVO[] r = new PersonVO[alPersonen.size()];

        r = alPersonen.toArray(r);

        return r;
    }

    /**
     * Findet die Personen, die eine Alarmberichtigung in der übergebenen
     * Schleife besitzen und die nötige Funktionsträger- und
     * Bereichs-Kombination haben.
     * 
     * @param _schleifeId
     * @param _bereichId
     * @param _funktionstraegerId
     * @return
     * @throws StdException
     */
    public PersonVO[] findPersonenMitEmpfangsberechtigungBySchleifeAndBereichAndFunktionstraeger(
                    final SchleifeId _schleifeId, final BereichId _bereichId,
                    final FunktionstraegerId _funktionstraegerId) throws StdException
    {
        return findPersonenMitEmpfangsberechtigungInSchleifeCallback(
                        _schleifeId, new IIsPersonAddable()
                        {

                            public boolean isAddable(PersonVO personVO)
                            {
                                return (_funktionstraegerId.equals(personVO
                                                .getFunktionstraegerId()) && _bereichId
                                                .equals(personVO.getBereichId()));
                            }
                        });
    }

    /**
     * Findet alle Personen innerhalb einer Schleife, die zwar
     * empfangsberechtigt sind, wo deren Funktionsträger-/Bereichs-Kombination
     * aber nicht in den übergebenen {@link BereichInSchleifeVO} existiert.<br />
     * Damit lässt sich heraus finden, welche Personen zusätzlich alarmiert
     * werden sollen.
     * 
     * @param _schleifeId
     * @param _bereichInSchleifenVO
     * @return
     * @throws StdException
     */
    public PersonVO[] findPersonenMitEmpfangsberechtigungOhneKonkrekteZurodnung(
                    final SchleifeId _schleifeId,
                    final BereichInSchleifeVO[] _bereichInSchleifenVO) throws StdException
    {
        final List<String> listKombination = new ArrayList<String>();

        for (int i = 0, m = _bereichInSchleifenVO.length; i < m; i++)
        {
            listKombination.add(_bereichInSchleifenVO[i].getBereichId() + "_"
                            + _bereichInSchleifenVO[i].getFunktionstraegerId());
        }

        return findPersonenMitEmpfangsberechtigungInSchleifeCallback(
                        _schleifeId, new IIsPersonAddable()
                        {

                            public boolean isAddable(PersonVO personVO)
                            {
                                return (!listKombination
                                                .contains(personVO
                                                                .getBereichId()
                                                                + "_"
                                                                + personVO
                                                                                .getFunktionstraegerId()));
                            }
                        });
    }
}
