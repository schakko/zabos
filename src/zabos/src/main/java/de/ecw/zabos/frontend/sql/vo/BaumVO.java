package de.ecw.zabos.frontend.sql.vo;

import java.util.ArrayList;
import java.util.List;

import de.ecw.zabos.frontend.beans.DataBean;

/**
 * Dient als Zwischenspeicher für Bäume bzw. Knoten.
 * 
 * @author ckl
 */
public class BaumVO
{
    // Serial
    final static long serialVersionUID = 1209212049;

    private DataBean dataBean = null;

    private List<BaumVO> alSubTree = new ArrayList<BaumVO>();

    private long id = 0;

    private String name = "";

    public BaumVO()
    {
    }

    /**
     * Liefert die Id
     * 
     * @return Id
     */
    public long getId()
    {
        return id;
    }

    /**
     * Setzt die Id
     * 
     * @param _id
     *            Id
     */
    public void setId(long _id)
    {
        this.id = _id;
    }

    /**
     * Liefert den Namen.
     * 
     * @return Name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Setzt den Namen
     * 
     * @param _name
     */
    public void setName(String _name)
    {
        this.name = _name;
    }

    /**
     * Liefert die Anzahl aller Zweige zurück, die dieser Knoten besitzt
     * 
     * @return Anzahl der Zweige
     */
    public int getSizeSubTree()
    {
        return alSubTree.size();
    }

    /**
     * Setzt ein DataBean
     * 
     * @param _dataBean
     */
    public void setDataBean(DataBean _dataBean)
    {
        this.dataBean = _dataBean;
    }

    /**
     * Liefert das DataBean
     * 
     * @return DataBean
     */
    public DataBean getDataBean()
    {
        return this.dataBean;
    }

    /**
     * Liefert die Liste mit allen Zweigen
     * 
     * @return ArrayList
     */
    public List<BaumVO> getSubTree()
    {
        return alSubTree;
    }

    /**
     * Fügt einen Zweig vom Typ BaumVO hinzu
     * 
     * @param _subTree
     */
    public void setSubTree(BaumVO _subTree)
    {
        this.alSubTree.add(_subTree);
    }

}
