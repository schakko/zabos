package de.ecw.zabos.frontend;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import de.ecw.zabos.frontend.beans.MessageContainerBean;

/**
 * Validiert HTML-Input-Felder
 * 
 * @author ckl
 */
public class FormValidator
{

    /**
     * Request
     */
    protected HttpServletRequest req = null;

    /**
     * Das Bean, in dem die Fehler gespeichert werden
     */
    protected MessageContainerBean errors = null;

    /**
     * ArrayList<FormObject>
     */
    protected List<FormObject> alFormObjects = new ArrayList<FormObject>();

    /**
     * Der String, der getestet werden soll
     */
    private String testString;

    /**
     * Logging-Instanz
     */
    private final static Logger log = Logger.getLogger(FormValidator.class);

    /**
     * Konstruktor
     * 
     * @param _req
     *            Request
     * @param _errors
     *            ErrorBean
     */
    public FormValidator(HttpServletRequest _req, MessageContainerBean _errors)
    {
        req = _req;
        errors = _errors;
    }

    /**
     * Fügt ein Objekt zum Validieren in die Liste hinzu
     * 
     * @param _f
     */
    public void add(FormObject _f)
    {
        alFormObjects.add(_f);
    }

    /**
     * Durchläuft die Validierung aller Form-Objekte
     */
    public void run()
    {
        boolean bValidationOk = false;
        FormObject f;

        // Alle Objekte durchlaufen
        for (int i = 0, m = alFormObjects.size(); i < m; i++)
        {
            f = (FormObject) alFormObjects.get(i);
            bValidationOk = validate(f);

            // Validierung soll bei einem Fehler abgebrochen werden
            if ((false == bValidationOk)
                            && (f
                                            .testFlag(FormObject.ON_ERROR_STOP_FORM_VALIDATION)))
            {
                break;
            }
        }
    }

    /**
     * Validiert das übergebene Objekt
     * 
     * @param _f
     *            Zu testendes Objekt
     * @return true wenn Validierung ok
     */
    public boolean validate(FormObject _f)
    {
        boolean bValidationOk = true;
        log.debug("FormObject " + _f.getInputFieldName()
                        + " hat Flags gesetzt: " + _f.getFlag());

        if (_f.testFlag(FormObject.NOT_EMPTY))
        {
            bValidationOk = isFilled(_f);

            if ((bValidationOk == false)
                            && (_f
                                            .testFlag(FormObject.ON_ERROR_STOP_OBJECT_VALIDATION)))
            {
                return false;
            }
        }

        if (_f.testFlag(FormObject.IDENTICAL))
        {
            bValidationOk = hasIdenticalValues(_f);

            if ((bValidationOk == false)
                            && (_f
                                            .testFlag(FormObject.ON_ERROR_STOP_OBJECT_VALIDATION)))
            {
                return false;
            }
        }

        if (_f.testFlag(FormObject.HAS_MAXLENGTH))
        {
            bValidationOk = hasMaxLength(_f);

            if ((bValidationOk == false)
                            && (_f
                                            .testFlag(FormObject.ON_ERROR_STOP_OBJECT_VALIDATION)))
            {
                return false;
            }
        }

        if (_f.testFlag(FormObject.HAS_MINLENGTH))
        {
            bValidationOk = hasMinLength(_f);

            if ((bValidationOk == false)
                            && (_f
                                            .testFlag(FormObject.ON_ERROR_STOP_OBJECT_VALIDATION)))
            {
                return false;
            }
        }

        if (_f.testFlag(FormObject.NUMERIC))
        {
            bValidationOk = isNumeric(_f);

            if ((bValidationOk == false)
                            && (_f
                                            .testFlag(FormObject.ON_ERROR_STOP_OBJECT_VALIDATION)))
            {
                return false;
            }
        }

        if (_f.testFlag(FormObject.VALID_DATE_FORMAT))
        {
            bValidationOk = isValidDateFormat(_f);

            if ((bValidationOk == false)
                            && (_f
                                            .testFlag(FormObject.ON_ERROR_STOP_OBJECT_VALIDATION)))
            {
                return false;
            }
        }

        if (_f.testFlag(FormObject.VALID_DATE))
        {
            bValidationOk = isValidDate(_f);

            if ((bValidationOk == false)
                            && (_f
                                            .testFlag(FormObject.ON_ERROR_STOP_OBJECT_VALIDATION)))
            {
                return false;
            }
        }

        if (_f.testFlag(FormObject.VALID_TIME_FORMAT))
        {
            bValidationOk = isValidTimeFormat(_f);

            if ((bValidationOk == false)
                            && (_f
                                            .testFlag(FormObject.ON_ERROR_STOP_OBJECT_VALIDATION)))
            {
                return false;
            }
        }

        if (_f.testFlag(FormObject.VALID_TIME))
        {
            bValidationOk = isValidTime(_f);

            if ((bValidationOk == false)
                            && (_f
                                            .testFlag(FormObject.ON_ERROR_STOP_OBJECT_VALIDATION)))
            {
                return false;
            }
        }

        if (_f.testFlag(FormObject.DISALLOW_RELOAD))
        {
            bValidationOk = isValidSubmit(_f);

            if ((bValidationOk == false)
                            && (_f
                                            .testFlag(FormObject.ON_ERROR_STOP_OBJECT_VALIDATION)))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Überprüft, ob das FormObject _f im Request definiert ist und deren Länge
     * größer als null ist
     * 
     * @param _f
     * @return true|false je nach Ergebnis
     */
    public boolean isFilled(FormObject _f)
    {
        if ((testString = getValue(_f)) != null)
        {
            if (testString.length() > 0)
            {
                return true;
            }
        }

        addError(_f, FormObject.ERROR_IS_EMPTY, createHashtable());
        return false;
    }

    /**
     * Überprüft, ob das FormObjekt _f mit dem FormObject
     * _f.checkAgainstFormObject() vom Inhalt identisch ist
     * 
     * @param _f
     * @return true|false je nach Ergebnis
     */
    public boolean hasIdenticalValues(FormObject _f)
    {
        String testString2;

        if (isFilled(_f.getCheckAgainstFormObject()) == false)
        {
            return false;
        }

        if (((testString = getValue(_f)) != null)
                        && ((testString2 = getValue(_f
                                        .getCheckAgainstFormObject())) != null))
        {
            if (testString.equals(testString2))
            {
                return true;
            }
        }

        Hashtable<String, String> h = createHashtable();
        h.put("fieldName2", _f.getCheckAgainstFormObject().getInputFieldName()
                        .toString());

        addError(_f, FormObject.ERROR_NOT_IDENTICAL, h);
        return false;
    }

    /**
     * Überprüft, ob das FormObject _f eine gültige Zahl ist
     * 
     * @param _f
     * @return true|false je nach Ergebnis
     */
    public boolean isNumeric(FormObject _f)
    {
        if ((testString = getValue(_f)) != null)
        {
            if (Pattern.matches("^(\\d*)$", testString))
            {
                return true;
            }
        }

        addError(_f, FormObject.ERROR_NOT_A_NUMBER, new Hashtable<String, String>());
        return false;
    }

    /**
     * Überprüft, ob das FormObject _f die maximale Zeichenlänge überschreitet
     * 
     * @param _f
     * @return true|false je nach Ergebnis
     */
    public boolean hasMaxLength(FormObject _f)
    {
        if ((testString = getValue(_f)) != null)
        {
            if (testString.length() <= _f.getMaxLength())
            {
                return true;
            }
        }

        Hashtable<String, String> h = createHashtable();
        h.put("maxLength", "" + _f.getMaxLength() + "");

        addError(_f, FormObject.ERROR_EXCEEDS_MAX_LENGTH, h);
        return false;
    }

    /**
     * �berpr�ft, ob das FormObject _f die minimale Zeichenl�nge unterschreitet
     * 
     * @param _f
     * @return true|false je nach Ergebnis
     */
    public boolean hasMinLength(FormObject _f)
    {
        if ((testString = getValue(_f)) != null)
        {
            if (testString.length() >= _f.getMinLength())
            {
                return true;
            }
        }

        Hashtable<String, String> h = createHashtable();
        h.put("minLength", "" + _f.getMinLength() + "");

        addError(_f, FormObject.ERROR_NOT_EXCEEDS_MIN_LENGTH, h);
        return false;
    }

    /**
     * �berpr�ft, ob das angegebene Objekt ein korrektes Datumsformat der Form
     * dd.mm.yyyy besitzt
     * 
     * @param _f
     * @return true|false je nach Ergebnis
     */
    public boolean isValidDateFormat(FormObject _f)
    {
        if ((testString = getValue(_f)) != null)
        {
            if (Pattern.matches("^(\\d\\d)\\.(\\d\\d)\\.(\\d\\d\\d\\d)$",
                            testString))
            {
                return true;
            }
        }

        addError(_f, FormObject.ERROR_NOT_VALID_DATE_FORMAT, null);

        return false;
    }

    /**
     * Überprüft, ob das Datum korrekt angegeben wurde
     * 
     * @param _f
     * @return true|false je nach Ergebnis
     */
    public boolean isValidDate(FormObject _f)
    {
        if ((testString = getValue(_f)) != null)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            java.util.Date testDate = null;

            try
            {
                testDate = sdf.parse(testString);

                if (sdf.format(testDate).equals(testString))
                {
                    return true;
                }
            }
            catch (ParseException e)
            {
            }

        }

        addError(_f, FormObject.ERROR_NOT_VALID_DATE, null);
        return false;
    }

    /**
     * Überprüft, ob das angegebene Objekt ein korrektes Zeitformat der Form
     * HH:ii besitzt
     * 
     * @param _f
     * @return true|false je nach Ergebnis
     */
    public boolean isValidTimeFormat(FormObject _f)
    {
        if ((testString = getValue(_f)) != null)
        {
            if (Pattern.matches("^(\\d\\d):(\\d\\d)$", testString))
            {
                return true;
            }
        }

        addError(_f, FormObject.ERROR_NOT_VALID_TIME_FORMAT, null);

        return false;
    }

    /**
     * Überprüft, ob die Zeitangabe korrekt ist
     * 
     * @param _f
     * @return true|false je nach Ergebnis
     */
    public boolean isValidTime(FormObject _f)
    {
        if ((testString = getValue(_f)) != null)
        {
            Pattern p = Pattern.compile("^(\\d\\d):(\\d\\d)$");
            Matcher m = p.matcher(testString);

            if (m.matches() == true)
            {
                if (Integer.valueOf(m.group(1)) < 24)
                {
                    if (Integer.valueOf(m.group(2)) < 60)
                    {
                        return true;
                    }
                }
            }
        }

        addError(_f, FormObject.ERROR_NOT_VALID_TIME, null);
        return false;
    }

    /**
     * Überprüft, ob ein Reload des Formulars statt gefunden hat. Dient als
     * Reload-Sperre
     * 
     * 2006-05-18 CST Korrekturen 2007-02-06 CKL Anpassung fuer IE 7.x
     * 
     * @param _f
     * @return true|false je nach Ergebnis
     **/
    @SuppressWarnings("unchecked")
    public boolean isValidSubmit(FormObject _f)
    {
        for (Enumeration<String> e = req.getHeaderNames(); e.hasMoreElements();)
        {
            String t = e.nextElement();
            log.debug("Header: " + t + ": " + req.getHeader(t));
        }

        if (
        // Internet Explorer - bei Reload wird immer */* gesendet
        (req.getHeader("accept").equals("*/*")) ||
        // Netscape, bei IE 7.x ist pragma standardmaessig immer auf no-cache
                        ((req.getHeader("pragma") != null) && (!req.getHeader(
                                        "user-agent").contains("MSIE 7."))) ||
                        // Firefox & Opera
                        ((!((req.getHeader("user-agent").contains("MSIE")) || (req
                                        .getHeader("user-agent")
                                        .contains("Internet Explorer")))) && (req
                                        .getHeader("cache-control") != null)))
        {
            log.debug("unallowed refresh");
            addError(_f, FormObject.ERROR_DISALLOW_RELOAD, null);
            return false;
        }

        return true;

    }

    /**
     * Fügt einen Fehler in das ErrorBean hinzu
     * 
     * @param _f
     *            Objekt, dass einen Fehler aufweist
     * @param _errorString
     *            Welcher Fehler aufgetreten ist
     * @param _map
     *            Hashtable mit zusätzlich übergebenen Parametern
     */
    protected void addError(FormObject _f, String _errorString,
                    Map<String, String> _map)
    {
        if (_map == null)
        {
            _map = createHashtable();
        }

        _map.put("fieldName", _f.getInputFieldName());
        _map.put("fieldId", _f.getInputFieldId());

        Iterator<String> e = _map.keySet().iterator();

        while (e.hasNext())
        {
            String key = (String) e.next();
            _errorString = _errorString.replaceAll("\\$\\{" + key + "\\}", _map
                            .get(key).toString());
        }

        // Fehler im Bean setzen
        errors.addMessage(_errorString, _f.getInputFieldId());
    }

    /**
     * Custom-Error-Handler, prinzipiell nur ein Wrapper für addError()
     * 
     * @param _f
     *            Objekt, auf den sich der Fehler bezieht
     * @param _errorString
     *            Fehler, der angezeigt werden soll
     * @param _h
     *            Hashtable mit Argumenten
     */
    public void addCustomError(FormObject _f, String _errorString,
                    Hashtable<String, String> _h)
    {
        this.addError(_f, _errorString, _h);
    }

    /**
     * Liefert die Anzahl der aufgetretenen Fehler
     * 
     * @return Anzahl der Fehler
     */
    public int getTotalErrors()
    {
        return errors.getTotalMessages();
    }

    /**
     * Liefert, ob das Formular Fehler enthält
     * 
     * @return
     */
    public boolean hasErrors()
    {
        return errors.getTotalMessages() > 0;
    }

    /**
     * Überprüft, ob der Parameter gesetzt ist
     * 
     * @param _f
     *            Liefert den Wert des Objekts zurück
     * @return String bzw. null
     */
    public String getValue(FormObject _f)
    {
        if (_f != null)
        {
            if (req.getParameter(_f.getInputFieldId()) != null)
            {
                return req.getParameter(_f.getInputFieldId()).toString();
            }
        }

        return null;
    }

    private Hashtable<String, String> createHashtable()
    {
        return new Hashtable<String, String>();
    }
}
