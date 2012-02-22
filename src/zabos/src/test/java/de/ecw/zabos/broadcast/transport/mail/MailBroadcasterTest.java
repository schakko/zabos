package de.ecw.zabos.broadcast.transport.mail;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.internet.MimeMessage;

import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import de.ecw.zabos.broadcast.IBroadcaster;
import de.ecw.zabos.broadcast.Recipient;
import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.vo.SmsOutVO;
import de.ecw.zabos.test.ZabosTestAdapter;
import de.ecw.zabos.types.TelefonNummer;

public class MailBroadcasterTest extends ZabosTestAdapter
{
    private Wiser wiser;

    @Test
    public void mailBroadcasterOhneLoadBalancing() throws Exception
    {
        wiser = new Wiser(7777);
        wiser.start();
        SmsOutVO smsOutVo = daoFactory.getObjectFactory().createSmsOut();
        smsOutVo.setContext("context");
        smsOutVo.setContextAlarm("alarmcontext");
        try
        {
            smsOutVo.setNachricht("nachricht\nmitzeilenumbruch");
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
        smsOutVo.setFestnetzSms(false);

        Recipient recipient = new Recipient(smsOutVo, new TelefonNummer("555"),
                        new TelefonNummer("111"));
        List<Recipient> recipients = new ArrayList<Recipient>();
        recipients.add(recipient);

        MailBroadcaster broadcaster = new MailBroadcaster();
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setPort(7777);
        mailSender.setHost("localhost");
        mailSender.setDefaultEncoding("iso-8859-1");
        broadcaster.setMailSender(mailSender);
        broadcaster.setApplicationContext(applicationContext);
        broadcaster.setFrom("'unittest@localhost'");
        broadcaster.setUseLoadBalancing(false);
        broadcaster.setTo("'recp@localhost'");
        broadcaster.setBody("smsOutVO.nachricht");
        broadcaster.setSubject("'An: ' + handyNr");
        broadcaster.setRecipients(recipients);
        broadcaster.run();

        assertEquals(IBroadcaster.SMS_STATUS_VERSCHICKT,
                        recipient.getStatusCode());
        List<WiserMessage> messages = wiser.getMessages();
        assertEquals(1, messages.size());
        WiserMessage msg = messages.get(0);
        MimeMessage mime = msg.getMimeMessage();

        Address[] tos = mime.getRecipients(RecipientType.TO);
        assertEquals(1, tos.length);
        assertEquals("recp@localhost", tos[0].toString());

        Address[] froms = mime.getFrom();
        assertEquals(1, froms.length);
        assertEquals("unittest@localhost", froms[0].toString());

        assertEquals("An: 555", mime.getSubject());
        String[] lines = mime.getContent().toString().split("\\r?\\n");
        assertEquals(2, lines.length);
        assertEquals("nachricht", lines[0]);
        assertEquals("mitzeilenumbruch", lines[1]);

        wiser.stop();
    }

    @Test
    public void mailBroadcasterMitLoadBalancing() throws Exception
    {
        wiser = new Wiser(7777);
        wiser.start();
        SmsOutVO smsOutVo = daoFactory.getObjectFactory().createSmsOut();
        smsOutVo.setContext("context");
        smsOutVo.setContextAlarm("alarmcontext");
        try
        {
            smsOutVo.setNachricht("nachricht\nmitzeilenumbruch");
        }
        catch (StdException e)
        {
            fail(e.getMessage());
        }
        smsOutVo.setFestnetzSms(false);

        Recipient r1 = new Recipient(smsOutVo, new TelefonNummer("1"),
                        new TelefonNummer("111"));
        Recipient r2 = new Recipient(smsOutVo, new TelefonNummer("2"),
                        new TelefonNummer("111"));
        Recipient r3 = new Recipient(smsOutVo, new TelefonNummer("3"),
                        new TelefonNummer("111"));
        List<Recipient> recipients = new ArrayList<Recipient>();
        recipients.add(r1);
        recipients.add(r2);
        recipients.add(r3);

        MailBroadcaster broadcaster = new MailBroadcaster();
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setPort(7777);
        mailSender.setHost("localhost");
        mailSender.setDefaultEncoding("iso-8859-1");
        broadcaster.setMailSender(mailSender);
        broadcaster.setApplicationContext(applicationContext);
        broadcaster.setFrom("'unittest@localhost'");
        broadcaster.setUseLoadBalancing(true);
        broadcaster.setTo(new String[]
        { "'loadbalancer_1@localhost'", "'loadbalancer_2@localhost'" });
        broadcaster.setBody("smsOutVO.nachricht");
        broadcaster.setSubject("handyNr");
        broadcaster.setRecipients(recipients);
        broadcaster.run();
        // Krams wurde durch den anderen Unittests abgedeckt
        List<WiserMessage> messages = wiser.getMessages();
        assertEquals(3, messages.size());
        assertFields(messages.get(0), "1", "loadbalancer_1@localhost");
        assertFields(messages.get(1), "2", "loadbalancer_2@localhost");
        assertFields(messages.get(2), "3", "loadbalancer_1@localhost");

        wiser.stop();
    }

    private void assertFields(WiserMessage _msg, String _neededSubject,
                    String _neededEmail) throws Exception
    {
        MimeMessage mime = _msg.getMimeMessage();

        assertEquals(_neededSubject, mime.getSubject());
        Address[] tos = mime.getRecipients(RecipientType.TO);
        assertEquals(1, tos.length);
        assertEquals(_neededEmail, tos[0].toString());

    }

}
