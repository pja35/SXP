---
title: SXP Contract specification format
permalink: wiki/SXP_Contract_specification_format/
layout: wiki
---

This page describes the technichal information about the SXP Contract
Definition, which is an extension of OASIS Legal XML eContracts.

Required clauses
----------------

These are the clauses that the user must fill in for creating a SXP
Contract:

-   Parties. Actually, this clause exists at the original Legal XML
    Specification (at the *contract-front* level), but it is not
    considered mandatory. In our specification it has been set
    as mandatory.

At the eContracts Specification, we have the *party* element where we
can specify address and name of each party.

Problem: should we extend the *party* element by specifying the country
of each party, in order to use this information at the following clauses
(see conflict resolution mode at breachClause?

-   Breach clause. A clause that specifies what happens in case of one
    party doesn't accomplish the contract. It has the following
    structure:
    -   Conflict Resolution Mode (mandatory).
        -   exchange-level. Conflicts are resolved between the parties
            who needed to exchange items within the contract.
        -   contract-level. Even local conflicts are resolved by
            all parties. These are the possibilities:
            -   defendant country. Complaints are presented at the court
                of the defendant party's country
            -   plaintive country. Complaints are presented at the court
                of the plaintitive party's country
            -   fixed country. Complaints are presented at the court of
                a given country
    -   Trust Rating Adjustements (optional). Information about the
        adjustements that will be done on the SXP trust rating system.

<!-- -->

-   Items clause. A reference to the items that will be exchanged. This
    reference will be done under the [Items
    Specification](/wiki/Items_Specification "wikilink").

<!-- -->

-   Delivery clause.
    -   the person who will pay the delivery of the objects
    -   when he will do it

Optional clauses
----------------

-   VAT clause. Information about the value-added taxes involved in
    the exchange.
    -   the amount
    -   the authority who will receive the taxes
    -   the parties responsibles of paying

<!-- -->

-   Other Clauses. Basically, users can add as many clauses as they want
    as *items*, under the *body* level. These clauses are intended to
    contain text that can be read by both parties.

The structure of this particular clauses is explained at the eContracts
Specification

Minimal example of a SXP Contract
---------------------------------

In this example, we have two parties (Alice and Bob) exchanging objects
(a violin and a guitar).

<?xml version="1.0"?>
`  `<contract xmlns="urn:oasis:names:tc:eContracts:1:0"
   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
   xsi:schemaLocation="urn:oasis:names:tc:eContracts:1:0 SXPContract.xsd"
   >  
`  `

<title>
<text>Contract between Alice and Bob</text>

</title>
`  `<contract-front>  
`  `<date-block>  
`  `<date>` 20-02-2013`</date>  
`  `</date-block>  
`  `<parties>  
`  `<party>`Alice`</party>  
`  `<party>`Bob`</party>  
`  `</parties>  
`  `</contract-front>  
`  `  
`  `

<body>
`  `<breachClause>  
`  `<authority>` France jurisdiction`</authority>  
`  `</breachClause>  
`  `<objects>  
`  `<object>` Violin `</object>  
`  `<object>` Guitar `</object>  
`  `</objects>  
`  `<deliveryClause>  
`  `<party>` Bob `</party>  
`  `<date>` 22-2-2013`</date>  
`  `</deliveryClause>  
`  `<vatClause>  
`  `<party>` Alice `</party>  
`  `<amount>` 1.40€ `</amount>  
`  `<authority>` France Revenue Service `</authority>  
`  `</vatClause>  
`  `

</body>
`  `</contract>

SXP Contract Schema
-------------------

For validating SXP contracts, users must [download the SXP Contract
Schema](https://docs.google.com/file/d/0B4JKZAq0izyxTFl4b0dZX1FXRUk/edit?usp=sharing)
and the Legal XML eContracts Schema from the [OASIS LegalXML eContracts
Technichal committee
website](https://www.oasis-open.org/committees/tc_home.php?wg_abbrev=legalxml-econtracts).
Then, contracts can be validated using a XML validator.
