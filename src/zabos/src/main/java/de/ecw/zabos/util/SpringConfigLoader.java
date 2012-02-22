package de.ecw.zabos.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;

import org.apache.log4j.Logger;
import org.springframework.context.support.AbstractRefreshableConfigApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * Lädt die Konfiguration aus dem WEB-INF-Verzeichnis.
 * 
 * @author ckl
 * 
 */
public class SpringConfigLoader
{
    private ServletConfig servletConfig;

    private AbstractRefreshableConfigApplicationContext applicationContext;

    public final static String DEFAULT_CONFIG_FILE_PREFIX = "applicationContext";

    public final static String CONFIG_DIRECTORY = "config";

    public final static String CONFIG_DIRECTORY_DEFAULT = "default";

    private final static Logger log = Logger
                    .getLogger(SpringConfigLoader.class);

    public SpringConfigLoader(
                    ServletConfig _servletConfig,
                    AbstractRefreshableConfigApplicationContext _applicationContext)
    {
        setServletConfig(_servletConfig);
        setApplicationContext(_applicationContext);
    }

    final public void setServletConfig(ServletConfig servletConfig)
    {
        this.servletConfig = servletConfig;
    }

    final public ServletConfig getServletConfig()
    {
        return servletConfig;
    }

    final public void setApplicationContext(
                    AbstractRefreshableConfigApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }

    final public AbstractRefreshableConfigApplicationContext getApplicationContext()
    {
        return applicationContext;
    }

    /**
     * Lädt die Konfigurationsdatei. Suchreihenfolge ist:
     * <ul>
     * <li>/WEB-INF/config/${hostname}/applicationContext.xml</li>
     * <li>/WEB-INF/applicationContext-${hostname}.xml</li>
     * <li>/WEB-INF/config/${ip}/applicationContext.xml</li>
     * <li>/WEB-INF/applicationContext-${ip}.xml</li>
     * <li>/WEB-INF/config/default/applicationContext.xml</li>
     * <li>/WEB-INF/config/applicationContext-default.xml</li>
     * <li>/WEB-INF/applicationContext.xml</li>
     * </ul>
     * Die erste Datei, die gefunden wird, wird auch benutzt
     */
    public void loadConfiguration()
    {
        List<String> candidates = getConfigurationLocationCandidates();
        String useFileName = getPreferedConfigLocation(candidates);
        getApplicationContext().setConfigLocation(useFileName);
    }

    /**
     * Liefert die Liste mit den möglichen Orten der Konfigurationsdatei zurück
     * 
     * @return
     */
    public List<String> getConfigurationLocationCandidates()
    {
        List<String> configLocations = new ArrayList<String>();

        try
        {
            String hostName = java.net.InetAddress.getLocalHost().getHostName();
            appendConfigurationLocations(configLocations, hostName);
            String ip = java.net.InetAddress.getLocalHost().getHostAddress();
            appendConfigurationLocations(configLocations, ip);
        }
        catch (Exception e)
        {
            log.error("Konnte lokalen Hostnamen nicht aufloesen, benutzte Standard-XML-Konfiguration");
        }

        appendConfigurationLocations(configLocations, CONFIG_DIRECTORY_DEFAULT);

        return configLocations;

    }

    /**
     * Überprüft alle Verzeichnisangaben innerhalb der Liste auf Existenz und
     * liefert den ersten Treffer zurück.
     * 
     * @param configLocations
     * @return
     */
    public String getPreferedConfigLocation(List<String> configLocations)
    {
        String useFileName = null;

        for (int i = 0, m = configLocations.size(); i < m; i++)
        {
            String fileName = configLocations.get(i);

            if (isConfigLocationAvailable(fileName))
            {
                log.info("Konfigurationsdatei \""
                                + fileName
                                + "\" existiert, benutze diese als Standardkonfiguration");
                useFileName = fileName;
                break;
            }
        }

        if (useFileName == null)
        {
            return XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION;
        }
        
        return useFileName;
    }

    /**
     * Liefert zurück, ob die Konfigurationsdatei existiert
     * 
     * @param _configLocation
     * @return
     */
    public boolean isConfigLocationAvailable(String _configLocation)
    {
        String fullPath = getServletConfig().getServletContext().getRealPath(
                        _configLocation);

        log.debug("Ueberpruefe, ob Konfigrationsatei \"" + fullPath
                        + "\" existiert...");

        return new File(fullPath).exists();
    }

    /**
     * Fügt der Liste der Suchpfade
     * <ul>
     * <li>/WEB-INF/{@value #CONFIG_DIRECTORY}
     * /_configSubDirectory/applicationContext.xml</li>
     * <li>und /WEB-INF/applicationContext-_configSubDirectory.xml</li>
     * </ul>
     * hinzu
     * 
     * @param _configLocations
     * @param _configSubDirectory
     */
    public void appendConfigurationLocations(List<String> _configLocations,
                    String _configSubDirectory)
    {
        _configLocations.add(XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION_PREFIX
                        + CONFIG_DIRECTORY
                        + "/"
                        + _configSubDirectory
                        + "/"
                        + DEFAULT_CONFIG_FILE_PREFIX
                        + XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION_SUFFIX);

        _configLocations.add(XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION_PREFIX
                        + DEFAULT_CONFIG_FILE_PREFIX
                        + "-"
                        + _configSubDirectory
                        + XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION_SUFFIX);
    }
}
