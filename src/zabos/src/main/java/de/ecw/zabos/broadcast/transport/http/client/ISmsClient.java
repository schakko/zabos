package de.ecw.zabos.broadcast.transport.http.client;

import java.util.List;

import de.ecw.zabos.broadcast.Recipient;
import de.ecw.zabos.license.License;

public interface ISmsClient
{
    /**
     * Baut aus den ggb. Parametern einen URL-encodete Aufrufstring zusammen. Es
     * werden die Daten des ersten Empfaengers aus der ArrayList als
     * SMS-Informationen gewählt!
     * 
     * @param _r
     *            ArrayList mit den Empfängern
     * @return
     */
    public String buildSMSRequest(List<Recipient> _r);

    /**
     * Liefert den Service-Port eines Clients
     * 
     * @return
     * @author ckl
     */
    public int getSmsServicePort();

    /**
     * Liefert die Service-Hosts eines Clients
     * 
     * @return
     * @author ckl
     */
    public String[] getSmsServiceHosts();

    /**
     * Liefert das Passwort als MD5
     * 
     * @return
     */
    public String getSmsPasswordMd5();

    /**
     * Liefert das Passwort unverschlüsselt
     * 
     * @return
     */
    public String getSmsPassword();

    /**
     * Liefert den Benutzernamen
     * 
     * @return
     */
    public String getSmsUsername();

    /**
     * Liefert die Lizenz
     * 
     * @param _license
     */
    public void setLicense(License _license);

    /**
     * Setzt die Adresse des Proxy-Servers
     * 
     * @param _proxyServer
     */
    public void setProxyServer(String _proxyServer);

    /**
     * Setzt den Proxy-Port
     * 
     * @param _proxyPort
     */
    public void setProxyPort(int _proxyPort);

    /**
     * Setzt den Proxy-Benutzernamen
     * 
     * @param _proxyUsername
     */
    public void setProxyUsername(String _proxyUsername);

    /**
     * Setzt das Passwort des Proxy-Benutzers
     * 
     * @param _proxyPassword
     */
    public void setProxyPassword(String _proxyPassword);

    /**
     * Liefert das Proxy-Passwort
     * 
     * @return
     */
    public String getProxyPassword();

    /**
     * Liefert den Proxy-Port
     * 
     * @return
     */
    public int getProxyPort();

    /**
     * Liefert den Proxy-Server
     * 
     * @return
     */
    public String getProxyServer();

    /**
     * Liefert den Proxy-Benutzer
     * 
     * @return
     */
    public String getProxyUsername();
}