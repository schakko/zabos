package de.ecw.zabos.broadcast.transport.http.client;

import java.util.List;

import de.ecw.zabos.broadcast.Recipient;
import de.ecw.zabos.broadcast.transport.http.client.AbstractSmsClient;
import de.ecw.zabos.license.License;

public class SmsClientStub extends AbstractSmsClient
{
    public SmsClientStub(License license)
    {
        super(license);
    }

    @Override
    public String buildSMSRequest(List<Recipient> _r)
    {
        String r = getHeader() + "\n";

        for (int i = 0, m = _r.size(); i < m; i++)
        {
            r += (i + 1) + ":: " + buildRecipient(_r.get(i)) + "\n";
        }

        return r;
    }

    private String buildRecipient(Recipient _r)
    {
        return "Kontext-Alarm:" + _r.getContextAlarm() + ", Kontext-O: "
                        + _r.getContextO() + " Kontext-OE: "
                        + _r.getContextOE() + ", von: "
                        + _r.getAbsenderRufnummer() + ", an: "
                        + _r.getHandyNr().getNummer() + ", Inhalt: "
                        + _r.getNachricht();
    }

    private String getHeader()
    {
        return "Passwort: " + getSmsPasswordMd5() + ", Benutzername: "
                        + getSmsUsername();
    }

}
