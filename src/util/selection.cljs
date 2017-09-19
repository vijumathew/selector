(ns util.selection)

(defn node-to-range [node]
  (let [range (js/Range.)]
    (.selectNode range node)
    range))

(defn add-ranges-to-selection [selection ranges]
  (.empty selection)
  (doseq [r ranges]
    (.addRange selection r))
  selection)

(defn expand-selection [selection]
  (let [parent-of-first-node (.-parentNode (.-anchorNode selection))
        parent-of-last-node (.-parentNode (.-focusNode selection))
        beginning-range (node-to-range parent-of-first-node)
        end-range (node-to-range parent-of-last-node)]
    (add-ranges-to-selection selection [beginning-range end-range])))

(defn expand-current-selection []
  (expand-selection (js/document.getSelection)))
