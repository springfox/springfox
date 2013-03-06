# Releasing
Follow these steps when preparing a release

## Branch
Assuming this is the first release of a new release set -- eg., `x.x.0` release:

    mvn release:branch -DbranchName=rel-1.0

Note - the naming convention for branches is `rel-x.x`.  Point-patch releases (the `z` in `x.y.z`) are not included in the branch name.

## Prepare

    mvn release:prepare -Darguments=-DskipGpgSigning=false

## Perform

     mvn release:perform -Darguments=-DskipGpgSigning=false

## Release

Then log on to https://oss.sonatype.org/index.html#stagingRepositories -> Filter to The Mango Factory, and close the listed repo.

Finally, release the Mango Factory repo.
