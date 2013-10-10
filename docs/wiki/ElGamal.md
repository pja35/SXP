---
title: ElGamal
permalink: wiki/ElGamal/
layout: wiki
---

Fix \(p\) a prime and \(g\) an integer. The powers of \(g\) form a
subgroup \(G\) inside the group \(Z_p\), which is that of integers
modulo \(p\). The choice of these \(p\) and \(g\) is important so that
they meet the [Decisional
Diffie-Hellman](http://en.wikipedia.org/wiki/Decisional_Diffie%E2%80%93Hellman_assumption)
assumption; but there are standard techniques for doing that.

Say Bob generates \(PrivKB=x, PubKB=g^x\) and makes the latter public.

ElGamal encryption
------------------

  
Alice encrypts \(m\in G\) as \(\{m\}_{PubKB}=(g^r,{g^x}^rm)\).

Bob decrypts \((a,b)\) as \(b/a^x\).

Indeed, \(b/a^x=g^{xr}m/g^{rx}=m\).

Schnorr signatures
------------------

  
Bob signs \(m\in G_q\) as \(SIG_B(m)=(r-xe,H(m.g^r))\).

Alice verifies \((s,e)\) checking that \(e=H(m.g^s {g^x}^e)\).

Indeed, \(g^{r-xe}{g^x}^e=g^r\).


