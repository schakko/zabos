package de.ecw.zabos.frontend.taglib.zabos;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import de.ecw.zabos.util.StringUtils;

/**
 * Taglib<br>
 * Formatiert ein Timestamp in ein Datums/Zeit-Format
 * 
 * @author ckl
 */
public class FormatNumberToHex extends TagSupport
{
    public static final long serialVersionUID = 3256440326364542774L;

    int number = 0;

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */
    public int doStartTag()
    {
        try
        {
            JspWriter out = pageContext.getOut();
            out.print("0x" + StringUtils.intToHexString(number));
        }
        catch (Exception e)
        {
            throw new Error("Config-Pfad konnte nicht erzeugt werden.");
        }

        return SKIP_BODY;
    }

    /**
     * Setzt den Parameter #number#
     * 
     * @param _number
     */
    public void setNumber(int _number)
    {
        this.number = _number;
    }
}
