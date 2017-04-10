package crypt.factories;


import crypt.api.annotation.ParserAnnotation;
import crypt.api.hashs.Hasher;
import crypt.utils.CryptoParser;
import model.entity.User;


/**
 * 
 * @author Radoua Abderrahim
 *
 */
public class ParserFactory {
	
	
	
	/**
	 * Create the default implementation of {@link ParserAnnotation}
	 * @return a {@link ParserAnnotation}
	 */
	public static ParserAnnotation createDefaultParser(Object entity,User u){
		return new CryptoParser(entity,u);
	}
	
	
	
	/*
	public static ParserAnnotation createParser(String parser,Object entity){
		
		if(parser.equals("hasher")){
			
			return new HasherParser(entity);
			
		}else if(parser.equals("crypt")){
			
			return null;
			
		}else if(parser.equals("decrypt")){
			
			return null;
			
		}
			
		
		
		return null;
	}*/
}
