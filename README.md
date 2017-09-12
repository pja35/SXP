# Secure Exchange Protocols (SXP)

Below is a description of the idea behind the SXP project. For technical details the reader should refer to the wiki on github.  
* SXP protocol wiki: https://pja35.github.io/SXP/wiki/.  
* SXP code wiki https://github.com/pja35/SXP/wiki/.

## Philosophy

Centralized and free services have counterparts: *Google*, *Facebook*, *Amazon*... are watching you. They scan your browsing to exploit your datas. "If it is free, then you are the product" is now a famous adage related to such services. Our technological era allows us to easily connect to each other. Why not build our self-managed services in peer-to-peer? [1]

[1] https://www.gnu.org/philosophy/who-does-that-server-really-serve.en.html

This is a project to create an open, ubiquitous, peer-to-peer, secure protocol suite for multi-party exchanges of contracts.

The idea is that any of us can join together and create a virtual market, in which others can tell what they have to offer and what they need. The market, or anyone for that matter, can propose deals, whereby one person would give an item to another who gives an item to a third etc. If all parties concerned agree to a deal, the deal gets secured into an electronic contract, signed by the different parties.

More precisely, say Alice, Bob, and Charlie have access to different resources a, b, and c. Say Alice wants b, Bob wants c and Charlie wants a, but they do not know each other, nor do they trust each other. Here a, b and c can be anything, e.g a the ability to give maths lessons, b some barrel of beer, and c coins from a certain currency. Finally, say Alice, Bob and Charlie have an iPhone, Android, or whatever smart phone in their pockets. Using Secure eXchange Protocols on their smart phones, they should be able to quickly declare their resources and needs on virtual ad hoc markets, which in turn should detect and propose the obvious deal between them. If the three of them accept the deal, then an electronic contract will be secured, which is a formal proof of their agreement to perform the swap.

## In brief

Most of websites bringing users together rely on common principles:
* account creation on a server,
* publication and search of announcements,
* connect users,
and differ only according to the market they hold: *Airbnb* (housing), *Drivy*  (cars), etc. Despite this common basis, each company develops its own software solution. The SXP project aims at offering this shared core features, and imposing it via open source diffusion, on the one hand. On the other hand, SXP decides to implement these services on **peer-to-peer**, i.e. it gets rid of the  centralized server maintaining accounts and announcements. This, in a:
* secure ("if I use an official version of the SXP client, then my datas are safe and my identity preserved, even if other users are malicious modify their SXP client"),
* resilient ("if I shutdown or even lost my computer or smartphone, my accounts continue to exists on the network, and I continue to receive messages, that I will be able to read from a new machine"),
* transparent ("As the source code is open and executed by me, I know exactly what the system does, and I can contribute to its enhancement").
These choices reassure users and allow economic actors to open new markets via a simple adaptation of the SXP client, without infrastructure cost.

## Contract signing

When two users, say Alice and Bob, agree on a contract C, they have to sign this contract: Alice wants C signed by B and Bob wants C signed by Alice. But who is about to sign first? In real life they meet and sign simultaneously. On a network this is not possible. Yet, if Alice commits first she runs the risk of never receiving Bob\'92s counterpart, whereas he can benefit from what she gave in.

The traditional solution relies on a trusted third party, Trent, acting as an intermediate to which Alice and Bob each send the contract with their signature. In SXP, this solution consists in offering centralized services in order to secure an exchange -- this is a possible economic model for the project.

Nevertheless, there exist "optimistic" contract signing protocols, that call Trent only if there is a conflict between Alice and Bob -- i.e. as a referee rather than as an intermediate. SXP implements such a protocol. An additional step would be to replace a single Trent with a set of random peers -- i.e. to replace the referee with a jury. It seems possible though still at a research stage. In technical terms, our idea is basically to merge two great works in order to fit our needs: publicly verifiable secret sharing [1] and optimistic fair exchange protocols [2,3].

[1] Markus Stadler, Publicly Verifiable Secret Sharing.
[2] Schunter Asokan, Review of optimistic fair exchange.
[3] Gildas Avoine and Serge Vaudenay, Optimistic Fair Exchange Based on Publicly Verifiable Secret Sharing.

## Comparison to existing services

* Some free CMS, such as *Wordpress*, ease the creation of online shops. None offers a complete solution to bring users together. None offers a decentralize solution.
* *Bitcoin* is a virtual currency in P2P. It is actually one big shared ledger book, with mechanisms enforcing consensus of users on its content, and in which writing "Alice gives 10B$ to Bob" signed by Alice is equivalent to effectively doing it. This technology has drawbacks: the ledger book is growing huge (13Go in Jan. 2014, 50Go in Dec. 2015), and the enormous consumption of computational power and energy needed for consensus mechanisms. Compared to SXP, *Bitcoin* does not allow diffusion and search of announcements, but offers a payment system that may be seen as complementary.
* *Diaspora* is a social network in P2P, that does not implement any cryptography yet.

## Status

SXP is now at a prototype stage. In particular, it has a rudimentary user interface. But main functionalities of account creation, announcement publication, announcement search, resilience of accounts and datas, confidentiality, are implemented, as well as a first version of optimistic contract signing.

