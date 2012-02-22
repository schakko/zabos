package de.ecw.zabos.bo;

import org.apache.log4j.Logger;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.cvo.PersonMitRollenCVO;
import de.ecw.zabos.sql.dao.OrganisationDAO;
import de.ecw.zabos.sql.dao.OrganisationsEinheitDAO;
import de.ecw.zabos.sql.dao.PersonDAO;
import de.ecw.zabos.sql.dao.PersonMitRollenDAO;
import de.ecw.zabos.sql.dao.RolleDAO;
import de.ecw.zabos.sql.dao.SchleifenDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.OrganisationId;
import de.ecw.zabos.types.id.OrganisationsEinheitId;
import de.ecw.zabos.types.id.SchleifeId;

public class PersonenMitRollenBO
{
    private final static Logger log = Logger
                    .getLogger(PersonenMitRollenBO.class);

    protected PersonDAO daoPerson = null;

    protected RolleDAO daoRollen = null;

    protected PersonMitRollenDAO daoPersonMitRollen = null;

    SchleifenDAO daoSchleife = null;

    OrganisationsEinheitDAO daoOE = null;

    OrganisationDAO daoO = null;

    /**
     * Konstruktor
     * 
     * @param _db
     */
    public PersonenMitRollenBO(DBResource _db)
    {
        daoPerson = _db.getDaoFactory().getPersonDAO();
        daoRollen = _db.getDaoFactory().getRolleDAO();
        daoSchleife = _db.getDaoFactory().getSchleifenDAO();
        daoOE = _db.getDaoFactory().getOrganisationsEinheitDAO();
        daoO = _db.getDaoFactory().getOrganisationDAO();
        daoPersonMitRollen = _db.getDaoFactory().getPersonMitRollenDAO();
    }

    /**
     * Wrapper für das Laden der Personen
     * 
     * @param _id
     * @return
     * @throws StdException
     */
    public PersonMitRollenCVO[] getPersonenMitVererbtenRollen(BaseId _id)
    {
        PersonMitRollenCVO[] r = new PersonMitRollenCVO[0];

        try
        {
            if (_id == null)
            {
                r = daoPersonMitRollen.findPersonenMitRollenInSystem();
            }
            else if (_id instanceof OrganisationId)
            {
                r = daoPersonMitRollen
                                .findPersonenMitVererbtenRollenInOrganisation((OrganisationId) _id);
            }
            else if (_id instanceof OrganisationsEinheitId)
            {
                r = daoPersonMitRollen
                                .findPersonenMitRollenInOrganisationseinheit((OrganisationsEinheitId) _id);
            }
            else if (_id instanceof SchleifeId)
            {
                r = daoPersonMitRollen
                                .findPersonenMitRollenInSchleife((SchleifeId) _id);
            }
        }
        catch (StdException e)
        {
            log.error(e);
        }

        // 2012-02-10 CKL #1952: Personen sortieren
        sortPersonenMitRollenByNachnameVorname(r);
        return r;
    }

    /**
     * Liefert die Personen zurück, die *oberhalb* eines Objekts zugewiesen
     * worden:
     * <ul>
     * <li>_id = null oder vom Typ {@link OrganisationId}: Alle Personen, die im
     * System Rollen besitzen</li>
     * <li>_id vom Typ {@link SchleifeId}: Alle Personen, die der
     * Organisationseinheit oder der Organisation der Schleife angehören oder
     * aber Rollen im System besitzen</li>
     * <li>_id vom Typ {@link OrganisationsEinheitId}: Alle Personen, die der
     * Organisation der Organisationseinheit angehören oder Rollen im System
     * besitzen</li>
     * </ul>
     * 
     * @param _id
     * @return
     */
    public PersonMitRollenCVO[] getPersonenMitVererbtenRollenAusUebergeordnetenEinheiten(
                    BaseId _id)
    {
        PersonMitRollenCVO[] r = new PersonMitRollenCVO[0];

        try
        {
            if (_id == null || (_id instanceof OrganisationId))
            {
                r = daoPersonMitRollen.findPersonenMitVererbtenRollenInSystem();
            }
            else if (_id instanceof SchleifeId)
            {
                SchleifeVO schleifeVO = daoSchleife
                                .findSchleifeById((SchleifeId) _id);
                OrganisationsEinheitVO organisationseinheitVO = daoOE
                                .findOrganisationsEinheitById(schleifeVO
                                                .getOrganisationsEinheitId());

                r = daoPersonMitRollen
                                .findPersonenMitVererbtenRollenInOrganisationsenheit(
                                                organisationseinheitVO
                                                                .getOrganisationId(),
                                                organisationseinheitVO
                                                                .getOrganisationsEinheitId());

            }
            else if (_id instanceof OrganisationsEinheitId)
            {
                OrganisationsEinheitVO oe = daoOE
                                .findOrganisationsEinheitById((OrganisationsEinheitId) _id);

                r = daoPersonMitRollen
                                .findPersonenMitVererbtenRollenInOrganisation(oe
                                                .getOrganisationId());
            }
        }
        catch (StdException e)
        {
            log.error(e);
        }

        // 2012-02-10 CKL #1952: Personen sortieren
        sortPersonenMitRollenByNachnameVorname(r);
        return r;
    }

    /**
     * Sortiert ein Array von PersonenMitRollen nach "Nachname Vorname"
     * 
     * @param _personen
     */
    public static void sortPersonenMitRollenByNachnameVorname(
                    PersonMitRollenCVO[] _personen)
    {
        if (_personen.length > 1)
        {
            boolean bSwapped;
            do
            {
                bSwapped = false;
                PersonMitRollenCVO l = _personen[0]; // PersonVO l =
                // _personen[0];

                // String lname = l.getNachname()+ " " + l.getVorname();
                String lname = (l.getPerson().getNachname().toLowerCase() + " " + l
                                .getPerson().getVorname().toLowerCase())
                                .replace('ä', 'a').replace('ö', 'o')
                                .replace('ü', 'u').replace('ß', 's');

                for (int i = 1; i < _personen.length; i++)
                {
                    PersonMitRollenCVO t = _personen[i]; // PersonVO t =
                    // _personen[i];
                    String rname = (t.getPerson().getNachname().toLowerCase()
                                    + " " + t.getPerson().getVorname()
                                    .toLowerCase()).replace('ä', 'a')
                                    .replace('ö', 'o').replace('ü', 'u')
                                    .replace('ß', 's');
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
}
