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

PCS\_Pi(C,Pj,T) denotes a Private Contract Signatures by Pi for Pj on
contract C with Trusted Third Party T. The object is such that:

-   It can be created by Pi and, in the eyes of an external party, faked
    by Pj;
-   Pi and T are able to convert it into {C}\_PrivPi.

How different is that from the Designated Verifiable Escrows of
[AsokanSchunter](/wiki/AsokanSchunter "wikilink") and
[BaumwaidnerWaidner](/wiki/BaumwaidnerWaidner "wikilink")? How does it compare
in terms of security? We need to find out. Here the further reference on
PCS given is
[GarayJakobssonMackenzie](/wiki/GarayJakobssonMackenzie "wikilink").

Main protocol for Pi
--------------------

For *t=1...n/2* do:

-   Await from j &lt; i: PCS\_Pj((C,t),Pi,T)
-   Send to j &gt; i: PCS\_Pi((C,t),Pj,T)
-   Await from j &gt; i: PCS\_Pj((C,t),Pi,T)
-   Send to j &lt; i: PCS\_Pi((C,t),Pj,T)

Then

-   Await from j &lt; i: PCS\_Pj((C,n/2+1),Pi,T), {C}\_PrivPj
-   Send to j &gt; i: PCS\_Pi((C,n/2+1),Pj,T), {C}\_PrivPi
-   Await from j &gt; i: PCS\_Pj((C,n/2+1),Pi,T), {C}\_PrivPj
-   Send to j &lt; i: PCS\_Pi((C,n/2+1),Pj,T), {C}\_PrivPi

