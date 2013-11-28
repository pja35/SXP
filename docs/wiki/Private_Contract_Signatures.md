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

-   It can be created any of the Pi's in S and, in the eyes of an
    external party, faked by any other of the Pj's in S;
-   The creating Pi, or T, are able to convert it into
    \(\textrm{SIG}^T_{i}(m)\), and other Pj's can be convinced of this.

Some Cryptography
-----------------

The following must be understood first. In particular, the description
of PCS we give uses the notations introduced there.

Outline of [ElGamal encryption](/wiki/ElGamalSchnorr "wikilink"), which are
encryption and signature schemes.

Outline of [Sigma protocols](/wiki/Sigma_Protocols "wikilink"), which are
composable, interactive zero-knowledge proof schemes.

Standard scheme
---------------

Normally, a \(\textrm{PCS}_S(m)\) is

\[\textrm{NI}\left( \bigvee_{i\in S} \textrm{CCE}^T(i,n)\wedge\textrm{Schnorr}_i(g^s,H(g^s,m) \right)(g^s,H(g^s,m))\]
with s random.

Simplified scheme (Failed attempt)
----------------------------------

An \(\textrm{SPCS}^T_S(m)\) is

\[\textrm{NI}\left(\bigvee_{i\in S}\textrm{CCE}^T(H(m),(\textrm{Pub}^{P_i},v))\right)(g^s,H(g^s,m))\]
with s random. Intuitively:

-   It constitutes a proof that \(v=\{H(m)\}_{\textrm{Pub}^T}\) under
    [ElGamal](/wiki/ElGamal "wikilink"), with ephemeral key one of
    \(\{\textrm{Priv}^{P_i}\}_{i\in S}\).
-   In order to provide such a proof one needs to have the ephemeral
    key used.
-   Thus, whoever has done it, has admittedly signed m.
-   But in order to know which of the \(P_i\) has signed, one needs a
    proof of which of private keys was used.

To unravel it, means to convert \(\textrm{SPCS}^T_S(m)\) into the final
signature \(\textrm{SIG}^T_{i\in S}(m)\):

\[\textrm{NI}\left(\textrm{CCE}^T(H(m),(\textrm{Pub}^{P_i},v))\vee\textrm{CCD}^T(H(m),(\textrm{Pub}^{P_i},v))\right)(g^{s'},H(g^{s'},m))\]
with s' random. Intuitively:

-   In order to accomplish the conversion one needs to either have
    \(\textrm{Priv}_{P_i}\) used as ephemeral key, or to have
    \(\textrm{Priv}_T\).
-   It constitutes a proof that \(v=\{H(m)\}_{\textrm{Pub}^T}\) under
    [ElGamal](/wiki/ElGamal "wikilink") with ephemeral key
    \(\textrm{Priv}_{P_i}\), which amounts to \(P_i\) signing m.
-   No step discloses \(\textrm{Priv}_{P_i}\).

This scheme is simpler than the original scheme. It has dangerous
weaknesses, however:

-   By requiring that \(\textrm{Priv}_{P_i}\) be the ephemeral key for
    ElGamal encryption, we are imposing that the pairs
    \((\textrm{Priv}_{P_i},\textrm{Pub}^{P_i})\) and
    \((\textrm{Priv}_{T},\textrm{Pub}^{T})\) are based on the same
    Diffie-Hellman group. Altogether, this would mean that all pairs get
    generates with respect to the same group. This is non-traditional,
    and perhaps it weakens security? Nevertheless, notice that precise,
    fixed groups have been recommended for use, for instance in [RFC
    5114](http://tools.ietf.org/html/rfc5114#page-4).
-   The same ephemeral key \(\textrm{Priv}_{P_i}\) is reused over and
    over, which means that the cyphertext is always
    \(H(m)g^{\textrm{Priv}_{P_i}\textrm{Priv}_{T}}\) but since \(H(m)\)
    is known, so is \(g^{\textrm{Priv}_{P_i}\textrm{Priv}_{T}}\), and so
    the next time Pi is immediately identified as the creator of
    the SPCS.
-   Worse even, Pi's signature, once it has been used once, can then be
    forged, since signing amounts to multiply by
    \(g^{\textrm{Priv}_{P_i}\textrm{Priv}_{T}}\) in this scheme.

To do
-----

Check duplication of a and c in and and or constructs.

Give simulators for CCE and CCT.
