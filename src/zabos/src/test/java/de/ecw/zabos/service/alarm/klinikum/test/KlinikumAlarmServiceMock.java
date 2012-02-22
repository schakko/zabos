package de.ecw.zabos.service.alarm.klinikum.test;

import java.util.ArrayList;

import de.ecw.zabos.alarm.RueckmeldeStatistik;
import de.ecw.zabos.broadcast.sms.SmsContent;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.service.alarm.klinikum.FunktionstraegerBereichRueckmeldung;
import de.ecw.zabos.service.alarm.klinikum.KlinikumAlarmService;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.vo.AlarmVO;
import de.ecw.zabos.sql.vo.BereichInSchleifeVO;
import de.ecw.zabos.sql.vo.PersonInAlarmVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.sql.vo.SystemKonfigurationVO;
import de.ecw.zabos.types.UnixTime;

public class KlinikumAlarmServiceMock extends KlinikumAlarmService
{
    public KlinikumAlarmServiceMock(DBResource _dbresource,
                    SystemKonfigurationVO _systemKonfiguration, SmsContent _smsContent)
    {
        super(_dbresource, _systemKonfiguration, _smsContent);
    }

    public void alarmStatistik(AlarmVO _alarmVO, PersonInAlarmVO[] _piaVOs,
                    RueckmeldeStatistik _ret_gesamt)
    {
        super.alarmStatistik(_alarmVO, _piaVOs, _ret_gesamt);
    }

    public void bereichInAlarmDeaktivieren(
                    BereichInSchleifeVO _bereichInSchleifeVO, AlarmVO _alarmVO,
                    FunktionstraegerBereichRueckmeldung _statistik) throws StdException
    {
        super.bereichInAlarmDeaktivieren(_bereichInSchleifeVO, _alarmVO,
                        _statistik);
    }

    public STATUS_BEREICH_NACHALARMIEREN bereichNachalarmieren(
                    AlarmVO _alarmVO, SchleifeVO _originalSchleife,
                    BereichInSchleifeVO _bereichInSchleifeVO,
                    FunktionstraegerBereichRueckmeldung _statistik) throws StdException
    {
        return super.bereichNachalarmieren(_alarmVO, _originalSchleife,
                        _bereichInSchleifeVO, _statistik);
    }

    public int funktionstraegerBereichAlarmieren(SchleifeVO _schleifeVO,
                    BereichInSchleifeVO _bereichInSchleifeVO, AlarmVO _alarmVO,
                    String _zusatzText, UnixTime _alarmZeit, String _kontextO,
                    String _kontextOE) throws StdException
    {
        return super.funktionstraegerBereichAlarmieren(_schleifeVO,
                        _bereichInSchleifeVO, _alarmVO, _zusatzText,
                        _alarmZeit, _kontextO, _kontextOE);
    }

    public int personenAlarmieren(SchleifeVO _schleifeVO, AlarmVO _alarmVO,
                    String _zusatzText, UnixTime _alarmZeit, String _kontextO,
                    String _kontextOE, PersonVO[] personen) throws StdException
    {
        return super.personenAlarmieren(_schleifeVO, _alarmVO, _zusatzText,
                        _alarmZeit, _kontextO, _kontextOE, personen);
    }

    public void personenEntwarnen(AlarmVO _alarmVO, PersonInAlarmVO[] _piaVOs) throws StdException
    {
        super.personenEntwarnen(_alarmVO, _piaVOs);
    }

    public ArrayList<Long> enableTimeout = new ArrayList<Long>();

    public boolean isTimeoutFuerBereichErreicht(
                    BereichInSchleifeVO _bereichInSchleifeVO, AlarmVO _alarmVO) throws StdException
    {
        return enableTimeout.contains(_bereichInSchleifeVO.getBereichInSchleifeId().getLongValue());
    }

}
