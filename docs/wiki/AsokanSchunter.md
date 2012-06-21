---
title: AsokanSchunter
permalink: wiki/AsokanSchunter/
layout: wiki
---

The link does not grant access to the whole document, but was recovered.
This is a recent review paper by specialists, hence a good entry to the
topic. The comments here are biased towards SXP.

The paper Describes fair exchange protocols as ways to exchange things
between distrustful parties in such a way that either both parties
receive what they expect or neither parties does. Includes receipts for
payments, certified mail, and contract signatures more specfically.

Fair Exchange with a Trusted Third Party
----------------------------------------

Requires a referee. In the setting of SXP, maybe the market could maybe
play such a role, but this is costly.

The protocols that implement this have a simple common pattern. Alice
(A) and Bob (B) need to sign contract (C) with there private keys
PrivKA/PrivKB, Trent (T) is the Trusted Third Party. See
[here](http://en.wikipedia.org/wiki/Security_protocol_notation) for the
notation.

\[A\rightarrow B:\{\{C\}_{PrivKA}\}_{K}\]

\[A\rightarrow T:K\]

\[B\rightarrow T:\{\{C\}_{PrivKA}\}_{K},\{C\}_{PrivKB}
:<math>T\rightarrow B:K\]

\[T\rightarrow A:\{C\}_{PrivKB}\]

To be honest I am not sure why going through K is necessary, this looks
like an optimization for Digital Certified Mail. In the explanations all
this seems to boil down to the boring protocol:

\[A\rightarrow T:\{C\}_{PrivKA}\]

\[B\rightarrow T:\{C\}_{PrivKB}\]

\[T\rightarrow A:\{C\}_{PrivKB}\]

\[T\rightarrow B:\{C\}_{PrivKA}\]

Optimistic Fair Exchange
------------------------

Does not require a referee, unless there is a conflict. If possible this
is what we should do.

The idea is that the TTP should be able to either:

-   cancel the contract;
-   act instead of the dishonest party to replace him to sign the
    contract;
-   punish the dishonest party by lowering his trust or so.

The authors point out two problems with that:

-   providing quality replacement, i.e. ideally allowing the TTP to sign
    for the dishonest party;
-   making sure that no one is wrongly blamed just because of loss of
    transmissions etc., i.e. ideally allowing either party to drop off
    the protocol anytime.

Solving these issues is done successively by the two next subsections.

### Verifiable escrow

Wouldn't it solve the problem if Alice was able to give Bob something
which:

-   Is the contract signed by her, but protected under PubKT;
-   A proof that this is indeed what she is giving to him.

Let us call this magic thing VE\_A.

The protocol would then run as follows:

\[A\rightarrow B:VE_A\]

\[B\rightarrow A:\{C\}_{PrivKB}\]

\[A\rightarrow B:\{C\}_{PrivKA}\]

If Alice disappears after step one, no worries, there is a recovery
protocol. Indeed Bob can go to the TTP with the contract signed by him,
and get from the TTP the contract signed by her. It is important that he
brings the contract signed by him, so that the TTP takes it and gives it
to Alice when she wakes up. More precisely the recovery protocol is:

\[B\rightarrow T:\{C\}_{PrivKB}, VE_A\]

\[T\rightarrow B:\{C\}_{PrivKA}\]

\[T\rightarrow A:\{C\}_{PrivKB}\]

This sounds great. Apparently these VE\_A exist, they are called
Verifiable Escrows.

### Asynchrony

The protocol outlined just above does not quite work due to the second
problem. Indeed Bob can stop the protocol anytime without problem: he is
either not engaged or is engaged but Alice is also engaged. But the
reverse is not true. Alice in the first step engages herself somehow.
Even if it is true that Bob cannot get the doubly signed contract unless
he gives it to her; she is still in a weaker situation than Bob: she
cannot stop the protocol, whereas he can. One solution is to impose a
time-limit but the authors argue that short time limits are impractical,
whereas long ones are a problem for Alice.

Solving this without time-limits is called the asynchronous model. The
authors explain that the following minor modification is secure and
minimal for the asynchronous model:

XXXXX

Other stuff
-----------

Exchanging in parts. These are protocols where the contracts get
exchanged gradually. For the purpose of SXP this is a bit inelegant.
Using secure H/W. These are protocols use smartcards etc. For the
purpose of SXP this is a bit unpractical.

Further reading
===============

Going multi-party: \[9,37\]

P2P context \[10\]

Using peers as TTP \[23\]

To analyze communication complexity: \[38\]

To analyze computational complexity: It is suggested that more efficient
VE schemes can be done via \[6\].

Naive reactions to this paper
=============================

Why not go for the following natural solutions: the contract C wears the
mention "This contract is not valid if not signed by PubKA, and then by
PubKB by time t". This way Alice can safely sign it with PrivB and send
it to Bob. Bob may not sign it but then the contract is not valid. To
validate he must sign it and have it time-stamped by time t... OK maybe
there would be two problems:

-   Alice is not ensured to get her copy of the valid contract;
-   The time-stamping service would need to be solicited all
    too frequently.
