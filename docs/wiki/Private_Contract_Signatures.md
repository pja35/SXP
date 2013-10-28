---
title: Private Contract Signatures
permalink: wiki/Private_Contract_Signatures/
layout: wiki
---

Private Contract Signatures are a cryptography primitive which we need
for the [Secure Contract Signing
Protocol](/wiki/Secure_Contract_Signing_Protocol "wikilink").

It was introduced in [Abuse-free optimistic contract
signing](http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.118.4142)
by Garay, Jakobsson, MacKenzie (1999).

This page aims to give an outline of what they achieve, and how to
implement them.

Specifications
--------------

\(\textrm{SPCS}^T_S(m)\) denotes a Private Contract Signatures by Pi in
S on contract m with Trusted Third Party T. The object is such that:

-   It can be created by Pi and, in the eyes of an external party, faked
    by Pj;
-   Pi and T are able to convert it into \(\textrm{SIG}^T_{i}(m)\), and
    Pj can be convinced of this.

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

An \(\textrm{SPCS}^T_S(m)\) is

\[\textrm{NI}\left(\bigvee_{i\in S}\textrm{CCE}^T(H(m),(\textrm{Pub}^{P_i},v))\right)(g^s,H(g^s,m))\]
with s random. Intuitively:

-   It constitutes a proof that \(v=\{H(m)\}_{\textrm{Pub}^T}\) under
    [ElGamal](/wiki/ElGamal "wikilink").
-   In order to provide such a proof one needs to have the ephemeral
    key used.
-   It also constitutes a proof that the ephemeral key was one of
    \(\{\textrm{Priv}^{P_i}\}_{i\in S}\).
-   Thus, whoever has done it, has admittedly signed m.
-   But in order to which \(P_i\) has signed, one needs a proof of which
    of private keys was used.

To unravel it, means to convert \(\textrm{SPCS}^T_S(m)\) into the final
signature \(\textrm{SIG}^T_{i\in S}(m)\):

\[\textrm{NI}\left(\textrm{CCE}^T(H(m),(\textrm{Pub}^{P_i},v))\vee\textrm{CCD}^T(H(m),(\textrm{Pub}^{P_i},v))\right)(g^{s'},H(g^{s'},m))\]
with s' random. Intuitively:

-   In order to accomplish the conversion one needs to either have
    \(\textrm{Priv}_{P_i}\) used as ephemeral key, or to have
    \(\textrm{Priv}_T\).
-   It constitutes a proof that \(v=\{H(m)\}_{\textrm{Pub}^T}\) under
    [ElGamal](/wiki/ElGamal "wikilink") with ephemeral key
    \(\textrm{Priv}_{P_i}\), which amounts to \(P_i\) signing m..
-   No step discloses \(\textrm{Priv}_{P_i}\).

Standard scheme
---------------

Normally, a \(\textrm{PCS}_B^A(m)\) is

\[\textrm{NI}\left((\textrm{CCE}^T(0,n)\wedge\textrm{Schnorr}_B)\vee(\textrm{CCE}^T(1,n)\wedge\textrm{Schnorr}_A)\right)(g^s,H(g^s,m))\]
with s random.

To do
-----

There might be a pb with g having to be the same in Trent's and the
Party's for the simplified scheme to work.

Check duplication of a and c in and and or constructs.

Check whether non-interactive CCE under ephemeral key PubB and challenge
m is an established way of signing.

Give simulators for CCE and CCT.
