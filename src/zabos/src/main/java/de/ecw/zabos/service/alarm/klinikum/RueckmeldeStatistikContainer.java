package de.ecw.zabos.service.alarm.klinikum;

import java.util.ArrayList;
import java.util.List;

/**
 * Container-Klasse für die Rückmeldestatistik
 * 
 * @author ckl
 * 
 */
public class RueckmeldeStatistikContainer implements
                IRueckmeldeStatistikContainer
{
    /**
     * Objekt, das als Referenz dient
     */
    private Object o;

    /**
     * Ja
     */
    private List<IRueckmeldeStatistik> listJa = new ArrayList<IRueckmeldeStatistik>();

    /**
     * Nein
     */
    private List<IRueckmeldeStatistik> listNein = new ArrayList<IRueckmeldeStatistik>();

    /**
     * Später
     */
    private List<IRueckmeldeStatistik> listSpaeter = new ArrayList<IRueckmeldeStatistik>();

    /**
     * Unbekannt
     */
    private List<IRueckmeldeStatistik> listUnbekannt = new ArrayList<IRueckmeldeStatistik>();

    public RueckmeldeStatistikContainer(Object _o)
    {
        o = _o;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.service.alarm.klinikum.IRueckmeldeStatistikContainer#getObject
     * ()
     */
    public Object getObject()
    {
        return o;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.service.alarm.klinikum.IRueckmeldeStatistikContainer#addJa
     * (de.ecw.zabos.service.alarm.klinikum.IRueckmeldeStatistik)
     */
    public void addJa(IRueckmeldeStatistik stat)
    {
        listJa.add(stat);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.service.alarm.klinikum.IRueckmeldeStatistikContainer#addNein
     * (de.ecw.zabos.service.alarm.klinikum.IRueckmeldeStatistik)
     */
    public void addNein(IRueckmeldeStatistik nein)
    {
        listNein.add(nein);
    }

    /*
     * (non-Javadoc)
     * 
     * @seede.ecw.zabos.service.alarm.klinikum.IRueckmeldeStatistikContainer#
     * addUnbekannt(de.ecw.zabos.service.alarm.klinikum.IRueckmeldeStatistik)
     */
    public void addUnbekannt(IRueckmeldeStatistik unbekannt)
    {
        listUnbekannt.add(unbekannt);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.service.alarm.klinikum.IRueckmeldeStatistik#getTotalJa()
     */
    public int getTotalJa()
    {
        int r = 0;

        for (int i = 0, m = listJa.size(); i < m; i++)
        {
            r += listJa.get(i).getTotalJa();
        }

        return r;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.service.alarm.klinikum.IRueckmeldeStatistik#getTotalNein()
     */
    public int getTotalNein()
    {
        int r = 0;

        for (int i = 0, m = listJa.size(); i < m; i++)
        {
            r += listJa.get(i).getTotalNein();
        }

        return r;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.service.alarm.klinikum.IRueckmeldeStatistik#getTotalUnbekannt
     * ()
     */
    public int getTotalUnbekannt()
    {
        int r = 0;

        for (int i = 0, m = listJa.size(); i < m; i++)
        {
            r += listJa.get(i).getTotalUnbekannt();
        }

        return r;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.service.alarm.klinikum.IRueckmeldeStatistik#getTotalSpaeter
     * ()
     */
    public int getTotalSpaeter()
    {
        int r = 0;

        for (int i = 0, m = listJa.size(); i < m; i++)
        {
            r += listJa.get(i).getTotalSpaeter();
        }

        return r;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.ecw.zabos.service.alarm.klinikum.IRueckmeldeStatistik#getTotal()
     */
    public int getTotal()
    {
        return getTotalJa() + getTotalNein() + getTotalSpaeter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.service.alarm.klinikum.IRueckmeldeStatistikContainer#addSpaeter
     * (de.ecw.zabos.service.alarm.klinikum.IRueckmeldeStatistik)
     */
    public void addSpaeter(IRueckmeldeStatistik unbekannt)
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.service.alarm.klinikum.IRueckmeldeStatistikContainer#getJa()
     */
    public List<IRueckmeldeStatistik> getJa()
    {
        return listJa;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.service.alarm.klinikum.IRueckmeldeStatistikContainer#getNein
     * ()
     */
    public List<IRueckmeldeStatistik> getNein()
    {
        return listNein;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.service.alarm.klinikum.IRueckmeldeStatistikContainer#getSpeater
     * ()
     */
    public List<IRueckmeldeStatistik> getSpeater()
    {
        return listSpaeter;
    }

    /*
     * (non-Javadoc)
     * 
     * @seede.ecw.zabos.service.alarm.klinikum.IRueckmeldeStatistikContainer#
     * getUnbekannt()
     */
    public List<IRueckmeldeStatistik> getUnbekannt()
    {
        return listUnbekannt;
    }
}
