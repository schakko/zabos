package de.ecw.zabos.frontend;

/**
 * Nachrichten-Objekt
 * 
 * @author ckl
 */
public class Message
{
    public Message(String _text)
    {
        setText(_text);
    }

    /**
     * Fehler-Text
     */
    protected String msgText = "Kein Text definiert";

    /**
     * HTML-Objekt, auf das sich die Nachricht bezieht
     */
    protected String field = "";

    /**
     * Liefert den Text
     * 
     * @return
     */
    public String getText()
    {
        return msgText;
    }

    /**
     * Setzt den Text
     * 
     * @param _text
     */
    public void setText(String _text)
    {
        this.msgText = _text;
    }

    /**
     * Liefert das HTML-Feld
     * 
     * @return Feld
     */
    public String getField()
    {
        return field;
    }

    /**
     * Setzt das HTML-Feld
     * 
     * @param _field
     */
    public void setField(String _field)
    {
        this.field = _field;
    }
}
