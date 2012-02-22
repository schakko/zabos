package de.ecw.zabos.service.alarm.klinikum;

public interface IRueckmeldeStatistik
{
    /**
     * Liefert die Anzahl der Elemente zurueck, die mit Ja geantwortet haben
     * 
     * @return
     */
    public int getTotalJa();

    /**
     * Liefert die Anzahl der Elemente zurueck, die mit Nein geantwortet haben
     * 
     * @return
     */
    public int getTotalNein();

    /**
     * Liefert die Anzahl der Elemente zurueck, die mit Spaeter geantwortet
     * haben
     * 
     * @return
     */
    public int getTotalSpaeter();

    /**
     * Liefert die Anzahl der Elemente zurueck, die nicht geantwortet haben
     * 
     * @return
     */
    public int getTotalUnbekannt();

    /**
     * Liefert die Anzahl der Elemente zurueck, die sich zur�ckgemeldet haben
     * 
     * @return
     */
    public int getTotal();

}
