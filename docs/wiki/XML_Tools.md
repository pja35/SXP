---
title: XML Tools
permalink: wiki/XML_Tools/
layout: wiki
---

PDF Generator of an XML SXP Contract File
-----------------------------------------

It is possible to generate PDF files according to contracts that follows
the [SXP Contract Specification](/wiki/SXP_Contract_Specification "wikilink").
These PDF files are intended to extract the xml files and present it in
a narrative way.

[Here can be
found](https://docs.google.com/file/d/0B4JKZAq0izyxV1VQYVRxZXJOQkE/edit?usp=sharing)
an example of a contract.

In order to make PDFs, we use the following XML technologies:

-   [XSL-FO](http://en.wikipedia.org/wiki/XSL_Formatting_Objects). It is
    an XML language that describes the format of the PDF file. Once we
    have an FO file describing our contract, we can automatically
    convert it to PDF using a print formatter like [Apache
    FOP](http://xmlgraphics.apache.org/fop/).
-   [XSLT](http://en.wikipedia.org/wiki/XSL_Transformations). Using an
    XSLT stylesheet (also XML-based) we make possible the transformation
    between the original XML contract file and the FO file. An XML
    editor is needed. The current stylesheet used can be [downloaded
    here](https://docs.google.com/file/d/0B4JKZAq0izyxU1ZUbzhaR3dNamc/edit?usp=sharing).

