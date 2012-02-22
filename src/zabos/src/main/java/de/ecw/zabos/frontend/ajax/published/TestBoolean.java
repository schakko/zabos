package de.ecw.zabos.frontend.ajax.published;

import org.json.JSONException;
import org.json.JSONObject;

import de.ecw.zabos.exceptions.StdException;
import de.ecw.zabos.frontend.ajax.AbstractAjaxMethodAdapter;
import de.ecw.zabos.frontend.ressources.RequestResources;
import de.ecw.zabos.sql.resource.DBResource;

/**
 * Testet den Boolean
 * 
 * @return JSON-Objekt
 */
public class TestBoolean extends AbstractAjaxMethodAdapter
{
    public TestBoolean(final DBResource db)
    {
        super(db);
    }

    public JSONObject run(RequestResources req, JSONObject jsonRequest) throws JSONException, StdException
    {
        JSONObject r = new JSONObject();
        boolean value = jsonRequest.getBoolean("VALUE");

        r.put("originalValueAsString", jsonRequest.get("VALUE"));
        r.put("originalValue", value);
        r.put("invertedValue", !value);

        return r;
    }
}
