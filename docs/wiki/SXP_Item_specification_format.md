---
title: SXP Item specification format
permalink: wiki/SXP_Item_specification_format/
layout: wiki
---

We make the Items Specification via an [XML schema](/wiki/XSD "wikilink"). The
Items described can be products and services. Users can create an xml
file for describing their item and then insert it on the respective
clause of the [SXP Contract](/wiki/SXP_Contract "wikilink").

[Download SXP
specification](https://docs.google.com/file/d/0B4JKZAq0izyxN2MzVkZnR29CN1E/edit?usp=sharing)

Structure of SXP Item schema
----------------------------

-   **Item Description**. General description of the item. It includes:
    -   Title (mandatory).
    -   Brand.
    -   Color.
    -   Dimensions.
    -   Description. A narrative description of the item.
    -   Item information URL. An Url with information of the item.
    -   Item picture URL. An Url with a picture of the item.
    -   Number of Items.

<!-- -->

-   **Item Category**. It classifies the product in categories such as:
    -   Clothing
    -   Sports
    -   Beauty
    -   ...

There are also several categories for services:

-   -   Teaching
    -   Personal Caring
    -   Transport
    -   Other Services

Currently, it is only possible to select the category of the product. We
may want to extend this and subdivide each category, or add specific
information (e.g. duration on services, subcategories)

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

