package de.ecw.zabos.frontend.controllers.helpers.deletedobjects;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.FormObject;
import de.ecw.zabos.frontend.FormValidator;
import de.ecw.zabos.frontend.Parameters;
import de.ecw.zabos.frontend.ressources.RequestResources;
import de.ecw.zabos.sql.vo.BaseIdVO;
import de.ecw.zabos.sql.vo.properties.IPropertyName;
import de.ecw.zabos.types.UnixTime;

/**
 * Implementierung für den Fall, dass ein Objekt mit dem selben Namen bereits in
 * der Datenbank existiert. Die Eigenschaft {@link IPropertyName} wird
 * herangezogen.
 * 
 * @author ckl
 * 
 */
public class PropertyHandlerNameImpl extends AbstractPropertyHandler
{
    public PropertyHandlerNameImpl(String objektTyp,
                    boolean renameDeletedObject,
                    IOnRenameDeletedObject onRenameDeletedObject,
                    FormValidator formValidator, RequestResources _req)
    {
        super(objektTyp, renameDeletedObject, onRenameDeletedObject,
                        formValidator, _req);
    }

    public String createRenameMessage(String _oldIdentifier,
                    String _newIdentifier)
    {
        return "Das gelöschte Objekt vom Typ "
                        + getObjektTyp()
                        + " mit dem Namen \""
                        + _oldIdentifier
                        + "\" wurde in \""
                        + _newIdentifier
                        + "\" umbenannt, so dass Sie den angegebenen Namen nutzen können";
    }

    public String createErrorMessage(BaseIdVO _baseIdVO)
    {
        return "Es existiert bereits eine anderes Objekt vom Typ "
                        + getObjektTyp()
                        + " mit dem angegebenem Namen. Wählen Sie bitte einen anderen Namen";
    }

    public void handleErrorOnRenameNotAllowed(BaseIdVO _baseIdVO)
    {
        if (getFormValidator() != null)
        {
            getFormValidator().addCustomError(
                            new FormObject(Parameters.TEXT_NAME, "Name"),
                            createErrorMessage(_baseIdVO), null);
        }
    }

    public String createNewIdentifier(BaseIdVO _baseIdVO)
    {
        if (_baseIdVO instanceof IPropertyName)
        {
            return ((IPropertyName) _baseIdVO).getName() + "_" + UnixTime.now();

        }

        return "" + UnixTime.now();
    }

    public void setNewIdentifier(BaseIdVO _baseIdVO, String _newIdentifier)
    {
        if (_baseIdVO instanceof IPropertyName)
        {
            try
            {
                ((IPropertyName) _baseIdVO).setName(_newIdentifier);
            }
            catch (StdException e)
            {
            }
        }
    }

    public String getIdentifier(BaseIdVO _baseIdVO)
    {
        if (_baseIdVO instanceof IPropertyName)
        {
            return ((IPropertyName) _baseIdVO).getName();
        }

        return "";
    }
}
