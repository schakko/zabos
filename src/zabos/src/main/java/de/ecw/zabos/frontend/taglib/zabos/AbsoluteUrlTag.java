package de.ecw.zabos.frontend.taglib.zabos;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Taglib<br>
 * Hängt an den �bergebenen Parameter url den relativen Pfad von Zabos an.
 * 
 * @author ckl
 */
public class AbsoluteUrlTag extends TagSupport
{
    public final static long serialVersionUID = 123981938;

    public final static String configPath = "/zabos/";

    private String url = "";

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
            out.print(configPath);
            out.print(this.url);
        }
        catch (Exception e)
        {
            throw new Error("Config-Pfad konnte nicht erzeugt werden.");
        }

        return SKIP_BODY;
    }

    /**
     * Setzt den Parameter #url#
     * 
     * @param _url
     *            Parameter
     */
    public void setUrl(String _url)
    {
        this.url = _url;
    }
}
