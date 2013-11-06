# ![inciꞅe](https://raw.github.com/RyanMcG/incise/master/assets/logo.png) [![Build Status](https://magnum.travis-ci.com/RyanMcG/incise.png?token=xvR7q3m9UymcEFtEtgj1&branch=master)](https://magnum.travis-ci.com/RyanMcG/incise)

A hopefully simplified static site generator in Clojure.

## Usage

The main usage of this library is through its main method. This can easily be
executed with [Leiningen](https://github.com/technomancy/leiningen).

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

## Running specs

Unfortunately the speclj leiningen plugin does not work well with clj-v8 because
it requires custom jvm options (this seems to be a bug with speclj). However,
you can still run the tests using lein run:

```bash
lein run -m specj.main
# The vigilant runner works too
lein run -m specj.main -a
```

## License

Copyright © 2013 Ryan McGowan

Distributed under the Eclipse Public License, the same as Clojure.
