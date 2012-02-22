package de.ecw.zabos.frontend.controllers;

import de.ecw.zabos.bo.BaumRechteVonPersonBO;
import de.ecw.zabos.bo.BaumViewBO;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.Navigation;
import de.ecw.zabos.frontend.Parameters;
import de.ecw.zabos.frontend.objects.BaseControllerAdapter;
import de.ecw.zabos.frontend.ressources.RequestResources;
import de.ecw.zabos.sql.cvo.BaumCVO;
import de.ecw.zabos.sql.dao.OrganisationDAO;
import de.ecw.zabos.sql.dao.OrganisationsEinheitDAO;
import de.ecw.zabos.sql.dao.PersonDAO;
import de.ecw.zabos.sql.dao.RolleDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.tao.BenutzerVerwaltungTAO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.types.id.PersonId;

/**
 * Controller für die Hierarchien
 * 
 * @author ckl
 */
public class HierarchieController extends BaseControllerAdapter
{
    // Serial
    public final static long serialVersionUID = 1209312049;

    // Logger-Instanz
    OrganisationDAO daoO = null;

    OrganisationsEinheitDAO daoOE = null;

    BenutzerVerwaltungTAO taoBV = null;

    PersonDAO daoPerson = null;

    RolleDAO daoRolle = null;

    public HierarchieController(final DBResource db)
    {
        super(db);

        // Verzeichnis mit den Templates setzen
        this.setActionDir(Navigation.ACTION_DIR_HIERARCHIE);

        // TAO/DAO-Factory initalisieren
        taoBV = db.getTaoFactory().getBenutzerVerwaltungTAO();
        daoPerson = db.getDaoFactory().getPersonDAO();
        daoRolle = db.getDaoFactory().getRolleDAO();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.objects.BaseControllerAdapter#setRequestIds(de.
     * ecw.zabos.frontend.ressources.RequestResources)
     */
    public void setRequestIds(final RequestResources req)
    {
        // Person-Id auflösen
        if (req.getServletRequest().getParameter(Parameters.PERSON_ID) != null)
        {
            req.setId(Parameters.PERSON_ID, req
                            .getLongForParam(Parameters.PERSON_ID));
        }
        else
        {
            req.setId(Parameters.PERSON_ID, 0);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.objects.BaseControllerAdapter#setViewData(de.ecw
     * .zabos.frontend.ressources.RequestResources)
     */
    public void setViewData(final RequestResources req)
    {
        PersonVO personVO = null;
        BaumViewBO treeView = req.getDbResource().getBoFactory()
                        .getBaumViewBO();

        req.getServletRequest().setAttribute(Parameters.NAVIGATION_TREE,
                        treeView.findTreeView());

        /**
         * Eine Organisations-Id ist gesetzt
         */
        if (req.getId(Parameters.PERSON_ID) > 0)
        {
            try
            {
                personVO = daoPerson.findPersonById(new PersonId(req
                                .getId(Parameters.PERSON_ID)));

                if (personVO != null)
                {
                    req.getUserBean().setCtxPerson(personVO);
                    // Rollen-/Personen-Zuweisungen
                    final BaumRechteVonPersonBO boBaum = new BaumRechteVonPersonBO(
                                    req.getDbResource(), personVO);
                    final BaumCVO voBaum = boBaum.findTreeView();

                    req.getServletRequest().setAttribute(
                                    Parameters.HIERARCHIE_ROLLEN_TREE, voBaum);
                }
                else
                {
                    req
                                    .getErrorBean()
                                    .addMessage(
                                                    "Die Person mit der ID "
                                                                    + req
                                                                                    .getId(Parameters.PERSON_ID)
                                                                    + " konnte nicht gefunden werden.");
                }
            }
            catch (StdException e)
            {
                req.getErrorBean().addMessage(e);
            }
        }

        // Organisation setzen
        req.getServletRequest().setAttribute(Parameters.OBJ_PERSON, personVO);
    }

}
