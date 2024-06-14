# How to contribute

Bug reports and pull requests from users are what keep this project working.

## Basics

1. Create an issue and describe your idea
2. [Fork it](https://github.com/openmobilehub/android-omh-auth/fork)
3. Create your feature branch (`git checkout -b my-new-feature`)
4. Commit your changes (`git commit -am 'Add some feature'`)
5. Publish the branch (`git push origin my-new-feature`)
6. Create a new Pull Request

## Development

For using the plugins for development locally, there are two approaches.

### Using local modules in the project

This scenario utilizes the local modules (subprojects) residing inside `packages/` to be used in place of dependencies so that between modifying the code in a plugin and running it in the sample app there is no need to publish to Maven.

This project has been pre-configured with such conditional configuration that can be enabled as follows:

- Via root project's `local.properties` (applies both to Android Studio and `gradlew`): add `useLocalProjects=true`
- Via a CLI flag: `./gradlew -PuseLocalProjects=true ...`

### Using Maven Local

This scenario includes primarily three things to be achieved compared to standard development scenario:

- `repositories` need to include `mavenLocal()`
- publishing needs to happen to maven local
- signing needs to be disabled for publishing

This project has been pre-configured with such conditional configuration that can be enabled as follows:

- Via root project's `local.properties` (applies both to Android Studio and `gradlew`): add `useMavenLocal=true`
- Via a CLI flag: `./gradlew -PuseMavenLocal=true ...`

Once you have made a change in any of the `packages/` modules, you must `publishToMavenLocal` in that module in order to see the changes.

#### Publishing to Maven Local

##### Step 1: Publish the plugins to Maven Local

###### With Android Studio

- Open the `Gradle` tab and run the `publishToMavenLocal` for modules: `packages > core`, `packages > plugin-google-gms`, `packages > plugin-google-non-gms`, `packages > plugin-facebook`, `packages > plugin-microsoft` and `packages > plugin-dropbox`:

![gradle-core](https://github.com/openmobilehub/omh-maps/assets/124717244/7a8aeb52-fcf2-4c8c-a0e8-e249e69b3fea)
![gradle-core-gms](https://github.com/openmobilehub/omh-maps/assets/124717244/e5a370d9-1429-4234-a884-b39a23c6dadb)
![gradle-core-ngms](https://github.com/openmobilehub/omh-maps/assets/124717244/2cc52110-8faa-47e3-9298-a6cec846a348)

**Note**: to publish all modules in `packages/`, you can simply run the task `publishToMavenLocal` in the root project. Please also remember to publish the `core` module first.

###### With the CLI:

- first publish `./gradlew :packages:core:publishToMavenLocal`
- then:
  - to publish all other modules: `./gradlew publishToMavenLocal`
  - to publish a selected module: `./gradlew :packages:{module}:publishToMavenLocal`

**Note**: to publish all modules in `packages/`, you can simply run the task `publishToMavenLocal` in the root project directory. Please also remember to publish the `core` module first.

##### Step 2: Verify plugin is published

Go to `/Users/your_user/.m2` dot folder and you'll find the plugin.

##### Step 3: Debug

Add some prints to debug the code

##### Step 4: Test it

Create a sample project, add the plugin and sync the project with gradle and you'll see logs in the `Build` tab in Android Studio.

## Checking your work

You can verify your code with the following tasks:

```
./gradlew assemble
./gradlew detekt
```

## Writing documentation

This project has documentation in a few places:

### Introduction and usage

Friendly `README.md` files written for many audiences:

- [root project](/README.md)
- [core](packages/core/README.md)
- [plugin-google-gms](packages/plugin-google-gms/README.md)
- [plugin-google-non-gms](packages/plugin-google-gms/README.md)
- [plugin-google-facebook](packages/plugin-facebook/README.md)
- [plugin-google-microsoft](packages/plugin-microsoft/README.md)
- [plugin-google-dropbox](packages/plugin-dropbox/README.md)

Readme files belonging to subprojects are included in API documentation auto-generated by Dokka upon PR merge and are included in the module listing pages. API documentation is collected and build by Gradle task `dokkaHtmlMultiModule`.

### Examples and advanced usage

You can find more information in the advanced sections of each package:

- [core](/packages/core/README.md)

Generally, all files that are placed inside `packages/<name>/docs/` are included in the markdown advanced documentation and are collected by custom Gradle task `copyMarkdownDocs`.

## Releasing a new version

1. Clone the repository
2. Update the changelog (and commit it afterwards)
3. Push the changes and wait for the latest CI build to complete
4. Bump the version, create a Git tag and commit the changes
5. Push the version bump commit: `git push`
6. Push the Git tag: `git push --tags`

## Building documentation locally

To build the documentation locally, you can use the `buildDocs` task, which in turn runs the following tasks:

- `dokkaHtmlMultiModule` - generates HTML API documentation for all modules, outputs are written to `/docs/generated/`
- `copyMarkdownDocs` - copies and sanitizes markdown files to be processed by Jekyll to `/docs/markdown/`, into directories named `_<subproject_directory_name>`

To locally view the generated HTML documentation, serve the directory `/docs/generated/` locally with a server of choice (e.g. `python3 -m http.server`).

To locally serve the markdown advanced documentation, it is required to have Ruby >= 3 installed. To run a local Jekyll server leveraging filesystem watching & live reloading, execute in `/docs/markdown` the command `bundle exec jekyll serve`. _Note:_ it is still required that each time you make any changes to the documentation in any of the packages, you run the `copyMarkdownDocs` Gradle task. Otherwise, Jekyll will not be provided the copied markdown files and will not process their new versions.

## Documenting a new module

Provided a new module is introduced to the project, the documentation needs to be configured to include it in auto-generation of HTML documentation.

### API documentation and module-level `README.md`

Each module's (except for `apps/auth-sample`) root `README.md` file will be included by default in Dokka documentation generation automatically and used in its listing page.

The following rules for writing module-level markdown files apply:

- Images can be included as usual, however they are required to be present in module-level `images/` directory to be picked up by Gradle scripts & copied to the correct location in the generated HTML documentation.
- All links to advanced markdown documentation need to be absolute, i.e. point to the full URL under which they are served.
- The root `README.md` files for all projects have to comply with [Dokka file format rules](https://kotlinlang.org/docs/dokka-module-and-package-docs.html#file-format). In case of the OMH project, usually this means that you have to ensure that the first line of a project's top-level readme file is `# Module <project-directory-name>`, e.g. for `packages/plugin-google-gms/README.md` the first line should be: `# Module plugin-google-gms`.

### Markdown advanced documentation

For markdown advanced documentation, the following rules apply to all files:

- Images can be included as usual, however they are required to be present in module-level `images/` directory to be picked up by Gradle scripts & copied to the correct location in the generated HTML documentation.
- Relative links to top-level `README.md` files of other modules (e.g. MD files in `packages/googlemaps/...` to reference `packages/plugin-core/README.md`) are not supported - use absolute (`/packages/...`) paths instead
- Other links to local files can be project-absolute (beginning with `/packages/`) or relative.

Additionally, rules for specific files are presented below.

#### `README.md`

Each module's (except for `apps/auth-sample`) root `README.md` file will NOT be included by default in advanced documentation generation automatically, but will be copied as `_README_ORIGINAL.md` to the documentation build directory. This is so as not to force the inclusion of the [front matter](https://jekyllrb.com/docs/front-matter/) in GitHub display of this file and in API docs generated by Dokka. To include such a README in the module's API docs listing page, please create the following file in module-level `docs/` directory:

```markdown
---
title: Plugin MyPluginName overview
layout: default
nav_order: 1
has_children: false
---

{% include_relative _README_ORIGINAL.md %}
```

This file is a placeholder README that specifies the [front matter](https://jekyllrb.com/docs/front-matter/) and imports the original contents of the "true" README file using the [Jekyll relative file include directive](https://jekyllrb.com/docs/includes/#including-files-relative-to-another-file).

#### Other files

To include a new module's markdown advanced documentation residing in its module-level `docs/` directory, in automatic generation of HTML documentation, Jekyll (static website generator) [configuration in `docs/markdown/_config.yml`](docs/markdown/_config.yml) needs to be updated. Assuming the new module is located in `packages/<<PLUGIN_NAME>>`, it is required to perform the following steps:

1. Add a new collection for the module to `collections`

   ```yaml
   plugin-<<PLUGIN_NAME>>:
     permalink: "/:collection/:path/"
     output: true
   ```

2. Add a new entry to `just_the_docs.collections` (see [theme docs](https://just-the-docs.com/docs/navigation-structure/#grouping-pages-with-collections) for details)

   ```yaml
   plugin-mapbox:
     name: Plugin Mapbox
     nav_fold: false
     nav_order: <<NAV_ORDER>>
   ```

   Where `<<NAV_ORDER>>` is used to specify the ordering of the plugin in the listing of modules in the collection. In the simplest case, it can be the highest `nav_order` + 1.

3. Ensure all `.md` files have a [front matter](https://jekyllrb.com/docs/front-matter/). To start with, you can either browse the existing `.md` files in `packages/` or use one of the following templates:

   - For a parent page (e.g. if this is a `README.md` file in a sub-directory of docs and you want it to be a parent page collecting multiple other sub-pages):

     ```yaml
     ---
     title: Parent page title
     layout: default
     has_children: true
     ---
     ```

   - For a child page:

     ```yaml
     ---
     title: Child page title
     layout: default
     parent: Parent page title # important: not the .md filename, but its front matter title
     ---
     ```

### Technical appendix

Each `.md` file in each module directory from `packages/{module_name}` will be copied to `docs/markdown/_{module_name}`, optionally with trailing directory structure (if present).

For implementation of the helpers backing documentation generation tasks, see [`plugin/docsTasks.gradle.kts`](plugin/docsTasks.gradle.kts).