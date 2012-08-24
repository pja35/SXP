---
title: Secure Contract Signing Protocol
permalink: wiki/Secure_Contract_Signing_Protocol/
layout: wiki
---

The Secure Contract Signing protocol of [SXP](/wiki/Main_Page "wikilink") is a
[Protocol](/wiki/Protocols "wikilink").

**Current status: The version 1 protocol is defined.**

Aims of the protocol
--------------------

To allow several parties to sign a contract securely.

What do we mean by secure?

-   *Fairness*. No party should be left having sent his signature on the
    contract; but not having received that of the other parties.
-   *Timeliness*. At every point any party can decide to leave. If that
    happens either the entire exchange either gets aborted, or can be
    completed without this party.
-   *Abuse-freeness*. No party is not able produce to an external party
    a proof that the others were willing to sign the contract, which
    she aborted.

Do we need a Trusted Third Party?

-   With an online TTP this can be easily done; but this is costly
-   Without any form of TTP this has to rely on progressive exchange
-   There are solutions to invoke the TTP only when things go wrong.

Which cryptographic primitive are we going to rely upon?

-   Designated Verifiable Escrows
-   Private Contract Signatures

The Protocol
------------

Main\_i :

` For k=1...n+2 :`  
`    broadcast Prom_i(k)`  
`    i awaits and checks until she forms Claim_i(k)`  
`    failing that, Resolve_i(k) and _exit_ otherwise you may produce a DishonestClaim_i(k)!`

Resolve\_i(k):

` broadcast E_T(S_Pi(Claim_i(k-1)))`  
` i gets either ResolveToken`  
` or gets AbortToken`  
` _exit_ otherwise you may produce a DishonestClaim_i(k)!`

ResolveT:

` Await and check S_Pi(Claim_i(k-1)).`  
` % j was dishonest and i shows it`  
` If PossiblyHonestClaims has some S_Pj(Claim_i(k')) with k'<k-2, then`  
`    constitute DishonestClaim_j(k) into DishonestClaims, `  
`    remove j from PossiblyHonestClaims`  
`    broadcast HonestyToken`  
`% i was dishonest and i shows it`  
` If i is in PossiblyHonestClaims or DishonestClaims already, and if this is no duplicate, then`  
`    constitute DishonestClaim_i(k) into DishonestClaims, `  
`    remove i from PossiblyHonestClaims,`  
`    optionally, broadcast HonestyToken `  
`    exit.`  
`%  i was dishonest and j shows it`  
` If PossiblyHonestClaims or DishonestClaims has some S_Pj(Claim_i(k')) with k'>k, then`  
`    constitute DishonestClaim_i(k) into DishonestClaims, `  
`    remove i from PossiblyHonestClaims, `  
`    optionally, broadcast HonestyToken`  
`    exit.`  
`% now the claim is possibly honest`  
`% intial, claim with promises, wins`  
` If (k>1 and PossiblyHonestsClaims is empty) or !optimistic, then`  
`    set optimistic to false`  
`    broadcast ResolveToken`  
`    exit.`  
`% initial, claim without nothing, triggers the piling up of all further possibly honest claims with promises, unless these get overturned`  
` If (k==1 or PossiblyHonestsClaims is not empty) and optimistic, then`  
`    add S_Pi(Claim_i(k-1)) to PossiblyHonestsClaims`  
`    broadcast AbortToken`  
`    exit.`

Some related papers we must understand first
--------------------------------------------

\[<http://books.google.fr/books?hl=en&lr>=&id=DUFqRPNqBrQC&oi=fnd&pg=PA365&dq=%22optimistic+fair%22&ots=HSsEcwYi5v&sig=d4Z6to7fQ06fl-OXlLkQKLIP8Cc\#v=onepage&q=%22optimistic%20fair%22&f=false
Review of optimistic fair exchange\] by Asokan, Schunter, (2009).
Comment on the usefulness of this paper to the project
[here](/wiki/AsokanSchunter "wikilink"). **Done.** Good entry point.

[An Optimistic Fair Protocol for Aggregate
Exchange](http://ieeexplore.ieee.org/xpl/freeabs_all.jsp?arnumber=5381051)
by Liu (2009). Comment on the usefulness of this paper to the project
[here](/wiki/Liu "wikilink").

[Improved multi-party contract
signing](http://www.cs.bham.ac.uk/~mdr/research/papers/pdf/07-fc07.pdf)
by Mukhamedov, Ryan (2007). Comment on the usefulness of this paper to
the project [here](/wiki/MukhamedovRyan "wikilink"). **Done.** Abuse-free
asynchronous multi-party optimistic contract signing.

[Payment Scheme for Multi-Party Cascading P2P
Exchange](http://www.springerlink.com/content/408876235155l787/) by Liu,
Zhao, (2007). Comment on the usefulness of this paper to the project
[here](/wiki/LiuZhao "wikilink").

[Formal Analysis of Multiparty Contract
Signing](http://www.springerlink.com/content/f301555p62357606/) by
Chadha, Kremer, Scedrov (2006). **Recommended.** Comment on the
usefulness of this thesis to the project
[here](/wiki/ChadhaKremerScedrov "wikilink").

[An exchange protocol for alternative
currencies](http://ieeexplore.ieee.org/xpl/freeabs_all.jsp?arnumber=1428498)
by Hao, Havey, Turner, (2005). Comment on the usefulness of this paper
to the project [here](/wiki/HaoHaveyTurner "wikilink").

[An Optimistic Fair Protocol for P2P Chained
Transaction](http://www.springerlink.com/content/72552jg5jk322154/) by
Liu, Fu, Zhang, (2005). Comment on the usefulness of this paper to the
project [here](/wiki/LiuFuZhang "wikilink").

[A Fair and Reliable P2P E-Commerce Model Based on Collaboration with
Distributed
Peers](http://www.springerlink.com/content/p3543433t71uh2n6/) by Sur,
Jung, Yang, Rhee (2005). Comment on the usefulness of this paper to the
project [here](/wiki/SurJungYangRhee "wikilink").

[Optimistic Fair Exchange Based on Publicly Verifiable Secret
Sharing](http://www.springerlink.com/content/28b0t21f4e5fhfb9/) by
Avoine, Vaudenay (2004). Comment on the usefulness of this paper to the
project [here](/wiki/AvoineVaudenay "wikilink").

[Formal Analysis of Optimistic Fair Exchange
Protocols](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.58.9931&rep=rep1&type=pdf)
by Kremer, (2004). Comment on the usefulness of this thesis to the
project [here](/wiki/Kremer "wikilink").

[Verifiable Encryption of Digital Signatures and
Applications](http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.66.4697)
by Ateniese, (2004). For efficient Verifiable Escrows. Comment on the
usefulness of this paper to the project [here](/wiki/Ateniese "wikilink").

[Trusted computing
platforms.](http://www.hpl.hp.com/techreports/2002/HPL-2002-221.pdf)
Pearson, ed. (2003) Comment on the usefulness of this paper to the
project [here](/wiki/Pearson "wikilink"). Multi-party abuse-free contract
signing. Asynchronous?

[A multi-party optimistic non-repudiation
protocol](http://www.springerlink.com/content/beq9v0d41wd7g462/) by
Markowitch, Kremer, (2001). Comment on the usefulness of this paper to
the project [here](/wiki/MarkowitchKremer "wikilink").

[Round-optimal and abuse-free optimistic multi-party contract
signing](http://www.springerlink.com/content/ruf1079b2vgjmm1m/) by
Baum-Waidner, Waidner (2000). Comment on the usefulness of this paper to
the project [here](/wiki/BaumwaidnerWaidner "wikilink"). **Done.** P2P
context. Abuse-free asynchronous multi-party optimistic contract
signing.

[Abuse-free optimistic contract
signing](http://www.springerlink.com/content/evrjx3tdjgxj2pmp/) by
Garay, Jakobsson, MacKenzie (1999). Comment on the usefulness of this
paper to the project [here](/wiki/GarayJakobssonMackenzie "wikilink").
**Recommended.** Introduces Private Contract Signatures.

[Optimistic Synchronous Multi-Party Contract
Signing](http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.20.3562)
by Asokan, Baum-waidner, Schunter , Waidner (1998). Comment on the
usefulness of this paper to the project [here](/wiki/ABSW98 "wikilink").

\[<http://ieeexplore.ieee.org/xpl/login.jsp?tp>=&arnumber=674825&url=http%3A%2F%2Fieeexplore.ieee.org%2Fxpls%2Fabs\_all.jsp%3Farnumber%3D674825
Efficient and practical fair exchange with offline TTP\] by Bao, Deng,
Mao, (1998) Comment on the usefulness of this paper to the project
[here](/wiki/BaoDengMao "wikilink"). Multi-party abuse-free contract signing.
Asynchronous?

[Opimal efficiency of optimistic contract
signing](http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.43.4716)
by Pfitzmann, Schunter, Waidner (1998) Comment on the usefulness of this
paper to the project [here](/wiki/PfitzmannSchunterWaidner "wikilink").
Communication complexity.

[Fair exchange with a semi-trusted third
party](http://www.cs.unc.edu/~reiter/papers/1997/CCS1.pdf) by Franklin,
Reiter, (1997). Comment on the usefulness of this paper to the project
[here](/wiki/FranklinReiter "wikilink"). Using peers as TTP.
