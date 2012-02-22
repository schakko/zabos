package de.ecw.zabos.frontend.controllers.helpers.deletedobjects;

import de.ecw.zabos.sql.vo.BaseIdVO;

/**
 * Interface zur Bearbeitung des Falls, dass ein Objekt umbenannt werden soll
 * 
 * @author ckl
 * 
 */
public interface IOnRenameDeletedObject
{
    /**
     * Wird aufgerufen, um ein Update in der Datenbank durchzuführen
     * 
     * @param _datenbankObjekt
     *            Bildet das umbenannte Objekt ab
     */
    public BaseIdVO renameDeletedObject(BaseIdVO _datenbankObjekt);
}
