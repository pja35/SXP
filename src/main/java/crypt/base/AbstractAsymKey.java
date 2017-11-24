package crypt.base;

import crypt.api.key.AsymKey;

import java.util.HashMap;

public abstract class AbstractAsymKey<T> implements AsymKey<T> {

    protected T publicKey = null;
    protected T privateKey = null;
    protected HashMap<String, T> params = new HashMap<>();

    @Override
    public T getPublicKey() {
        return publicKey;
    }

    @Override
    public void setPublicKey(T pbk) {
        this.publicKey = pbk;
    }

    @Override
    public T getPrivateKey() {
        return privateKey;
    }

    @Override
    public void setPrivateKey(T pk) {
        this.privateKey = pk;
    }

    @Override
    public T getParam(String p) {
        return params.get(p);
    }

}
