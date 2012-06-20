---
title: AsokanSchunter
permalink: wiki/AsokanSchunter/
layout: wiki
---

The link does not grant access to the whole document, however I
recovered it by other means. This is a recent review paper by
specialists, hence a good entry to the topic. The comments here are
biased towards SXP.

Fair Exchange
=============

Describes fair exchange protocols as ways to exchange things between
distrustful parties in such a way that either both parties receive what
they expect or neither parties does. Includes receipts for payments,
certified mail, and contract signatures more specfically.

Types of Fair Exchanges (FX)
============================

With a Trusted Third Party (TTP)
--------------------------------

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
this seems to be a variation of the boring protocol:

\[A\rightarrow T:\{C\}_{PrivKA}\]

\[B\rightarrow T:\{C\}_{PrivKB}\]

\[T\rightarrow A:\{C\}_{PrivKB}\]

\[T\rightarrow B:\{C\}_{PrivKA}\]

Being Optimistic
----------------

Does not require a referee, unless there is a conflict.

### Optimistic FX

### Verifiable escrow of digital signatures

### Verifiable escrow using pre-issued coupons

Exchanging in parts
-------------------
