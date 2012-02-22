package de.ecw.zabos.sql.ao.test;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.controllers.SchleifeController;
import de.ecw.zabos.sql.tao.RechteTAO;
import de.ecw.zabos.sql.vo.ObjectFactory;
import de.ecw.zabos.sql.vo.OrganisationVO;
import de.ecw.zabos.sql.vo.OrganisationsEinheitVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.RolleVO;
import de.ecw.zabos.sql.vo.SchleifeVO;
import de.ecw.zabos.test.ZabosTestAdapter;

public class TransactionTest extends ZabosTestAdapter
{
    private static SchleifeVO schleifeVO;

    private static PersonVO personVO;

    private static RechteTAO rechteTAO;

    private static RolleVO rolleVO;

    private static boolean isInitialized = false;

    @Before
    public synchronized void initEnvironmentSub() throws StdException
    {
        if (!isInitialized)
        {
            ObjectFactory of = dbResource.getObjectFactory();
            rechteTAO = dbResource.getTaoFactory().getRechteTAO();

            personVO = of.createPerson();
            personVO.setName("name");
            personVO.setVorname("vorname");
            personVO.setNachname("nachname");
            personVO = dbResource.getTaoFactory().getBenutzerVerwaltungTAO()
                            .createPerson(personVO);

            OrganisationVO o = of.createOrganisation();
            o.setName("o");

            o = dbResource.getTaoFactory().getBenutzerVerwaltungTAO()
                            .createOrganisation(o);

            OrganisationsEinheitVO oe = of.createOrganisationsEinheit();
            oe.setOrganisationId(o.getOrganisationId());
            oe.setName("oe");
            oe = dbResource.getTaoFactory().getBenutzerVerwaltungTAO()
                            .createOrganisationseinheit(oe);

            schleifeVO = of.createSchleife();
            schleifeVO.setOrganisationsEinheitId(oe.getOrganisationsEinheitId());
            schleifeVO.setName("s");
            schleifeVO.setKuerzel("sk");

            schleifeVO = dbResource.getTaoFactory().getBenutzerVerwaltungTAO()
                            .createSchleife(schleifeVO);

            rolleVO = of.createRolle();
            rolleVO.setName("rolle");
            rolleVO = dbResource.getTaoFactory().getBenutzerVerwaltungTAO()
                            .createRolle(rolleVO);

            isInitialized = true;
        }
    }

    class TestRunnable implements Runnable
    {
        private SchleifeVO schleifeVO;

        private PersonVO personVO;

        private RechteTAO rechteTAO;

        private RolleVO rolleVO;

        private String name;

        private final Logger log = Logger.getLogger(TestRunnable.class);

        public TestRunnable(RechteTAO rechteTAO, PersonVO personVO,
                        RolleVO rolleVO, SchleifeVO schleifeVO, String name)
        {
            this.rechteTAO = rechteTAO;
            this.personVO = personVO;
            this.schleifeVO = schleifeVO;
            this.rolleVO = rolleVO;
            this.name = name;
        }

        public final static int MAX = 1000;

        public void run()
        {
            for (int i = 0; i < MAX; i++)
            {
                log.info(name + ": ENTER ");
                rechteTAO.addPersonInRolleToSchleife(personVO.getPersonId(),
                                rolleVO.getRolleId(),
                                schleifeVO.getSchleifeId());

                log.info(name + ": EXIT");
            }
        }

    }

    @Test
    public void addPersonInKontext() throws Exception
    {
        // Hier wird getestet, ob die Personen sich gleichzeitig hinzufügen
        // lassen.
        // Wir bekommen ohne synchronized-Statement Probleme mit dem
        // Doppelklick. So blöd das auch klingt
        Thread t1 = new Thread(new TestRunnable(rechteTAO, personVO, rolleVO,
                        schleifeVO, "thread-1"));
        Thread t2 = new Thread(new TestRunnable(rechteTAO, personVO, rolleVO,
                        schleifeVO, "thread-2"));

        t1.start();
        t2.start();
        // join(), damit der Hauptthread (JUnit) wartet, bis t1 und t2 beendet worden sind.
        // Ansonsten gibt es eine NullPointerException
        t1.join();
        t2.join();
    }
}
