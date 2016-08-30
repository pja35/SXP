package model.entity;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import crypt.api.key.AsymKey;

public interface ContractEntity<Sign> {
	public List<? extends AsymKey<BigInteger>> getParties();
	public Map<? extends AsymKey<BigInteger>, Sign> getSignatures();
}
