package de.ecw.zabos.broadcast.sms;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import de.ecw.zabos.alarm.RueckmeldeStatistik;
import de.ecw.zabos.broadcast.sms.SmsContent;
import de.ecw.zabos.sql.vo.AlarmVO;
import de.ecw.zabos.sql.vo.FunktionstraegerVO;
import de.ecw.zabos.sql.vo.SchleifeVO;

public class SmsContentTest
{
    private ReloadableResourceBundleMessageSource messageSource;

    @Before
    public void setUp()
    {
        messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("smscontent");
    }

    @Test
    public void resolve()
    {
        SmsContent test = new SmsContent(messageSource);
        String msg = test.resolve(SmsContent.ABWESENHEIT_AUFGEHOBEN, null);
        assertTrue(msg.length() > 10);
        msg = test.resolve(SmsContent.ABWESENHEITSZEIT_GESETZT, null);
        assertTrue(msg.length() > 10);
        msg = test.resolve(SmsContent.ABWESENHEIT_AUFGEHOBEN, null);
        assertTrue(msg.length() > 10);
        msg = test.resolve(SmsContent.SYSTEM_AKTIVIERT, null);
        assertTrue(msg.length() > 10);
        msg = test.resolve(SmsContent.SYSTEM_BEREITS_AKTIV, null);
        assertTrue(msg.length() > 10);
        msg = test.resolve(SmsContent.SYSTEM_BEREITS_INAKTIV, null);
        assertTrue(msg.length() > 10);
        msg = test.resolve(SmsContent.SYSTEM_DEAKTIVIERT, null);
        assertTrue(msg.length() > 10);
        msg = test.resolve(SmsContent.SYSTEM_UNBEKANNT, null);
        assertTrue(msg.length() > 10);

        int reihenfolge = 0x666;
        AlarmVO alarm = mock(AlarmVO.class);
        Map<String, Object> args = new HashMap<String, Object>();
        args.put(SmsContent.PARAM_ALARM, alarm);

        when(alarm.getReihenfolge()).thenReturn(reihenfolge);
        msg = test.resolve(SmsContent.ALARM_ENTWARNUNG, args);
        System.out.println(msg);
        assertTrue(msg.length() > 0);
        assertTrue(msg.contains("666"));
    }

    @Test
    public void resolveAusloesung()
    {
        SmsContent test = new SmsContent(messageSource);
        int reihenfolge = 0x666;
        AlarmVO alarm = mock(AlarmVO.class);
        SchleifeVO schleife = mock(SchleifeVO.class);
        when(alarm.getReihenfolge()).thenReturn(reihenfolge);
        when(schleife.getName()).thenReturn("SCHLEIFE_NAME");
        when(schleife.getKuerzel()).thenReturn("SCHLEIFE_KUERZEL");
        String ausgeloest = "R_ausgeloest";
        String unbekannt = "R_unbekannt";
        String keineBerechtigung = "R_keineBerechtigung";
        String bereitsAusgeloest = "R_bereitsAusgeloest";

        String msg = test.resolveAusloesung(alarm, ausgeloest, unbekannt,
                        keineBerechtigung, bereitsAusgeloest, null);
        System.out.println(msg);
        assertTrue(msg.contains("666"));
        assertTrue(msg.contains(ausgeloest));
        assertTrue(msg.contains(unbekannt));
        assertTrue(msg.contains(keineBerechtigung));
        assertTrue(msg.contains(bereitsAusgeloest));

        msg = test.resolveAusloesung(alarm, ausgeloest, unbekannt,
                        keineBerechtigung, null, null);
        System.out.println(msg);
        assertFalse(msg.contains(bereitsAusgeloest));

    }

    @Test
    public void resolveRueckmeldeStatistik()
    {
        SmsContent test = new SmsContent(messageSource);
        int reihenfolge = 0x666;
        AlarmVO alarm = mock(AlarmVO.class);
        SchleifeVO schleife = mock(SchleifeVO.class);
        RueckmeldeStatistik rueckmeldeStatistik = mock(RueckmeldeStatistik.class);
        when(rueckmeldeStatistik.getNumJa()).thenReturn(10);
        when(rueckmeldeStatistik.getNumNein()).thenReturn(20);
        when(rueckmeldeStatistik.getNumSpaeter()).thenReturn(30);
        when(rueckmeldeStatistik.getNumUnbekannt()).thenReturn(40);
        when(alarm.getReihenfolge()).thenReturn(reihenfolge);
        when(schleife.getName()).thenReturn("SCHLEIFE_NAME");
        when(schleife.getKuerzel()).thenReturn("SCHLEIFE_KUERZEL");

        String msg = test.resolveRueckmeldeStatistik(
                        SmsContent.RUECKMELDUNG_REPORT, alarm, schleife,
                        rueckmeldeStatistik, null);
        assertFalse(msg.contains(SmsContent.FAIL));
        System.out.println(msg);
        msg = test.resolveRueckmeldeStatistik(
                        SmsContent.RUECKMELDUNG_REPORT_KLINIKUM, alarm,
                        schleife, rueckmeldeStatistik, null);
        assertFalse(msg.contains(SmsContent.FAIL));
        System.out.println(msg);

        // Test für Funktionsträger-Statistik
        FunktionstraegerVO funktionstraeger = mock(FunktionstraegerVO.class);
        when(funktionstraeger.getKuerzel()).thenReturn("FUNKTIONSTRAEGER");

        Map<String, Object> mapFunktionstraegerStatistik = new HashMap<String, Object>();
        mapFunktionstraegerStatistik.put(SmsContent.PARAM_FUNKTIONSTRAEGER,
                        funktionstraeger);
        mapFunktionstraegerStatistik.put(SmsContent.PARAM_RUECKMELDESTATISTIK,
                        rueckmeldeStatistik);
        String funktionstraegerDetails = test.resolve(
                        SmsContent.FUNKTIONSTRAEGER_STATISTIK,
                        mapFunktionstraegerStatistik);

        mapFunktionstraegerStatistik.put(
                        SmsContent.PARAM_FUNKTIONSTRAEGER_DETAILS,
                        funktionstraegerDetails);
        msg = test.resolveRueckmeldeStatistik(
                        SmsContent.RUECKMELDUNG_FUNKTIONSTRAEGER_REPORT, alarm,
                        schleife, rueckmeldeStatistik,
                        mapFunktionstraegerStatistik);
        System.out.println(msg);
        assertFalse(msg.contains(SmsContent.FAIL));

    }

    @Test
    public void resolveBestMatchingContentForAlarm()
    {
        SmsContent test = new SmsContent(messageSource);
        int reihenfolge = 0x666;
        AlarmVO alarm = mock(AlarmVO.class);
        SchleifeVO schleife = mock(SchleifeVO.class);
        when(alarm.getReihenfolge()).thenReturn(reihenfolge);
        when(schleife.getName()).thenReturn("SCHLEIFE_NAME");
        when(schleife.getKuerzel()).thenReturn("SCHLEIFE_KUERZEL");

        String msg = test.resolveBestMatchingContentForAlarm(alarm, schleife,
                        "", "");
        System.out.println(msg);
        assertFalse(msg.contains("Hinweis:"));
        msg = test.resolveBestMatchingContentForAlarm(alarm, schleife, "H", "");
        System.out.println(msg);
        assertTrue(msg.contains("Hinweis:"));
        msg = test.resolveBestMatchingContentForAlarm(alarm, schleife, "", "G");
        System.out.println(msg);
        assertTrue(msg.contains("GPS:"));
        msg = test.resolveBestMatchingContentForAlarm(alarm, schleife, "H", "G");
        System.out.println(msg);
        assertTrue(msg.contains("Hinweis:"));
        assertTrue(msg.contains("GPS:"));
    }
}
