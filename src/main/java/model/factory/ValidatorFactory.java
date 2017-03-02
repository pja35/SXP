package model.factory;

import model.validator.ContractValidator;
import model.validator.ItemValidator;
import model.validator.UserValidator;

public class ValidatorFactory {
	
	public ItemValidator createItemValidator() {return new ItemValidator();}
	public UserValidator createUserValidator() {return new UserValidator();}
	public ContractValidator createContractValidator() {return new ContractValidator();}
}
