(ns incise.utils)

(defmacro future-with-default-out
  "Just like future, but ensures that *out* and *err* inside the future are the
   same as the calling context."
  [& body]
  `(let [orig-out# *out*
         orig-err# *err*]
    (future
      (binding [*out* orig-out#
                *err* orig-err#]
        ~@body))))

