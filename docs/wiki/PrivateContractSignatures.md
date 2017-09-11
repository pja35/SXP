---
title: Private Contract Signatures
permalink: wiki/PrivateContractSignatures/
layout: wiki
---

Private Contract Signatures are a cryptography primitive which we need
for the [Secure Contract Signing
Protocol](/SXP/wiki/SecureContractSigningProtocol "wikilink").

It was introduced in [Abuse-free optimistic contract
signing](http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.118.4142)
by Garay, Jakobsson, MacKenzie (1999).

This page aims to give an outline of what they achieve, and how to
implement them.

Specifications
--------------

$\textrm{SPCS}^T_S(m)$ denotes a Private Contract Signatures by $Pi$ in
$S$ on contract m with Trusted Third Party $T$. The object is such that:

-   It can be created any of the $Pi$'s in $S$ and, in the eyes of an
    external party, faked by any other of the $Pj$'s in $S$;
-   The creating $Pi$, or $T$, are able to convert it into
    $\textrm{SIG}^T_{i}(m)$, and other $Pj$'s can be convinced of this.

Some Cryptography
-----------------

The following must be understood first. In particular, the description
of $PCS$ we give uses the notations introduced there.

Outline of [ElGamal encryption](/SXP/wiki/ElGamal "wikilink"), which are
encryption and signature schemes.

Outline of [Sigma protocols](/SXP/wiki/SigmaProtocols "wikilink"), which are
composable, interactive zero-knowledge proof schemes.

Standard scheme
---------------

A **private contract signature** $\textrm{PCS}_S(m,n)$ is

$$\textrm{NI}\left( \bigvee_{i\in S} (\textrm{CCE}^T(i,n)\wedge\textrm{Schnorr}_i) \right)(m)$$

Intuitively:

-   It constitutes a proof that one of the $Pi$ has passed the Schnorr
    identification test on challenge $H(g_i^{s_i},m)$.
-   This amounts to having signed $m$, just like in the Schnorr
    signature scheme.
-   Except that we do not know, yet, which of the $Pi$ has signed.
-   In order to convert this into a signature by $Pi$, one must prove
    that the cyphertext $n$ has content the integer $i$.

A **private contract signature revealer** $\textrm{RPCS}_i(n)$ is

$$\textrm{NI}\left( \textrm{CCE}^T(i,n)\vee\textrm{CCD}^T(i,n) \right)(m)$$

Intuitively:

-   It constitutes a proof that the cyphertext $n$ has content the
    integer $i$.
-   Either $Pi$ or $T$ can produce this.

A **contract signature** $\textrm{SIG}_i(m)$ is

$$\left(\textrm{PCS}_S(m,n),\textrm{RPCS}_i(n)\right)$$

Intuitively:

-   It constitutes a combined proof that $Pi$ has passed the Schnorr
    identification test on challenge $H(g_i^{s_i},m)$.
-   This amounts to having signed $m$, just like in the Schnorr
    signature scheme.

Simplified scheme (Failed attempt)
----------------------------------

An $\textrm{SPCS}^T_S(m)$ is

$$\textrm{NI}\left(\bigvee_{i\in S}\textrm{CCE}^T(H(m),(\textrm{Pub}^{Pi},v))\right)(g^s,H(g^s,m))$$

with $s$ random. Intuitively:

-   It constitutes a proof that $v=\{H(m)\}_{\textrm{Pub}^T}$ under
    [ElGamal](/SXP/wiki/ElGamal "wikilink"), with ephemeral key one of
    $\{\textrm{Priv}^{Pi}\}_{i\in S}$.
-   In order to provide such a proof one needs to have the ephemeral
    key used.
-   Thus, whoever has done it, has admittedly signed $m$.
-   But in order to know which of the $Pi$ has signed, one needs a
    proof of which of private keys was used.

To unravel it, means to convert $\textrm{SPCS}^T_S(m)$ into the final
signature $\textrm{SIG}^T_{i\in S}(m)$:

$$\textrm{NI}\left(\textrm{CCE}^T(H(m),(\textrm{Pub}^{Pi},v))\vee\textrm{CCD}^T(H(m),(\textrm{Pub}^{Pi},v))\right)(g^{s'},H(g^{s'},m))$$

with $s'$ random. Intuitively:

-   In order to accomplish the conversion one needs to either have
    $\textrm{Priv}_{Pi}$ used as ephemeral key, or to have
    $\textrm{Priv}_T$.
-   It constitutes a proof that $v=\{H(m)\}_{\textrm{Pub}^T}$ under
    [ElGamal](/SXP/wiki/ElGamal "wikilink") with ephemeral key
    $\textrm{Priv}_{Pi}$, which amounts to $Pi$ signing $m$.
-   No step discloses $\textrm{Priv}_{Pi}$.

This scheme is simpler than the original scheme. It has dangerous
weaknesses, however:

-   By requiring that $\textrm{Priv}_{Pi}- be the ephemeral key for
    ElGamal encryption, we are imposing that the pairs
    $(\textrm{Priv}_{Pi},\textrm{Pub}^{Pi})$ and
    $(\textrm{Priv}_{T},\textrm{Pub}^{T})$ are based on the same
    Diffie-Hellman group. Altogether, this would mean that all pairs get
    generates with respect to the same group. This is non-traditional,
    and may weaken security? Nevertheless, notice that precise,
    fixed groups have been recommended for use, for instance in [RFC
    5114](http://tools.ietf.org/html/rfc5114#page-4).
-   The same ephemeral key $\textrm{Priv}_{Pi}$ is reused over and
    over, which means that the cyphertext is always
    $H(m)g^{\textrm{Priv}_{Pi}\textrm{Priv}_{T}}$ but since $H(m)$
    is known, so is $g^{\textrm{Priv}_{Pi}\textrm{Priv}_{T}}$, and so
    the next time $Pi$ is immediately identified as the creator of
    the $SPCS$.
-   Worse even, $Pi$'s signature, once it has been used once, can then be
    forged, since signing amounts to multiply by
    $g^{\textrm{Priv}_{Pi}\textrm{Priv}_{T}}$ in this scheme.

