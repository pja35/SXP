package protocol.impl.sigma;

import com.fasterxml.jackson.core.type.TypeReference;
import controller.Users;
import controller.tools.JsonTools;
import crypt.api.signatures.Signable;
import crypt.impl.signatures.SigmaSigner;
import model.api.EstablisherType;
import model.api.Status;
import model.api.Wish;
import model.entity.ContractEntity;
import model.entity.ElGamalKey;
import model.entity.User;
import model.entity.sigma.SigmaSignature;
import org.bouncycastle.util.Arrays;
import protocol.api.EstablisherContract;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * This class aims to create an adapter between ContractEntity and EstablisherContract. We want everything
 * to be in the ContractEntity and an establisher will use an EstablisherContract. That's why we use this
 * adapter in the establishers
 *
 * @author Nathanaël Eon
 */
public class SigmaContract extends EstablisherContract<BigInteger, ElGamalKey, SigmaSignature, SigmaSigner> {

    // List of parties keys
    protected ArrayList<ElGamalKey> parties = new ArrayList<>();
    // Maps the keys with the id of a user
    protected HashMap<ElGamalKey, String> partiesId = new HashMap<ElGamalKey, String>();
    // Maps the keys with the signatures
    protected HashMap<ElGamalKey, SigmaSignature> signatures = new HashMap<ElGamalKey, SigmaSignature>();
    // Clauses in the format we need them
    protected Signable<SigmaSignature> termination = null;

    protected Signable<SigmaSignature> clauses = null;

    protected Signable<SigmaSignature> implementing = null;

    protected Signable<SigmaSignature> exchange = null;


    // Signer object
    protected SigmaSigner signer;

    // Basic constructor
    public SigmaContract() {
        super();
        this.signer = new SigmaSigner();
        this.contract = new ContractEntity();
        contract.setClauses(new ArrayList<String>());
        contract.setParties(new ArrayList<String>());
        contract.setSignatures(new HashMap<String, String>());
        contract.setEstablisherType(EstablisherType.Sigma);
    }

    // Constructor from clauses (problem when resolve, because no partiesId set)
//    public SigmaContract(Signable<SigmaSignature> clauses) {
//        super();
//        this.signer = new SigmaSigner();
//        this.contract = new ContractEntity();
//        this.setClauses(clauses);
//        this.contract.setParties(new ArrayList<String>());
//        this.contract.setSignatures(new HashMap<String, String>());
//        this.contract.setEstablisherType(EstablisherType.Sigma);
//    }

    // Constructor from a ContractEntity (what will be most used)
    public SigmaContract(ContractEntity c) {
        super();
        this.contract = c;
        this.signer = new SigmaSigner();
        this.setTermination(contract.getTermination());
        this.setImplementing(contract.getImplementing());
        this.setExchange(contract.getExchange());
        this.setParties(contract.getParties());
        this.contract.setEstablisherType(EstablisherType.Sigma);
    }

    /************* GETTERS ***********/
    public Signable<SigmaSignature> getTermination() {
        return termination;
    }

    public void setTermination(Signable<SigmaSignature> c) {
        this.termination = c;
        ArrayList<String> a = new ArrayList<String>();
        a.add(new String(c.getHashableData()));
        this.contract.setTermination(a);
    }

    public Signable<SigmaSignature> getImplementing() {
        return implementing;
    }

    public void setImplementing(Signable<SigmaSignature> c) {
        this.implementing = c;
        ArrayList<String> a = new ArrayList<String>();
        a.add(new String(c.getHashableData()));
        this.contract.setImplementing(a);
    }

    public Signable<SigmaSignature> getExchange() {
        return exchange;
    }

    public void setExchange(Signable<SigmaSignature> c) {
        this.exchange = c;
        ArrayList<String> a = new ArrayList<String>();
        a.add(new String(c.getHashableData()));
        this.contract.setExchange(a);
    }

    public ArrayList<ElGamalKey> getParties() {
        return parties;
    }

    /**
     * Find the parties keys
     *
     * @param s : List of user ids
     */
    public void setParties(ArrayList<String> s) {
        for (String u : s) {
            JsonTools<User> json = new JsonTools<>(new TypeReference<User>() {
            });
            Users users = new Users();
            User user = json.toEntity(users.get(u));
            this.parties.add(user.getKey());
            this.partiesId.put(user.getKey(), user.getId());
        }
        this.contract.setParties(s);

        // Order parties by publicKey (useful to get hashable data
        this.parties.sort(new Comparator<ElGamalKey>() {
            @Override
            public int compare(ElGamalKey k1, ElGamalKey k2) {
                return k1.getPublicKey().compareTo(k2.getPublicKey());
            }
        });
    }

    public ElGamalKey getTrentKey() {
        return signer.getTrentK();
    }

    /**
     * Set Trent key and store it into Establishement data
     */
    public void setTrentKey(ElGamalKey k) {
        signer.setTrentK(k);
    }

    /************* SETTERS ***********/
    public void setClauses(ArrayList<String> c) {
        this.clauses = new SigmaClauses(c);
        this.contract.setClauses(c);
    }

    public void setTermination(ArrayList<String> c) {
        this.termination = new SigmaClauses(c);
        this.contract.setTermination(c);
    }
    public void setImplementing(ArrayList<String> c) {
        this.implementing = new SigmaClauses(c);
        this.contract.setImplementing(c);
    }
    public void setExchange(ArrayList<String> c) {
        this.exchange = new SigmaClauses(c);
        this.contract.setExchange(c);
    }

    /**
     * Set the parties from a list of ElGamalKey
     * WARNING : this won't set the partiesId
     */
    public void setParties(ArrayList<ElGamalKey> p, boolean isSigmaParty) {
        this.parties = p;
    }

    /************* STATUS / WISH ***********/
    @Override
    public Status getStatus() {
        return contract.getStatus();
    }

    @Override
    public void setStatus(Status s) {
        contract.setStatus(s);
    }

    @Override
    public Wish getWish() {
        return contract.getWish();
    }

    @Override
    public void setWish(Wish w) {
        contract.setWish(w);
    }

    /************* Abstract method implementation **********/

    @Override
    public boolean isFinalized() {
        boolean result = false;

        if (this.getTrentKey() == null) {
            return false;
        }


        for (ElGamalKey k : parties) {
            signer.setReceiverK(k);
            if (signatures.get(k) == null) {
                return false;
            }

            byte[] data = (new String(this.getHashableData())).getBytes();
            if (signer.verify(data, signatures.get(k)))
                return true;

            for (int round = 1; round < parties.size() + 2; round++) {
                data = (new String(this.getHashableData()) + round).getBytes();
                if (signer.verify(data, signatures.get(k))) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public void addSignature(ElGamalKey k, SigmaSignature s) {
        if (k == null || !this.parties.contains(k)) {
            throw new RuntimeException("invalid key");
        }
        signatures.put(k, s);
        contract.getSignatures().put(this.partiesId.get(k), s.toString());

        if (this.isFinalized())
            this.setStatus(Status.FINALIZED);
    }

    @Override
    public boolean checkContrat(EstablisherContract<BigInteger, ElGamalKey, SigmaSignature, SigmaSigner> contract) {
        return this.equals(contract) && this.isFinalized();
    }

    @Override
    public boolean equals(EstablisherContract<BigInteger, ElGamalKey, SigmaSignature, SigmaSigner> c) {
        if (!(c instanceof SigmaContract))
            return false;
        SigmaContract contract = (SigmaContract) c;
        if (contract.clauses == null)
            return false;
        return Arrays.areEqual(this.getHashableData(), contract.getHashableData());
    }


    @Override
    public byte[] getHashableData() {
        BigInteger sum = BigInteger.ZERO;
        for (ElGamalKey k : parties) {
            sum = sum.add(k.getPublicKey());
        }

        StringBuffer buffer = new StringBuffer();
        buffer.append(sum.toString());
        byte[] implementing = this.implementing.getHashableData();
        byte[] exchange = this.exchange.getHashableData();
        byte[] termination = this.termination.getHashableData();


        int bufferL = buffer.toString().getBytes().length;
        byte[] concate = new byte[implementing.length+exchange.length+termination.length + bufferL];
        System.arraycopy(new String(buffer).getBytes(), 0, concate, 0, bufferL);
        System.arraycopy(implementing, 0, concate, bufferL, implementing.length);
        System.arraycopy(exchange, 0, concate, implementing.length+bufferL, exchange.length);
        System.arraycopy(termination, 0, concate, implementing.length+exchange.length+bufferL, termination.length);

        return concate;
    }

    @Override
    public SigmaSignature sign(SigmaSigner signer, ElGamalKey k) {
        signer.setKey(k);
        return signer.sign(this.getHashableData());
    }
}
