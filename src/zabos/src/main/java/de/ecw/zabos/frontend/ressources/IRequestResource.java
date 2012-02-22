package de.ecw.zabos.frontend.ressources;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.ecw.zabos.frontend.beans.MessageContainerBean;
import de.ecw.zabos.frontend.beans.UserBean;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.types.id.BaseId;

/**
 * Interface für die HTTP-Anfragen.
 * 
 * @author ckl
 */
public interface IRequestResource
{

    /**
     * Bean mit den Fehlermeldungen
     */
    public final static String ERROR_BEAN = "errors";

    /**
     * Bean mit den Hinweis-Texten
     */
    public final static String INFO_BEAN = "info";

    /**
     * Bean mit den Benutzerinformationen
     */
    public final static String USER_BEAN = "user";

    /**
     * Liefert eine BaseId für das angegebene Attribut zurück.<br>
     * Existiert das Attribut "OrganisationId" mit dem Wert '1', wird ein
     * BaseId(1) zurückgeliefert.<br>
     * Existiert das Attribut nicht, ist der Rückgabewert null.
     * 
     * @param _attr
     *            Attribut, dass abgefragt werden soll.
     * @return BaseId oder null
     */
    public BaseId getBaseId(String _attr, BaseId _idSignature);

    /**
     * Liefert true bzw. false zurück, wenn der Parameter gesetzt ist
     * 
     * @param _param
     * @return true|false
     */
    public boolean getBoolForParam(String _param);

    /**
     * Liefert die Datenbank-Ressource
     * 
     * @return DBResource
     */
    public DBResource getDbResource();

    /**
     * Liefert das Errors-Objekt des Requests.<br />
     * Das Objekt ist <strong>niemals</strong> null.
     * 
     * @return {@link MessageContainerBean}
     */
    public MessageContainerBean getErrorBean();

    /**
     * Liefert das Info-Objekt des Requests.<br />
     * Das Objekt ist <strong>niemals</strong> null.
     * 
     * @return {@link MessageContainerBean}
     */
    public MessageContainerBean getInfoBean();

    /**
     * Liefert die Id des Attributs
     * 
     * @param _attr
     *            Attribut
     */
    public long getId(String _attr);

    /**
     * Liefert ein Intger für einen Parameter
     * 
     * @param _param
     * @return int bzw. null wenn Parameter nicht gesetzt
     */
    public int getIntForParam(String _param);

    /**
     * Liefert eine Liste mit den übergebenen Parametern des HTML-Objekts
     * zurück.<br>
     * Wird für die Select-Boxen benötigt, die eine ID besitzen
     * 
     * @param _param
     *            Parameter
     * @return long[] Werte, die Select-Box inne hat
     */
    public long[] getLongArrayForParam(String _param);

    /**
     * Überprüft, ob im Request der Parameter _param gesetzt ist
     * 
     * @param _param
     * @return int bzw. null wenn der Param nicht gesetzt ist
     */
    public long getLongForParam(String _param);

    /**
     * Liefert den veränderten Request
     * 
     * @return HttpServletRequest
     */
    public HttpServletRequest getModifiedRequest();

    /**
     * Liefert den veränderten Response
     * 
     * @return HttpServletResponse
     */
    public HttpServletResponse getModifiedResponse();

    /**
     * Liefert den Wert der Action
     * 
     * @return String mit der Action
     */
    public String getRequestDo();

    /**
     * Liefert die Servlet-Konfiguration
     * 
     * @return
     */
    public ServletConfig getServletConfig();

    /**
     * Liefert die Servlet-Anfrage
     * 
     * @return
     */
    public HttpServletRequest getServletRequest();

    /**
     * Liefert die Servlet-Antwort
     * 
     * @return
     */
    public HttpServletResponse getServletResponse();

    /**
     * Liefert das Objekt des aktuellen Benutzers.<br />
     * Das Objekt ist <strong>niemals</strong> null.
     * 
     * @return UserBean
     */
    public UserBean getUserBean();

    /**
     * Liefert eine Liste mit den übergebenen Parametern des HTML-Objekts
     * zurück.<br>
     * Wird primär für das dynamische Hinzufügen von Telefonen beim Anlegen
     * einer neuen Person benutzt
     * 
     * @param _param
     *            Parameter
     * @return String[] Werte, die die Input-Boxen inne hat
     */
    public String[] getStringArrayForParam(String _param);

    /**
     * Liefert das Attribut als String zurück. Ist das Attribut nicht gesetzt,
     * wird ein leerer String zurückgeliefert
     * 
     * @param _attr
     * @return String
     */
    public String getStringForAttribute(String _attr);

    /**
     * Liefert den Parameter als String zurück. Ist der Parameter nicht gesetzt,
     * wird ein leerer String ("") zurückgeliefert
     * 
     * @param _param
     * @return String
     */
    public String getStringForParam(String _param);

    /**
     * Liefert den Tab zurück, der gerade geladen wurde
     * 
     * @return String mit dem Tab
     */
    public String getTab();

    /**
     * Liefert zurück, ob der Submit eines Formulars durchgeführt wurde und das
     * Submit auch wirklich zu der Controller-/View-Kombination passt.<br>
     * Es wird überprüft, ob der Parameter &submit=* gesetzt ist.
     * 
     * @return true|false
     */
    public boolean isValidSubmit();

    /**
     * Setzt die Datenbank-Resource
     * 
     * @param DBResource
     */
    public void setDbResource(DBResource _db);

    /**
     * Setzt die Id mit dem Namen _param
     * 
     * @param _param
     *            Parameter
     * @param _id
     *            Id
     */
    public void setId(String _param, long _id);

    /**
     * Setzt die Servlet-Konfiguration
     * 
     * @param servletConfig
     */
    public void setServletConfig(ServletConfig servletConfig);

    /**
     * Setzt die Servlet-Anfrage
     * 
     * @param servletRequest
     */
    public void setServletRequest(HttpServletRequest servletRequest);

    /**
     * Setzt die Servlet-Antwort
     * 
     * @param res
     */
    public void setServletResponse(HttpServletResponse res);

}