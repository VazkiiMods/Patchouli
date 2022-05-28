# Patchouli Website

This website is built using [Docusaurus 2](https://docusaurus.io/), a modern static website generator.

### Style

Please keep all lines within 90 columns.

### Installation

```
$ yarn
```

### Local Development

```
$ yarn start
```

This command starts a local development server and opens up a browser window. Most changes are reflected live without having to restart the server.

### Build

```
$ yarn build
```

This command generates static content into the `build` directory and can be served using any static contents hosting service.

### Deployment

Automatic deployment is handled by a GitHub Action.

If you need to do a manual deploy for whatever reason, do:

```
$ GIT_USER=<Your GitHub username> USE_SSH=true yarn deploy
```

This will build the website then push it to the `gh-pages` branch of the repository. You
must have push access to this repository to do this, of course.
