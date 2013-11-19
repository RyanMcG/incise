# ![inciꞅe](https://raw.github.com/RyanMcG/incise/master/assets/logo.png) [![Build Status](https://travis-ci.org/RyanMcG/incise.png?branch=master)](https://travis-ci.org/RyanMcG/incise)

<span class="tag-line">An extensible static site generator written in
Clojure.</span>

Yes, incise is yet another static site generator. I did not particularly enjoy
using [octopress][] or [jekyll bootstrap][]. They are both ambitious, powerful
and useful pieces of software. I have used them both in the past and they have
worked. I just never liked it. They did not feel robust. I had difficulty
navigating around them. Perhaps my most legitimate gripe is that being
implemented in Ruby they lack the simplicity of an API which boils down to *its
a function*.

## Get excited

Or don't. There are more exciting things out there than another static website
generator. The world around us is amazing!

> *Did you know that the universe is 13.8 billion years old?*

That's crazy!

Still here? OK, well you probably want to see what incise can do then. I will
try my best to trick you into thinking this is a valuable framework by showing
off some cool examples.

* [THIS WEBSITE][incise]
* [My stupid personal website][blog]

These examples have been specifically designed to look better than they are.

&#8942;

Still here? Ugh, FINE. Read on and learn how incise works. &#9786;

## Extensibility

Incise has been designed to be extensible. It scans the classpath for namespaces
that match various patterns and requires them to register new functionality.
This functionality could be one of three things:

1. A parser - `incise.parsers.impl.*`
2. A layout - `incise.layouts.impl.*`
3. A deployment workflow - `incise.deploy.workflows.*`

I'll call this the require-register pattern. Effectively, incise does a
straightforward require of any namespace that matches the one of the patterns
above. The namespace may invoke a `register` function to make incise aware of
the implementation. Parsers, layouts and deployment workflows all have their own
register functions (`incise.parsers.core/register`,
`incise.layouts.core/register` and `incise.deploy.core/register`). In all three
register functions a key or collection of keys is provided to identified the
registered function.

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

#### Two step parsing

Incise parsers must satisfy the following criteria:

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



### Layouts

### Deployment workflows

## Configuration

## Usage

Shockingly, the easiest way to use incise is through its main method. This can
easily be executed with [Leiningen](https://github.com/technomancy/leiningen).

```bash
lein run
```

The main method takes several switches.

    Switches               Default  Desc
    --------               -------  ----
    -h, --no-help, --help  false    Print this help.
    -m, --method           :serve   serve, once, or deploy
    -i, --in-dir                    The directory to get source from
    -o, --out-dir                   The directory to put content into
    -u, --uri-root                  The path relative to the domain root where
                                    the generated site will be hosted.

These options can be used to override their config counterparts.

## Running specs

Unfortunately the speclj leiningen plugin does not work well with clj-v8 because
it requires custom jvm options (this seems to be a bug with speclj). However,
you can still run the tests using `lein run`:

```bash
lein run -m specj.main
# The vigilant runner works too
lein run -m specj.main -a
```

## License

Copyright © 2013 Ryan McGowan

Distributed under the Eclipse Public License, the same as Clojure.

[blog]: http://www.ryanmcg.com/
[incise]: http://www.ryanmcg.com/incise/
[thunk]: http://en.wikipedia.org/wiki/Thunk_(functional_programming)
[octopress]: http://octopress.org/
[jekyll bootstrap]: http://jekyllbootstrap.com/usage/jekyll-quick-start.html
