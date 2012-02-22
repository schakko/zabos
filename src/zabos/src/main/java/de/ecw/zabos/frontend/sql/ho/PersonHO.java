package de.ecw.zabos.frontend.sql.ho;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.objects.BaseControllerAdapter;
import de.ecw.zabos.sql.dao.OrganisationDAO;
import de.ecw.zabos.sql.dao.OrganisationsEinheitDAO;
import de.ecw.zabos.sql.dao.PersonDAO;
import de.ecw.zabos.sql.dao.SchleifenDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.RechtId;

/**
 * HelperObject<br />
 * Enthält Methoden zum Verwalten / Bearbeiten von personenbezeogenen Daten.
 * 
 * @author ckl
 */
public class PersonHO
{
    private final static Logger log = Logger
                    .getLogger(BaseControllerAdapter.class);

    private DBResource db = null;

    private PersonDAO daoPerson = null;

    private SchleifenDAO daoSchleife = null;

    private OrganisationsEinheitDAO daoOrganisationsEinheit = null;

    private OrganisationDAO daoOrganisation = null;

    /**
     * @param _db
     */
    public PersonHO(final DBResource _db)
    {
        this.db = _db;

        this.daoPerson = db.getDaoFactory().getPersonDAO();
        this.daoSchleife = db.getDaoFactory().getSchleifenDAO();
        this.daoOrganisation = db.getDaoFactory().getOrganisationDAO();
        this.daoOrganisationsEinheit = db.getDaoFactory()
                        .getOrganisationsEinheitDAO();
    }

    /**
     * Lieferte eine Liste mit Objekten zurück, denen der Benutzer angehört.<br />
     * Die Objekte können vom Typ OrganisationVO, OrganisationsEinheitVO oder
     * SchleifeVO sein
     * 
     * @param _id
     * @return Liste
     */
    public List<Object> findObjekteMitZugehoerigkeitVonPerson(PersonId _id)
    {
        List<Object> alObjekte = new ArrayList<Object>();

        try
        {
            OrganisationVO[] organisationVO = daoOrganisation
                            .findMitgliedschaftInOrganisationVonPerson(_id);
            OrganisationsEinheitVO[] organisationsEinheitVO = daoOrganisationsEinheit
                            .findMitgliedschaftInOrganisationseinheitenVonPerson(_id);
            SchleifeVO[] schleifeVO = daoSchleife
                            .findMitgliedschaftInSchleifenVonPerson(_id);

            if (organisationVO != null)
            {
                for (int i = 0, m = organisationVO.length; i < m; i++)
                {
                    alObjekte.add(organisationVO[i]);
                }
            }

            if (organisationsEinheitVO != null)
            {
                for (int i = 0, m = organisationsEinheitVO.length; i < m; i++)
                {
                    alObjekte.add(organisationsEinheitVO[i]);
                }
            }

            if (schleifeVO != null)
            {
                for (int i = 0, m = schleifeVO.length; i < m; i++)
                {
                    alObjekte.add(schleifeVO[i]);
                }
            }
        }
        catch (StdException e)
        {
            log.error(e);
        }

        return alObjekte;
    }

    /**
     * Überprüft, ob die Person mit der Id _id das Recht mit der Id _id in der
     * Liste von Objekten besitzt und liefert beim ersten Vorkommen true zurück
     * 
     * @param _alObjekte
     * @param _id
     * @param _rechtId
     * @return
     */
    public boolean hatPersonRechtInObjektListe(List<Object> _alObjekte,
                    PersonId _id, RechtId _rechtId)
    {
        try
        {
            // Person darf alle anderen Personen administrieren
            if (daoPerson.hatPersonRechtInSystem(_id, _rechtId))
            {
                return true;
            }

            if (_alObjekte != null)
            {
                for (int i = 0, m = _alObjekte.size(); i < m; i++)
                {
                    if (_alObjekte.get(i) instanceof OrganisationVO)
                    {
                        if (daoPerson.hatPersonRechtInOrganisation(_id,
                                        _rechtId, ((OrganisationVO) _alObjekte
                                                        .get(i))
                                                        .getOrganisationId()))
                        {
                            return true;
                        }
                    }
                    else if (_alObjekte.get(i) instanceof OrganisationsEinheitVO)
                    {
                        if (daoPerson
                                        .hatPersonRechtInOrganisationseinheit(
                                                        _id,
                                                        _rechtId,
                                                        ((OrganisationsEinheitVO) _alObjekte
                                                                        .get(i))
                                                                        .getOrganisationsEinheitId()))
                        {
                            return true;
                        }
                    }
                    else if (_alObjekte.get(i) instanceof SchleifeVO)
                    {
                        if (daoPerson.hatPersonRechtInSchleife(_id, _rechtId,
                                        ((SchleifeVO) _alObjekte.get(i))
                                                        .getSchleifeId()))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        catch (StdException e)
        {
            log.error(e);
        }

        return false;
    }

    /**
     * Liefert zurück, ob die Person _personId ein bestimmtes Recht in Bezug auf
     * Person _anderePersonId besitzt.
     * 
     * @param _personId
     * @param _rechtId
     * @param _anderePersonId
     *            Wenn null, wird überprüft, ob _personId irgendwo im System das
     *            übergebene Recht besitzt
     * @return
     */
    public boolean isRechtInBezugAufAnderePersonVerfuegbar(PersonId _personId,
                    RechtId _rechtId, PersonId _anderePersonId)
    {
        PersonId bezugsPerson = _personId;

        if (_personId == null)
        {
            log.error("Es wurde ein NULL-Objekt uebergeben.");
        }

        if (_anderePersonId != null)
        {
            bezugsPerson = _anderePersonId;
        }

        List<Object> listObjekte = findObjekteMitZugehoerigkeitVonPerson(bezugsPerson);

        return hatPersonRechtInObjektListe(listObjekte, _personId, _rechtId);
    }
}
