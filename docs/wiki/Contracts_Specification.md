---
title: Contracts Specification
permalink: wiki/Contracts_Specification/
layout: wiki
---

The goal of these specifications is to define the possible terms of a
[contract](/wiki/Contracts "wikilink") using an [XML](XML "wikilink") schema

Currently, the [SXP Contract Specification
Schema](/wiki/SXP_Contract "wikilink") is based in Legal XML eContracts (see
below). This page is for presenting the different XML existing formats
and discussing the theme associated to the specification of contracts.

Existing XML standards for describing contracts
-----------------------------------------------

-   **Legal XML eContracts (OASIS Standard).**

Legal XML is an XML schema for describing the generic structure of a
wide range of contract documents.

This standard uses basic building blocks called *items*, for the
description of the classical notions of clauses or sections. It also
uses blocks called *party*, *date*, *party-signature*. Then it encloses
these informations under the tags *contract-front*, *body* and *back*,
emulating the structure of a real paper-based contract.

More detailed description at the [eContracts Specification
1.0](http://docs.oasis-open.org/legalxml-econtracts/CS01/legalxml-econtracts-specification-1.0.pdf).

-   **Electronic Business using eXtensible Markup Language (ebXML).**

ebXML is a family of XML standards that provides an infrastructure for
electronic bussiness. It is also developed by OASIS.

In ebXML, contracts are not defined in a single document — instead , the
trading partners may form a contract by exchanging requesting documents
constituting *binding offers*, and responding documents constituting
*binding acceptances*, resulting in a demonstrably successful or failed
negotiation of the business terms proposed in the offer.

For more information, read the [E-Commerce Patterns
v1.0](http://ebxml.org/specs/bpPATT.pdf) Technichal Report, from the
ebXML specification.

-   **Financial Product Markup Language (FpML).**

FpML is a XML standard for describing OTC derivatives.

-   **eLEGAL.**

eLEGAL defines a framework for legal conditions and contracts regarding
the use of ICT in project business.

Adopting an XML standard for SXP
--------------------------------

As said above, the current version of SXP Contract Specification is an
extension of Legal XML Contract. The reason is that this standard is
more flexible and allows us to define contracts forcing the user to fill
out the obligatory clauses, while he can add more clauses if necessary.

Trying to use the ebXML definitions of contract could be less suitable,
unless we adopted the whole ebXML specification for the interchange.
Still, ebXML specification is intended to be used in normal buy/sell
actions, not interchanges as we want in SXP.

Further reading
---------------

-   [Language, Deals and Standards: The Future of XML
    Contracts](http://lawdigitalcommons.bc.edu/cgi/viewcontent.cgi?article=1139&context=lsfp)
    \[Cunningham 2006\]

<!-- -->

-   [E-contracting
    Challenges](http://www.adaptivity.nl/articles/E-contracting.pdf)
    \[Xu, de Vrieze\]

External links
--------------

-   [OASIS Legal XML eContracts Technical
    Committee](https://www.oasis-open.org/committees/tc_home.php?wg_abbrev=legalxml-econtracts)

<!-- -->

-   [ebXML.org](http://www.ebxml.org).

<!-- -->

-   [FPML](http://www.fpml.org).

<!-- -->

-   [eLEGAL](http://cic.vtt.fi/projects/elegal/public.html).

