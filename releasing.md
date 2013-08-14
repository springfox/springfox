# Releasing
Follow these steps when preparing a release

Note - the maven release plugin is used for managing releases.
This plugin requires that releases be performed from a `-SNAPSHOT` version.

Therefore, pom version numbers should be managed exclusively via the release plugin.


## Branch
Assuming this is the first release of a new release set -- eg., `x.x.0` release:

    mvn release:branch -DbranchName=rel-1.0

Note - the naming convention for branches is `rel-x.x`.  Point-patch releases (the `z` in `x.y.z`) are not included in the branch name.

## Prepare

    mvn release:prepare

## Perform

     mvn release:perform

## Release

Then log on to https://oss.sonatype.org/index.html#stagingRepositories -> Filter to The Mango Factory, and close the listed repo.

Finally, release the Mango Factory repo.
