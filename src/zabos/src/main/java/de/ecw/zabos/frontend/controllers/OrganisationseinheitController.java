package de.ecw.zabos.frontend.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import de.ecw.zabos.bo.BaumViewBO;
import de.ecw.zabos.bo.PersonenMitRollenBO;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.FormObject;
import de.ecw.zabos.frontend.FormValidator;
import de.ecw.zabos.frontend.Navigation;
import de.ecw.zabos.frontend.Parameters;
import de.ecw.zabos.frontend.beans.DataBean;
import de.ecw.zabos.frontend.beans.DateBean;
import de.ecw.zabos.frontend.controllers.helpers.ProbeterminHelper;
import de.ecw.zabos.frontend.controllers.helpers.deletedobjects.CheckExistingObject;
import de.ecw.zabos.frontend.controllers.helpers.deletedobjects.IOnRenameDeletedObject;
import de.ecw.zabos.frontend.controllers.helpers.deletedobjects.PropertyHandlerNameImpl;
import de.ecw.zabos.frontend.objects.BaseControllerAdapter;
import de.ecw.zabos.frontend.ressources.RequestResources;
import de.ecw.zabos.frontend.sql.ho.AccessControllerHO;
import de.ecw.zabos.frontend.types.id.ScheduleId;
import de.ecw.zabos.frontend.utils.DateUtils;
import de.ecw.zabos.sql.dao.OrganisationDAO;
import de.ecw.zabos.sql.dao.OrganisationsEinheitDAO;
import de.ecw.zabos.sql.dao.PersonDAO;
import de.ecw.zabos.sql.dao.ProbeTerminDAO;
import de.ecw.zabos.sql.dao.RolleDAO;
import de.ecw.zabos.sql.dao.SchleifenDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.tao.BenutzerVerwaltungTAO;
import de.ecw.zabos.sql.tao.ProbeTerminTAO;
import de.ecw.zabos.sql.tao.RechteTAO;
import de.ecw.zabos.sql.vo.BaseIdVO;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.RolleVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.types.id.OrganisationsEinheitId;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.ProbeTerminId;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.types.id.RolleId;

/**
 * Controller für die Organisationeinheiten
 * 
 * @author ckl
 */
public class OrganisationseinheitController extends BaseControllerAdapter
{
    public static final String DO_REMOVE_ROLLE_FROM_PERSON = "doRemoveRolleFromPerson";

    public static final String DO_DELETE_PROBETERMINE = "doDeleteProbetermine";

    public static final String DO_ADD_PROBETERMINE = "doAddProbetermine";

    public static final String DO_UPDATE_ROLLEN_KONTEXT = "doUpdateRollenKontext";

    public static final String DO_DELETE_ORGANISATIONSEINHEIT = "doDeleteOrganisationseinheit";

    public static final String DO_UPDATE_ORGANISATIONSEINHEIT = "doUpdateOrganisationseinheit";

    // Serial
    final static long serialVersionUID = 1209312049;

    // Logger-Instanz
    private final static Logger log = Logger
                    .getLogger(OrganisationseinheitController.class);

    protected OrganisationDAO daoOrganisation = null;

    protected OrganisationsEinheitDAO daoOrganisationsEinheit = null;

    protected BenutzerVerwaltungTAO taoBV = null;

    protected RolleDAO daoRolle = null;

    protected PersonDAO daoPerson = null;

    protected ProbeTerminDAO daoProbeTermin = null;

    protected ProbeTerminTAO taoProbeTermin = null;

    protected RechteTAO taoRecht = null;

    protected SchleifenDAO daoSchleife = null;

    protected PersonenMitRollenBO boPersonenMitRollen = null;

    protected RechteTAO taoRechte = null;

    public OrganisationseinheitController(final DBResource dbResource)
    {
        super(dbResource);

        // Verzeichnis mit den Templates setzen
        this.setActionDir(Navigation.ACTION_DIR_ORGANISATIONSEINHEIT);

        // TAO/DAO-Factory initalisieren
        daoOrganisation = dbResource.getDaoFactory().getOrganisationDAO();
        daoOrganisationsEinheit = dbResource.getDaoFactory()
                        .getOrganisationsEinheitDAO();
        taoBV = dbResource.getTaoFactory().getBenutzerVerwaltungTAO();
        daoRolle = dbResource.getDaoFactory().getRolleDAO();
        daoPerson = dbResource.getDaoFactory().getPersonDAO();
        daoProbeTermin = dbResource.getDaoFactory().getProbeTerminDAO();
        taoRecht = dbResource.getTaoFactory().getRechteTAO();
        daoSchleife = dbResource.getDaoFactory().getSchleifenDAO();
        taoProbeTermin = dbResource.getTaoFactory().getProbeTerminTAO();
        daoPerson = dbResource.getDaoFactory().getPersonDAO();
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
         * Organisationseinheit-ID aufloesen Bevorzugt wird der uebergebene
         * Parameter Organisationseinheit-ID Ist dieser nicht gesetzt, wird
         * ueberprueft, ob eine Organisation ausgesucht ist Ansonsten wird davon
         * ausgegangen, dass eine neue Organisaiton erstellt wird
         */
        if (req.getServletRequest().getParameter(Parameters.OE_ID) != null)
        {
            req.setId(Parameters.OE_ID, req.getLongForParam(Parameters.OE_ID));
        }
        else if (req.getUserBean().getCtxOE() != null)
        {
            req.setId(Parameters.OE_ID, req.getUserBean().getCtxOE()
                            .getBaseId().getLongValue());
        }
        else
        {
            req.setId(Parameters.OE_ID, 0);
        }

        if (req.getServletRequest().getParameter(Parameters.PERSON_ID) != null)
        {
            req.setId(Parameters.PERSON_ID,
                            req.getLongForParam(Parameters.PERSON_ID));
        }

        // Rolle-Id aufl�sen
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
        AccessControllerHO accessController = req.buildAccessController();

        // 2006-07-11 CKL: ueberpruefung der Rechte bei Anlegen neuer OEs
        // Wenn eine neue Organisationseinheit erstellt werden soll und der
        // Benutzer
        // sich im Kontext einer Organisation befindet, wird das Recht in der
        // Organisation geprueft
        // 2007-04-17 CKL:
        // Groben Fehler gefixt - Sobald ein Benutzer in einer
        // Organisationseinheit
        // eine Rolle zuweisen moechte und er den "übernehmen-Button" drueckt,
        // werden
        // die Rechte geloescht
        if ((req.getServletRequest().getParameter(Parameters.OE_ID) != null)
                        && (req.getLongForParam(Parameters.OE_ID) == 0)
                        && (req.getUserBean().getCtxO() != null))
        {
            accessController.update(req.getUserBean().getCtxO().getBaseId());
        }
        else
        {
            accessController.update(req.getBaseId(Parameters.OE_ID,
                            new OrganisationsEinheitId(0)));
        }

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
        OrganisationsEinheitVO organisationseinheitVO = null;
        OrganisationVO organisationVO = null;
        RolleVO rolleVO = null;
        DataBean beanPersonenAssigned = new DataBean();
        DataBean beanDataKompatibleRollen = new DataBean();
        DataBean beanPersonenAlle = new DataBean();
        DataBean beanSchleifenAvailable = new DataBean();
        DataBean beanPersonenMitRollen = new DataBean();
        DataBean beanProbetermineAvailable = new DataBean();
        DataBean beanPersonenMitVererbtenRollen = new DataBean();
        PersonVO[] voSortedPersonen = null;
        ArrayList<String> alYears = new ArrayList<String>();
        BaumViewBO treeView = req.getDbResource().getBoFactory()
                        .getBaumViewBO();

        req.getServletRequest().setAttribute(Parameters.NAVIGATION_TREE,
                        treeView.findTreeView());

        /**
         * Eine Organisations-Id ist gesetzt
         */
        if (req.getId(Parameters.OE_ID) > 0)
        {
            try
            {
                organisationseinheitVO = daoOrganisationsEinheit
                                .findOrganisationsEinheitById(new OrganisationsEinheitId(
                                                req.getId(Parameters.OE_ID)));

                if (organisationseinheitVO != null)
                {
                    // 2006-06-08 CKL: Kontext muss geupdatet werden
                    organisationVO = daoOrganisation
                                    .findOrganisationById(organisationseinheitVO
                                                    .getOrganisationId());

                    if (organisationVO != null)
                    {
                        req.getUserBean().setCtxO(organisationVO);
                    }

                    // OE updaten
                    req.getUserBean().setCtxOE(organisationseinheitVO);

                    beanDataKompatibleRollen
                                    .setData(taoRechte
                                                    .findKompatibleRollenByPersonInOrganisationsEinheit(
                                                                    req.getUserBean()
                                                                                    .getPerson()
                                                                                    .getPersonId(),
                                                                    new OrganisationsEinheitId(
                                                                                    req.getId(Parameters.OE_ID))));

                    beanSchleifenAvailable
                                    .setData(daoSchleife
                                                    .findSchleifenByOrganisationsEinheitId(new OrganisationsEinheitId(
                                                                    req.getId(Parameters.OE_ID))));
                    // Rollen-/Personen-Zuweisungen
                    beanPersonenMitRollen
                                    .setData(boPersonenMitRollen
                                                    .getPersonenMitVererbtenRollen(new OrganisationsEinheitId(
                                                                    req.getId(Parameters.OE_ID))));

                    // 2007-01-15 CKL: Vererbte Rollen
                    beanPersonenMitVererbtenRollen
                                    .setData(boPersonenMitRollen
                                                    .getPersonenMitVererbtenRollenAusUebergeordnetenEinheiten(new OrganisationsEinheitId(
                                                                    req.getId(Parameters.OE_ID))));

                    // 2006-06-08 CKL: Probetermine
                    beanProbetermineAvailable
                                    .setData(daoProbeTermin
                                                    .findProbeTermineByOrganisationsEinheitId(new OrganisationsEinheitId(
                                                                    req.getId(Parameters.OE_ID))));

                    // 2006-06-09 CKL: Datumsbereiche hinzufuegen
                    GregorianCalendar cal = new GregorianCalendar(
                                    TimeZone.getTimeZone("Europe/Berlin"));
                    int cYear = cal.get(Calendar.YEAR);

                    for (int i = 0, m = 5; i < m; i++)
                    {
                        alYears.add("" + (cYear + i));
                    }

                }
                else
                {
                    req.getErrorBean()
                                    .addMessage("Die Organisationseinheit mit der ID "
                                                    + req.getId(Parameters.OE_ID)
                                                    + " konnte nicht gefunden werden.");
                }
            }
            catch (StdException e)
            {
                req.getErrorBean().addMessage(e);
            }
        }

        if (req.getId("RolleId") > 0)
        {
            try
            {
                rolleVO = daoRolle.findRolleById(new RolleId(req
                                .getId(Parameters.ROLLE_ID)));

                // Personen sortieren
                voSortedPersonen = daoPerson
                                .findPersonenByRolleInOrganisationseinheit(
                                                new RolleId(
                                                                req.getId(Parameters.ROLLE_ID)),
                                                new OrganisationsEinheitId(
                                                                req.getId(Parameters.OE_ID)));
                PersonVO.sortPersonenByNachnameVorname(voSortedPersonen);
                beanPersonenAssigned.setData(voSortedPersonen);

                // Personen sortieren
                voSortedPersonen = daoPerson.findAll();
                PersonVO.sortPersonenByNachnameVorname(voSortedPersonen);
                beanPersonenAlle.setData(voSortedPersonen);

                if (rolleVO == null)
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
        req.getServletRequest().setAttribute(Parameters.OBJ_OE,
                        organisationseinheitVO);
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
                        Parameters.ARR_PROBETERMINE_AVAILABLE,
                        beanProbetermineAvailable);
        req.getServletRequest().setAttribute(
                        Parameters.ARR_KOMPATIBLE_ROLLEN_AVAILABLE,
                        beanDataKompatibleRollen);
        req.getServletRequest().setAttribute(
                        Parameters.ARR_SCHLEIFEN_AVAILABLE,
                        beanSchleifenAvailable);
        req.getServletRequest().setAttribute(Parameters.AL_YEARS, alYears);
        req.getServletRequest().setAttribute(Parameters.OBJ_ROLLE, rolleVO);
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
            if (req.getRequestDo().equals(DO_UPDATE_ORGANISATIONSEINHEIT))
            {
                doUpdateOrganisationseinheit(req);
            }
            else if (req.getRequestDo().equals(DO_DELETE_ORGANISATIONSEINHEIT))
            {
                doDeleteOrganisationseinheit(req);
            }
            else if (req.getRequestDo().equals(DO_UPDATE_ROLLEN_KONTEXT))
            {
                doUpdateRollenKontext(req);
            }
            else if (req.getRequestDo().equals(DO_ADD_PROBETERMINE))
            {
                doAddProbetermine(req);
            }
            else if (req.getRequestDo().equals(DO_DELETE_PROBETERMINE))
            {
                doDeleteProbetermine(req);
            }
            else if (req.getRequestDo().equals(DO_REMOVE_ROLLE_FROM_PERSON))
            {
                doRemoveRolleFromPerson(req);
            }
            else
            {
                log.error("Do " + req.getRequestDo()
                                + " wurde noch nicht in der Methode "
                                + this.getClass().getName()
                                + "::dispatchExplicitSubmit definiert");
            }
        }
    }

    /**
     * Löscht ein Objekt dieser View
     * 
     * @since 2006-05-30 CKL: Hier wird die OE erst auf "geloescht" gesetzt
     * @since 2006-06-01 CKL: Sicherheitsabfrage, ob Person berechtigt ist,
     *        diese Organisationseinheit zu löschen
     * @author ckl
     */
    protected void doDeleteOrganisationseinheit(final RequestResources req)
    {
        // 2006-06-01 CKL: Sicherheitsabfrage, ob Person berechtigt ist, diese
        // Organisationseinheit zu loeschen
        if (!req.isActionAllowed(RechtId.ORGANISATIONSEINHEIT_ANLEGEN_LOESCHEN,
                        "Sie besitzen nicht das Recht, diese Organisationseinheit zu löschen."))
        {
            return;
        }

        long id = req.getId(Parameters.OE_ID);

        // Objekt mit einer ID wurde uebergeben
        if (id > 0)
        {
            // ueberpruefen, ob noch Objekte an der OE haengen
            SchleifeVO[] schleifenVO = null;

            try
            {
                schleifenVO = daoSchleife
                                .findSchleifenByOrganisationsEinheitId(new OrganisationsEinheitId(
                                                id));
            }
            catch (StdException e)
            {
                req.getErrorBean().addMessage(e);
            }

            log.debug(buildLogMessage(req, "Loesche Organisationseinheit ["
                            + id + "]"));

            if (schleifenVO != null)
            {
                if (schleifenVO.length == 0)
                {
                    // Setzt alle Schleifen in der darunter liegen OE auf
                    // "geloescht"
                    taoBV.deleteOrganisationseinheit(new OrganisationsEinheitId(
                                    id));
                    // Wir muessen die aktuelle OE aus dem Kontext loeschen
                    req.getUserBean().setCtxOE(null);

                    // Das Objekt mit der ID 0 (Neues Objekt) soll angezeigt
                    // werden
                    req.setId(Parameters.OE_ID, 0);
                }
                else
                {
                    log.error(buildLogMessage(
                                    req,
                                    "Organisationseinheit ["
                                                    + id
                                                    + "] kann nicht geloescht werden, da noch Schleifen der OE angehoeren."));
                    req.getErrorBean()
                                    .addMessage("Die Organisationseinheit konnte nicht gelöscht werden, da ihr noch Schleifen angehören. Diese Schleifen müssen vorher gelöscht werden.");
                }
            }
        }
    }

    /**
     * Updatet ein Objekt dieser View
     * 
     * @since 2006-06-01 CKL: Sicherheitsabfrage, ob Person berechtigt ist,
     *        diese Organisationeinheit zu ändern
     * @since 2006-06-01 CKL: Es kommt ein Fehler, wenn versucht wird, die
     *        Organisationseinheit anzulegen, wenn KEIN Kontext Organisation
     *        existiert
     * @author ckl
     */
    protected void doUpdateOrganisationseinheit(final RequestResources req)
    {
        OrganisationsEinheitVO organisationseinheitVO = null;
        OrganisationsEinheitVO oeVOinDatenbank = null;

        long id = req.getId(Parameters.OE_ID);

        // ID wurde gesetzt => Organisationseinheit soll geaendert werden
        if (id > 0)
        {
            // 2006-06-01 CKL: Sicherheitsabfrage, ob Person berechtigt ist,
            // diese
            // Organisationeinheit zu aendern
            if (!req.isActionAllowed(RechtId.ORGANISATIONSEINHEIT_AENDERN,
                            "Sie besitzen nicht das Recht, diese Organisationseinheit zu ändern."))
            {
                return;
            }

            log.debug(buildLogMessage(req, "Organisationseinheit [" + id + "]"
                            + " soll geaendert werden."));

            // Organisationseinheit finden
            try
            {
                organisationseinheitVO = daoOrganisationsEinheit
                                .findOrganisationsEinheitById(new OrganisationsEinheitId(
                                                req.getId(Parameters.OE_ID)));
            }
            catch (StdException e)
            {
                req.getErrorBean().addMessage(e);
            }
        }
        else
        {
            // Es soll eine neue Organisation erstellt werden
            // 2006-06-01 CKL: Sicherheitsabfrage, ob Person berechtigt ist,
            // diese
            // Organisationseinheit zu erstellen
            if (!req.isActionAllowed(
                            RechtId.ORGANISATIONSEINHEIT_ANLEGEN_LOESCHEN,
                            "Sie besitzen nicht das Recht, eine neue Organisationseinheit zu erstellen."))
            {
                return;
            }

            organisationseinheitVO = daoOrganisationsEinheit.getObjectFactory()
                            .createOrganisationsEinheit();
            log.debug(buildLogMessage(req,
                            "Es soll eine neue Organisationseinheit angelegt werden."));
        }

        // Eingabe validieren
        FormValidator formValidator = req.buildFormValidator();
        FormObject foName = new FormObject(Parameters.TEXT_NAME, "Name");
        formValidator.add(foName);

        // ueberpruefen, ob eine Organisation geaendert/angelegt werden soll und
        // der
        // Name bereits existiert
        try
        {
            oeVOinDatenbank = daoOrganisationsEinheit
                            .findOrganisationsEinheitByName(req
                                            .getStringForParam(Parameters.TEXT_NAME));

            CheckExistingObject.handle(organisationseinheitVO, oeVOinDatenbank,
                            new PropertyHandlerNameImpl("Organisationseinheit",
                                            isRenameDeletedObject(),
                                            new IOnRenameDeletedObject()
                                            {
                                                public BaseIdVO renameDeletedObject(
                                                                BaseIdVO datenbankObjekt)
                                                {
                                                    return taoBV.updateOrganisationsEinheit((OrganisationsEinheitVO) datenbankObjekt);
                                                }
                                            }, formValidator, req));
        }
        catch (StdException e)
        {
            log.error(buildLogMessage(req, e.getMessage()));
        }

        // 2006-06-01 CKL: Es kommt ein Fehler, wenn versucht wird, die
        // Organisationseinheit anzulegen, wenn KEIN Kontext Organisation
        // existiert
        if (req.getUserBean().getCtxO() == null)
        {
            formValidator.addCustomError(
                            new FormObject(Parameters.TEXT_NAME, "Name"),
                            "Sie versuchen eine Organisationseinheit anzulegen, ohne dass Sie sich im Kontext einer Organisation befinden.",
                            null);
        }

        formValidator.run();

        // Es sind keine Fehler aufgetreten
        if (formValidator.getTotalErrors() == 0)
        {

            // Wenn ein Organisations-Objekt existiert
            if (null != organisationseinheitVO)
            {

                // Versuchen, Einstellungen des Objekts zu setzen
                try
                {
                    organisationseinheitVO
                                    .setBeschreibung(req
                                                    .getStringForParam(Parameters.TEXT_BESCHREIBUNG));
                    organisationseinheitVO.setName(req
                                    .getStringForParam(Parameters.TEXT_NAME));
                    organisationseinheitVO.setOrganisationId(req.getUserBean()
                                    .getCtxO().getOrganisationId());

                    // ID existiert > Organisation �ndern
                    if (req.getId(Parameters.OE_ID) > 0)
                    {
                        organisationseinheitVO = taoBV
                                        .updateOrganisationsEinheit(organisationseinheitVO);
                    }
                    // ID existiert nicht > Organisation erstellen
                    else
                    {
                        organisationseinheitVO = taoBV
                                        .createOrganisationseinheit(organisationseinheitVO);
                    }

                    if (null == organisationseinheitVO)
                    {
                        req.getErrorBean()
                                        .addMessage("Die Organisationseinheit konnte nicht erzeugt bzw. geändert werden.");
                    }
                    else
                    {
                        req.setId(Parameters.OE_ID, organisationseinheitVO
                                        .getBaseId().getLongValue());
                    }
                }
                catch (StdException e)
                {
                    req.getErrorBean().addMessage(e);
                }
            }
        }
    }

    /**
     * Updated die Rollen
     * 
     * @since 2006-06-01 CKL: Sicherheitsabfrage, ob Person berechtigt ist,
     *        Rollen in der Organisationseinheit zuzuweisen
     */
    protected void doUpdateRollenKontext(final RequestResources req)
    {
        // 2006-06-01 CKL: Sicherheitsabfrage, ob Person berechtigt ist, Rollen
        // in
        // der Organisationseinheit zuzuweisen
        if (!req.isActionAllowed(
                        RechtId.PERSONEN_ROLLEN_ZUWEISEN,
                        "Sie besitzen nicht das Recht, Personen in dieser Organisationseinheit und deren Rollen zu ändern."))
        {
            return;
        }

        long id = req.getId(Parameters.ROLLE_ID);
        if (id > 0)
        {
            PersonVO[] personenVO = null;

            try
            {
                RolleVO voRolle = daoRolle.findRolleById(new RolleId(id));
                personenVO = doUpdateKontextGetPersonen(req);

                long[] personenSelected = req
                                .getLongArrayForParam(Parameters.SELECT_PERSONEN_ASSIGNED);

                // Zuerst einmal alle Personen aus den Rollen loeschen
                if (null != personenVO)
                {
                    for (int i = 0, m = personenVO.length; i < m; i++)
                    {
                        doUpdateKontextRemovePersonInRolle(req,
                                        personenVO[i].getPersonId(),
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

                log.info(buildLogMessage(req, "Rolle [" + id + "] geupdatet"));
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
     */
    protected void doRemoveRolleFromPerson(final RequestResources req)
    {
        if (!req.isActionAllowed(
                        RechtId.PERSONEN_ROLLEN_ZUWEISEN,
                        "Sie besitzen nicht das Recht, Personen in dieser Organisationseinheit und deren Rollen zu ändern."))
        {
            return;
        }

        PersonId personId = new PersonId(req.getId(Parameters.PERSON_ID));
        RolleId rolleId = new RolleId(req.getId(Parameters.ROLLE_ID));
        doUpdateKontextRemovePersonInRolle(req, personId, rolleId);
        log.debug(buildLogMessage(
                        req,
                        "Person [" + personId + "] aus Rolle [" + rolleId + "]"
                                        + " aus Organisationseinheit ["
                                        + req.getId(Parameters.OE_ID)
                                        + "] entfernt"));
        req.setId(Parameters.PERSON_ID, 0);
        req.setId(Parameters.ROLLE_ID, 0);
    }

    /**
     * Laedt die Personen, die waehrend des Updates der Kontext-Liste geupdatet
     * werden sollen
     * 
     * @return
     */
    protected PersonVO[] doUpdateKontextGetPersonen(final RequestResources req)
    {
        PersonVO[] personenVO = null;

        try
        {
            RolleId rolleId = new RolleId(req.getId(Parameters.ROLLE_ID));
            OrganisationsEinheitId oeId = new OrganisationsEinheitId(
                            req.getId(Parameters.OE_ID));
            personenVO = daoPerson.findPersonenByRolleInOrganisationseinheit(
                            rolleId, oeId);
        }
        catch (StdException e)
        {
            req.getErrorBean().addMessage(e);
        }

        return personenVO;
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
        OrganisationsEinheitId oeId = new OrganisationsEinheitId(
                        req.getId(Parameters.OE_ID));

        if (!taoRecht.removePersonInRolleFromOrganisationseinheit(_personId,
                        _rolleId, oeId))
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
        OrganisationsEinheitId oeId = new OrganisationsEinheitId(
                        req.getId(Parameters.OE_ID));

        try
        {
            // Überprüfen, ob die Person noch nicht der Organisationseinheit
            // zugewiesen wurde
            if (!daoPerson.hatPersonRolleInOrganisationseinheitNichtVererbt(
                            _personId, _rolleId, oeId))
            {
                if (!taoRecht.addPersonInRolleToOrganisationseinheit(_personId,
                                _rolleId, oeId))
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

    /**
     * Löscht ein Objekt dieser View
     * 
     */
    protected void doDeleteProbetermine(RequestResources req)
    {
        // 2006-06-01 CKL: Sicherheitsabfrage, ob Person berechtigt ist,
        // Probealarme
        // zu administrieren
        if (!req.isActionAllowed(RechtId.PROBEALARM_ADMINISTRIEREN,
                        "Sie besitzen nicht das Recht, Probealarme zu administrieren"))
        {
            return;
        }

        long arrToDelete[] = req.getLongArrayForParam("arrToDelete[]");

        if (arrToDelete != null)
        {
            for (int i = 0, m = arrToDelete.length; i < m; i++)
            {
                log.debug(buildLogMessage(req, "Loesche Probetermin ["
                                + arrToDelete[i] + "]"));
                // 2006-07-01 CKL: DAO durch TAO ersetzt, ansonsten unlogisches
                // Verhalten in der GUI
                ProbeTerminId ptId = new ProbeTerminId(arrToDelete[i]);
                taoProbeTermin.deleteProbeTermin(ptId);
            }
        }
    }

    /**
     * Updatet ein Objekt dieser View
     * 
     */
    protected void doAddProbetermine(final RequestResources req)
    {
        // 2006-06-01 CKL: Sicherheitsabfrage, ob Person berechtigt ist,
        // Probealarme
        // zu administrieren
        if (!req.isActionAllowed(RechtId.PROBEALARM_ADMINISTRIEREN,
                        "Sie besitzen nicht das Recht, Probealarme zu administrieren"))
        {
            return;
        }

        GregorianCalendar calendar = new GregorianCalendar();
        int yearStart = calendar.get(GregorianCalendar.YEAR);
        int monthStart = calendar.get(GregorianCalendar.MONTH);
        int yearEnd = yearStart + 1;
        int monthEnd = monthStart;

        // Typ
        int idType = req.getIntForParam(Parameters.ID_TYPE);

        FormValidator formValidator = req.buildFormValidator();
        FormObject foZeitStart = new FormObject(Parameters.TEXT_ZEIT_START,
                        "Startzeit");
        foZeitStart.setFlag(FormObject.VALID_TIME);
        formValidator.add(foZeitStart);

        FormObject foZeitEnde = new FormObject(Parameters.TEXT_ZEIT_ENDE,
                        "Endzeit");
        foZeitEnde.setFlag(FormObject.VALID_TIME);
        formValidator.add(foZeitEnde);

        // Start/Ende muss hinten liegen
        DateBean dateStart = new DateBean();
        DateBean dateEnde = new DateBean();

        // Zeit & Datum setzen
        dateStart.setTime(req.getStringForParam(Parameters.TEXT_ZEIT_START));
        dateEnde.setTime(req.getStringForParam(Parameters.TEXT_ZEIT_ENDE));

        int startTimeHash = Integer.valueOf(String.valueOf(dateStart.getHour())
                        + DateUtils.fillTwoSigns(dateStart.getMinute()));
        int endTimeHash = Integer.valueOf(dateEnde.getHour()
                        + DateUtils.fillTwoSigns(dateEnde.getMinute()));

        // Fehler, wenn Begin >= Ende
        if (startTimeHash >= endTimeHash)
        {
            formValidator.addCustomError(new FormObject(
                            Parameters.TEXT_ZEIT_START, "Startzeitpunkt"),
                            "Die Endzeit muss später als die Startzeit sein.",
                            null);
        }

        // Individuelle Ueberpruefung der einzelnen Typen
        if (idType != 0)
        {
            if (idType == ScheduleId.EINMALIG)
            {
                FormObject foDatum = new FormObject(Parameters.TEXT_DATUM,
                                "Datum");
                foDatum.setFlag(FormObject.VALID_DATE);
                formValidator.add(foDatum);
            }
            else if (idType == ScheduleId.MONATLICH)
            {
                long lengthOfTag = req.getLongForParam(Parameters.TEXT_DATUM);

                if (lengthOfTag < 1 || lengthOfTag > 28)
                {
                    formValidator.addCustomError(
                                    new FormObject(Parameters.TEXT_DATUM, "Tag"),
                                    "Es kann als Tag des Monats nur 1 - 28 eingegeben werden.",
                                    null);
                }
            }
            else
            {
                monthStart = req.getIntForParam(Parameters.SELECT_MONTH_START);
                yearStart = req.getIntForParam(Parameters.SELECT_YEAR_START);
                monthEnd = req.getIntForParam(Parameters.SELECT_MONTH_END);
                yearEnd = req.getIntForParam(Parameters.SELECT_YEAR_END);

                // Hash erstellen. 200608 z.b.
                int startHash = Integer.valueOf(String.valueOf(yearStart)
                                + DateUtils.fillTwoSigns(monthStart));
                int endHash = Integer.valueOf(String.valueOf(yearEnd)
                                + DateUtils.fillTwoSigns(monthEnd));

                if (startHash >= endHash)
                {
                    formValidator.addCustomError(new FormObject(
                                    Parameters.SELECT_MONTH_END, "Datum"),
                                    "Das Enddatum muss in der Zukunft liegen.",
                                    null);
                }
            }
        }

        // Validierung durchfuehren
        formValidator.run();

        // Keine Fehler aufgetreten
        if (formValidator.getTotalErrors() == 0)
        {
            idType = req.getIntForParam(Parameters.ID_TYPE);

            if (idType > 0)
            {
                try
                {
                    ProbeterminHelper probeTerminHelper = new ProbeterminHelper(
                                    req);

                    if (idType == ScheduleId.EINMALIG)
                    {
                        probeTerminHelper.createProbetermineEinmalig();
                    }
                    else if (idType == ScheduleId.TAEGLICH)
                    {
                        probeTerminHelper.createProbetermineTaeglich(
                                        monthStart, yearStart, monthEnd,
                                        yearEnd);
                    }
                    else if (idType == ScheduleId.WOECHENTLICH)
                    {
                        probeTerminHelper.createProbetermineWoechentlich(
                                        monthStart, yearStart, monthEnd,
                                        yearEnd);
                    }
                    else
                    {
                        probeTerminHelper.createProbetermineMonatlich(
                                        monthStart, yearStart, monthEnd,
                                        yearEnd);
                    }
                }
                catch (StdException e)
                {
                    req.getErrorBean().addMessage(e);
                }
            }
        }
    }

}
