---
title: SXP Item specification format
permalink: wiki/SXP_Item_specification_format/
layout: wiki
---

We do the Items Specification via an XML schema (XSD), which is one of
the possible ways to define an XML structure.

SXP Items schema
----------------

The SXP Item schema is an [XML schema](/wiki/XML_schema "wikilink"), and gives
a description of the items. Note that items here means products and
services. It provides the following entries:

-   External URL (optional). An Url with information about the product.

<!-- -->

-   Number of Items (optional).

<!-- -->

-   Description Data (optional).
    -   Title.
    -   Brand.
    -   Designer
    -   Description.

<!-- -->

-   Item Data. It classifies the product in categories such as:
    -   Clothing
    -   Sports
    -   Beauty
    -   ...

There are also several categories for services:

-   -   Teaching
    -   Personal Caring
    -   Transport
    -   Other Services

Currently, it is only possible to select the category and add a text
description of the product. We may want to extend this and subdivide
each category, or add specific information (e.g. duration on services)

[Link for downloading
SXPProducts.xsd](https://docs.google.com/file/d/0B4JKZAq0izyxZTVickJ2MDM5VDQ/edit?usp=sharing)

Related Frameworks
------------------

There are some XML schemas for defining products in the most known
e-commerce websites:

-   Amazon
-   Google Shopping

The following services have a product taxonomy, but they do not use XML
schemas (even when they are XML-based):

-   Prestashop
-   Magento

External links
--------------

-   [Selling on Amazon guide to XML (description of objects in
    page 38/85)](https://images-na.ssl-images-amazon.com/images/G/01/rainier/help/XML_Documentation_Intl.pdf)

