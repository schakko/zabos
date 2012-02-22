package de.ecw.zabos.frontend.controllers.helpers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.beans.DateBean;
import de.ecw.zabos.frontend.ressources.RequestResources;
import de.ecw.zabos.sql.tao.ProbeTerminTAO;
import de.ecw.zabos.sql.vo.ProbeTerminVO;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.OrganisationsEinheitId;

/**
 * Hilfsklasse zum Erzeugen der Probetermine
 * 
 * @author ckl
 * 
 */
public class ProbeterminHelper
{
    RequestResources req = null;

    ProbeTerminTAO taoProbeTermin = null;

    private final static Logger log = Logger.getLogger(ProbeterminHelper.class);

    public ProbeterminHelper(final RequestResources _req)
    {
        req = _req;
        taoProbeTermin = req.getDbResource().getTaoFactory()
                        .getProbeTerminTAO();

        assert taoProbeTermin != null;

    }

    /**
     * Liefrt die Wochen zur�ck, in der ein Ereignis stattfinden soll
     * 
     * @return
     */
    public List<String> getWochenImMonat()
    {
        List<String> r = new ArrayList<String>();

        if (req.getBoolForParam("checkErsten"))
        {
            r.add("1");
        }

        if (req.getBoolForParam("checkZweiten"))
        {
            r.add("2");
        }

        if (req.getBoolForParam("checkDritten"))
        {
            r.add("3");
        }

        if (req.getBoolForParam("checkVierten"))
        {
            r.add("4");
        }

        if (req.getBoolForParam("checkLetzten"))
        {
            r.add("5");
        }

        return r;
    }

    /**
     * Liefert die Tage der Woche
     * 
     * @return
     */
    public List<String> getTageInWoche()
    {
        List<String> r = new ArrayList<String>();

        if (req.getBoolForParam("checkSonntag"))
        {
            r.add("1");
        }

        if (req.getBoolForParam("checkMontag"))
        {
            r.add("2");
        }

        if (req.getBoolForParam("checkDienstag"))
        {
            r.add("3");
        }

        if (req.getBoolForParam("checkMittwoch"))
        {
            r.add("4");
        }

        if (req.getBoolForParam("checkDonnerstag"))
        {
            r.add("5");
        }

        if (req.getBoolForParam("checkFreitag"))
        {
            r.add("6");
        }

        if (req.getBoolForParam("checkSamstag"))
        {
            r.add("7");
        }

        return r;
    }

    /**
     * Erstellt einen einmaligen Probetermin
     * 
     * @throws StdException
     */
    public void createProbetermineEinmalig() throws StdException
    {
        log.debug("Trage Probetermin ein");
        ProbeTerminVO voProbeTermin = taoProbeTermin.getDBResource()
                        .getObjectFactory().createProbeTermin();
        DateBean dateStart = new DateBean();
        DateBean dateEnde = new DateBean();

        // Zeit & Datum setzen
        dateStart.setDate(req.getStringForParam("textDatum"));
        dateEnde.setDate(req.getStringForParam("textDatum"));
        dateStart.setTime(req.getStringForParam("textZeitStart"));
        dateEnde.setTime(req.getStringForParam("textZeitEnde"));

        // Probetermin erstellen
        voProbeTermin.setOrganisationsEinheitId(req.getUserBean().getCtxOE()
                        .getOrganisationsEinheitId());
        voProbeTermin.setStart(new UnixTime(dateStart.getTimestamp()));
        voProbeTermin.setEnde(new UnixTime(dateEnde.getTimestamp()));
        taoProbeTermin.createProbeTermin(voProbeTermin);
    }

    /**
     * Erstellt tägliche Probetermine
     * 
     * @throws StdException
     * @param _monthStart
     *            Anfangsmonat
     * @param _yearStart
     *            Anfangsjahr
     * @param _monthEnde
     *            Endmonat
     * @param _yearEnde
     *            Endjahr
     */
    public void createProbetermineTaeglich(int _monthStart, int _yearStart,
                    int _monthEnde, int _yearEnde) throws StdException
    {
        UnixTime[] arrTimestamps;
        DateBean dateStart = new DateBean();
        DateBean dateEnde = new DateBean();

        // Zeit & Datum setzen
        dateStart.setTime(req.getStringForParam("textZeitStart"));
        dateEnde.setTime(req.getStringForParam("textZeitEnde"));

        int tag;
        List<String> tage = getTageInWoche();

        for (int i = 0, m = tage.size(); i < m; i++)
        {
            tag = Integer.valueOf(tage.get(i));
            arrTimestamps = UnixTime.generiereTaeglicheTermine(_yearStart,
                            _monthStart, dateStart.getHour(),
                            dateStart.getMinute(), _yearEnde, _monthEnde,
                            dateEnde.getHour(), dateEnde.getMinute(), tag);
            createProbeTermineByArray(arrTimestamps, req.getUserBean()
                            .getCtxOE().getOrganisationsEinheitId());
        }
    }

    /**
     * Erstellt wöchentliche Probetermine
     * 
     * @throws StdException
     * @param _monthStart
     *            Anfangsmonat
     * @param _yearStart
     *            Anfangsjahr
     * @param _monthEnde
     *            Endmonat
     * @param _yearEnde
     *            Endjahr
     */
    public void createProbetermineWoechentlich(int _monthStart, int _yearStart,
                    int _monthEnde, int _yearEnde) throws StdException
    {
        DateBean dateStart = new DateBean();
        DateBean dateEnde = new DateBean();

        // Zeit & Datum setzen
        dateStart.setTime(req.getStringForParam("textZeitStart"));
        dateEnde.setTime(req.getStringForParam("textZeitEnde"));

        int tag;
        int woche;

        UnixTime[] arrTimestamps;
        List<String> tage = getTageInWoche();
        List<String> wocheInMonat = getWochenImMonat();

        for (int i = 0, m = tage.size(); i < m; i++)
        {
            for (int j = 0, n = wocheInMonat.size(); j < n; j++)
            {
                tag = Integer.valueOf(tage.get(i));
                woche = Integer.valueOf(wocheInMonat.get(j));

                arrTimestamps = UnixTime.generiereWoechentlicheTermine(
                                _yearStart, _monthStart, dateStart.getHour(),
                                dateStart.getMinute(), _yearEnde, _monthEnde,
                                dateEnde.getHour(), dateEnde.getMinute(), tag,
                                woche);

                createProbeTermineByArray(arrTimestamps, req.getUserBean()
                                .getCtxOE().getOrganisationsEinheitId());
            }
        }
    }

    /**
     * Erstellt monatliche Probetermine
     * 
     * @throws StdException
     * @param _monthStart
     *            Anfangsmonat
     * @param _yearStart
     *            Anfangsjahr
     * @param _monthEnde
     *            Endmonat
     * @param _yearEnde
     *            Endjahr
     */
    public void createProbetermineMonatlich(int _monthStart, int _yearStart,
                    int _monthEnde, int _yearEnde) throws StdException
    {
        DateBean dateStart = new DateBean();
        DateBean dateEnde = new DateBean();

        // Zeit & Datum setzen
        dateStart.setTime(req.getStringForParam("textZeitStart"));
        dateEnde.setTime(req.getStringForParam("textZeitEnde"));

        int tag = Integer.valueOf(req.getIntForParam("textDatum"));
        UnixTime[] arrTimestamps;

        arrTimestamps = UnixTime.generiereMonatlicheTermine(_yearStart,
                        _monthStart, dateStart.getHour(),
                        dateStart.getMinute(), _yearEnde, _monthEnde,
                        dateEnde.getHour(), dateEnde.getMinute(), tag);
        createProbeTermineByArray(arrTimestamps, req.getUserBean().getCtxOE()
                        .getOrganisationsEinheitId());
    }

    /**
     * Erstellt die eigentlichen Probetermine
     * 
     * @param _termine
     * @param _organisationseinheitid
     * @author ckl
     */
    private void createProbeTermineByArray(UnixTime[] _termine,
                    OrganisationsEinheitId _organisationseinheitid)
    {
        ProbeTerminVO probeterminVO;

        for (int i = 0, m = _termine.length; i < m; i = i + 2)
        {
            try
            {
                probeterminVO = taoProbeTermin.getDBResource()
                                .getObjectFactory().createProbeTermin();
                probeterminVO.setStart(_termine[i]);
                probeterminVO.setEnde(_termine[(i + 1)]);
                probeterminVO.setOrganisationsEinheitId(_organisationseinheitid);

                taoProbeTermin.createProbeTermin(probeterminVO);
            }
            catch (StdException e)
            {
                log.error(e);
            }
        }
    }
}
