package de.ecw.report.types;

/**
 * Defines a type of report
 * 
 * @author ckl
 */
final public class ReportType
{
    /**
     * unique hash of report
     */
    private String hash;

    /**
     * description of report
     */
    private String description;

    /**
     * Construcotr
     * 
     * @param _hash
     * @param _description
     */
    public ReportType(String _hash, String _description)
    {
        setHash(_hash);
        setDescription(_description);
    }

    /**
     * Set description
     * 
     * @param description
     */
    public void setDescription(String _description)
    {
        this.description = _description;
    }

    /**
     * Get description
     * 
     * @return
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Set unique hash
     * 
     * @param hash
     */
    public void setHash(String _hash)
    {
        this.hash = _hash;
    }

    /**
     * Get unique hash
     * 
     * @return
     */
    public String getHash()
    {
        return hash;
    }
}
