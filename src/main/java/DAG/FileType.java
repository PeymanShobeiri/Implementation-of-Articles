package DAG;

/**
 * defines the type of files data|executable|pattern
 * <p>
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:simpleType xmlns:ns="http://pegasus.isi.edu/schema/DAX" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="FileType">
 *   &lt;xs:restriction base="xs:string">
 *     &lt;xs:enumeration value="data"/>
 *     &lt;xs:enumeration value="executable"/>
 *     &lt;xs:enumeration value="pattern"/>
 *   &lt;/xs:restriction>
 * &lt;/xs:simpleType>
 * </pre>
 */
public enum FileType {
    DATA("data"), EXECUTABLE("executable"), PATTERN("pattern");
    private final String value;

    private FileType(String value) {
        this.value = value;
    }

    public static FileType convert(String value) {
        for (FileType inst : values()) {
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
