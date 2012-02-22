package de.ecw.zabos.sql.vo;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.sql.Scheme;
import de.ecw.zabos.types.UnixTime;
import de.ecw.zabos.types.id.BaseId;
import de.ecw.zabos.types.id.FuenfTonId;

/**
 * ValueObject fuer {@link Scheme#FUENFTON_TABLE}
 * 
 * @author bsp
 * 
 */
public class FuenfTonVO implements BaseIdVO
{
    FuenfTonVO()
    {

    }

    private FuenfTonId id;

    private String folge;

    private UnixTime zeitpunkt;

    public BaseId getBaseId()
    {
        return id;
    }

    public FuenfTonId getFuenfTonId()
    {
        return id;
    }

    /**
     * @param _fuenfTonId
     * @throws StdException
     *             Wenn ID null
     */
    public void setFuenfTonId(FuenfTonId _fuenfTonId) throws StdException
    {
        if (_fuenfTonId == null)
        {
            throw new StdException("primary key darf nicht null sein");
        }
        id = _fuenfTonId;
    }

    public String getFolge()
    {
        return folge;
    }

    /**
     * 
     * @param _folge
     * @throws StdException
     *             Wenn _folge null
     */
    public void setFolge(String _folge) throws StdException
    {
        if (_folge == null)
        {
            throw new StdException("folge darf nicht null sein");
        }
        folge = _folge;
    }

    public UnixTime getZeitpunkt()
    {
        return zeitpunkt;
    }

    public void setZeitpunkt(UnixTime _zeitpunkt) throws StdException
    {
        if (_zeitpunkt == null)
        {
            throw new StdException("zeitpunkt darf nicht null sein");
        }
        zeitpunkt = _zeitpunkt;
    }

}
