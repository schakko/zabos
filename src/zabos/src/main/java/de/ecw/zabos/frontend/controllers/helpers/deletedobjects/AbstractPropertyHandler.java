package de.ecw.zabos.frontend.controllers.helpers.deletedobjects;

import de.ecw.zabos.frontend.FormValidator;
import de.ecw.zabos.frontend.ressources.RequestResources;
import de.ecw.zabos.sql.vo.BaseIdVO;

/**
 * Abstrakte Klasse, die die Getter von {@link IPropertyHandler} implementiert
 * 
 * @author ckl
 * 
 */
abstract public class AbstractPropertyHandler implements IPropertyHandler
{
    private boolean renameDeletedObject;

    private IOnRenameDeletedObject onRenameDeletedObject;

    private String objektTyp;

    private FormValidator formValidator;

    private RequestResources requestResources;

    public AbstractPropertyHandler(String _objektTyp,
                    boolean _renameDeletedObject,
                    IOnRenameDeletedObject _onRenameDeletedObject,
                    FormValidator _formValidator, RequestResources _req)
    {
        setObjektTyp(_objektTyp);
        setRenameDeletedObject(_renameDeletedObject);
        setOnRenameDeletedObject(_onRenameDeletedObject);
        setFormValidator(_formValidator);
        setRequestResources(_req);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.controllers.helpers.deletedobjects.IPropertyHandler
     * #createRenameMessage(java.lang.String, java.lang.String)
     */
    abstract public String createRenameMessage(String _oldIdentifier,
                    String _newIdentifier);

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.controllers.helpers.deletedobjects.IPropertyHandler
     * #createErrorMessage(de.ecw.zabos.sql.vo.BaseIdVO)
     */
    abstract public String createErrorMessage(BaseIdVO _baseIdVO);

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.controllers.helpers.deletedobjects.IPropertyHandler
     * #handleErrorOnRenameNotAllowed(de.ecw.zabos.sql.vo.BaseIdVO)
     */
    abstract public void handleErrorOnRenameNotAllowed(BaseIdVO _baseIdVO);

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.controllers.helpers.deletedobjects.IPropertyHandler
     * #createNewIdentifier(de.ecw.zabos.sql.vo.BaseIdVO)
     */
    abstract public String createNewIdentifier(BaseIdVO _baseIdVO);

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.controllers.helpers.deletedobjects.IPropertyHandler
     * #setNewIdentifier(de.ecw.zabos.sql.vo.BaseIdVO, java.lang.String)
     */
    abstract public void setNewIdentifier(BaseIdVO _baseIdVO, String _newName);

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.controllers.helpers.deletedobjects.IPropertyHandler
     * #handleRenameOfObject(de.ecw.zabos.sql.vo.BaseIdVO)
     */
    public void handleRenameOfObject(BaseIdVO _baseIdVO)
    {
        String oldIdentifier = getIdentifier(_baseIdVO);
        String newIdentifier = createNewIdentifier(_baseIdVO);
        setNewIdentifier(_baseIdVO, newIdentifier);

        BaseIdVO renamedObject = getOnRenameDeletedObject()
                        .renameDeletedObject(_baseIdVO);

        if (renamedObject == null)
        {
            getRequestResources()
                            .getErrorBean()
                            .addMessage(
                                            "Das bestehende Datenbankelement konnte anscheinend nicht umbenannt werden");
        }
        else
        {
            getRequestResources().getInfoBean().addMessage(
                            createRenameMessage(oldIdentifier, newIdentifier));
        }
    }

    final public void setRenameDeletedObject(boolean renameDeletedObject)
    {
        this.renameDeletedObject = renameDeletedObject;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.controllers.helpers.deletedobjects.IPropertyHandler
     * #isRenameDeletedObject()
     */
    public boolean isRenameDeletedObject()
    {
        return renameDeletedObject;
    }

    final public void setOnRenameDeletedObject(
                    IOnRenameDeletedObject onRenameDeletedObject)
    {
        this.onRenameDeletedObject = onRenameDeletedObject;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.controllers.helpers.deletedobjects.IPropertyHandler
     * #getOnRenameDeletedObject()
     */
    public IOnRenameDeletedObject getOnRenameDeletedObject()
    {
        return onRenameDeletedObject;
    }

    final public void setObjektTyp(String objektTyp)
    {
        this.objektTyp = objektTyp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.controllers.helpers.deletedobjects.IPropertyHandler
     * #getObjektTyp()
     */
    public String getObjektTyp()
    {
        return objektTyp;
    }

    final public void setFormValidator(FormValidator formValidator)
    {
        this.formValidator = formValidator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ecw.zabos.frontend.controllers.helpers.deletedobjects.IPropertyHandler
     * #getFormValidator()
     */
    public FormValidator getFormValidator()
    {
        return formValidator;
    }

    final public void setRequestResources(RequestResources requestResources)
    {
        this.requestResources = requestResources;
    }

    public RequestResources getRequestResources()
    {
        return requestResources;
    }

}
