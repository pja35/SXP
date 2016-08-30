package model.validator;

import crypt.api.signatures.Signer;
import crypt.impl.key.ElGamalAsymKey;
import crypt.impl.signatures.ElGamalSignature;
import model.entity.Item;

public class ItemValidator extends EntityValidator<Item>{
	
	private Signer<ElGamalSignature, ElGamalAsymKey> signer = null;
	
	public void setSigner(Signer<ElGamalSignature, ElGamalAsymKey> signer) {
		this.signer = signer;
	}
	
	@Override
	public boolean validate() {
		return super.validate() && validateSignature();
	}
	
	private boolean validateSignature() {
		if(signer == null) throw new RuntimeException("no signer were setteld");
		
		return false;
	}
}
