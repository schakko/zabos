package de.ecw.zabos.sql.cvo;

import java.util.ArrayList;
import java.util.List;

import de.ecw.zabos.frontend.beans.DataBean;

/**
 * Dient als Zwischenspeicher für Bäume bzw. Knoten
 * 
 * @author ckl
 */
public class BaumCVO
{
    // Serial
    public final static long serialVersionUID = 1209212049;

    /**
     * {@link DataBean}
     */
    private DataBean dataBean = null;

    /**
     * Liste mit den zugehörigen Knoten
     */
    private List<BaumCVO> listSubTree = new ArrayList<BaumCVO>();

    /**
     * ID des Eintrags
     */
    private long id = 0;

    /**
     * Name des Eintrags
     */
    private String name = "";

    public BaumCVO()
    {
    }

    /**
     * Liefert die ID
     * 
     * @return
     */
    public long getId()
    {
        return id;
    }

    /**
     * Setzt die ID
     * 
     * @param id
     */
    public void setId(long id)
    {
        this.id = id;
    }

    /**
     * Liefert den Namen
     * 
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * Setzt den Namen
     * 
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Liefert die Anzahl der Knoten
     * 
     * @return
     */
    public int getSizeSubTree()
    {
        return listSubTree.size();
    }

    /**
     * Setzt das {@link DataBean}
     * 
     * @param _dataBean
     */
    public void setDataBean(DataBean _dataBean)
    {
        this.dataBean = _dataBean;
    }

    /**
     * Liefert das {@link DataBean}
     * 
     * @return
     */
    public DataBean getDataBean()
    {
        return this.dataBean;
    }

    /**
     * Liefert die Liste mit den Knoten
     * 
     * @return
     */
    public List<BaumCVO> getSubTree()
    {
        return listSubTree;
    }

    /**
     * Setzt die Liste mit den Knoten
     * 
     * @param subTree
     */
    public void setSubTree(BaumCVO subTree)
    {
        this.listSubTree.add(subTree);
    }

}
