package moviescraper.doctord.view;

import java.io.*;
import java.awt.*;
import javax.swing.event.*;
import javax.swing.text.*;



/*
 *  Create a simple console to display text messages.
 *
 *  Messages can be directed here from different sources. Each source can
 *  have its messages displayed in a different color.
 *
 *  Messages can either be appended to the console or inserted as the first
 *  line of the console
 *
 *  You can limit the number of lines to hold in the Document.
 *  
 *  from: http://tips4java.wordpress.com/2008/11/08/message-console/
 */
public class MessageConsole
{
	private JTextComponent textComponent;
	private Document document;
	private boolean isAppend;
	private DocumentListener limitLinesListener;

	public MessageConsole(JTextComponent textComponent)
	{
		this(textComponent, true);
	}

	/*
	 *	Use the text component specified as a simply console to display
	 *  text messages.
	 *
	 *  The messages can either be appended to the end of the console or
	 *  inserted as the first line of the console.
	 */
	public MessageConsole(JTextComponent textComponent, boolean isAppend)
	{
		this.textComponent = textComponent;
		this.document = textComponent.getDocument();
		this.isAppend = isAppend;
		textComponent.setEditable( false );
	}

	/*
	 *  Redirect the output from the standard output to the console
	 *  using the default text color and null PrintStream
	 */
	public void redirectOut()
	{
		redirectOut(null, null);
	}

	/*
	 *  Redirect the output from the standard output to the console
	 *  using the specified color and PrintStream. When a PrintStream
	 *  is specified the message will be added to the Document before
	 *  it is also written to the PrintStream.
	 */
	public void redirectOut(Color textColor, PrintStream printStream)
	{
		ConsoleOutputStream cos = new ConsoleOutputStream(textColor, printStream);
		//TODO: There's a warning here, however if I use a try with resources the message console breaks
		System.setOut( new PrintStream(cos, true) );
	}

	/*
	 *  Redirect the output from the standard error to the console
	 *  using the default text color and null PrintStream
	 */
	public void redirectErr()
	{
		redirectErr(null, null);
	}

	/*
	 *  Redirect the output from the standard error to the console
	 *  using the specified color and PrintStream. When a PrintStream
	 *  is specified the message will be added to the Document before
	 *  it is also written to the PrintStream.
	 */
	public void redirectErr(Color textColor, PrintStream printStream)
	{
		ConsoleOutputStream cos = new ConsoleOutputStream(textColor, printStream);
		//TODO: There's a warning here, however if I use a try with resources the message console breaks
		System.setErr( new PrintStream(cos, true) );
	}

	/*
	 *  To prevent memory from being used up you can control the number of
	 *  lines to display in the console
	 *
	 *  This number can be dynamically changed, but the console will only
	 *  be updated the next time the Document is updated.
	 */
	public void setMessageLines(int lines)
	{
		if (limitLinesListener != null)
			document.removeDocumentListener( limitLinesListener );

		limitLinesListener = new LimitLinesDocumentListener(lines, isAppend);
		document.addDocumentListener( limitLinesListener );
	}

	/*
	 *	Class to intercept output from a PrintStream and add it to a Document.
	 *  The output can optionally be redirected to a different PrintStream.
	 *  The text displayed in the Document can be color coded to indicate
	 *  the output source.
	 */
	class ConsoleOutputStream extends ByteArrayOutputStream
	{
		private final String EOL = System.getProperty("line.separator");
		private SimpleAttributeSet attributes;
		private PrintStream printStream;
		private StringBuffer buffer = new StringBuffer(80);
		private boolean isFirstLine;

		/*
		 *  Specify the option text color and PrintStream
		 */
		public ConsoleOutputStream(Color textColor, PrintStream printStream)
		{
			if (textColor != null)
			{
				attributes = new SimpleAttributeSet();
				StyleConstants.setForeground(attributes, textColor);
			}

			this.printStream = printStream;

			if (isAppend)
				isFirstLine = true;
		}

		/*
		 *  Override this method to intercept the output text. Each line of text
		 *  output will actually involve invoking this method twice:
		 *
		 *  a) for the actual text message
		 *  b) for the newLine string
		 *
		 *  The message will be treated differently depending on whether the line
		 *  will be appended or inserted into the Document
		 */
		@Override
		public void flush()
		{
			String message = toString();

			if (message.length() == 0) return;

			if (isAppend)
			    handleAppend(message);
			else
			    handleInsert(message);

			reset();
		}

		/*
		 *	We don't want to have blank lines in the Document. The first line
		 *  added will simply be the message. For additional lines it will be:
		 *
		 *  newLine + message
		 */
		private void handleAppend(String message)
		{
			//  This check is needed in case the text in the Document has been
			//	cleared. The buffer may contain the EOL string from the previous
			//  message.

			if (document.getLength() == 0)
				buffer.setLength(0);

			if (EOL.equals(message))
			{
				buffer.append(message);
			}
			else
			{
				buffer.append(message);
				clearBuffer();
			}

		}
		/*
		 *  We don't want to merge the new message with the existing message
		 *  so the line will be inserted as:
		 *
		 *  message + newLine
		 */
		private void handleInsert(String message)
		{
			buffer.append(message);

			if (EOL.equals(message))
			{
				clearBuffer();
			}
		}

		/*
		 *  The message and the newLine have been added to the buffer in the
		 *  appropriate order so we can now update the Document and send the
		 *  text to the optional PrintStream.
		 */
		private void clearBuffer()
		{
			//  In case both the standard out and standard err are being redirected
			//  we need to insert a newline character for the first line only

			if (isFirstLine && document.getLength() != 0)
			{
			    buffer.insert(0, "\n");
			}

			isFirstLine = false;
			String line = buffer.toString();

			try
			{
				if (isAppend)
				{
					int offset = document.getLength();
					document.insertString(offset, line, attributes);
					textComponent.setCaretPosition( document.getLength() );
				}
				else
				{
					document.insertString(0, line, attributes);
					textComponent.setCaretPosition( 0 );
				}
			}
			catch (BadLocationException ble) {
				//ble.printStackTrace();
			}

			if (printStream != null)
			{
				printStream.print(line);
			}

			buffer.setLength(0);
		}
	}
}
