package controller.managers;

import crypt.api.annotation.ParserAction;
import crypt.api.annotation.ParserAnnotation;
import crypt.factories.ParserFactory;
import model.api.Manager;
import model.api.ManagerDecorator;
import model.api.ManagerListener;
import model.entity.Message;
import model.entity.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Radoua Abderrahim
 */
public class CryptoMessageManagerDecorator extends ManagerDecorator<Message> {

    private User user, userSender;
    private String who;

    public CryptoMessageManagerDecorator(Manager<Message> em, String who, User reciever, User sender) {
        super(em);
        this.user = reciever;
        this.userSender = sender;
        this.who = who;
    }

    @Override
    public boolean persist(Message entity) {

        ParserAnnotation parser = ParserFactory.createDefaultParser(entity, userSender.getKey());

        entity = (Message) parser.parseAnnotation(ParserAction.SigneAction);

        parser.setKey(user.getKey());

        entity = (Message) parser.parseAnnotation(ParserAction.CryptAction);

        return super.persist(entity);
    }

    @Override
    public void findAllByAttribute(String attribute, String value, final ManagerListener<Message> l) {

        super.findAllByAttribute(attribute, value, new ManagerListener<Message>() {
            @Override
            public void notify(Collection<Message> results) {

                ArrayList<Message> res = new ArrayList<>();

                for (Iterator iterator = results.iterator(); iterator.hasNext(); ) {

                    Message message = (Message) iterator.next();

                    ParserAnnotation<Message> parser;

                    parser = ParserFactory.createDefaultParser(message, user.getKey());

                    message = (Message) parser.parseAnnotation(ParserAction.DecryptAction);

                    parser.setKey(null);

                    message = (Message) parser.parseAnnotation(ParserAction.CheckAction);


                    if (message != null) {
                        res.add(message);
                    }
                }

                l.notify(res);
            }
        });
    }

    @Override
    public boolean end() {
        return super.end();
    }
}
