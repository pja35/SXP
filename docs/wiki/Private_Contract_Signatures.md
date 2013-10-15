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

Scheme
------

Traditionally, \(\textrm{PCS}_B^A(m)\) is

\[\textrm{NI}\left((\textrm{CCE}^T(0,n)\wedge\textrm{Schnorr}_B)\vee(\textrm{CCE}^T(1,n)\wedge\textrm{Schnorr}_A)\right)(g^s,H(g^s,m))\]
with s random.

Why not try \(\textrm{PCS}_B^A(m)\) to be

\[\textrm{NI}\left(\textrm{CCE}^T(B,(\textrm{Pub}^B,v))\vee\textrm{CCE}^T(A,(\textrm{Pub}^A,v))\right)(g^s,H(g^s,m))\]
with s random?

To do
-----

Check duplication of a and c in and and or constructs.

Check whether non-interactive CCE under ephemeral key PubB and challenge
m is an established way of signing.
