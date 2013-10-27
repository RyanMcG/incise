# ![inciꞅe](https://raw.github.com/RyanMcG/incise/master/assets/logo.png)

A hopefully simplified static site generator in Clojure.

## Usage

Development mode

```bash
lein run # Development mode by default
```

Unfortunately the speclj leiningen plugin does not work well with clj-v8 because
it requires custom jvm options (this seems to be a bug with speclj). However,
you can still run the tests using lein run:

```bash
lein run -m specj.main
# The vigilant runner works too
lein run -m specj.main -a
```

## License

Copyright © 2012 Ryan McGowan

Distributed under the Eclipse Public License, the same as Clojure.
