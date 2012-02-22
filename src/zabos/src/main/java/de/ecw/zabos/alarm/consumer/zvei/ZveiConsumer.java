package de.ecw.zabos.alarm.consumer.zvei;

import org.apache.log4j.Logger;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.service.alarm.IAlarmService;
import de.ecw.zabos.sql.dao.SchleifenDAO;
import de.ecw.zabos.sql.dao.SystemKonfigurationDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.tao.FuenfTonTAO;
import de.ecw.zabos.sql.vo.FuenfTonVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.AlarmQuelleId;

public class ZveiConsumer
{
    private final static Logger log = Logger.getLogger(ZveiConsumer.class);

    /**
     * Zeitspanne in Millisekunden, in der der Fuenftondaemon die Wiederholung
     * einer ausgeloesten Schleife nicht mehr annimmt
     * 
     * @author ckl
     */
    private static final long FUENFTON_WIEDERHOLUNG_TIMEOUT = 1500;

    private DBResource dbresource;

    /**
     * {@link IAlarmService}
     */
    private IAlarmService alarmService;

    private boolean ausloesungDeaktiviert = false;

    public boolean isAusloesungDeaktiviert()
    {
        return ausloesungDeaktiviert;
    }

    public void setAusloesungDeaktiviert(boolean ausloesungDeaktiviert)
    {
        this.ausloesungDeaktiviert = ausloesungDeaktiviert;
    }

    /**
     * @param _dbResource
     *            Unbedingt eine neue Datenbankinstanz übergeben, da diese
     *            Klasse gethreadet wird.
     */
    public ZveiConsumer(final DBResource _dbResource)
    {
        this.dbresource = _dbResource;
    }

    /**
     * Verarbeitet die eingehenden 5-Ton-Folgerufe. Die Methode ist als
     * synchronized markiert, da damit verhindert werden soll, dass gleichzeitig
     * die Wiederholungen eines 5-Tonfolgerufs in die Datenbank geschrieben
     * werden.
     * 
     * @param fuenfton
     */
    public synchronized void process5Ton(String fuenfton)
    {
        log.debug("Verarbeite eingehenden Fuenf-Ton-Folgeruf \"" + fuenfton
                        + "\"");

        try
        {
            SystemKonfigurationDAO systemKonfigurationDAO = dbresource
                            .getDaoFactory().getSystemKonfigurationDAO();
            if (systemKonfigurationDAO.istSystemDeaktiviert())
            {
                log.debug("Der Fuenf-Tonfolge-Ruf \""
                                + fuenfton
                                + "\" wird ignoriert da das System deaktiviert ist.");
                return;
            }

            FuenfTonVO letzterEmpfangerFuenfon = dbresource.getDaoFactory()
                            .getFuenfTonDAO().findLatestFuenfTon(fuenfton);

            if (letzterEmpfangerFuenfon != null)
            {
                if ((letzterEmpfangerFuenfon.getZeitpunkt().getTimeStamp() + FUENFTON_WIEDERHOLUNG_TIMEOUT) >= UnixTime
                                .now().getTimeStamp())
                {
                    log.debug("Fuenf-Ton-Folgeruf als Wiederholung identifiziert.");
                    return;

                }
            }

            FuenfTonVO vo = dbresource.getObjectFactory().createFuenfTon();
            vo.setZeitpunkt(UnixTime.now());
            vo.setFolge(fuenfton);
            FuenfTonTAO fuenftonTAO = dbresource.getTaoFactory()
                            .getFuenfTonTAO();
            fuenftonTAO.createFuenfTon(vo);

            // Schleife zu Fuenfton ermitteln
            SchleifenDAO schleifenDAO = dbresource.getDaoFactory()
                            .getSchleifenDAO();
            /*
             * 2008-03-17 CKL: Es werden jetzt nur noch die Schleifen aus der
             * Datenbank geladen, die das Flag "geloescht" ***nicht*** gesetzt
             * haben. Grund war der, dass ein Benutzer seine Schleife geloescht
             * hat und in einer anderen OE neu erstellte. Der Fuenfton blieb
             * gleich, die Schleife wurde aber nicht alarmiert. Dies kam durch
             * das "geloescht"-Flag. In der Schleifen-Tabelle existieren zwei
             * Eintraege mit dem selben Fuenfton, es wurde nur der erste Eintrag
             * geladen. Dieser hatte das "geloescht"-Flag aktiviert.
             */
            SchleifeVO schleifeVO = schleifenDAO
                            .findSchleifeByFuenfton(fuenfton);

            if (schleifeVO == null)
            {
                log.debug("dem empfangenen FuenfTonRuf \"" + fuenfton
                                + "\" ist keine Schleife zugeordnet!");
                return;
            }

            // 2006-07-21 CKL: Schleife wird nicht ausgeloest, wenn sie
            // bereits geloescht ist.
            if (schleifeVO.getGeloescht())
            {
                log.debug("die Schleife, die dem FuenfTonRuf \""
                                + fuenfton
                                + "\" zugeordnet ist, wurde als geloescht markiert.");
                return;
            }

            log.debug("Schleife kuerzel=\"" + schleifeVO.getKuerzel()
                            + "\" gefunden!");
            /*
             * Dem 5TonRuf *ist* eine Schleife zugeordnet Schauen ob die
             * Schleife gerade nur getestet wird oder ausgeloest werden soll
             */
            if (schleifenDAO.istSchleifeInProbeAlarm(schleifeVO.getSchleifeId()))
            {
                log.debug("die Schleife, die dem empfangenen FuenfTonRuf \""
                                + fuenfton
                                + "\" zugeordnet ist wird gerade probealarmiert. ==> keine AlarmAusloesung.");
                return;
            }

            log.debug("Schleife wird *nicht* probealarmiert und nun ausgeloest");

            if (isAusloesungDeaktiviert())
            {
                log.info("Alarm wird nicht ausgeloest, da dies in der Konfiguration manuelle deaktiviert worden ist");
                return;
            }

            /*
             * Schleife wird gerade *nicht* getestet ==> Alarm ausloesen!
             * 2007-06-21 CKL: GPS-Koordinaten werden fuer einen 5Ton nicht
             * gesetzt
             */
            getAlarmService().alarmAusloesen("", new SchleifeVO[]
            { schleifeVO }, AlarmQuelleId.ID_5TON, null /* PersonId */, null,
                            null);
        }
        catch (StdException e)
        {
            log.error(e);
        }
    }

    /**
     * Setzt den {@link IAlarmService}
     * 
     * @param alarmService
     */
    final public void setAlarmService(IAlarmService alarmService)
    {
        this.alarmService = alarmService;
    }

    /**
     * Liefert den {@link IAlarmService}
     * 
     * @return
     */
    final public IAlarmService getAlarmService()
    {
        return alarmService;
    }

}
