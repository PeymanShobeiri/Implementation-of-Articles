package DAG;

/**
 * defines the tri-state transfer modes.
 * <p>
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:simpleType xmlns:ns="http://pegasus.isi.edu/schema/DAX" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="TransferType">
 *   &lt;xs:restriction base="xs:string">
 *     &lt;xs:enumeration value="false"/>
 *     &lt;xs:enumeration value="optional"/>
 *     &lt;xs:enumeration value="true"/>
 *   &lt;/xs:restriction>
 * &lt;/xs:simpleType>
 * </pre>
 */
public enum TransferType {
    FALSE("false"), OPTIONAL("optional"), TRUE("true");
    private final String value;

    private TransferType(String value) {
        this.value = value;
    }

    public static TransferType convert(String value) {
        for (TransferType inst : values()) {
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
