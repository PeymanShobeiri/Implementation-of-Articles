package DAG;

/**
 * defines the usage of a logical filename.
 * <p>
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:simpleType xmlns:ns="http://pegasus.isi.edu/schema/DAX" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="LinkageType">
 *   &lt;xs:restriction base="xs:string">
 *     &lt;xs:enumeration value="none"/>
 *     &lt;xs:enumeration value="input"/>
 *     &lt;xs:enumeration value="output"/>
 *     &lt;xs:enumeration value="inout"/>
 *   &lt;/xs:restriction>
 * &lt;/xs:simpleType>
 * </pre>
 */
public enum LinkageType {
    NONE("none"), INPUT("input"), OUTPUT("output"), INOUT("inout");
    private final String value;

    private LinkageType(String value) {
        this.value = value;
    }

    public static LinkageType convert(String value) {
        for (LinkageType inst : values()) {
            if (inst.xmlValue().equals(value)) {
                return inst;
            }
        }
        return null;
    }

    public String xmlValue() {
        return value;
    }
}
