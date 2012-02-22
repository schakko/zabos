package de.ecw.zabos.sql.ao.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.test.ZabosTestAdapter;

public class PersonAOTest extends ZabosTestAdapter
{
    private static boolean isInitialized = false;

    private static PersonVO testObject;

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {
        }
    }

    @Test
    public void createPerson()
    {
        try
        {
            testObject = daoFactory.getObjectFactory().createPerson();
            testObject.setVorname("vorname");
            testObject.setNachname("nachname");
            testObject.setName("benutzername");

            HashMap<String, String> hmOptions = new HashMap<String, String>();
            hmOptions.put("KEY", "VALUE");
            testObject.setReportOptionen(hmOptions);

            testObject = taoBV.createPerson(testObject);

            assertTrue((testObject != null));

            assertTrue((testObject.getReportOptionen() != null));
            assertTrue((testObject.getReportOptionen().containsKey("KEY")));
            assertEquals(1, testObject.getReportOptionen().size());
            assertEquals("VALUE", testObject.getReportOptionen().get("KEY"));
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }
}
