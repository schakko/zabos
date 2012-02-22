package de.ecw.zabos.test;

import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.ecw.zabos.SpringContext;
import de.ecw.zabos.sql.dao.DAOFactory;
import de.ecw.zabos.sql.resource.DBResource;
import de.ecw.zabos.sql.tao.BenutzerVerwaltungTAO;
import de.ecw.zabos.sql.tao.TestTAO;

/**
 * Von diesem Test-Adapter müssen alle JUnit-Tests von ZABOS erben. Es werden
 * einige Default-Properties bereit gestellt. Standardmäßig wird über Annotation
 * {@link ContextConfiguration} die test-applicationContext.xml herangezogen. In
 * ihr stehen die Daten, die für die Unit-Tests benutzt werden.
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations =
{ "classpath:test-applicationContext.xml" })
public class ZabosTestAdapter extends AbstractJUnit4SpringContextTests
{
    protected static TestTAO testTAO;

    protected static DBResource dbResource;

    protected static DAOFactory daoFactory;

    protected static BenutzerVerwaltungTAO taoBV;

    private static boolean isEnvInitialized = false;

    @Before
    synchronized public void initEnvironment()
    {
        if (!isEnvInitialized)
        {
            isEnvInitialized = true;

            try
            {
                SpringContext
                                .getInstance()
                                .setApplicationContext(
                                                (AbstractApplicationContext) applicationContext);
                dbResource = (DBResource) SpringContext.getInstance().getBean(
                                SpringContext.BEAN_DB_RESOURCE,
                                DBResource.class);
                testTAO = new TestTAO(dbResource);
                daoFactory = dbResource.getDaoFactory();
                taoBV = dbResource.getTaoFactory().getBenutzerVerwaltungTAO();
                testTAO.cleanTestData();
            }
            catch (Exception e)
            {

                fail("Subsystem konnte nicht initalisiert werden: "
                                + e.getMessage());
            }
        }
    }

    @AfterClass
    public static void clean()
    {
        try
        {
            testTAO.cleanTestData();
        }
        catch (Exception e)
        {
            fail("Cleaning fehlgeschlagen: " + e.getMessage());
        }
    }
}
