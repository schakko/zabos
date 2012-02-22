package de.ecw.zabos.frontend.objects.fassade;

import de.ecw.zabos.types.UnixTime;

/**
 * H�lt Lizenz-Informationen vor
 * 
 * @author ckl
 * 
 */
public class LizenzFassade
{
    private UnixTime ablaufDatum = null;

    private UnixTime ausstellungsDatum = null;

    private int kundenNummer = 0;

    private short maxSchleifen = 0;

    private short maxPersonen = 0;

    private long curPersonen = 0;

    private long curSchleifen = 0;

    private String version = "";

    public UnixTime getAblaufDatum()
    {
        return ablaufDatum;
    }

    public void setAblaufDatum(UnixTime ablaufDatum)
    {
        this.ablaufDatum = ablaufDatum;
    }

    public UnixTime getAusstellungsDatum()
    {
        return ausstellungsDatum;
    }

    public void setAusstellungsDatum(UnixTime ausstellungsDatum)
    {
        this.ausstellungsDatum = ausstellungsDatum;
    }

    public long getCurPersonen()
    {
        return curPersonen;
    }

    public void setCurPersonen(long curPersonen)
    {
        this.curPersonen = curPersonen;
    }

    public long getCurSchleifen()
    {
        return curSchleifen;
    }

    public void setCurSchleifen(long curSchleifen)
    {
        this.curSchleifen = curSchleifen;
    }

    public int getKundenNummer()
    {
        return kundenNummer;
    }

    public void setKundenNummer(int kundenNummer)
    {
        this.kundenNummer = kundenNummer;
    }

    public short getMaxPersonen()
    {
        return maxPersonen;
    }

    public void setMaxPersonen(short maxPersonen)
    {
        this.maxPersonen = maxPersonen;
    }

    public short getMaxSchleifen()
    {
        return maxSchleifen;
    }

    public void setMaxSchleifen(short maxSchleifen)
    {
        this.maxSchleifen = maxSchleifen;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }
}
