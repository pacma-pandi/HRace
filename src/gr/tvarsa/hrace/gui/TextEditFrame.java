package gr.tvarsa.hrace.gui;

import gr.tvarsa.hrace.utility.UtGui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class TextEditFrame
{
	protected TextPane textAreaComponent;
	protected JTextArea textArea;
	protected JFrame frame;
	private int extendedState = Frame.NORMAL;
	private boolean modal = false;
	private JFrame parent = null;

	public int getExtendedState()
	{
		return extendedState;
	}

	public void setExtendedState(int newExtendedState)
	{
		extendedState = newExtendedState;
	}

	public JTextArea getTextArea()
	{
		return textArea;
	}

	public String getText()
	{
		return textArea.getText();
	}

	public void setText(String text)
	{
		textArea.setText(text);
	}

	public String getSelectedText()
	{
		return textArea.getSelectedText();
	}

	public int getSelectionStart()
	{
		return textArea.getSelectionStart();
	}

	public void setSelectionStart(int selectionStart)
	{
		textArea.setSelectionStart(selectionStart);
	}

	public int getSelectionEnd()
	{
		return textArea.getSelectionEnd();
	}

	public void setSelectionEnd(int selectionEnd)
	{
		textArea.setSelectionEnd(selectionEnd);
	}

	public Font getFont()
	{
		return textArea.getFont();
	}

	public void setFont(Font font)
	{
		textArea.setFont(font);
	}

	public void setCaretPosition(int position)
	{
		textArea.setCaretPosition(position);
	}

	public void setCaretPosition(int line, int column)
	{
		textAreaComponent.setCaretPosition(line, column);
	}

	public Point getVisibleLineRange()
	{
		return textAreaComponent.getVisibleLineRange();
	}

	/**
	 * Scrolls text to the point where the last text line is shown at the bottom of the screen
	 */
	public void setCaretToTextEnd()
	{
		textAreaComponent.setCaretToTextEnd();
	}

	public boolean isTextEndVisible()
	{
		return textAreaComponent.isTextEndVisible();
	}

	public void setEditable(boolean mode)
	{
		textArea.setEditable(mode);
	}

	public boolean isEditable()
	{
		return textArea.isEditable();
	}

	public JFrame getFrame()
	{
		return frame;
	}

	public int getLineCount()
	{
		return textArea.getLineCount();
	}

	public String[] getLines()
	{
		return textAreaComponent.getLines();
	}

	public int getTextLineStartOffset(int line)
	{
		return textAreaComponent.getTextLineStartOffset(line);
	}

	public int getTextLineEndOffset(int line)
	{
		return textAreaComponent.getTextLineEndOffset(line);
	}

	public int getCaretLine()
	{
		return textAreaComponent.getCaretLine();
	}

	public int getStartingSelectedLine(boolean fullySelected)
	{
		return textAreaComponent.getStartingSelectedLine(fullySelected);
	}

	public int getEndingSelectedLine(boolean fullySelected)
	{
		return textAreaComponent.getEndingSelectedLine(fullySelected);
	}

	public String getLine(int line)
	{
		return textAreaComponent.getLine(line);
	}

	public void setLine(int line, String text)
	{
		textAreaComponent.setLine(line, text);
	}

	public void deleteLine(int line)
	{
		textAreaComponent.deleteLine(line);
	}

	public void insertLine(String text, int lineNo)
	{
		textAreaComponent.insertLine(text, lineNo);
	}

	public void addLine(String text)
	{
		textAreaComponent.addLine(text);
	}

	public void clearText()
	{
		textAreaComponent.clearText();
	}

	public void adjustHeightToText(int minHeight, int maxHeight)
	{
		float f = getLineCount() * frame.getFontMetrics(textArea.getFont()).getHeight() + 27; // 27 pixels around clientHeight
		int height = (int)f;
		if (height < minHeight) height = minHeight;
		if (height > maxHeight) height = maxHeight;
		frame.setSize(frame.getWidth(), height);
	}

	public TextEditFrame(JFrame parent, Image icon, String title, int width, int height, boolean resizable, Font font,
			Color foreColor, Color backColor, String[] text, boolean editable, boolean visible, boolean cutCopyPasteMenu)
	{
		textAreaComponent = new TextPane();
		textArea = textAreaComponent.getTextArea();
		JScrollPane scrollPane = new JScrollPane();
		textArea.setEditable(editable);
		if (font == null)
			textArea.setFont(new Font("Courier New", Font.PLAIN, 11));
		else
			textArea.setFont(font);
		if (backColor == null) backColor = Color.black;
		if (foreColor == null) backColor = Color.white;
		textArea.setBackground(backColor);
		textArea.setForeground(foreColor);
		scrollPane.setViewportView(textArea);
		if (text != null) for (int i = 0; i < text.length; i++)
		{
			if (i > 0) textArea.append("\n");
			textArea.append(text[i]);
		}
		textArea.setCaretPosition(0);
		frame = new JFrame(title);
		frame.setIconImage(icon);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(scrollPane, BorderLayout.CENTER);
		frame.setSize(width, height);
		frame.setResizable(resizable);
		UtGui.centerOnComponent(parent, frame);
		this.parent = parent;
		frame.setVisible(visible);
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent event)
			{
				extendedState = frame.getExtendedState();
				frame.setExtendedState(Frame.NORMAL);
				if (canClose())
				{
					setVisible(false);
					frame.dispose();
				}
			}
		});
		// if (cutCopyPasteMenu) new CutCopyPasteMenu(textArea, true);
	}

	public TextEditFrame(JFrame parent, String title, int width, int height, boolean resizable, Font font, Color foreColor,
			Color backColor, String[] text, boolean editable, boolean visible)
	{
		this(parent, null, title, width, height, resizable, font, foreColor, backColor, text, editable, visible, true);
	}

	public TextEditFrame(JFrame parent, String title, int width, int height, boolean resizable, Font font, Color foreColor,
			Color backColor, String[] text)
	{
		this(parent, title, width, height, resizable, font, foreColor, backColor, text, false, false);
	}

	public TextEditFrame(JFrame parent, String title, int width, int height, boolean resizable, Font font, Color foreColor,
			Color backColor)
	{
		this(parent, title, width, height, resizable, font, foreColor, backColor, new String[] {""});
		clearText();
	}

	public TextEditFrame(JFrame parent, String title, int width, int height, boolean resizable)
	{
		this(parent, title, width, height, resizable, null, null, null);
	}

	public TextEditFrame(JFrame parent, int width, int height)
	{
		this(parent, "", width, height, true);
	}

	public void setModal(boolean modal)
	{
		this.modal = modal;
		if (frame.isVisible()) setVisible(frame.isVisible());
	}

	public boolean canClose()
	{
		return true;
	}

	public void setTile(String title)
	{
		frame.setTitle(title);
	}

	public void setSize(int width, int height)
	{
		frame.setSize(width, height);
	}

	public void setResizable(boolean resizable)
	{
		frame.setResizable(resizable);
	}

	public void setIconImage(Image image)
	{
		frame.setIconImage(image);
	}

	public void setColors(Color foreColor, Color backColor)
	{
		if (foreColor != null) textArea.setBackground(backColor);
		if (backColor != null) textArea.setForeground(foreColor);
	}

	public void setVisible(boolean visible)
	{
		if (visible)
		{
			if (parent != null && modal) parent.setEnabled(false);
			frame.toFront();
			frame.requestFocusInWindow();
		}
		else
		{
			if (parent != null)
			{
				parent.setEnabled(true);
				parent.toFront();
			}
		}
		frame.setVisible(visible);
	}
}
