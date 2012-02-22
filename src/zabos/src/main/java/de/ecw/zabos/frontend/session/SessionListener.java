package de.ecw.zabos.frontend.session;

import java.io.Serializable;

import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import de.ecw.zabos.frontend.beans.UserBean;
import de.ecw.zabos.frontend.ressources.IRequestResource;

public class SessionListener implements HttpSessionListener,
                HttpSessionActivationListener, Serializable
{

    public final static long serialVersionUID = 12391833;

    public void sessionCreated(HttpSessionEvent event)
    {
        event.getSession().setAttribute(IRequestResource.USER_BEAN,
                        new UserBean());
    }

    public void sessionDestroyed(HttpSessionEvent event)
    {
        // Session-Objekt auf null setzen
        event.getSession().setAttribute(IRequestResource.USER_BEAN, null);
        // Neues Objekt setzen
        event.getSession().setAttribute(IRequestResource.USER_BEAN,
                        new UserBean());
    }

    public void sessionWillPassivate(HttpSessionEvent event)
    {

    }

    public void sessionDidActivate(HttpSessionEvent event)
    {
    }
}
