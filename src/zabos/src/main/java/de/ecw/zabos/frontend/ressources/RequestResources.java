package de.ecw.zabos.frontend.ressources;

import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.ecw.zabos.frontend.FormValidator;
import de.ecw.zabos.frontend.beans.MessageContainerBean;
import de.ecw.zabos.frontend.beans.UserBean;
import de.ecw.zabos.frontend.sql.ho.AccessControllerHO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.OrganisationId;
import de.ecw.zabos.types.id.OrganisationsEinheitId;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.types.id.SchleifeId;

/**
 * Abstrakte Basis-Klasse aller Servlets.<br>
 * Die Klasse stellt verschiedene Methoden bereit, um GET-/POST-Parameter
 * auszuwerten und als verschiedene Typen zurückzuliefern.
 * 
 * @author ckl
 */
final public class RequestResources implements IRequestResource
{
    // Serial
    public final static long serialVersionUID = 1209312039;

    /**
     * Datenbank
     */
    protected DBResource db = null;

    /**
     * URL im System, zu der geforwardet werden soll
     */
    private String forwardPage = null;

    // Logger-Instanz
    private final static Logger log = Logger.getLogger(RequestResources.class);

    /**
     * Name des Attributs, das die ID für das Objekt hält, z.B. RolleId,
     * RechtId, PersonId
     */
    protected String objectIdentifierName = "";

    /**
     * Servlet-Konfiguration
     */
    private ServletConfig servletConfig = null;

    /**
     * Request
     */
    private HttpServletRequest servletRequest = null;

    /**
     * Response
     */
    private HttpServletResponse servletResponse = null;

    /**
     * @param _req
     * @param _res
     * @param _conf
     * @param _db
     */
    public RequestResources(HttpServletRequest _req, HttpServletResponse _res,
                    ServletConfig _conf, DBResource _db)
    {

        setServletRequest(_req);
        setServletResponse(_res);
        db = _db;
        setServletConfig(_conf);

        // Error-Bean neu setzen
        if (getServletRequest().getAttribute(ERROR_BEAN) == null)
        {
            getServletRequest().setAttribute(ERROR_BEAN,
                            new MessageContainerBean());
        }

        // Message-Bean neu setzen
        if (getServletRequest().getAttribute(INFO_BEAN) == null)
        {
            getServletRequest().setAttribute(INFO_BEAN,
                            new MessageContainerBean());
        }

        // User-Objekt ist null
        if (getServletRequest().getSession().getAttribute(USER_BEAN) == null)
        {
            getServletRequest().getSession().setAttribute(USER_BEAN,
                            new UserBean());
        }
    }

    /**
     * Erstellt einen neuen {@link AccessControllerHO} aus
     * {@link #getUserBean()} und {@link #getDbResource()}
     * 
     * @return
     */
    public AccessControllerHO buildAccessController()
    {
        return new AccessControllerHO(getUserBean(), getDbResource());
    }

    /**
     * Erstellt einen {@link FormValidator} aus {@link #getServletRequest()} und
     * {@link #getErrorBean()}
     * 
     * @return
     */
    public FormValidator buildFormValidator()
    {
        return new FormValidator(this.getServletRequest(), this.getErrorBean());
    }

    /**
     * Debuggt alle Parameter die übergeben wurden Die Ausgabe erfolgt auf den
     * Stdout-Stream
     */
    @SuppressWarnings("unchecked")
    protected void debugAll()
    {
        Enumeration<String> params = getServletRequest().getParameterNames();
        int i = 0;

        while (params.hasMoreElements())
        {
            String param = params.nextElement().toString();
            String value = null;
            String[] values = null;
            i++;

            if ((value = getServletRequest().getParameter(param)) != null)
            {
                log.debug("Parameter (" + i + "): " + param + " --- " + value);

                values = null;
                if ((values = getServletRequest().getParameterValues(param)) != null)
                {

                    int m = 0;

                    if (values.length > 1)
                    {
                        for (; m < values.length; m++)
                        {
                            log.debug("  + " + (m + 1) + ": " + values[m]);

                        }
                    }
                }
            }
        }

        Enumeration attrs = getServletRequest().getAttributeNames();

        while (attrs.hasMoreElements())
        {
            String attr = attrs.nextElement().toString();
            Object value = null;
            i++;

            if ((value = getServletRequest().getAttribute(attr)) != null)
            {
                log.debug("Attribut (" + i + "): " + attr + " --- "
                                + value.toString());
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.ressources.IRequestResource#getBaseId(java.lang
     * .String, de.ecw.zabos.types.id.BaseId)
     */
    public BaseId getBaseId(String _attr, BaseId _idSignature)
    {
        long id = getId(_attr);

        if (id == 0)
        {
            return null;
        }
        else
        {
            if (_idSignature instanceof OrganisationId)
            {
                return new OrganisationId(id);
            }
            else if (_idSignature instanceof OrganisationsEinheitId)
            {
                return new OrganisationsEinheitId(id);
            }
            else if (_idSignature instanceof SchleifeId)
            {
                return new SchleifeId(id);
            }
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.ressources.IRequestResource#getBoolForParam(java
     * .lang.String)
     */
    public boolean getBoolForParam(String _param)
    {
        if (getServletRequest().getParameter(_param) != null)
        {
            return true;
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.frontend.ressources.IRequestResource#getDBRessource()
     */
    public DBResource getDbResource()
    {
        return db;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.frontend.ressources.IRequestResource#getRequestErrors()
     */
    public MessageContainerBean getErrorBean()
    {
        return (MessageContainerBean) getServletRequest().getAttribute(
                        ERROR_BEAN);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.frontend.ressources.IRequestResource#getInfoBean()
     */
    public MessageContainerBean getInfoBean()
    {
        return (MessageContainerBean) getServletRequest().getAttribute(
                        INFO_BEAN);
    }

    /**
     * Liefert die Seite an die geforwardet wird
     * 
     * @return
     */
    public String getForwardPage()
    {
        return forwardPage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.ressources.IRequestResource#getId(java.lang.String)
     */
    public long getId(String _attr)
    {
        long retValue = 0;

        if (_attr != null)
        {
            if (getServletRequest().getAttribute(_attr) != null)
            {
                try
                {
                    retValue = Long.valueOf(getServletRequest().getAttribute(
                                    _attr).toString());
                }
                catch (NumberFormatException e)
                {
                    log.error("Fehler beim Aufloesen der Attributs '"
                                    + _attr
                                    + "' zu einem Long. Parameter hat Wert '"
                                    + getServletRequest().getAttribute(_attr)
                                                    .toString() + "'");
                }
            }
        }

        return retValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.ressources.IRequestResource#getIntForParam(java
     * .lang.String)
     */
    public int getIntForParam(String _param)
    {
        int retValue = 0;

        if (getServletRequest().getParameter(_param) != null)
        {
            if (getServletRequest().getParameter(_param).toString().equals("") == false)
            {
                try
                {
                    retValue = Integer.valueOf(getServletRequest()
                                    .getParameter(_param).toString());
                }
                catch (NumberFormatException e)
                {
                    log
                                    .error("Fehler beim Aufloesen des Parameters '"
                                                    + _param
                                                    + "' zu einem Integer. Parameter hat Wert '"
                                                    + getServletRequest()
                                                                    .getParameter(
                                                                                    _param)
                                                                    .toString()
                                                    + "'");
                }
            }
        }

        return retValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.ressources.IRequestResource#getLongArrayForParam
     * (java.lang.String)
     */
    public long[] getLongArrayForParam(String _param)
    {
        long returnValues[] = {};
        String values[] = null;

        if (getServletRequest().getParameter(_param) != null)
        {
            values = null;
            if ((values = getServletRequest().getParameterValues(_param)) != null)
            {
                returnValues = new long[values.length];

                for (int i = 0; i < values.length; i++)
                {
                    try
                    {
                        returnValues[i] = Long.valueOf(values[i]);
                    }
                    catch (NumberFormatException e)
                    {
                        log.error(e);
                        returnValues[i] = 0;
                    }
                }
            }
        }

        return returnValues;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.ressources.IRequestResource#getLongForParam(java
     * .lang.String)
     */
    public long getLongForParam(String _param)
    {
        long retValue = 0;

        if (getServletRequest().getParameter(_param) != null)
        {
            if (getServletRequest().getParameter(_param).toString().equals("") == false)
            {
                try
                {
                    retValue = Long.valueOf(getServletRequest().getParameter(
                                    _param).toString());
                }
                catch (NumberFormatException e)
                {
                    log.error("Fehler beim Aufloesen der Parameters '"
                                    + _param
                                    + "' zu einem Long. Parameter hat Wert '"
                                    + getServletRequest().getParameter(_param)
                                                    .toString() + "'");
                }
            }
        }

        return retValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.ressources.IRequestResource#getModifiedRequest()
     */
    public HttpServletRequest getModifiedRequest()
    {
        return getServletRequest();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.ressources.IRequestResource#getModifiedResponse()
     */
    public HttpServletResponse getModifiedResponse()
    {
        return getServletResponse();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.frontend.ressources.IRequestResource#getRequestDo()
     */
    public String getRequestDo()
    {
        return getStringForParam("do");
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.frontend.ressources.IRequestResource#getServletConfig()
     */
    public ServletConfig getServletConfig()
    {
        return servletConfig;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.ressources.IRequestResource#getServletRequest()
     */
    public HttpServletRequest getServletRequest()
    {
        return servletRequest;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.ressources.IRequestResource#getServletResponse()
     */
    public HttpServletResponse getServletResponse()
    {
        return servletResponse;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.frontend.ressources.IRequestResource#getSessionUser()
     */
    public UserBean getUserBean()
    {
        return (UserBean) getServletRequest().getSession().getAttribute(
                        USER_BEAN);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.ressources.IRequestResource#getStringArrayForParam
     * (java.lang.String)
     */
    public String[] getStringArrayForParam(String _param)
    {
        String returnValues[] = {};
        String values[] = null;

        if (getServletRequest().getParameter(_param) != null)
        {
            values = null;
            if ((values = getServletRequest().getParameterValues(_param)) != null)
            {
                returnValues = new String[values.length];

                for (int i = 0; i < values.length; i++)
                {
                    returnValues[i] = new String(values[i].toString());
                }
            }
        }

        return returnValues;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.ressources.IRequestResource#getStringForAttribute
     * (java.lang.String)
     */
    public String getStringForAttribute(String _attr)
    {
        if (getServletRequest().getAttribute(_attr) != null)
        {
            return getServletRequest().getAttribute(_attr).toString();
        }

        return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.ressources.IRequestResource#getStringForParam(java
     * .lang.String)
     */
    public String getStringForParam(String _param)
    {
        if (getServletRequest().getParameter(_param) != null)
        {
            return getServletRequest().getParameter(_param).toString();
        }

        return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.frontend.ressources.IRequestResource#getTab()
     */
    public String getTab()
    {
        return getStringForParam("tab");
    }

    /**
     * Überprüft, ob der Benutzer das Recht mit der angegebenen Id besitzt.
     * Besitzt er es nicht, wird automatisch die Meldung "Sie verfügen nicht
     * über das Recht dazu." als Fehler ausgegeben
     * 
     * @param _rechtId
     * @return true|false
     */
    protected boolean isActionAllowed(RechtId _rechtId)
    {
        return isActionAllowed(_rechtId,
                        "Sie verfügen nicht über das Recht dazu.");
    }

    /**
     * überprüft, ob der Benutzer das Recht mit der angegebenen Id besitzt und
     * dementsprechend die Action ausführen darf. Bei einem Fehler wird der
     * String _msg ausgegeben.
     * 
     * @param _rechtId
     *            Das zu überprüfende Recht
     * @param _msg
     *            Die Nachricht, die angezeigt werden soll
     */
    public boolean isActionAllowed(RechtId _rechtId, String _msg)
    {
        // Kein User
        if (getUserBean() == null)
        {
            return false;
        }

        if (getUserBean().getPerson() == null)
        {
            getErrorBean().addMessage("Sie sind nicht eingeloggt.");
            return false;
        }

        // Benutzer hat das Recht im aktuellen Kontext
        if (getUserBean().getAccessControlList().isRechtVerfuegbar(_rechtId))
        {
            return true;
        }

        if (_msg != null)
        {
            if (_msg.length() > 0)
            {
                getErrorBean().addMessage(_msg);
            }
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.frontend.ressources.IRequestResource#isValidSubmit()
     */
    public boolean isValidSubmit()
    {
        if (getServletRequest().getParameter("submit") != null)
        {
            return true;
        }

        return false;
    }

    /**
     * Wenn der übergebene _parameterName nicht null sein sollte, wird im
     * Request daraus die ID gebildet. Andernfalls erhält die ID des Parameters
     * den Wert "0"
     * 
     * @param _parameterName
     * @return
     */
    public RequestResources resolveId(String _parameterName)
    {
        if (getServletRequest().getParameter(_parameterName) != null)
        {
            setId(_parameterName, getLongForParam(_parameterName));
        }
        else
        {
            setId(_parameterName, 0);
        }

        return this;
    }

    /**
     * Delegiert an {@link HttpServletRequest#setAttribute(String, Object)}
     * 
     * @param _param
     * @param _o
     * @return
     */
    public RequestResources setData(String _param, Object _o)
    {
        getServletRequest().setAttribute(_param, _o);
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.frontend.ressources.IRequestResource#getDBRessource()
     */
    public void setDbResource(DBResource _db)
    {
        db = _db;
    }

    /**
     * Setzt die Seite, an die geforwardet wird
     * 
     * @param forwardPage
     */
    public void setForwardPage(String forwardPage)
    {
        this.forwardPage = forwardPage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.ressources.IRequestResource#setId(java.lang.String,
     * long)
     */
    public void setId(String _param, long _id)
    {
        getServletRequest().setAttribute(_param, Long.valueOf(_id));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.ressources.IRequestResource#setServletConfig(javax
     * .servlet.ServletConfig)
     */
    public void setServletConfig(ServletConfig servletConfig)
    {
        this.servletConfig = servletConfig;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.ressources.IRequestResource#setServletRequest(javax
     * .servlet.http.HttpServletRequest)
     */
    public void setServletRequest(HttpServletRequest servletRequest)
    {
        this.servletRequest = servletRequest;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.ressources.IRequestResource#setServletResponse(
     * javax.servlet.http.HttpServletResponse)
     */
    public void setServletResponse(HttpServletResponse res)
    {
        this.servletResponse = res;
    }
}
