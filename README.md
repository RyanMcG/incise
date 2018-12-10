# ![inciꞅe](https://raw.github.com/RyanMcG/incise/master/website/content/assets/images/logo.png) [![Build Status](https://travis-ci.org/RyanMcG/incise-core.png?branch=master)](https://travis-ci.org/RyanMcG/incise-core)

<span class="tag-line">An extensible static site generator written in
Clojure.</span>

<!-- Unfortunately, this is duplicated in src/incise/transformers/impl/incise_layout.clj -->
[**Source**][incise-source] **|**
[**API**][incise-api] **|**
[**Extensibility**][extensibility]

## Get excited (or don't)

One more static website generator might not be the world's most exciting development. That being said, if the following items sound like good ideas to you then
you may like *incise*:

* Use Clojure ✓
* Allow many different setups and configurations ✓
* Extensible by nature ✓
* Batteries included, but only if you want them. ✓
    * `incise` is just a collection of extensions on top of `incise-core`.

#### Examples

* [This website][incise]
* [My personal website][blog]
* [The official example project][ex-project]

Of course these examples are sort of meaningless, in that *incise* does not help you
create content or designs---it is simply a mechanism for finding files
dispatching them to functions based on file extensions. This pattern happens to
be pretty powerful and batteries are included to actually make it useful too.

---

## Configuration

*Incise* is primarily configured via a config file it tries to find as a resource.
This file must be named `incise.edn` and be found in the root of your resources directory.
You can override that file and pass in the `-c` or `--config` option to *incise's* main function to specify an alternative location of the config file.

An [example `incise.edn`][incise.edn.example] file is included with this project as well as the `incise.edn` file used to generate [this website][incise].

## Usage

The easiest way to use *incise* is with the [Leiningen][].
Just add the following alias to your `project.clj`.

```clojure
:dependencies [[incise "0.5.0"]]
:aliases {"incise" ^:pass-through-help ["run" "-m" "incise.core"]}
```

Let's see what that gets us.

```bash
lein incise help
```

All command line options can also be specified in your `incise.edn`.
For instance, if you want the default log level to be `:info` instead of `:warn`.

```clojure
;; In your incise.edn
:log-level :info
```

Which would be like running commands with the `--log-level info` option.

```bash
lein incise --log-level info
```

## Methods

The default method is to serve.
This launches a ring powered webserver that will parse all content on first request, and re-parse them when they are modified.
It also launches an nREPL server so clients who want one do not need to startup a separate process to have it.

```bash
lein incise
# Or to be explicit
lein incise serve
```

You can also generate all content by parsing all parsable files in the input directory using the once command.

```bash
lein incise once
```

Finally, as mentioned above, you can use your configured deployment method.

```bash
lein incise deploy
```

---

## Extensibility

*Incise* has been designed to be extensible.
In order to make this extensibility as broad as possible there are five different types of extensions.

1.  A parser: `incise.parsers.impl.*`

    Parsers are good for generating output files from input files.
    That sounds really vague, but that's because it could be used for many things.
    The most common usecase is generating HTML from an input file.
    For instance, there is a markdown parser which takes a markdown input file and generates an html file as a result.

2.  A transformer: `incise.transformers.impl.*`

    Transformers are just functions that take a Parse and return a Parse (typically after making some change to it).
    They are an entirely optional feature that some parsers use when generating content (like adding layouts to HTML content).
    Transformers may be specified project wide via `:parse-defaults` or per file being parsed.

3.  A once fixture: `incise.once.fixtures.impl.*`

    A once fixture is used for wrapping `once` in with additional behaviour.
    For instance, if you want an asset pipeline like stefon, the [`incise-stefon`][incise-stefon] extension adds a once fixture for precompiling assets.

4.  A middleware: `incise.middlewares.impl.*`

    Middlewares for the development serving mode may also be extended.
    [`incise-stefon`][incise-stefon] uses this feature to add [stefon's `asset-pipeline` middleware][asset-pipeline].

5.  A deployer: `incise.deployer.impl.*`

    Deployers are used for deploying a website.
    They often call `incise.once.core/once` to first generate the site into a directory.
    Those generated files are then *deployed* by whatever means your configured deployer uses.
    A git branch based deployer is included in `incise`.

You can read more about how extensions are found and used on the [extensibility][] page.
This might be helpful if you are hoping to extend *incise*.

### Default extensions

#### Parsers

The following default parsers are available in `incise`:

Included in `incise-core`:

* copy -- [incise.parsers.impl.copy](https://github.com/RyanMcG/incise-core/blob/master/src/incise/parsers/impl/copy.clj)
* clj -- [incise.parsers.impl.clj](https://github.com/RyanMcG/incise-core/blob/master/src/incise/parsers/impl/clj.clj)
* html -- [incise.parsers.impl.html](https://github.com/RyanMcG/incise-core/blob/master/src/incise/parsers/impl/html.clj)

As a separate package:

* incise-markdown-parser -- [incise.parsers.impl.markdown](https://github.com/RyanMcG/incise-markdown-parser/blob/master/src/incise/parsers/impl/markdown.clj)

#### Transformers

The only transformer specified by default is [`html-header-anchors`][html-header-anchors].
It assumes the content of a Parse to be html and adds easily styled anchor tags into headers.

#### The `git-branch` deployer

The only deployer defined included in `incise` is the [`git-branch` deployer][git-deployer].
It makes deploying to [github pages][] very easy.

---

## Where are the tests?

The `incise` package has no tests on its own since it is just a collection of extensions and `incise-core`.
So, there are no tests in this package but `incise-core` is somewhat well speced out.

## What's next? & Contributing

I have been adding [issues][] with ideas.
Please open issues with your own idea!
Contribution and feedback are **very** welcome.

## Tips

If you find *incise* valuable and are feeling particularly generous you may send
some BTC to the address below.

    16QAD8aVDkQYqT8WehSQtfQp1xRjbwxK3Q

## Insular s (ꞅ)

The *s* in *incise* logo is the [insular][] *s* (ꞅ). It is sometimes found in
[Gaelic type][gaelic-type].

## License

Copyright © 2013 Ryan McGowan

Distributed under the Eclipse Public License, the same as Clojure.

[blog]: http://www.ryanmcg.com/
[incise]: http://www.ryanmcg.com/incise/
[incise-source]: https://github.com/RyanMcG/incise-core
[incise-api]: http://www.ryanmcg.com/incise/api/
[incise.edn.example]: https://github.com/RyanMcG/incise-core/blob/master/resources/incise.example.edn
[ex-project]: https://github.com/RyanMcG/incise-example-project
[insular]: http://en.wikipedia.org/wiki/Insular_script
[gaelic-type]: http://en.wikipedia.org/wiki/Gaelic_type
[Leiningen]: https://github.com/technomancy/leiningen
[plugin]: https://clojars.org/lein-incise
[issues]: https://github.com/RyanMcG/incise/issues?state=open
[git-deployer]: https://github.com/RyanMcG/incise-git-deployer
[github pages]: http://pages.github.com/
[extensibility]: http://www.ryanmcg.com/incise/extensibility/
[incise-stefon]: https://github.com/RyanMcG/incise-stefon
[asset-pipeline]: https://github.com/circleci/stefon
[html-header-anchors]: http://www.ryanmcg.com/incise/api/incise.transformers.impl.html-header-anchors.html
