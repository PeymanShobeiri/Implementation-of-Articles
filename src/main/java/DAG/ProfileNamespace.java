package DAG;

/**
 * defines the legal namespaces of execution profiles.
 * <p>
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:simpleType xmlns:ns="http://pegasus.isi.edu/schema/DAX" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="ProfileNamespace">
 *   &lt;xs:restriction base="xs:string">
 *     &lt;xs:enumeration value="pegasus"/>
 *     &lt;xs:enumeration value="condor"/>
 *     &lt;xs:enumeration value="dagman"/>
 *     &lt;xs:enumeration value="env"/>
 *     &lt;xs:enumeration value="hints"/>
 *     &lt;xs:enumeration value="globus"/>
 *     &lt;xs:enumeration value="selector"/>
 *   &lt;/xs:restriction>
 * &lt;/xs:simpleType>
 * </pre>
 */
public enum ProfileNamespace {
    PEGASUS("pegasus"), CONDOR("condor"), DAGMAN("dagman"), ENV("env"), HINTS(
            "hints"), GLOBUS("globus"), SELECTOR("selector");
    private final String value;

    private ProfileNamespace(String value) {
        this.value = value;
    }

    public static ProfileNamespace convert(String value) {
        for (ProfileNamespace inst : values()) {
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
