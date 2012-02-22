package de.ecw.zabos.frontend.controllers.klinikum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.ecw.zabos.bo.SchleifeBO;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.Message;
import de.ecw.zabos.frontend.Parameters;
import de.ecw.zabos.frontend.beans.DataBean;
import de.ecw.zabos.frontend.controllers.helpers.BereichInSchleifeFinder;
import de.ecw.zabos.frontend.objects.fassade.klinikum.SchleifenFassade;
import de.ecw.zabos.frontend.ressources.RequestResources;
import de.ecw.zabos.service.alarm.IAlarmService;
import de.ecw.zabos.service.alarm.klinikum.KlinikumAlarmService;
import de.ecw.zabos.sql.dao.BereichDAO;
import de.ecw.zabos.sql.dao.BereichInSchleifeDAO;
import de.ecw.zabos.sql.dao.FunktionstraegerDAO;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.BereichInSchleifeVO;
import de.ecw.zabos.sql.vo.BereichVO;
import de.ecw.zabos.sql.vo.FunktionstraegerVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.types.id.BereichId;
import de.ecw.zabos.types.id.BereichInSchleifeId;
import de.ecw.zabos.types.id.FunktionstraegerId;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.types.id.SchleifeId;

/**
 * Erweitert den Standard-SchleifenController um die Möglichkeit des Zuweisens
 * von Bereichen bzw. Funktionsträgern
 * 
 * @author ckl
 * 
 */
public class SchleifeController extends
                de.ecw.zabos.frontend.controllers.SchleifeController
{
    public final static String DO_UPDATE_BEREICH_FUNKTIONSTRAEGER = "doUpdateBereichFunktionstraeger";

    private IAlarmService alarmService;

    protected BereichDAO daoBereich;

    protected FunktionstraegerDAO daoFunktionstraeger;

    protected BereichInSchleifeDAO daoBereichInSchleife;

    private BereichInSchleifeFinder bereichInSchleifeFinder;

    protected SchleifeBO boSchleife;

    public SchleifeController(final DBResource _dbResource,
                    BereichInSchleifeFinder _bisFinder)
    {
        super(_dbResource);
        setBereichInSchleifeFinder(_bisFinder);

        daoBereich = _dbResource.getDaoFactory().getBereichDAO();
        daoFunktionstraeger = _dbResource.getDaoFactory()
                        .getFunktionstraegerDAO();
        daoBereichInSchleife = _dbResource.getDaoFactory()
                        .getBereichInSchleifeDAO();
        boSchleife = _dbResource.getBoFactory().getSchleifeBO();

        // Defaults für das Klinikum
        setRueckmeldeintervallAktiv(true);
        setFolgeschleifeAktiv(true);
        setDruckerKuerzelAktiv(true);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.controllers.SchleifeController#run(de.ecw.zabos
     * .frontend.ressources.RequestResources)
     */
    public void run(RequestResources req)
    {
        if (true == req.isValidSubmit())
        {
            if (req.getRequestDo().equals(DO_UPDATE_BEREICH_FUNKTIONSTRAEGER))
            {
                doUpdateBereichFunktionstraeger(req);
            }
            else
            {
                super.run(req);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.controllers.SchleifeController#setViewData(de.ecw
     * .zabos.frontend.ressources.RequestResources)
     */
    public void setViewData(RequestResources req)
    {
        super.setViewData(req);

        DataBean beanFunktionstraegerAvailable = new DataBean();
        DataBean beanBereicheAvailable = new DataBean();
        DataBean beanBereichInSchleifeAvailable = new DataBean();
        DataBean beanAlarmierendePersonenAvailable = new DataBean();

        // Überschrieben, da die Zuordnung von Bereichen/Funktionsträgern
        // übergeben werden muss
        if (req.getId(Parameters.SCHLEIFE_ID) > 0)
        {
            try
            {
                SchleifeId schleifeId = new SchleifeId(
                                req.getId(Parameters.SCHLEIFE_ID));
                SchleifeVO schleifeVO = daoSchleife
                                .findSchleifeById(schleifeId);

                boolean bPersonenLaden = (getAlarmService() != null)
                                && (getAlarmService() instanceof KlinikumAlarmService);

                SchleifenFassade schleifenFassade = getBereichInSchleifeFinder()
                                .createSchleifenFassade(schleifeVO,
                                                bPersonenLaden);

                FunktionstraegerVO[] funktionstraegerVOs = daoFunktionstraeger
                                .findAll();
                BereichVO[] bereichVOs = daoBereich.findAll();

                beanFunktionstraegerAvailable.setData(funktionstraegerVOs);
                beanBereicheAvailable.setData(bereichVOs);

                beanBereichInSchleifeAvailable.setData(schleifenFassade
                                .getBereichInSchleifeFassade());
                beanAlarmierendePersonenAvailable.setData(schleifenFassade
                                .getBereichInSchleifeMitPersonenFassade());
            }
            catch (StdException e)
            {
                req.getErrorBean().addMessage(e);
            }
        }

        req.setData(Parameters.ARR_FUNKTIONSTRAEGER_AVAILABLE,
                        beanFunktionstraegerAvailable)
                        .setData(Parameters.ARR_BEREICHE_AVAILABLE,
                                        beanBereicheAvailable)
                        .setData(Parameters.ARR_BEREICH_IN_SCHLEIFE_AVAILABLE,
                                        beanBereichInSchleifeAvailable)
                        .setData(Parameters.ARR_BEREICH_IN_SCHLEIFE_MIT_PERSONEN_AVAILABLE,
                                        beanAlarmierendePersonenAvailable);
    }

    /**
     * Ändert die Zuordnung der einzelnen Bereiche/Funktionsträger innerhalb
     * einer Schleife
     * 
     * @param _req
     */
    protected void doUpdateBereichFunktionstraeger(RequestResources req)
    {
        if (req.getId(Parameters.SCHLEIFE_ID) == 0)
        {
            req.getErrorBean()
                            .addMessage(new StdException(
                                            "Sie müssen eine Schleife auswählen"));
            return;
        }

        if (!req.isActionAllowed(
                        RechtId.SCHLEIFE_AENDERN,
                        "Sie besitzen nicht das Recht, Bereich/Funktionsträger-Zuordnungen dieser Schleife zu ändern."))
        {
            return;
        }

        SchleifeId schleifeId = new SchleifeId(
                        req.getId(Parameters.SCHLEIFE_ID));

        try
        {
            BereichInSchleifeVO[] bisVOs = daoBereichInSchleife
                            .findBereicheInSchleifeBySchleifeId(schleifeId);

            Map<String, BereichInSchleifeVO> mapZuLoeschendeBereicheInSchleife = new HashMap<String, BereichInSchleifeVO>();

            if (bisVOs != null)
            {
                for (int i = 0, m = bisVOs.length; i < m; i++)
                {
                    BereichInSchleifeVO bis = bisVOs[i];
                    mapZuLoeschendeBereicheInSchleife.put(buildUid(bis), bis);
                }
            }

            long[] funktionstraegerIds = req
                            .getLongArrayForParam(Parameters.SELECT_FOLGESCHLEIFE
                                            + "[]");
            long[] bereichIds = req
                            .getLongArrayForParam(Parameters.SELECT_BEREICH
                                            + "[]");

            long[] bereichInSchleifeIds = req
                            .getLongArrayForParam(Parameters.ARR_BEREICH_IN_SCHLEIFE_AVAILABLE
                                            + "[]");

            String[] sSollstaerke = req
                            .getStringArrayForParam(Parameters.TEXT_SOLLSTAERKE
                                            + "[]");

            if (bereichInSchleifeIds != null && funktionstraegerIds != null
                            && bereichIds != null && sSollstaerke != null)
            {
                if ((funktionstraegerIds.length != bereichIds.length)
                                || (funktionstraegerIds.length != sSollstaerke.length)
                                || (bereichInSchleifeIds.length != sSollstaerke.length))
                {
                    throw new StdException(
                                    "Die Anzahl der übergebenen Bereiche, Funktionsträger und Sollstärken stimmt nicht überein");
                }

                ArrayList<String> alFunktionstraegerBereichZuordnung = new ArrayList<String>();
                int iDoppelteZuweisung = 0;

                for (int i = 0, m = bereichInSchleifeIds.length; i < m; i++)
                {
                    FunktionstraegerId fId = new FunktionstraegerId(
                                    funktionstraegerIds[i]);
                    BereichId bId = new BereichId(bereichIds[i]);
                    BereichInSchleifeId bisId = new BereichInSchleifeId(
                                    bereichInSchleifeIds[i]);
                    int sollstaerke = 0;

                    if (alFunktionstraegerBereichZuordnung.contains(fId
                                    .getLongValue() + "_" + bId.getLongValue()))
                    {
                        iDoppelteZuweisung++;
                        continue;
                    }

                    try
                    {
                        sollstaerke = Integer.valueOf(sSollstaerke[i]);
                    }
                    catch (NumberFormatException e)
                    {
                        // Fehler wird nicht geschmissen
                    }

                    BereichInSchleifeVO bisVO = daoBereichInSchleife
                                    .getObjectFactory()
                                    .createBereichInSchleife();
                    bisVO.setSchleifeId(schleifeId);
                    bisVO.setFunktionstraegerId(fId);
                    bisVO.setBereichId(bId);
                    bisVO.setSollstaerke(sollstaerke);

                    if (bisId != null && (bisId.getLongValue() > 0))
                    {
                        bisVO.setBereichInSchleifeId(bisId);
                        BereichInSchleifeVO bisInDatenbankVO = mapZuLoeschendeBereicheInSchleife
                                        .get(buildUid(bisVO));

                        // Wenn Funktionsträger & Bereich identisch sind, soll
                        // der Eintrag geupdatet werden.
                        // Ansonsten muss der Datenbank-Eintrag gelöscht werden
                        // und ein zusätzlicher Eintrag erstellt werden.

                        if (bisInDatenbankVO != null)
                        {
                            // Funktionsträger/Bereich sind nicht identisch =>
                            // Neuen Eintrag hinzufügen; bestehender
                            // Datenbankeintrag muss als gelöscht markiert
                            // werden
                            if ((bisInDatenbankVO.getBereichId().getLongValue() != bId
                                            .getLongValue())
                                            || (bisInDatenbankVO
                                                            .getFunktionstraegerId()
                                                            .getLongValue() != fId
                                                            .getLongValue()))
                            {
                                bisVO.setBereichInSchleifeId(new BereichInSchleifeId(
                                                0));
                            }
                            // F/B sind identisch. Dementsprechend muss der
                            // Eintrag am Ende nicht gelöscht werden
                            else
                            {
                                mapZuLoeschendeBereicheInSchleife
                                                .remove(buildUid(bisVO));
                            }
                        }
                    }

                    alFunktionstraegerBereichZuordnung.add(fId.getLongValue()
                                    + "_" + bId.getLongValue());

                    // ID ist gegeben, also Bereich updaten
                    if (bisVO.getBereichInSchleifeId() != null
                                    && bisVO.getBereichInSchleifeId()
                                                    .getLongValue() > 0)
                    {
                        taoBV.updateBereichInSchleife(bisVO);
                    }
                    // Bereich erstellen
                    else
                    {
                        taoBV.createBereichInSchleife(bisVO);
                    }
                } // for bereichInSchleife

                // Warnung ausgeben, falls doppelte Zuweisungen gemacht wurden
                // sind
                if (iDoppelteZuweisung > 0)
                {
                    req.getInfoBean()
                                    .addMessage(new Message(
                                                    "Es wurde(n) "
                                                                    + iDoppelteZuweisung
                                                                    + " Mehrfach-Zuweisung(en) von Ihnen vorgenommen. Nur die erste Zuweisung wurde gespeichert, da es immer nur eine Zuweisung zwischen Funktionsträger und Bereich geben kann."));
                }
                // Noch in der Datenbank bestehende Zuweisungen löschen
                Iterator<String> itZuLoeschendeBereiche = mapZuLoeschendeBereicheInSchleife
                                .keySet().iterator();

                while (itZuLoeschendeBereiche.hasNext())
                {
                    String key = itZuLoeschendeBereiche.next();
                    BereichInSchleifeVO bis = mapZuLoeschendeBereicheInSchleife
                                    .get(key);

                    if (bis != null)
                    {
                        taoBV.deleteBereichInSchleife(bis
                                        .getBereichInSchleifeId());
                    }
                }
            }
        }
        catch (StdException e)
        {
            req.getErrorBean().addMessage(e);
        }
    }

    /**
     * Erstellt eine UID
     * 
     * @param _bis
     * @return
     */
    protected String buildUid(BereichInSchleifeVO _bis)
    {
        return "" + _bis.getBereichInSchleifeId().getLongValue();
    }

    public void setAlarmService(IAlarmService alarmService)
    {
        this.alarmService = alarmService;
    }

    public IAlarmService getAlarmService()
    {
        return alarmService;
    }

    public void setBereichInSchleifeFinder(
                    BereichInSchleifeFinder bereichInSchleifeFinder)
    {
        this.bereichInSchleifeFinder = bereichInSchleifeFinder;
    }

    public BereichInSchleifeFinder getBereichInSchleifeFinder()
    {
        return bereichInSchleifeFinder;
    }
}
