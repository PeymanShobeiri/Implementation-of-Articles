package DAG;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * abstract DAG in XML
 * <p>
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:element xmlns:ns="http://pegasus.isi.edu/schema/DAX" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="adag">
 *   &lt;xs:complexType>
 *     &lt;xs:sequence>
 *       &lt;xs:element name="filename" minOccurs="0" maxOccurs="unbounded">
 *         &lt;!-- Reference to inner class Filename -->
 *       &lt;/xs:element>
 *       &lt;xs:element name="job" maxOccurs="unbounded">
 *         &lt;!-- Reference to inner class Job -->
 *       &lt;/xs:element>
 *       &lt;xs:element name="child" minOccurs="0" maxOccurs="unbounded">
 *         &lt;!-- Reference to inner class Child -->
 *       &lt;/xs:element>
 *     &lt;/xs:sequence>
 *     &lt;xs:attribute type="xs:string" use="required" name="version"/>
 *     &lt;xs:attribute type="xs:string" use="required" name="name"/>
 *     &lt;xs:attribute type="xs:integer" use="required" name="index"/>
 *     &lt;xs:attribute type="xs:integer" use="required" name="count"/>
 *     &lt;xs:attribute type="xs:string" use="optional" name="jobCount"/>
 *     &lt;xs:attribute type="xs:integer" use="optional" name="fileCount"/>
 *     &lt;xs:attribute type="xs:integer" use="optional" name="childCount"/>
 *   &lt;/xs:complexType>
 * &lt;/xs:element>
 * </pre>
 */
public class Adag {
    private List<Filename> filenameList = new ArrayList<Filename>();
    private List<Job> jobList = new ArrayList<Job>();
    private List<Child> childList = new ArrayList<Child>();
    private String version;
    private String name;
    private BigInteger index;
    private BigInteger count;
    private String jobCount;
    private BigInteger fileCount;
    private BigInteger childCount;

    /**
     * Get the list of 'filename' element items.
     *
     * @return list
     */
    public List<Filename> getFilenameList() {
        return filenameList;
    }

    /**
     * Set the list of 'filename' element items.
     *
     * @param list
     */
    public void setFilenameList(List<Filename> list) {
        filenameList = list;
    }

    /**
     * Get the list of 'job' element items.
     *
     * @return list
     */
    public List<Job> getJobList() {
        return jobList;
    }

    /**
     * Set the list of 'job' element items.
     *
     * @param list
     */
    public void setJobList(List<Job> list) {
        jobList = list;
    }

    /**
     * Get the list of 'child' element items.
     *
     * @return list
     */
    public List<Child> getChildList() {
        return childList;
    }

    /**
     * Set the list of 'child' element items.
     *
     * @param list
     */
    public void setChildList(List<Child> list) {
        childList = list;
    }

    /**
     * Get the 'version' attribute value.
     *
     * @return value
     */
    public String getVersion() {
        return version;
    }

    /**
     * Set the 'version' attribute value.
     *
     * @param version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Get the 'name' attribute value.
     *
     * @return value
     */
    public String getName() {
        return name;
    }

    /**
     * Set the 'name' attribute value.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the 'index' attribute value.
     *
     * @return value
     */
    public BigInteger getIndex() {
        return index;
    }

    /**
     * Set the 'index' attribute value.
     *
     * @param index
     */
    public void setIndex(BigInteger index) {
        this.index = index;
    }

    /**
     * Get the 'count' attribute value.
     *
     * @return value
     */
    public BigInteger getCount() {
        return count;
    }

    /**
     * Set the 'count' attribute value.
     *
     * @param count
     */
    public void setCount(BigInteger count) {
        this.count = count;
    }

    /**
     * Get the 'jobCount' attribute value.
     *
     * @return value
     */
    public String getJobCount() {
        return jobCount;
    }

    /**
     * Set the 'jobCount' attribute value.
     *
     * @param jobCount
     */
    public void setJobCount(String jobCount) {
        this.jobCount = jobCount;
    }

    /**
     * Get the 'fileCount' attribute value.
     *
     * @return value
     */
    public BigInteger getFileCount() {
        return fileCount;
    }

    /**
     * Set the 'fileCount' attribute value.
     *
     * @param fileCount
     */
    public void setFileCount(BigInteger fileCount) {
        this.fileCount = fileCount;
    }

    /**
     * Get the 'childCount' attribute value.
     *
     * @return value
     */
    public BigInteger getChildCount() {
        return childCount;
    }

    /**
     * Set the 'childCount' attribute value.
     *
     * @param childCount
     */
    public void setChildCount(BigInteger childCount) {
        this.childCount = childCount;
    }

    /**
     * List of all filenames used.
     * <p>
     * Schema fragment(s) for this class:
     * <pre>
     * &lt;xs:element xmlns:ns="http://pegasus.isi.edu/schema/DAX" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="filename" minOccurs="0" maxOccurs="unbounded">
     *   &lt;xs:complexType>
     *     &lt;xs:complexContent>
     *       &lt;xs:extension base="ns:PlainFilenameType">
     *         &lt;xs:attribute type="ns:LinkageType" use="optional" name="link"/>
     *         &lt;xs:attribute type="xs:boolean" use="optional" default="false" name="optional"/>
     *       &lt;/xs:extension>
     *     &lt;/xs:complexContent>
     *   &lt;/xs:complexType>
     * &lt;/xs:element>
     * </pre>
     */
    public static class Filename extends PlainFilenameType {
        private LinkageType link;
        private Boolean optional;

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
    }

    /**
     * Job specification in terms of a logical transformation.
     * <p>
     * Schema fragment(s) for this class:
     * <pre>
     * &lt;xs:element xmlns:ns="http://pegasus.isi.edu/schema/DAX" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="job" maxOccurs="unbounded">
     *   &lt;xs:complexType>
     *     &lt;xs:sequence>
     *       &lt;xs:element name="argument" minOccurs="0">
     *         &lt;!-- Reference to inner class Argument -->
     *       &lt;/xs:element>
     *       &lt;xs:element name="profile" minOccurs="0" maxOccurs="unbounded">
     *         &lt;!-- Reference to inner class Profile -->
     *       &lt;/xs:element>
     *       &lt;xs:element name="stdin" minOccurs="0">
     *         &lt;!-- Reference to inner class Stdin -->
     *       &lt;/xs:element>
     *       &lt;xs:element name="stdout" minOccurs="0">
     *         &lt;!-- Reference to inner class Stdout -->
     *       &lt;/xs:element>
     *       &lt;xs:element name="stderr" minOccurs="0">
     *         &lt;!-- Reference to inner class Stderr -->
     *       &lt;/xs:element>
     *       &lt;xs:element type="ns:FilenameType" name="uses" minOccurs="0" maxOccurs="unbounded"/>
     *     &lt;/xs:sequence>
     *     &lt;xs:attribute type="xs:string" use="optional" name="namespace"/>
     *     &lt;xs:attribute type="xs:string" use="required" name="name"/>
     *     &lt;xs:attribute type="xs:string" use="optional" name="version"/>
     *     &lt;xs:attribute type="xs:string" use="optional" name="dv-namespace"/>
     *     &lt;xs:attribute type="xs:string" use="optional" name="dv-name"/>
     *     &lt;xs:attribute type="xs:string" use="optional" name="dv-version"/>
     *     &lt;xs:attribute type="xs:string" use="required" name="id"/>
     *     &lt;xs:attribute type="xs:string" use="optional" name="runtime"/>
     *     &lt;xs:attribute type="xs:integer" use="optional" name="level"/>
     *     &lt;xs:attribute type="xs:string" use="optional" name="compound"/>
     *   &lt;/xs:complexType>
     * &lt;/xs:element>
     * </pre>
     */
    public static class Job {
        private Argument argument;
        private List<Profile> profileList = new ArrayList<Profile>();
        private Stdin stdin;
        private Stdout stdout;
        private Stderr stderr;
        private List<FilenameType> useList = new ArrayList<FilenameType>();
        private String namespace;
        private String name;
        private String version;
        private String dvNamespace;
        private String dvName;
        private String dvVersion;
        private String id;
        private String runtime;
        private BigInteger level;
        private String compound;

        /**
         * Get the 'argument' element value.
         *
         * @return value
         */
        public Argument getArgument() {
            return argument;
        }

        /**
         * Set the 'argument' element value.
         *
         * @param argument
         */
        public void setArgument(Argument argument) {
            this.argument = argument;
        }

        /**
         * Get the list of 'profile' element items.
         *
         * @return list
         */
        public List<Profile> getProfileList() {
            return profileList;
        }

        /**
         * Set the list of 'profile' element items.
         *
         * @param list
         */
        public void setProfileList(List<Profile> list) {
            profileList = list;
        }

        /**
         * Get the 'stdin' element value.
         *
         * @return value
         */
        public Stdin getStdin() {
            return stdin;
        }

        /**
         * Set the 'stdin' element value.
         *
         * @param stdin
         */
        public void setStdin(Stdin stdin) {
            this.stdin = stdin;
        }

        /**
         * Get the 'stdout' element value.
         *
         * @return value
         */
        public Stdout getStdout() {
            return stdout;
        }

        /**
         * Set the 'stdout' element value.
         *
         * @param stdout
         */
        public void setStdout(Stdout stdout) {
            this.stdout = stdout;
        }

        /**
         * Get the 'stderr' element value.
         *
         * @return value
         */
        public Stderr getStderr() {
            return stderr;
        }

        /**
         * Set the 'stderr' element value.
         *
         * @param stderr
         */
        public void setStderr(Stderr stderr) {
            this.stderr = stderr;
        }

        /**
         * Get the list of 'uses' element items.
         *
         * @return list
         */
        public List<FilenameType> getUseList() {
            return useList;
        }

        /**
         * Set the list of 'uses' element items.
         *
         * @param list
         */
        public void setUseList(List<FilenameType> list) {
            useList = list;
        }

        /**
         * Get the 'namespace' attribute value.
         *
         * @return value
         */
        public String getNamespace() {
            return namespace;
        }

        /**
         * Set the 'namespace' attribute value.
         *
         * @param namespace
         */
        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        /**
         * Get the 'name' attribute value.
         *
         * @return value
         */
        public String getName() {
            return name;
        }

        /**
         * Set the 'name' attribute value.
         *
         * @param name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Get the 'version' attribute value.
         *
         * @return value
         */
        public String getVersion() {
            return version;
        }

        /**
         * Set the 'version' attribute value.
         *
         * @param version
         */
        public void setVersion(String version) {
            this.version = version;
        }

        /**
         * Get the 'dv-namespace' attribute value.
         *
         * @return value
         */
        public String getDvNamespace() {
            return dvNamespace;
        }

        /**
         * Set the 'dv-namespace' attribute value.
         *
         * @param dvNamespace
         */
        public void setDvNamespace(String dvNamespace) {
            this.dvNamespace = dvNamespace;
        }

        /**
         * Get the 'dv-name' attribute value.
         *
         * @return value
         */
        public String getDvName() {
            return dvName;
        }

        /**
         * Set the 'dv-name' attribute value.
         *
         * @param dvName
         */
        public void setDvName(String dvName) {
            this.dvName = dvName;
        }

        /**
         * Get the 'dv-version' attribute value.
         *
         * @return value
         */
        public String getDvVersion() {
            return dvVersion;
        }

        /**
         * Set the 'dv-version' attribute value.
         *
         * @param dvVersion
         */
        public void setDvVersion(String dvVersion) {
            this.dvVersion = dvVersion;
        }

        /**
         * Get the 'id' attribute value.
         *
         * @return value
         */
        public String getId() {
            return id;
        }

        /**
         * Set the 'id' attribute value.
         *
         * @param id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * Get the 'runtime' attribute value.
         *
         * @return value
         */
        public String getRuntime() {
            return runtime;
        }

        /**
         * Set the 'runtime' attribute value.
         *
         * @param runtime
         */
        public void setRuntime(String runtime) {
            this.runtime = runtime;
        }

        /**
         * Get the 'level' attribute value. Level from search in router.
         *
         * @return value
         */
        public BigInteger getLevel() {
            return level;
        }

        /**
         * Set the 'level' attribute value. Level from search in router.
         *
         * @param level
         */
        public void setLevel(BigInteger level) {
            this.level = level;
        }

        /**
         * Get the 'compound' attribute value.
         *
         * @return value
         */
        public String getCompound() {
            return compound;
        }

        /**
         * Set the 'compound' attribute value.
         *
         * @param compound
         */
        public void setCompound(String compound) {
            this.compound = compound;
        }

        /**
         * Arguments on the commandline, text interrupted by filenames
         * <p>
         * Schema fragment(s) for this class:
         * <pre>
         * &lt;xs:element xmlns:ns="http://pegasus.isi.edu/schema/DAX" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="argument" minOccurs="0">
         *   &lt;xs:complexType mixed="true">
         *     &lt;xs:sequence>
         *       &lt;xs:element type="ns:PlainFilenameType" name="filename" minOccurs="0" maxOccurs="unbounded"/>
         *     &lt;/xs:sequence>
         *   &lt;/xs:complexType>
         * &lt;/xs:element>
         * </pre>
         */
        public static class Argument {
            private List<PlainFilenameType> filenameList = new ArrayList<PlainFilenameType>();

            /**
             * Get the list of 'filename' element items.
             *
             * @return list
             */
            public List<PlainFilenameType> getFilenameList() {
                return filenameList;
            }

            /**
             * Set the list of 'filename' element items.
             *
             * @param list
             */
            public void setFilenameList(List<PlainFilenameType> list) {
                filenameList = list;
            }
        }

        /**
         * Execution environment specific data to be passed to lower levels.
         * <p>
         * Schema fragment(s) for this class:
         * <pre>
         * &lt;xs:element xmlns:ns="http://pegasus.isi.edu/schema/DAX" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="profile" minOccurs="0" maxOccurs="unbounded">
         *   &lt;xs:complexType mixed="true">
         *     &lt;xs:sequence>
         *       &lt;xs:element type="ns:PlainFilenameType" name="filename" minOccurs="0" maxOccurs="unbounded"/>
         *     &lt;/xs:sequence>
         *     &lt;xs:attribute type="xs:string" use="required" name="key"/>
         *     &lt;xs:attribute type="ns:ProfileNamespace" use="required" name="namespace"/>
         *     &lt;xs:attribute type="xs:string" use="optional" default="vdl" name="origin"/>
         *   &lt;/xs:complexType>
         * &lt;/xs:element>
         * </pre>
         */
        public static class Profile {
            private List<PlainFilenameType> filenameList = new ArrayList<PlainFilenameType>();
            private String key;
            private ProfileNamespace namespace;
            private String origin;

            /**
             * Get the list of 'filename' element items.
             *
             * @return list
             */
            public List<PlainFilenameType> getFilenameList() {
                return filenameList;
            }

            /**
             * Set the list of 'filename' element items.
             *
             * @param list
             */
            public void setFilenameList(List<PlainFilenameType> list) {
                filenameList = list;
            }

            /**
             * Get the 'key' attribute value.
             *
             * @return value
             */
            public String getKey() {
                return key;
            }

            /**
             * Set the 'key' attribute value.
             *
             * @param key
             */
            public void setKey(String key) {
                this.key = key;
            }

            /**
             * Get the 'namespace' attribute value.
             *
             * @return value
             */
            public ProfileNamespace getNamespace() {
                return namespace;
            }

            /**
             * Set the 'namespace' attribute value.
             *
             * @param namespace
             */
            public void setNamespace(ProfileNamespace namespace) {
                this.namespace = namespace;
            }

            /**
             * Get the 'origin' attribute value.
             *
             * @return value
             */
            public String getOrigin() {
                return origin;
            }

            /**
             * Set the 'origin' attribute value.
             *
             * @param origin
             */
            public void setOrigin(String origin) {
                this.origin = origin;
            }
        }

        /**
         * stand-in for "filename", linkage is "input" fixed.
         * <p>
         * Schema fragment(s) for this class:
         * <pre>
         * &lt;xs:element xmlns:ns="http://pegasus.isi.edu/schema/DAX" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="stdin" minOccurs="0">
         *   &lt;xs:complexType>
         *     &lt;xs:complexContent>
         *       &lt;xs:extension base="ns:StdioType">
         *         &lt;xs:attribute type="ns:LinkageType" use="optional" fixed="input" name="link"/>
         *       &lt;/xs:extension>
         *     &lt;/xs:complexContent>
         *   &lt;/xs:complexType>
         * &lt;/xs:element>
         * </pre>
         */
        public static class Stdin extends StdioType {
            private LinkageType link;

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
        }

        /**
         * stand-in for "filename", linkage is "output" fixed.
         * <p>
         * Schema fragment(s) for this class:
         * <pre>
         * &lt;xs:element xmlns:ns="http://pegasus.isi.edu/schema/DAX" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="stdout" minOccurs="0">
         *   &lt;xs:complexType>
         *     &lt;xs:complexContent>
         *       &lt;xs:extension base="ns:StdioType">
         *         &lt;xs:attribute type="ns:LinkageType" use="optional" fixed="output" name="link"/>
         *       &lt;/xs:extension>
         *     &lt;/xs:complexContent>
         *   &lt;/xs:complexType>
         * &lt;/xs:element>
         * </pre>
         */
        public static class Stdout extends StdioType {
            private LinkageType link;

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
        }

        /**
         * stand-in for "filename", linkage is "output" fixed.
         * <p>
         * Schema fragment(s) for this class:
         * <pre>
         * &lt;xs:element xmlns:ns="http://pegasus.isi.edu/schema/DAX" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="stderr" minOccurs="0">
         *   &lt;xs:complexType>
         *     &lt;xs:complexContent>
         *       &lt;xs:extension base="ns:StdioType">
         *         &lt;xs:attribute type="ns:LinkageType" use="optional" fixed="output" name="link"/>
         *       &lt;/xs:extension>
         *     &lt;/xs:complexContent>
         *   &lt;/xs:complexType>
         * &lt;/xs:element>
         * </pre>
         */
        public static class Stderr extends StdioType {
            private LinkageType link;

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
        }
    }

    /**
     * List of dependencies.
     * <p>
     * Schema fragment(s) for this class:
     * <pre>
     * &lt;xs:element xmlns:ns="http://pegasus.isi.edu/schema/DAX" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="child" minOccurs="0" maxOccurs="unbounded">
     *   &lt;xs:complexType>
     *     &lt;xs:sequence>
     *       &lt;xs:element name="parent" maxOccurs="unbounded">
     *         &lt;!-- Reference to inner class Parent -->
     *       &lt;/xs:element>
     *     &lt;/xs:sequence>
     *     &lt;xs:attribute type="xs:string" use="required" name="ref"/>
     *   &lt;/xs:complexType>
     * &lt;/xs:element>
     * </pre>
     */
    public static class Child {
        private List<Parent> parentList = new ArrayList<Parent>();
        private String ref;

        /**
         * Get the list of 'parent' element items.
         *
         * @return list
         */
        public List<Parent> getParentList() {
            return parentList;
        }

        /**
         * Set the list of 'parent' element items.
         *
         * @param list
         */
        public void setParentList(List<Parent> list) {
            parentList = list;
        }

        /**
         * Get the 'ref' attribute value.
         *
         * @return value
         */
        public String getRef() {
            return ref;
        }

        /**
         * Set the 'ref' attribute value.
         *
         * @param ref
         */
        public void setRef(String ref) {
            this.ref = ref;
        }

        /**
         * parent node refering to a job.
         * <p>
         * Schema fragment(s) for this class:
         * <pre>
         * &lt;xs:element xmlns:ns="http://pegasus.isi.edu/schema/DAX" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="parent" maxOccurs="unbounded">
         *   &lt;xs:complexType>
         *     &lt;xs:attribute type="xs:string" use="required" name="ref"/>
         *   &lt;/xs:complexType>
         * &lt;/xs:element>
         * </pre>
         */
        public static class Parent {
            private String ref;

            /**
             * Get the 'ref' attribute value.
             *
             * @return value
             */
            public String getRef() {
                return ref;
            }

            /**
             * Set the 'ref' attribute value.
             *
             * @param ref
             */
            public void setRef(String ref) {
                this.ref = ref;
            }
        }
    }
}
