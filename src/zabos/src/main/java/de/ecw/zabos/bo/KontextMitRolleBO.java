package de.ecw.zabos.bo;

import org.apache.log4j.Logger;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.sql.vo.KontextMitRolleVO;
import de.ecw.zabos.frontend.types.KontextType;
import de.ecw.zabos.sql.dao.OrganisationDAO;
import de.ecw.zabos.sql.dao.OrganisationsEinheitDAO;
import de.ecw.zabos.sql.dao.PersonDAO;
import de.ecw.zabos.sql.dao.RolleDAO;
import de.ecw.zabos.sql.dao.SchleifenDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.tao.RechteTAO;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.RolleVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.types.id.OrganisationId;
import de.ecw.zabos.types.id.OrganisationsEinheitId;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.types.id.RolleId;
import de.ecw.zabos.types.id.SchleifeId;

/**
 * 
 * @author ckl
 * 
 */
public class KontextMitRolleBO
{
    private RolleDAO daoRolle = null;

    private SchleifenDAO daoSchleife = null;

    private OrganisationDAO daoO = null;

    private OrganisationsEinheitDAO daoOE = null;

    private PersonDAO daoPerson = null;

    private RechteTAO taoRecht = null;

    private final static Logger log = Logger.getLogger(KontextMitRolleBO.class);

    /**
     * Konstrutkor
     * 
     * @param _db
     */
    public KontextMitRolleBO(DBResource _db)
    {
        daoRolle = _db.getDaoFactory().getRolleDAO();
        daoSchleife = _db.getDaoFactory().getSchleifenDAO();
        daoO = _db.getDaoFactory().getOrganisationDAO();
        daoOE = _db.getDaoFactory().getOrganisationsEinheitDAO();
        daoPerson = _db.getDaoFactory().getPersonDAO();
        taoRecht = _db.getTaoFactory().getRechteTAO();
    }

    /**
     * Fügt die Person mit der angegebenen Id und dem festgelegtem Recht in den
     * zugehörigen Kontext hinzu.
     * 
     * @param _personId
     *            Id der Person, deren Rolle überprüft werden soll
     * @param _kontextType
     *            Typ des Kontext
     * @param _idKontext
     *            Id des Kontext (also OrganisationseinheitId, OrganisationId,
     *            SchleifeId oder null (für System))
     * @param _rolleId
     *            Id der Rolle
     * @return boolean true|false
     */
    public boolean addPersonInRolleInSpeziellemKontext(PersonId _personId,
                    int _kontextType, long _idKontext, RolleId _rolleId)
    {
        boolean bPersonHinzugefuegt = false;

        if (_personId == null)
        {
            log.error("_personId ist null");
            return false;
        }

        if (_rolleId == null)
        {
            log.error("rolleId ist null");
            return false;
        }

        if ((_kontextType != KontextType.SYSTEM) && (_idKontext == 0))
        {
            log
                            .error("_kontextType ist *nicht* SYSTEM und *kein* _idKontext gegeben.");
            return false;
        }

        if (_kontextType == KontextType.SYSTEM)
        {
            bPersonHinzugefuegt = taoRecht.addPersonInRolleToSystem(_personId,
                            _rolleId);
        }
        else if (_kontextType == KontextType.ORGANISATION)
        {
            bPersonHinzugefuegt = taoRecht
                            .addPersonInRolleToOrganisation(_personId,
                                            _rolleId, new OrganisationId(
                                                            _idKontext));
        }
        else if (_kontextType == KontextType.ORGANISATIONSEINHEIT)
        {
            bPersonHinzugefuegt = taoRecht
                            .addPersonInRolleToOrganisationseinheit(_personId,
                                            _rolleId,
                                            new OrganisationsEinheitId(
                                                            _idKontext));
        }
        else if (_kontextType == KontextType.SCHLEIFE)
        {
            bPersonHinzugefuegt = taoRecht.addPersonInRolleToSchleife(
                            _personId, _rolleId, new SchleifeId(_idKontext));
        }
        else
        {
            log.error("unknown _kontexType: " + _kontextType);
        }

        log.debug("_personId " + _personId.getLongValue()
                        + " wurde der Rolle (Id " + _rolleId.getLongValue()
                        + ") im Kontext " + KontextType.getName(_kontextType)
                        + " (Id " + _idKontext + ") hinzugefuegt: "
                        + Boolean.valueOf(bPersonHinzugefuegt).toString());

        return bPersonHinzugefuegt;
    }

    /**
     * Überprüft, ob die Person die angegebene Rolle im Kontext besitzt oder
     * nicht
     * 
     * @param _personId
     *            Id der Person, deren Rolle überprüft werden soll
     * @param _kontextType
     *            Typ des Kontext
     * @param _idKontext
     *            Id des Kontext (also OrganisationseinheitId, OrganisationId,
     *            SchleifeId oder null (für System))
     * @param _rolleId
     *            Id der Rolle
     * @return boolean true|false
     */
    public boolean hatPersonRolleInSpeziellemKontext(PersonId _personId,
                    int _kontextType, long _idKontext, RolleId _rolleId)
    {
        boolean bHasRolle = false;
        RolleVO[] voKompatibleRollen = null;

        if (_personId == null)
        {
            log.error("_personId ist null");
            return false;
        }

        if (_rolleId == null)
        {
            log.error("rolleId ist null");
            return false;
        }

        if ((_kontextType != KontextType.SYSTEM) && (_idKontext == 0))
        {
            log
                            .error("_kontextType ist *nicht* SYSTEM und *kein* _idKontext gegeben.");
            return false;
        }

        if (_kontextType == KontextType.SYSTEM)
        {
            voKompatibleRollen = taoRecht
                            .findKompatibleRollenByPersonInSystem(_personId);
        }
        else if (_kontextType == KontextType.ORGANISATION)
        {
            voKompatibleRollen = taoRecht
                            .findKompatibleRollenByPersonInOrganisation(
                                            _personId, new OrganisationId(
                                                            _idKontext));
        }
        else if (_kontextType == KontextType.ORGANISATIONSEINHEIT)
        {
            voKompatibleRollen = taoRecht
                            .findKompatibleRollenByPersonInOrganisationsEinheit(
                                            _personId,
                                            new OrganisationsEinheitId(
                                                            _idKontext));
        }
        else if (_kontextType == KontextType.SCHLEIFE)
        {
            voKompatibleRollen = taoRecht
                            .findKompatibleRollenByPersonInSchleife(_personId,
                                            new SchleifeId(_idKontext));
        }
        else
        {
            log.error("Unbekannter _kontexType: " + _kontextType);
        }

        // Rollen gefunden
        if (voKompatibleRollen != null)
        {
            for (int i = 0, m = voKompatibleRollen.length; i < m; i++)
            {
                if (voKompatibleRollen[i].getBaseId().getLongValue() == _rolleId
                                .getLongValue())
                {
                    bHasRolle = true;
                }
            }
        }

        log.debug("_personId " + _personId.getLongValue() + " hat Rolle (Id "
                        + _rolleId.getLongValue() + ") in Kontext "
                        + KontextType.getName(_kontextType) + " (Id "
                        + _idKontext + "): "
                        + Boolean.valueOf(bHasRolle).toString());

        return bHasRolle;
    }

    /**
     * Liefert boolean true oder false zurück, ob eine Person ein Recht in einem
     * Kontext besitzt. Ist sehr abstrahiert.
     * 
     * @param _personId
     *            Id der Person, deren Recht überprüft werden soll
     * @param _kontextType
     *            Typ des Kontext
     * @param _idKontext
     *            Id des Kontext (also OrganisationseinheitId, OrganisationId,
     *            SchleifeId oder null (für System))
     * @param _rechtId
     *            Id des Rechts
     * @return boolean true|false
     */
    public boolean hatPersonRechtInSpeziellemKontext(PersonId _personId,
                    int _kontextType, long _idKontext, RechtId _rechtId)
    {
        boolean bHasRight = false;

        if (_personId == null)
        {
            log.error("_personId ist null");
            return false;
        }

        if (_rechtId == null)
        {
            log.error("rechtId ist null");
            return false;
        }

        if ((_kontextType != KontextType.SYSTEM) && (_idKontext == 0))
        {
            log
                            .error("_kontextType ist *nicht* SYSTEM und *kein* _idKontext gegeben.");
            return false;
        }

        try
        {
            if (_kontextType == KontextType.SYSTEM)
            {
                bHasRight = daoPerson.hatPersonRechtInSystem(_personId,
                                _rechtId);
            }
            else if (_kontextType == KontextType.ORGANISATION)
            {
                bHasRight = daoPerson.hatPersonRechtInOrganisation(_personId,
                                _rechtId, new OrganisationId(_idKontext));
            }
            else if (_kontextType == KontextType.ORGANISATIONSEINHEIT)
            {
                bHasRight = daoPerson.hatPersonRechtInOrganisationseinheit(
                                _personId, _rechtId,
                                new OrganisationsEinheitId(_idKontext));
            }
            else if (_kontextType == KontextType.SCHLEIFE)
            {
                bHasRight = daoPerson.hatPersonRechtInSchleife(_personId,
                                _rechtId, new SchleifeId(_idKontext));
            }
            else
            {
                log.error("Unbekannter _kontexType: " + _kontextType);
            }
        }
        catch (StdException e)
        {
            log
                            .error("Fehler waehrend des Ueberpruefens der Rechte einer Person im Kontext: "
                                            + e.getMessage());
        }

        log.debug("_personId hat Recht (Id " + _rechtId.getLongValue()
                        + ") in Kontext " + KontextType.getName(_kontextType)
                        + " (Id " + _idKontext + "): "
                        + Boolean.valueOf(bHasRight).toString());

        return bHasRight;
    }

    /**
     * Liefert ein Objekt zurück, dass den Namen des Kontexts, der Id und der
     * Rolle enthält
     * 
     * @param _kontextType
     *            Typ des Kontext, siehe KontextType
     * @param _idKontext
     *            Id des Kontext (also OrganisationseinheitId, OrganisationId
     *            oder SchleifeId)
     * @param _idRolle
     *            Id der Rolle
     * @throws StdException
     * @return
     */
    public KontextMitRolleVO findKontextMitRolle(int _kontextType,
                    long _idKontext, long _idRolle) throws StdException
    {
        KontextMitRolleVO voRolleMitKontext = new KontextMitRolleVO();
        voRolleMitKontext.setKontextType(new KontextType(_kontextType));
        RolleVO voRolle = null;
        Object voKontext = null;

        voRolle = daoRolle.findRolleById(new RolleId(_idRolle));

        if (_kontextType == KontextType.ORGANISATION)
        {
            voKontext = daoO
                            .findOrganisationById(new OrganisationId(_idKontext));

            if (null != voKontext)
            {
                voRolleMitKontext.setName(((OrganisationVO) voKontext)
                                .getName());
            }
        }

        if (_kontextType == KontextType.ORGANISATIONSEINHEIT)
        {
            voKontext = daoOE
                            .findOrganisationsEinheitById(new OrganisationsEinheitId(
                                            _idKontext));

            if (null != voKontext)
            {
                voRolleMitKontext.setName(((OrganisationsEinheitVO) voKontext)
                                .getName());
            }
        }

        if (_kontextType == KontextType.SCHLEIFE)
        {
            voKontext = daoSchleife
                            .findSchleifeById(new SchleifeId(_idKontext));

            if (null != voKontext)
            {
                voRolleMitKontext.setName(((SchleifeVO) voKontext).getName());
            }
        }

        if (null != voKontext)
        {
            voRolleMitKontext.setKontextId(_idKontext);
        }

        if (null != voRolle)
        {
            voRolleMitKontext.setRollenName(voRolle.getName());
            voRolleMitKontext.setRolleId(_idRolle);
        }

        return voRolleMitKontext;
    }
}
