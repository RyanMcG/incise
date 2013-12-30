# ![inciꞅe](https://raw.github.com/RyanMcG/incise/master/resources/content/assets/images/logo.png) [![Build Status](https://travis-ci.org/RyanMcG/incise.png?branch=master)](https://travis-ci.org/RyanMcG/incise)

<span class="tag-line">An extensible static site generator written in
Clojure.</span>

## Get excited (or don't)

There are more exciting things out there than another static website
generator. That said if the following items sound like good ideas to you then
you may like incise:

* Use Clojure ✓
* Allow many different setups and configurations ✓
* Extensible by nature ✓
* Batteries included ✓

#### Examples

* [This website][incise]
* [My stupid personal website][blog]
* [The official example project][ex-project]

Of course these examples are sort of meaningless. Incise does not help you
create content or designs. It is simply a mechanism for finding files
dispatching them to functions based on file extensions. This pattern happens to
be pretty powerful and batteries are included to actually make it useful too.

## Extensibility

Incise has been designed to be extensible. It scans the classpath for namespaces
that match various patterns and requires them to register new functionality.
This functionality could be one of three things:

1. A parser - `incise.parsers.impl.*`
2. A layout - `incise.layouts.impl.*`
3. A deployment workflow - `incise.deploy.workflows.*`

Effectively, incise does a straightforward require of any namespace that matches
the one of the patterns above. The namespace may invoke a `register` function to
make incise aware of the implementation. Parsers, layouts and deployment
workflows all have their own register functions (`incise.parsers.core/register`,
`incise.layouts.core/register` and `incise.deploy.core/register`). All three
register functions take a key or collection of keys to map the function to be
registered to.

### Parsers

Parsers are the muscles that give incise its ability to do. A parser is a
function which parses a file and writes zero or more files as a result. The
simplest parser would be the no-op parser. A no-op parser would take in a file
and do nothing with it. Here is the incise friendly implementation of the no-op
parser:

```clojure
(ns incise.parsers.impl.no-op)

(defn no-op-parse [^File _] (delay []))

(incise.parsers.core/register [:gitignore] no-op-parser)
```

Quite a few things are going on here despite being only a few SLOC. Firstly, you
will notice the namespace the parser is defined in. It matches the pattern
mentioned above so it will get required automatically.

The actual definition of `no-op-parse` returns a delay which when invoked
returns a sequence of files. In this case it is an empty sequence since this is
the no-op parser. Why not just return a sequence of files?

Finally, the parser is registered to files with the `gitignore` extension.
Effectively, this means incise would parse all gitignore files with the no-op
parser. This is, of course, the same as not parsing them at all.

#### Two step parsing

Parsers must satisfy the following criteria:

* Take a single file (i.e a `java.io.File` instance)
* Return a delay or [thunk][] (a parameterless function) which when invoked
  returns a sequence of files.

This is pretty easy to do and very loose. It is also a bit more complex than may
seem necessary (why return a thunk that does something instead of just doing
it?). In order to implement features like tags each invocation of a parser must
have at least some access to data from other files being parsed. The solution
incise uses is to split parsing into two steps. Generally these steps are:

1.  Allow side-effects like modifying public atoms. Read in the file.
2.  Invoke the thunk/delay. Write files and return corresponding `java.io.File`
    instances.

Despite parsers needing to have two steps the work does not have to really be
split between them. In fact, the general guidance is to do as much work as
possible in the thunk/delay. The copy parser (`incise.parsers.impl.copy/parse`)
is a good example of this. Since it has no need to be aware of parser
invocations or side effects during step 1 it has no side effects (e.g. I/O
operations). All side effects occur in step 2.

Other parsers (like those designed for rendering HTML pages and posts) have
definite need for these two steps.

#### `html-parser`

95% of the time a parser is probably meant to convert some sort of source into
HTML. For this specific use case a lot of the hard work has been done for you if
you use `incise.parsers.html/html-parser`.

`html-parser` is a higher-order function which takes a function and returns a
valid parsers. The function passed to `html-parser` should take a string
and either return HTML as a string or a list of Clojure code to evaluate in
context later (with the result being an HTML string). The first
case is simpler so I will start there.

Here is the html to html parser implementation.

```clojure
(ns incise.parsers.impl.html
  (:require (incise.parsers [core :as pc]
                            [html :refer [html-parser]])))

(pc/register [:html :htm] (html-parser identity))
```

The identity function simply returns the value of whatever it is passed. So, if
it is passed a string of HTML it fits are requirements to be our argument to
`html-parser`.

The following example html file could then be parsed using the parser defined
above.

```html
{:layout :base
 :path "hmmm/index.html"}

<h1>Hmmm</h1>
```

It would generate an html page using the base layout with the contents of the
body tag being "`<h1>Hmmm</h1>`". Since we used the `identity` function no
special processing of the source content was done.

Alternatively we could define a more complicated parser. Imagine we have a
function that takes a string of markdown and returns a string of HTML. We could
use `html-parser` to create a valid parser for markdown files almost as easily
as we did for HTML files.

```clojure
(ns incise.parsers.impl.markdown
  (:require [markdown.core :refer [md-to-html]]
            (incise.parsers [core :as pc]
                            [html :refer [html-parser]])))

(pc/register [:md :markdown :mkd] (html-parser md-to-html))
```

Note that `markdown.core/md-to-html` is a fictional function.

[The included implementation of a markdown parser][md-parser-source] uses
[cegdown][], a useful Clojure wrapper of the markdown parsing Java library
[pegdown][], and is only slightly more complicated so that various its options
may be overridden via dynamic binding.

### Layouts

While they are a core feature of incise, layouts may be used or not be any given
parser. The `html-parser` uses them for instance though other parsers may as
well. Layouts are functions which take a sting and return a string. The layouts
`html-parser` uses simply wrap html tags around some generated content. The
benefit of separating layouts from the parser is that different layouts can be
used for different files of the same extension. This seemed like it would be a
common feature desired by parsers so it is part of incise's core.

### Deployment workflows

Static websites are pretty much useless of they do not go anywhere.
`incise.once/once` is meant for parsing all content and writing it out to a
specified (or default) output directory.  While very useful on its own, certain
deployment procedures are so common (not project specific) that it seemed
sensible to make them plugable.

Like layouts and parsers, deployment workflows are registered in an atom by
calling the `incise.deploy.core/register` function and the namespaces are
automatically required by the previously mentioned scheme.

#### The `git-branch` deployment workflow

The only deployment workflow defined by default is the `git-branch` workflow. It
attempts the following:

1.  `incise.once/once` to create content for deploying to a static web server
2.  Move that content into the git directory so checking out other branches does
    not remove it.
3.  Create an orphaned branch of a configurable name (`gh-pages` by default) or
    checkout the branch of that name if it already exists.
4.  Move the content from inside the git directory to the working tree
    directory.
5.  Commit it with a generated message including the commit hash of the source
    branch.
6.  Push it to a configurable remote.

#### Configuring a deployment workflow

To configure a deployment workflow you need to modify your `incise.edn`. A
deployment workflow is passed the configuration under the deploy key and the
workflow to be used is determined by the value associated with the `:workflow`
key in the `:deploy` map.

It is probably helpful to [look at an example][incise.edn.example].

## Usage

Shockingly, the easiest way to use incise is through its main method. This can
easily be executed with [Leiningen](https://github.com/technomancy/leiningen).

```bash
lein run -- --help
```

The main method takes several switches.

    Switches               Default  Desc
    --------               -------  ----
    -h, --no-help, --help  false    Print this help.
    -m, --method           :serve   serve, once, or deploy
    -c, --config                    The path to an edn file acting as
                                    configuration for incise
    -i, --in-dir                    The directory to get source from
    -o, --out-dir                   The directory to put content into
    -u, --uri-root                  The path relative to the domain root where
                                    the generated site will be hosted.

These options can be used to override their config counterparts.

As seen above, the default method is to serve. This launches a ring application
which automatically parsers files and re-parses them when they are modified. It
also launches an nREPL server so clients who want one do not need to startup a
separate process to have it.

```bash
lein run
# Or to be explicit
lein run -- -m serve
```

You can also generate all content by parsing all parsable files in the input
directory using the once command.

```bash
lein run -- -m once
```

Finally, as mentioned above, you can use your configured deployment method.

```bash
lein run -- -m deploy
```

## Configuration

Incise is primarily configured via a config file it tries to find as a resource.
This file must be named `incise.edn` and be found in the root of your resources
directory. You can override that file and pass in the `-c` or `--config` option
to incise's main function to specify an alternative location of the config file.

An [example `incise.edn`][incise.edn.example] file is included with this project
as well as the `incise.edn` file used to generate [this website][incise].

## Running specs

Unfortunately the speclj leiningen plugin does not work well with clj-v8 because
it requires custom JVM options (this seems to be a bug with speclj). However,
you can still run the tests using `lein run`:

```bash
lein run -m specj.main
# The vigilant runner works too
lein run -m specj.main -a
```

## What's next?

Here are some ideas for features I intend to work on. Open an issue with more
ideas or contribute. I would love some feedback and/or collaboration!

* Live editing
* More parsers
* Handle parser extension conflicts intelligently (instead of blindly
  overwriting based on load order)

## Donate

If you find incise valuable and are feeling particularly generous you may send
some BTC to the address below.

    16QAD8aVDkQYqT8WehSQtfQp1xRjbwxK3Q

## Insular s (ꞅ)

The *s* in incise logo is the [insular][] *s* (ꞅ). It is sometimes found in
[Gaelic type][gaelic-type].

## License

Copyright © 2013 Ryan McGowan

Distributed under the Eclipse Public License, the same as Clojure.

[blog]: http://www.ryanmcg.com/
[incise]: http://www.ryanmcg.com/incise/
[thunk]: http://en.wikipedia.org/wiki/Thunk_(functional_programming)
[incise.edn.example]: https://github.com/RyanMcG/incise/blob/master/resources/incise.example.edn
[md-parser-source]: https://github.com/RyanMcG/incise/blob/master/src/incise/parsers/impl/markdown.clj
[cegdown]: https://github.com/Raynes/cegdown
[ex-project]: https://github.com/RyanMcG/incise-example-project
[pegdown]: http://pegdown.org/
[insular]: http://en.wikipedia.org/wiki/Insular_script
[gaelic-type]: http://en.wikipedia.org/wiki/Gaelic_type
