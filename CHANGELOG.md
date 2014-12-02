To generate change logs run:

```bash
git log --pretty='* %h - %s (%an, %ad)' TAGNAME..HEAD replacing TAGNAME and HEAD as appropriate.

e.g:  git log --pretty='* %h - %s (%an, %ad)' v0.9.1..6f2bc58
```

# 0.9.2 - Fixes for response messages, swagger-core upgrade and excluded transitive scala lib dependencies.

* 6f2bc58 - Updates readme with latest release (Adrian Kelly, Tue Dec 2 20:48:22 2014 +1100)
* b0092b6 - Getting ready to release 0.9.2 (Dilip Krishnan, Tue Dec 2 00:08:23 2014 -0600)
* b975eb7 - [#436] Enum values with JsonValue are not supported (Dilip Krishnan, Mon Dec 1 23:00:54 2014 -0600)
* dad5ab9 - [#494] Add support for JsonGetter and JsonSetter annotations (Dilip Krishnan, Mon Dec 1 22:29:02 2014 -0600)
* 52eeefa - [#438] Reworked the response message readers (Dilip Krishnan, Wed Nov 26 06:15:17 2014 -0600)
* 0e472d7 - Updated dependency versions (Dilip Krishnan, Mon Dec 1 12:02:35 2014 -0600)
* b73eb9b - Setup scala library excludes (Dilip Krishnan, Mon Dec 1 11:46:10 2014 -0600)
* dc68b47 - Update the swagger-core depdendency to a working version (Dilip Krishnan, Fri Nov 28 21:35:32 2014 -0600)
* 338528f - Fixes .gitignore (Adrian Kelly, Thu Nov 27 21:07:34 2014 +1100)
* 30ee175 - Fixed the broken build (Dilip Krishnan, Wed Nov 26 10:17:56 2014 -0600)
* 2fbe0d5 - [#440] Add support for generating models that are represented in the ApiResponse annotations (Dilip Krishnan, Tue Nov 25 21:37:45 2014 -0600)
* 37eb1fb - [#447] Added support for @ApiOperation(hidden=[true|false]) (Dilip Krishnan, Tue Nov 25 21:09:02 2014 -0600)
* 8249102 - [#192] Fixed problem with @JsonUnwrapped not being respected (Dilip Krishnan, Mon Nov 17 20:00:43 2014 -0600)
* 2347dfe - [#506] Fixed media type reader to use default media type (all) when none is specified (Dilip Krishnan, Tue Nov 25 11:09:58 2014 -0600)
* f731a70 - [#469] Fixed the arrays inside generics problem (Dilip Krishnan, Thu Nov 13 22:58:04 2014 -0600)
* d8f49c5 - Updating .gitignore (Dilip Krishnan, Mon Nov 3 15:58:14 2014 -0800)
* ae35239 - Caught up with all the compatible versions (Dilip Krishnan, Fri Sep 26 16:39:18 2014 -0500)
* 052ab09 - Removing build dependency on http-builder (Adrian Kelly, Tue Nov 25 19:01:47 2014 +1100)
* 6816cb2 - Using gradle-travisci-trigger-plugin to trigger downstream demo build (Adrian Kelly, Tue Nov 25 18:38:16 2014 +1100)
* fd87f05 - Bumping version post release (Adrian Kelly, Sun Nov 9 01:40:24 2014 +1100)