package de.ecw.zabos.frontend.controllers.helpers.deletedobjects;

import de.ecw.zabos.frontend.FormValidator;
import de.ecw.zabos.sql.vo.BaseIdVO;

/**
 * Behandelt die unterschiedlichen Eigenschaften eines Objekts
 * 
 * @author ckl
 * 
 */
public interface IPropertyHandler
{
    /**
     * Erstellt eine Nachricht, dass ein Objekt von _oldIdentifier nach
     * _newIdentifier umbenannt worden ist
     * 
     * @param _oldIdentifier
     * @param _newIdentifier
     * @return
     */
    public String createRenameMessage(String _oldIdentifier,
                    String _newIdentifier);

    /**
     * Erstellt die Fehlermeldung, dass bereits ein Objekt mit der angegebenen
     * Eigenschaft existiert
     * 
     * @param _baseIdVO
     * @return
     */
    public String createErrorMessage(BaseIdVO _baseIdVO);

    /**
     * Für den Fall, dass die Umbenennung eines bestehenden Datenbank-Objekts
     * nicht durchgeführt werden soll, wird diese Methode aufgerufen
     * 
     * @param _baseIdVO
     */
    public void handleErrorOnRenameNotAllowed(BaseIdVO _baseIdVO);

    /**
     * Erstellt eine neue Bezeichnung für ein Objekts. Standardmäßig geschieht
     * dies, in dem der Timestamp des aktuellen Datums an die Eigenschaft
     * gehangen wird
     * 
     * @param _baseIdVO
     * @return
     */
    public String createNewIdentifier(BaseIdVO _baseIdVO);

    /**
     * Setzt die neue Bezeichnung für das Objekt
     * 
     * @param _baseIdVO
     * @param _newName
     */
    public void setNewIdentifier(BaseIdVO _baseIdVO, String _newName);

    /**
     * Für den Fall, dass das Objekt umbenannt werden soll, wird diese Methode
     * aufgerufen
     * 
     * @param _baseIdVO
     */
    public void handleRenameOfObject(BaseIdVO _baseIdVO);

    /**
     * Liefert zurück, ob das Umbenennen von Objekten erlaubt ist
     * 
     * @return
     */
    public boolean isRenameDeletedObject();

    /**
     * Liefert den Handler, der beim Umbenennen von Objekten aufgerufen wird
     * 
     * @return
     */
    public IOnRenameDeletedObject getOnRenameDeletedObject();

    /**
     * Liefert den Namen des Objekts {@link #createNewIdentifier(BaseIdVO)} und
     * {@link #createErrorMessage(BaseIdVO)} beziehen sich auf diesen Namen.
     * 
     * @return
     */
    public String getObjektTyp();

    /**
     * Liefert den {@link FormValidator}
     * 
     * @return
     */
    public FormValidator getFormValidator();

    /**
     * Liefert den Bezeichner zurück
     * 
     * @param
     * @return
     */
    public String getIdentifier(BaseIdVO _baseIdVO);

}