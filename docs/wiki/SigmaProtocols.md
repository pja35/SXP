---
title: Sigma Protocols
permalink: wiki/SigmaProtocols/
layout: wiki
---

Those are key to understanding [Private Contract
Signatures](/SXP/wiki/PrivateContractSignatures "wikilink"). A good reference is
[ here](http://www.cs.au.dk/~ivan/Sigma.pdf).

In general
----------

Sigma protocols involve a verifier ($A$) and prover ($B$). They suppose that
Alice and Bob share a public input $v$ and agree on an efficiently
testable relation $R$, and that Bob pretends to have a private input
$w$:

-   such that $(v,w)\in R$;
-   which he does does not want to disclose.

Through the protocol, Bob will seek to convince Alice.

The protocol has three rounds:

$$B\rightarrow A: a=a(v,R,w)$$

$$A\rightarrow B: c=c(v,R,a)$$

$$B\rightarrow A: r=r(v,R,w,a,c,w)$$

Lastly, Alice checks that Bob
response $r$ to her challenge $c$ is valid. This explains what the
last two rounds are for. The first round is there out of technical
necessity: Bob chooses this $a$ as a mask for passing the challenge
without disclosing $w$.

Toolbox
-------

Here are examples of Sigma protocols based on the hardness of Discrete
Logarithms. Fix $p$ a prime and $g$ an integer. The powers of $g$
form a subgroup $G$ inside the group $Z_p$, which is that of
integers modulo $p$. The choice of these $p$ and $g$ is important
so that they meet the [Decisional
Diffie-Hellman](http://en.wikipedia.org/wiki/Decisional_Diffie%E2%80%93Hellman_assumption)
assumption; but there are standard techniques for doing that.

**Ex. 1: Schnorr identification protocol**

Fix $g$ an integer.

-   Public input $v=\textrm{Pub}^B$.
-   Agreed relation $(v,w)\in R \Leftrightarrow g^w=v$.
-   Private input $w=\textrm{Priv}_B$.
-   Bob will need some random ephemeral $s\in Z_p$.
-   Alice will need some random $c\in Z_p$.

The protocol has three rounds:

$$B\rightarrow A: a=g^s$$

$$A\rightarrow B: c$$

$$B\rightarrow A: r=wc+s$$

Alice validates Bob's response by checking
that $g^r=v^ca$. Indeed, if Bob was honest it should be that

$$g^r={g^w}^c g^s=v^ca$$

**Denote by
$(a,c)\mapsto\textrm{Schnorr}_B(a,c)$
the response of this protocol,
done by prover $B$ with mask $a$ and under challenge $c$.**

Beware that $B$ must not be left to freely choose the challenge, otherwise
he can generate valid triplets through the following simulator:

-   Pick $c,r$.
-   Let $a=g^{r}v^{-c}$.

Indeed, you then have $g^{r}=v^{c}a$ so that the run is valid.

**Ex. 2: Diffie-Hellman pairs**

Fix $g$ and $h$ integers. The powers of $h$ form a subgroup $H$
inside the group $Z_p$.

-   Public input $u\in G,v\in H$.
-   Agreed relation
    $((u,v),w)\in R \Leftrightarrow g^w=u \wedge h^w=v$.
-   Private input $w$.
-   Bob will need some random $s\in Z_p$.
-   Alice will need some random $c\in Z_p$.

The protocol has three rounds:

$$B\rightarrow A: a=g^s, a'=h^{s}$$

$$A\rightarrow B: c$$

$$B\rightarrow A: r=wc+s$$

Alice validates Bob's response by checking
that $g^r=u^ca$ and that $h^{r}=v^{c}a'$. Indeed, if Bob was honest
it should be that $g^r={g^w}^c g^s=u^ca$ and similarly for $h$ with $v$.

Beware that $B$ must not be left to freely choose the challenge, otherwise
he can generate valid triplets through the following simulator:

-   Pick $c,r$.
-   Let $a=g^{r}u^{-c}$ and $a'=h^{r}v^{-c}$.

Indeed, you then have $g^{r}=u^{c}a$ and $h^{r}=v^{c}a'$ so that the
run is valid.

**Ex. 3: Proof of cyphertext content by encrypter**

Fix $g$ and $h=g^x=\textrm{Pub}^T$ integers.

-   Public input $m$ and $n=(u,v)$.
-   Agreed relation
    $((m,n),w)\in R \Leftrightarrow u=g^w \wedge v={g^x}^w m  \Leftrightarrow n=\{m\}_{\textrm{Pub}^T}$
    under [ElGamal](/SXP/wiki/ElGamal "wikilink") with ephemeral key
    $w$.
-   Private input $w$.
-   Bob will need some random $s\in Z_p$.
-   Alice will need some random $c\in Z_p$.

The protocol has three rounds:

$$B\rightarrow A: a=g^s, a'={g^x}^{s}$$

$$A\rightarrow B: c$$

$$B\rightarrow A: r=wc+s$$

Alice validates Bob's response by checking
that $g^r=u^ca$ and that ${g^x}^{r}=(v/m)^{c}a'$. Indeed, if Bob was
honest it should be that $g^r={g^w}^c g^s=u^ca.$ and similarly for $h=g^x$ with $v'=v/m$.

**Denote by $(a,c)\mapsto\textrm{CCE}^T(m,n)(a,c)$ the response of
this protocol, done towards T with mask a and under challenge c.**

Beware that $B$ must not be left to freely choose the challenge, otherwise
he can generate valid triplets through the following simulator:

-   Pick $c,r$.
-   Let $a=g^{r}u^{-c}$ and $a'={g^x}^{r}{(v/m)}^{-c}$.

Indeed, you then have $g^{r}=u^{c}a$ and ${g^x}^{r}={(v/m)}^{c}a'$
so that the run is valid.

**Ex. 4: Proof of cyphertext content by decrypter**

Fix $g$ and $h=g^x=\textrm{Pub}^T$ integers.

-   Public input $m$ and $n=(u,v)$.
-   Agreed relation
    $((m,n),w)\in R \Leftrightarrow v=u^x m  \Leftrightarrow n=\{m\}_{\textrm{Pub}^T}$
    under [ElGamal](/SXP/wiki/ElGamal "wikilink") for Trent.
-   Private input $x=\textrm{Priv}_T$.
-   Trent will need some random $s\in Z_p$.
-   Alice will need some random $c\in Z_p$.

The protocol has three rounds:

$$T\rightarrow A: a=g^s, a'=u^{s}$$

$$A\rightarrow T: c$$

$$T\rightarrow A: r=xc+s$$

Alice validates Trent's response by checking
that $g^r=h^c a$ and that $u^r=(v/m)^{c} a'$. Indeed, if Trent was
honest it should be that

$$g^r={g^x}^c g^s=h^c a.$$

and

$$u^r=u^{xc} u^s=(v/m)^c a'.$$

**Denote by $(a,c)\mapsto\textrm{CCD}^T(m,n)(a,c)$ the response of
this protocol, done towards T with mask a and under challenge c.**

Beware that $T$ must not be left to freely choose the challenge, otherwise
he can generate valid triplets through the following simulator:

-   Pick $c,r$.
-   Let $a=g^{r}u^{-c}$ and $a'={u}^{r}{(v/m)}^{-c}$.

Indeed, you then have $g^{r}=u^{c}a$ and ${u}^{r}={(v/m)}^{c}a'$ so
that the run is valid.

Composability
-------------

Consider $v_0, v_1$ and $R_0, R_1$. Say Bob pretends to have
$w_0, w_1$ such that $(v_0,w_0)\in R_0 \wedge (v_1,w_1)\in R_1$, and
does not want to disclose them. Is there a Sigma protocol for this new
relation
$R_0\wedge R_1=\{(v_0,v_1),(w_0,w_1)\,|\,(v_0,w_0)\in R_0 \wedge (v_1,w_1)\in R_1\}$?
If there was some for $R_0$ and $R_1$, then yes. It suffices to
combine the parallel run of both protocols into one, as tuples. We
cannot use twice the same mask a for security reasons, so although could
use twice the same challenge c for both runs, we will assume that both a
and c are in fact pairs.

**Denote by $(a,c)\mapsto(P \wedge Q)(a,c)$ the response to the and
composition of P and Q, under the pair of masks a and the pair of
challenges c.**

Now, say Bob pretends to have one of $w_0$ or $w_1$, and does not
want to disclose it, not tell which one it is. Is there a Sigma protocol
for this new relation
$R_0\vee R_1=\{(v_0,v_1),(w_0,w_1)\,|\,(v_0,w_0)\in R_0 \vee (v_1,w_1)\in R_1\}$?
If there was some for $R_0$ and $R_1$, then sometimes yes. This
sometimes is related to Bob's ability to simulate, on its own, a valid
run of the component Sigma protocols. In other words, for instance say
that Bob does not know $w_1$, but that he has the freedom to choose
himself the corresponding challenge $c_1$. Is he able to efficiently
generate $a_1,c_1,r_1$ so that they are valid? If so, then yes. This
particular property of the component Sigma protocols is referred to as
*existence of a simulator* or *special honest-verifier zero-knowledge*.
Here is how.

-   Public input $v_0,v_1$.
-   Agreed relation $R_0\vee R_1$.
-   Private input $w_0$, say, but could equally be $w_1$.
-   Bob will need some random $(u_0,a_0)\in R_0$ and some run
    $a_1,c_1,r_1$.
-   Alice will need some random $c$.

The protocol has three rounds:

$$B\rightarrow A: (a_0,a_1)$$

$$A\rightarrow B: s$$

$$B\rightarrow A: (c_0,c_1),(r_0,r_1)$$

where $r_0$ is computed by Bob
thanks to his knowledge of $w_0$. Alice validates Bob response by
checking that:

-   $c=c_0\oplus c_1$
-   $a_0,c_0,r_0$ is valid
-   $a_1,c_1,r_1$ is valid

The intuition is that Bob has "divided up" the random challenge $s$
into $c_0$ which is random, and $c_1$, which is chosen. He has
passed the challenge $c_0$ thanks to his $w_0$. He has passed the
challenge $c_1$ because it was an easy challenge that he has himself
chosen.

**Denote by $(a,c)\mapsto(P \vee Q)(a,c)$ the response to the or
composition of two protocols P and Q, under the divided up mask a and
challenge c.**

Non-interactive version
-----------------------

Instead of doing the Sigma protocol in three rounds, we could just do it
in one round, by using the Fiat-Shamir heuristics. The idea is that Bob
challenges himself with something that he does not really control,
namely $H(a,m)$, where $H$ is a hash function like SHA2 and $m$ is
a public input, often just $v$. For instance, let us apply this
procedure to the Schnorr identification protocol. We get:

-   Public input $m=v\in G$.
-   Agreed relation $(v,w)\in R \Leftrightarrow g^w=v$.
-   Private input $w$.
-   Bob will need some random $s\in Z_p$.

The modified protocol has only one true round, since B' is just B
challenging himself:

$$B\rightarrow B': a=g^s$$

$$B'\rightarrow B: c=H(a,m)$$

$$B\rightarrow A: a,c,r=wc+s$$

Alice validates Bob response by checking
that $c=H(a,m)$ and that $v^{-c}g^r=a$. Indeed, if Bob was honest it
should be that

$$v^{-c}g^r=g^{-wc} g^{wc} g^s=a$$.

**Denote by $\textrm{NI}(P)(m)=(g^s,H(g^s,m),P(g^s,H(g^s,m)))$ the
non-interactive version of P.**

Notice that is some Sigma protocols, such as those obtained by
and-composition, we have $a=(a_1=g_1^{s_1},a_2=g_2^{s_2},\ldots)$ and
$c=(c_1,c_2,\ldots)$, in which case we use
$c=(c_1=H(a_i,m), c_2=H(a_2,m), \ldots)$ for their non-interactive
versions.

Schnorr signatures
------------------

Let us further modify the above, and say that the challenge that Bob
will put to himself is to be fabricated based upon some public $m$
instead of $v$.

-   Public input $m$ a message and $v=\textrm{Pub}^B$.
-   Agreed relation $(v,w)\in R \Leftrightarrow g^w=v$.
-   Private input $w=\textrm{Priv}_B$.
-   Bob will need some random ephemeral $s\in Z_p$.
  
Bob computes $c=H(g^s,m)$ and $r=s+wc$.

Bob signs $m\in G$ as $(c,r)$.

Alice verifies $(c,r)$ checking that $c=H(a, m)$ with
$a=v^{-c}g^r$.

Indeed, if Bob was honest it should be that
$v^{-c}g^r =g^{-wc}g^{wc+s} =g^s=a$.

Thus, a Schnorr signature of the message m is essence just the
non-interactive version of the Schnorr identification protocol, i.e.

$$\textrm{NI}(\textrm{Schnorr}_B)(m)$$

with s random. In practice the
first element of the triple is dropped.

