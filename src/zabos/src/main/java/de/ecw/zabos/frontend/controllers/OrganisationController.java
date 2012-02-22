package de.ecw.zabos.frontend.controllers;

import org.apache.log4j.Logger;

import de.ecw.zabos.bo.BaumViewBO;
import de.ecw.zabos.bo.PersonenMitRollenBO;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.FormObject;
import de.ecw.zabos.frontend.FormValidator;
import de.ecw.zabos.frontend.Navigation;
import de.ecw.zabos.frontend.Parameters;
import de.ecw.zabos.frontend.beans.DataBean;
import de.ecw.zabos.frontend.controllers.helpers.deletedobjects.CheckExistingObject;
import de.ecw.zabos.frontend.controllers.helpers.deletedobjects.IOnRenameDeletedObject;
import de.ecw.zabos.frontend.controllers.helpers.deletedobjects.PropertyHandlerNameImpl;
import de.ecw.zabos.frontend.objects.BaseControllerAdapter;
import de.ecw.zabos.frontend.ressources.RequestResources;
import de.ecw.zabos.frontend.sql.ho.AccessControllerHO;
import de.ecw.zabos.sql.dao.OrganisationDAO;
import de.ecw.zabos.sql.dao.OrganisationsEinheitDAO;
import de.ecw.zabos.sql.dao.PersonDAO;
import de.ecw.zabos.sql.dao.RolleDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.tao.BenutzerVerwaltungTAO;
import de.ecw.zabos.sql.tao.RechteTAO;
import de.ecw.zabos.sql.vo.BaseIdVO;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.RolleVO;
import de.ecw.zabos.types.id.OrganisationId;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.types.id.RolleId;

/**
 * Controller für die Organisationen
 * 
 * @author ckl
 */
public class OrganisationController extends BaseControllerAdapter
{
    private static final String DO_REMOVE_ROLLE_FROM_PERSON = "doRemoveRolleFromPerson";

    private static final String DO_UPDATE_ROLLEN_KONTEXT = "doUpdateRollenKontext";

    public static final String DO_DELETE_ORGANISATION = "doDeleteOrganisation";

    public static final String DO_UPDATE_ORGANISATION = "doUpdateOrganisation";

    // Serial
    final static long serialVersionUID = 1209312049;

    protected OrganisationDAO daoO = null;

    protected OrganisationsEinheitDAO daoOE = null;

    protected BenutzerVerwaltungTAO taoBV = null;

    protected PersonDAO daoPerson = null;

    protected RolleDAO daoRolle = null;

    protected PersonenMitRollenBO boPersonenMitRollen = null;

    protected RechteTAO taoRechte = null;

    // Logger-Instanz
    private final static Logger log = Logger
                    .getLogger(OrganisationController.class);

    /**
     * Konstruktor
     */
    public OrganisationController(DBResource dbResource)
    {
        super(dbResource);

        // Verzeichnis mit den Templates setzen
        this.setActionDir(Navigation.ACTION_DIR_ORGANISATION);

        daoO = dbResource.getDaoFactory().getOrganisationDAO();
        daoOE = dbResource.getDaoFactory().getOrganisationsEinheitDAO();
        taoBV = dbResource.getTaoFactory().getBenutzerVerwaltungTAO();
        daoPerson = dbResource.getDaoFactory().getPersonDAO();
        daoRolle = dbResource.getDaoFactory().getRolleDAO();
        boPersonenMitRollen = dbResource.getBoFactory()
                        .getPersonenMitRollenBO();
        taoRechte = dbResource.getTaoFactory().getRechteTAO();

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.objects.BaseControllerAdapter#setRequestIds(de.
     * ecw.zabos.frontend.ressources.RequestResources)
     */
    @Override
    public void setRequestIds(final RequestResources req)
    {
        /*
         * Organisations-ID aufloesen Bevorzugt wird der uebergebene Parameter
         * Organisation-ID Ist dieser nicht gesetzt, wird ueberprueft, ob eine
         * Organisation ausgesucht ist Ansonsten wird davon ausgegangen, dass
         * eine neue Organisaiton erstellt wird
         */
        if (req.getServletRequest().getParameter(Parameters.O_ID) != null)
        {
            req.setId(Parameters.O_ID, req.getLongForParam(Parameters.O_ID));
        }
        else if (req.getUserBean().getCtxO() != null)
        {
            req.setId(Parameters.O_ID, req.getUserBean().getCtxO().getBaseId()
                            .getLongValue());
        }
        else
        {
            req.setId(Parameters.O_ID, 0);
        }

        if (req.getServletRequest().getParameter(Parameters.PERSON_ID) != null)
        {
            req.setId(Parameters.PERSON_ID,
                            req.getLongForParam(Parameters.PERSON_ID));
        }
        // Rolle-Id auflösen
        if (req.getServletRequest().getParameter(Parameters.ROLLE_ID) != null)
        {
            req.setId(Parameters.ROLLE_ID,
                            req.getLongForParam(Parameters.ROLLE_ID));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.objects.BaseControllerAdapter#processACL(de.ecw
     * .zabos.frontend.ressources.RequestResources)
     */
    @Override
    public boolean processACL(final RequestResources req)
    {
        if (!super.processACL(req))
        {
            return false;
        }

        // Zugriffe auf die einzelnen Ressourcen setzen
        final AccessControllerHO accessController = req.buildAccessController();
        accessController.update(req.getBaseId(Parameters.O_ID,
                        new OrganisationId(0)));

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.objects.BaseControllerAdapter#setViewData(de.ecw
     * .zabos.frontend.ressources.RequestResources)
     */
    @Override
    public void setViewData(final RequestResources req)
    {
        OrganisationVO voOrganisation = null;
        RolleVO voRolle = null;
        DataBean beanDataOE = new DataBean();
        DataBean beanPersonenAssigned = new DataBean();
        DataBean beanDataKompatibleRollen = new DataBean();
        DataBean beanPersonenAlle = new DataBean();
        DataBean beanPersonenMitRollen = new DataBean();
        PersonVO[] voSortedPersonen = null;
        DataBean beanPersonenMitVererbtenRollen = new DataBean();
        BaumViewBO treeView = req.getDbResource().getBoFactory()
                        .getBaumViewBO();

        req.getServletRequest().setAttribute(Parameters.NAVIGATION_TREE,
                        treeView.findTreeView());

        /**
         * Eine Organisations-Id ist gesetzt
         */
        if (req.getId(Parameters.O_ID) > 0)
        {
            try
            {
                voOrganisation = daoO.findOrganisationById(new OrganisationId(
                                req.getId(Parameters.O_ID)));

                if (voOrganisation == null)
                {
                    req.getErrorBean()
                                    .addMessage("Die Organisation mit der ID "
                                                    + req.getId(Parameters.O_ID)
                                                    + " konnte nicht gefunden werden.");
                }
                else
                {
                    req.getUserBean().setCtxO(voOrganisation);
                    beanDataKompatibleRollen
                                    .setData(taoRechte
                                                    .findKompatibleRollenByPersonInOrganisation(
                                                                    req.getUserBean()
                                                                                    .getPerson()
                                                                                    .getPersonId(),
                                                                    new OrganisationId(
                                                                                    req.getId(Parameters.O_ID))));

                    beanDataOE.setData(daoOE
                                    .findOrganisationsEinheitenByOrganisationId(voOrganisation
                                                    .getOrganisationId()));

                    // Rollen-/Personen-Zuweisungen
                    beanPersonenMitRollen
                                    .setData(boPersonenMitRollen
                                                    .getPersonenMitVererbtenRollen(new OrganisationId(
                                                                    req.getId(Parameters.O_ID))));

                    // 2007-01-15 CKL: Vererbte Rollen
                    beanPersonenMitVererbtenRollen
                                    .setData(boPersonenMitRollen
                                                    .getPersonenMitVererbtenRollenAusUebergeordnetenEinheiten(new OrganisationId(
                                                                    req.getId(Parameters.O_ID))));

                }
            }
            catch (StdException e)
            {
                req.getErrorBean().addMessage(e);
            }
        }

        if (req.getId(Parameters.ROLLE_ID) > 0)
        {
            try
            {
                voRolle = daoRolle.findRolleById(new RolleId(req
                                .getId(Parameters.ROLLE_ID)));

                // Personen sortieren
                voSortedPersonen = daoPerson.findPersonenByRolleInOrganisation(
                                new RolleId(req.getId(Parameters.ROLLE_ID)),
                                new OrganisationId(req.getId(Parameters.O_ID)));
                PersonVO.sortPersonenByNachnameVorname(voSortedPersonen);
                beanPersonenAssigned.setData(voSortedPersonen);

                // Personen sortieren
                voSortedPersonen = daoPerson.findAll();
                PersonVO.sortPersonenByNachnameVorname(voSortedPersonen);
                beanPersonenAlle.setData(voSortedPersonen);

                if (voRolle == null)
                {
                    req.getErrorBean()
                                    .addMessage("Die Rolle mit der ID "
                                                    + req.getId(Parameters.ROLLE_ID)
                                                    + " konnte nicht gefunden werden.");
                }
            }
            catch (StdException e)
            {
                req.getErrorBean().addMessage(e);
            }
        }

        // Organisation setzen
        req.getServletRequest().setAttribute(Parameters.OBJ_ORGANISATION,
                        voOrganisation);
        req.getServletRequest().setAttribute(
                        Parameters.ARR_ORGANISATIONSEINHEITEN_AVAILABLE,
                        beanDataOE);
        req.getServletRequest().setAttribute(
                        Parameters.ARR_PERSONEN_MIT_ROLLEN,
                        beanPersonenMitRollen);
        // 2007-01-15 CKL: Vererbte Rollen
        req.getServletRequest().setAttribute(
                        Parameters.ARR_PERSONEN_MIT_ROLLEN_VERERBT,
                        beanPersonenMitVererbtenRollen);
        req.getServletRequest().setAttribute(Parameters.ARR_PERSONEN_ASSIGNED,
                        beanPersonenAssigned);
        req.getServletRequest().setAttribute(Parameters.ARR_PERSONEN_AVAILABLE,
                        beanPersonenAlle);
        req.getServletRequest().setAttribute(
                        Parameters.ARR_KOMPATIBLE_ROLLEN_AVAILABLE,
                        beanDataKompatibleRollen);
        req.getServletRequest().setAttribute(Parameters.OBJ_ROLLE, voRolle);

        // Organisations-Context laden
        if (null != voOrganisation)
        {
            req.getUserBean().setCtxOrganisationseinheitenAvailable(beanDataOE);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.objects.BaseControllerAdapter#run(de.ecw.zabos.
     * frontend.ressources.RequestResources)
     */
    @Override
    public void run(final RequestResources req)
    {
        if (req.isValidSubmit())
        {
            if (req.getRequestDo().equals(DO_UPDATE_ORGANISATION))
            {
                doUpdateOrganisation(req);
            }
            else if (req.getRequestDo().equals(DO_DELETE_ORGANISATION))
            {
                doDeleteOrganisation(req);
            }
            else if (req.getRequestDo().equals(DO_UPDATE_ROLLEN_KONTEXT))
            {
                doUpdateRollenKontext(req);
            }
            else if (req.getRequestDo().equals(DO_REMOVE_ROLLE_FROM_PERSON))
            {
                doRemoveRolleFromPerson(req);
            }
            else
            {
                log.error(buildLogMessage(req, "Do " + req.getRequestDo()
                                + " wurde noch nicht in der Methode "
                                + this.getClass().getName()
                                + "::dispatchExplicitSubmit definiert"));
            }
        }
    }

    /**
     * Löscht ein Objekt dieser View
     * 
     * @since 2006-06-01 CKL: Sicherheitsabfrage, ob Person berechtigt ist,
     *        diese Organisation zu löschen
     * @author ckl
     */
    protected void doDeleteOrganisation(final RequestResources req)
    {
        OrganisationsEinheitDAO daoOE = req.getDbResource().getDaoFactory()
                        .getOrganisationsEinheitDAO();
        BenutzerVerwaltungTAO taoBV = req.getDbResource().getTaoFactory()
                        .getBenutzerVerwaltungTAO();

        // 20060601_1115 CKL: Sicherheitsabfrage, ob Person berechtigt ist,
        // diese Organisation zu loeschen
        if (!req.isActionAllowed(RechtId.ORGANISATION_ANLEGEN_LOESCHEN,
                        "Sie besitzen nicht das Recht, diese Organisation zu löschen."))
        {
            return;
        }

        long id = req.getId(Parameters.O_ID);

        // Objekt mit einer ID wurde uebergeben
        if (id > 0)
        {
            // ueberpruefen, ob noch Objekte an der OE haengen
            OrganisationsEinheitVO[] organisationseinheitenVO = null;

            try
            {
                organisationseinheitenVO = daoOE
                                .findOrganisationsEinheitenByOrganisationId(new OrganisationId(
                                                req.getId(Parameters.O_ID)));
            }
            catch (StdException e)
            {
                req.getErrorBean().addMessage(e);
            }

            log.debug(buildLogMessage(req, "Loesche Organisation [" + id + "]"));

            if (organisationseinheitenVO != null)
            {
                if (organisationseinheitenVO.length == 0)
                {
                    OrganisationId organisationId = new OrganisationId(id);
                    taoBV.deleteOrganisation(organisationId);
                    // Wir müssen die aktuelle O aus dem Kontext loeschen
                    req.getUserBean().setCtxO(null);

                    // Das Objekt mit der ID 0 (Neues Objekt) soll angezeigt
                    // werden
                    req.setId(Parameters.O_ID, 0);
                }
                else
                {
                    log.error(buildLogMessage(
                                    req,
                                    "Organisation ["
                                                    + id
                                                    + "] kann nicht geloescht werden, da noch OEs der Organisation angehoeren."));
                    req.getErrorBean()
                                    .addMessage("Die Organisation konnte nicht gelöscht werden, da ihr noch Organisationseinheiten angehören. Diese Organisationseinheiten müssen vorher gelöscht werden.");
                }
            }
        }
    }

    /**
     * Updatet ein Objekt dieser View
     * 
     * @author ckl
     * @since 2006-06-01 CKL: Sicherheitsabfrage, ob Person berechtigt ist,
     *        diese Organisation anzulegen/zu löschen
     */
    protected void doUpdateOrganisation(final RequestResources req)
    {
        OrganisationDAO daoOrganisation = req.getDbResource().getDaoFactory()
                        .getOrganisationDAO();
        final BenutzerVerwaltungTAO taoBV = req.getDbResource().getTaoFactory()
                        .getBenutzerVerwaltungTAO();

        // Veroeffentlichtes Objekt
        OrganisationVO organisationVO = null;
        OrganisationVO oVOinDatenbank = null;

        long id = req.getId(Parameters.O_ID);

        // ID wurde gesetzt => Organisation soll geaendert werden
        if (id > 0)
        {
            // 2006-06-01 CKL: Sicherheitsabfrage, ob Person berechtigt ist,
            // diese Organisation zu aendern
            if (!req.isActionAllowed(RechtId.ORGANISATION_AENDERN,
                            "Sie besitzen nicht das Recht, diese Organisation zu ändern."))
            {
                return;
            }

            log.debug(buildLogMessage(req, "Organisation [" + id
                            + "] soll geaendert werden."));
            // Organisation finden
            try
            {
                organisationVO = daoOrganisation
                                .findOrganisationById(new OrganisationId(req
                                                .getId(Parameters.O_ID)));
                log.debug(buildLogMessage(req, "Organisation [" + id + "] "
                                + " gefunden."));
            }
            catch (StdException e)
            {
                req.getErrorBean().addMessage(e.getMessage());
            }
        }
        else
        {
            // 2006-06-01 CKL: Sicherheitsabfrage, ob Person berechtigt ist,
            // diese Organisation zu erstellen
            if (!req.isActionAllowed(RechtId.ORGANISATION_ANLEGEN_LOESCHEN,
                            "Sie besitzen nicht das Recht, eine neue Organisation zu erstellen."))
            {
                return;
            }

            // Es soll eine neue Organisation erstellt werden
            organisationVO = daoOrganisation.getObjectFactory()
                            .createOrganisation();

            log.debug(buildLogMessage(req,
                            "Es soll eine neue Organisation angelegt werden."));
        }

        // Eingabe validieren
        FormValidator formValidator = req.buildFormValidator();
        FormObject foName = new FormObject(Parameters.TEXT_NAME, "Name");
        formValidator.add(foName);

        // Ueberpruefen, ob eine Organisation geaendert/angelegt werden soll und
        // der
        // Name bereits existiert
        try
        {
            oVOinDatenbank = daoOrganisation.findOrganisationByName(req
                            .getStringForParam(Parameters.TEXT_NAME));

            CheckExistingObject.handle(organisationVO, oVOinDatenbank,
                            new PropertyHandlerNameImpl("Organisation",
                                            isRenameDeletedObject(),
                                            new IOnRenameDeletedObject()
                                            {
                                                public BaseIdVO renameDeletedObject(
                                                                BaseIdVO datenbankObjekt)
                                                {
                                                    return taoBV.updateOrganisation((OrganisationVO) datenbankObjekt);
                                                }
                                            }, formValidator, req));
        }
        catch (StdException e)
        {
            log.error(buildLogMessage(req, e.getMessage()));
        }

        formValidator.run();

        // Es sind keine Fehler aufgetreten
        if ((formValidator.getTotalErrors() == 0) && (null != organisationVO))
        {
            // Versuchen, Einstellungen des Objekts zu setzen
            try
            {
                organisationVO.setBeschreibung(req
                                .getStringForParam(Parameters.TEXT_BESCHREIBUNG));
                organisationVO.setName(req
                                .getStringForParam(Parameters.TEXT_NAME));

                // ID existiert > Organisation �ndern
                if (req.getId(Parameters.O_ID) > 0)
                {
                    log.info(buildLogMessage(
                                    req,
                                    "Aendere bestehenede Organisation ["
                                                    + organisationVO.getBaseId()
                                                    + "]"));
                    organisationVO = taoBV.updateOrganisation(organisationVO);
                }
                // ID existiert nicht > Organisation erstellen
                else
                {
                    log.info(buildLogMessage(req,
                                    "Erstelle neue Organisation ["
                                                    + organisationVO + "]"));
                    organisationVO = taoBV.createOrganisation(organisationVO);
                }

                // Abfrage hat geklappt
                if (null == organisationVO)
                {
                    req.getErrorBean()
                                    .addMessage("Die Organisation konnte nicht erzeugt bzw. geändert werden.");
                }
                else
                {
                    req.setId(Parameters.O_ID, organisationVO.getBaseId()
                                    .getLongValue());
                    req.getUserBean().setCtxO(organisationVO);
                }
            }
            catch (StdException e)
            {
                req.getErrorBean().addMessage(e.getMessage());
            }
        }
    }

    /**
     * Updated die Rollen
     * 
     * @author ckl
     * @since 2006-06-01 CKL: Sicherheitsabfrage, ob Person berechtigt ist,
     *        Rollen in der Organisation zuzuweisen
     */
    protected void doUpdateRollenKontext(final RequestResources req)
    {
        RolleDAO daoRolle = req.getDbResource().getDaoFactory().getRolleDAO();

        // 2006-06-01 CKL: Sicherheitsabfrage, ob Person berechtigt ist, Rollen
        // in der Organisation zuzuweisen
        if (!req.isActionAllowed(
                        RechtId.PERSONEN_ROLLEN_ZUWEISEN,
                        "Sie besitzen nicht das Recht, Personen in dieser Organisation und deren Rollen zu ändern."))
        {
            return;
        }

        log.debug(buildLogMessage(req,
                        "Rolle im Kontext Organisation soll geupdatet werden"));

        long id = req.getId(Parameters.ROLLE_ID);

        if (id > 0)
        {
            PersonVO[] voPersonen = null;

            try
            {
                RolleId rolleId = new RolleId(id);
                RolleVO voRolle = daoRolle.findRolleById(rolleId);
                voPersonen = doUpdateKontextGetPersonen(req);

                long[] personenSelected = req
                                .getLongArrayForParam(Parameters.SELECT_PERSONEN_ASSIGNED);

                // Zuerst einmal alle Personen aus den Rollen loeschen
                if (null != voPersonen)
                {
                    for (int i = 0, m = voPersonen.length; i < m; i++)
                    {
                        doUpdateKontextRemovePersonInRolle(req,
                                        voPersonen[i].getPersonId(),
                                        voRolle.getRolleId());
                    }
                }

                // Selectierte Personen hinzufuegen
                if (null != personenSelected)
                {
                    for (int i = 0, m = personenSelected.length; i < m; i++)
                    {
                        PersonId personId = new PersonId(personenSelected[i]);
                        doUpdateKontextAddPersonInRolle(req, personId,
                                        voRolle.getRolleId());
                    }
                }

                log.info(buildLogMessage(req, "Rolle [" + voRolle
                                + "] geupdatet"));
            }
            catch (StdException e)
            {
                log.error(e);
            }
        }
    }

    /**
     * Entfernt eine Person ueber einen Link "Entfernen" aus der
     * Rollen-/Schleifen-Kombination
     * 
     * @author ckl
     * @since 200715.01.2007_10:52:10
     */
    protected void doRemoveRolleFromPerson(final RequestResources req)
    {
        if (!req.isActionAllowed(
                        RechtId.PERSONEN_ROLLEN_ZUWEISEN,
                        "Sie besitzen nicht das Recht, Personen in dieser Organisation und deren Rollen zu ändern."))
        {
            return;
        }

        PersonId personId = new PersonId(req.getId(Parameters.PERSON_ID));
        RolleId rolleId = new RolleId(req.getId(Parameters.ROLLE_ID));
        doUpdateKontextRemovePersonInRolle(req, personId, rolleId);;
        log.debug(buildLogMessage(
                        req,
                        "Person [" + personId + "] aus Rolle [" + rolleId
                                        + "] aus Organisation ["
                                        + req.getId(Parameters.O_ID)
                                        + "] entfernt"));
        req.setId(Parameters.PERSON_ID, 0);
        req.setId(Parameters.ROLLE_ID, 0);
    }

    /**
     * Lädt die Personen, die während des Updates der Kontext-Liste geupdatet
     * werden sollen
     * 
     * @return
     */
    protected PersonVO[] doUpdateKontextGetPersonen(final RequestResources req)
    {
        PersonDAO daoPerson = req.getDbResource().getDaoFactory()
                        .getPersonDAO();

        PersonVO[] arrPersonen = null;
        RolleId rolleId = new RolleId(req.getId(Parameters.ROLLE_ID));
        OrganisationId organisationId = new OrganisationId(
                        req.getId(Parameters.O_ID));

        try
        {
            arrPersonen = daoPerson.findPersonenByRolleInOrganisation(rolleId,
                            organisationId);
        }
        catch (StdException e)
        {
            log.error(e);
        }

        return arrPersonen;
    }

    /**
     * Löscht die Person mit der passenden ID aus dem jeweiligen Kontext
     * 
     * @param _personId
     * @param _rolleId
     */
    protected void doUpdateKontextRemovePersonInRolle(
                    final RequestResources req, PersonId _personId,
                    RolleId _rolleId)
    {
        RechteTAO taoRecht = req.getDbResource().getTaoFactory().getRechteTAO();

        OrganisationId organisationId = new OrganisationId(
                        req.getId(Parameters.O_ID));

        if (!taoRecht.removePersonInRolleFromOrganisation(_personId, _rolleId,
                        organisationId))
        {
            req.getErrorBean()
                            .addMessage("Die Person mit der ID "
                                            + _personId
                                            + " konnte nicht aus der Rolle entfernt werden.");
        }
    }

    /**
     * Fügt die Person mit der angegebenen Id zu der Rolle
     * 
     * @param _personId
     * @param _rolleId
     */
    protected void doUpdateKontextAddPersonInRolle(RequestResources req,
                    PersonId _personId, RolleId _rolleId)
    {
        RechteTAO taoRecht = req.getDbResource().getTaoFactory().getRechteTAO();
        OrganisationId organisationId = new OrganisationId(
                        req.getId(Parameters.O_ID));

        try
        {
            // Überprüfen, ob die Person noch nicht der Organisation zugewiesen
            // wurde
            if (!daoPerson.hatPersonRolleInOrganisationNichtVererbt(_personId,
                            _rolleId, organisationId))
            {
                if (!taoRecht.addPersonInRolleToOrganisation(_personId,
                                _rolleId, organisationId))
                {
                    req.getErrorBean()
                                    .addMessage("Die Person mit der ID "
                                                    + _personId
                                                    + " konnte nicht der Rolle hinzugefügt werden.");
                }
            }
        }
        catch (StdException e)
        {
            log.error(e);
        }
    }
}
