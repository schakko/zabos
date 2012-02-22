package de.ecw.zabos.broadcast.transport.http;

import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;

import de.ecw.zabos.broadcast.Recipient;
import de.ecw.zabos.broadcast.transport.http.HttpBroadcasterGroup;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.vo.SmsOutVO;
import de.ecw.zabos.test.ZabosTestAdapter;
import de.ecw.zabos.types.TelefonNummer;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.SmsOutId;

public class SmsBroadcasterGroupTest extends ZabosTestAdapter
{
    @Test
    public void newSmsBroadcasterGroup()
    {
        ArrayList<Recipient> recp = new ArrayList<Recipient>();

        for (int i = 0; i < 20; i++)
        {
            SmsOutVO smsOutVO = daoFactory.getObjectFactory().createSmsOut();
            smsOutVO.setContext("Kontext");
            smsOutVO.setContextO("Kontext-O");
            smsOutVO.setContextOE("Kontext-OE");

            try
            {
                smsOutVO.setNachricht("Nachricht");
                smsOutVO.setZeitpunkt(UnixTime.now());
                SmsOutId outId = new SmsOutId(i);
                smsOutVO.setSmsOutId(outId);
            }
            catch (StdException e)
            {
                fail("Failed: " + e.getMessage());
            }

            Recipient r = new Recipient(smsOutVO, new TelefonNummer(""
                            + (i + 1)), new TelefonNummer("000011"));
            recp.add(r);
        }

        HttpBroadcasterGroup grp = new HttpBroadcasterGroup();
        grp.setRecipients(recp);
        grp.run();
    }
}
