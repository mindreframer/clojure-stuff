# Clojure Cookbook

*Clojure Cookbook* marks Clojure's entry into O'Reilly's prestigious [Cookbook Series](http://shop.oreilly.com/category/series/cookbooks.do). The book details a large number of recipes – pairs of problems and solutions – for common topics in Clojure.

*Clojure Cookbook* doesn't just teach you Clojure, it also shows you how to use the language and many of its common libraries. The most difficult part of mastering any language is knowing how to apply it, in an idiomatic way, to tasks that real software developers encounter every day. This is especially true of Clojure.

With code recipes that teach you how to use the language in a variety of domains, *Clojure Cookbook* goes beyond simply teaching Clojure syntax and semantics. It contains annotated example code with detailed analysis and explanation for hundreds of real programming tasks. You can read the book straight through to gain insights about Clojure, or use it as a reference to solve particular problems.

## Exploring the Book

If you're an Emacs-wielding Clojurist, you will probably want to read this book in Emacs, too. Here is a function that "turns the page" from one recipe to the next (find and open the next recipe, close the buffer with the previous recipe).

```elisp
(defun increment-clojure-cookbook ()
  "When reading the Clojure cookbook, find the next section, and
close the buffer. If the next section is a sub-directory or in
the next chapter, open Dired so you can find it manually."
  (interactive)
  (let* ((cur (buffer-name))
	 (split-cur (split-string cur "[-_]"))
	 (chap (car split-cur))
	 (rec (car (cdr split-cur)))
	 (rec-num (string-to-number rec))
	 (next-rec-num (1+ rec-num))
	 (next-rec-s (number-to-string next-rec-num))
	 (next-rec (if (< next-rec-num 10)
		       (concat "0" next-rec-s)
		     next-rec-s))
	 (target (file-name-completion (concat chap "-" next-rec) "")))
    (progn 
      (if (equal target nil)
	  (dired (file-name-directory (buffer-file-name)))
	(find-file target))
      (kill-buffer cur))))
```

If you wish, you can then bind the function to a key:

```elisp
(define-key adoc-mode-map (kbd "M-+") 'increment-clojure-cookbook)
```

Of course, this binding assumes you're using adoc-mode for reading .asciidoc files. We suggest CIDER for evaluating code interactively. Adding the following hook to your config will enable cider-mode every time you enter an AsciiDoc file. Once active, you can start a REPL and evaluate code like you would do in any regular Clojure file.

```elisp
(add-hook 'adoc-mode-hook 'cider-mode)
```

## Contributing

As of Jan. 10, 2014 we are preparing the book for print. See [CONTRIBUTING.md](CONTRIBUTING.md) for more info.

## Building the Book

You can build a PDF/MOBI/EPUB/HTML version of the book with the `asciidoc`
command-line utility.  (You must also have the `source-highlight` application
installed and properly configured.)

### Pre-requisites

You must have the `asciidoc` and `source-highlight` command-line utilities
installed and configured before attempting to build the book.

To install and configure the tools on OS X or Linux,
run the included [`bootstrap.sh`](script/asciidoc/bootstrap.sh) script:

```sh
$ ./script/asciidoc/bootstrap.sh
```

### Rendering

With installation and configuration complete, all that is left is to run the `asciidoc` command.

* To render a single document:

    ```sh
    $ asciidoc -b html5 conventions.asciidoc
    # ... outputs conventions.html
    ```

* To render the entire book:

    ```sh
    $ asciidoc -b html5 book.asciidoc
    # ... outputs book.html
    ```

**NOTE**: Rendered output is *similar* to the final book, but does not include O'Reilly style sheets.

### Testing

To verify asciidoc files are without error/warning, run the following:

```sh
$ ./script/asciidoc/check.sh
```
The only output should be the file detail.


#### Fixing Asciidoc Warnings/Errors

The only acceptable warning is related to structure of the book sections. It's
OK to ignore this one:

```
asciidoc: WARNING: conventions.asciidoc: line 1: section title out of sequence: expected level 1, got level 2
```

Please correct all others or ask for guidance if the error message is unclear.
A common one is related to callouts like "\<1\>" at the end of a line of code.

```
asciidoc: WARNING: formatting-strings.asciidoc: line 57: no callouts refer to list item 1
```

To prevent this warning, the callout must be commented using the language
appropriate comment character(s). This also keeps the code runnable in the REPL
when pasted.

Clojure Example:

```clojure
(defn foo [] "bar" ) <1>
```

requires a semicolon before the callout reference

```clojure
(defn foo [] "bar" ) ; <1>
```

Console Example:
```sh
Username: <1>
```

should be

```sh
Username: #<1>
```

## Who we are

We are Luke Vanderhart ([@levand](http://github.com/levand)) and Ryan Neufeld ([@rkneufeld](http://github.com/rkneufeld)), developers, authors, conference speakers and (at the moment), teachers. For this book-building adventure we will be your guides; we'll be collecting and editing your contributions, interfacing with the publisher (O'Reilly) and writing a solid chunk of the book ourselves.

## License

<a rel="license" href="http://creativecommons.org/licenses/by-nc-nd/3.0/deed.en_US"><img alt="Creative Commons License" style="border-width:0" src="http://i.creativecommons.org/l/by-nc-nd/3.0/88x31.png" /></a><br /><span xmlns:dct="http://purl.org/dc/terms/" property="dct:title">This draft of Clojure Cookbook</span> is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc-nd/3.0/deed.en_US">Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License</a>.


Please see the [contribution guide](CONTRIBUTING.md) for how this works for accepting pull requests.

Also, please note that because this is a *No Derivatives* license, you may *not* use this repository as a basis for creating your own book based on this one. Technically speaking, this book is open source in the "free as in beer" sense, rather than "free as in speech."
