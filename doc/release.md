# Release information process

To release the plugin, you need first to create a file gradle.properties in ~/.gradle containing the following information:

```
nexusUrl=https://oss.sonatype.org
nexusUsername=
nexusPassword=
signing.secretKeyRingFile=
signing.keyId=
signing.password=
```

See: https://github.com/INRIA/spoon/blob/master/doc/_release/Release.md to get the proper values. 

Then, type `./gradlew uploadArchive` and wait for the upload to finish.
Go to https://oss.sonatype.org, log in, then go to Staging repositories and find the automatically created folder (usually name something like frinriagforge-[0-9]*).
When the upload is finished, checkbox the folder and click on "close" in the upper bound of the window. Wait (again !) for the process to finish (you may need to click on refresh several times...).

If some errors happened fix them (Thanks Captain Obvious !), and else click on release button on the right of close. 

Finally create a git tag and push it. 