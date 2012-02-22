package de.ecw.zabos.frontend.controllers.helpers.deletedobjects;

import de.ecw.zabos.frontend.FormObject;
import de.ecw.zabos.frontend.FormValidator;
import de.ecw.zabos.frontend.Parameters;
import de.ecw.zabos.frontend.ressources.RequestResources;
import de.ecw.zabos.sql.vo.BaseIdVO;
import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.types.UnixTime;

/**
 * Implementierung für den Fall, dass ein Objekt mit der selben Email bereits in
 * der Datenbank existiert. Die Eigenschaft {@link PersonVO#getEmail()} wird
 * herangezogen.
 * 
 * @author ckl
 * 
 */
public class PropertyHandlerEmailImpl extends AbstractPropertyHandler
{
    public PropertyHandlerEmailImpl(String objektTyp,
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
                        + " mit der Email \""
                        + _oldIdentifier
                        + "\" wurde in \""
                        + _newIdentifier
                        + "\" umbenannt, so dass Sie die angegebene Email-Adresse nutzen können";
    }

    public String createErrorMessage(BaseIdVO _baseIdVO)
    {
        return "Es existiert bereits eine anderes Objekt vom Typ "
                        + getObjektTyp()
                        + " mit der angegebenen Email-Adresse. Wählen Sie bitte eine anderee Email-Adresse.";
    }

    public void handleErrorOnRenameNotAllowed(BaseIdVO _baseIdVO)
    {
        if (getFormValidator() != null)
        {
            getFormValidator().addCustomError(
                            new FormObject(Parameters.TEXT_EMAIL, "Email"),
                            createErrorMessage(_baseIdVO), null);
        }
    }

    public String createNewIdentifier(BaseIdVO _baseIdVO)
    {
        if (_baseIdVO instanceof PersonVO)
        {
            return ((PersonVO) _baseIdVO).getEmail() + "_" + UnixTime.now();

        }

        return "" + UnixTime.now();
    }

    public void setNewIdentifier(BaseIdVO _baseIdVO, String _newIdentifier)
    {
        if (_baseIdVO instanceof PersonVO)
        {
            ((PersonVO) _baseIdVO).setEmail(_newIdentifier);
        }
    }

    public String getIdentifier(BaseIdVO _baseIdVO)
    {
        if (_baseIdVO instanceof PersonVO)
        {
            return ((PersonVO) _baseIdVO).getEmail();
        }

        return "";
    }
}
