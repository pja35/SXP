---
title: XML
permalink: wiki/XML/
layout: wiki
tags:
 - Technical Glossary
---

What is XML?
------------

XML is a tunable tag language for describing things in a hierarchical,
formatted manner. For instance HTML, the language of all webpages, is a
tuned version of XML.

XML is not particularly elegant, but it has become a standard. As a
consequence:

-   there are many tools around for manipulating XML.
-   there may already by some tuned versions of XML for specifying
    [items](/wiki/Items_Specification "wikilink") and
    [contracts](/wiki/Contracts_Specification "wikilink"), which is what
    we want.
-   once GUIs will be developed none will have to see the
    underlying XML.

Basic Structure
---------------

-   **Tags**. The information in an XML file is stored inside tags. Tags
    are not predefined in XML: users must define their own ones.

<!-- -->

-   **Elements**. An XML element is everything from (including) the
    element's start tag to (including) the element's end tag.

<!-- -->

-   **Attributes**. An attribute is some extra information, provided at
    the start tag in quotation marks. They are intended to be metadata
    (data about data).

Here is an example of an .xml file:

<?xml version="1.0" encoding="ISO-8859-1"?>
<note id=“1245”>
`         `<to>`Tove`</to>  
`         `<from>`Jani`</from>  
`         `<heading>`Reminder`</heading>  
`         `

<body>
Don't forget me this weekend!

</body>
</note>
In the example, *note* is an element and *id* is an attribute.

Here are the obvious pages about XML:

-   [on Wikipedia](http://en.wikipedia.org/wiki/XML%7CXML)
-   [XML tutorial at w3schools](http://www.w3schools.com/xml/)

