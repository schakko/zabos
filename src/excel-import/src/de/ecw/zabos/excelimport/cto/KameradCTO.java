package de.ecw.zabos.excelimport.cto;

import de.ecw.zabos.sql.vo.PersonVO;
import de.ecw.zabos.sql.vo.TelefonVO;

public class KameradCTO
{

	private PersonVO person;

	private TelefonVO telefon;

	private String[] zugehoerigeSchleifen;

	/**
	 * @return Returns the person.
	 */
	public PersonVO getPerson()
	{
		return person;
	}

	/**
	 * @param person The person to set.
	 */
	public void setPerson(PersonVO person)
	{
		this.person = person;
	}

	/**
	 * @return Returns the telefon.
	 */
	public TelefonVO getTelefon()
	{
		return telefon;
	}

	/**
	 * @param telefon The telefon to set.
	 */
	public void setTelefon(TelefonVO telefon)
	{
		this.telefon = telefon;
	}

	/**
	 * @return Returns the zugehoerigeSchleifen.
	 */
	public String[] getZugehoerigeSchleifen()
	{
		return zugehoerigeSchleifen;
	}

	/**
	 * @param zugehoerigeSchleifen The zugehoerigeSchleifen to set.
	 */
	public void setZugehoerigeSchleifen(String[] zugehoerigeSchleifen)
	{
		this.zugehoerigeSchleifen = zugehoerigeSchleifen;
	}
}
