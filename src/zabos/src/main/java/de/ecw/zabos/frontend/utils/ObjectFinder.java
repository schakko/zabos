package de.ecw.zabos.frontend.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.ecw.zabos.sql.vo.BaseIdVO;
import de.ecw.zabos.types.id.BaseId;

/**
 * Finder zum Laden von Objekten anhand der ID
 * 
 * @author ckl
 * 
 */
public class ObjectFinder
{
    /**
     * ArrayList mit den einzelnen Objekten
     */
    private List<BaseIdVO> listElements = new ArrayList<BaseIdVO>();

    /**
     * Lookup-Table; Beziehung Id -> Objekt
     */
    private Map<BaseId, BaseIdVO> mapLookupTable = new HashMap<BaseId, BaseIdVO>();

    /**
     * Konstruktor
     * 
     * @param _baseIds
     */
    public ObjectFinder(BaseIdVO[] _baseIds)
    {
        buildLookupTable(_baseIds);
    }

    /**
     * Erstellt die Lookup-Tabelle
     * 
     * @param _baseIds
     */
    private void buildLookupTable(BaseIdVO[] _baseIds)
    {
        if (_baseIds == null)
        {
            return;
        }

        for (int i = 0, m = _baseIds.length; i < m; i++)
        {
            listElements.add(_baseIds[i]);
        }
    }

    /**
     * Findet das Element mit der angegebenen ID
     * 
     * @param _id
     * @return
     */
    public BaseIdVO findElement(BaseId _id)
    {
        if (mapLookupTable.containsKey(_id))
        {
            return mapLookupTable.get(_id);
        }

        boolean bFound = false;

        for (int i = 0, m = listElements.size(); i < m; i++)
        {
            BaseIdVO elem = listElements.get(i);

            if (elem.getBaseId().equals(_id))
            {
                mapLookupTable.put(elem.getBaseId(), elem);
                bFound = true;
                break;
            }
        }

        if (!bFound)
        {
            mapLookupTable.put(_id, null);
        }

        return mapLookupTable.get(_id);
    }
}
