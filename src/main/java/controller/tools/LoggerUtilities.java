package controller.tools;

public class LoggerUtilities {
	
	static public String getStackTrace(Exception e){
		if (e == null)
			return "";
		StringBuffer buff = new StringBuffer();
		buff.append(e.toString() + '\n' + e.getMessage() + '\n');
		for(StackTraceElement el : e.getStackTrace()){
			buff.append(el.toString() + '\n');
		}
		return buff.toString();
	}

}
