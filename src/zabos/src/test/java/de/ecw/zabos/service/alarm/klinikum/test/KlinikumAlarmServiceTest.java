package de.ecw.zabos.service.alarm.klinikum.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import de.ecw.zabos.SpringContext;
import de.ecw.zabos.alarm.ZwischenStatistik;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.service.alarm.klinikum.FunktionstraegerBereichRueckmeldung;
import de.ecw.zabos.service.alarm.klinikum.KlinikumAlarmService;
import de.ecw.zabos.sql.dao.AlarmDAO;
import de.ecw.zabos.sql.dao.DAOFactory;
import de.ecw.zabos.sql.dao.StatistikDAO;
import de.ecw.zabos.sql.vo.AlarmVO;
import de.ecw.zabos.sql.vo.BereichInSchleifeVO;
import de.ecw.zabos.sql.vo.BereichReportStatistikVO;
import de.ecw.zabos.sql.vo.BereichVO;
import de.ecw.zabos.sql.vo.FunktionstraegerVO;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.PersonInAlarmVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.RolleVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.sql.vo.TelefonVO;
import de.ecw.zabos.test.ZabosTestAdapter;
import de.ecw.zabos.types.TelefonNummer;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.AlarmQuelleId;
import de.ecw.zabos.types.id.BereichId;
import de.ecw.zabos.types.id.FunktionstraegerId;
import de.ecw.zabos.types.id.PersonId;
import de.ecw.zabos.types.id.RechtId;
import de.ecw.zabos.types.id.RueckmeldungStatusId;
import de.ecw.zabos.types.id.SchleifeId;

public class KlinikumAlarmServiceTest extends ZabosTestAdapter
{
    public final static int SOLLSTAERKE = 40;

    public final static int SOLLSTAERKE_BIS3 = 10;

    public final static int SOLLSTAERKE_BIS4 = 20;

    private static AlarmDAO alarmDAO;

    private final static Logger log = Logger
                    .getLogger(KlinikumAlarmServiceTest.class);

    private static KlinikumAlarmServiceMock klinikumAlarmService;

    private static OrganisationVO o;

    private static OrganisationsEinheitVO oe;

    private static SchleifeVO schleifeOhneFolgebereich;

    private static SchleifeVO schleifeOhneFolgebereich2;

    /**
     * Hauptschleife
     */
    private static SchleifeVO s1;

    /**
     * Hauptschleife
     */
    private static SchleifeVO s2;

    /**
     * Folgeschleife von {{@link #s1}
     */
    private static SchleifeVO s3;

    /**
     * Folgeschleife von {{@link #s3}
     */
    private static SchleifeVO s4;

    /**
     * Funktionsträger
     */
    private static FunktionstraegerVO f;

    /**
     * Bereich
     */
    private static BereichVO b;

    private static RolleVO ro;

    /**
     * Bereich in Schleife von {@link #s1}
     */
    private static BereichInSchleifeVO bis;

    /**
     * Bereich in Schleife von {@link #s3}
     */
    private static BereichInSchleifeVO bis3;

    /**
     * Bereich in Schleife von {@link #s4}
     */
    private static BereichInSchleifeVO bis4;

    private static BereichInSchleifeVO bisOhneFolgebereich;

    /**
     * Die Person besitzt das Recht Alarm-Reporte zu empfangen
     */
    private static PersonVO p;

    private static TelefonVO t;

    private static boolean isInitialized = false;

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {
            isInitialized = true;

            klinikumAlarmService = (KlinikumAlarmServiceMock) SpringContext
                            .getInstance().getBean(
                                            SpringContext.BEAN_ALARM_SERVICE,
                                            KlinikumAlarmServiceMock.class);
            alarmDAO = klinikumAlarmService.getDBResource().getDaoFactory()
                            .getAlarmDAO();

            createEnvironment();
        }
    }

    private static void createEnvironment()
    {
        try
        {
            o = daoFactory.getObjectFactory().createOrganisation();
            o.setBeschreibung("O");
            o.setName("O");
            o = taoBV.createOrganisation(o);

            oe = daoFactory.getObjectFactory().createOrganisationsEinheit();
            oe.setBeschreibung("OE");
            oe.setName("OE");
            oe.setOrganisationId(o.getOrganisationId());
            oe = taoBV.createOrganisationseinheit(oe);

            schleifeOhneFolgebereich2 = daoFactory.getObjectFactory()
                            .createSchleife();
            schleifeOhneFolgebereich2.setOrganisationsEinheitId(oe
                            .getOrganisationsEinheitId());
            schleifeOhneFolgebereich2.setBeschreibung("Ohne Folgebereich2");
            schleifeOhneFolgebereich2.setName("Ohne Folgebereich2");
            schleifeOhneFolgebereich2.setKuerzel("SOF2");
            schleifeOhneFolgebereich2.setFuenfton("5");
            schleifeOhneFolgebereich2 = taoBV
                            .createSchleife(schleifeOhneFolgebereich2);

            schleifeOhneFolgebereich = daoFactory.getObjectFactory()
                            .createSchleife();
            schleifeOhneFolgebereich.setOrganisationsEinheitId(oe
                            .getOrganisationsEinheitId());
            schleifeOhneFolgebereich.setBeschreibung("Ohne Folgebereich");
            schleifeOhneFolgebereich.setName("Ohne Folgebereich");
            schleifeOhneFolgebereich.setKuerzel("SOF");
            schleifeOhneFolgebereich.setFuenfton("5");
            schleifeOhneFolgebereich
                            .setFolgeschleifeId(schleifeOhneFolgebereich2
                                            .getSchleifeId());
            schleifeOhneFolgebereich = taoBV
                            .createSchleife(schleifeOhneFolgebereich);

            s4 = daoFactory.getObjectFactory().createSchleife();
            s4.setOrganisationsEinheitId(oe.getOrganisationsEinheitId());
            s4.setBeschreibung("S4");
            s4.setName("S4");
            s4.setKuerzel("S4");
            s4.setFuenfton("4");
            s4 = taoBV.createSchleife(s4);

            s3 = daoFactory.getObjectFactory().createSchleife();
            s3.setOrganisationsEinheitId(oe.getOrganisationsEinheitId());
            s3.setBeschreibung("S3");
            s3.setName("S3");
            s3.setKuerzel("S3");
            s3.setFuenfton("3");
            s3.setFolgeschleifeId(s4.getSchleifeId());
            s3 = taoBV.createSchleife(s3);

            s2 = daoFactory.getObjectFactory().createSchleife();
            s2.setOrganisationsEinheitId(oe.getOrganisationsEinheitId());
            s2.setBeschreibung("S2");
            s2.setKuerzel("24");
            s2.setName("S2");
            s2.setFuenfton("2");
            s2 = taoBV.createSchleife(s2);

            s1 = daoFactory.getObjectFactory().createSchleife();
            s1.setOrganisationsEinheitId(oe.getOrganisationsEinheitId());
            s1.setBeschreibung("S1");
            s1.setKuerzel("S1");
            s1.setName("S1");
            s1.setFuenfton("1");
            s1.setFolgeschleifeId(s3.getSchleifeId());
            // 2 Sekunden
            s1.setRueckmeldeintervall(2);
            s1 = taoBV.createSchleife(s1);

            f = daoFactory.getObjectFactory().createFunktionstraeger();
            f.setBeschreibung("F");
            f.setKuerzel("F");
            f = taoBV.createFunktionstraeger(f);

            b = daoFactory.getObjectFactory().createBereich();
            b.setBeschreibung("B");
            b.setName("B");
            b = taoBV.createBereich(b);

            bis = daoFactory.getObjectFactory().createBereichInSchleife();
            bis.setBereichId(b.getBereichId());
            bis.setFunktionstraegerId(f.getFunktionstraegerId());
            bis.setSchleifeId(s1.getSchleifeId());
            bis.setSollstaerke(SOLLSTAERKE);
            bis = taoBV.createBereichInSchleife(bis);

            bis3 = daoFactory.getObjectFactory().createBereichInSchleife();
            bis3.setBereichId(b.getBereichId());
            bis3.setFunktionstraegerId(f.getFunktionstraegerId());
            bis3.setSchleifeId(s3.getSchleifeId());
            bis3.setSollstaerke(SOLLSTAERKE_BIS3);
            bis3 = taoBV.createBereichInSchleife(bis3);

            bis4 = daoFactory.getObjectFactory().createBereichInSchleife();
            bis4.setBereichId(b.getBereichId());
            bis4.setFunktionstraegerId(f.getFunktionstraegerId());
            bis4.setSchleifeId(s4.getSchleifeId());
            bis4.setSollstaerke(SOLLSTAERKE_BIS4);
            bis4 = taoBV.createBereichInSchleife(bis4);

            bisOhneFolgebereich = daoFactory.getObjectFactory()
                            .createBereichInSchleife();
            bisOhneFolgebereich.setBereichId(b.getBereichId());
            bisOhneFolgebereich
                            .setFunktionstraegerId(f.getFunktionstraegerId());
            bisOhneFolgebereich.setSchleifeId(schleifeOhneFolgebereich
                            .getSchleifeId());
            bisOhneFolgebereich.setSollstaerke(10);
            bisOhneFolgebereich = taoBV
                            .createBereichInSchleife(bisOhneFolgebereich);

            p = daoFactory.getObjectFactory().createPerson();
            p.setNachname("N");
            p.setVorname("V");
            p.setName("U");
            p = taoBV.createPerson(p);

            ro = daoFactory.getObjectFactory().createRolle();
            ro.setName("R");
            ro.setBeschreibung("R");
            ro = taoBV.createRolle(ro);

            t = daoFactory.getObjectFactory().createTelefon();
            t.setAktiv(true);
            t.setNummer(new TelefonNummer("004912313"));
            t.setPersonId(p.getPersonId());
            t = taoBV.createTelefon(t);

            dbResource.getTaoFactory()
                            .getRolleTAO()
                            .addRechtToRolle(
                                            RechtId.ALARMBENACHRICHTIGUNG_EMPFANGEN,
                                            ro.getRolleId());

            dbResource.getTaoFactory()
                            .getRechteTAO()
                            .addPersonInRolleToSystem(p.getPersonId(),
                                            ro.getRolleId());
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findAlarmierbareSchleife()
    {
        try
        {
            SchleifeVO s;

            /**
             * Schleife s1 ist in keinem Alarm, hat Folgeschleifen und lässt
             * sich somit alarmieren
             */
            s = klinikumAlarmService.findAlarmierbareSchleife(s1, null);
            assertEquals(s1, s);

            /**
             * Schleife s2 hat keine Folgeschleife und soll ausgeschlossen
             * werden => Rückgabewert muss null sein
             */
            ArrayList<SchleifeId> al = new ArrayList<SchleifeId>();
            al.add(s2.getSchleifeId());

            s = klinikumAlarmService.findAlarmierbareSchleife(s2, al);
            assertNull(s);

            /**
             * Schleife s2 hat keine Folgeschleife und ist in keinem Alarm
             */
            s = klinikumAlarmService.findAlarmierbareSchleife(s2, null);
            assertEquals(s2, s);

            /**
             * Schleife s1 und s3 sind ausgeschlossen, s4 muss alarmiert werden
             */
            al.clear();
            al.add(s1.getSchleifeId());
            al.add(s3.getSchleifeId());
            s = klinikumAlarmService.findAlarmierbareSchleife(s1, al);
            assertEquals(s4, s);

            /**
             * Schleife und Folgeschleife ist ausgeschlossen => null
             */
            al.add(s4.getSchleifeId());
            s = klinikumAlarmService.findAlarmierbareSchleife(s1, al);
            assertNull(s);

            /**
             * Schleife s1 ist aktiv, s3 muss alarmiert werden
             */

            AlarmVO a = createAlarm();

            // Schleife S1 hinzufügen
            klinikumAlarmService.begin();
            alarmDAO.addSchleifeToAlarm(s1.getSchleifeId(), a.getAlarmId());
            klinikumAlarmService.commit();
            s = klinikumAlarmService.findAlarmierbareSchleife(s1, null);
            assertEquals(s3, s);

            testTAO.cleanTestData();
            createEnvironment();
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void findAuszuloesendeSchleifen()
    {
        try
        {
            /*
             * Testfall: Beide Schleifen sind nicht ausgeloest und lassen sich
             * somit auch alarmieren
             */
            List<SchleifeVO> r = null;
            r = klinikumAlarmService
                            .findAuszuloesendeSchleifen(new SchleifeVO[]
                            { s1, s2 });

            assertEquals(false, (r == null));
            assertEquals(2, r.size());
            assertEquals(true, r.contains(s1));
            assertEquals(true, r.contains(s2));

            /*
             * Testfall: Schleife S1 ist bereits alarmiert, soll aber neu
             * alarmiert werden. Außerdem soll S3 alarmiert werden. Es kann
             * somit nur S3 als Folgeschleife von S1 alarmiert werden
             */
            klinikumAlarmService.begin();
            AlarmDAO alarmDAO = klinikumAlarmService.getDBResource()
                            .getDaoFactory().getAlarmDAO();

            AlarmVO a = daoFactory.getObjectFactory().createAlarm();
            a.setAlarmZeit(UnixTime.now());
            a.setAlarmQuelleId(AlarmQuelleId.ID_WEB);
            a = alarmDAO.createAlarm(a);
            assertEquals(false, (a == null));

            // Schleife S1 hinzufügen
            alarmDAO.addSchleifeToAlarm(s1.getSchleifeId(), a.getAlarmId());

            klinikumAlarmService.commit();

            r = klinikumAlarmService
                            .findAuszuloesendeSchleifen(new SchleifeVO[]
                            { s1, s3 });
            assertEquals(false, (r == null));
            assertEquals(1, r.size());
            assertEquals(true, r.contains(s3));
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void alarmAusloesen()
    {
        try
        {
            testTAO.cleanTestData();
            createEnvironment();

            AlarmVO alarm = klinikumAlarmService.alarmAusloesen("Zusatztext",
                            new SchleifeVO[]
                            { s1 }, AlarmQuelleId.ID_5TON, null, "", "");

            assertTrue((alarm.getAlarmId() != null));
            assertTrue(alarmDAO.isBereichInSchleifeInAlarmAktiv(
                            alarm.getAlarmId(), bis.getBereichInSchleifeId()));
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void bereichInAlarmDeaktivieren()
    {
        try
        {
            AlarmMock am = fbAlarmieren();

            FunktionstraegerBereichRueckmeldungMock mock = new FunktionstraegerBereichRueckmeldungMock(
                            taoBV, 100);

            FunktionstraegerBereichRueckmeldung fbr = FunktionstraegerBereichRueckmeldung
                            .buildPersonenRueckmeldung(mock.piaVOs, dbResource
                                            .getDaoFactory().getPersonDAO());

            for (int i = 0, m = mock.alBereiche.size(); i < m; i++)
            {
                BereichInSchleifeVO bis2 = daoFactory.getObjectFactory()
                                .createBereichInSchleife();
                bis2.setBereichId(mock.alBereiche.get(i).getBereichId());
                bis2.setFunktionstraegerId(mock.alFunktionstraeger.get(i)
                                .getFunktionstraegerId());

                klinikumAlarmService.bereichInAlarmDeaktivieren(bis2, am.alarm,
                                fbr);
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }

    }

    @Test
    public void istTimeoutFuerBereichErreicht()
    {
        // Alarm erstellen, Funktionsträger/Bereich alarmieren,
        // Rückmeldeintervall ist auf 2 Sekunden
        try
        {
            AlarmMock am = fbAlarmieren();
            assertFalse(klinikumAlarmService.isTimeoutFuerBereichErreicht(bis,
                            am.alarm));
            klinikumAlarmService.enableTimeout.add(bis.getBereichInSchleifeId()
                            .getLongValue());
            assertTrue(klinikumAlarmService.isTimeoutFuerBereichErreicht(bis,
                            am.alarm));
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void getPositiveRueckmeldungen()
    {
        try
        {
            testTAO.cleanTestData();
            createEnvironment();

            FunktionstraegerBereichRueckmeldungMock mock = new FunktionstraegerBereichRueckmeldungMock(
                            taoBV, 100);

            for (int i = 0, m = mock.alFunktionstraeger.size(); i < m; i++)
            {
                FunktionstraegerId fId = mock.alFunktionstraeger.get(i)
                                .getFunktionstraegerId();
                BereichId bId = mock.alBereiche.get(i).getBereichId();
                String id = bId.getLongValue() + "-" + fId.getLongValue();

                FunktionstraegerBereichRueckmeldung fbr = FunktionstraegerBereichRueckmeldung
                                .buildPersonenRueckmeldung(mock.piaVOs,
                                                dbResource.getDaoFactory()
                                                                .getPersonDAO());

                assertEquals(mock.hmStatPositiveRueckmeldung.get(id).intValue(),
                                klinikumAlarmService.getPositiveRueckmeldungen(
                                                bId, fId, fbr));
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void isSollstaerkeErreicht()
    {
        try
        {
            testTAO.cleanTestData();
            createEnvironment();

            FunktionstraegerBereichRueckmeldungMock mock = new FunktionstraegerBereichRueckmeldungMock(
                            taoBV, 50);

            FunktionstraegerBereichRueckmeldung fbr = FunktionstraegerBereichRueckmeldung
                            .buildPersonenRueckmeldung(mock.piaVOs,
                                            daoFactory.getPersonDAO());

            for (int i = 0, m = mock.alFunktionstraeger.size(); i < m; i++)
            {
                BereichInSchleifeVO bis = daoFactory.getObjectFactory()
                                .createBereichInSchleife();
                FunktionstraegerId fId = mock.alFunktionstraeger.get(i)
                                .getFunktionstraegerId();
                BereichId bId = mock.alBereiche.get(i).getBereichId();
                bis.setBereichId(bId);
                bis.setFunktionstraegerId(fId);
                int positiveRueckmeldungen = mock.hmStatPositiveRueckmeldung
                                .get(bId.getLongValue() + "-"
                                                + fId.getLongValue());

                bis.setSollstaerke((positiveRueckmeldungen + 1));
                assertFalse(klinikumAlarmService.isSollstaerkeErreicht(
                                bis.getSollstaerke(), bis, fbr));
                bis.setSollstaerke(positiveRueckmeldungen);
                assertTrue(klinikumAlarmService.isSollstaerkeErreicht(
                                bis.getSollstaerke(), bis, fbr));
                bis.setSollstaerke((positiveRueckmeldungen - 1));
                assertTrue(klinikumAlarmService.isSollstaerkeErreicht(
                                bis.getSollstaerke(), bis, fbr));
            }

        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void bereichNachalarmieren()
    {
        try
        {
            testTAO.cleanTestData();
            createEnvironment();

            BereichInSchleifeVO bisTest = daoFactory.getObjectFactory()
                            .createBereichInSchleife();
            bisTest.setSchleifeId(s2.getSchleifeId());
            bisTest.setBereichId(b.getBereichId());
            bisTest.setFunktionstraegerId(f.getFunktionstraegerId());
            bisTest.setSollstaerke(40);
            bisTest = taoBV.createBereichInSchleife(bisTest);

            FunktionstraegerBereichRueckmeldungMock mock = new FunktionstraegerBereichRueckmeldungMock(
                            taoBV, 50);

            FunktionstraegerBereichRueckmeldung fbr = FunktionstraegerBereichRueckmeldung
                            .buildPersonenRueckmeldung(mock.piaVOs,
                                            daoFactory.getPersonDAO());

            /**
             * Testfall: Es wurde keine Folgeschleife definiert
             */
            AlarmMock am = fbAlarmieren(s2, bisTest);

            assertEquals(KlinikumAlarmService.STATUS_BEREICH_NACHALARMIEREN.FOLGESCHLEIFE_NICHT_DEFINIERT,
                            klinikumAlarmService.bereichNachalarmieren(
                                            am.alarm, s2, bisTest, fbr));

            assertFalse(alarmDAO.isBereichInSchleifeInAlarmAktiv(
                            am.alarm.getAlarmId(),
                            bisTest.getBereichInSchleifeId()));

            /**
             * Testfall: Der Folgebereich existiert nicht
             */
            testTAO.cleanTestData();
            createEnvironment();

            am = fbAlarmieren(schleifeOhneFolgebereich, bis);
            assertEquals(KlinikumAlarmService.STATUS_BEREICH_NACHALARMIEREN.FOLGEBEREICH_NICHT_GEFUNDEN,
                            klinikumAlarmService.bereichNachalarmieren(
                                            am.alarm, schleifeOhneFolgebereich,
                                            bisOhneFolgebereich, fbr));

            /**
             * Testfall: Bereich wurde erfolgreich nachalarmiert
             */
            testTAO.cleanTestData();
            createEnvironment();

            am = fbAlarmieren(s1, bis);
            bisTest.setSchleifeId(s3.getSchleifeId());
            bisTest.setFunktionstraegerId(bis.getFunktionstraegerId());
            bisTest.setBereichId(bis.getBereichId());
            taoBV.createBereichInSchleife(bisTest);

            assertEquals(KlinikumAlarmService.STATUS_BEREICH_NACHALARMIEREN.FOLGEBEREICH_ALARMIERT,
                            klinikumAlarmService.bereichNachalarmieren(
                                            am.alarm, s1, bis, fbr));
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void funktionstraegerBereichAlarmieren()
    {
        try
        {
            AlarmMock am = null;

            /**
             * Testfall: Personen sind noch nicht in der
             * Bereich-Funktionsträger-Kombi hinzugefügt
             */
            am = fbAlarmieren();

            assertEquals(0, am.personenAlarmiert);

            assertEquals(true,
                            daoFactory.getAlarmDAO()
                                            .isBereichInSchleifeInAlarmAktiv(
                                                            am.alarm.getAlarmId(),
                                                            bis.getBereichInSchleifeId()));
            testTAO.cleanTestData();
            createEnvironment();

            /**
             * Testfall: Person hat Funktionsträger, Recht; hat *nicht* Bereich
             */
            p.setFunktionstraegerId(f.getFunktionstraegerId());
            taoBV.updatePerson(p);

            am = fbAlarmieren();

            assertEquals(0, am.personenAlarmiert);
            testTAO.cleanTestData();
            createEnvironment();

            /**
             * Testfall: Person hat Bereich, Recht; hat *nicht* Funktionsträger
             */
            p.setBereichId(b.getBereichId());
            taoBV.updatePerson(p);

            am = fbAlarmieren();
            assertEquals(0, am.personenAlarmiert);
            testTAO.cleanTestData();
            createEnvironment();

            /**
             * Testfall: Person hat Bereich, Recht -> kann alarmiert werden
             */
            p.setBereichId(b.getBereichId());
            p.setFunktionstraegerId(f.getFunktionstraegerId());
            taoBV.updatePerson(p);

            am = fbAlarmieren();
            assertEquals(1, am.personenAlarmiert);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    class AlarmMock
    {
        public AlarmVO alarm;

        public int personenAlarmiert;
    }

    /**
     * Erstellt einen Alarm, fügt die Schleife s1 dem Alarm hinzu und alarmiert
     * den BereichInSchleife bis
     * 
     * @return
     * @throws StdException
     */
    private AlarmMock fbAlarmieren() throws StdException
    {
        return fbAlarmieren(s1, bis);
    }

    /**
     * Erstellt einen neuen Alarm und fügt die übergebene Schleife mit dem
     * BereichInSchleife hinzu
     * 
     * @param _schleifeVO
     * @param _bis
     * @return
     * @throws StdException
     */
    private AlarmMock fbAlarmieren(SchleifeVO _schleifeVO,
                    BereichInSchleifeVO _bis) throws StdException
    {
        AlarmMock r = new AlarmMock();
        r.alarm = createAlarm();

        klinikumAlarmService.begin();

        alarmDAO.addSchleifeToAlarm(_schleifeVO.getSchleifeId(),
                        r.alarm.getAlarmId());

        r.personenAlarmiert = klinikumAlarmService
                        .funktionstraegerBereichAlarmieren(_schleifeVO, _bis,
                                        r.alarm, "Text", UnixTime.now(), "K",
                                        "OE");
        klinikumAlarmService.commit();

        return r;
    }

    private AlarmVO createAlarm() throws StdException
    {
        AlarmVO a = daoFactory.getObjectFactory().createAlarm();

        klinikumAlarmService.begin();
        AlarmDAO alarmDAO = klinikumAlarmService.getDBResource()
                        .getDaoFactory().getAlarmDAO();

        a.setAlarmZeit(UnixTime.now());
        a.setAlarmQuelleId(AlarmQuelleId.ID_WEB);
        a = alarmDAO.createAlarm(a);
        assertEquals(false, (a == null));

        klinikumAlarmService.commit();

        return a;
    }

    @Test
    public void findReferenceSollstaerkeEinBereich()
    {
        try
        {
            // S1 -> S3 -> S4
            testTAO.cleanTestData();
            createEnvironment();

            // Es wurde nur ein Bereich alarmiert
            AlarmMock alarmMock = fbAlarmieren();
            assertEquals(SOLLSTAERKE,
                            klinikumAlarmService.findReferenceSollstaerke(
                                            alarmMock.alarm.getAlarmId(), bis));

            // Deaktivieren
            deaktivieren(alarmMock);

            testTAO.cleanTestData();
            createEnvironment();

            // Schleife ist eigentlich Folgeschleife, da sie aber direkt
            // alarmiert wurde, ist die Referenz der Sollstärke von BIS3
            alarmMock = fbAlarmieren(s3, bis3);
            assertEquals(SOLLSTAERKE_BIS3,
                            klinikumAlarmService.findReferenceSollstaerke(
                                            alarmMock.alarm.getAlarmId(), bis3));

            // Deaktivieren
            deaktivieren(alarmMock);

            testTAO.cleanTestData();
            createEnvironment();

            // Schleife ist Folgeschleifen, wir alarmieren den Folgebereich
            alarmMock = fbAlarmieren(s3, bis3);

            FunktionstraegerBereichRueckmeldungMock mock = new FunktionstraegerBereichRueckmeldungMock(
                            taoBV, 50);

            FunktionstraegerBereichRueckmeldung fbr = FunktionstraegerBereichRueckmeldung
                            .buildPersonenRueckmeldung(mock.piaVOs,
                                            daoFactory.getPersonDAO());

            klinikumAlarmService.begin();
            klinikumAlarmService.bereichNachalarmieren(alarmMock.alarm, s3,
                            bis4, fbr);
            klinikumAlarmService.commit();

            // Bereich muss nachalarmiert worden sein
            assertTrue(daoFactory.getBereichInSchleifeDAO()
                            .istBereichInSchleifeInAlarmNachalarmiert(
                                            alarmMock.alarm.getAlarmId(),
                                            bis4.getFunktionstraegerId(),
                                            bis4.getBereichId()));

            // Bereich in Schleife 4 wurde nachalarmiert, Referenz-Sollstärke
            // ist von BIS3
            assertEquals(SOLLSTAERKE_BIS3,
                            klinikumAlarmService.findReferenceSollstaerke(
                                            alarmMock.alarm.getAlarmId(), bis4));

        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    private void deaktivieren(AlarmMock _mock) throws StdException
    {
        FunktionstraegerBereichRueckmeldungMock mock = new FunktionstraegerBereichRueckmeldungMock(
                        taoBV, 50);

        FunktionstraegerBereichRueckmeldung fbr = FunktionstraegerBereichRueckmeldung
                        .buildPersonenRueckmeldung(mock.piaVOs,
                                        daoFactory.getPersonDAO());

        klinikumAlarmService.alarmDeaktivieren(_mock.alarm, fbr);
    }

    @Test
    public void zwischenStatistik()
    {
        // TODO
    }

    @Test
    public void alarmDeaktivieren()
    {
        try
        {
            testTAO.cleanTestData();
            createEnvironment();

            AlarmMock am = fbAlarmieren();

            FunktionstraegerBereichRueckmeldungMock mock = new FunktionstraegerBereichRueckmeldungMock(
                            taoBV, 50);

            FunktionstraegerBereichRueckmeldung fbr = FunktionstraegerBereichRueckmeldung
                            .buildPersonenRueckmeldung(mock.piaVOs,
                                            daoFactory.getPersonDAO());

            klinikumAlarmService.alarmDeaktivieren(am.alarm, fbr);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void alarmEntwarnung()
    {
        try
        {
            testTAO.cleanTestData();
            createEnvironment();

            AlarmMock am = fbAlarmieren();

            FunktionstraegerBereichRueckmeldungMock mock = new FunktionstraegerBereichRueckmeldungMock(
                            taoBV, 50);

            klinikumAlarmService.alarmEntwarnung(am.alarm, mock.piaVOs);
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void personenEntwarnen()
    {
    }

    @Test
    public void generateRueckmeldungReport()
    {
        try
        {
            testTAO.cleanTestData();
            createEnvironment();

            AlarmMock am = fbAlarmieren();

            ZwischenStatistik zs = klinikumAlarmService
                            .zwischenStatistik(am.alarm);

            String report = klinikumAlarmService.generateRueckmeldungReport(
                            am.alarm, dbResource.getObjectFactory()
                                            .createSchleife(), zs.gesamt);
            assertTrue((report != null));
            assertTrue((report.length() > 0));
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void processAktiveAlarmeSollstaerkeErreichtAlarmDeatkvieren()
    {
        /**
         * Testfall: In allen Bereichen der Schleife wurde die Sollstärke
         * erreicht. <br />
         * Der Alarm muss deaktiviert werden.
         */
        try
        {
            AlarmVO alarm = null;

            testTAO.cleanTestData();
            createEnvironment();

            ArrayList<PersonVO> p = createPersonenMitTelefon(SOLLSTAERKE, "",
                            f.getFunktionstraegerId(), b.getBereichId());

            for (int i = 0, m = p.size(); i < m; i++)
            {
                dbResource.getTaoFactory()
                                .getRechteTAO()
                                .addPersonInRolleToSchleife(
                                                p.get(i).getPersonId(),
                                                ro.getRolleId(),
                                                s1.getSchleifeId());

            }

            alarm = klinikumAlarmService.alarmAusloesen("", new SchleifeVO[]
            { s1 }, AlarmQuelleId.ID_5TON, null, "", "");

            for (int i = 0, m = p.size(); i < m; i++)
            {
                taoBV.updateRueckmeldungStatus(p.get(i).getPersonId(),
                                new RueckmeldungStatusId(
                                                RueckmeldungStatusId.STATUS_JA));
            }

            klinikumAlarmService.processAktiveAlarme();

            assertFalse(dbResource
                            .getDaoFactory()
                            .getAlarmDAO()
                            .isBereichInSchleifeInAlarmAktiv(
                                            alarm.getAlarmId(),
                                            bis.getBereichInSchleifeId()));
            assertFalse(dbResource.getDaoFactory().getAlarmDAO()
                            .isSchleifeAktiv(s1.getSchleifeId()));
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    public void processAktiveAlarmeFolgeschleifeAlarmieren()
    {
        /**
         * Testfall: In 1 / 2 Bereichen wurde die Sollstärke erreicht und das
         * Timeout ist abgelaufen. Die Schleife besitzt eine Folgeschleife <br />
         * Der fehlende Bereich der Folgeschleife muss alarmiert werden
         */
        try
        {
            testTAO.cleanTestData();
            createEnvironment();

            FunktionstraegerVO f1 = daoFactory.getObjectFactory()
                            .createFunktionstraeger();
            f1.setBeschreibung("F1");
            f1.setKuerzel("F1");
            f1 = taoBV.createFunktionstraeger(f1);

            FunktionstraegerVO f2 = daoFactory.getObjectFactory()
                            .createFunktionstraeger();
            f2.setBeschreibung("F2");
            f2.setKuerzel("F2");
            f2 = taoBV.createFunktionstraeger(f2);

            BereichVO b1 = daoFactory.getObjectFactory().createBereich();
            b1.setName("B1");
            b1.setBeschreibung("B1");
            b1 = taoBV.createBereich(b1);

            BereichVO b2 = daoFactory.getObjectFactory().createBereich();
            b2.setName("B2");
            b2.setBeschreibung("B2");
            b2 = taoBV.createBereich(b2);

            SchleifeVO schleife3 = daoFactory.getObjectFactory()
                            .createSchleife();
            schleife3.setOrganisationsEinheitId(oe.getOrganisationsEinheitId());
            schleife3.setName("_s3");
            schleife3.setKuerzel("_s3");
            schleife3.setRueckmeldeintervall(2);
            schleife3 = taoBV.createSchleife(schleife3);

            SchleifeVO schleife2 = daoFactory.getObjectFactory()
                            .createSchleife();
            schleife2.setOrganisationsEinheitId(oe.getOrganisationsEinheitId());
            schleife2.setName("_s2");
            schleife2.setKuerzel("_s2");
            schleife2.setFolgeschleifeId(schleife3.getSchleifeId());
            schleife2.setRueckmeldeintervall(2);
            schleife2 = taoBV.createSchleife(schleife2);

            SchleifeVO schleife1 = daoFactory.getObjectFactory()
                            .createSchleife();
            schleife1.setOrganisationsEinheitId(oe.getOrganisationsEinheitId());
            schleife1.setName("_s1");
            schleife1.setKuerzel("_s1");
            schleife1.setFolgeschleifeId(schleife2.getSchleifeId());
            schleife1.setRueckmeldeintervall(2);
            schleife1 = taoBV.createSchleife(schleife1);

            // Aufbau: S1 -> S2 -> S3
            // S1: F1/B1 (wird erreicht), F2/B2 (wird nicht erreicht)
            // S2: F2/B2 (keine Person)
            // S3: F2/B2 (erreicht die Sollstärke von S1;

            int sollstaerkeBisF1B1S1 = 10;
            BereichInSchleifeVO bisF1B1S1 = daoFactory.getObjectFactory()
                            .createBereichInSchleife();
            bisF1B1S1.setBereichId(b1.getBereichId());
            bisF1B1S1.setSchleifeId(schleife1.getSchleifeId());
            bisF1B1S1.setFunktionstraegerId(f1.getFunktionstraegerId());
            bisF1B1S1.setSollstaerke(sollstaerkeBisF1B1S1);
            bisF1B1S1 = taoBV.createBereichInSchleife(bisF1B1S1);

            int sollstaerkeBisF2B2S1 = 20;
            BereichInSchleifeVO bisF2B2S1 = daoFactory.getObjectFactory()
                            .createBereichInSchleife();
            bisF2B2S1.setBereichId(b2.getBereichId());
            bisF2B2S1.setSchleifeId(schleife1.getSchleifeId());
            bisF2B2S1.setFunktionstraegerId(f2.getFunktionstraegerId());
            bisF2B2S1.setSollstaerke(sollstaerkeBisF2B2S1);
            bisF2B2S1 = taoBV.createBereichInSchleife(bisF2B2S1);

            int sollstaerkeBisF2B2S2 = 30;
            BereichInSchleifeVO bisF2B2S2 = daoFactory.getObjectFactory()
                            .createBereichInSchleife();
            bisF2B2S2.setBereichId(b2.getBereichId());
            bisF2B2S2.setSchleifeId(schleife2.getSchleifeId());
            bisF2B2S2.setFunktionstraegerId(f2.getFunktionstraegerId());
            bisF2B2S2.setSollstaerke(sollstaerkeBisF2B2S2);
            bisF2B2S2 = taoBV.createBereichInSchleife(bisF2B2S2);

            int sollstaerkeBisF2B2S3 = 40;
            BereichInSchleifeVO bisF2B2S3 = daoFactory.getObjectFactory()
                            .createBereichInSchleife();
            bisF2B2S3.setBereichId(b2.getBereichId());
            bisF2B2S3.setSchleifeId(schleife3.getSchleifeId());
            bisF2B2S3.setFunktionstraegerId(f2.getFunktionstraegerId());
            bisF2B2S3.setSollstaerke(sollstaerkeBisF2B2S3);
            bisF2B2S3 = taoBV.createBereichInSchleife(bisF2B2S3);

            // Personen, die alle Ja antworten
            ArrayList<PersonVO> personenInF1B1S1 = createPersonenMitTelefon(
                            sollstaerkeBisF1B1S1, "f1b1s1",
                            f1.getFunktionstraegerId(), b1.getBereichId());

            for (int i = 0, m = personenInF1B1S1.size(); i < m; i++)
            {

                dbResource.getTaoFactory()
                                .getRechteTAO()
                                .addPersonInRolleToSchleife(
                                                personenInF1B1S1.get(i)
                                                                .getPersonId(),
                                                ro.getRolleId(),
                                                schleife1.getSchleifeId());
            }

            // Personen, die alle mit Ja antworten. Sind aber zu wenig
            ArrayList<PersonVO> personenInF2B2S1 = createPersonenMitTelefon(
                            (sollstaerkeBisF2B2S1 - 5), "f2b2s1",
                            f2.getFunktionstraegerId(), b2.getBereichId());

            for (int i = 0, m = personenInF2B2S1.size(); i < m; i++)
            {

                dbResource.getTaoFactory()
                                .getRechteTAO()
                                .addPersonInRolleToSchleife(
                                                personenInF2B2S1.get(i)
                                                                .getPersonId(),
                                                ro.getRolleId(),
                                                schleife2.getSchleifeId());
            }

            // In Schleife 2 befindet sich gar keine Person

            // In Schleife 3 wird dann schließlich das SOLL erreicht
            ArrayList<PersonVO> personenInF2B2S3 = createPersonenMitTelefon(
                            sollstaerkeBisF2B2S1, "f2b2s3",
                            f2.getFunktionstraegerId(), b2.getBereichId());

            for (int i = 0, m = personenInF2B2S3.size(); i < m; i++)
            {

                dbResource.getTaoFactory()
                                .getRechteTAO()
                                .addPersonInRolleToSchleife(
                                                personenInF2B2S3.get(i)
                                                                .getPersonId(),
                                                ro.getRolleId(),
                                                schleife3.getSchleifeId());
            }

            // S1 zuerst alarmieren. Sollstärke von 40 ist benötigt, 20 Personen
            // existieren aber nur innerhalb dieser Schleife
            AlarmVO alarm = klinikumAlarmService.alarmAusloesen("",
                            new SchleifeVO[]
                            { schleife1 }, AlarmQuelleId.ID_5TON, null, "", "");

            assertTrue(dbResource
                            .getDaoFactory()
                            .getAlarmDAO()
                            .isBereichInSchleifeInAlarmAktiv(
                                            alarm.getAlarmId(),
                                            bisF1B1S1.getBereichInSchleifeId()));
            assertTrue(dbResource
                            .getDaoFactory()
                            .getAlarmDAO()
                            .isBereichInSchleifeInAlarmAktiv(
                                            alarm.getAlarmId(),
                                            bisF2B2S1.getBereichInSchleifeId()));
            assertTrue(dbResource.getDaoFactory().getAlarmDAO()
                            .isSchleifeAktiv(schleife1.getSchleifeId()));

            // Alarm läuft ab; bisF1B1S1 wird deaktiviert da das SOLL erreicht
            // ist, Bereich von S2 wird
            // nachalarmiert
            klinikumAlarmService.processAktiveAlarme();

            assertTrue(dbResource.getDaoFactory().getAlarmDAO()
                            .isSchleifeAktiv(schleife1.getSchleifeId()));

            // Schleife ist noch nicht aktiv, da kein Timeout
            assertFalse(dbResource.getDaoFactory().getAlarmDAO()
                            .isSchleifeAktiv(schleife2.getSchleifeId()));

            assertFalse(dbResource.getDaoFactory().getAlarmDAO()
                            .isSchleifeAktiv(schleife3.getSchleifeId()));

            // Schleife ist noch aktiv da noch keine Rückmeldung
            assertTrue(dbResource
                            .getDaoFactory()
                            .getAlarmDAO()
                            .isBereichInSchleifeInAlarmAktiv(
                                            alarm.getAlarmId(),
                                            bisF1B1S1.getBereichInSchleifeId()));

            assertTrue(dbResource
                            .getDaoFactory()
                            .getAlarmDAO()
                            .isBereichInSchleifeInAlarmAktiv(
                                            alarm.getAlarmId(),
                                            bisF2B2S1.getBereichInSchleifeId()));

            // Rückmeldungen eintragen
            for (int i = 0, m = personenInF1B1S1.size(); i < m; i++)
            {
                taoBV.updateRueckmeldungStatus(personenInF1B1S1.get(i)
                                .getPersonId(), new RueckmeldungStatusId(
                                RueckmeldungStatusId.STATUS_JA));
            } // F1B1S1 erhält somit alle Rückmeldungen

            // F2B2S1 erhält Rückmeldungen, aber die Sollstärke kann nicht
            // eingetragen werden
            for (int i = 0, m = personenInF2B2S1.size(); i < m; i++)
            {
                taoBV.updateRueckmeldungStatus(personenInF2B2S1.get(i)
                                .getPersonId(), new RueckmeldungStatusId(
                                RueckmeldungStatusId.STATUS_JA));
            }
            klinikumAlarmService.enableTimeout.add(bisF2B2S1
                            .getBereichInSchleifeId().getLongValue());

            klinikumAlarmService.processAktiveAlarme();

            assertTrue(dbResource.getDaoFactory().getAlarmDAO()
                            .isSchleifeAktiv(schleife1.getSchleifeId()));
            // S2 muss nun nachalarmiert wurden sein
            assertTrue(dbResource.getDaoFactory().getAlarmDAO()
                            .isSchleifeAktiv(schleife2.getSchleifeId()));
            assertFalse(dbResource.getDaoFactory().getAlarmDAO()
                            .isSchleifeAktiv(schleife3.getSchleifeId()));
            // Der 1. Bereich wurde entwarnt
            assertFalse(dbResource
                            .getDaoFactory()
                            .getAlarmDAO()
                            .isBereichInSchleifeInAlarmAktiv(
                                            alarm.getAlarmId(),
                                            bisF1B1S1.getBereichInSchleifeId()));
            assertTrue(dbResource
                            .getDaoFactory()
                            .getAlarmDAO()
                            .isBereichInSchleifeInAlarmAktiv(
                                            alarm.getAlarmId(),
                                            bisF2B2S2.getBereichInSchleifeId()));

            // Timeout von S2 ablaufen lassen
            klinikumAlarmService.enableTimeout.add(bisF2B2S2
                            .getBereichInSchleifeId().getLongValue());

            klinikumAlarmService.processAktiveAlarme();

            assertTrue(dbResource.getDaoFactory().getAlarmDAO()
                            .isSchleifeAktiv(schleife1.getSchleifeId()));
            // S2 muss nun nachalarmiert wurden sein
            assertTrue(dbResource.getDaoFactory().getAlarmDAO()
                            .isSchleifeAktiv(schleife2.getSchleifeId()));
            assertTrue(dbResource.getDaoFactory().getAlarmDAO()
                            .isSchleifeAktiv(schleife3.getSchleifeId()));
            // Der 1. Bereich wurde entwarnt
            assertFalse(dbResource
                            .getDaoFactory()
                            .getAlarmDAO()
                            .isBereichInSchleifeInAlarmAktiv(
                                            alarm.getAlarmId(),
                                            bisF1B1S1.getBereichInSchleifeId()));
            assertFalse(dbResource
                            .getDaoFactory()
                            .getAlarmDAO()
                            .isBereichInSchleifeInAlarmAktiv(
                                            alarm.getAlarmId(),
                                            bisF2B2S2.getBereichInSchleifeId()));
            assertTrue(dbResource
                            .getDaoFactory()
                            .getAlarmDAO()
                            .isBereichInSchleifeInAlarmAktiv(
                                            alarm.getAlarmId(),
                                            bisF2B2S3.getBereichInSchleifeId()));

            // Personen für S3 eintragen
            for (int i = 0, m = personenInF2B2S3.size(); i < m; i++)
            {
                taoBV.updateRueckmeldungStatus(personenInF2B2S3.get(i)
                                .getPersonId(), new RueckmeldungStatusId(
                                RueckmeldungStatusId.STATUS_JA));
            }

            klinikumAlarmService.processAktiveAlarme();

            assertFalse(dbResource.getDaoFactory().getAlarmDAO()
                            .isSchleifeAktiv(schleife1.getSchleifeId()));
            // S2 muss nun nachalarmiert wurden sein
            assertFalse(dbResource.getDaoFactory().getAlarmDAO()
                            .isSchleifeAktiv(schleife2.getSchleifeId()));
            assertFalse(dbResource.getDaoFactory().getAlarmDAO()
                            .isSchleifeAktiv(schleife3.getSchleifeId()));
            // Der 1. Bereich wurde entwarnt
            assertFalse(dbResource
                            .getDaoFactory()
                            .getAlarmDAO()
                            .isBereichInSchleifeInAlarmAktiv(
                                            alarm.getAlarmId(),
                                            bisF1B1S1.getBereichInSchleifeId()));
            assertFalse(dbResource
                            .getDaoFactory()
                            .getAlarmDAO()
                            .isBereichInSchleifeInAlarmAktiv(
                                            alarm.getAlarmId(),
                                            bisF2B2S2.getBereichInSchleifeId()));
            assertFalse(dbResource
                            .getDaoFactory()
                            .getAlarmDAO()
                            .isBereichInSchleifeInAlarmAktiv(
                                            alarm.getAlarmId(),
                                            bisF2B2S3.getBereichInSchleifeId()));
            assertEquals(0, dbResource.getDaoFactory().getAlarmDAO()
                            .findAktiveAlarme().length);
        }

        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    private void append(Map<SchleifeId, Map<String, List<PersonVO>>> _map,
                    SchleifeId _schleifeId, String _key, PersonVO _person)
    {
        if (!_map.containsKey(_schleifeId))
        {
            _map.put(_schleifeId, new HashMap<String, List<PersonVO>>());
        }

        if (!_map.get(_schleifeId).containsKey(_key))
        {
            _map.get(_schleifeId).put(_key, new ArrayList<PersonVO>());
        }

        if (_person != null)
        {
            _map.get(_schleifeId).get(_key).add(_person);
        }
    }

    private List<PersonVO> extractPersonenFromSchleife(
                    Map<SchleifeId, Map<String, List<PersonVO>>> _map,
                    SchleifeId _schleifeId)
    {
        List<PersonVO> r = new ArrayList<PersonVO>();

        if (_map.containsKey(_schleifeId))
        {
            for (String key : _map.get(_schleifeId).keySet())
            {
                r.addAll(_map.get(_schleifeId).get(key));
            }
        }
        return r;
    }

    private List<PersonVO> filterPersonen(List<PersonVO> _personVO,
                    List<Long> _personIds)
    {
        List<PersonVO> r = new ArrayList<PersonVO>();

        if (_personVO != null)
        {
            for (PersonVO p : _personVO)
            {
                if (_personIds.contains(p.getPersonId().getLongValue()))
                {
                    r.add(p);
                }
            }
        }

        return r;
    }

    @Test
    public void simulateMassentest()
    {
        try
        {
            testTAO.cleanTestData();
            createEnvironment();
            Random rand = new Random();

            StatistikDAO statistikDAO = dbResource.getDaoFactory()
                            .getStatistikDao();

            // Standard-Benutzer entfernen
            taoBV.deleteTelefon(t.getTelefonId());
            dbResource.getTaoFactory()
                            .getRechteTAO()
                            .removePersonInRolleFromSystem(p.getPersonId(),
                                            ro.getRolleId());
            taoBV.deletePerson(p.getPersonId());

            int minAnzahlPersonenPerSchleife = 100;
            int maxAnzahlPersonenPerSchleife = 300;
            int anzahlBereiche = 5;
            int anzahlFunktionstraeger = 5;
            // Ergibt 100 Kombinationen Bereich/Funktionsträger
            int minBereichFunktionstraegerPerSchleife = 10;
            int maxBereichFunktionstraegerPerSchleife = 20;
            int minSollstaerkePerBereich = 5;
            int maxSollstaerkePerBereich = 20;
            int schleifenAnzahl = 4;
            // Jede Schleife besitzt eine Folgeschleife
            int folgeschleifenAnzahl = 4;

            assertTrue((maxSollstaerkePerBereich > minSollstaerkePerBereich));
            assertTrue((folgeschleifenAnzahl <= schleifenAnzahl && folgeschleifenAnzahl > 0));

            for (int cntTestSzenario = 1, anzahlAlarmierungsSzenarien = 3; cntTestSzenario < anzahlAlarmierungsSzenarien; cntTestSzenario++)
            {
                log.info("Erzeuge Testszenario " + cntTestSzenario);

                List<FunktionstraegerVO> funktionstraeger = new ArrayList<FunktionstraegerVO>();
                List<PersonVO> personen = new ArrayList<PersonVO>();
                List<BereichVO> bereiche = new ArrayList<BereichVO>();
                List<SchleifeVO> hauptschleifen = new ArrayList<SchleifeVO>();
                List<SchleifeVO> folgeSchleifen = new ArrayList<SchleifeVO>();
                List<SchleifeVO> mergedSchleifen = new ArrayList<SchleifeVO>();
                Map<String, BereichInSchleifeVO> mapBereichInSchleife = new HashMap<String, BereichInSchleifeVO>();

                // Hält die Struktur der erzeugten Personen innerhalb der
                // Schleifen
                // vor
                Map<SchleifeId, Map<String, List<PersonVO>>> mapSchleifeToBISKeyToPersonen = new HashMap<SchleifeId, Map<String, List<PersonVO>>>();;

                // Zuerst die Bereiche erzeugen
                for (int i = 0; i < anzahlBereiche; i++)
                {
                    BereichVO be = daoFactory.getObjectFactory()
                                    .createBereich();
                    be.setBeschreibung("A" + cntTestSzenario + "_BE_" + i);
                    be.setName("A" + cntTestSzenario + "_BE_" + i);
                    be = taoBV.createBereich(be);
                    bereiche.add(be);
                }

                // Funktionsträger erzeugen
                for (int i = 0; i < anzahlFunktionstraeger; i++)
                {
                    FunktionstraegerVO ft = daoFactory.getObjectFactory()
                                    .createFunktionstraeger();
                    ft.setBeschreibung("A" + cntTestSzenario + "_FT_" + i);
                    ft.setKuerzel("A" + cntTestSzenario + "_FT_" + i);
                    ft = taoBV.createFunktionstraeger(ft);
                    funktionstraeger.add(ft);
                }

                // Schleifen erzeugen
                for (int i = 0; i < schleifenAnzahl; i++)
                {
                    SchleifeVO s = daoFactory.getObjectFactory()
                                    .createSchleife();
                    s.setKuerzel("HS" + cntTestSzenario + "_" + i);
                    s.setName("HS" + cntTestSzenario + "_" + i);
                    s.setOrganisationsEinheitId(oe.getOrganisationsEinheitId());
                    s.setRueckmeldeintervall(10);
                    s = taoBV.createSchleife(s);
                    hauptschleifen.add(s);
                }

                List<Long> alreadyUsedSchleifenIds = new ArrayList<Long>();

                int anzahlBereichInSchleifenKombinationen = 0;

                /** Key: Id */
                Map<String, SchleifeVO> mapHauptSchleifeToFolgeSchleife = new HashMap<String, SchleifeVO>();

                // Folgeschleifen erzeugen. Jeweils nur eine Folgeschleife pro
                // Hauptschleife
                for (int i = 0; i < folgeschleifenAnzahl; i++)
                {
                    SchleifeVO hauptschleife = null;

                    // Zufällig eine noch nicht benutzte Schleife auswählen
                    do
                    {
                        int idx = randomInteger(0, (schleifenAnzahl - 1), rand);
                        hauptschleife = hauptschleifen.get(idx);
                    }
                    while (alreadyUsedSchleifenIds.contains(hauptschleife
                                    .getSchleifeId().getLongValue()));

                    // Schleife als schon benutzt markieren
                    alreadyUsedSchleifenIds.add(hauptschleife.getSchleifeId()
                                    .getLongValue());
                    assertNotNull(hauptschleife);
                    SchleifeVO folgeschleife = daoFactory.getObjectFactory()
                                    .createSchleife();
                    folgeschleife.setKuerzel("FS" + cntTestSzenario + "_" + i);
                    folgeschleife.setName("FS" + cntTestSzenario + "_" + i);
                    folgeschleife.setOrganisationsEinheitId(oe
                                    .getOrganisationsEinheitId());
                    folgeschleife = taoBV.createSchleife(folgeschleife);
                    folgeSchleifen.add(folgeschleife);

                    hauptschleife.setFolgeschleifeId(folgeschleife
                                    .getSchleifeId());
                    hauptschleife = taoBV.updateSchleife(hauptschleife);

                    mapHauptSchleifeToFolgeSchleife.put(""
                                    + hauptschleife.getSchleifeId()
                                                    .getLongValue(),
                                    folgeschleife);

                    log.info("Zuweisung Folgeschleife [" + hauptschleife
                                    + "]  -> [" + folgeschleife.getKuerzel()
                                    + "]");
                }

                // Schleifen zusammenführen
                mergedSchleifen.addAll(hauptschleifen);
                mergedSchleifen.addAll(folgeSchleifen);
                assertEquals(mergedSchleifen.size(),
                                (hauptschleifen.size() + folgeSchleifen.size()));

                Map<String, BereichInSchleifeVO> mapFBKToBIS = new HashMap<String, BereichInSchleifeVO>();

                Map<String, List<BereichInSchleifeVO>> mapSchleifeIdToBereicheInSchleife = new HashMap<String, List<BereichInSchleifeVO>>();

                // Bereichs-Funktionsträgerkombinationen erstellen
                for (int i = 0, m = hauptschleifen.size(); i < m; i++)
                {
                    SchleifeVO s = hauptschleifen.get(i);
                    mapSchleifeIdToBereicheInSchleife.put(
                                    "" + s.getSchleifeId(),
                                    new ArrayList<BereichInSchleifeVO>());

                    int anzahlFunktionstraegerBereichKombintationInSchleife = randomInteger(
                                    minBereichFunktionstraegerPerSchleife,
                                    maxBereichFunktionstraegerPerSchleife, rand);

                    List<String> alreadyUsedFunktionstraegerBereichKombination = new ArrayList<String>();
                    List<BereichInSchleifeVO> listSubBereichInSchleife = new ArrayList<BereichInSchleifeVO>();

                    log.info("Erstelle Funktionstraeger-Bereich-Kombination für Schleife ["
                                    + s.getName()
                                    + "] ["
                                    + s.getSchleifeId()
                                    + "]: ");

                    for (int j = 0; j < anzahlFunktionstraegerBereichKombintationInSchleife; j++)
                    {
                        BereichVO b = null;
                        FunktionstraegerVO f = null;

                        String key = null;

                        do
                        {
                            int idxRandomBereiche = randomInteger(0,
                                            (bereiche.size() - 1), rand);
                            int idxRandomFunktionstraeger = randomInteger(0,
                                            (funktionstraeger.size() - 1), rand);
                            f = funktionstraeger.get(idxRandomFunktionstraeger);
                            b = bereiche.get(idxRandomBereiche);
                            key = toKey(f.getFunktionstraegerId(),
                                            b.getBereichId());
                        }
                        while (alreadyUsedFunktionstraegerBereichKombination
                                        .contains(key));

                        alreadyUsedFunktionstraegerBereichKombination.add(key);

                        BereichInSchleifeVO bis = daoFactory.getObjectFactory()
                                        .createBereichInSchleife();
                        bis.setSchleifeId(s.getSchleifeId());
                        int sollstaerke = randomInteger(
                                        minSollstaerkePerBereich,
                                        maxSollstaerkePerBereich, rand);
                        bis.setSollstaerke(sollstaerke);
                        bis.setBereichId(b.getBereichId());
                        bis.setFunktionstraegerId(f.getFunktionstraegerId());

                        bis = taoBV.createBereichInSchleife(bis);
                        log.info("    FBK Funktionsträger [" + f.getKuerzel()
                                        + "] [" + f.getFunktionstraegerId()
                                        + "] ");
                        log.info("    FBK Bereich [" + b.getName() + "] ["
                                        + b.getBereichId() + "] ");
                        log.info("  FBK erstellt ["
                                        + bis.getBereichInSchleifeId()
                                        + "] Sollstärke ["
                                        + bis.getSollstaerke() + "]");

                        mapFBKToBIS.put(key, bis);
                        anzahlBereichInSchleifenKombinationen++;
                        listSubBereichInSchleife.add(bis);
                        mapBereichInSchleife
                                        .put(toKey(s.getSchleifeId(),
                                                        bis.getFunktionstraegerId(),
                                                        bis.getBereichId()),
                                                        bis);
                        mapSchleifeIdToBereicheInSchleife.get(
                                        "" + s.getSchleifeId()).add(bis);
                    }

                    // Zufallsmäßig die Folgeschleifen erzeugen
                    SchleifeVO folgeschleife = mapHauptSchleifeToFolgeSchleife
                                    .get("" + s.getSchleifeId().getLongValue());
                    assertNotNull(folgeschleife);

                    for (int j = 0, n = randomInteger(0,
                                    (listSubBereichInSchleife.size() - 1), rand); j < n; j++)
                    {
                        BereichInSchleifeVO bisHauptschleife = listSubBereichInSchleife
                                        .get(j);
                        BereichInSchleifeVO bisFolgeschleife = daoFactory
                                        .getObjectFactory()
                                        .createBereichInSchleife();
                        bisFolgeschleife.setBereichId(bisHauptschleife
                                        .getBereichId());
                        bisFolgeschleife.setFunktionstraegerId(bisHauptschleife
                                        .getFunktionstraegerId());
                        bisFolgeschleife.setSchleifeId(s.getFolgeschleifeId());
                        bisFolgeschleife.setSollstaerke(10);
                        bisFolgeschleife = taoBV
                                        .createBereichInSchleife(bisFolgeschleife);
                        log.info("  FBK fuer Folgeschleife " + bisFolgeschleife
                                        + " erzeugt");

                        mapBereichInSchleife
                                        .put(toKey(bisFolgeschleife
                                                        .getSchleifeId(),
                                                        bisFolgeschleife.getFunktionstraegerId(),
                                                        bisFolgeschleife.getBereichId()),
                                                        bisFolgeschleife);

                    }
                }

                log.info("Insgesamt " + anzahlBereichInSchleifenKombinationen
                                + " FBKs erzeugt");

                int bMax = (bereiche.size() - 1);
                int fMax = (funktionstraeger.size() - 1);
                FunktionstraegerVO f = null;
                BereichVO b = null;
                SchleifeVO s = null;

                // Personen, die in der Folgeschleife alarmiert werden müssen
                List<PersonVO> expectedZuAlarmierendePersonenInHauptschleifen = new ArrayList<PersonVO>();
                // Personen, die in der Nachschleife alarmiert werden müssen
                List<PersonVO> expectedZuAlarmierendePersonenInFolgeschleifen = new ArrayList<PersonVO>();

                int erzeugtePersonenGesamt = 0;

                List<Long> personenInFolgeschleife = new ArrayList<Long>();
                List<Long> personenInHauptschleife = new ArrayList<Long>();

                // Personen für die Schleife erzeugen
                for (int i = 0, m = mergedSchleifen.size(); i < m; i++)
                {
                    s = mergedSchleifen.get(i);
                    int anzahlPersonen = randomInteger(
                                    minAnzahlPersonenPerSchleife,
                                    maxAnzahlPersonenPerSchleife, rand);

                    append(mapSchleifeToBISKeyToPersonen, s.getSchleifeId(),
                                    "", null);

                    for (int j = 0; j < anzahlPersonen; j++)
                    {
                        taoBV.begin();
                        f = funktionstraeger.get(randomInteger(0, fMax, rand));
                        b = bereiche.get(randomInteger(0, bMax, rand));

                        // Wir brauchen nur eine Person. Name der Person ist
                        // mergedSchleifen_anzahlPersonen
                        PersonVO p = createPersonenMitTelefon("A"
                                        + cntTestSzenario + "_" + i + "_" + j,
                                        f.getFunktionstraegerId(),
                                        b.getBereichId());

                        personen.add(p);

                        dbResource.getDaoFactory()
                                        .getSchleifenDAO()
                                        .addPersonInRolleToSchleife(
                                                        p.getPersonId(),
                                                        ro.getRolleId(),
                                                        s.getSchleifeId());
                        taoBV.commit();

                        // Die Überprüfung, ob die Person die passende
                        // Funktionsträger/Bereichskombination besitzt, findet
                        // nicht
                        // statt.
                        // Wenn eine Schleife ausgelöst wird, werden alle darin
                        // enthaltenen Personen alarmiert und *nicht* nur die,
                        // die
                        // in den Bereichen stehen
                        if (s.getFolgeschleifeId() == null)
                        {
                            personenInFolgeschleife.add(p.getPersonId()
                                            .getLongValue());
                            expectedZuAlarmierendePersonenInFolgeschleifen
                                            .add(p);
                        }
                        else
                        {
                            personenInHauptschleife.add(p.getPersonId()
                                            .getLongValue());
                            expectedZuAlarmierendePersonenInHauptschleifen
                                            .add(p);
                        }

                        append(mapSchleifeToBISKeyToPersonen,
                                        s.getSchleifeId(),
                                        toKey(p.getFunktionstraegerId(),
                                                        p.getBereichId()), p);

                    }

                    erzeugtePersonenGesamt += anzahlPersonen;
                }

                log.info("Erstellte Personen in den Schleifen:");
                for (SchleifeId schleifeId : mapSchleifeToBISKeyToPersonen
                                .keySet())
                {
                    log.info("  Schleife [" + schleifeId + "]");

                    for (String key : mapSchleifeToBISKeyToPersonen.get(
                                    schleifeId).keySet())
                    {
                        log.info("    FBK ["
                                        + key
                                        + "]: "
                                        + mapSchleifeToBISKeyToPersonen
                                                        .get(schleifeId)
                                                        .get(key).size()
                                        + " Personen");
                    }
                }

                SchleifeVO[] arrSchleifen = new SchleifeVO[hauptschleifen
                                .size()];
                arrSchleifen = hauptschleifen.toArray(arrSchleifen);

                String TEXT = "randomtext";

                AlarmVO alarm = klinikumAlarmService.alarmAusloesen(TEXT,
                                arrSchleifen, AlarmQuelleId.ID_5TON, null, "",
                                "");

                assertEquals(hauptschleifen.size(),
                                daoFactory.getSchleifenDAO()
                                                .findSchleifenByAlarmId(
                                                                alarm.getAlarmId()).length);

                DAOFactory daoFactory = dbResource.getDaoFactory();

                List<PersonId> alarmiertePersonen = new ArrayList<PersonId>();

                PersonInAlarmVO[] piaVOs = daoFactory.getPersonInAlarmDAO()
                                .findPersonenInAlarmByAlarmId(
                                                alarm.getAlarmId());

                assertEquals(expectedZuAlarmierendePersonenInHauptschleifen
                                .size(),
                                piaVOs.length);

                for (PersonInAlarmVO pia : piaVOs)
                {
                    alarmiertePersonen.add(pia.getPersonId());
                }

                // Überprüfen, ob die Hauptschleifen alarmiert sind
                for (int i = 0; i < hauptschleifen.size(); i++)
                {
                    SchleifeVO schleife = hauptschleifen.get(i);
                    log.info("Überprüfe Hauptschleife [" + schleife.getName()
                                    + "] [" + schleife.getSchleifeId() + "]");

                    assertTrue(daoFactory.getAlarmDAO().isSchleifeAktiv(
                                    schleife.getSchleifeId()));
                    // Überprüfen, ob alle Personen innerhalb der
                    // Bereiche-/Funktionstraeger alarmiert worden sind
                    BereichInSchleifeVO[] bereicheInSchleife = daoFactory
                                    .getBereichInSchleifeDAO()
                                    .findBereicheInSchleifeBySchleifeId(
                                                    schleife.getSchleifeId());

                    List<PersonVO> expectedPersonenInSchleife = extractPersonenFromSchleife(
                                    mapSchleifeToBISKeyToPersonen,
                                    schleife.getSchleifeId());

                    PersonVO[] personenInSchleife = daoFactory.getPersonDAO()
                                    .findPersonenInSchleife(
                                                    schleife.getSchleifeId());

                    List<PersonVO> personenFound = Arrays
                                    .asList(personenInSchleife);

                    assertEquals(expectedPersonenInSchleife.size(),
                                    personenFound.size());

                    for (BereichInSchleifeVO bis : bereicheInSchleife)
                    {
                        String key = toKey(bis.getFunktionstraegerId(),
                                        bis.getBereichId());
                        log.info("Überprüfe  BereichInSchleife FBK " + bis
                                        + " [" + key + "]");

                        BereichReportStatistikVO statistik = statistikDAO
                                        .findByAlarmIdAndFunktionstraegerIdAndBereichIdAndSchleifeId(
                                                        alarm.getAlarmId(),
                                                        bis.getFunktionstraegerId(),
                                                        bis.getBereichId(),
                                                        bis.getSchleifeId());
                        assertNotNull(statistik);
                        assertEquals(bis.getBereichId(),
                                        statistik.getBereichId());
                        assertEquals(bis.getFunktionstraegerId(),
                                        statistik.getFunktionstraegerId());
                        assertEquals(0, statistik.getRueckmeldungPositiv());
                        assertEquals(bis.getSchleifeId(),
                                        statistik.getSchleifeId());
                        List<PersonVO> personenUngefiltert = mapSchleifeToBISKeyToPersonen
                                        .get(schleife.getSchleifeId()).get(key);
                        int gefiltert = filterPersonen(personenUngefiltert,
                                        personenInHauptschleife).size();

                        assertEquals(gefiltert,
                                        statistik.getRueckmeldungUnbekannt());
                    }

                }

                assertEquals(anzahlBereichInSchleifenKombinationen,
                                statistikDAO.findByAlarmId(alarm.getAlarmId()).length);

                // Folgeschleifen müssen alle deaktiviert sein, da noch nicht
                // ausgelöst
                for (int i = 0; i < folgeSchleifen.size(); i++)
                {

                    assertFalse(daoFactory.getAlarmDAO().isSchleifeAktiv(
                                    folgeSchleifen.get(i).getSchleifeId()));
                }

                int anzahlAlarmiertePersonen = 0;

                Map<String, Long> rueckmeldungenPositiv = new HashMap<String, Long>();
                Map<String, Long> rueckmeldungenNegativ = new HashMap<String, Long>();

                for (SchleifeVO hauptschleife : hauptschleifen)
                {
                    Map<String, List<PersonVO>> fbrToPersonen = mapSchleifeToBISKeyToPersonen
                                    .get(hauptschleife.getSchleifeId());
                    log.info("Erzeuge Rückmeldungen für Schleife ["
                                    + hauptschleife.getName() + "] ["
                                    + hauptschleife.getSchleifeId() + "]");

                    for (String key : fbrToPersonen.keySet())
                    {
                        String keySBF = hauptschleife.getSchleifeId() + "_"
                                        + key;

                        rueckmeldungenNegativ.put(keySBF, new Long(0));
                        rueckmeldungenPositiv.put(keySBF, new Long(0));

                        for (PersonVO p : fbrToPersonen.get(key))
                        {
                            boolean bJa = rand.nextBoolean();
                            long r = (bJa) ? (RueckmeldungStatusId.STATUS_JA)
                                            : (RueckmeldungStatusId.STATUS_NEIN);
                            taoBV.updateRueckmeldungStatus(p.getPersonId(),
                                            new RueckmeldungStatusId(r));

                            updateRueckmeldung(bJa, keySBF,
                                            rueckmeldungenPositiv,
                                            rueckmeldungenNegativ);

                            anzahlAlarmiertePersonen++;
                        }

                        log.info("  positive Rückmeldungen für FBK [" + key
                                        + "]: "
                                        + rueckmeldungenPositiv.get(keySBF));

                    }
                }

                // Nun die Anzahl der Rückmeldungen aufaddieren
                piaVOs = daoFactory.getPersonInAlarmDAO()
                                .findPersonenInAlarmByAlarmId(
                                                alarm.getAlarmId());
                assertNotNull(piaVOs);
                assertEquals(anzahlAlarmiertePersonen, piaVOs.length);

                int rueckmeldungJa = 0;
                int rueckmeldungNein = 0;

                for (PersonInAlarmVO piaVO : piaVOs)
                {
                    if ((piaVO.getRueckmeldungStatusId() == null)
                                    || (piaVO.getRueckmeldungStatusId()
                                                    .getLongValue() == RueckmeldungStatusId.STATUS_NEIN))
                    {
                        rueckmeldungNein++;
                    }
                    else if (piaVO.getRueckmeldungStatusId().getLongValue() == RueckmeldungStatusId.STATUS_JA)
                    {
                        rueckmeldungJa++;
                    }
                    else
                    {
                        fail("Rueckmeldung ist nicht definiert");
                    }
                }

                long expectedAnzahlRueckmeldungenPositiv = sumOverKey(rueckmeldungenPositiv);
                long expectedAnzahlRueckmeldungenNegativ = sumOverKey(rueckmeldungenNegativ);
                assertEquals(expectedAnzahlRueckmeldungenPositiv,
                                rueckmeldungJa);
                assertEquals(expectedAnzahlRueckmeldungenNegativ,
                                rueckmeldungNein);

                // Rückmeldung überprüfen
                FunktionstraegerBereichRueckmeldung frb = FunktionstraegerBereichRueckmeldung
                                .buildPersonenRueckmeldung(piaVOs,
                                                daoFactory.getPersonDAO());

                assertEquals((expectedAnzahlRueckmeldungenNegativ + expectedAnzahlRueckmeldungenPositiv),
                                frb.getTotalPersonen());

                // FBK testen
                rueckmeldungJa = 0;

                List<BereichInSchleifeVO> expectedBereicheInSchleifeNachalarmiert = new ArrayList<BereichInSchleifeVO>();

                for (SchleifeVO schleife : hauptschleifen)
                {
                    log.info("Überprüfe Schleife [" + schleife.getName() + "]");

                    for (BereichInSchleifeVO bis : mapSchleifeIdToBereicheInSchleife
                                    .get("" + schleife.getSchleifeId()))
                    {
                        String SFBKkey = toKey(schleife.getSchleifeId(),
                                        bis.getFunktionstraegerId(),
                                        bis.getBereichId());
                        // Timeout aktivieren
                        klinikumAlarmService.enableTimeout.add(bis
                                        .getBereichInSchleifeId()
                                        .getLongValue());

                        PersonInAlarmVO[] piaVOInSchleifenBereich = daoFactory
                                        .getPersonInAlarmDAO()
                                        .findPersonenInAlarmByAlarmIdAndSchleifeId(
                                                        alarm.getAlarmId(),
                                                        schleife.getSchleifeId());

                        FunktionstraegerBereichRueckmeldung statistikFuerBereich = FunktionstraegerBereichRueckmeldung
                                        .buildPersonenRueckmeldung(
                                                        piaVOInSchleifenBereich,
                                                        daoFactory.getPersonDAO());

                        boolean sollstaerkeErreicht = klinikumAlarmService
                                        .isSollstaerkeErreicht(
                                                        bis.getSollstaerke(),
                                                        bis,
                                                        statistikFuerBereich);

                        long rueckmeldungenFuerBereich = 0;

                        if (rueckmeldungenPositiv.get(SFBKkey) != null)
                        {
                            rueckmeldungenFuerBereich += rueckmeldungenPositiv
                                            .get(SFBKkey);
                        }
                        else
                        {
                            log.error("Es wurden keine Rückmeldungen für "
                                            + SFBKkey + " eingetragen!");
                        }

                        log.info("        Sollstaerke erreicht ["
                                        + sollstaerkeErreicht
                                        + "] positive Rückmeldungen ["
                                        + rueckmeldungenFuerBereich + "]");

                        assertEquals((rueckmeldungenFuerBereich >= bis
                                        .getSollstaerke()),
                                        sollstaerkeErreicht);

                        if (!sollstaerkeErreicht)
                        {
                            if (schleife.getFolgeschleifeId() == null)
                            {
                                log.info("        Für den Bereich existiert keine Folgeschleife");
                                continue;
                            }

                            log.info("        Versuche Folgebereich der Folgeschleife zu finden");

                            BereichInSchleifeVO folgeBereich = mapBereichInSchleife
                                            .get(schleife.getFolgeschleifeId()
                                                            + "_"
                                                            + toKey(bis.getFunktionstraegerId(),
                                                                            bis.getBereichId()));

                            if (folgeBereich == null)
                            {
                                log.error("        Folgebereich existiert nicht. Schleife wird nicht nachalarmiert");
                                continue;

                            }

                            log.info("        Bereich [" + folgeBereich
                                            + "] muss nachalarmiert werden!");
                            expectedBereicheInSchleifeNachalarmiert
                                            .add(folgeBereich);
                        }

                    }
                }

                Map<String, Boolean> mapNachalarmierteSchleifenMitRueckmeldung = new HashMap<String, Boolean>();

                klinikumAlarmService.processAktiveAlarme();

                BereichInSchleifeVO[] bereicheInSchleifeNachalarmiert = daoFactory
                                .getBereichInSchleifeDAO()
                                .findAktiveBereicheInSchleifeInAlarmByAlarmId(
                                                alarm.getAlarmId());

                for (BereichInSchleifeVO bisNachalarmiertInDatenbank : bereicheInSchleifeNachalarmiert)
                {
                    boolean bWasExpected = false;
                    for (BereichInSchleifeVO bisNachalarmiertExpected : expectedBereicheInSchleifeNachalarmiert)
                    {
                        if (bisNachalarmiertInDatenbank
                                        .getBereichInSchleifeId()
                                        .getLongValue() == bisNachalarmiertExpected
                                        .getBereichInSchleifeId()
                                        .getLongValue())
                        {
                            bWasExpected = true;
                            log.info("FBK ["
                                            + bisNachalarmiertExpected
                                            + "] existiert in Datenbank und ist erwartet worden");
                            break;
                        }
                    }

                    if (!bWasExpected)
                    {
                        log.error("FBK ["
                                        + bisNachalarmiertInDatenbank
                                        + "] wurde in Datenbank nachalarmiert, obwohl dies nicht erwartet war");
                    }
                }

                assertEquals(expectedBereicheInSchleifeNachalarmiert.size(),
                                bereicheInSchleifeNachalarmiert.length);

                /** Key: $SchleifeId_$FunktionstraegerId_$BereichId */
                HashMap<String, BereichInSchleifeVO> mapSFBKToNachalarmierteBereichInSchleife = new HashMap<String, BereichInSchleifeVO>();

                // Überprüfen, ob alle nötigen Bereiche wirklich nachalarmiert
                // worden sind
                for (BereichInSchleifeVO bis : expectedBereicheInSchleifeNachalarmiert)
                {
                    boolean sollstaerkeErreichen = rand.nextBoolean();

                    assertTrue(daoFactory
                                    .getBereichInSchleifeDAO()
                                    .istBereichInSchleifeInAlarmNachalarmiert(
                                                    alarm.getAlarmId(),
                                                    bis.getFunktionstraegerId(),
                                                    bis.getBereichId()));

                    // Timeout für die ggw. FBK definieren
                    klinikumAlarmService.enableTimeout.add(bis
                                    .getBereichInSchleifeId().getLongValue());

                    // Überprüfen, ob die Personen des Bereichs der
                    // Folgeschleife im
                    // Alarm sind
                    PersonVO[] nachalarmiertePersonen = daoFactory
                                    .getPersonDAO()
                                    .findPersonenByRechtInSchleife(
                                                    RechtId.ALARMBENACHRICHTIGUNG_EMPFANGEN,
                                                    bis.getSchleifeId());

                    int personenGefunden = 0;

                    SchleifeId ref = bis.getSchleifeId();

                    if (mapHauptSchleifeToFolgeSchleife.containsKey(""
                                    + ref.getLongValue()))
                    {
                        log.info("Mappe SchleifeId BereichInSchleife auf Folgeschleife!");
                        ref = mapHauptSchleifeToFolgeSchleife.get("" + ref)
                                        .getSchleifeId();
                    }

                    String key = toKey(ref, bis.getFunktionstraegerId(),
                                    bis.getBereichId());

                    for (PersonVO person : nachalarmiertePersonen)
                    {
                        // Person muss nachalarmiert worden sein
                        if ((person.getBereichId().getLongValue() == bis
                                        .getBereichId().getLongValue())
                                        && (person.getFunktionstraegerId()
                                                        .getLongValue() == bis
                                                        .getFunktionstraegerId()
                                                        .getLongValue()))
                        {
                            assertTrue(daoFactory
                                            .getAlarmDAO()
                                            .isPersonInAlarm(
                                                            person.getPersonId(),
                                                            alarm.getAlarmId()));

                            long rueckmeldung = RueckmeldungStatusId.STATUS_NEIN;

                            if (sollstaerkeErreichen)
                            {
                                rueckmeldung = RueckmeldungStatusId.STATUS_JA;
                                personenGefunden++;
                            }

                            taoBV.updateRueckmeldungStatus(
                                            person.getPersonId(),
                                            new RueckmeldungStatusId(
                                                            rueckmeldung));

                            updateRueckmeldung(sollstaerkeErreichen, key,
                                            rueckmeldungenPositiv,
                                            rueckmeldungenNegativ);
                            mapSFBKToNachalarmierteBereichInSchleife.put(key,
                                            bis);
                        }
                    }

                    // Referenzsollstärke laden, d.h. die Sollstärke die in der
                    // zuerst ausgelösten Vorgängerschleife definiert worden ist
                    int sollstaerke = klinikumAlarmService
                                    .findReferenceSollstaerke(
                                                    alarm.getAlarmId(), bis);

                    // TODO Letzte Änderung
                    sollstaerkeErreichen = (personenGefunden >= sollstaerke);

                    mapNachalarmierteSchleifenMitRueckmeldung.put(key,
                                    sollstaerkeErreichen);
                }

                for (String key : mapSFBKToNachalarmierteBereichInSchleife
                                .keySet())
                {
                    log.info("Nachzularmierende FBKs: " + key);
                }

                // Alarm wird beendet da das Timeout für alle FBKs erreicht
                // worden
                // ist
                klinikumAlarmService.processAktiveAlarme();

                // Alle Bereiche in den Schleifen müssen deaktiviert worden sein
                for (String key : mapFBKToBIS.keySet())
                {
                    assertFalse(daoFactory
                                    .getAlarmDAO()
                                    .isBereichInSchleifeInAlarmAktiv(
                                                    alarm.getAlarmId(),
                                                    mapFBKToBIS.get(key)
                                                                    .getBereichInSchleifeId()));
                }

                // Alle Schleifen müssen inaktiv sein
                for (SchleifeVO schleife : mergedSchleifen)
                {
                    assertFalse(daoFactory.getAlarmDAO().isSchleifeAktiv(
                                    schleife.getSchleifeId()));
                }

                // Alarm muss inaktiv sein
                AlarmVO datenbankAlarm = daoFactory.getAlarmDAO()
                                .findAlarmById(alarm.getAlarmId());
                assertFalse(datenbankAlarm.getAktiv());

                BereichReportStatistikVO[] statistiken = statistikDAO
                                .findByAlarmId(alarm.getAlarmId());
                assertNotNull(statistiken);

                /** Key: $Schleife_$Funktionsträger_$Bereich */
                Map<String, BereichReportStatistikVO> mapSFBKToStatistik = new HashMap<String, BereichReportStatistikVO>();

                // Lookup-Table aufbauen
                for (BereichReportStatistikVO statistik : statistiken)
                {
                    mapSFBKToStatistik.put(
                                    toKey(statistik.getSchleifeId(), statistik
                                                    .getFunktionstraegerId(),
                                                    statistik.getBereichId()),
                                    statistik);
                }

                // Über die Hauptschleifen iterieren und mit der Statistik
                // vergleichen
                int expectedStatistikEintraege = 0;
                for (SchleifeVO schleife : hauptschleifen)
                {
                    BereichInSchleifeVO[] bereichInSchleifen = daoFactory
                                    .getBereichInSchleifeDAO()
                                    .findBereicheInSchleifeBySchleifeId(
                                                    schleife.getSchleifeId());

                    for (BereichInSchleifeVO bis : bereichInSchleifen)
                    {
                        expectedStatistikEintraege++;

                        log.info("Überrpüfe [" + schleife + "] ["
                                        + schleife.getSchleifeId() + "] FBK "
                                        + bis);

                        String key = toKey(bis.getSchleifeId(),
                                        bis.getFunktionstraegerId(),
                                        bis.getBereichId());
                        assertTrue(mapSFBKToStatistik.containsKey(key));

                        BereichReportStatistikVO statistik = mapSFBKToStatistik
                                        .get(key);

                        assertEquals(schleife.getSchleifeId(),
                                        statistik.getSchleifeId());
                        assertEquals(schleife.getName(),
                                        statistik.getSchleifeName());

                        assertEquals(bis.getBereichId(),
                                        statistik.getBereichId());
                        assertEquals(bis.getFunktionstraegerId(),
                                        statistik.getFunktionstraegerId());
                        log.info("Sollstärke expected: " + bis.getSollstaerke()
                                        + ", Sollstärke gegeben: "
                                        + statistik.getBereichSollstaerke());
                        assertEquals(bis.getSollstaerke(),
                                        statistik.getBereichSollstaerke());
                        assertEquals(alarm.getAlarmId(), statistik.getAlarmId());
                        assertEquals(alarm.getEntwarnZeit(),
                                        statistik.getEntwarnZeit());

                        long positiveRueckmeldungenInFBK = 0;

                        if (rueckmeldungenPositiv.containsKey(key))
                        {
                            positiveRueckmeldungenInFBK += rueckmeldungenPositiv
                                            .get(key);
                        }

                        if (schleife.getFolgeschleifeId() != null)
                        {
                            String folgeschleifenKey = toKey(
                                            schleife.getFolgeschleifeId(),
                                            bis.getFunktionstraegerId(),
                                            bis.getBereichId());

                            if (mapNachalarmierteSchleifenMitRueckmeldung
                                            .containsKey(folgeschleifenKey))
                            {
                                log.info("FBK "
                                                + bis
                                                + " wurde nachalarmiert, lade positive Rückmeldungen");
                                Long r = rueckmeldungenPositiv
                                                .get(folgeschleifenKey);
                                if (r == null)
                                {
                                    log.warn("FBK "
                                                    + bis
                                                    + " wurde nachalarmiert, es wurden aber keine positiven Antworten dazu eingetragen");
                                    continue;
                                }

                                positiveRueckmeldungenInFBK += r.longValue();
                            }
                        }

                        assertEquals(positiveRueckmeldungenInFBK,
                                        statistik.getRueckmeldungPositiv());
                    }
                }

                assertEquals(expectedStatistikEintraege, statistiken.length);
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }

    private void updateRueckmeldung(boolean isPositiv, String _key,
                    Map<String, Long> _mapRueckmeldungPositiv,
                    Map<String, Long> _mapRueckmeldungNegativ)
    {
        Map<String, Long> refMap = _mapRueckmeldungNegativ;

        if (isPositiv)
        {
            refMap = _mapRueckmeldungPositiv;
        }

        long val = 1;

        if (!refMap.containsKey(_key))
        {
            refMap.put(_key, val);
        }
        else
        {
            val = refMap.get(_key);
            val++;
        }

        refMap.put(_key, val);
    }

    private long sumOverKey(Map<String, Long> _map)
    {
        long r = 0;
        for (String o : _map.keySet())
        {
            r += _map.get(o);
        }

        return r;
    }

    private String toKey(SchleifeId _sId, FunktionstraegerId _fId,
                    BereichId _bId)
    {
        return _sId.getLongValue() + "_" + toKey(_fId, _bId);
    }

    private String toKey(FunktionstraegerId _fId, BereichId _bId)
    {
        return _fId.getLongValue() + "_" + _bId.getLongValue();
    }

    public static int randomInteger(int aStart, int aEnd, Random aRandom)
    {
        if (aStart > aEnd)
        {
            throw new IllegalArgumentException("Start cannot exceed End.");
        }
        // get the range, casting to long to avoid overflow problems
        long range = (long) aEnd - (long) aStart + 1;
        // compute a fraction of the range, 0 <= frac < range
        long fraction = (long) (range * aRandom.nextDouble());
        int randomNumber = (int) (fraction + aStart);

        return randomNumber;
    }

    private ArrayList<PersonVO> createPersonenMitTelefon(int _total,
                    String _prefix, FunktionstraegerId _fId, BereichId _bId) throws StdException
    {
        ArrayList<PersonVO> r = new ArrayList<PersonVO>();
        for (int i = 0; i < _total; i++)
        {
            klinikumAlarmService.begin();
            r.add(createPersonenMitTelefon(i + "_" + _prefix, _fId, _bId));
            klinikumAlarmService.commit();
        }

        return r;
    }

    private PersonVO createPersonenMitTelefon(String _prefix,
                    FunktionstraegerId _fId, BereichId _bId) throws StdException
    {
        PersonVO person = daoFactory.getObjectFactory().createPerson();
        person.setVorname("" + _prefix);
        person.setNachname("" + _prefix);
        person.setName("" + _prefix);
        person.setBereichId(_bId);
        person.setFunktionstraegerId(_fId);
        person = daoFactory.getPersonDAO().createPerson(person);

        TelefonVO telefon = daoFactory.getObjectFactory().createTelefon();
        telefon.setNummer(new TelefonNummer("0049" + _prefix));
        telefon.setPersonId(person.getPersonId());
        telefon.setAktiv(true);
        telefon = daoFactory.getTelefonDAO().createTelefon(telefon);

        return person;
    }
}
