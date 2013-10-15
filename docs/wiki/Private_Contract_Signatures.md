---
title: Private Contract Signatures
permalink: wiki/Private_Contract_Signatures/
layout: wiki
---

Private Contract Signatures are a cryptography primitive which we need
for the [Secure Contract Signing
Protocol](/wiki/Secure_Contract_Signing_Protocol "wikilink").

Those are constructed via non-interactive zero-knowledge proofs
constructions, and based upon the [Decisional
Diffie-Hellman](http://en.wikipedia.org/wiki/Decisional_Diffie%E2%80%93Hellman_assumption)
assumption.

It was introduced in [Abuse-free optimistic contract
signing](http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.118.4142)
by Garay, Jakobsson, MacKenzie (1999). Comment on the usefulness of this
paper to the project [here](/wiki/GarayJakobssonMackenzie "wikilink").

This page aims to give an outline of how they work, and how to implement
them.

Some Cryptography
-----------------

The following must be understood first. In particular, the description
of PCS we give uses the notations introduced there.

Outline of [ElGamal encryption](/wiki/ElGamalSchnorr "wikilink"), which are
encryption and signature schemes.

Outline of [Sigma protocols](/wiki/Sigma_Protocols "wikilink"), which are
composable, interactive zero-knowledge proof schemes.

Simplified scheme
-----------------

An \(\textrm{SPCS}_{AB}(m)\) is

\[\textrm{NI}\left(\textrm{CCE}^T(m,(\textrm{Pub}^A,v))\vee\textrm{CCE}^T(m,(\textrm{Pub}^B,v))\right)(g^s,H(g^s,m))\]
with s random. Intuitively:

-   It constitutes a proof that \(v=\{m\}_{\textrm{Pub}^T}\) under
    [ElGamal](/wiki/ElGamal "wikilink").
-   In order to provide such a proof one needs to have the ephemeral
    key used.
-   It also constitutes a proof that the ephemeral key used is either
    Alice's or Bob's private key.
-   Thus, whoever has done it, has admittedly signed m.
-   But in order to know whom of Alice or Bob has signed, one needs a
    proof of which of private keys was used.

Thus unravel it, we need \(\textrm{USPCS}_B(m)\)

\[\textrm{NI}\left(\textrm{CCE}^T(m,(\textrm{Pub}^B,v))\vee\textrm{CCD}^T(m,(\textrm{Pub}^B,v))\right)(g^{s'},H(g^{s'},m))\]
with s' random. Intuitively:

-   It constitutes a proof that \(v=\{m\}_{\textrm{Pub}^T}\) under
    [ElGamal](/wiki/ElGamal "wikilink").
-   In order to provide such a proof one need to either have
    \(\textrm{Priv}_B\) used, or have \(\textrm{Priv}_T\).
-   In any case it constitutes a proof that the ephemeral key used was
    Bob's private key.

A signature \(\textrm{SIG}_B(m)\) is a pair

\[\left(\textrm{SPCS}_B(m), \textrm{USPCS}_B^A(m)\right).\] Notice that:

-   No step discloses \(\textrm{Priv}_B\).
-   Yet is constitutes a proof that \(v=\{m\}_{\textrm{Pub}^T}\) with
    ephemeral key \(\textrm{Priv}_B\), which amounts to signing m.

Normal scheme
-------------

Normally, a \(\textrm{PCS}_B^A(m)\) is

\[\textrm{NI}\left((\textrm{CCE}^T(0,n)\wedge\textrm{Schnorr}_B)\vee(\textrm{CCE}^T(1,n)\wedge\textrm{Schnorr}_A)\right)(g^s,H(g^s,m))\]
with s random.

To do
-----

Check duplication of a and c in and and or constructs.

Check whether non-interactive CCE under ephemeral key PubB and challenge
m is an established way of signing.

Give simulators for CCE and CCT.
