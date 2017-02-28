package controller.tools;

import org.apache.log4j.LogManager;

public class LoggerUtilities {
	
	/**
	 * Log the stacktrace if it exists and return it in a string.
	 * @param e input exception
	 * @return String containing the stacktrace
	 */
	static public String logStackTrace(Exception e){
		if (e == null)
			return "";
		StackTraceElement[] stackTrace = e.getStackTrace();
		StringBuffer buff = new StringBuffer();
		buff.append(e.toString() + '\n' + e.getMessage() + '\n');
		for(StackTraceElement el : stackTrace){
			buff.append(el.toString() + '\n');
		}
		String ret = buff.toString();
		LogManager.getLogger(stackTrace[0].getClassName()).error(ret);
		return ret;
	}

}
