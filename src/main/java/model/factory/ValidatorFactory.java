package model.factory;

import model.validator.ItemValidator;
import model.validator.MessageValidator;
import model.validator.UserValidator;

public class ValidatorFactory {
	
	public static ItemValidator createItemValidator() {return new ItemValidator();}
	public static UserValidator createUserValidator() {return new UserValidator();}
	public static MessageValidator createMessageValidator() {return new MessageValidator();}
}
