package de.ecw.zabos.broadcast.transport.mail;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import de.ecw.zabos.broadcast.BroadcasterAdapter;
import de.ecw.zabos.broadcast.IBroadcaster;
import de.ecw.zabos.broadcast.Recipient;

/**
 * Verteilt die zu versendenden SMSen per E-Mail als Transportmedium.
 * Hintergrund ist, dass in bestimmten Umgebungen der Zugriff auf das Internet
 * nicht möglich oder nur eingeschränkt über SMTP möglich ist.
 * 
 * <br />
 * Dieser Broadcaster kann z.B. für Exchange 2010 als SMS-Provider eingesetzt
 * werden. <br />
 * Versendete SMSen bekommen den Status
 * {@link IBroadcaster#SMS_STATUS_VERSCHICKT}, da wir nicht wissen können, ob
 * die Nachricht auch wirklich angekommen ist.
 * 
 * @author ckl
 * 
 */
public class MailBroadcaster extends BroadcasterAdapter
{
    private final static Logger log = Logger.getLogger(MailBroadcaster.class);

    private String subject = "'An: ' + handyNr";

    private String[] to = new String[]
    { "'sms-incoming@localhost'" };

    private String body = "";

    private String from = "";

    private JavaMailSender mailSender;

    private boolean useLoadBalancing = false;

    private int idxLoadBalancing = 0;

    public void run()
    {
        List<Recipient> recipients = getRecipients();

        for (Recipient recipient : recipients)
        {
            ExpressionParser parser = new SpelExpressionParser();
            StandardEvaluationContext sec = new StandardEvaluationContext(
                            recipient);

            String[] outTo = getMailRecipients();
            String outSubject = "", outBody = "", outFrom = "";

            try
            {
                for (int i = 0, m = outTo.length; i < m; i++)
                {
                    outTo[i] = parser.parseExpression(outTo[i]).getValue(sec,
                                    String.class);
                }

                outSubject = parser.parseExpression(getSubject()).getValue(sec,
                                String.class);

                outBody = parser.parseExpression(getBody()).getValue(sec,
                                String.class);
                outFrom = parser.parseExpression(getFrom()).getValue(sec,
                                String.class);
                try
                {

                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setSentDate(new Date());
                    message.setTo(outTo);
                    message.setFrom(outFrom);
                    message.setSubject(outSubject);
                    message.setText(outBody);

                    if (getMailSender() == null)
                    {
                        log.error("Es wurde kein Mail-Sender definiert. Mail kann nicht versendet werden");
                        recipient.setStatusCode(SMS_STATUS_GATEWAY_ERROR);
                    }
                    else
                    {
                        getMailSender().send(message);
                    }
                }
                catch (MailException e)
                {
                    recipient.setStatusCode(SMS_STATUS_GATEWAY_ERROR);
                    log.error("Konnte SMS-over-E-Mail an " + recipient
                                    + " nicht versenden: " + e.getMessage());
                }
            }
            catch (ParseException e)
            {
                recipient.setStatusCode(SMS_STATUS_INTERNAL_ERROR);
                log.error("Konnte die Einstellungen der E-Mail nicht parsen. Ueberpruefen Sie die Syntax: "
                                + e.getMessage());
            }

            recipient.setStatusCode(SMS_STATUS_VERSCHICKT);
        }

        if (getFinishBroadcastingListener() != null)
        {
            getFinishBroadcastingListener().finish(recipients);
        }
    }

    /**
     * Liefert die Liste der Empfänger zurück. Es wird überprüft, ob
     * Load-Balancing aktiviert worden ist. Falls dies der Fall ist, wird bei
     * jeder E-Mail der jeweils nächste bzw. erste Empfänger genommen.
     * 
     * @return
     */
    public String[] getMailRecipients()
    {
        if (!isUseLoadBalancing())
        {
            return getTo();
        }

        if (idxLoadBalancing == to.length)
        {
            idxLoadBalancing = 0;
        }

        String r[] =
        { to[idxLoadBalancing] };
        idxLoadBalancing++;
        return r;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setBody(String body)
    {
        this.body = body;
    }

    public String getBody()
    {
        return body;
    }

    public void setFrom(String from)
    {
        this.from = from;
    }

    public String getFrom()
    {
        return from;
    }

    public void setMailSender(JavaMailSender mailSender)
    {
        this.mailSender = mailSender;
    }

    public JavaMailSender getMailSender()
    {
        return mailSender;
    }

    public void setTo(String[] to)
    {
        this.to = to;
    }

    public void setTo(String to)
    {
        this.to = new String[]
        { to };
    }

    public String[] getTo()
    {
        return to;
    }

    public void setUseLoadBalancing(boolean useLoadBalancing)
    {
        this.useLoadBalancing = useLoadBalancing;
    }

    public boolean isUseLoadBalancing()
    {
        return useLoadBalancing;
    }

}
