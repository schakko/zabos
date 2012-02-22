package de.ecw.zabos.frontend.beans;

import java.util.ArrayList;
import java.util.List;

import de.ecw.zabos.sql.vo.BaseIdDescVO;
import de.ecw.zabos.types.id.BaseId;

/**
 * Das DataBean nimmt Objekte jedes beliebigen Typs auf und speichert sie
 * zwischen.<br>
 * Damit können verschiedene Daten in den JSP-Seiten iteriert werden.
 * 
 * @author ckl
 */
public class DataBean
{
    // ArrayList<Object>
    private List<Object> alData = new ArrayList<Object>();

    /**
     * Liefert die Daten
     * 
     * @return alData
     */
    public List<Object> getData()
    {
        return alData;
    }

    /**
     * Setzt Daten in das JavaBean. Die Daten müssen als Array von einem
     * beliebigen Typ übergeben werden
     * 
     * @param _c
     *            zu setzende Daten
     */
    public void setData(Object[] _c)
    {
        if (null != _c)
        {
            for (int i = 0, m = _c.length; i < m; i++)
            {
                setData(_c[i]);
            }
        }
    }

    /**
     * Setzt ein Objekt in die Liste, wenn die Referenz bereits in der Liste
     * ist, wird sie nicht hinzugefügt
     * 
     * @param _o
     *            zu setzende Daten
     */
    public void setData(Object _o)
    {
        if (_o != null)
        {
            if (alData.contains(_o) == false)
            {
                alData.add(_o);
            }
        }
    }

    /**
     * Liefert die Größe des DataBeans zurück
     * 
     * @return
     */
    public int getSize()
    {
        return alData.size();
    }

    /**
     * Überprüft, ob das Objekt mit der angegebenen Id bereits in der Liste
     * existiert.<br>
     * Es wird nur nach der Id geschaut und es können nur Objekte in der Liste
     * überprüft werden, die von BaseIdDescVO erben (alle VOs)
     * 
     * @param _id
     *            zu überprüfende Id
     * @return true|false
     */
    public boolean isObjectInList(BaseId _id)
    {
        BaseIdDescVO objectToCheck;

        for (int i = 0, m = alData.size(); i < m; i++)
        {
            if (alData.get(i) instanceof BaseIdDescVO)
            {
                objectToCheck = (BaseIdDescVO) alData.get(i);

                // ueberpruefen, ob die Ids identisch sind
                if (objectToCheck.getBaseId().getLongValue() == _id
                                .getLongValue())
                {
                    return true;
                }
            }
        }

        return false;
    }
}
