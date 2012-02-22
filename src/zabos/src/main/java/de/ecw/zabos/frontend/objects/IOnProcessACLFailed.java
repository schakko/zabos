package de.ecw.zabos.frontend.objects;

import de.ecw.zabos.frontend.dispatchers.FrontendDispatcher;
import de.ecw.zabos.frontend.ressources.RequestResources;

public interface IOnProcessACLFailed
{
    /**
     * Wird ausgeführt, nachdem die Richtlinienbestimmung zur Überprüfung des
     * Logins nicht durchgesetzt werden konnte. Danach wird die Anfrage
     * abgebrochen, der Controller wird nicht weiterverarbeitet.
     * 
     * @param _controller
     * @param _frontendDispatcher
     * @param _req
     */
    public void onProcessACLFailed(IBaseController _controller,
                    FrontendDispatcher _frontendDispatcher,
                    RequestResources _req);
}
