<img alt="Goslings" title="Goslings" src="https://github.com/kaitoy/goslings/raw/master/www/images/goslings-logo.png" width="70%" style="margin: 0px auto; display: block;" />

Goslings - Git Repository Visualizer
====================================

Contents
--------

* [What's Goslings](#whats-goslings)
* [Download](#download)
* [Build](#build)
* [How to Use](#how-to-use)
* [GaaS](#gaas)
* [License](#license)
* [Contact](#contact)

What's Goslings
---------------
Goslings visualizes a Git repository.

A Git repository consists of objects, references, and an index.

* Object
    * Blob: Represents a file, and contains the entire contents of the file.
    * Tree: Represents a directory, and contains pointers to other trees and blobs.
    * Commit: Represents a commit, and contains pointers to the parent commits and a pointer to a tree which represents the project's root directory.
    * Annotated Tag: Represents an annotation of a tag, and contains a pointer to a commit.
* Reference
    * Branch: A pointer to a commit.
    * Tag: A pointer to a commit or a tag object.
    * Symbolic Reference: A pointer to a branch or commit.
* Index: A structure containing paths to files and pointers to blobs.

Understanding this repository architecture helps you to master Git.
Goslings was created as a tool to learn the architecture.

![goslings.gif](https://github.com/kaitoy/goslings/raw/master/www/images/goslings.gif)

Download
--------

You can download Goslings from [GitHub Releases](https://github.com/kaitoy/goslings/releases)

Build
-----
1. Install [Git](https://git-scm.com/) and JDK 8+.
2. Download the project by `git clone --recursive https://github.com/kaitoy/goslings.git`.
3. In the project root directory, run `./gradlew build`.

How to Use
----------
JRE 8+ is required to run Goslings server.

Run `java -jar goslings/goslings-server/build/libs/goslings-server-0.0.1.jar --server.port=80` to start Goslings server.
After the startup completes, open `http://localhost` by a Web browser. You will see Goslings GUI like below:

![goslings-form](https://github.com/kaitoy/goslings/raw/master/www/images/goslings-form.png)

In the GUI, enter the path to or the URL of a Git repository (e.g. `C:\tmp\goslings\.git`, `/tmp/goslings/.git`, `https://github.com/kaitoy/goslings.git`, etc.)
and press the `Browse` button.
Then, Goslings server creates a symlink (if local) or a clone (if remote) of the repository into the working directory `goslings` in the tmp directory which `java.io.tmpdir` points to,
and shows the Git [objects](https://git-scm.com/book/en/v2/Git-Internals-Git-Objects) and [references](https://git-scm.com/book/en/v2/Git-Internals-Git-Objects) like below:

![goslings.png](https://github.com/kaitoy/goslings/raw/master/www/images/goslings.png)

* Legend:
    * Pink Circle   : Commit
    * Green Triangle: Tree
    * Purple Box    : Blob
    * Red Diamond   : Tag Object
    * Blue Box      : (Symbolic) Reference
    * Green Box     : Index

* Operations
    * Use your mouse wheel to zoom in and out.
    * Drag and drop an object, a reference, or the index to move it.
    * Drag and drop the background to slide the view.
    * Single click on an object, a reference, or the index shows its contents.
    * And double click on a commit or a tree shows trees and blobs under it.

* Configurations
    * `com.github.kaitoy.goslings.server.reposDir`: Set this property to change the path of the working directory.
    * `com.github.kaitoy.goslings.server.uriPrefix`: If this property is set, the Goslings server returns an error for a repository URI which doesn't start with the value of the property.

GaaS
----
GaaS (Goslings as a Service) is available at http://www.goslings.tk/ .

GaaS treats [my GitHub repositories](https://github.com/kaitoy) only.

License
-------

Goslings is distributed under the MIT license.

    Copyright (c) 2016-2017 Kaito Yamada

    Permission is hereby granted, free of charge, to any person obtaining a copy of
    this software and associated documentation files (the "Software"), to deal in the Software without restriction,
    including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
    and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
    subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
    NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
    IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
    WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

Contact
-------

Kaito Yamada (kaitoy@pcap4j.org)
