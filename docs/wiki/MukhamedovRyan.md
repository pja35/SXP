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

Outline of the protocol
-----------------------

-   Await from j &lt; i: PCS\_Pj((C,t),Pi,T). Else quit.
-   Send to j &gt; i: PCS\_Pi((C,t),Pj,T)
-   Await from j &gt; i: PCS\_Pj((C,t),Pi,T). Else abort.
-   Send to j &lt; i: PCS\_Pi((C,t),Pj,T)

For *t=2...n/2* do:

-   Await from j &lt; i: PCS\_Pj((C,t),Pi,T). Else resolve.
-   Send to j &gt; i: PCS\_Pi((C,t),Pj,T)
-   Await from j &gt; i: PCS\_Pj((C,t),Pi,T). Else resolve.
-   Send to j &lt; i: PCS\_Pi((C,t),Pj,T)

<!-- -->

-   Await from j &lt; i: PCS\_Pj((C,n/2+1),Pi,T), {C}\_PrivPj.
    Else resolve.
-   Send to j &gt; i: PCS\_Pi((C,n/2+1),Pj,T), {C}\_PrivPi
-   Await from j &gt; i: PCS\_Pj((C,n/2+1),Pi,T), {C}\_PrivPj.
    Else resolve.
-   Send to j &lt; i: PCS\_Pi((C,n/2+1),Pj,T), {C}\_PrivPi

*Abort* is a subprotocol, where T intervenes, it aims at cancelling the
entire protocol by asking T to longer accept to convert some
PCS\_Pi((C,t),Pj,T) into {C}\_PrivPi. T will either send back a signed
contract or agree to abort. However it may overturn its abort decision
if it realizes that Pi is dishonest.

*Resolve* is a subprotocol, where T intervenes, it aims at completing
the protocol by asking T to convert some PCS\_Pj((C,t),Pi,T) into
{C}\_PrivPj. If provided enough grounds for doing it, T will do so.

See [AsokanSchunter](/wiki/AsokanSchunter "wikilink") for an intuition on
abort/resolve.

Remarks:

-   The cascading message structure is so that Parties are committed to
    certain stages, e.g. when P2 sends its PCS\_P2((C,2),P3,T), it
    should mean that it has received PCS\_P1((C,1),P2,T).
-   This information allows the TTP to determine dishonest parties.
-   Colluded dishonest players must not be able to propagate an
    abort decision. The total number of rounds outnumbers the total
    number of abort requests that a coalition of *n-1* can perform.

Naive questions on this paper, so far
-------------------------------------

-   *Resiliency.* The authors make the assumption that any message will,
    eventually, reach its goal. This does not seem a realistic
    assumption, but nor does it seem a crucial assumption. Clarify.
-   *PCS.* How secure are they?

