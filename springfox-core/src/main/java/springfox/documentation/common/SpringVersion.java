package springfox.documentation.common;

/**
 * Class providing a proxy for @{@link org.springframework.core.SpringVersion}
 * This enable mocking and testing of specific situations
 */
public class SpringVersion {
    /**
     * Return the full version string of the present Spring codebase,
     * or {@code null} if it cannot be determined.
     * @see Package#getImplementationVersion()
     */
    public Version getVersion() {
        return Version.parse(org.springframework.core.SpringVersion.getVersion());
    }
}