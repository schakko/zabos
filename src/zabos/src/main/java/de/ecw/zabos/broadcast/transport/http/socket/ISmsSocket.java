package de.ecw.zabos.broadcast.transport.http.socket;

import de.ecw.zabos.broadcast.transport.http.client.ISmsClient;

/**
 * Interface zum Beschreiben der einzelnen UDP/TCP-Sockets zu den jeweiligen
 * SMS-Gateways
 * 
 * @author ckl
 * 
 */
public interface ISmsSocket
{
    /**
     * Liefert den Status-Code zurück
     * 
     * @return
     */
    public int getStatusCode();

    /**
     * Liefert das Resultat zurück
     * 
     * @return
     */
    public String getResult();

    /**
     * Wird neu versucht?
     * 
     * @return
     */
    public boolean isRetrying();

    /**
     * Verbindung zum Host öffnen
     * 
     * @return
     */
    public boolean connect();

    /**
     * Disconnected von Host
     * 
     * @return
     */
    public boolean disconnect();

    /**
     * Sendet den Get-Request
     * 
     * @param _requestURI
     * @return
     */
    public boolean sendGETRequest(String _requestURI);

    /**
     * Überprüft den Socket
     * 
     * @return
     */
    public boolean checkSocket();

    /**
     * Setzt die Tracker-Id fuer die Nachverfolgung von Verbindungsversuchen
     * 
     * @param _trackerId
     * @return
     */
    public void setTrackerId(String _trackerId);

    /**
     * Liefert einen String der Form "[$tracker_id] " zurueck
     * 
     * @return
     */
    public String getDisplayTrackerId();

    /**
     * Liefert den {@link ISmsClient}
     * 
     * @return
     */
    public ISmsClient getSmsClient();

    /**
     * Setzt den {@link ISmsClient}
     * 
     * @param smsClient
     */
    public void setSmsClient(ISmsClient smsClient);

}