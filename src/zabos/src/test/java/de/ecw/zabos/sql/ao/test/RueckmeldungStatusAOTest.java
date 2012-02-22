package de.ecw.zabos.sql.ao.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.dao.RueckmeldungStatusDAO;
import de.ecw.zabos.sql.vo.RueckmeldungStatusAliasVO;
import de.ecw.zabos.sql.vo.RueckmeldungStatusVO;
import de.ecw.zabos.test.ZabosTestAdapter;
import de.ecw.zabos.types.id.RueckmeldungStatusId;

public class RueckmeldungStatusAOTest extends ZabosTestAdapter
{
    private static final String NAME = "name";

    private static RueckmeldungStatusVO testObject = null;

    private static RueckmeldungStatusDAO daoRueckmeldungStatus;

    private static boolean isInitialized = false;

    private void assertObject(RueckmeldungStatusVO r)
    {
        assertNotNull(r);
        assertEquals(r.getName(), NAME);
    }

    @Before
    public synchronized void initEnvironmentSub()
    {
        if (!isInitialized)
        {
            daoRueckmeldungStatus = dbResource.getDaoFactory()
                            .getRueckmeldungStatusDAO();
            isInitialized = true;
        }
    }

    @Test
    public void findRueckmeldungStatusByAlias()
    {
        RueckmeldungStatusAliasVO vo = daoFactory.getObjectFactory()
                        .createRueckmeldungStatusAlias();
        try
        {
            vo.setAlias("jetzt");
            vo.setRueckmeldungStatusId(new RueckmeldungStatusId(
                            RueckmeldungStatusId.STATUS_JA));
            vo = dbResource.getTaoFactory().getRueckmeldungStatusAliasTAO()
                            .createRueckmeldungStatusAlias(vo);

            RueckmeldungStatusVO r = daoRueckmeldungStatus
                            .findRueckmeldungStatusByAlias(vo.getAlias());
            assertEquals(r.getRueckmeldungStatusId(),
                            vo.getRueckmeldungStatusId());

            dbResource.getTaoFactory()
                            .getRueckmeldungStatusAliasTAO()
                            .deleteRueckmeldungStatusAlias(
                                            vo.getRueckmeldungStatusAliasId());
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
    }
}
