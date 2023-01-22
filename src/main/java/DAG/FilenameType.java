package DAG;

/**
 * logical filename representation.
 * <p>
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://pegasus.isi.edu/schema/DAX" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="FilenameType">
 *   &lt;xs:complexContent>
 *     &lt;xs:extension base="ns:PlainFilenameType">
 *       &lt;xs:attribute type="xs:string" use="optional" name="temporaryHint"/>
 *       &lt;xs:attribute type="ns:LinkageType" use="optional" name="link"/>
 *       &lt;xs:attribute type="xs:boolean" use="optional" default="false" name="optional"/>
 *       &lt;xs:attribute type="xs:boolean" use="optional" default="true" name="register"/>
 *       &lt;xs:attribute type="ns:TransferType" use="optional" default="true" name="transfer"/>
 *       &lt;xs:attribute type="ns:FileType" use="optional" default="data" name="type"/>
 *       &lt;xs:attribute type="xs:string" use="optional" default="data" name="size"/>
 *     &lt;/xs:extension>
 *   &lt;/xs:complexContent>
 * &lt;/xs:complexType>
 * </pre>
 */
public class FilenameType extends PlainFilenameType {
    private String temporaryHint;
    private LinkageType link;
    private Boolean optional;
    private Boolean register;
    private TransferType transfer;
    private FileType type;
    private String size;

    /**
     * Get the 'temporaryHint' attribute value.
     *
     * @return value
     */
    public String getTemporaryHint() {
        return temporaryHint;
    }

    /**
     * Set the 'temporaryHint' attribute value.
     *
     * @param temporaryHint
     */
    public void setTemporaryHint(String temporaryHint) {
        this.temporaryHint = temporaryHint;
    }

    /**
     * Get the 'link' attribute value.
     *
     * @return value
     */
    public LinkageType getLink() {
        return link;
    }

    /**
     * Set the 'link' attribute value.
     *
     * @param link
     */
    public void setLink(LinkageType link) {
        this.link = link;
    }

    /**
     * Get the 'optional' attribute value.
     *
     * @return value
     */
    public Boolean getOptional() {
        return optional;
    }

    /**
     * Set the 'optional' attribute value.
     *
     * @param optional
     */
    public void setOptional(Boolean optional) {
        this.optional = optional;
    }

    /**
     * Get the 'register' attribute value.
     *
     * @return value
     */
    public Boolean getRegister() {
        return register;
    }

    /**
     * Set the 'register' attribute value.
     *
     * @param register
     */
    public void setRegister(Boolean register) {
        this.register = register;
    }

    /**
     * Get the 'transfer' attribute value.
     *
     * @return value
     */
    public TransferType getTransfer() {
        return transfer;
    }

    /**
     * Set the 'transfer' attribute value.
     *
     * @param transfer
     */
    public void setTransfer(TransferType transfer) {
        this.transfer = transfer;
    }

    /**
     * Get the 'type' attribute value.
     *
     * @return value
     */
    public FileType getType() {
        return type;
    }

    /**
     * Set the 'type' attribute value.
     *
     * @param type
     */
    public void setType(FileType type) {
        this.type = type;
    }

    /**
     * Get the 'size' attribute value.
     *
     * @return value
     */
    public String getSize() {
        return size;
    }

    /**
     * Set the 'size' attribute value.
     *
     * @param size
     */
    public void setSize(String size) {
        this.size = size;
    }
}
