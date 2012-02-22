package de.ecw.zabos.frontend.controllers;

import gnu.io.CommPortIdentifier;

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
import de.ecw.zabos.frontend.controllers.helpers.deletedobjects.PropertyHandlerKuerzelImpl;
import de.ecw.zabos.frontend.controllers.helpers.deletedobjects.PropertyHandlerNameImpl;
import de.ecw.zabos.frontend.objects.BaseControllerAdapter;
import de.ecw.zabos.frontend.objects.fassade.LizenzFassade;
import de.ecw.zabos.frontend.ressources.RequestResources;
import de.ecw.zabos.frontend.sql.ho.AccessControllerHO;
import de.ecw.zabos.frontend.sql.ho.ImportUserHO;
import de.ecw.zabos.mc35.MC35ManagerDaemon;
import de.ecw.zabos.sql.dao.BereichDAO;
import de.ecw.zabos.sql.dao.FunktionstraegerDAO;
import de.ecw.zabos.sql.dao.OrganisationDAO;
import de.ecw.zabos.sql.dao.OrganisationsEinheitDAO;
import de.ecw.zabos.sql.dao.PersonDAO;
import de.ecw.zabos.sql.dao.RechtDAO;
import de.ecw.zabos.sql.dao.RolleDAO;
import de.ecw.zabos.sql.dao.RueckmeldungStatusAliasDAO;
import de.ecw.zabos.sql.dao.SchleifenDAO;
import de.ecw.zabos.sql.dao.SystemKonfigurationDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.tao.BenutzerVerwaltungTAO;
import de.ecw.zabos.sql.tao.RechteTAO;
import de.ecw.zabos.sql.tao.RolleTAO;
import de.ecw.zabos.sql.tao.RueckmeldungStatusAliasTAO;
import de.ecw.zabos.sql.tao.SystemKonfigurationTAO;
import de.ecw.zabos.sql.vo.BaseIdVO;
import de.ecw.zabos.sql.vo.BereichVO;
import de.ecw.zabos.sql.vo.FunktionstraegerVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.RolleVO;
import de.ecw.zabos.sql.vo.RueckmeldungStatusAliasVO;
import de.ecw.zabos.sql.vo.SystemKonfigurationMc35VO;
import de.ecw.zabos.sql.vo.SystemKonfigurationVO;
import de.ecw.zabos.types.TelefonNummer;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.BereichId;
import de.ecw.zabos.types.id.FunktionstraegerId;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.types.id.RolleId;
import de.ecw.zabos.types.id.RueckmeldungStatusAliasId;
import de.ecw.zabos.types.id.RueckmeldungStatusId;

/**
 * Controller für die Sytemverwaltung
 * 
 * @author ckl
 */
public class SystemController extends BaseControllerAdapter
{
    private static final String DO_IMPORT = "doImport";

    public static final String DO_REMOVE_ROLLE_FROM_PERSON = "doRemoveRolleFromPerson";

    public static final String DO_UPDATE_ROLLEN_KONTEXT = "doUpdateRollenKontext";

    public static final String DO_UPDATE_ROLLE = "doUpdateRolle";

    public static final String DO_DELETE_ROLLE = "doDeleteRolle";

    public static final String DO_UPDATE_SYSTEM = "doUpdateSystem";

    public static final String DO_UPDATE_MODEM = "doUpdateModem";

    public static final String DO_DELETE_MODEM = "doDeleteModem";

    public static final String DO_UPDATE_BEREICH = "doUpdateBereich";

    public static final String DO_DELETE_BEREICH = "doDeleteBereich";

    public static final String DO_UPDATE_FUNKTIONSTRAEGER = "doUpdateFunktionstraeger";

    public static final String DO_DELETE_FUNKTIONSTRAEGER = "doDeleteFunktionstraeger";

    public static final String DO_UPDATE_ALIASE = "doUpdateAliase";

    // Serial
    final static long serialVersionUID = 1209312049;

    // Logger-Instanz
    private final static Logger log = Logger.getLogger(SystemController.class);

    protected OrganisationDAO daoOrganisation = null;

    protected OrganisationsEinheitDAO daoOE = null;

    protected BenutzerVerwaltungTAO taoBV = null;

    protected RolleDAO daoRolle = null;

    protected PersonDAO daoPerson = null;

    protected SystemKonfigurationDAO daoSystem = null;

    protected SystemKonfigurationTAO taoSystem = null;

    protected RueckmeldungStatusAliasDAO daoRueckmeldungStatusAlias = null;

    protected RueckmeldungStatusAliasTAO taoRueckmeldungStatusAlias = null;

    protected BereichDAO daoBereich = null;

    protected FunktionstraegerDAO daoFunktionstraeger = null;

    protected RechtDAO daoRecht = null;

    protected RechteTAO taoRecht = null;

    protected RolleTAO taoRolle = null;

    protected SchleifenDAO daoSchleife = null;

    protected PersonenMitRollenBO boPersonenMitRollen = null;

    private SystemKonfigurationVO systemKonfiguration;

    private MC35ManagerDaemon mc35ManagerDaemon;

    public SystemController(final DBResource dbResource,
                    SystemKonfigurationVO _systemKonfiguration)
    {
        super(dbResource);

        // Verzeichnis mit den Templates setzen
        this.setActionDir(Navigation.ACTION_DIR_SYSTEM);

        // TAO/DAO-Factory initalisieren
        daoOrganisation = dbResource.getDaoFactory().getOrganisationDAO();
        daoOE = dbResource.getDaoFactory().getOrganisationsEinheitDAO();
        taoBV = dbResource.getTaoFactory().getBenutzerVerwaltungTAO();
        daoRolle = dbResource.getDaoFactory().getRolleDAO();
        daoPerson = dbResource.getDaoFactory().getPersonDAO();
        daoSystem = dbResource.getDaoFactory().getSystemKonfigurationDAO();
        taoSystem = dbResource.getTaoFactory().getSystemKonfigurationTAO();
        daoRueckmeldungStatusAlias = dbResource.getDaoFactory()
                        .getRueckmeldungStatusAliasDAO();
        taoRueckmeldungStatusAlias = dbResource.getTaoFactory()
                        .getRueckmeldungStatusAliasTAO();
        daoRecht = dbResource.getDaoFactory().getRechtDAO();
        taoRecht = dbResource.getTaoFactory().getRechteTAO();
        taoRolle = dbResource.getTaoFactory().getRolleTAO();

        boPersonenMitRollen = dbResource.getBoFactory()
                        .getPersonenMitRollenBO();
        daoSchleife = dbResource.getDaoFactory().getSchleifenDAO();
        daoBereich = dbResource.getDaoFactory().getBereichDAO();
        daoFunktionstraeger = dbResource.getDaoFactory()
                        .getFunktionstraegerDAO();
        setSystemKonfiguration(_systemKonfiguration);
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
        req.resolveId(Parameters.MODEM_ID).resolveId(Parameters.PERSON_ID)
                        .resolveId(Parameters.ROLLE_ID)
                        .resolveId(Parameters.FUNKTIONSTRAEGER_ID)
                        .resolveId(Parameters.BEREICH_ID);
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
        accessController.update(req.getBaseId(null, null));

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
        SystemKonfigurationVO systemKonfigurationVO = null;
        SystemKonfigurationMc35VO systemKonfigurationMc35VO = null;
        CommPortIdentifier[] commPortsVO = null;
        RolleVO rolleVO = null;
        PersonVO[] sortedPersonenVO = null;
        FunktionstraegerVO funktionstraegerVO = null;
        BereichVO bereichVO = null;
        boolean isModemOnline = false;

        DataBean beanModemAvailable = new DataBean();
        DataBean beanAliasJa = new DataBean();
        DataBean beanAliasNein = new DataBean();
        DataBean beanAliasSpaeter = new DataBean();
        DataBean beanComPortsAvailable = new DataBean();
        DataBean beanOrganisationenAvailable = new DataBean();
        DataBean beanBereicheAvailable = new DataBean();
        DataBean beanFunktionstraegerAvailable = new DataBean();
        DataBean beanDataKompatibleRollen = new DataBean();
        DataBean beanDataRechteAlle = new DataBean();
        DataBean beanDataRechteZugewiesen = new DataBean();
        DataBean beanPersonenAssigned = new DataBean();
        DataBean beanPersonenAlle = new DataBean();
        DataBean beanPersonenMitRollen = new DataBean();
        DataBean beanPersonenOhneRollen = new DataBean();
        BaumViewBO treeView = req.getDbResource().getBoFactory()
                        .getBaumViewBO();

        req.getServletRequest().setAttribute(Parameters.NAVIGATION_TREE,
                        treeView.findTreeView());

        try
        {
            systemKonfigurationVO = daoSystem.readKonfiguration();
            beanDataKompatibleRollen.setData(taoRecht
                            .findKompatibleRollenByPersonInSystem(req
                                            .getUserBean().getPerson()
                                            .getPersonId()));
            beanDataRechteAlle.setData(daoRecht.findAll());

            // Personen sortieren
            sortedPersonenVO = daoPerson.findAll();
            PersonVO.sortPersonenByNachnameVorname(sortedPersonenVO);
            beanPersonenAlle.setData(sortedPersonenVO);

            beanPersonenMitRollen.setData(boPersonenMitRollen
                            .getPersonenMitVererbtenRollen(null));
            beanFunktionstraegerAvailable
                            .setData(daoFunktionstraeger.findAll());
            beanBereicheAvailable.setData(daoBereich.findAll());
            beanPersonenOhneRollen.setData(daoPerson
                            .findPersonenOhneRolleInSystem());

            // Rolle geladen
            if (req.getId(Parameters.ROLLE_ID) > 0)
            {
                rolleVO = daoRolle.findRolleById(new RolleId(req
                                .getId(Parameters.ROLLE_ID)));

                if (rolleVO == null)
                {
                    req.getErrorBean()
                                    .addMessage("Die Rolle mit der Id "
                                                    + req.getId(Parameters.ROLLE_ID)
                                                    + " konnte nicht gefunden werden.");
                }
                else
                {
                    beanDataRechteZugewiesen.setData(daoRecht
                                    .findRechteByRolleId(rolleVO.getRolleId()));
                }
            }

            if (req.getId(Parameters.FUNKTIONSTRAEGER_ID) > 0)
            {
                funktionstraegerVO = daoFunktionstraeger
                                .findFunktionstraegerById(new FunktionstraegerId(
                                                req.getId(Parameters.FUNKTIONSTRAEGER_ID)));
            }

            if (req.getId(Parameters.BEREICH_ID) > 0)
            {
                bereichVO = daoBereich.findBereichById(new BereichId(req
                                .getId(Parameters.BEREICH_ID)));
            }

            if (systemKonfigurationVO == null)
            {
                req.getErrorBean()
                                .addMessage("Die Konfiguration konnte nicht geladen werden");
            }
            else
            {
                SystemKonfigurationMc35VO[] voMC35Modems = daoSystem
                                .findAllMC35();

                beanModemAvailable.setData(voMC35Modems);

                // Uebepruefen, ob ein Modem ausgewaehlt wurde
                if (req.getId(Parameters.MODEM_ID) > 0)
                {
                    for (int i = 0, m = voMC35Modems.length; i < m; i++)
                    {
                        if (voMC35Modems[i].getBaseId().getLongValue() == req
                                        .getId(Parameters.MODEM_ID))
                        {
                            systemKonfigurationMc35VO = voMC35Modems[i];
                        }
                    }
                }

                MC35ManagerDaemon mc35Manager = getMc35ManagerDaemon();

                if (mc35Manager != null)
                {
                    if (systemKonfigurationMc35VO != null)
                    {
                        isModemOnline = mc35Manager
                                        .isModemOnline(systemKonfigurationMc35VO);
                    }

                    commPortsVO = mc35Manager.getPortIdentifiers();

                    if (commPortsVO != null)
                    {
                        beanComPortsAvailable.setData(commPortsVO);
                    }
                }

                beanOrganisationenAvailable.setData(daoOrganisation.findAll());
                beanAliasJa.setData(daoRueckmeldungStatusAlias
                                .findByRueckmeldungStatusId(new RueckmeldungStatusId(
                                                RueckmeldungStatusId.STATUS_JA)));
                beanAliasNein.setData(daoRueckmeldungStatusAlias
                                .findByRueckmeldungStatusId(new RueckmeldungStatusId(
                                                RueckmeldungStatusId.STATUS_NEIN)));
                beanAliasSpaeter.setData(daoRueckmeldungStatusAlias
                                .findByRueckmeldungStatusId(new RueckmeldungStatusId(
                                                RueckmeldungStatusId.STATUS_SPAETER)));
            }
        }
        catch (StdException e)
        {
            log.error(e);
        }

        if (req.getId(Parameters.ROLLE_ID) > 0)
        {
            try
            {
                rolleVO = daoRolle.findRolleById(new RolleId(req
                                .getId(Parameters.ROLLE_ID)));

                // Personen sortieren
                sortedPersonenVO = daoPerson
                                .findPersonenByRolleInSystem(new RolleId(req
                                                .getId(Parameters.ROLLE_ID)));
                PersonVO.sortPersonenByNachnameVorname(sortedPersonenVO);
                beanPersonenAssigned.setData(sortedPersonenVO);

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

        // Lizenz-Daten setzen
        LizenzFassade objLicense = new LizenzFassade();

        if (getLicense() != null)
        {

            objLicense.setAblaufDatum(getLicense().getAblaufDatum());
            objLicense.setAusstellungsDatum(getLicense().getAusstellungsDatum());
            objLicense.setMaxPersonen(getLicense().getPersonen());
            objLicense.setMaxSchleifen(getLicense().getSchleifen());
            objLicense.setVersion(getLicense().getVersionString());
            objLicense.setKundenNummer(getLicense().getKundennummer());
        }

        try
        {
            objLicense.setCurPersonen(daoPerson.countPersonen());
            objLicense.setCurSchleifen(daoSchleife.countSchleifen());
        }
        catch (StdException e)
        {
            log.error(e);
        }

        req.setData(Parameters.OBJ_LICENSE, objLicense);

        // Kontext zuruecksetzen
        req.getUserBean().setCtxO(null);

        // Attribute setzen
        req.setData(Parameters.IS_MODEM_ONLINE, isModemOnline);
        req.setData(Parameters.ARR_MODEM_AVAILABLE, beanModemAvailable);
        req.setData(Parameters.OBJ_MODEM, systemKonfigurationMc35VO);
        req.setData(Parameters.OBJ_KONFIGURATION, systemKonfigurationVO);
        req.setData(Parameters.OBJ_BEREICH, bereichVO);
        req.setData(Parameters.OBJ_FUNKTIONSTRAEGER, funktionstraegerVO);

        req.setData(Parameters.ARR_ALIAS_JA_AVAILABLE, beanAliasJa);
        req.setData(Parameters.ARR_ALIAS_NEIN_AVAILABLE, beanAliasNein);
        req.setData(Parameters.ARR_ALIAS_SPAETER_AVAILABLE, beanAliasSpaeter);
        req.setData(Parameters.ARR_AVAILABLE_COM_PORTS, beanComPortsAvailable);
        req.setData(Parameters.ARR_ORGANISATIONEN_AVAILABLE,
                        beanOrganisationenAvailable);
        req.setData(Parameters.OBJ_ROLLE, rolleVO);
        req.setData(Parameters.ARR_PERSONEN_MIT_ROLLEN, beanPersonenMitRollen);
        req.setData(Parameters.ARR_PERSONEN_ASSIGNED, beanPersonenAssigned);
        req.setData(Parameters.ARR_PERSONEN_AVAILABLE, beanPersonenAlle);
        req.setData(Parameters.ARR_KOMPATIBLE_ROLLEN_AVAILABLE,
                        beanDataKompatibleRollen);
        req.setData(Parameters.ARR_BEREICHE_AVAILABLE, beanBereicheAvailable);
        req.setData(Parameters.ARR_FUNKTIONSTRAEGER_AVAILABLE,
                        beanFunktionstraegerAvailable);
        req.setData(Parameters.ARR_RECHTE_AVAILABLE, beanDataRechteAlle);
        req.setData(Parameters.ARR_RECHTE_ASSIGNED, beanDataRechteZugewiesen);
        req.setData(Parameters.ARR_PERSONEN_OHNE_ROLLEN, beanPersonenOhneRollen);
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
            if (req.getRequestDo().equals(DO_UPDATE_ALIASE))
            {
                doUpdateAliase(req);
            }
            else if (req.getRequestDo().equals(DO_DELETE_MODEM))
            {
                doDeleteModem(req);
            }
            else if (req.getRequestDo().equals(DO_UPDATE_MODEM))
            {
                doUpdateModem(req);
            }
            else if (req.getRequestDo().equals(DO_DELETE_FUNKTIONSTRAEGER))
            {
                doDeleteFunktionstraeger(req);
            }
            else if (req.getRequestDo().equals(DO_UPDATE_FUNKTIONSTRAEGER))
            {
                doUpdateFunktionstraegter(req);
            }
            else if (req.getRequestDo().equals(DO_DELETE_BEREICH))
            {
                doDeleteBereich(req);
            }
            else if (req.getRequestDo().equals(DO_UPDATE_BEREICH))
            {
                doUpdateBereich(req);
            }
            else if (req.getRequestDo().equals(DO_UPDATE_SYSTEM))
            {
                doUpdateSystem(req);
            }
            else if (req.getRequestDo().equals(DO_DELETE_ROLLE))
            {
                doDeleteRolle(req);
            }
            else if (req.getRequestDo().equals(DO_UPDATE_ROLLE))
            {
                doUpdateRolle(req);
            }
            else if (req.getRequestDo().equals(DO_UPDATE_ROLLEN_KONTEXT))
            {
                doUpdateRollenKontext(req);
            }
            else if (req.getRequestDo().equals(DO_REMOVE_ROLLE_FROM_PERSON))
            {
                doRemoveRolleFromPerson(req);
            }
            else if (req.getRequestDo().equals(DO_IMPORT))
            {
                doImport(req);
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
     * Updated das Objekt dieser View
     */
    protected void doUpdateAliase(final RequestResources req)
    {
        if (!req.isActionAllowed(RechtId.SYSTEMKONFIGURATION_AENDERN,
                        "Sie besitzen nicht das Recht, die Systemkonfiguration zu ändern."))
        {
            return;
        }

        long arrToDelete[] = req.getLongArrayForParam(Parameters.ARR_TO_DELETE);
        RueckmeldungStatusAliasVO[] existierendeAliaseVO = null;

        if (arrToDelete != null)
        {
            for (int i = 0, m = arrToDelete.length; i < m; i++)
            {
                taoRueckmeldungStatusAlias
                                .deleteRueckmeldungStatusAlias(new RueckmeldungStatusAliasId(
                                                arrToDelete[i]));
            }
        }

        if (req.getStringForParam(Parameters.TEXT_ALIAS).equals("") == false)
        {
            RueckmeldungStatusAliasVO voAlias = daoRueckmeldungStatusAlias
                            .getObjectFactory().createRueckmeldungStatusAlias();

            try
            {
                existierendeAliaseVO = daoRueckmeldungStatusAlias
                                .findByRueckmeldungStatusAlias(req
                                                .getStringForParam(Parameters.TEXT_ALIAS));
                voAlias.setAlias(req.getStringForParam(Parameters.TEXT_ALIAS));
                voAlias.setRueckmeldungStatusId(new RueckmeldungStatusId(req
                                .getLongForParam(Parameters.SELECT_TYPE)));

                if (existierendeAliaseVO != null)
                {
                    for (int i = 0, m = existierendeAliaseVO.length; i < m; i++)
                    {
                        // Der neu zu erstellende Alias existiert bereits in
                        // einer anderen
                        // Kategorie => Fehler
                        if ((existierendeAliaseVO[i].getRueckmeldungStatusId()
                                        .getLongValue() != req
                                        .getLongForParam(Parameters.SELECT_TYPE))
                                        && (req.getErrorBean()
                                                        .getTotalMessages() == 0))
                        {
                            req.getErrorBean()
                                            .addMessage("Der Alias existiert bereits in einer anderen Alias-Kategorie.");
                        }
                    }
                }

                if (req.getErrorBean().getTotalMessages() == 0)
                {
                    taoRueckmeldungStatusAlias
                                    .createRueckmeldungStatusAlias(voAlias);
                }
            }
            catch (StdException e)
            {
                req.getErrorBean().addMessage(e);
            }
        }
    }

    /**
     * Importiert Benutzerdaten anhand einer CSV-Tabelle
     */
    protected void doImport(final RequestResources req)
    {
        if (!req.isActionAllowed(RechtId.SYSTEMKONFIGURATION_AENDERN,
                        "Sie besitzen nicht das Recht, Benutzer zu importieren."))
        {
            return;
        }

        try
        {
            ImportUserHO hoImportUser = new ImportUserHO(req.getDbResource(),
                            getLicense());
            hoImportUser.setCsvList(req
                            .getStringForParam(Parameters.TEXT_IMPORT_CSV));
            hoImportUser.run();
        }
        catch (StdException e)
        {
            req.getErrorBean().addMessage(e);
        }
    }

    /**
     * Löscht ein Objekt dieser View
     */
    protected void doDeleteModem(final RequestResources req)
    {
        // 2006-06-01 CKL: Sicherheitsabfrage, ob Person berechtigt ist, Modems
        // zu loeschen
        if (!req.isActionAllowed(RechtId.COMPORTS_FESTLEGEN,
                        "Sie besitzen nicht das Recht, die Systemkonfiguration zu ändern."))
        {
            return;
        }

        try
        {
            BaseId id = new BaseId(req.getId(Parameters.MODEM_ID));

            SystemKonfigurationMc35VO mc35VO = daoSystem
                            .findKonfigurationMc35ById(id);

            if (id != null)
            {
                taoSystem.deleteSystemKonfigurationMc35(mc35VO);

                if (getMc35ManagerDaemon() != null)
                {
                    getMc35ManagerDaemon().remove(mc35VO);
                }
            }
        }
        catch (StdException e)
        {
            req.getErrorBean().addMessage(e);
        }
    }

    /**
     * Bereich als gelöscht markieren.
     * 
     * @author ckl
     */
    protected void doDeleteBereich(RequestResources req)
    {
        if (!req.isActionAllowed(RechtId.BEREICHE_FESTLEGEN,
                        "Sie besitzen nicht das Recht, die Bereiche festzulegen."))
        {
            return;
        }

        BereichId bereichId = new BereichId(req.getId(Parameters.BEREICH_ID));

        log.debug(buildLogMessage(req, "Loesche Bereich [" + bereichId + "]"));
        taoBV.deleteBereich(bereichId);
        req.setId(Parameters.BEREICH_ID, 0);
    }

    /**
     * Bereich ändern
     * 
     * @param req
     */
    protected void doUpdateBereich(RequestResources req)
    {
        if (!req.isActionAllowed(RechtId.BEREICHE_FESTLEGEN,
                        "Sie besitzen nicht das Recht, die Bereiche festzulegen."))
        {
            return;
        }

        BereichVO bereichVO = null;
        BereichVO bereichVOinDatenbank = null;

        long id = req.getId(Parameters.BEREICH_ID);
        if (id > 0)
        {
            log.debug(buildLogMessage(req, "Bereich [" + id
                            + "] soll geaendert werden."));

            try
            {
                bereichVO = daoBereich.findBereichById(new BereichId(req
                                .getId(Parameters.BEREICH_ID)));
                log.debug(buildLogMessage(req, "Bereich [" + id + "] gefunden."));
            }
            catch (StdException e)
            {
                req.getErrorBean().addMessage(e);
            }
        }

        FormValidator formValidator = req.buildFormValidator();
        FormObject foName = new FormObject(Parameters.TEXT_NAME, "Name");
        formValidator.add(foName);
        formValidator.run();

        try
        {
            bereichVOinDatenbank = daoBereich.findBereichByName(req
                            .getStringForParam(Parameters.TEXT_NAME));
            CheckExistingObject.handle(bereichVO, bereichVOinDatenbank,
                            new PropertyHandlerNameImpl("Bereich",
                                            isRenameDeletedObject(),
                                            new IOnRenameDeletedObject()
                                            {
                                                public BaseIdVO renameDeletedObject(
                                                                BaseIdVO datenbankObjekt)
                                                {
                                                    return taoBV.updateBereich((BereichVO) datenbankObjekt);
                                                }
                                            }, formValidator, req));
        }
        catch (StdException e)
        {
            log.error(buildLogMessage(req, e.getMessage()));
        }

        if (formValidator.hasErrors())
        {
            return;
        }

        long longBereichId = req.getId(Parameters.BEREICH_ID);

        try
        {
            if (longBereichId > 0)
            {
                bereichVO = daoBereich.findBereichById(new BereichId(
                                longBereichId));

                if (bereichVO == null)
                {
                    throw new StdException(
                                    "Der Bereich konnte nicht gefunden werden");
                }
            }
            // Bereich soll hinzugefügt werden
            else
            {
                bereichVO = daoBereich.getObjectFactory().createBereich();
            }

            // TODO Korrekte Überprüfung ob Element schon existiert

            bereichVO.setBeschreibung(req
                            .getStringForParam(Parameters.TEXT_BESCHREIBUNG));
            bereichVO.setName(req.getStringForParam(Parameters.TEXT_NAME));

            if (longBereichId > 0)
            {
                log.debug(buildLogMessage(req,
                                "Aendere Bereich [" + bereichVO.getBaseId()
                                                + "]"));
                bereichVO = taoBV.updateBereich(bereichVO);
            }
            else
            {
                log.debug(buildLogMessage(req, "Erstelle neuen Bereich ["
                                + bereichVO + "]"));
                bereichVO = taoBV.createBereich(bereichVO);
            }

            if (bereichVO != null)
            {
                req.setId(Parameters.BEREICH_ID, bereichVO.getBereichId()
                                .getLongValue());
            }
        }
        catch (StdException e)
        {
            log.error(buildLogMessage(req, e.getMessage()));
            req.getErrorBean().addMessage(e);
        }
    }

    /**
     * Funktionstraeger als gelöscht markieren.
     * 
     * @author ckl
     */
    protected void doDeleteFunktionstraeger(RequestResources req)
    {
        if (!req.isActionAllowed(RechtId.FUNKTIONSTRAEGER_FESTLEGEN,
                        "Sie besitzen nicht das Recht, die Funktionsträger festzulegen."))
        {
            return;
        }

        FunktionstraegerId id = new FunktionstraegerId(
                        req.getId(Parameters.FUNKTIONSTRAEGER_ID));

        log.debug(buildLogMessage(req, "Loesche Funktionstraeger [" + id + "]"));
        taoBV.deleteFunktionstraeger(id);
        req.setId(Parameters.FUNKTIONSTRAEGER_ID, 0);
    }

    /**
     * Funktionsträger ändern
     * 
     * @param req
     */
    protected void doUpdateFunktionstraegter(final RequestResources req)
    {
        if (!req.isActionAllowed(RechtId.FUNKTIONSTRAEGER_FESTLEGEN,
                        "Sie besitzen nicht das Recht, die Funktionsträger festzulegen."))
        {
            return;
        }

        FunktionstraegerVO funktionstraegerVO = null;
        FunktionstraegerVO fVOinDatenbank = null;

        long id = req.getId(Parameters.FUNKTIONSTRAEGER_ID);

        if (id > 0)
        {
            log.debug(buildLogMessage(req, "Funktionstraeger [" + id
                            + "] soll geaendert werden."));

            try
            {
                funktionstraegerVO = daoFunktionstraeger
                                .findFunktionstraegerById(new FunktionstraegerId(
                                                id));
                log.debug(buildLogMessage(req, "Funktionstraeger [" + id
                                + "] gefunden."));
            }
            catch (StdException e)
            {
                req.getErrorBean().addMessage(e);
            }
        }

        FormValidator fv = req.buildFormValidator();
        FormObject foKuerzel = new FormObject(Parameters.TEXT_KUERZEL, "Kürzel");
        fv.add(foKuerzel);

        try
        {
            fVOinDatenbank = daoFunktionstraeger
                            .findFunktionstraegerByKuerzel(req
                                            .getStringForParam(Parameters.TEXT_KUERZEL));
            CheckExistingObject.handle(funktionstraegerVO, fVOinDatenbank,
                            new PropertyHandlerKuerzelImpl("Funktionsträger",
                                            isRenameDeletedObject(),
                                            new IOnRenameDeletedObject()
                                            {
                                                public BaseIdVO renameDeletedObject(
                                                                BaseIdVO datenbankObjekt)
                                                {
                                                    return taoBV.updateFunktionstraeger((FunktionstraegerVO) datenbankObjekt);
                                                }
                                            }, fv, req));
        }
        catch (StdException e)
        {
            log.error(e);
        }

        fv.run();

        if (fv.hasErrors())
        {
            return;
        }

        try
        {
            // ändern
            if (id > 0)
            {
                funktionstraegerVO = daoFunktionstraeger
                                .findFunktionstraegerById(new FunktionstraegerId(
                                                id));

                if (funktionstraegerVO == null)
                {
                    throw new StdException(
                                    "Der Funktionsträger konnte nicht gefunden werden");
                }
            }
            // Neu
            else
            {
                funktionstraegerVO = daoBereich.getObjectFactory()
                                .createFunktionstraeger();
            }

            funktionstraegerVO.setKuerzel(req
                            .getStringForParam(Parameters.TEXT_KUERZEL));
            funktionstraegerVO.setBeschreibung(req
                            .getStringForParam(Parameters.TEXT_BESCHREIBUNG));

            if (id > 0)
            {
                log.debug(buildLogMessage(req, "Aendere Funktionstraeger ["
                                + funktionstraegerVO.getBaseId() + "]"));
                funktionstraegerVO = taoBV
                                .updateFunktionstraeger(funktionstraegerVO);
            }
            else
            {
                log.debug(buildLogMessage(req,
                                "Erstelle neuen Funktionstraeger ["
                                                + funktionstraegerVO + "]"));
                funktionstraegerVO = taoBV
                                .createFunktionstraeger(funktionstraegerVO);
            }

            if (funktionstraegerVO != null)
            {
                req.setId(Parameters.FUNKTIONSTRAEGER_ID, funktionstraegerVO
                                .getFunktionstraegerId().getLongValue());
            }
        }
        catch (StdException e)
        {
            log.error(buildLogMessage(req, e.getMessage()));
            req.getErrorBean().addMessage(e);
        }
    }

    /**
     * Updated das Objekt dieser View
     * 
     * @since 2006-06-01 CKL: Sicherheitsabfrage auf Recht COMPORTS_FESTLEGEN
     * @author ckl
     */
    protected void doUpdateModem(final RequestResources req)
    {
        // 2006-06-01 CKL: Sicherheitsabfrage, ob Person berechtigt ist, Modems
        // zu loeschen
        if (!req.isActionAllowed(RechtId.COMPORTS_FESTLEGEN,
                        "Sie besitzen nicht das Recht, die Comports zu ändern."))
        {
            return;
        }

        // ID des Telefons
        long id = 0;
        // Modem, dass evtl neu gestartet werden muss
        SystemKonfigurationMc35VO reloadMC35VO = null;

        FormValidator fv = req.buildFormValidator();

        FormObject foPin = new FormObject(Parameters.TEXT_PIN1, "PIN");
        foPin.setFlag(FormObject.NUMERIC);
        fv.add(foPin);
        fv.run();

        if (fv.getTotalErrors() == 0)
        {
            try
            {
                if ((id = req.getId(Parameters.MODEM_ID)) > 0)
                {
                    reloadMC35VO = daoSystem
                                    .findKonfigurationMc35ById(new BaseId(id));
                }

                if (reloadMC35VO == null)
                {
                    reloadMC35VO = daoBereich.getObjectFactory()
                                    .createSystemKonfigurationMc35();
                }

                TelefonNummer telefonnummer = new TelefonNummer(
                                req.getStringForParam(Parameters.TEXT_RUFNUMMER));

                reloadMC35VO.setAlarmModem(req
                                .getBoolForParam(Parameters.IS_ALARM_MODEM));
                reloadMC35VO.setPin1(req
                                .getStringForParam(Parameters.TEXT_PIN1));
                reloadMC35VO.setRufnummer(telefonnummer);

                int modemPort = Integer.valueOf(req
                                .getStringForParam(Parameters.TEXT_COM_PORT));
                reloadMC35VO.setComPort(modemPort);

                if (reloadMC35VO.getBaseId() == null)
                {
                    reloadMC35VO = taoSystem
                                    .createSystemKonfigurationMc35(reloadMC35VO);
                }
                else
                {
                    reloadMC35VO = taoSystem
                                    .updateSystemKonfigurationMc35(reloadMC35VO);
                }

                // 2006-06-28 CKL: Modem neu starten
                mc35ManagerDaemon.reload(reloadMC35VO);
            }
            catch (StdException e)
            {
                req.getErrorBean().addMessage(
                                "Während des Änderns des Modems traten Fehler auf: "
                                                + e.getMessage());
                log.error(buildLogMessage(req, e.getMessage()));
            }
        }
    }

    /**
     * Updated das Objekt dieser View
     */
    protected void doUpdateSystem(final RequestResources req)
    {

        // 2006-06-01 CKL: Sicherheitsabfrage, ob Person berechtigt ist, Konfig
        // zu aendern
        if (!req.isActionAllowed(RechtId.SYSTEMKONFIGURATION_AENDERN,
                        "Sie besitzen nicht das Recht, die Systemkonfiguration zu ändern."))
        {
            return;
        }

        FormValidator formValidator = req.buildFormValidator();

        // Timeout: Alarm
        FormObject foAlarmTimeout = new FormObject(
                        Parameters.TEXT_ALARM_TIMEOUT, "Timeout: Alarm");
        foAlarmTimeout.setFlag(FormObject.NUMERIC);
        foAlarmTimeout.setMaxLength(6);
        formValidator.add(foAlarmTimeout);

        // COM-Schnittstelle
        FormObject foCom5Ton = new FormObject(Parameters.TEXT_COM5_TON,
                        "COM-Schnittstelle");
        // Braucht nicht gesetzt zu sein
        foCom5Ton.setFlag(-FormObject.NOT_EMPTY);
        // Wenn es aber gesetzt ist muss es numerisch sein
        foCom5Ton.setFlag(FormObject.NUMERIC);
        foCom5Ton.setMaxLength(1);
        formValidator.add(foCom5Ton);

        // Timeout: Reaktivierung
        FormObject foReaktivierungTimeout = new FormObject(
                        Parameters.TEXT_REAKTIVIERUNG_TIMEOUT,
                        "Timeout: Reaktivierung");
        foReaktivierungTimeout.setFlag(FormObject.NUMERIC);
        foReaktivierungTimeout.setMaxLength(6);
        formValidator.add(foReaktivierungTimeout);

        // Max. Anzahl der Eintraege in der Alarmübersicht der rechten Seite
        FormObject foAlarmHistorieLaenge = new FormObject(
                        Parameters.TEXT_ALARM_HISTORIE_LAENGE,
                        "Anzahl der Alarmierungen in Alarmierungs-Historie");
        foAlarmHistorieLaenge.setFlag(FormObject.NUMERIC);
        foAlarmHistorieLaenge.setMaxLength(3);
        formValidator.add(foAlarmHistorieLaenge);

        // Timeout: SMS-In
        FormObject foSmsInTimeout = new FormObject(
                        Parameters.TEXT_SMS_IN_TIMEOUT, "Timeout: SMS-In");
        foSmsInTimeout.setFlag(FormObject.NUMERIC);
        foSmsInTimeout.setMaxLength(6);
        formValidator.add(foSmsInTimeout);

        formValidator.run();

        // Keine Fehler aufgetreten => Einstellungen speichern
        if (formValidator.getTotalErrors() == 0)
        {
            try
            {
                SystemKonfigurationVO voSystem = getSystemKonfiguration();
                voSystem.setAlarmTimeout(req
                                .getLongForParam(Parameters.TEXT_ALARM_TIMEOUT));

                // 5-Ton deaktivieren
                if (req.getStringForParam(Parameters.TEXT_COM5_TON).equals(""))
                {
                    voSystem.setCom5Ton(null);
                }
                else
                {
                    voSystem.setCom5Ton(Integer.valueOf(req
                                    .getStringForParam(Parameters.TEXT_COM5_TON)));
                }

                voSystem.setReaktivierungTimeout(req
                                .getIntForParam(Parameters.TEXT_REAKTIVIERUNG_TIMEOUT));
                voSystem.setSmsInTimeout(req
                                .getIntForParam(Parameters.TEXT_SMS_IN_TIMEOUT));
                voSystem.setAlarmHistorieLaenge(req
                                .getIntForParam(Parameters.TEXT_ALARM_HISTORIE_LAENGE));

                log.debug(buildLogMessage(req, "Aendere Systemkonfiguration"));
                taoSystem.updateSystemKonfiguration(voSystem);
            }
            catch (StdException e)
            {
                req.getErrorBean().addMessage(e);
                log.error(buildLogMessage(req, e.getMessage()));
            }
        }
    }

    /**
     * Updated das Objekt dieser View
     */
    protected void doUpdateRolle(final RequestResources req)
    {

        // Veroeffentlichtes Objekt
        RolleVO rolleVO = null;

        // Eingabe validieren
        FormValidator fv = req.buildFormValidator();
        // Name wird gebraucht
        FormObject foName = new FormObject(Parameters.TEXT_NAME, "Name");
        fv.add(foName);
        fv.run();

        // Es sind keine Fehler aufgetreten
        if (fv.getTotalErrors() == 0)
        {
            // ID wurde gesetzt => Rolle soll ge�ndert werden
            if (req.getId(Parameters.ROLLE_ID) > 0)
            {
                // 2006-06-01 CKL: Sicherheitsabfrage, ob Person berechtigt ist,
                // diese
                // Rolle zu aendern
                if (!req.isActionAllowed(RechtId.ROLLEN_AENDERN,
                                "Sie besitzen nicht das Recht, diese Rolle zu ändern."))
                {
                    return;
                }

                // Rolle finden
                try
                {
                    rolleVO = daoRolle.findRolleById(new RolleId(req
                                    .getId(Parameters.ROLLE_ID)));
                }
                catch (StdException e)
                {
                    log.error(buildLogMessage(req, e.getMessage()));
                    req.getErrorBean().addMessage(e.getMessage());
                }
            }
            else
            {
                // 2006-06-01 CKL: Sicherheitsabfrage, ob Person berechtigt ist,
                // Rolle anzulegen
                if (!req.isActionAllowed(RechtId.ROLLEN_ANLEGEN_LOESCHEN,
                                "Sie besitzen nicht das Recht, eine Rolle anzulegen."))
                {
                    return;
                }

                // Es soll eine neue Rolle erstellt werden
                rolleVO = daoRolle.getObjectFactory().createRolle();
            }

            // Wenn ein Rollen-Objekt existiert
            if (null != rolleVO)
            {
                // Versuchen, Einstellungen des Objekts zu setzen
                try
                {
                    rolleVO.setBeschreibung(req
                                    .getStringForParam(Parameters.TEXT_BESCHREIBUNG));
                    rolleVO.setName(req.getStringForParam(Parameters.TEXT_NAME));

                    // Rechte der Rolle zuweisen
                    long[] rechteSelected = req
                                    .getLongArrayForParam(Parameters.SELECT_RECHTE_ASSIGNED);

                    // ID existiert > Rolle �ndern
                    if (req.getId(Parameters.ROLLE_ID) > 0)
                    {
                        rolleVO = taoBV.updateRolle(rolleVO);

                        // 2006-05-31 CKL: DAOs durch TAOs ersetzt, da sonst
                        // Fehler beim
                        // Loeschen entstanden
                        // Jetzt die Rechte loeschen
                        taoRolle.removeRechteFromRolle(rolleVO.getRolleId());

                        // Und schlie�lich die Rechte hinzuf�gen
                        if (null != rechteSelected)
                        {
                            for (int i = 0, m = rechteSelected.length; i < m; i++)
                            {
                                RechtId rechtId = new RechtId(rechteSelected[i]);
                                // 2006-05-31 CKL: DAOs durch TAOs ersetzt, da
                                // sonst Fehler beim
                                // Loeschen entstanden
                                taoRolle.addRechtToRolle(rechtId,
                                                rolleVO.getRolleId());
                            }
                        }
                    }
                    // ID existiert nicht > Rolle erstellen
                    else
                    {
                        rolleVO = taoBV.createRolle(rolleVO);

                        // Alle aus der Select-Box geladenen Rechte der Rolle
                        // zuweisen
                        if (null != rechteSelected)
                        {
                            for (int i = 0, m = rechteSelected.length; i < m; i++)
                            {
                                // 2006-05-31 CKL: DAOs durch TAOs ersetzt, da
                                // sonst Fehler beim
                                // Loeschen entstanden
                                taoRolle.addRechtToRolle(new RechtId(
                                                rechteSelected[i]), rolleVO
                                                .getRolleId());
                            }
                        }
                    }

                    if (null == rolleVO)
                    {
                        req.getErrorBean()
                                        .addMessage("Die Rolle konnte nicht erzeugt werden. Bitte überprüfen Sie, ob der Name schon existiert.");
                    }
                    else
                    {
                        req.setId("RolleId", rolleVO.getRolleId()
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
    }

    /**
     * Löscht ein Objekt dieser View
     */
    protected void doDeleteRolle(final RequestResources req)
    {
        // 2006-06-01 CKL: Sicherheitsabfrage, ob Person berechtigt ist, diese
        // Schleife zu loeschen
        if (!req.isActionAllowed(RechtId.ROLLEN_ANLEGEN_LOESCHEN,
                        "Sie besitzen nicht das Recht, diese Rolle zu löschen."))
        {
            return;
        }

        // Objekt mit einer ID wurde uebergeben
        long id = req.getId(Parameters.ROLLE_ID);
        if (id > 0)
        {
            log.debug(buildLogMessage(req, "Rolle [" + id
                            + "] soll geloescht werden"));
            long aktiveRollen = 0;

            try
            {
                aktiveRollen = daoRolle.countRolleById(new RolleId(id));
            }
            catch (StdException e)
            {
                log.error(e);
            }

            if (aktiveRollen == 0)
            {
                RolleId rolleId = new RolleId(id);

                // Zuerst alle zugehoerigen Rechte entfernen
                if (taoRolle.removeRechteFromRolle(rolleId))
                {
                    taoBV.deleteRolle(new RolleId(id));
                    // Das Objekt mit der ID 0 (Neues Objekt) soll angezeigt
                    // werden
                    req.setId(Parameters.ROLLE_ID, 0);
                }
                else
                {
                    req.getErrorBean()
                                    .addMessage("Der Rolle mit der Id "
                                                    + id
                                                    + " konnten die Rechte nicht entzogen werden");
                }
            }
            else
            {
                req.getErrorBean()
                                .addMessage("Die Rolle mit der Id "
                                                + id
                                                + " konnte nicht gelöscht werden, da noch Personen dieser Rolle zugewiesen sind.");
            }
        }
    }

    protected void doUpdateRollenKontext(final RequestResources req)
    {
        if (!req.isActionAllowed(RechtId.PERSONEN_ROLLEN_ZUWEISEN,
                        "Sie besitzen nicht das Recht, Personen im System und deren Rollen zu ändern."))
        {
            return;
        }

        long id = req.getId(Parameters.ROLLE_ID);

        if (id > 0)
        {
            PersonVO[] personenVO = null;

            try
            {
                RolleVO rolle = daoRolle.findRolleById(new RolleId(id));
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
                                        rolle.getRolleId());
                    }
                }

                // Selectierte Personen hinzufuegen
                if (null != personenSelected)
                {
                    for (int i = 0, m = personenSelected.length; i < m; i++)
                    {
                        doUpdateKontextAddPersonInRolle(req, new PersonId(
                                        personenSelected[i]),
                                        rolle.getRolleId());
                    }
                }

                log.debug(buildLogMessage(req, "Rolle [" + id + "] geupdatet"));
            }
            catch (StdException e)
            {
                log.error(buildLogMessage(req, e.getMessage()));
            }
        }
    }

    /**
     * Lädt die Personen, die während des Updates der Kontext-Liste geupdatet
     * werden sollen
     */
    protected PersonVO[] doUpdateKontextGetPersonen(final RequestResources req)
    {
        PersonVO[] arrPersonen = null;

        try
        {
            arrPersonen = daoPerson.findPersonenByRolleInSystem(new RolleId(req
                            .getId(Parameters.ROLLE_ID)));
        }
        catch (StdException e)
        {
            req.getErrorBean().addMessage(e);
        }

        return arrPersonen;
    }

    /**
     * Entfernt eine Person ueber einen Link "Entfernen" aus der
     * Rollen-/Schleifen-Kombination
     */
    protected void doRemoveRolleFromPerson(final RequestResources req)
    {
        if (!req.isActionAllowed(RechtId.PERSONEN_ROLLEN_ZUWEISEN,
                        "Sie besitzen nicht das Recht, Personen im System und deren Rollen zu ändern."))
        {
            return;
        }

        doUpdateKontextRemovePersonInRolle(req,
                        new PersonId(req.getId(Parameters.PERSON_ID)),
                        new RolleId(req.getId(Parameters.ROLLE_ID)));
        log.debug(buildLogMessage(
                        req,
                        "Person [" + req.getId(Parameters.PERSON_ID)
                                        + "] mit Rolle ["
                                        + req.getId(Parameters.ROLLE_ID)
                                        + "] aus System entfernt"));
        req.setId(Parameters.PERSON_ID, 0);
        req.setId(Parameters.ROLLE_ID, 0);
    }

    /**
     * Muss von der zu erbenden View implementiert werden. Loescht die Person
     * mit der passenden ID aus dem jeweiligen Kontext
     * 
     * @param _personId
     * @param _rolleId
     */
    protected void doUpdateKontextRemovePersonInRolle(
                    final RequestResources req, PersonId _personId,
                    RolleId _rolleId)
    {
        if (!taoRecht.removePersonInRolleFromSystem(_personId, _rolleId))
        {
            req.getErrorBean()
                            .addMessage("Die Person mit der ID "
                                            + _personId
                                            + " konnte nicht aus der Rolle entfernt werden.");
        }
    }

    /**
     * Muss von der zu erbenden View implementiert werden. Fuegt die Person mit
     * der angegebenen Id zu der Rolle
     * 
     * @param _personId
     * @param _rolleId
     */
    protected void doUpdateKontextAddPersonInRolle(RequestResources req,
                    PersonId _personId, RolleId _rolleId)
    {
        try
        {
            // Überprüfen, ob die Person noch nicht dem System zugewiesen wurde
            if (!daoPerson.hatPersonRolleInSystem(_personId, _rolleId))
            {
                if (!taoRecht.addPersonInRolleToSystem(_personId, _rolleId))
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
     * Setzt das {@link SystemKonfigurationVO}
     * 
     * @param systemKonfiguration
     */
    public final void setSystemKonfiguration(
                    SystemKonfigurationVO systemKonfiguration)
    {
        this.systemKonfiguration = systemKonfiguration;
    }

    /**
     * Liefert das {@link SystemKonfigurationVO}
     * 
     * @return
     */
    public SystemKonfigurationVO getSystemKonfiguration()
    {
        return systemKonfiguration;
    }

    public void setMc35ManagerDaemon(MC35ManagerDaemon mc35ManagerDaemon)
    {
        this.mc35ManagerDaemon = mc35ManagerDaemon;
    }

    public MC35ManagerDaemon getMc35ManagerDaemon()
    {
        return mc35ManagerDaemon;
    }
}
