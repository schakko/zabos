package de.ecw.zabos.broadcast.transport.http.client;

import java.util.List;

import de.ecw.zabos.broadcast.Recipient;
import de.ecw.zabos.license.License;
import de.ecw.zabos.util.StringUtils;

/**
 * Abstrakte Klasse für SMS-Clients
 * 
 * @author ckl
 */
public abstract class AbstractSmsClient implements ISmsClient
{
    private License license;

    private int smsServicePort = 80;

    private int proxyPort = 80;

    private String proxyServer;

    private String proxyUsername;

    private String proxyPassword;

    public String smsUsername;

    public String smsPassword;

    /**
     * Lizenz kommt in den Konstruktor, da sie auf jeden Fall gesetzt sein muss
     * 
     * @param _license
     */
    public AbstractSmsClient(License _license)
    {
        setLicense(_license);
    }

    /**
     * Alternativer Konstruktor, wo Benutzername und Passwort übergeben werden
     * 
     * @param _smsUsername
     * @param _smsPassword
     */
    public AbstractSmsClient(String _smsUsername, String _smsPassword)
    {
        setSmsUsername(_smsUsername);
        setSmsPassword(_smsPassword);
    }

    /**
     * Ggb. String URL encoden, d.h. aus \000 wird %00 z.b. Wir können leider
     * nicht URLEncoder.encode() verwenden da aufgrund eines PHP Fehlers dieser
     * String nicht wieder richtig decoded werden kann.
     * 
     * @param _src
     * @return URL encoded string
     */
    protected String custUrlEncode(String _src)
    {
        int len = _src.length();
        StringBuffer ret = new StringBuffer();
        for (int i = 0; i < len; i++)
        {
            // //ret.append("%");
            ret.append(StringUtils.byteToHexString((byte) _src.charAt(i)));
        }
        return ret.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.sms.gateway.client.ISmsClient#BuildSMSRequest(java.util.
     * ArrayList)
     */
    abstract public String buildSMSRequest(List<Recipient> _r);

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.sms.gateway.client.ISmsClient#getSmsServicePort()
     */
    public int getSmsServicePort()
    {
        return smsServicePort;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.sms.gateway.client.ISmsClient#getSmsServiceHosts()
     */
    public String[] getSmsServiceHosts()
    {
        return new String[] {};
    }

    /**
     * Setzt das Passwort
     * 
     * @param _smsPassword
     * @return
     */
    public void setSmsPassword(String _smsPassword)
    {
        smsPassword = _smsPassword;
    }

    /**
     * Setzt den Benutzernamen
     * 
     * @param _smsUsername
     */
    public void setSmsUsername(String _smsUsername)
    {
        smsUsername = _smsUsername;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.sms.gateway.client.ISmsClient#getSmsPasswordMd5()
     */
    public String getSmsPasswordMd5()
    {
        return StringUtils.md5(getSmsPassword());
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.sms.gateway.client.ISmsClient#getSmsPassword()
     */
    public String getSmsPassword()
    {
        return smsPassword;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.sms.gateway.client.ISmsClient#getSmsUsername()
     */
    public String getSmsUsername()
    {
        return smsUsername;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.sms.gateway.client.ISmsClient#setLicense(de.ecw.zabos.license
     * .License)
     */
    final public void setLicense(License _license)
    {
        license = _license;
        setSmsUsername(license.getGatewayUser());
        setSmsPassword(license.getGatewayPasswd());
    }

    /**
     * Liefert die {@link License}
     * 
     * @return
     */
    final public License getLicense()
    {
        return license;
    }

    final public void setProxyPassword(String _proxyPassword)
    {
        proxyPassword = _proxyPassword;
    }

    final public void setProxyPort(int _proxyPort)
    {
        proxyPort = _proxyPort;
    }

    final public void setProxyServer(String _proxyServer)
    {
        proxyServer = _proxyServer;
    }

    final public void setProxyUsername(String _proxyUsername)
    {
        proxyUsername = _proxyUsername;
    }

    final public String getProxyPassword()
    {
        return proxyPassword;
    }

    final public int getProxyPort()
    {
        return proxyPort;
    }

    final public String getProxyServer()
    {
        return proxyServer;
    }

    final public String getProxyUsername()
    {
        return proxyUsername;
    }
}
