package de.ecw.zabos.frontend.controllers;

import org.apache.log4j.Logger;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.FormObject;
import de.ecw.zabos.frontend.FormValidator;
import de.ecw.zabos.frontend.Navigation;
import de.ecw.zabos.frontend.Parameters;
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
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.util.StringUtils;

/**
 * Controller für den Login/Logout
 * 
 * @author ckl
 */
public class SecurityController extends BaseControllerAdapter
{
    public static final String DO_LOGOUT = "doLogout";

    public static final String DO_LOGIN = "doLogin";

    // Serial
    final static long serialVersionUID = 1209312049;

    // Logger-Instanz
    private final static Logger log = Logger
                    .getLogger(SecurityController.class);

    protected OrganisationDAO daoOrganisation = null;

    protected OrganisationsEinheitDAO daoOE = null;

    protected BenutzerVerwaltungTAO taoBV = null;

    protected RolleDAO daoRolle = null;

    protected PersonDAO daoPerson = null;

    protected SchleifenDAO daoSchleife = null;

    public SecurityController(final DBResource dbResource)
    {
        super(dbResource);

        // Verzeichnis mit den Templates setzen
        this.setActionDir(Navigation.ACTION_DIR_SECURITY);

        // TAO/DAO-Factory initalisieren
        daoOrganisation = dbResource.getDaoFactory().getOrganisationDAO();
        daoOE = dbResource.getDaoFactory().getOrganisationsEinheitDAO();
        taoBV = dbResource.getTaoFactory().getBenutzerVerwaltungTAO();
        daoRolle = dbResource.getDaoFactory().getRolleDAO();
        daoPerson = dbResource.getDaoFactory().getPersonDAO();
        daoSchleife = dbResource.getDaoFactory().getSchleifenDAO();
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
            if (req.getRequestDo().equals(DO_LOGIN))
            {
                doLogin(req);
            }
            else if (req.getRequestDo().equals(DO_LOGOUT))
            {
                doLogout(req);
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
     * Wird aufgerufen, wenn der User sich einloggen will
     */
    protected void doLogin(final RequestResources req)
    {
        FormValidator formValidator = req.buildFormValidator();
        formValidator.add(new FormObject(Parameters.TEXT_USERNAME,
                        "Benutzername"));
        formValidator.add(new FormObject(Parameters.TEXT_PASSWORD, "Passwort"));
        formValidator.run();

        if (formValidator.getTotalErrors() == 0)
        {
            PersonVO voPerson;

            try
            {
                voPerson = daoPerson
                                .findPersonByNameAndPasswd(
                                                req
                                                                .getStringForParam(Parameters.TEXT_USERNAME),
                                                StringUtils
                                                                .md5(req
                                                                                .getStringForParam(Parameters.TEXT_PASSWORD)));

                if (voPerson != null)
                {
                    // Person darf sich in die Web-Oberfläche einloggen
                    if (voPerson.getPassword() != null)
                    {
                        log.debug(buildLogMessage(req,
                                        "Person konnte gefunden werden"));
                        req.getUserBean().isLoggedIn(true);
                        req.getUserBean().setPerson(voPerson);

                        // Zugriffe auf die einzelnen Ressourcen setzen
                        AccessControllerHO accessController = req
                                        .buildAccessController();

                        accessController.update(null);

                        // 2006-31-05 CKL: Eigene Rechte werden zu Beginn
                        // gesetzt
                        accessController.setGlobaleRechte();

                        // Weiterleitung
                        if (req.getStringForParam(Parameters.FORWARD_PAGE)
                                        .equals("") == false)
                        {
                            req
                                            .setForwardPage(req
                                                            .getStringForParam(Parameters.FORWARD_PAGE));
                        }
                        // if (Weiterleitung auf eine andere Seite ist gesetzt)
                        else
                        {
                            // 2006-06-08 CKL: Person darf keinen Alarm auslösen
                            // und Alarmhistorie NICHT sehen =>
                            // Weiterleitung auf die "Eigene Einstellung"-Seite
                            if ((false == req.getUserBean()
                                            .getAccessControlList()
                                            .isAlarmAusloesenErlaubt())
                                            && (false == req
                                                            .getUserBean()
                                                            .getAccessControlList()
                                                            .isAlarmhistorieSehenErlaubt()))
                            {
                                req
                                                .setForwardPage("/controller/person/?PersonId="
                                                                + req
                                                                                .getUserBean()
                                                                                .getPerson()
                                                                                .getPersonId()
                                                                                .getLongValue());

                            }
                            else
                            {
                                req
                                                .setForwardPage("/controller/"
                                                                + Navigation.ACTION_DIR_ALARMIERUNG);
                            }
                        }
                    }
                    else
                    {
                        req
                                        .getErrorBean()
                                        .addMessage(
                                                        "Ihr Benutzer-Account verbietet das Einloggen in die Web-Oberfläche.");
                    }
                }
                else
                {
                    req
                                    .getErrorBean()
                                    .addMessage(
                                                    "Der Benutzer mit den angegebenen Daten konnte nicht gefunden werden.");
                }
            }
            catch (StdException e)
            {
                req.getErrorBean().addMessage(e);
            }
        }
        else
        {
            log.error("Es fehlen Daten fuer die Authentifizierung");
        }
    }

    /**
     * Wird aufgerufen, wenn der User sich einloggen will
     */
    protected void doLogout(final RequestResources req)
    {
        log.debug(buildLogMessage(req, "Logout."));
        req.getServletRequest().getSession().invalidate();
    }
}
