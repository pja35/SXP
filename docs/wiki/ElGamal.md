---
title: ElGamal
permalink: wiki/ElGamal/
layout: wiki
---

Take p a prime and g an integer. The powers of g form a subgroup Gq
(having q elements) inside the group Zp (having p elements), which is
that of integers modulo p. The choice of these p and q is important so
that they meet the [Decisional
Diffie-Hellman](http://en.wikipedia.org/wiki/Decisional_Diffie%E2%80%93Hellman_assumption)
assumption; but there are standard techniques for doing that. Thanks to
that:

  
Bob generates \(PrivKB=x, PubKB=g^x\) and makes the latter public.

Alice encrypts \(m\in G_q\) as \(\{m\}_{PubKB}=(y^rm,g^r)\).

Bob decrypts \((a,b)\) as \(a/b^x\).

Indeed, \(a/b^x=y^rm/g^{rx}=g^{xr}m/g^{rx}=m\).


