package de.ecw.zabos.excelimport.cto;

import java.util.HashMap;

import de.ecw.zabos.sql.vo.SchleifeVO;

public class DataCTO
{
	private SchleifeVO[] schleifen = null;

	private KameradCTO[] kameraden = null;

	private HashMap<String,String> hmLookupSchleifeFuenfton = null;
	
	/**
	 * @return Returns the kameraden.
	 */
	public KameradCTO[] getKameraden()
	{
		return kameraden;
	}

	/**
	 * @param kameraden
	 *          The kameraden to set.
	 */
	public void setKameraden(KameradCTO[] kameraden)
	{
		this.kameraden = kameraden;
	}

	/**
	 * @return Returns the schleifen.
	 */
	public SchleifeVO[] getSchleifen()
	{
		return schleifen;
	}

	/**
	 * @param schleifen The schleifen to set.
	 */
	public void setSchleifen(SchleifeVO[] schleifen)
	{
		this.schleifen = schleifen;
	}

	/**
	 * <Schleifennummer, Fünfton>
	 * @return Returns the hmLookupSchleifeFuenfton.
	 */
	public HashMap<String, String> getHmLookupSchleifeFuenfton()
	{
		return hmLookupSchleifeFuenfton;
	}

	/**
	 * @param hmLookupSchleifeFuenfton The hmLookupSchleifeFuenfton to set.
	 */
	public void setHmLookupSchleifeFuenfton(HashMap<String, String> hmLookupSchleifeFuenfton)
	{
		this.hmLookupSchleifeFuenfton = hmLookupSchleifeFuenfton;
	}
}
