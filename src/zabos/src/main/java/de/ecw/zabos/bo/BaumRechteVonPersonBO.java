package de.ecw.zabos.bo;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.beans.DataBean;
import de.ecw.zabos.frontend.objects.fassade.RolleMitVererbungFassade;
import de.ecw.zabos.sql.cvo.BaumCVO;
import de.ecw.zabos.sql.dao.PersonDAO;
import de.ecw.zabos.sql.dao.RolleDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.RolleVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.OrganisationId;
import de.ecw.zabos.types.id.OrganisationsEinheitId;
import de.ecw.zabos.types.id.RolleId;
import de.ecw.zabos.types.id.SchleifeId;

/**
 * Liefert einen Baum mit Rechten von Personen zurück.
 * 
 * @author ckl
 */
public class BaumRechteVonPersonBO extends BaumViewBO
{
    // Serial
    public final static long serialVersionUID = 1209312249;

    private final static Logger log = Logger
                    .getLogger(BaumRechteVonPersonBO.class);

    private RolleVO[] rollenVO = null;

    private RolleDAO daoRolle = null;

    private PersonDAO daoPerson = null;

    private PersonVO personVO = null;

    private ArrayList<RolleId> alHatRolleInSystem = new ArrayList<RolleId>();

    private ArrayList<RolleId> alHatRolleInCurrentOrganisation = new ArrayList<RolleId>();

    private ArrayList<RolleId> alHatRolleInCurrentOrganisationseinheit = new ArrayList<RolleId>();

    private ArrayList<RolleId> alHatRolleInCurrentSchleife = new ArrayList<RolleId>();

    /**
     * Konstruktor
     * 
     * @param _db
     * @param _person
     */
    public BaumRechteVonPersonBO(final DBResource _db, PersonVO _person)
    {
        super(_db);

        daoRolle = db.getDaoFactory().getRolleDAO();
        daoPerson = db.getDaoFactory().getPersonDAO();

        this.personVO = _person;

        try
        {
            this.rollenVO = daoRolle.findAll();
        }
        catch (StdException e)
        {
            log.error(e);
        }
    }

    /**
     * Liefert den Root-Branch zurück
     * 
     * @return BaumBO
     */
    protected BaumCVO findRootAsBranch()
    {
        DataBean zugewieseneRollen = getZugewieseneRollen(null);
        BaumCVO branch = super.findRootAsBranch();
        branch.setDataBean(zugewieseneRollen);

        return branch;
    }

    /**
     * Liefert den Zweig der Organisation inkl. aller Unterknoten zurück
     * 
     * @param _organisation
     * @return BaumBO
     */
    @Override
    protected BaumCVO findOrganisationAsBranch(OrganisationVO _organisation)
    {
        /*
         * Rollen leeren 2006-06-01 CKL: Vorherige Rollen löschen, ansonsten
         * gibt es Fehler in der Vererbung
         */
        alHatRolleInCurrentOrganisation.clear();
        alHatRolleInCurrentOrganisationseinheit.clear();
        alHatRolleInCurrentSchleife.clear();

        DataBean zugewieseneRollen = getZugewieseneRollen(_organisation
                        .getOrganisationId());

        BaumCVO branch = super.findOrganisationAsBranch(_organisation);
        branch.setDataBean(zugewieseneRollen);
        return branch;
    }

    /**
     * Liefert den Zweig der Organisationseinheit inkl. aller Unterknoten zurück
     * 
     * @param _organisationseinheit
     * @return BaumBO
     */
    @Override
    protected BaumCVO findOrganisationseinheitAsBranch(
                    OrganisationsEinheitVO _organisationseinheit)
    {
        /*
         * 2006-06-01 CKL: Vorherige Rollen löschen, ansonsten gibt es Fehler in
         * der Vererbung
         */
        alHatRolleInCurrentOrganisationseinheit.clear();
        alHatRolleInCurrentSchleife.clear();
        DataBean zugewieseneRollen = getZugewieseneRollen(_organisationseinheit
                        .getOrganisationsEinheitId());

        BaumCVO branch = super
                        .findOrganisationseinheitAsBranch(_organisationseinheit);
        branch.setDataBean(zugewieseneRollen);

        return branch;
    }

    /**
     * Liefert den Zweig zurück, die Schleife besitzt keine Unterknoten
     * 
     * @param _schleife
     * @return BaumBO
     */
    @Override
    protected BaumCVO findSchleifeAsBranch(SchleifeVO _schleife)
    {
        /*
         * 2006-06-01 CKL: Vorherige Rollen löschen, ansonsten gibt es Fehler in
         * der Vererbung
         */
        alHatRolleInCurrentSchleife.clear();
        DataBean zugewieseneRollen = getZugewieseneRollen(_schleife
                        .getSchleifeId());

        BaumCVO branch = super.findSchleifeAsBranch(_schleife);
        branch.setDataBean(zugewieseneRollen);
        return branch;
    }

    /**
     * Liefert die zugewiesene Rollen zu der übergebenen Id
     * 
     * @param _id
     * @return Liefert eine DataBean<RolleMitVererbung> zur�ck
     */
    protected DataBean getZugewieseneRollen(BaseId _id)
    {
        DataBean retDataBeanRollen = new DataBean();
        RolleMitVererbungFassade rolleMitVererbung = null;
        boolean istInRolleInCurrentObject = false;

        if (this.rollenVO != null)
        {
            // Rollen einzeln iterieren
            for (int i = 0, m = rollenVO.length; i < m; i++)
            {
                istInRolleInCurrentObject = false;

                try
                {
                    if (_id instanceof OrganisationId)
                    {
                        // voPersonen =
                        // daoPerson.findPersonenByRolleInOrganisation(voRollen[i].getRolleId(),
                        // (OrganisationId)_id);
                        istInRolleInCurrentObject = daoPerson
                                        .hatPersonRolleInOrganisationNichtVererbt(
                                                        this.personVO.getPersonId(),
                                                        rollenVO[i].getRolleId(),
                                                        (OrganisationId) _id);
                    }
                    else if (_id instanceof OrganisationsEinheitId)
                    {
                        // voPersonen =
                        // daoPerson.findPersonenByRolleInOrganisationseinheit(voRollen[i].getRolleId(),
                        // (OrganisationsEinheitId)_id);
                        istInRolleInCurrentObject = daoPerson
                                        .hatPersonRolleInOrganisationseinheitNichtVererbt(
                                                        this.personVO.getPersonId(),
                                                        rollenVO[i].getRolleId(),
                                                        (OrganisationsEinheitId) _id);
                    }
                    else if (_id instanceof SchleifeId)
                    {
                        // voPersonen =
                        // daoPerson.findPersonenByRolleInSchleife(voRollen[i].getRolleId(),
                        // (SchleifeId)_id);
                        istInRolleInCurrentObject = daoPerson
                                        .hatPersonRolleInSchleifeNichtVererbt(
                                                        this.personVO.getPersonId(),
                                                        rollenVO[i].getRolleId(),
                                                        (SchleifeId) _id);
                    }
                    else
                    {
                        istInRolleInCurrentObject = daoPerson
                                        .hatPersonRolleInSystem(
                                                        this.personVO.getPersonId(),
                                                        rollenVO[i].getRolleId());
                    }

                    // Wenn Person in der Rolle des ggw. Objekts ist, wird sie
                    // hinzugefuegt
                    if (istInRolleInCurrentObject)
                    {
                        setPersonInRolleInObjekt(rollenVO[i].getRolleId(), _id);
                    }

                    if (istPersonMitRolleInBaum(rollenVO[i].getRolleId()))
                    {

                        rolleMitVererbung = new RolleMitVererbungFassade(
                                        rollenVO[i],
                                        (!istRolleNichtVererbt(rollenVO[i]
                                                        .getRolleId(), _id)));
                        retDataBeanRollen.setData(rolleMitVererbung);
                    }
                }
                catch (StdException e)
                {
                    log.error(e);
                }
            }
        }

        return retDataBeanRollen;
    }

    /**
     * Liefert true zurück, wenn die Rolle im aktuellen Zweig des Baumes
     * definiert wurde
     * 
     * @param _rolleId
     * @return true|false
     */
    protected boolean istPersonMitRolleInBaum(RolleId _rolleId)
    {
        if (alHatRolleInSystem.contains(_rolleId))
        {
            return true;
        }

        if (alHatRolleInCurrentOrganisation.contains(_rolleId))
        {
            return true;
        }

        if (alHatRolleInCurrentOrganisationseinheit.contains(_rolleId))
        {
            return true;
        }

        if (alHatRolleInCurrentSchleife.contains(_rolleId))
        {
            return true;
        }

        return false;
    }

    /**
     * Liefert true zurück, wenn die Rolle mit der der Objekt-ID NICHT vererbt
     * ist, sondern direkt an dem Objekt hängt
     * 
     * @param _rolleId
     * @param _baseId
     * @return true|false
     */
    protected boolean istRolleNichtVererbt(RolleId _rolleId, BaseId _baseId)
    {
        if ((_baseId == null) && alHatRolleInSystem.contains(_rolleId))
        {
            return true;
        }

        if ((_baseId instanceof OrganisationId)
                        && alHatRolleInCurrentOrganisation.contains(_rolleId))
        {
            return true;
        }

        if ((_baseId instanceof OrganisationsEinheitId)
                        && alHatRolleInCurrentOrganisationseinheit
                                        .contains(_rolleId))
        {
            return true;
        }

        if ((_baseId instanceof SchleifeId)
                        && alHatRolleInCurrentSchleife.contains(_rolleId))
        {
            return true;
        }

        return false;
    }

    /**
     * Setzt die Rolle für das Objekt als anwesend
     * 
     * @param _rolleId
     * @param _baseId
     */
    protected void setPersonInRolleInObjekt(RolleId _rolleId, BaseId _baseId)
    {
        boolean bSetInSystem = false;
        boolean bSetInO = false;
        boolean bSetInOE = false;
        boolean bSetInS = false;

        // Rolle ist im System gesetzt worden
        if (_baseId == null)
        {
            bSetInSystem = true;
        }

        if (_baseId instanceof OrganisationId)
        {
            bSetInO = true;
        }

        if (_baseId instanceof OrganisationsEinheitId)
        {
            bSetInOE = true;
        }

        if (_baseId instanceof SchleifeId)
        {
            bSetInS = true;
        }

        if (bSetInSystem)
        {
            this.alHatRolleInSystem.add(_rolleId);
            this.alHatRolleInCurrentOrganisation.add(_rolleId);
            this.alHatRolleInCurrentOrganisationseinheit.add(_rolleId);
            this.alHatRolleInCurrentSchleife.add(_rolleId);
        }

        if (bSetInO)
        {
            this.alHatRolleInCurrentOrganisation.add(_rolleId);
            this.alHatRolleInCurrentOrganisationseinheit.add(_rolleId);
            this.alHatRolleInCurrentSchleife.add(_rolleId);
        }

        if (bSetInOE)
        {
            this.alHatRolleInCurrentOrganisationseinheit.add(_rolleId);
            this.alHatRolleInCurrentSchleife.add(_rolleId);
        }

        if (bSetInS)
        {
            this.alHatRolleInCurrentSchleife.add(_rolleId);
        }
    }

    /**
     * Überprüft, ob die Person this.person in dem übergebenen Array vorhanden
     * ist
     * 
     * @param _personen
     * @return true falls die Person in dem Array vorhanden ist
     */
    protected boolean istPersonInObjekt(PersonVO[] _personen)
    {
        if (_personen != null)
        {
            for (int i = 0, m = _personen.length; i < m; i++)
            {
                if (_personen[i].getPersonId().equals(
                                this.personVO.getPersonId()))
                {
                    return true;
                }
            }
        }

        return false;
    }
}
