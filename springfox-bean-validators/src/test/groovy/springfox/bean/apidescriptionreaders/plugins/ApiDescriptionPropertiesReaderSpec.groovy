package springfox.bean.apidescriptionreaders.plugins

import spock.lang.Specification

class ApiDescriptionPropertiesReaderSpec extends Specification {
    //Default file
    private static final String KEY_1 = "test.property.1"
    private static final String KEY_2 = "test.property.2"
    private static final String KEY_3 = "test.property.3"
    private static final String VALUE_KEY_1 = "Value1"
    private static final String VALUE_KEY_2 = "Value2"
    private static final String VALUE_KEY_3 = "Value3"

    //Other path file
    private static final String OTHER_PATH = "/api_description_otherPath.properties"
    private static final String KEY_4 = "test.property.4"
    private static final String KEY_5 = "test.property.5"
    private static final String KEY_6 = "test.property.6"
    private static final String VALUE_KEY_4 = "Value4"
    private static final String VALUE_KEY_5 = "Value5"
    private static final String VALUE_KEY_6 = "Value6"

    //Test other paths
    private static final String RELATIVE_PATH = "samePackage.properties"
    private static final String RELATIVE_PATH_UP = "mypackage/relativeFolderUp.properties"
    private static final String RELATIVE_PATH_FROM_ABSOLUTE_PATH = "/mypackage/../relativePackageDown.properties"
    private static final String ABSOLUTE_PATH = OTHER_PATH
    private static final String OTHER_ABSOLUTE_PATH = "/" + RELATIVE_PATH_UP

    private static final String RANDOM_KEY = UUID.randomUUID().toString()

    def "Instantiate but not initialized"() {
        def reader = new ApiDescriptionPropertiesReader()
        expect:
        value == reader.getProperty(key)
        where:
        value | key
        null  | KEY_1
        null  | KEY_2
        null  | KEY_3
        null  | KEY_4
        null  | KEY_5
        null  | KEY_6
        null  | RANDOM_KEY
    }

    def "Cannot initialized - File not found"() {
        def reader = new ApiDescriptionPropertiesReader()

        when:
        reader.setPropertyFilePath("fileNotFound")
        reader.init()
        then:
        thrown(NullPointerException)
    }

    def "Cannot initialized - File not specified"() {
        def reader = new ApiDescriptionPropertiesReader()

        when:
        reader.setPropertyFilePath(null)
        reader.init()
        then:
        thrown(NullPointerException)
    }

    def "Can load absolute paths"() {
        given:
        def reader = new ApiDescriptionPropertiesReader()
        when:
        reader.setPropertyFilePath(file)
        reader.init()
        then:
        noExceptionThrown()
        where:
        file << [ABSOLUTE_PATH, OTHER_ABSOLUTE_PATH]
    }

    def "Can't load relative paths"() {
        given:
        def reader = new ApiDescriptionPropertiesReader()
        when:
        reader.setPropertyFilePath(file)
        reader.init()
        then:
        thrown(NullPointerException)
        where:
        file << [RELATIVE_PATH, RELATIVE_PATH_UP, RELATIVE_PATH_FROM_ABSOLUTE_PATH]
    }

    def "Initialized with default file"() {
        def reader = new ApiDescriptionPropertiesReader()
        when:
        reader.init()
        then:
        value == reader.getProperty(key)
        where:
        value       | key
        VALUE_KEY_1 | KEY_1
        VALUE_KEY_2 | KEY_2
        VALUE_KEY_3 | KEY_3
        null        | KEY_4
        null        | KEY_5
        null        | KEY_6
        null        | RANDOM_KEY
    }

    def "Initialized with a specific file"() {
        def reader = new ApiDescriptionPropertiesReader()
        when:
        reader.setPropertyFilePath(OTHER_PATH)
        reader.init()
        then:
        value == reader.getProperty(key)
        where:
        value       | key
        null        | KEY_1
        null        | KEY_2
        null        | KEY_3
        VALUE_KEY_4 | KEY_4
        VALUE_KEY_5 | KEY_5
        VALUE_KEY_6 | KEY_6
        null        | RANDOM_KEY
    }

    def "File path is saved"() {
        def reader = new ApiDescriptionPropertiesReader()
        expect:
        reader.setPropertyFilePath(fileExpected)
        fileExpected == reader.getPropertyFilePath()
        where:
        fileExpected << [
                "somePath",
                "otherPath",
                "it doesn't care about path format",
                "",
                null]
    }


}
