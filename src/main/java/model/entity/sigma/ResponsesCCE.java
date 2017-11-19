/* Copyright 2015 Pablo Arrighi, Sarah Boukris, Mehdi Chtiwi, 
   Michael Dubuis, Kevin Perrot, Julien Prudhomme.

   This file is part of SXP.

   SXP is free software: you can redistribute it and/or modify it 
   under the terms of the GNU Lesser General Public License as published 
   by the Free Software Foundation, version 3.

   SXP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
   PURPOSE.  See the GNU Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public License along with SXP. 
   If not, see <http://www.gnu.org/licenses/>. */
package model.entity.sigma;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import model.entity.ElGamalKey;

import java.math.BigInteger;


/**
 * The CCE response
 *
 * @author sarah
 */
public class ResponsesCCE extends Responses {

    /**
     * Constructor
     *
     * @param mask
     * @param challenge
     * @param response
     */
    @JsonCreator
    public ResponsesCCE(@JsonProperty("masks") Masks mask,
                        @JsonProperty("challenge") BigInteger challenge,
                        @JsonProperty("response") BigInteger response) {
        super(mask, challenge, response);
    }

    public ResponsesCCE() {
        super();
    }


    @Override
    /**
     * Extends Responses
     * Verify if the CCE response is good or not
     */
    public Boolean Verifies(ElGamalKey tKeys, ResEncrypt res) {

        BigInteger gPowr = tKeys.getG().modPow(getResponse(), tKeys.getP());
        BigInteger uPowc = res.getU().modPow(getChallenge(), tKeys.getP());
        BigInteger uPowcMulta = uPowc.multiply(getMasks().getA()).mod(tKeys.getP());

        if (!gPowr.equals(uPowcMulta)) {
            System.out.println("CCE verification failed on test 1");
            return false;
        }

        BigInteger M = new BigInteger(res.getM());
        BigInteger pubPowr = tKeys.getPublicKey().modPow(getResponse(), tKeys.getP());

        BigInteger vdivM = res.getV().divide(M);
        BigInteger vDivMPowc = vdivM.modPow(getChallenge(), tKeys.getP());
        BigInteger vDivMPowcMultaBis = (vDivMPowc.multiply((getMasks().getaBis()))).mod(tKeys.getP());

        if (!pubPowr.equals(vDivMPowcMultaBis)) {
            System.out.println("CCE verification failed on test 2");
            return false;
        }

        return true;
    }

    /**
     * Override equals to be able to compare two responses
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ResponsesCCE)) {
            return false;
        }
        return super.equals(o);
    }

    /**
     * Override hashCode to be able to compare two responses
     */
    @Override
    public int hashCode() {
        int hashS = super.hashCode();
        return hashS + 3;
    }

}
