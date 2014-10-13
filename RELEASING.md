### Releasing 

- Follow the release process outlined here : https://github.com/martypitt/swagger-springmvc/issues/422  

- Tag the newly created release

```bash 

git tag -a <version_number> -m "<version_number>."

git push origin <version_number>

```

- Re-run the demo app to verify the published release is available in jcenter:

https://travis-ci.org/adrianbk/swagger-springmvc-demo