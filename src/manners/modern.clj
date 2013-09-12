(ns manners.modern
  (:require [manners.utils :refer [defalias]]
            [manners.victorian :as vic]))

(defalias valid? vic/proper?)
(defalias invalid? vic/rude?)
(defalias errors vic/bad-manners)
(defalias validate! vic/avow!)
(defalias validator vic/coach)
(defalias throw-errors vic/falter)
