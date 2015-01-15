package aok.coc.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class SingleLineFormatter extends Formatter {

	Date						dat				= new Date();
	private final static String	format			= "{0,date} {0,time}";
	private MessageFormat		formatter;
	private final Object		args[]			= new Object[1];

	// Line separator string.  This is the value of the line.separator
	// property at the moment that the SimpleFormatter was created.
	//private String lineSeparator = (String) java.security.AccessController.doPrivileged(
	//        new sun.security.action.GetPropertyAction("line.separator"));
	private final String		lineSeparator	= "\n";

	/**
	 * Format the given LogRecord.
	 * @param record the log record to be formatted.
	 * @return a formatted log record
	 */
	@Override
	public synchronized String format(LogRecord record) {

		StringBuilder sb = new StringBuilder();
		// Minimize memory allocations here.
		dat.setTime(record.getMillis());
		args[0] = dat;

		// Date and time 
		StringBuffer text = new StringBuffer();
		if (formatter == null) {
			formatter = new MessageFormat(format);
		}
		formatter.format(args, text, null);
		sb.append(text);
		sb.append(" ");

		// Class name 
		if (record.getSourceClassName() != null) {
			sb.append(record.getSourceClassName());
		} else {
			sb.append(record.getLoggerName());
		}

		// Method name 
		if (record.getSourceMethodName() != null) {
			sb.append(" ");
			sb.append(record.getSourceMethodName());
		}
		sb.append(" - "); // lineSeparator

		String message = formatMessage(record);

		// Level
		sb.append(record.getLevel().getLocalizedName());
		sb.append(": ");

		// Indent - the more serious, the more indented.
		//sb.append( String.format("% ""s") );
		int iOffset = (1000 - record.getLevel().intValue()) / 100;
		for (int i = 0; i < iOffset; i++) {
			sb.append(" ");
		}

		sb.append(message);
		sb.append(lineSeparator);
		if (record.getThrown() != null) {
			try (
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);) {
				record.getThrown().printStackTrace(pw);
				pw.close();
				sb.append(sw.toString());
			} catch (Exception ex) {
			}
		}
		return sb.toString();
	}
}