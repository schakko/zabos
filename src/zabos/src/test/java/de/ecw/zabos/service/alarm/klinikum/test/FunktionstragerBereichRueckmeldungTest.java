package de.ecw.zabos.service.alarm.klinikum.test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.service.alarm.klinikum.FunktionstraegerBereichRueckmeldung;
import de.ecw.zabos.sql.dao.PersonDAO;
import de.ecw.zabos.sql.vo.BereichVO;
import de.ecw.zabos.sql.vo.FunktionstraegerVO;
import de.ecw.zabos.test.ZabosTestAdapter;

public class FunktionstragerBereichRueckmeldungTest extends ZabosTestAdapter
{
    private static boolean isInitialized = false;

    private static PersonDAO personDAO;

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {
            isInitialized = true;
            personDAO = daoFactory.getPersonDAO();
        }
    }

    @Test
    public void buildPersonenRueckmeldung()
    {

        try
        {
            FunktionstraegerBereichRueckmeldungMock mock = new FunktionstraegerBereichRueckmeldungMock(
                            taoBV, 50);

            FunktionstraegerBereichRueckmeldung fbr = FunktionstraegerBereichRueckmeldung
                            .buildPersonenRueckmeldung(mock.piaVOs, personDAO);

            assertEquals(mock.personen, fbr.getTotalPersonen());

            assertEquals(mock.alFunktionstraeger.size(), fbr
                            .getTotalFunktionstraeger());

            // Anzahl der Personen zu den zugehörigen Funktionsträgern finden
            for (int i = 0, m = mock.alFunktionstraeger.size(); i < m; i++)
            {
                FunktionstraegerVO f = mock.alFunktionstraeger.get(i);
                BereichVO b = mock.alBereiche.get(i);
                assertNotNull(fbr.findByFunktionstraegerId(f
                                .getFunktionstraegerId()));

                String id = b.getBereichId().getLongValue() + "-"
                                + f.getFunktionstraegerId().getLongValue();

                assertNotNull(mock.hmStatPersonenInBereichFunktionstraegerKombination.get(id));

                assertEquals((int) mock.hmStatPersonenInBereichFunktionstraegerKombination.get(id), fbr
                                .findByFunktionstraegerIdAndBereichId(
                                                f.getFunktionstraegerId(),
                                                b.getBereichId()).size());
            }
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }
}
