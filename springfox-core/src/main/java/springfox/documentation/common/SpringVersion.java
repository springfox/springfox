/*
 *
 *  Copyright 2018 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.common;

/**
 * Class providing a proxy for @{@link org.springframework.core.SpringVersion}
 * This enable mocking and testing of specific situations
 * @author dbaje
 */
public class SpringVersion {
    /**
     * Return the full version string of the present Spring codebase,
     * or {@code null} if it cannot be determined.
     * @see Package#getImplementationVersion()
     * @return Spring version
     */
    public Version getVersion() {
        return Version.parse(getVersionString());
    }

    /**
     * Return the full version string of the present Spring codebase,
     * or {@code null} if it cannot be determined.
     * @see Package#getImplementationVersion()
     */
    private String getVersionString() {
        return org.springframework.core.SpringVersion.getVersion();
    }
}