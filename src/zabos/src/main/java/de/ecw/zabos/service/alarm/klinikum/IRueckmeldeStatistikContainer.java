package de.ecw.zabos.service.alarm.klinikum;

import java.util.List;

public interface IRueckmeldeStatistikContainer extends IRueckmeldeStatistik
{
    public Object getObject();

    /**
     * Fügt ein Element hinzu, dass eine positive Rückmeldung gegeben hat
     * 
     * @return
     */
    public void addJa(IRueckmeldeStatistik _stat);

    /**
     * Fügt ein Element hinzu, dass eine negative Rückmeldung gegeben hat
     * 
     * @return
     */
    public void addNein(IRueckmeldeStatistik _nein);

    /**
     * Fügt ein Element hinzu, dass keine Rückmeldung gegeben hat
     * 
     * @return
     */
    public void addUnbekannt(IRueckmeldeStatistik _unbekannt);

    /**
     * Fügt ein Element hinzu, dass eine spätere Rückmeldung gegeben hat
     * 
     * @return
     */
    public void addSpaeter(IRueckmeldeStatistik _unbekannt);

    /**
     * Liefert die Elemente zurück, die mit Ja geantwortet haben
     * 
     * @return
     */
    public List<IRueckmeldeStatistik> getJa();

    /**
     * Liefert die Elemente zurück, die mit Nein geantwortet haben
     * 
     * @return
     */
    public List<IRueckmeldeStatistik> getNein();

    /**
     * Liefert die Elemente zurück, die mit Später geantwortet haben
     * 
     * @return
     */
    public List<IRueckmeldeStatistik> getSpeater();

    /**
     * Liefert die Elemente zurück, die nicht geantwortet haben
     * 
     * @return
     */
    public List<IRueckmeldeStatistik> getUnbekannt();
}
