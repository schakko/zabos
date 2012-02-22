package de.ecw.zabos.frontend.controllers;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import de.ecw.zabos.bo.BaumViewBO;
import de.ecw.zabos.bo.PersonenMitRollenBO;
import de.ecw.zabos.bo.SchleifeBO;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.FormObject;
import de.ecw.zabos.frontend.FormValidator;
import de.ecw.zabos.frontend.Navigation;
import de.ecw.zabos.frontend.Parameters;
import de.ecw.zabos.frontend.beans.DataBean;
import de.ecw.zabos.frontend.controllers.helpers.deletedobjects.CheckExistingObject;
import de.ecw.zabos.frontend.controllers.helpers.deletedobjects.IOnRenameDeletedObject;
import de.ecw.zabos.frontend.controllers.helpers.deletedobjects.PropertyHandlerKuerzelImpl;
import de.ecw.zabos.frontend.objects.BaseControllerAdapter;
import de.ecw.zabos.frontend.ressources.RequestResources;
import de.ecw.zabos.frontend.sql.ho.AccessControllerHO;
import de.ecw.zabos.sql.dao.OrganisationDAO;
import de.ecw.zabos.sql.dao.OrganisationsEinheitDAO;
import de.ecw.zabos.sql.dao.PersonDAO;
import de.ecw.zabos.sql.dao.RolleDAO;
import de.ecw.zabos.sql.dao.SchleifenDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.tao.BenutzerVerwaltungTAO;
import de.ecw.zabos.sql.tao.RechteTAO;
import de.ecw.zabos.sql.vo.BaseIdVO;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.RolleVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.types.id.RolleId;
import de.ecw.zabos.types.id.SchleifeId;

/**
 * Controller<br>
 * Controller fuer die Schleifen
 * 
 * @author ckl
 */
public class SchleifeController extends BaseControllerAdapter
{
    public static final String DO_REMOVE_ROLLE_FROM_PERSON = "doRemoveRolleFromPerson";

    public static final String DO_UPDATE_ROLLEN_KONTEXT = "doUpdateRollenKontext";

    public static final String DO_DELETE_SCHLEIFE = "doDeleteSchleife";

    public static final String DO_UPDATE_SCHLEIFE = "doUpdateSchleife";

    // Serial
    final static long serialVersionUID = 1209312049;

    // Logger-Instanz
    private final static Logger log = Logger
                    .getLogger(SchleifeController.class);

    protected OrganisationDAO daoOrganisation = null;

    protected OrganisationsEinheitDAO daoOE = null;

    protected BenutzerVerwaltungTAO taoBV = null;

    protected RolleDAO daoRolle = null;

    protected PersonDAO daoPerson = null;

    protected SchleifenDAO daoSchleife = null;

    protected RechteTAO taoRecht = null;

    protected PersonenMitRollenBO boPersonenMitRollen = null;

    private boolean isDruckerKuerzelAktiv = false;

    private boolean isFolgeschleifeAktiv = false;

    private boolean isRueckmeldeintervallAktiv = false;

    public SchleifeController(final DBResource dbResource)
    {
        super(dbResource);

        // Verzeichnis mit den Templates setzen
        this.setActionDir(Navigation.ACTION_DIR_SCHLEIFE);

        // TAO/DAO-Factory initalisieren
        daoOrganisation = dbResource.getDaoFactory().getOrganisationDAO();
        daoOE = dbResource.getDaoFactory().getOrganisationsEinheitDAO();
        taoBV = dbResource.getTaoFactory().getBenutzerVerwaltungTAO();
        daoRolle = dbResource.getDaoFactory().getRolleDAO();
        daoPerson = dbResource.getDaoFactory().getPersonDAO();
        daoSchleife = dbResource.getDaoFactory().getSchleifenDAO();
        taoRecht = dbResource.getTaoFactory().getRechteTAO();
        boPersonenMitRollen = dbResource.getBoFactory()
                        .getPersonenMitRollenBO();
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
        // Organisationseinheit-ID aufloesen
        // Bevorzugt wird der uebergebene Parameter Schleife-ID
        // Ist dieser nicht gesetzt, wird ueberprueft, ob eine Schleife
        // ausgesucht
        // ist
        // Ansonsten wird davon ausgegangen, dass eine neue Schleife erstellt
        // wird
        if (req.getServletRequest().getParameter(Parameters.SCHLEIFE_ID) != null)
        {
            req.setId(Parameters.SCHLEIFE_ID,
                            req.getLongForParam(Parameters.SCHLEIFE_ID));
        }
        else if (req.getUserBean().getCtxSchleife() != null)
        {
            req.setId(Parameters.SCHLEIFE_ID, req.getUserBean()
                            .getCtxSchleife().getBaseId().getLongValue());
        }
        else
        {
            req.setId(Parameters.SCHLEIFE_ID, 0);
        }

        if (req.getServletRequest().getParameter(Parameters.PERSON_ID) != null)
        {
            req.setId(Parameters.PERSON_ID,
                            req.getLongForParam(Parameters.PERSON_ID));
        }

        // Rolle-Id aufloesen
        if (req.getServletRequest().getParameter(Parameters.ROLLE_ID) != null)
        {
            req.setId(Parameters.ROLLE_ID,
                            req.getLongForParam(Parameters.ROLLE_ID));
        }

        if (req.getServletRequest().getParameter(Parameters.ROLLE_ID) != null)
        {
            req.setId(Parameters.ROLLE_ID,
                            req.getLongForParam(Parameters.ROLLE_ID));
        }

        // Organisations-Id aufloesen
        if (req.getServletRequest().getParameter(Parameters.SELECT_O_ID) != null)
        {
            req.setId(Parameters.SELECT_O_ID,
                            req.getLongForParam(Parameters.SELECT_O_ID));
        }

        // Organisationseinheit-Id aufloesen
        if (req.getServletRequest().getParameter(Parameters.SELECT_OE_ID) != null)
        {
            req.setId(Parameters.SELECT_OE_ID,
                            req.getLongForParam(Parameters.SELECT_OE_ID));
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

        if ((req.getServletRequest().getParameter(Parameters.SCHLEIFE_ID) != null)
                        && (req.getLongForParam(Parameters.SCHLEIFE_ID) == 0)
                        && (req.getUserBean().getCtxOE() != null))
        {
            accessController.update(req.getUserBean().getCtxOE().getBaseId());
        }
        else
        {
            accessController.update(req.getBaseId(Parameters.SCHLEIFE_ID,
                            new SchleifeId(0)));
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
        SchleifeVO schleifeVO = null;
        OrganisationVO organisationVO = null;
        OrganisationsEinheitVO oeVO = null;
        RolleVO rolleVO = null;
        DataBean beanPersonenAssigned = new DataBean();
        DataBean beanDataKomptabibleRollen = new DataBean();
        DataBean beanPersonenAlle = new DataBean();
        DataBean beanPersonenMitRollen = new DataBean();
        DataBean beanPersonenMitVererbtenRollen = new DataBean();
        PersonVO[] sortedPersonenVO = null;
        BaumViewBO treeView = req.getDbResource().getBoFactory()
                        .getBaumViewBO();
        DataBean beanMoeglicheFolgeschleifen = new DataBean();

        req.getServletRequest().setAttribute(Parameters.NAVIGATION_TREE,
                        treeView.findTreeView());

        /**
         * Eine Schleifen-Id ist gesetzt
         */
        if (req.getId(Parameters.SCHLEIFE_ID) > 0)
        {
            try
            {
                schleifeVO = daoSchleife.findSchleifeById(new SchleifeId(req
                                .getId(Parameters.SCHLEIFE_ID)));

                if (schleifeVO == null)
                {
                    req.getErrorBean()
                                    .addMessage("Die Schleife mit der ID "
                                                    + req.getId(Parameters.SCHLEIFE_ID)
                                                    + " konnte nicht gefunden werden.");
                }
                else
                { // 2006-06-08 CKL: Kontext muss geupdatet werden
                    oeVO = daoOE.findOrganisationsEinheitById(schleifeVO
                                    .getOrganisationsEinheitId());

                    if (oeVO != null)
                    {
                        organisationVO = daoOrganisation
                                        .findOrganisationById(oeVO
                                                        .getOrganisationId());

                        if (organisationVO != null)
                        {
                            req.getUserBean().setCtxO(organisationVO);
                        }

                        req.getUserBean().setCtxOE(oeVO);
                    }

                    req.getUserBean().setCtxSchleife(schleifeVO);
                    beanDataKomptabibleRollen
                                    .setData(taoRecht
                                                    .findKompatibleRollenByPersonInSchleife(
                                                                    req.getUserBean()
                                                                                    .getPerson()
                                                                                    .getPersonId(),
                                                                    new SchleifeId(
                                                                                    req.getId(Parameters.SCHLEIFE_ID))));

                    beanPersonenMitRollen
                                    .setData(boPersonenMitRollen
                                                    .getPersonenMitVererbtenRollen(new SchleifeId(
                                                                    req.getId(Parameters.SCHLEIFE_ID))));
                    beanPersonenMitVererbtenRollen
                                    .setData(boPersonenMitRollen
                                                    .getPersonenMitVererbtenRollenAusUebergeordnetenEinheiten(new SchleifeId(
                                                                    req.getId(Parameters.SCHLEIFE_ID))));

                }
            }
            catch (StdException e)
            {
                req.getErrorBean().addMessage(e);
            }
        }

        // Folgeschleifen festlegen
        try
        {
            SchleifeVO[] alleSchleifen = daoSchleife.findAll();

            // Die eigene Schleife muss entfernt werden
            beanMoeglicheFolgeschleifen.setData(alleSchleifen);
        }
        catch (StdException e)
        {
            log.error(e.getMessage());
        }

        if (req.getId(Parameters.ROLLE_ID) > 0)
        {
            try
            {
                rolleVO = daoRolle.findRolleById(new RolleId(req
                                .getId(Parameters.ROLLE_ID)));
                beanPersonenAssigned
                                .setData(daoPerson.findPersonenByRolleInSchleife(
                                                new RolleId(
                                                                req.getId(Parameters.ROLLE_ID)),
                                                new SchleifeId(
                                                                req.getId(Parameters.SCHLEIFE_ID))));
                // Personen sortieren
                sortedPersonenVO = daoPerson.findAll();
                PersonVO.sortPersonenByNachnameVorname(sortedPersonenVO);
                beanPersonenAlle.setData(sortedPersonenVO);

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

        // Differenz zwischen übergebenen Daten in der Select-Box "Zugeordnete
        // Personen" und den eigentlichen Presonen berechnen
        beanPersonenAssigned = createContentSelectBoxAssignedPersonen(req,
                        beanPersonenAssigned);

        // Organisation setzen
        req.setData(Parameters.OBJ_SCHLEIFE, schleifeVO);
        req.setData(Parameters.ARR_PERSONEN_MIT_ROLLEN, beanPersonenMitRollen);
        req.setData(Parameters.ARR_PERSONEN_MIT_ROLLEN_VERERBT,
                        beanPersonenMitVererbtenRollen);
        req.setData(Parameters.ARR_PERSONEN_ASSIGNED, beanPersonenAssigned);
        req.setData(Parameters.ARR_PERSONEN_AVAILABLE, beanPersonenAlle);
        req.setData(Parameters.ARR_KOMPATIBLE_ROLLEN_AVAILABLE,
                        beanDataKomptabibleRollen);
        req.setData(Parameters.OBJ_ROLLE, rolleVO);
        req.setData(Parameters.ARR_MOEGLICHE_FOLGESCHLEIFEN,
                        beanMoeglicheFolgeschleifen);
        req.setData(Parameters.IS_RUECKMELDEINTERVAL_AKTIV,
                        isRueckmeldeintervallAktiv());
        req.setData(Parameters.IS_FOLGESCHLEIFE_AKTIV, isFolgeschleifeAktiv());
        req.setData(Parameters.IS_DRUCKERKUERZEL_AKTIV, isDruckerKuerzelAktiv());
    }

    /**
     * Erstellt den Inhalt der Select-Box "Zugeordnete Personen"
     * 
     * @param _currentlyAssigned
     * @return DataBean
     */
    protected DataBean createContentSelectBoxAssignedPersonen(
                    final RequestResources req,
                    final DataBean _currentlyAssigned)
    {
        long[] personenSelected = req
                        .getLongArrayForParam(Parameters.SELECT_PERSONEN_ASSIGNED);

        for (int i = 0, m = personenSelected.length; i < m; i++)
        {
            try
            {
                PersonVO personVO = daoPerson.findPersonById(new PersonId(
                                personenSelected[i]));

                if (!_currentlyAssigned.isObjectInList(personVO.getBaseId()))
                {
                    _currentlyAssigned.setData(personVO);
                }
            }
            catch (StdException e)
            {
                log.error(e);
            }
        }

        return _currentlyAssigned;
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
            if (req.getRequestDo().equals(DO_UPDATE_SCHLEIFE))
            {
                doUpdateSchleife(req);
            }
            else if (req.getRequestDo().equals(DO_DELETE_SCHLEIFE))
            {
                doDeleteSchleife(req);
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
                log.error("Do " + req.getRequestDo()
                                + " wurde noch nicht in der Methode "
                                + this.getClass().getName()
                                + "::dispatchExplicitSubmit definiert");
            }
        }
    }

    /**
     * Löscht ein Objekt dieser View
     */
    protected void doDeleteSchleife(final RequestResources req)
    {
        // 2006-06-01 CKL: Sicherheitsabfrage, ob Person berechtigt ist, diese
        // Schleife zu loeschen
        if (!req.isActionAllowed(RechtId.SCHLEIFE_ANLEGEN_LOESCHEN,
                        "Sie besitzen nicht das Recht, diese Schleife zu löschen."))
        {
            return;
        }

        long id = req.getId(Parameters.SCHLEIFE_ID);

        // Objekt mit einer ID wurde uebergeben
        if (id > 0)
        {
            SchleifeVO schleifeVO;

            // 2006-05-30 CKL: ueberpruefen, ob Schleife wirklich existiert
            try
            {
                schleifeVO = daoSchleife.findSchleifeById(new SchleifeId(req
                                .getId(Parameters.SCHLEIFE_ID)));

                if (schleifeVO == null)
                {
                    req.getErrorBean()
                                    .addMessage("Die angegebene Schleife konnte nicht gefunden werden.");
                }
                else
                {
                    log.debug(buildLogMessage(req, "Loesche Schleife [" + id
                                    + "]"));

                    taoBV.deleteSchleife(new SchleifeId(id));

                    // Wir müssen die aktuelle O aus dem Kontext löschen
                    req.getUserBean().setCtxSchleife(null);

                    // Das Objekt mit der ID 0 (Neues Objekt) soll angezeigt
                    // werden
                    req.setId(Parameters.SCHLEIFE_ID, 0);

                    // 2006-05-30 CKL: Zur Seite der uebergeordneten
                    // Organisationseinheit,
                    // Reiter "Schleifen" weiterleiten
                    req.setData("objDeletedSchleife", schleifeVO);

                    if (req.getUserBean().getCtxOE() != null)
                    {
                        req.setForwardPage("/controller/"
                                        + Navigation.ACTION_DIR_ORGANISATIONSEINHEIT
                                        + "/?OrganisationsEinheitId="
                                        + req.getUserBean()
                                                        .getCtxOE()
                                                        .getOrganisationsEinheitId()
                                                        .getLongValue()
                                        + "&tab=schleifen");
                    }
                }
            }
            catch (StdException e)
            {
                log.error(buildLogMessage(req, e.getMessage()));
                req.getErrorBean().addMessage(
                                "Beim Löschen der Schleife traten Fehler auf.");
            }
        }
    }

    /**
     * Updatet ein Objekt dieser View
     */
    protected void doUpdateSchleife(final RequestResources req)
    {
        SchleifeVO schleifeVO = null;
        SchleifeVO sVOinDatenbank = null;

        long id = req.getId(Parameters.SCHLEIFE_ID);

        // ID wurde gesetzt => Organisationseinheit soll geaendert werden
        if (id > 0)
        {
            // 2006-06-01 CKL: Sicherheitsabfrage, ob Person berechtigt ist,
            // diese
            // Schleife zu aendern
            if (!req.isActionAllowed(RechtId.SCHLEIFE_AENDERN,
                            "Sie besitzen nicht das Recht, diese Schleife zu ändern."))
            {
                return;
            }

            log.debug(buildLogMessage(req, "Schleife [" + id
                            + "] soll geaendert werden."));

            // Organisationseinheit finden
            try
            {
                schleifeVO = daoSchleife.findSchleifeById(new SchleifeId(id));

                log.debug(buildLogMessage(req, "Schleife [" + id + "]"
                                + " gefunden"));
            }
            catch (StdException e)
            {
                req.getErrorBean().addMessage(e);
            }
        }
        else
        {
            // Es soll eine neue Schleife erstellt werden
            // 2006-06-01 CKL: Sicherheitsabfrage, ob Person berechtigt ist,
            // diese
            // Schleife zu erstellen
            if (!req.isActionAllowed(RechtId.SCHLEIFE_ANLEGEN_LOESCHEN,
                            "Sie besitzen nicht das Recht, eine neue Schleife zu erstellen."))
            {
                return;
            }

            schleifeVO = daoSchleife.getObjectFactory().createSchleife();
            log.debug(buildLogMessage(req,
                            "Es soll eine neue Schleife angelegt werden."));
        }

        // Eingabe validieren
        FormValidator formValidator = req.buildFormValidator();
        // Name wird gebraucht
        FormObject foName = new FormObject(Parameters.TEXT_NAME, "Name");

        // Fuenfton => Laenge genau 5 und nur Zahlen - wenn etwas eingegeben
        // wurde
        if (req.getStringForParam(Parameters.TEXT_FUENFTON) != null)
        {
            if (req.getStringForParam(Parameters.TEXT_FUENFTON).length() > 0)
            {
                FormObject foFuenfton = new FormObject(
                                Parameters.TEXT_FUENFTON, "Fünfton");
                foFuenfton.setMaxLength(5);
                foFuenfton.setMinLength(5);
                foFuenfton.setFlag(FormObject.NUMERIC);

                formValidator.add(foFuenfton);
            }
        }

        // Kann das Drucker-Kürzel definiert werden
        if (isDruckerKuerzelAktiv()
                        && (req.getStringForParam(
                                        Parameters.TEXT_DRUCKER_KUERZEL)
                                        .length() > 0))
        {
            FormObject foDruckerKuerzel = new FormObject(
                            Parameters.TEXT_DRUCKER_KUERZEL, "Drucker-Kürzel");
            foDruckerKuerzel.setMaxLength(15);
            formValidator.add(foDruckerKuerzel);
        }

        // Kann das Rückmeldeintervall definiert werden
        if (isRueckmeldeintervallAktiv()
                        && (req.getStringForParam(
                                        Parameters.TEXT_RUECKMELDEINTERVALL)
                                        .length() > 0))
        {
            FormObject foRueckmeldeIntervall = new FormObject(
                            Parameters.TEXT_RUECKMELDEINTERVALL,
                            "Rückmeldeintervall");
            foRueckmeldeIntervall.setFlag(FormObject.NUMERIC);

            formValidator.add(foRueckmeldeIntervall);
        }

        // Kuerzel
        FormObject foKuerzel = new FormObject(Parameters.TEXT_KUERZEL, "Kürzel");
        formValidator.add(foName);
        formValidator.add(foKuerzel);

        // 2006-05-23 CKL: Wenn Schleifenkuerzel bereits in der Datenbank
        // existiert,
        // wird eine Fehlermeldung ausgegeben
        try
        {
            sVOinDatenbank = daoSchleife.findSchleifeByKuerzel(req
                            .getStringForParam(Parameters.TEXT_KUERZEL));
            CheckExistingObject.handle(schleifeVO, sVOinDatenbank,
                            new PropertyHandlerKuerzelImpl("Schleife",
                                            isRenameDeletedObject(),
                                            new IOnRenameDeletedObject()
                                            {
                                                public BaseIdVO renameDeletedObject(
                                                                BaseIdVO datenbankObjekt)
                                                {
                                                    return taoBV.updateSchleife((SchleifeVO) datenbankObjekt);
                                                }
                                            }, formValidator, req));
        }
        catch (StdException e)
        {
            log.error(buildLogMessage(req, e.getMessage()));
        }

        // Lizenz-Pruefung (Wenn die max. Anzahl der Schleife erreicht ist,
        // gibts
        // einen Fehler
        try
        {
            if (!istCreateSchleifeLizensiert()
                            && (req.getId(Parameters.SCHLEIFE_ID) == 0))
            {
                formValidator.addCustomError(
                                new FormObject(Parameters.TEXT_NAME,
                                                "Schleifenname"),
                                "Die maximale Anzahl der zu erstellenden Schleifen in dieser Lizenz ist erreicht.",
                                null);
            }
        }
        catch (StdException e)
        {
            log.error(buildLogMessage(req, e.getMessage()));
        }

        // 2006-06-01 CKL: Es kommt ein Fehler, wenn versucht wird, die
        // Schleife anzulegen, wenn KEIN Kontext Organisationseinheit existiert
        if (req.getUserBean().getCtxOE() == null)
        {
            formValidator.addCustomError(
                            new FormObject(Parameters.TEXT_NAME, "Name"),
                            "Sie versuchen eine Schleife anzulegen, ohne dass Sie sich im Kontext einer Organisationseinheit befinden.",
                            null);
        }

        formValidator.run();

        // Es sind keine Fehler aufgetreten
        if ((formValidator.getTotalErrors() == 0) && (schleifeVO != null))
        {

            // Versuchen, Einstellungen des Objekts zu setzen
            try
            {
                schleifeVO.setBeschreibung(req
                                .getStringForParam(Parameters.TEXT_BESCHREIBUNG));
                schleifeVO.setName(req.getStringForParam(Parameters.TEXT_NAME));
                schleifeVO.setKuerzel(req
                                .getStringForParam(Parameters.TEXT_KUERZEL));
                schleifeVO.setFuenfton(req
                                .getStringForParam(Parameters.TEXT_FUENFTON));
                schleifeVO.setOrganisationsEinheitId(req.getUserBean()
                                .getCtxOE().getOrganisationsEinheitId());
                schleifeVO.setStatusreportFuenfton(req
                                .getBoolForParam(Parameters.CB_STATUSREPORT_FUENFTON));
                schleifeVO.setDruckerKuerzel(req
                                .getStringForParam(Parameters.TEXT_DRUCKER_KUERZEL));

                long rueckmeldeIntervall = req
                                .getIntForParam(Parameters.TEXT_RUECKMELDEINTERVALL);

                if (rueckmeldeIntervall == 0)
                {
                    rueckmeldeIntervall = dbResource.getDaoFactory()
                                    .getSystemKonfigurationDAO()
                                    .readKonfiguration().getAlarmTimeout();
                }

                schleifeVO.setRueckmeldeintervall(rueckmeldeIntervall);

                // Folgeschleife definieren
                if (isFolgeschleifeAktiv())
                {
                    int idFolgeschleife = req
                                    .getIntForParam(Parameters.SELECT_FOLGESCHLEIFE);

                    if (idFolgeschleife > 0)
                    {
                        schleifeVO.setFolgeschleifeId(new SchleifeId(
                                        idFolgeschleife));
                    }
                    else
                    {
                        schleifeVO.setFolgeschleifeId(null);
                    }
                }

                // Abrechenbar
                if (req.getUserBean().getAccessControlList()
                                .isSchleifeAbrechnungFestlegenErlaubt())
                {
                    schleifeVO.setAbrechenbar(req
                                    .getBoolForParam(Parameters.CB_ABRECHENBAR));
                }
                else
                {
                    schleifeVO.setAbrechenbar(true);
                }

                if (req.getId(Parameters.SCHLEIFE_ID) > 0)
                {
                    log.info(buildLogMessage(
                                    req,
                                    "Aendere Schleife ["
                                                    + req.getId(Parameters.SCHLEIFE_ID)
                                                    + "]"));
                    schleifeVO = taoBV.updateSchleife(schleifeVO);
                }
                else
                {
                    log.info(buildLogMessage(req, "Erstelle Schleife ["
                                    + schleifeVO + "]"));
                    schleifeVO = taoBV.createSchleife(schleifeVO);
                }

                // Nach dem Erstellen der Schleife überprüfen, ob zirkuläre
                // Abhängigkeiten bei aktivierter Folgeschleife entstanden sind
                if (isFolgeschleifeAktiv())
                {
                    // Zirkuläre Abhängigkeit überprüfen
                    SchleifeBO boSchleife = dbResource.getBoFactory()
                                    .getSchleifeBO();
                    ArrayList<SchleifeVO> alFolgeschleifen = new ArrayList<SchleifeVO>();

                    if (boSchleife.findFolgeSchleifenListe(
                                    schleifeVO.getSchleifeId(),
                                    alFolgeschleifen) != SchleifeBO.FOLGESCHLEIFEN_STATUS.OK)
                    {
                        schleifeVO.setFolgeschleifeId(null);
                        schleifeVO = taoBV.updateSchleife(schleifeVO);
                        StringBuffer sbFolgeschleifenListe = new StringBuffer();

                        for (int i = 0, m = alFolgeschleifen.size(); i < m; i++)
                        {
                            sbFolgeschleifenListe.append(alFolgeschleifen
                                            .get(i).getDisplayName());

                            if ((i + 1) != m)
                            {
                                sbFolgeschleifenListe.append(" => ");
                            }
                        }

                        throw new StdException(
                                        "Die Schleife wurde zwar geändert/erstellt, allerdings wurde die Folgeschleife nicht gesetzt. Es entstand eine zirkuläre Abhängigkeit. Schleifen und deren Folgeschleifen: "
                                                        + sbFolgeschleifenListe);
                    }
                }

                if (null == schleifeVO)
                {
                    req.getErrorBean()
                                    .addMessage("Die Schleife konnte nicht erzeugt bzw. geupdatet werden. Bitte überprüfen Sie, ob eine Schleife mit dem Kürzel oder dem Folgeruf nicht schon existiert.");
                }
                else
                {
                    req.setId(Parameters.SCHLEIFE_ID, schleifeVO.getBaseId()
                                    .getLongValue());
                }
            }
            catch (StdException e)
            {
                log.error(buildLogMessage(req, e.getMessage()));
                req.getErrorBean().addMessage(e);
            }
        }
    }

    /**
     * Updated die Rollen
     */
    protected void doUpdateRollenKontext(final RequestResources req)
    {
        if (!req.isActionAllowed(
                        RechtId.PERSONEN_ROLLEN_ZUWEISEN,
                        "Sie besitzen nicht das Recht, Personen in dieser Schleife und deren Rollen zu ändern."))
        {
            return;
        }

        log.debug(buildLogMessage(req, "Kontext soll geupdatet werden"));

        if (req.getId(Parameters.ROLLE_ID) > 0)
        {
            PersonVO[] personenVO = null;

            try
            {
                RolleVO rolle = daoRolle.findRolleById(new RolleId(req
                                .getId(Parameters.ROLLE_ID)));
                personenVO = doUpdateKontextGetPersonen(req);

                long[] personenSelected = req
                                .getLongArrayForParam(Parameters.SELECT_PERSONEN_ASSIGNED);

                // Zuerst einmal alle Personen aus den Rollen löschen
                if (null != personenVO)
                {
                    for (int i = 0, m = personenVO.length; i < m; i++)
                    {
                        doUpdateKontextRemovePersonInRolle(req,
                                        personenVO[i].getPersonId(),
                                        rolle.getRolleId());
                    }
                }

                // Selectierte Personen hinzufuegen
                if (null != personenSelected)
                {
                    for (int i = 0, m = personenSelected.length; i < m; i++)
                    {
                        PersonId personId = new PersonId(personenSelected[i]);
                        doUpdateKontextAddPersonInRolle(req, personId,
                                        rolle.getRolleId());
                    }
                }
            }
            catch (StdException e)
            {
                log.error(buildLogMessage(req, e.getMessage()));
            }
        }
    }

    /**
     * Entfernt eine Person ueber einen Link "Entfernen" aus der
     * Rollen-/Schleifen-Kombination
     */
    public void doRemoveRolleFromPerson(final RequestResources req)
    {
        if (!req.isActionAllowed(
                        RechtId.PERSONEN_ROLLEN_ZUWEISEN,
                        "Sie besitzen nicht das Recht, Personen in dieser Schleife und deren Rollen zu ändern."))
        {
            return;
        }

        PersonId personId = new PersonId(req.getId(Parameters.PERSON_ID));
        RolleId rolleId = new RolleId(req.getId(Parameters.ROLLE_ID));
        doUpdateKontextRemovePersonInRolle(req, personId, rolleId);
        log.debug(buildLogMessage(req, "Person [" + personId + "] Rolle ["
                        + rolleId + "] aus Schleife entfernt"));
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
        PersonVO[] arrPersonen = null;

        try
        {
            arrPersonen = daoPerson.findPersonenByRolleInSchleife(new RolleId(
                            req.getId(Parameters.ROLLE_ID)),
                            new SchleifeId(req.getId(Parameters.SCHLEIFE_ID)));
        }
        catch (StdException e)
        {
            req.getErrorBean().addMessage(e);
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
        SchleifeId schleifeId = new SchleifeId(
                        req.getId(Parameters.SCHLEIFE_ID));

        if (!taoRecht.removePersonInRolleFromSchleife(_personId, _rolleId,
                        schleifeId))
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
        SchleifeId schleifeId = new SchleifeId(
                        req.getId(Parameters.SCHLEIFE_ID));

        try
        {
            log.debug(buildLogMessage(req, "Person " + _personId
                            + " soll in Rolle " + _rolleId + "  der Schleife "
                            + schleifeId + " hinzugefuegt werden"));
            // Überprüfen, ob die Person noch nicht der Schleife zugewiesen
            // wurde
            if (!daoPerson.hatPersonRolleInSchleifeNichtVererbt(_personId,
                            _rolleId, schleifeId))
            {
                log.debug(buildLogMessage(req, "Person " + _personId
                                + " ist in Rolle " + _rolleId + " in Schleife "
                                + schleifeId
                                + " noch nicht vorhanden. Fuege hinzu."));
                if (!taoRecht.addPersonInRolleToSchleife(_personId, _rolleId,
                                schleifeId))
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
     * Testet ob die aktuelle Lizenz dazu reicht noch eine weitere Schleife
     * anzulegen
     * 
     * @return
     * @throws StdException
     */
    public boolean istCreateSchleifeLizensiert() throws StdException
    {
        if (getLicense() == null)
        {
            throw new StdException("Es wurde keine Lizenz definiert!");
        }

        if (getLicense().getSchleifen() > 0)
        {
            return (daoSchleife.countSchleifen() < getLicense().getSchleifen());
        }

        return true;
    }

    /**
     * Legt fest, ob das Feld {@link SchleifeVO#setDruckerKuerzel(String)}
     * gefüllt wird
     * 
     * @param isDruckerKuerzelAktiv
     */
    public void setDruckerKuerzelAktiv(boolean isDruckerKuerzelAktiv)
    {
        this.isDruckerKuerzelAktiv = isDruckerKuerzelAktiv;
    }

    /**
     * Liefert, ob das Feld {@link SchleifeVO#setDruckerKuerzel(String)} gefüllt
     * wird
     * 
     * @return
     */
    public boolean isDruckerKuerzelAktiv()
    {
        return isDruckerKuerzelAktiv;
    }

    /**
     * Legt fest, ob das Feld {@link SchleifeVO#setFolgeschleifeId(SchleifeId)}
     * gefüllt wird
     * 
     * @return
     */
    public void setFolgeschleifeAktiv(boolean isFolgeschleifeAktiv)
    {
        this.isFolgeschleifeAktiv = isFolgeschleifeAktiv;
    }

    /**
     * Liefert, ob das Feld {@link SchleifeVO#setFolgeschleifeId(SchleifeId)}
     * gefüllt wird
     * 
     * @return
     */
    public boolean isFolgeschleifeAktiv()
    {
        return isFolgeschleifeAktiv;
    }

    /**
     * Legt fest, ob das Feld {@link SchleifeVO#getRueckmeldeintervall()}
     * gefüllt wird
     * 
     * @return
     */
    public void setRueckmeldeintervallAktiv(boolean isRueckmeldeintervallAktiv)
    {
        this.isRueckmeldeintervallAktiv = isRueckmeldeintervallAktiv;
    }

    /**
     * Liefert, ob das Feld {@link SchleifeVO#getRueckmeldeintervall()} gefüllt
     * wird
     * 
     * @return
     */
    public boolean isRueckmeldeintervallAktiv()
    {
        return isRueckmeldeintervallAktiv;
    }
}
