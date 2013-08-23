---
title: Sigma Protocols
permalink: wiki/Sigma_Protocols/
layout: wiki
---

In general
----------

Sigma protocols involve a verifier (A) and prover (B). They suppose that
Alice and Bob share a public input \(v\) and agree on an efficiently
testable relation \(R\), and that Bob pretends to have a private input
\(w\):

-   such that \((v,w)\in R\);
-   which he does does not want to disclose.

Through the protocol, Bob will seek to convince Alice.

The protocol has three rounds:

\[B\rightarrow A: a=a(v,R,w)\]

\[A\rightarrow B: c=c(v,R,a)\]

\[B\rightarrow B: r=r(v,R,w,a,c,w)\] Lastly, Alice checks that Bob
response \(r\) to her challenge \(r\) is valid. This explains what the
last two rounds are for. The first round is there out of technical
necessity: Bob chooses this \(a\) as a mask for passing the challenge
without disclosing \(w\).

Example
-------

Here is an example based on Discrete Logarithms. Take \(p\) a prime and
\(g\) an integer. The powers of \(g\) form a subgroup \(G_q\) (having
\(q\) elements) inside the group \(Z_p\) (having \(p\) elements), which
is that of integers modulo \(p\). The choice of these \(p\) and \(g\) is
important so that they meet the [Decisional
Diffie-Hellman](http://en.wikipedia.org/wiki/Decisional_Diffie%E2%80%93Hellman_assumption)
assumption; but there are standard techniques for doing that.

-   Public input \(v\in G_q\).
-   Agreed relation \((v,w)\in R \Leftrightarrow g^w=v\).
-   Private input \(w\).
-   Bob will need some random \(u\in Z_p\), Alice will need some random
    \(c\in Z_p\).

The protocol has three rounds:

\[B\rightarrow A: a=g^u\]

\[A\rightarrow B: c\]

\[B\rightarrow B: r=wc+u\] Alice validates Bob response by checking that
\(g^r=ah^c\). Indeed,

\[g^r=g^{wc}g^u=h^ca.\]
