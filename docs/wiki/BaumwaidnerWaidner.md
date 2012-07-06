---
title: BaumwaidnerWaidner
permalink: wiki/BaumwaidnerWaidner/
layout: wiki
---

This paper is referenced by [AsokanSchunter](/wiki/AsokanSchunter "wikilink")
and [MukhamedovRyan](/wiki/MukhamedovRyan "wikilink"). It is an abuse-free
asynchronous multi-party optimistic contract signing protocols. Say
there are *n* parties:

-   it requires *(n+1)n(n-1)* messages which may be high;
-   but only *n+2* rounds which is optimal;
-   in the optimistic case the end signed result is *(C,n+1)*, thus the
    contract *C* has to specify *n*;
-   but in the pessimistic case the end signed result is *(C,i)* plus
    some countersigning by *T*.

It uses only standard asymmetric cryptography.

Outline of the protocol
-----------------------

-   Send to j=1...n : (C,1)\_Pi.
-   Await from j=1...n : (C,1)\_Pj. Else resolve.

For *t=2...n+1* do:

-   Send to j=1...n : ((C,t-1)\_P1...(C,t-1)\_Pn)\_P\_i,(C,t)\_Pi
-   Await from j=1...n : ((C,t-1)\_P1...(C,t-1)\_Pn)\_P\_j,(C,t)\_Pj.
    Else resolve.

*Resolve* is a subprotocol, where T intervenes, it aims at completing
the protocol by asking T to take a decision whether the contract is
valid and provide a certificate of that. If provided enough grounds for
doing it, T will do so.
