package DAG;

/**
 * Derivation of Plain filename, with added attributes for variable name recording.
 * <p>
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://pegasus.isi.edu/schema/DAX" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="StdioType">
 *   &lt;xs:complexContent>
 *     &lt;xs:extension base="ns:PlainFilenameType">
 *       &lt;xs:attribute type="xs:string" use="required" name="varname"/>
 *     &lt;/xs:extension>
 *   &lt;/xs:complexContent>
 * &lt;/xs:complexType>
 * </pre>
 */
public class StdioType extends PlainFilenameType {
    private String varname;

    /**
     * Get the 'varname' attribute value.
     *
     * @return value
     */
    public String getVarname() {
        return varname;
    }

    /**
     * Set the 'varname' attribute value.
     *
     * @param varname
     */
    public void setVarname(String varname) {
        this.varname = varname;
    }
}
