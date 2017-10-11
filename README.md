# Selector  #

This is a chrome extension built using [chromex](http://github.com/binaryage/chromex) and [boot](http://github.com/boot-clj/boot). It uses [boot chromex sample](http://github.com/vijumathew/boot-chromex-sample) as a starting point.

## Purpose ##

This lets users bind keybindings to various text selection commands. These are configured from the extension's popup. The various commands available currently are `expand-current-selection`.

## Building ##

Install the latest Chrome Canary with [custom formatters](https://github.com/binaryage/cljs-devtools#enable-custom-formatters-in-your-chrome-canary) enabled. 

### Local dev ###

To build locally open up Emacs and start a Clojure REPL with `M-x cider-jack-in`. Then from the REPL do:

```clojure
boot.user=> (def p (future (boot run)))
```

Now open Chrome Canary and go to the Extensions page. Enable Developer mode and add `target/` via "Load unpacked extension...". 

The icon to open the popup should appear next to the address bar.

#### Popup REPL ####

To start a REPL that will connect to the popup from Emacs right click on the icon and select "Inspect Popup". Go to the sources page of Developer Tools and open `popup.html` in a new tab.

Now back at the Clojure REPL:

```clojure
boot.user=> (start-repl)
```

If this does not connect refresh the popup page. Now we're connected!

### Production ###

From a terminal window do:

```bash
$ boot production
```

Load the extension by adding `target/` via "Load unpacked extension..." to test the extension. Finally use Chrome's "Pack extension" tool to prepare the final package (.crx and .pem files)

## Code Overview ##

### Util ###

There is code for storage and selection in `src/util`. `storage.cljs` uses the Chromex Storage API to facilitate communication between the content script and popup, and `selection.cljs` contains code to extend the current text selection.

### Content Script ###

The content script, the code that is injected into the page, is located in `src/content_script`. This sets up a keyboard listener and when there is a match alters the current text selection using code from the `selection` namespace.

### Popup ###

The popup code located in `src/popup` is for setting and getting the data from the popup.

## Roadmap ##
  * Add customizable selections
  * Support for more complex keybindings
