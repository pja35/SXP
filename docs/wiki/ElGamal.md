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

Alice needs a random ephemeral key \(w\).

  
Alice encrypts \(m\in G\) as \(\{m\}_{PubKB}=(g^w,{g^x}^w m)\).

Bob decrypts \((u,v)\) as \(v/u^x\).

Indeed, if Alice was honest it should be that
\(v/u^x=g^{xw}m/g^{wx}=m\).


