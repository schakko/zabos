package de.ecw.zabos.frontend.controllers.helpers.deletedobjects;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.FormObject;
import de.ecw.zabos.frontend.FormValidator;
import de.ecw.zabos.frontend.Parameters;
import de.ecw.zabos.frontend.ressources.RequestResources;
import de.ecw.zabos.sql.vo.BaseIdVO;
import de.ecw.zabos.sql.vo.properties.IPropertyKuerzel;
import de.ecw.zabos.types.UnixTime;

/**
 * Implementierung für den Fall, dass ein Objekt mit dem selben Kürzel bereits
 * in der Datenbank existiert. Die Eigenschaft {@link IPropertyKuerzel} wird
 * herangezogen.
 * 
 * @author ckl
 * 
 */
public class PropertyHandlerKuerzelImpl extends AbstractPropertyHandler
{
    public PropertyHandlerKuerzelImpl(String objektTyp,
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
                        + " mit dem Kürzel \""
                        + _oldIdentifier
                        + "\" wurde in \""
                        + _newIdentifier
                        + "\" umbenannt, so dass Sie das angegebene Kürzel nutzen können";
    }

    public String createErrorMessage(BaseIdVO _baseIdVO)
    {
        return "Es existiert bereits eine anderes Objekt vom Typ "
                        + getObjektTyp()
                        + " mit dem angegebenem Kürzel. Wählen Sie bitte ein anderes Kürzel.";
    }

    public void handleErrorOnRenameNotAllowed(BaseIdVO _baseIdVO)
    {
        if (getFormValidator() != null)
        {
            getFormValidator().addCustomError(
                            new FormObject(Parameters.TEXT_KUERZEL, "Kürzel"),
                            createErrorMessage(_baseIdVO), null);
        }
    }

    public String createNewIdentifier(BaseIdVO _baseIdVO)
    {
        if (_baseIdVO instanceof IPropertyKuerzel)
        {
            return ((IPropertyKuerzel) _baseIdVO).getKuerzel() + "_"
                            + UnixTime.now();

        }

        return "" + UnixTime.now();
    }

    public void setNewIdentifier(BaseIdVO _baseIdVO, String _newIdentifier)
    {
        if (_baseIdVO instanceof IPropertyKuerzel)
        {
            try
            {
                ((IPropertyKuerzel) _baseIdVO).setKuerzel(_newIdentifier);
            }
            catch (StdException e)
            {
            }
        }
    }

    public String getIdentifier(BaseIdVO _baseIdVO)
    {
        if (_baseIdVO instanceof IPropertyKuerzel)
        {
            return ((IPropertyKuerzel) _baseIdVO).getKuerzel();
        }

        return "";
    }
}
