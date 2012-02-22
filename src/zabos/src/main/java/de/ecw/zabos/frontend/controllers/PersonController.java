package de.ecw.zabos.frontend.controllers;

import org.apache.log4j.Logger;

import de.ecw.zabos.bo.BaumViewBO;
import de.ecw.zabos.bo.KontextMitRolleBO;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.FormObject;
import de.ecw.zabos.frontend.FormValidator;
import de.ecw.zabos.frontend.Navigation;
import de.ecw.zabos.frontend.Parameters;
import de.ecw.zabos.frontend.beans.DataBean;
import de.ecw.zabos.frontend.beans.DateBean;
import de.ecw.zabos.frontend.controllers.helpers.deletedobjects.CheckExistingObject;
import de.ecw.zabos.frontend.controllers.helpers.deletedobjects.IOnRenameDeletedObject;
import de.ecw.zabos.frontend.controllers.helpers.deletedobjects.PropertyHandlerEmailImpl;
import de.ecw.zabos.frontend.controllers.helpers.deletedobjects.PropertyHandlerNameImpl;
import de.ecw.zabos.frontend.objects.BaseControllerAdapter;
import de.ecw.zabos.frontend.ressources.RequestResources;
import de.ecw.zabos.frontend.sql.ho.AccessControllerHO;
import de.ecw.zabos.frontend.sql.vo.KontextMitRolleVO;
import de.ecw.zabos.frontend.types.KontextType;
import de.ecw.zabos.sql.dao.BereichDAO;
import de.ecw.zabos.sql.dao.FunktionstraegerDAO;
import de.ecw.zabos.sql.dao.OrganisationDAO;
import de.ecw.zabos.sql.dao.OrganisationsEinheitDAO;
import de.ecw.zabos.sql.dao.PersonDAO;
import de.ecw.zabos.sql.dao.RolleDAO;
import de.ecw.zabos.sql.dao.TelefonDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.tao.BenutzerVerwaltungTAO;
import de.ecw.zabos.sql.vo.BaseIdVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.TelefonVO;
import de.ecw.zabos.types.Pin;
import de.ecw.zabos.types.TelefonNummer;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.BereichId;
import de.ecw.zabos.types.id.FunktionstraegerId;
import de.ecw.zabos.types.id.OrganisationsEinheitId;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.types.id.RolleId;
import de.ecw.zabos.types.id.TelefonId;
import de.ecw.zabos.util.StringUtils;

/**
 * Controller für die Personen
 * 
 * @author ckl
 */
public class PersonController extends BaseControllerAdapter
{
    public static final String DO_UPDATE_ABWESENHEITSZEIT = "doUpdateAbwesenheitszeit";

    public static final String DO_DELETE_TELEFON = "doDeleteTelefon";

    public static final String DO_UPDATE_TELEFON = "doUpdateTelefon";

    public static final String DO_DELETE_PERSON = "doDeletePerson";

    public static final String DO_UPDATE_PERSON = "doUpdatePerson";

    // Serial
    final static long serialVersionUID = 1209312049;

    // Logger-Instanz
    private final static Logger log = Logger.getLogger(PersonController.class);

    protected OrganisationDAO daoOrganisation = null;

    protected OrganisationsEinheitDAO daoOE = null;

    protected BenutzerVerwaltungTAO taoBV = null;

    protected RolleDAO daoRolle = null;

    protected PersonDAO daoPerson = null;

    protected TelefonDAO daoTelefon = null;

    protected KontextMitRolleBO boKontextMitRolle = null;

    protected FunktionstraegerDAO daoFunktionstraeger = null;

    protected BereichDAO daoBereich = null;

    private boolean emailRequired = false;

    public PersonController(final DBResource dbResource)
    {
        super(dbResource);

        // Verzeichnis mit den Templates setzen
        this.setActionDir(Navigation.ACTION_DIR_PERSON);

        // TAO/DAO-Factory initalisieren
        daoOrganisation = dbResource.getDaoFactory().getOrganisationDAO();
        daoOE = dbResource.getDaoFactory().getOrganisationsEinheitDAO();
        taoBV = dbResource.getTaoFactory().getBenutzerVerwaltungTAO();
        daoRolle = dbResource.getDaoFactory().getRolleDAO();
        daoPerson = dbResource.getDaoFactory().getPersonDAO();
        daoTelefon = dbResource.getDaoFactory().getTelefonDAO();
        boKontextMitRolle = dbResource.getBoFactory().getKontextMitRolleBO();
        daoFunktionstraeger = dbResource.getDaoFactory()
                        .getFunktionstraegerDAO();
        daoBereich = dbResource.getDaoFactory().getBereichDAO();
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
        // Person-ID aufloesen
        // Bevorzugt wird der uebergebene Parameter Personen-ID
        if (req.getServletRequest().getParameter(Parameters.PERSON_ID) != null)
        {
            req.setId(Parameters.PERSON_ID,
                            req.getLongForParam(Parameters.PERSON_ID));
        }
        else if (req.getUserBean().getCtxPerson() != null)
        {
            req.setId(Parameters.PERSON_ID, req.getUserBean().getCtxPerson()
                            .getBaseId().getLongValue());
        }
        else
        {
            req.setId(Parameters.PERSON_ID, 0);
        }

        // Rolle-Id aufloesen
        if (req.getServletRequest().getParameter(Parameters.TELEFON_ID) != null)
        {
            req.setId(Parameters.TELEFON_ID,
                            req.getLongForParam(Parameters.TELEFON_ID));
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

        final AccessControllerHO accessController = req.buildAccessController();
        PersonId personId = null;

        // Zugriffe auf die Personenbezogenen Daten setzen
        if (req.getId(Parameters.PERSON_ID) > 0)
        {
            personId = new PersonId(req.getId(Parameters.PERSON_ID));
        }

        accessController.updateRechteFuerPersonenBearbeiten(personId);

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
        PersonVO personVO = null;
        TelefonVO telefonVO = null;
        TelefonVO[] telefoneVO = null;
        PersonVO erstelltVonPersonVO = null;
        DataBean beanTelefoneAvailable = new DataBean();
        DataBean beanOrganisationseinheitenAvailable = new DataBean();
        DataBean beanFunktionstraegerAvailable = new DataBean();
        DataBean beanBereicheAvailable = new DataBean();
        BaumViewBO treeView = req.getDbResource().getBoFactory()
                        .getBaumViewBO();

        req.getServletRequest().setAttribute(Parameters.NAVIGATION_TREE,
                        treeView.findTreeView());

        /**
         * Eine Personen-Id ist gesetzt
         */
        if (req.getId(Parameters.PERSON_ID) > 0)
        {
            try
            {
                personVO = daoPerson.findPersonById(new PersonId(req
                                .getId(Parameters.PERSON_ID)));

                if (personVO == null)
                {
                    req.getErrorBean()
                                    .addMessage("Die Person mit der ID "
                                                    + req.getId(Parameters.PERSON_ID)
                                                    + " konnte nicht gefunden werden.");
                }
                else
                {
                    if (personVO.getErstelltVon() != null)
                    {
                        erstelltVonPersonVO = daoPerson.findPersonById(personVO
                                        .getErstelltVon());
                    }

                    telefoneVO = daoTelefon
                                    .findTelefoneByPersonId(new PersonId(
                                                    req.getId(Parameters.PERSON_ID)));
                    beanTelefoneAvailable.setData(telefoneVO);
                    req.getUserBean().setCtxPerson(personVO);

                    if (personVO.getPassword() != null)
                    {
                        req.setData(Parameters.IS_ABLE_TO_LOGIN, "1");
                    }

                    if (personVO.getAbwesendBis() != null)
                    {
                        if (personVO.getAbwesendBis().getTimeStamp() != 0)
                        {
                            req.setData(Parameters.IS_ABWESEND_BIS, "1");
                        }
                    }
                }
            }
            catch (StdException e)
            {
                req.getErrorBean().addMessage(e);
            }
        }
        else
        {
            if (req.getLongForParam(Parameters.ROLLE_ID) > 0)
            {

                KontextMitRolleVO voKontextMitRolle = new KontextMitRolleVO();

                long idToUse = 0;
                long idTemp = 0;
                int kontextType = KontextType.SYSTEM;

                if ((idTemp = req.getLongForParam(Parameters.O_ID)) > 0)
                {
                    kontextType = KontextType.ORGANISATION;
                    idToUse = idTemp;
                }

                if ((idTemp = req.getLongForParam(Parameters.OE_ID)) > 0)
                {
                    kontextType = KontextType.ORGANISATIONSEINHEIT;
                    idToUse = idTemp;
                }

                if ((idTemp = req.getLongForParam(Parameters.SCHLEIFE_ID)) > 0)
                {
                    kontextType = KontextType.SCHLEIFE;
                    idToUse = idTemp;
                }

                try
                {
                    voKontextMitRolle = boKontextMitRolle.findKontextMitRolle(
                                    kontextType, idToUse,
                                    req.getLongForParam(Parameters.ROLLE_ID));
                }
                catch (StdException e)
                {
                    log.error(e);
                }

                req.getServletRequest().setAttribute(
                                Parameters.OBJ_KONTEXT_MIT_ROLLE,
                                voKontextMitRolle);
            }
        }

        if (req.getId(Parameters.TELEFON_ID) > 0)
        {
            try
            {
                telefonVO = daoTelefon.findTelefonById(new TelefonId(req
                                .getId(Parameters.TELEFON_ID)));
            }
            catch (StdException e)
            {
                req.getErrorBean()
                                .addMessage("Das Telefon mit der ID "
                                                + req.getId(Parameters.TELEFON_ID)
                                                + " konnte nicht gefunden werden.");
            }
        }

        try
        {
            beanOrganisationseinheitenAvailable.setData(daoOE.findAll());
            beanFunktionstraegerAvailable
                            .setData(daoFunktionstraeger.findAll());
            beanBereicheAvailable.setData(daoBereich.findAll());
        }
        catch (StdException e)
        {
            log.error(e);
        }

        // Attribute setzen
        req.setData(Parameters.ARR_TELEFONE_AVAILABLE, beanTelefoneAvailable)
                        .setData(Parameters.OBJ_PERSON_ERSTELLT_VON,
                                        erstelltVonPersonVO)
                        .setData(Parameters.OBJ_PERSON, personVO)
                        .setData(Parameters.OBJ_TELEFON, telefonVO)
                        .setData(Parameters.ARR_ORGANISATIONSEINHEITEN_AVAILABLE,
                                        beanOrganisationseinheitenAvailable)
                        .setData(Parameters.ARR_FUNKTIONSTRAEGER_AVAILABLE,
                                        beanFunktionstraegerAvailable)
                        .setData(Parameters.ARR_BEREICHE_AVAILABLE,
                                        beanBereicheAvailable);
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
            if (req.getRequestDo().equals(DO_UPDATE_PERSON))
            {
                doUpdatePerson(req);
            }
            else if (req.getRequestDo().equals(DO_DELETE_PERSON))
            {
                doDeletePerson(req);
            }
            else if (req.getRequestDo().equals(DO_UPDATE_TELEFON))
            {
                doUpdateTelefon(req);
            }
            else if (req.getRequestDo().equals(DO_DELETE_TELEFON))
            {
                doDeleteTelefon(req);
            }
            else if (req.getRequestDo().equals(DO_UPDATE_ABWESENHEITSZEIT))
            {
                doUpdateAbwesenheit(req);
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
     * @since 2006-06-01 CKL: Sicherheitsabfrage, ob Person berechtigt ist,
     *        diese Person zu löschen
     * @since 2006-06-01 CKL: Person kann sich nicht mehr selbst löschen
     * @author ckl
     */
    protected void doDeletePerson(final RequestResources req)
    {
        // 2006-06-01 CKL: Sicherheitsabfrage, ob Person berechtigt ist, diese
        // Person zu löschen
        if (!req.isActionAllowed(RechtId.PERSON_ANLEGEN_LOESCHEN,
                        "Sie besitzen nicht das Recht, diese Person zu löschen."))
        {
            return;
        }

        long id = req.getId(Parameters.PERSON_ID);

        // Objekt mit einer ID wurde übergeben
        if (id > 0)
        {
            long personIdSessionUser = req.getUserBean().getPerson()
                            .getPersonId().getLongValue();

            // 2006-06-01 CKL: Person kann sich nicht mehr selbst löschen
            if (personIdSessionUser == req.getId(Parameters.PERSON_ID))
            {
                req.getErrorBean().addMessage(
                                "Sie können sich nicht selbst löschen.");
            }
            else
            {
                log.debug(buildLogMessage(req, "Loesche Person [" + id + "]"));

                taoBV.deletePerson(new PersonId(id));

                // Bestehende Telefone der Person als gelöscht markieren
                try
                {
                    TelefonVO[] telefoneVO = daoTelefon
                                    .findTelefoneByPersonId(new PersonId(
                                                    req.getId(Parameters.PERSON_ID)));

                    if (telefoneVO != null)
                    {
                        for (int i = 0, m = telefoneVO.length; i < m; i++)
                        {
                            TelefonVO telefonVO = telefoneVO[i];
                            log.debug(buildLogMessage(req, "Loesche Telefon ["
                                            + telefonVO.getTelefonId() + "]"));
                            taoBV.deleteTelefon(telefonVO.getTelefonId());
                        }
                    }
                }
                catch (StdException e)
                {
                    log.error(buildLogMessage(req, e.getMessage()));
                }

                req.setId(Parameters.PERSON_ID, 0);
            }
        }
    }

    /**
     * Ändert die Abwesenheitszeiten einer Person
     */
    protected void doUpdateAbwesenheit(final RequestResources req)
    {
        // Person
        PersonVO personVO = null;
        // Abwesend von
        DateBean dateAbwesendVon = new DateBean();
        // Abwesend bis
        DateBean dateAbwesendBis = new DateBean();

        if (req.getId(Parameters.PERSON_ID) > 0)
        {
            // Person finden
            try
            {
                personVO = daoPerson.findPersonById(new PersonId(req
                                .getId(Parameters.PERSON_ID)));

                // 2006-06-01 CKL: 2006-06-01 CKL: Sicherheitsabfrage, ob Person
                // berechtigt ist, Person anzulegen
                if (personVO != null)
                {
                    // User will sich selbst �ndern
                    if (personVO.getPersonId().getLongValue() == req
                                    .getUserBean().getPerson().getPersonId()
                                    .getLongValue())
                    {
                        if (!req.isActionAllowed(
                                        RechtId.EIGENE_ABWESENHEITSZEITEN_AENDERN,
                                        "Sie sind nicht dazu berechtigt, Ihre eigene Abwesenheitszeit zu ändern."))
                        {
                            return;
                        }
                    }
                    else
                    {
                        // User will andere Person aendern
                        if (!req.isActionAllowed(RechtId.PERSON_AENDERN,
                                        "Sie sind nicht dazu berechtigt, diese Person zu ändern."))
                        {
                            return;
                        }
                    }
                }

            }
            catch (StdException e)
            {
                req.getErrorBean().addMessage(e);
            }
        }

        // Eingabe validieren
        FormValidator formValidator = req.buildFormValidator();

        // Wenn Benutzer abwesend ist, Datum und Zeit prüfen
        if (req.getIntForParam(Parameters.IS_ABWESEND_BIS) != 0)
        {
            // 2006-07-12 CKL: Abwesend von
            FormObject foAbwesendVonDatum = new FormObject(
                            Parameters.TEXT_ABWESEND_VON_DATUM,
                            "Abwesend von: Datum");
            foAbwesendVonDatum.setFlag(FormObject.VALID_DATE_FORMAT);
            foAbwesendVonDatum.setFlag(FormObject.VALID_DATE);
            formValidator.add(foAbwesendVonDatum);

            FormObject foAbwesendVonZeit = new FormObject(
                            Parameters.TEXT_ABWESEND_VON_ZEIT,
                            "Abwesend von: Zeit");
            foAbwesendVonZeit.setFlag(FormObject.VALID_TIME_FORMAT);
            foAbwesendVonZeit.setFlag(FormObject.VALID_TIME);
            formValidator.add(foAbwesendVonZeit);

            FormObject foAbwesendBisDatum = new FormObject(
                            Parameters.TEXT_ABWESEND_BIS_DATUM,
                            "Abwesend bis: Datum");
            foAbwesendBisDatum.setFlag(FormObject.VALID_DATE_FORMAT);
            foAbwesendBisDatum.setFlag(FormObject.VALID_DATE);
            formValidator.add(foAbwesendBisDatum);

            FormObject foAbwesendBisZeit = new FormObject(
                            Parameters.TEXT_ABWESEND_BIS_ZEIT,
                            "Abwesend bis: Zeit");
            foAbwesendBisZeit.setFlag(FormObject.VALID_TIME_FORMAT);
            foAbwesendBisZeit.setFlag(FormObject.VALID_TIME);
            formValidator.add(foAbwesendBisZeit);
        }

        formValidator.run();

        if (personVO == null)
        {
            req.getErrorBean().addMessage(
                            "Es konnte keine Person gefunden werden.");
        }
        else
        {
            if (formValidator.getTotalErrors() == 0)
            {
                // User ist auf abwesend gesetzt
                if (req.getIntForParam(Parameters.IS_ABWESEND_BIS) != 0)
                {
                    if ((req.getStringForParam(
                                    Parameters.TEXT_ABWESEND_BIS_DATUM).equals(
                                    "") == false)
                                    && (req.getStringForParam(
                                                    Parameters.TEXT_ABWESEND_BIS_ZEIT)
                                                    .equals("") == false
                                                    && (req.getStringForParam(
                                                                    Parameters.TEXT_ABWESEND_VON_DATUM)
                                                                    .equals("") == false) && (req
                                                    .getStringForParam(
                                                                    Parameters.TEXT_ABWESEND_VON_ZEIT)
                                                    .equals("") == false)))
                    {
                        dateAbwesendVon.setDate(req
                                        .getStringForParam(Parameters.TEXT_ABWESEND_VON_DATUM));
                        dateAbwesendVon.setTime(req
                                        .getStringForParam(Parameters.TEXT_ABWESEND_VON_ZEIT));
                        dateAbwesendBis.setDate(req
                                        .getStringForParam(Parameters.TEXT_ABWESEND_BIS_DATUM));
                        dateAbwesendBis.setTime(req
                                        .getStringForParam(Parameters.TEXT_ABWESEND_BIS_ZEIT));

                        long tsAbwesendBis = dateAbwesendBis.getTimestamp();
                        long tsAbwesendVon = dateAbwesendVon.getTimestamp();

                        log.debug(buildLogMessage(
                                        req,
                                        "Person ["
                                                        + req.getId(Parameters.PERSON_ID)
                                                        + "]  ist abwesend von "
                                                        + dateAbwesendVon
                                                                        .getDate()
                                                        + ", "
                                                        + dateAbwesendVon
                                                                        .getTime()
                                                        + " bis "
                                                        + dateAbwesendBis
                                                                        .getDate()
                                                        + ", "
                                                        + dateAbwesendBis
                                                                        .getTime()));
                        personVO.setAbwesendVon(new UnixTime(tsAbwesendVon));
                        personVO.setAbwesendBis(new UnixTime(tsAbwesendBis));
                    }
                }
                else
                { // Abwesenheitszeit deaktivieren
                    personVO.setAbwesendVon(new UnixTime(0));
                    personVO.setAbwesendBis(new UnixTime(0));
                }

                log.info("Aendere Person [" + personVO.getBaseId() + "]");
                taoBV.updatePerson(personVO);
            }
        }
    }

    /**
     * Updatet ein Objekt dieser View
     */
    protected void doUpdatePerson(final RequestResources req)
    {
        PersonVO personVO = null;
        PersonVO personVOinDatenbank = null;

        // ID wurde gesetzt => Person soll geaendert werden
        if (req.getId(Parameters.PERSON_ID) > 0)
        {
            // Person finden
            try
            {
                personVO = daoPerson.findPersonById(new PersonId(req
                                .getId(Parameters.PERSON_ID)));

                // 2006-06-01 CKL: 2006-06-01 CKL: Sicherheitsabfrage, ob Person
                // berechtigt ist, Person anzulegen
                if (personVO != null)
                {
                    // User will sich selbst aendern
                    if (personVO.getPersonId().getLongValue() == req
                                    .getUserBean().getPerson().getPersonId()
                                    .getLongValue())
                    {
                        if (!req.isActionAllowed(RechtId.EIGENE_PERSON_AENDERN,
                                        "Sie sind nicht dazu berechtigt, Ihre eigenen Daten zu ändern."))
                        {
                            return;
                        }
                    }
                    else
                    {
                        // User will andere Person ändern
                        if (!req.isActionAllowed(RechtId.PERSON_AENDERN,
                                        "Sie sind nicht dazu berechtigt, diese Person zu ändern."))
                        {
                            return;
                        }
                    }
                }
            }
            catch (StdException e)
            {
                req.getErrorBean().addMessage(e);
            }
        }
        else
        {
            // Es soll eine neue Person erstellt werden
            personVO = daoPerson.getObjectFactory().createPerson();

            // 2006-06-01 CKL: 2006-06-01 CKL: Sicherheitsabfrage, ob Person
            // berechtigt ist, Person anzulegen
            if (!req.isActionAllowed(
                            RechtId.PERSON_ANLEGEN_LOESCHEN,
                            "Sie sind nicht dazu berechtigt, eine Person im aktuellen Kontext zu erstellen."))
            {
                return;
            }

            log.debug(buildLogMessage(req,
                            "Es soll eine neue Person angelegt werden."));
        }

        // Eingabe validieren
        FormValidator formValidator = req.buildFormValidator();
        // Benutzername wird gebraucht
        FormObject foUsername = new FormObject(Parameters.TEXT_NAME,
                        "Benutzerame");
        formValidator.add(foUsername);
        // Vorname
        FormObject foVorname = new FormObject(Parameters.TEXT_VORNAME,
                        "Vorname");
        formValidator.add(foVorname);
        // Nachname
        FormObject foNachname = new FormObject(Parameters.TEXT_NACHNAME,
                        "Nachname");
        formValidator.add(foNachname);

        if (isEmailRequired())
        {
            FormObject foEmail = new FormObject(Parameters.TEXT_EMAIL, "E-Mail");
            formValidator.add(foEmail);
        }

        // Existiert Email für einen anderen Benutzer schon?
        try
        {
            if (req.getStringForParam(Parameters.TEXT_EMAIL).toString()
                            .equals("") == false)
            {
                personVOinDatenbank = daoPerson.findPersonByEmail(req
                                .getStringForParam(Parameters.TEXT_EMAIL));

                CheckExistingObject.handle(personVO, personVOinDatenbank,
                                new PropertyHandlerEmailImpl("Person",
                                                isRenameDeletedObject(),
                                                new IOnRenameDeletedObject()
                                                {
                                                    public BaseIdVO renameDeletedObject(
                                                                    BaseIdVO datenbankObjekt)
                                                    {
                                                        return taoBV.updatePerson((PersonVO) datenbankObjekt);
                                                    }
                                                }, formValidator, req));

            }

            // Existiert der Benutzername bereits in der Datenbank
            if (req.getStringForParam(Parameters.TEXT_NAME).equals("") == false)
            {
                personVOinDatenbank = daoPerson.findPersonByName(req
                                .getStringForParam(Parameters.TEXT_NAME));

                CheckExistingObject.handle(personVO, personVOinDatenbank,
                                new PropertyHandlerNameImpl("Person",
                                                isRenameDeletedObject(),
                                                new IOnRenameDeletedObject()
                                                {
                                                    public BaseIdVO renameDeletedObject(
                                                                    BaseIdVO datenbankObjekt)
                                                    {
                                                        return taoBV.updatePerson((PersonVO) datenbankObjekt);
                                                    }
                                                }, formValidator, req));
            }
        }
        catch (StdException e)
        {
            log.error(buildLogMessage(req, e.getMessage()));
        }

        // Lizenz-Pruefung (Wenn die max. Anzahl der Personen eingetragen ist,
        // gibts
        // einen Fehler
        try
        {
            if (!istCreatePersonenLizensiert()
                            && (req.getId(Parameters.PERSON_ID) == 0))
            {
                formValidator.addCustomError(
                                new FormObject(Parameters.TEXT_NAME,
                                                "Benutzername"),
                                "Die maximale Anzahl der zu erstellenden Personen in dieser Lizenz ist erreicht.",
                                null);
            }
        }
        catch (StdException e)
        {
            log.error(buildLogMessage(req, e.getMessage()));
        }

        formValidator.run();

        // Es sind keine Fehler aufgetreten und Person-Objekt existiert
        if ((formValidator.getTotalErrors() == 0) && (null != personVO))
        {
            // Versuchen, Einstellungen des Objekts zu setzen
            try
            {
                personVO.setBeschreibung(req
                                .getStringForParam(Parameters.TEXT_BESCHREIBUNG));
                personVO.setName(req.getStringForParam(Parameters.TEXT_NAME));
                personVO.setVorname(req
                                .getStringForParam(Parameters.TEXT_VORNAME));
                personVO.setNachname(req
                                .getStringForParam(Parameters.TEXT_NACHNAME));

                // 2010-05-05 CKL: Ist Person in Folgeschleife
                personVO.setInFolgeschleife(req
                                .getBoolForParam(Parameters.CB_IS_IN_FOLGESCHLEIFE));

                // 2007-06-18 CKL: Funktionstraeger
                personVO.setFunktionstraegerId(new FunktionstraegerId(
                                req.getLongForParam(Parameters.TEXT_FUNKTIONSTRAEGER_ID)));

                personVO.setBereichId(new BereichId(req
                                .getLongForParam(Parameters.TEXT_BEREICH_ID)));
                // Keine Email-Adresse
                if (req.getStringForParam(Parameters.TEXT_EMAIL).equals(""))
                {
                    personVO.setEmail(null);
                }
                else
                {
                    personVO.setEmail(req
                                    .getStringForParam(Parameters.TEXT_EMAIL));
                }

                personVO.setPin(new Pin(req
                                .getStringForParam(Parameters.TEXT_PIN)));

                // Benutzer darf die Kostenstelle definieren
                if (req.isActionAllowed(RechtId.OE_KOSTENSTELLE_FESTLEGEN, null))
                {
                    int idKostenstelle = 0;
                    OrganisationsEinheitId voKostenstelleId = null;

                    if ((idKostenstelle = req
                                    .getIntForParam(Parameters.TEXT_OE_KOSTENSTELLE_ID)) != 0)
                    {
                        voKostenstelleId = new OrganisationsEinheitId(
                                        idKostenstelle);
                    }

                    personVO.setOEKostenstelle(voKostenstelleId);
                }

                // User darf sich einloggen
                if (req.getIntForParam(Parameters.IS_ABLE_TO_LOGIN_IN_WEBINTERFACE) != 0)
                {
                    // Passwort ist nicht leer => Neues Passwort setzen
                    if (req.getStringForParam(Parameters.TEXT_PASSWORD).equals(
                                    "") == false)
                    {
                        personVO.setPassword(StringUtils.md5(req
                                        .getStringForParam(Parameters.TEXT_PASSWORD)));
                    }
                }
                else
                { // Login deaktivieren
                    personVO.setPassword(null);
                }

                // ID existiert > Person ändern
                if (req.getId(Parameters.PERSON_ID) > 0)
                {
                    log.debug(buildLogMessage(
                                    req,
                                    "Aendere Person ["
                                                    + req.getId(Parameters.PERSON_ID)
                                                    + "]"));
                    personVO = taoBV.updatePerson(personVO);
                }
                // ID existiert nicht > Person erstellen
                else
                {
                    log.debug(buildLogMessage(req, "Erstelle Person ["
                                    + personVO + "]"));

                    personVO.setErstelltVon(req.getUserBean().getPerson()
                                    .getPersonId());

                    personVO = taoBV.createPerson(personVO);

                    // Telefone eintragen
                    if (personVO != null)
                    {
                        // Array mit den Daten der anzulegenden Telefone
                        String[] arrPostNeueTelefone = req
                                        .getStringArrayForParam(Parameters.ARR_TEXT_TELEFON_NUMMERN);
                        TelefonVO tempTelefonVO = null;
                        TelefonVO neuesTelefonVO = null;
                        TelefonNummer telefonNummer = null;

                        // Parameter arrTextTelefonNummern wurde gesetzt
                        if (arrPostNeueTelefone != null)
                        {
                            // Zuerst Telefone setzen
                            for (int i = 0, m = arrPostNeueTelefone.length; i < m; i++)
                            {

                                // Es wurde eine Telefonnummer eingetragen
                                if (!("".equals(arrPostNeueTelefone[i])))
                                {
                                    neuesTelefonVO = daoPerson
                                                    .getObjectFactory()
                                                    .createTelefon();
                                    telefonNummer = new TelefonNummer(
                                                    arrPostNeueTelefone[i]
                                                                    .toString());
                                    neuesTelefonVO.setPersonId(personVO
                                                    .getPersonId());
                                    neuesTelefonVO.setNummer(telefonNummer);
                                    // 2006-05-23 CKL: Neue Telefone werden
                                    // hardcodiert
                                    // aktiviert
                                    neuesTelefonVO.setAktiv(true);

                                    boolean bError = true;

                                    try
                                    {
                                        tempTelefonVO = daoTelefon
                                                        .findTelefonByNummer(neuesTelefonVO
                                                                        .getNummer());

                                        // Ueberpruefen, ob das Telefon mit
                                        // der angegebenen Nummer
                                        // noch nicht existiert bzw.
                                        // geloescht ist
                                        if (tempTelefonVO == null)
                                        {
                                            bError = false;
                                        }
                                        else
                                        {
                                            if (tempTelefonVO.getGeloescht() == true)
                                            {
                                                bError = false;
                                            }
                                        }

                                        // Standardmaessig ist es ein
                                        // Fehler,
                                        // wenn das
                                        // Telefon mit der Nummer schon
                                        // existiert
                                        if (bError)
                                        {
                                            personVOinDatenbank = daoPerson
                                                            .findPersonById(tempTelefonVO
                                                                            .getPersonId());
                                            req.getErrorBean()
                                                            .addMessage("Das Telefon mit der Nummer "
                                                                            + neuesTelefonVO.getNummer()
                                                                            + " konnte nicht erzeugt werden, da im System bereits diese Telefonnummer von "
                                                                            + personVOinDatenbank
                                                                                            .getDisplayName()
                                                                            + " benutzt wird.");
                                        }
                                        else
                                        {
                                            tempTelefonVO = taoBV
                                                            .createTelefon(neuesTelefonVO);

                                            if (tempTelefonVO == null)
                                            {
                                                req.getErrorBean()
                                                                .addMessage("Das Telefon mit der Nummer "
                                                                                + neuesTelefonVO.getNummer()
                                                                                + " konnte nicht erzeugt werden.");
                                            }
                                        }
                                    }
                                    catch (StdException e)
                                    {
                                        log.error(buildLogMessage(req,
                                                        e.getMessage()));
                                    }
                                }
                            }
                        }

                        // 2006-05-24 CKL: Automatisch der Person die
                        // passende Rolle
                        // zuweisen
                        if (req.getStringForParam(
                                        Parameters.B_ASSIGN_PERSON_TO_ROLLE)
                                        .length() > 0)
                        {
                            if (boKontextMitRolle
                                            .hatPersonRechtInSpeziellemKontext(
                                                            req.getUserBean()
                                                                            .getPerson()
                                                                            .getPersonId(),
                                                            req.getIntForParam(Parameters.INT_KONTEXT_TYPE),
                                                            req.getIntForParam(Parameters.INT_KONTEXT_ID),
                                                            RechtId.PERSON_ANLEGEN_LOESCHEN))
                            {
                                if (false == boKontextMitRolle
                                                .addPersonInRolleInSpeziellemKontext(
                                                                personVO.getPersonId(),
                                                                req.getIntForParam(Parameters.INT_KONTEXT_TYPE),
                                                                req.getIntForParam(Parameters.INT_KONTEXT_ID),
                                                                new RolleId(
                                                                                req.getLongForParam(Parameters.INT_ROLLE_ID))))
                                {
                                    req.getErrorBean()
                                                    .addMessage("Der erzeugten Person konnte die angegebene Rolle nicht zugewiesen werden.");
                                }
                                else
                                {
                                    // Request zusammenbauen, so dass auf
                                    // die naechste View
                                    // geleitet wird
                                    String szRedirectString = buildRedirectString(req);
                                    req.setForwardPage(szRedirectString);
                                }
                            }
                            else
                            {
                                req.getErrorBean()
                                                .addMessage("Sie besitzen nicht das Recht, eine Person mit der Rolle im ggw. Kontext zu erzeugen.");
                            }
                        }
                    }
                }

                // Person konnte erzeugt werden
                if (null == personVO)
                {
                    req.getErrorBean()
                                    .addMessage("Die Person konnte nicht erzeugt bzw. geändert werden. ");
                }
                else
                {
                    req.setId(Parameters.PERSON_ID, personVO.getBaseId()
                                    .getLongValue());

                    // Benutzereinstellungen dynamisch updaten
                    if (req.getUserBean().getPerson().getPersonId()
                                    .equals(personVO.getPersonId()))
                    {
                        req.getUserBean().setPerson(personVO);
                    }
                }
            }
            catch (StdException e)
            {
                req.getErrorBean().addMessage(e);
            }
        }
    }

    /**
     * Löscht ein Objekt dieser View
     */
    protected void doDeleteTelefon(RequestResources req)
    {
        // Objekt mit einer ID wurde �bergeben
        if (req.getId(Parameters.TELEFON_ID) > 0)
        {
            try
            {
                TelefonId telefonId = new TelefonId(
                                req.getId(Parameters.TELEFON_ID));
                // 2006-06-01 CKL: Sicherheitsabfrage, ob Person berechtigt ist,
                // dieses Telefon zu loeschen
                TelefonVO voTempTelefon = daoTelefon.findTelefonById(telefonId);

                if (voTempTelefon != null)
                {
                    // ggw. User will Telefon einer anderen Person aendern
                    if (voTempTelefon.getPersonId().getLongValue() != req
                                    .getUserBean().getPerson().getPersonId()
                                    .getLongValue())
                    {
                        // Andere Personen duerfen nicht geaendert werden
                        if (!req.isActionAllowed(
                                        RechtId.PERSON_AENDERN,
                                        "Sie sind nicht dazu berechtigt, andere Personen und deren Telefone zu ändern oder zu löschen."))
                        {
                            return;
                        }
                    }
                    // ggw. User will sein eigenes Telefon aendern
                    else
                    {
                        if (!req.isActionAllowed(
                                        RechtId.EIGENE_TELEFONE_AENDERN,
                                        "Sie sind nicht dazu berechtigt, ihre eigenen Telefone zu ändern."))
                        {
                            return;
                        }
                    }
                }

                // Benutzer darf Telefon loeschen
                taoBV.deleteTelefon(voTempTelefon.getTelefonId());
                req.setId(Parameters.TELEFON_ID, 0);
            }
            catch (StdException e)
            {
                log.error(buildLogMessage(req, e.getMessage()));
            }
        }
    }

    /**
     * Updatet ein Objekt dieser View
     * 
     */
    protected void doUpdateTelefon(final RequestResources req)
    {
        TelefonVO telefonVO = null;
        TelefonVO telefonTempVO = null;
        TelefonNummer telefonNummer = null;
        PersonVO personVO = null;

        // Eingabe validieren
        FormValidator formValidator = req.buildFormValidator();
        telefonNummer = new TelefonNummer(
                        req.getStringForParam(Parameters.TEXT_NUMMER));

        // Telefonnummer validieren
        if (telefonNummer.equals(TelefonNummer.UNBEKANNT))
        {
            formValidator.addCustomError(
                            new FormObject(Parameters.TEXT_NUMMER,
                                            "Telefonnummer"),
                            "Die Telefonnummer muss ein gültiges Format der Form 00491* besitzen.",
                            null);
            telefonNummer = null;
        }
        else
        {
            try
            {
                telefonTempVO = daoTelefon.findTelefonByNummer(telefonNummer);

                // Neues Telefon anlegen bzw. existierendes ändern
                if (telefonTempVO != null)
                {
                    boolean bError = true;

                    if ((telefonTempVO.getTelefonId() != null)
                                    && (telefonTempVO.getTelefonId()
                                                    .getLongValue() == telefonTempVO
                                                    .getTelefonId()
                                                    .getLongValue()))
                    {
                        bError = false;
                    }

                    // Standardmaessig ist es ein Fehler, wenn der
                    // Organisationsname schon
                    // existiert
                    if (bError)
                    {
                        personVO = daoPerson.findPersonById(telefonTempVO
                                        .getPersonId());
                        formValidator.addCustomError(
                                        new FormObject(Parameters.TEXT_NUMMER,
                                                        "Telefonnummer"),
                                        "Die Telefonnummer wird bereits von der Person "
                                                        + personVO.getDisplayName()
                                                        + " benutzt. Bitte geben Sie eine andere Telefonnummer ein.",
                                        null);
                    }
                }
            }
            catch (StdException e)
            {
                log.error(buildLogMessage(req, e.getMessage()));
            }
        }

        // Kein Fehler aufgetreten
        if (formValidator.getTotalErrors() == 0)
        {
            if (req.getId(Parameters.TELEFON_ID) > 0)
            {
                try
                {
                    telefonVO = daoTelefon.findTelefonById(new TelefonId(req
                                    .getId(Parameters.TELEFON_ID)));

                    if (telefonVO != null)
                    {
                        // 2006-06-01 CKL: Sicherheitsabfrage, ob Person
                        // berechtigt ist, dieses Telefon zu
                        // aendern/hinzuzufuegen
                        if (telefonVO.getPersonId().getLongValue() != req
                                        .getUserBean().getPerson()
                                        .getPersonId().getLongValue())
                        {
                            // Andere Personen duerfen nicht geaendert werden
                            if (!req.isActionAllowed(
                                            RechtId.PERSON_AENDERN,
                                            "Sie sind nicht dazu berechtigt, andere Personen und deren Telefone zu ändern oder zu löschen."))
                            {
                                return;
                            }
                        }
                        // ggw. User will sein eigenes Telefon aendern
                        // D.h. ein Benutzer, der andere Personen aendern kann,
                        // aber nicht seine eigene, kann alle AUSSER seiner
                        // eigenen aendern :)
                        else
                        {
                            if (!req.isActionAllowed(
                                            RechtId.EIGENE_TELEFONE_AENDERN,
                                            "Sie sind nicht dazu berechtigt, ihre eigenen Telefone zu ändern."))
                            {
                                return;
                            }
                        }
                    }
                }
                catch (StdException e)
                {
                    req.getErrorBean().addMessage(e);
                }
            }
            else
            { // Es soll ein neues Telefon erstellt werden
                telefonVO = daoTelefon.getObjectFactory().createTelefon();
            }

            try
            {
                // 2006-06-28 CKL: Gefixt - jedes Telefon haette sonst dem
                // aktuellen Benutzer gehoert
                telefonVO.setPersonId(new PersonId(req
                                .getId(Parameters.PERSON_ID)));
                telefonVO.setAktiv(req.getBoolForParam(Parameters.IS_AKTIV));
                telefonVO.setNummer(new TelefonNummer(req
                                .getStringForParam(Parameters.TEXT_NUMMER)));

                if (req.getId(Parameters.TELEFON_ID) > 0)
                {
                    log.debug(buildLogMessage(req, "Aendere Telefon ["
                                    + telefonVO.getBaseId() + "]"));
                    telefonVO = taoBV.updateTelefon(telefonVO);
                }
                else
                {
                    log.debug(buildLogMessage(req, "Erstelle neues Telefon ["
                                    + telefonVO + "]"));
                    telefonVO = taoBV.createTelefon(telefonVO);
                }

                if (telefonVO != null)
                {
                    req.setId(Parameters.TELEFON_ID, telefonVO.getTelefonId()
                                    .getLongValue());
                }
                else
                {
                    req.getErrorBean()
                                    .addMessage("Das Telefon konnte nicht erzeugt bzw. geupdatet werden. Bitte überprüfen Sie, ob eine Telefonnummer mit dem Kürzel bereits eingetragen wurde.");
                }
            }
            catch (StdException e)
            {
                req.getErrorBean().addMessage(e);
            }
        }
    }

    /**
     * Baut den Redirect-String zusammen
     * 
     * @return
     */
    protected String buildRedirectString(RequestResources req)
    {
        String rRedirectString = "";
        long l = 0;

        if ((l = req.getLongForParam("OrganisationId")) > 0)
        {
            rRedirectString = "/zabos/controller/organisation/?tab=rollen&amp;OrganisationId="
                            + l;
        }
        else if ((l = req.getLongForParam("OrganisationsEinheitId")) > 0)
        {
            rRedirectString = "/zabos/controller/organisationseinheit/?tab=rollen&amp;OrganisationsEinheitId="
                            + l;
        }
        else if ((l = req.getLongForParam("SchleifeId")) > 0)
        {
            rRedirectString = "/zabos/controller/schleife/?tab=rollen&amp;SchleifeId="
                            + l;
        }

        if (rRedirectString.length() > 0)
        {
            rRedirectString += "&amp;RolleId=" + req.getLongForParam("RolleId");
        }
        return rRedirectString;
    }

    /**
     * Testet ob die aktuelle Lizenz dazu reicht noch eine weitere Person
     * anzulegen
     * 
     * @return
     * @throws StdException
     */
    public boolean istCreatePersonenLizensiert() throws StdException
    {
        if (getLicense() == null)
        {
            throw new StdException("Es wurde keine Lizenz definiert!");
        }

        if (getLicense().getPersonen() > 0)
        {
            return (daoPerson.countPersonen() < getLicense().getPersonen());
        }

        return true;
    }

    public void setEmailRequired(boolean emailRequired)
    {
        this.emailRequired = emailRequired;
    }

    public boolean isEmailRequired()
    {
        return emailRequired;
    }
}
