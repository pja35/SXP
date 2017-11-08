package controller.managers;

import crypt.api.annotation.ParserAction;
import crypt.api.annotation.ParserAnnotation;
import crypt.factories.ParserFactory;
import model.api.Manager;
import model.api.ManagerDecorator;
import model.entity.User;

import java.util.Collection;

/**
 * @author Radoua Abderrahim
 */
public class CryptoUserManagerDecorator extends ManagerDecorator<User> {

    private User user;

    public CryptoUserManagerDecorator(Manager<User> em, User user) {
        super(em);
        this.user = user;
    }

    @Override
    public boolean persist(User entity) {
        return super.persist(entity);
    }

    @Override
    public boolean end() {

        Collection<User> collection = this.changesInWatchlist();

        for (User u : collection) {

            if (u.getId() == user.getId()) {

                ParserAnnotation parser = ParserFactory.createDefaultParser(u, user.getKey());

                u = (User) parser.parseAnnotation(ParserAction.HasherAction, ParserAction.SigneAction);

            }
        }

        return super.end();
    }

}
