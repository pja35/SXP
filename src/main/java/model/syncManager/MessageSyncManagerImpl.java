package model.syncManager;

import model.api.MessageSyncManager;
import model.entity.Message;
import model.factory.ValidatorFactory;
import model.validator.EntityValidator;

public class MessageSyncManagerImpl extends AbstractSyncManager<Message> implements MessageSyncManager {

	public MessageSyncManagerImpl() {
		super();
		this.initialisation("persistence", Message.class);
	}

	@Override
	protected EntityValidator<?> getAdaptedValidator() {
		return ValidatorFactory.createMessageValidator();
	}
}

