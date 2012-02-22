package de.ecw.zabos.mc35;

import org.junit.Assert;
import org.junit.Test;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.mc35.MC35;
import de.ecw.zabos.mc35.ShortMessage;
import de.ecw.zabos.service.smsin.ISmsInService;
import de.ecw.zabos.sql.vo.ObjectFactory;
import de.ecw.zabos.sql.vo.SystemKonfigurationMc35VO;

public class MC35Test
{
    class MC35Mock extends MC35
    {
        public MC35Mock(ISmsInService smsInService,
                        SystemKonfigurationMc35VO konfigurationVO)
                        throws StdException
        {
            super(smsInService, konfigurationVO);
        }

        public ShortMessage newMessage(String _cmglHeader)
        {
            return super.newMessage(_cmglHeader);
        }
    }

    @Test
    public void newMessage() throws StdException
    {
        ObjectFactory objectFactory = new ObjectFactory();
        MC35Mock mock = new MC35Mock(null,
                        objectFactory.createSystemKonfigurationMc35());

        mock.newMessage("");
        mock.newMessage("+CMGL: 1,\"REC UNREAD\",\"10/09/01,16:19:00+08\"");
        mock.newMessage("+CMGL: 1,\"REC UNREAD\",\"+4917123213123\",,\"10/09/01,16:19:00+08\"");
        mock.newMessage("+CMGL: A,\"REC UNREAD\",\"+491711111111111\",,\"0/09/01,16:18:49+08\"");
    }

    @Test
    public void testAtSpic()
    {
        String s = "^SPIC: 3";
        String expectedAnswer = "^SPIC: ";
        int r = 0;

        if (s.indexOf(expectedAnswer) == 0)
        {
            String retriesLeft = s.substring(expectedAnswer.length());

            r = new Integer(retriesLeft).intValue();
        }

        Assert.assertEquals(3, r);
    }
}
