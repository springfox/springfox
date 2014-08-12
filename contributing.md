## Some general guidelines 

### Here are some great resources 
[Github Flow](https://guides.github.com/introduction/flow/index.html)

[Creating Effective Pull Requests](http://codeinthehole.com/writing/pull-requests-and-other-good-practices-for-teams-using-github/)

Here is an example of [a great pull request](https://github.com/bitly/dablooms/pull/19)

### Before starting to work on a PR

- Create an issue with the problem description **_BEFORE_** creating a PR directly. Provide examples if possible. This 
gives an opportunity for the library authors to prioritize the work stream as well.
- Describe the approach you'd like to use to solve the problem. This way the library authors will have a better idea 
when it comes to reviewing the PR. Its also a good way to vet ideas out, in case there is a simpler solution.

### While working on the PR
While working on the PR, here are some suggestions to make submitting a PR fun for everyone! 

- Before starting on a feature or a bug, if you see some cleanup opportunities upfront do them all and group these 
into one or more logical commits. Definitely if there are whitespace changes I get them out of the way. That way the 
code reviewer can focus on reviewing what has actually changed, without having to open up the IDE.
- Never mix class rename/move changes with changes to the class itself.
- Break up your commits into chunks of work. Group logical changes together into one commit. This gives a history and 
context as to what the thought processes is or how you arrived at the final solution
- If you encounter significant cleanup required mid-way through your work try to isolate that as a separate commit. 
This is not to say ever single minute cleanup needs to be in its own commit, just the large chunks of cleanup.
- Importantly, make sure there are tests that cover the change.
- Finally clean up all the merge commits by rebaseing against the source repo often.

Along the lines of the _agile manifesto_:

- Prefer separate commits over one big change
- Prefer isolating code cleanup/formatting issues on a separate commit over mixing it with the commit that addresses 
an issue
- Prefer not squashing commits/re-writing history after it has been reviewed
- Prefer squashing commits like "fixed broken test", "oops I forgot to add this before I pushed it" to having the 
commit noise
- Prefer squashing merge commits for sure