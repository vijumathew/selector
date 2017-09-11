(ns util.selection)

(defn node-to-range [node]
  (let [range (js/Range.)]
    (.selectNode range node)
    range))

(defn expand-selection [selection]
  (let [parent-of-first-node (.-parentNode (.-anchorNode selection))
        parent-of-last-node (.-parentNode (.-focusNode selection))
        beginning-range (node-to-range parent-of-first-node)
        end-range (node-to-range parent-of-last-node)]
    (.empty selection)
    (.addRange selection beginning-range)
    (.addRange selection end-range))
  selection)

(defn expand-current-selection []
  (expand-selection (js/document.getSelection)))
