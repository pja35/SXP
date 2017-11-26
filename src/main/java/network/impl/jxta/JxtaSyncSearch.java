package network.impl.jxta;

import controller.tools.LoggerUtilities;
import network.api.Search;
import network.api.SearchListener;
import network.api.advertisement.Advertisement;
import network.api.service.Service;

import java.util.ArrayList;
import java.util.Collection;

public class JxtaSyncSearch<T extends Advertisement> implements Search<T>, SearchListener<T> {

    private Service s;
    private ArrayList<T> results = new ArrayList<>();

    @Override
    public void initialize(Service s) {
        this.s = s;
    }

    @Override
    public Collection<T> search(String attribute, String value) {
        s.search(attribute, value, this);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            LoggerUtilities.logStackTrace(e);
        }
        return results;
    }

    @Override
    public void notify(Collection<T> result) {
        results.addAll(result);
    }

}
