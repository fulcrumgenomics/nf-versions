# Development and Testing

## Testing the Plugin Locally

Execute the following to compile and run unit tests for the plugin:

```
make test
```

To install the plugin for use in local workflows (_e.g._ not internet connected), execute the following:

```
make install
```

## Using the Plugin Locally

Test your changes to the plugin on a Nextflow script like:

```bash
nextflow run <script.nf> -plugins nf-versions@#.#.#-dev
```

## Publishing to GitHub

Read and follow the official documentation on setting up your environment for [plugin release](https://nextflow.io/docs/latest/guides/gradle-plugin.html#publishing-a-plugin).

After bumping the version of the plugin in the file [`build.gradle`](./build.gradle) and making a GitHub Release, execute the following:

```
make release
```
