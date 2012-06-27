---
title: MukhamedovRyan
permalink: wiki/MukhamedovRyan/
layout: wiki
---

Provides abuse-free asynchronous multi-party optimistic contract
signing.

It says that there are only two competitors doing just that:

-   Garay and MacKenzie, whose security has been put at stake.
-   [BaumwaidnerWaidner](/wiki/BaumwaidnerWaidner "wikilink").

Let *n* be the number of parties. This protocol requires *n(n-1)(n/2)+1*
messages. It relies on Private Contract Signature.

Private Contract Signatures (PCS)
---------------------------------

PCS\_P\_i(C,P\_j,T) denotes a Private Contract Signatures by P\_i for
P\_j on contract C with Trusted Third Party T. The object is such that:

-   It can be created by P\_i and, in the eyes of an external party,
    faked by P\_j;
-   P\_i and T are able to convert it into {C}\_PrivP\_i.

How different is that from the Designated Verifiable Escrows of
[AsokanShunter](/wiki/AsokanShunter "wikilink") and
[BaumwaidnerWaidner](/wiki/BaumwaidnerWaidner "wikilink")? How does it compare
in terms of security? We need to find out. Here the further reference on
PCS given is
[GarayJakobssonMackenzie](/wiki/GarayJakobssonMackenzie "wikilink").
