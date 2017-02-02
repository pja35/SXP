# SXP

## Bugs

| **Title** | **Description** |
| :-------: | :-------------: |
| Bad userid `XmlElement` name in `model.entity.Item` | Line 61 : the `XmlElement.name()` is "username" but should probably be "userid". |
| Constant server URL | The server address is hard-coded to localhost:8081 in src/main/js/modules/app.js. The very first line. |
| Duplicated Code | There are two classes with the same name that behave the same way : `src/main/java/model/entity/ElGamaAsymKey.java` `src/main/java/crypt/impl/key/ElGamaAsymKey.java`. There is also a third class that ressemble the two : `src/main/java/model/entity/ElGamaKey.java` |
| Duplicated code | Class Hasher defined in `src/main/java/protocol/impl/sigma/Hasher.java` is designed to hash different data types using SHA256 algorithm. It is redundant with class `SHA256Hasher` defined in `src/main/java/crypt/impl/hashs/SHA256Hasher.java` |
| `ElGamalEngineK` is never used | `crypt.utils.ElGamalEngineK` is never used. Should be removed? |
| Error in code logic | In file `src/main/java/protocol/impl/sigma/Utils.java`. Line 39 replace `while(s.compareTo(BigInteger.ONE)<=0 && s.compareTo(p)>= 0)` with `while(s.compareTo(BigInteger.ONE) < 0 || s.compareTo(p) > 0)` |
| Adding unknown service to jxtaPeer does not raise an exception | See `network.impl.jxta.JxtaServiceTest::addServiceToPeer()`. |
| `JstaNode::start()` System.exit should be avoided | Line 60-61 and 64-65 : replace by `throw e;` |
| `JxtaAdvertisement` : replace all System.out.println by a log message | |
| ` protocol.impl.sigma.Utils.rand()` : illegalArgumentException may occur and is not managed | if bitLength < 0 the exception is raised by BigInteger constructor but is not managed. At least a rethrow should be done. |
| `protocol.impl.sigma.Utils.rand()` : infinite loop | Using the rand function with the BigInteger argument equals to zero (new BigInteger("00000") for example) leads to an infinite loop. |
| Unable to convert output of http request api/users/{id} into json object | See `controller.ControllerTest.testCa()` and `testCb()` error description : see [^1] |
| In `controller.Search` algorithm simple2 does not work. | It always return an empty collection. See controller.ControllerTest.testJ |


[^1]: com.fasterxml.jackson.databind.exc.InvalidFormatException: Can not construct instance of java.math.BigInteger from String value 'a9c088ee44742b00bbfefba36b37360842322162827000507b5039141652d5f9bf5d6856cdfd688789f8ff1e7e29f360f249bfd1503c27497dbd8e42ba8d98e2c3d8250e8315f0c52441057e61f3be1af9ddafe31f292a146007c973761e50bede3125ef3dff67095c45019e56694428fab3489918f8781c720d1eec5c6278b8': not a valid representation
 at [Source: {"id":"05C299FE-11CC-4936-B197-D5157D2E29EA","nick":"RABkDIJVEcSVCtmjVErE","salt":"Azy8L/gF6J3JtehezK6iNXw=","passwordHash":"vO5jUAnnMSl93nkKHqaqgHwanEqym+sPf4P5CpoUr/I=","createdAt":1482487774046,"key":{"privateKey":"a9c088ee44742b00bbfefba36b37360842322162827000507b5039141652d5f9bf5d6856cdfd688789f8ff1e7e29f360f249bfd1503c27497dbd8e42ba8d98e2c3d8250e8315f0c52441057e61f3be1af9ddafe31f292a146007c973761e50bede3125ef3dff67095c45019e56694428fab3489918f8781c720d1eec5c6278b8","publicKey":"2f8e0b70c8798eaa3a9b9a40fe872605f63333c68cfdc57d48fbb96ae1ab382c74307c0b8e269c7dc9d59a826ed30f16303ee574ef237ca8bcc783f6d592bb2727c998f943b63e32be0e551e8aef50afcc7be4044cb625fbbcc962e91606984a2b6902f43076cf48005ac3fef8c55c1602b9f68e4898af5b7d03f01a1191676e","p":"b0ea05cb9e2fdb5c46fde7970a2b297dd91d4e56f136cde3d40a8fc3d672883daa07adaee3f6a2f6b669dad6aba95ee9f247d47c6d4d321c2785364f0792cd1fff10d0566f29e8cba7dfa92971c73f3881e45b500ce1a9121e9c991038a4d462d74791ea76f984076355906f5dec2409a31ec995affe25aff2458067572a49b7","g":"526c8072d4e606f1c8bf9f016f972ada069c114a063a232b4326f59ca589a21632f715bf18c90591cfe73beef9ef060b6e63645a1bc8265116a0be4de30d3cf77866352d06ccefa6aa6b5cde3c9f3692e4161dae57f39db764e2d04e6da83716c615d238cd63312bcf72a228e2ad6ab0502080d97f5e927d7a7cdba3dfa6e46b"}}; line: 1, column: 205] (through reference chain: model.entity.User["key"]->model.entity.ElGamalKey["privateKey"])


## Notes d'architecture

| **Classe** | **Etat Actuel** | **Proposition** |
| :--------: | :-------------: | :-------------: |
| `crypt.ElGamalEngine` | Copy de la classe ElGamalEngine de bouncycastle avec ajout de k | Un héritage avec surcharge de la méthode processBlock aurait suffit |
| `crypt.base.AbstractHasher` | Constructeur avec salt en paramètre qui n'est jamais utilisé par la factory. | Supprimer ce constructeur |
| `crypt.base.BaseSignature` | La méthode getParam doit retourner la valeur d'un champ s'il est annoté avec @ParamName. Or l'annotation n'a pas de retention définie au Runtime donc en l'état cette méthode ne sert à rien. | Supprimer la méthode qui ne sert pas. |
| `model.entity.ElGamalAsymKey` et `crypt.impl.key.ElGamalAsymKey` | Deux classes portent le même nom. Celle de model.entity semble ne servir à rien | Changer le nom d'une des deux classes ou bien les fusionner ou encore supprimer celle qui ne sert pas |
| `model.validator.ItemValidator` | La validation de la signature retourne toujours false | Il faut écrire l'algorithme de validation des signatures.  |
| `rest.impl.SimpleAuthentifier` | Il est possible d'ajouter plusieurs fois le même (username/password) dans tokens. | Vérifier l'existence avant l'ajout dans `tokens`. |
| `rest.impl.SimpleAuthentifier` | Pour un même (username/password) le token calculé sera différent. | Vérifier que ce comportement est celui qui est attendu. |
| Partout | Les captures d'exceptions ne sont pas gérées (seulement un `e.printStackTrace()`) | A minima loguer proprement les exceptions |
| `network.impl.jxta.AdvertisementInstaciator` |  Instaciator should be renamed to Instantiator for a better understanding | Class rename. |
| `network.impl.advertisement.UserAdvertisement` | Classe non utilisée | Retirer la classe |
| `network.impl.advertisement.PeerAdvertisement` | Classe non utilisée | Retirer la classe |
| `protocol.impl.sigma.Utils.rand()` | Générations aléatoires tant que les conditions ne sont pas satisfaites. | Cette méthode peut être longue. Il convient plutôt de retirer à `p` une valeur aléatoire. |
| `protocol.impl.sigma.Sender.SendMasksXXX` | The SendMasksSchnorr method is public but the SendMasksCCE method is private. What is the logic? | All methods that have the same usage must have the same visibility. |
| `protocol.impl.sigma.Sender` et `Receiver` | Documentation des API publiques insuffisante. Les paramètres ne sont pas explicités. | Documenter |
| `protocol.impl.sigmpa.Fabric` | Les méthodes d'instantiation des fabrique devraient être statiques. | Rendre les méthodes statiques. |
| `protocol.impl.sigma.Or` | La visibilité des attributs est publique sans raison. | Par défaut les attributs doivent être privé. |
| `protocol.impl.sigma.TestOr` | "problème dans les challenges", ce message apparaît lors des tests. | Expliciter le fonctionnement des challenges dans la doc et reprendre le test. |
| `protocol.impl.sigma.TestOr` | Vérification OK avec un encryptage aléatoire. Est-ce le comportement attendu? | voir ` protocol.impl.sigma.TestOr.badVerifyTest()`. |
| `protocol.impl.sigma.Hexa.java` | Cette classe devrait être dans un sous-paquet `utils`. | Déplacer au moins dans un sous-paquet  `protocol.utils.Hexa.java`. Préférablement, il conviendrait de créer un répertoire `utils` à la racine du projet. |
| `protocol.impl.sigma.ElGamal*` |  On trouve des paquets dont le nom commence par `ElGamal` à la fois dans protocol et dans `crypt`. |  Il faudrait rationaliser et expliciter la gestion de ces paquets. |
| `protocol.impl.sigma.ElGamal` |  Pourquoi l'attribut `random` est-il public? | Rendre visible un attribut doit être motivé par un commentaire.  |
| `protocol.impl.sigma.ElGamal` | L'exception de la méthode `getMessageSignature()` n'est pas envoyée, le cas testé peut donc être vrai et faire échouer la méthode. | Générer et envoyer une exception avec un nom explicite dans le cas testé, c'est-à-dire quand la clef privée est null. |
| `protocol.impl.sigma.ElGamal` | Même remarque pour les exceptions de la méthode `verifySignature()`. Elles ne sont pas envoyées et n'évitent donc pas les cas testés. | Générer et envoyer des exceptions avec des noms explicites dans les cas testés. |
| `protocol.impl.sigma.And` | Tous les attributs sont visibles sans raison. | Restreindre la visibilité des attributs. |
| `controller.Application` et `network.factories.PeerFactory` | Il n'y a aucun moyen d’accéder au nom du cache jxta. C'est utile, par exemple, que l'application possède une méthode qui supprime le cache et la base. | Ajout d'un attribut cache dans les peer avec un accesseur dessus. |
| Serveur Rest | Attention au fait que certains caractères ne sont pas acceptés pour les noms ou login : "{}\"" | Mettre un validateur du côté js |