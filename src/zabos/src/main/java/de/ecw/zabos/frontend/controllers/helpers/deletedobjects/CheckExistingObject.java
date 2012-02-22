package de.ecw.zabos.frontend.controllers.helpers.deletedobjects;

import de.ecw.zabos.sql.vo.BaseIdVO;
import de.ecw.zabos.sql.vo.properties.IPropertyGeloescht;

/**
 * Delegate für {@link IPropertyHandler};
 * 
 * @author ckl
 */
public class CheckExistingObject
{
    private BaseIdVO neuesObjekt;

    private BaseIdVO datenbankObjekt;

    private IPropertyHandler propertyHandler;

    public CheckExistingObject(BaseIdVO _neuesObjekt,
                    BaseIdVO _datenbankObjekt, IPropertyHandler _propertyHandler)
    {
        setNeuesObjekt(_neuesObjekt);
        setDatenbankObjekt(_datenbankObjekt);
        setPropertyHandler(_propertyHandler);
    }

    /**
     * Setzt das Objekt, dass bereits in der Datenbank existiert
     * 
     * @param datenbankObjekt
     */
    final public void setDatenbankObjekt(BaseIdVO datenbankObjekt)
    {
        this.datenbankObjekt = datenbankObjekt;
    }

    /**
     * Liefert das Objekt, dass in der Datenbank existiert
     * 
     * @return
     */
    public BaseIdVO getDatenbankObjekt()
    {
        return datenbankObjekt;
    }

    /**
     * Setzt das Objekt, das momentan in Bearbeitung ist
     * 
     * @param neuesObjekt
     */
    final public void setNeuesObjekt(BaseIdVO neuesObjekt)
    {
        this.neuesObjekt = neuesObjekt;
    }

    /**
     * Liefert das Objekt, das momentan in Bearbeitung ist
     * 
     * @return
     */
    public BaseIdVO getNeuesObjekt()
    {
        return neuesObjekt;
    }

    /**
     * Führt die Überprüfung des Objekts durch. Wenn die IDs von
     * {@link #getNeuesObjekt()} und {@link #getDatenbankObjekt()} nicht
     * identisch sind, wird {@link #handleExistingObject()} aufgerufen.
     */
    public void handle()
    {
        // Beide Objekte sind gesetzt
        if (getNeuesObjekt() != null && getDatenbankObjekt() != null)
        {
            // Das Datenbank-Objekt besitzt eine gültige Id
            if ((getDatenbankObjekt().getBaseId() != null))
            {
                // Standardmäßig wird davon ausgegangen, dass das Objekt neu
                // erstellt werden soll
                long idNeuesObjekt = 0;

                // Das ggw. Objekt besitzt eine ID => Objekt soll geändert
                // werden
                if (getNeuesObjekt().getBaseId() != null)
                {
                    idNeuesObjekt = getNeuesObjekt().getBaseId().getLongValue();
                }

                // Beide Objekte besitzen nicht die selbe ID => Fehler
                if (getDatenbankObjekt().getBaseId().getLongValue() != idNeuesObjekt)
                {
                    handleExistingObject();
                }
            }
        }
    }

    /**
     * Wenn {@link #getDatenbankObjekt()} vom Typ {@link IPropertyGeloescht}
     * ist, das {@link #getDatenbankObjekt()} als gelöscht gekennzeichnet ist
     * und das Umbenennen von bestehenden Datenbankeinträgen über
     * {@link IPropertyHandler#isRenameDeletedObject()} aktiviert ist, wird
     * {@link IPropertyHandler#handleRenameOfObject(BaseIdVO)} aufgerufen.<br />
     * Andernfalls wird die Fehlermeldung über
     * {@link IPropertyHandler#handleErrorOnRenameNotAllowed(BaseIdVO)} erzeugt.
     */
    protected void handleExistingObject()
    {
        if ((getDatenbankObjekt() instanceof IPropertyGeloescht)
                        && ((IPropertyGeloescht) getDatenbankObjekt())
                                        .getGeloescht()
                        && getPropertyHandler().isRenameDeletedObject())
        {
            getPropertyHandler().handleRenameOfObject(getDatenbankObjekt());
        }
        else
        {
            getPropertyHandler().handleErrorOnRenameNotAllowed(
                            getDatenbankObjekt());
        }
    }

    /**
     * Setzt den Handler für die einzelnen Eigenschaften
     * 
     * @param propertyHandler
     */
    final public void setPropertyHandler(IPropertyHandler propertyHandler)
    {
        this.propertyHandler = propertyHandler;
    }

    /**
     * Liefert den Handler für die einzelnen Eigenschaften zurück
     * 
     * @return
     */
    public IPropertyHandler getPropertyHandler()
    {
        return propertyHandler;
    }

    /**
     * Factory-Methode
     * 
     * @param _neuesObjekt
     * @param _datenbankObjekt
     * @param _propertyHandler
     */
    public static void handle(BaseIdVO _neuesObjekt, BaseIdVO _datenbankObjekt,
                    IPropertyHandler _propertyHandler)
    {
        CheckExistingObject ceo = new CheckExistingObject(_neuesObjekt,
                        _datenbankObjekt, _propertyHandler);
        ceo.handle();
    }
}
