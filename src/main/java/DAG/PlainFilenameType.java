package DAG;

/**
 * defines just a filename.
 * <p>
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://pegasus.isi.edu/schema/DAX" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="PlainFilenameType">
 *   &lt;xs:attribute type="xs:string" use="required" name="file"/>
 * &lt;/xs:complexType>
 * </pre>
 */
public class PlainFilenameType {
    private String file;

    /**
     * Get the 'file' attribute value.
     *
     * @return value
     */
    public String getFile() {
        return file;
    }

    /**
     * Set the 'file' attribute value.
     *
     * @param file
     */
    public void setFile(String file) {
        this.file = file;
    }
}
