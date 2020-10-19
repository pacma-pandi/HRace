package gr.tvarsa.hrace.gui;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

public class TextPane extends JScrollPane
{
    private JTextArea textArea = null;

    public TextPane()
    {
        super();
        setViewportView(getTextArea());
    }

    public JTextArea getTextArea()
    {
        if (textArea == null) textArea = new JTextArea();
        return textArea;
    }

    public boolean isEditable()
    {
        return getTextArea().isEditable();
    }

    public void setEditable(boolean editable)
    {
        getTextArea().setEditable(editable);
    }

    public Font getFont()
    {
        return getTextArea().getFont();
    }

    public void setFont(Font font)
    {
        getTextArea().setFont(font);
    }

    public String getText()
    {
        return getTextArea().getText();
    }

    public void setText(String text)
    {
        getTextArea().setText(text);
    }

    public String getSelectedText()
    {
        return getTextArea().getSelectedText();
    }

    public int getSelectionStart()
    {
        return getTextArea().getSelectionStart();
    }

    public void setSelectionStart(int selectionStart)
    {
        getTextArea().setSelectionStart(selectionStart);
    }

    public int getSelectionEnd()
    {
        return getTextArea().getSelectionEnd();
    }

    public void setSelectionEnd(int selectionEnd)
    {
        getTextArea().setSelectionEnd(selectionEnd);
    }

    public int getCaretPosition()
    {
        return getTextArea().getCaretPosition();
    }

    public void setCaretPosition(int position)
    {
        if (position < 0) position = 0;
        try
        {
            getTextArea().setCaretPosition(position);
        }
        catch (Exception e)
        {}
    }

    public void setCaretPosition(int line, int column)
    {
        int lines = getTextArea().getLineCount();
        int at = getTextLineStartOffset(line);
        if (at >= 0)
            at += column;
        else
            at = getTextLineEndOffset(lines - 1);
        getTextArea().setCaretPosition(at);
    }

    public Point getVisibleLineRange()
    {
        Graphics g = getTextArea().getGraphics();
        Insets insets = getTextArea().getInsets();
        Rectangle visibleRect = getTextArea().getVisibleRect();
        FontMetrics fm = g.getFontMetrics();
        int fontHeight = fm.getHeight();
        int startY = fm.getAscent() + insets.top;
        int startingLine = (visibleRect.y + insets.top) / fontHeight + 1;
        if (startY < visibleRect.y) startY = startingLine * fontHeight - (fontHeight - fm.getAscent());
        int endY = startY + visibleRect.height + fontHeight;
        int endingLine = startingLine;
        int y = startY;
        while (y < endY)
        {
            y += fontHeight;
            endingLine++;
        }
        return new Point(startingLine, endingLine);
    }

    public int getVisibleLineCount()
    {
        Graphics g = getTextArea().getGraphics();
        Rectangle visibleRect = getTextArea().getVisibleRect();
        FontMetrics fm = g.getFontMetrics();
        int fontHeight = fm.getHeight();
        if (fontHeight < 1) return 0;
        return (int)Math.ceil((double)visibleRect.height / fontHeight);
    }

    /**
     * Scrolls text to the point where the last text line is shown at the bottom of the screen
     */
    public void setCaretToTextEnd()
    {
        int lastLine = getTextArea().getLineCount() - 1;
        if (lastLine < 1) return;
        setCaretPosition(lastLine, 0);
    }

    public boolean isTextEndVisible()
    {
        return isTextEndVisible(0);
    }

    /**
     *
     * @param linesOffset
     *            number of lines below the current last visible line within which the text end will still be considered to be
     *            visible. For example, <code>lineOffset&nbsp=&nbsp1</code> will report the last line as visible if it is up to
     *            1 line below the actual last visible line.
     * @return
     */
    public boolean isTextEndVisible(int linesOffset)
    {
        int lastLine = getTextArea().getLineCount() - 1;
        if (lastLine < 1) return true;
        Point visibleLines = getVisibleLineRange();
        return visibleLines.y + linesOffset >= lastLine;
    }

    public String[] getLines()
    {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < getTextArea().getLineCount(); i++)
            lines.add(getLine(i));
        String stringLines[] = new String[lines.size()];
        lines.toArray(stringLines);
        return stringLines;
    }

    public int getTextLineStartOffset(int line)
    {
        try
        {
            return getTextArea().getLineStartOffset(line);
        }
        catch (Exception e)
        {
            return -1;
        }
    }

    public int getTextLineEndOffset(int line)
    {
        try
        {
            return getTextArea().getLineEndOffset(line);
        }
        catch (Exception e)
        {
            return -1;
        }
    }

    public int getCaretLine()
    {
        int start = getTextArea().getSelectionStart();
        try
        {
            int lineStart = getTextArea().getLineOfOffset(start);
            return lineStart;
        }
        catch (Exception e)
        {
            return getTextArea().getLineCount();
        }
    }

    public int getStartingSelectedLine(boolean fullySelected)
    {
        int start = getTextArea().getSelectionStart();
        int end = getTextArea().getSelectionEnd();
        if (start == end) return 0;
        try
        {
            int lineStart = getTextArea().getLineOfOffset(start);
            if (fullySelected && start > 0 && getTextArea().getLineOfOffset(start - 1) == lineStart) return lineStart + 1;
            return lineStart;
        }
        catch (Exception e)
        {
            return getTextArea().getLineCount();
        }
    }

    public int getEndingSelectedLine(boolean fullySelected)
    {
        int start = getTextArea().getSelectionStart();
        int end = getTextArea().getSelectionEnd();
        if (start == end) return getTextArea().getLineCount() - 1;
        try
        {
            int lineStart = getTextArea().getLineOfOffset(end);
            if (fullySelected && end < getTextArea().getText().length() - 1
                    && getTextArea().getLineOfOffset(end + 1) == lineStart)
                return lineStart - 1;
            if (getTextArea().getLineStartOffset(lineStart) == end) return lineStart - 1;
            return lineStart;
        }
        catch (Exception e)
        {
            return -1;
        }
    }

    public String getLine(int line)
    {
        int start, end;
        try
        {
            start = getTextArea().getLineStartOffset(line);
            end = getTextArea().getLineEndOffset(line);
            String text = getTextArea().getText(start, end - start);
            int at = text.indexOf('\n');
            if (at == 0) return "";
            if (at > 0) return text.substring(0, at);
            return text;
        }
        catch (Exception e)
        {
            return "";
        }
    }

    public void setLine(int line, String text)
    {
        int start, end, selectionStart;
        try
        {
            text = text + "\n";
            start = getTextArea().getLineStartOffset(line);
            end = getTextArea().getLineEndOffset(line);
            selectionStart = getTextArea().getSelectionStart();
            if (selectionStart != getTextArea().getSelectionEnd()) getTextArea().setSelectionEnd(selectionStart);
            getTextArea().replaceRange(text, start, end);
        }
        catch (Exception e)
        {}
    }

    public void deleteLine(int line)
    {
        int start, end;
        try
        {
            start = getTextArea().getLineStartOffset(line);
            end = getTextArea().getLineEndOffset(line);
            getTextArea().replaceRange("", start, end);
        }
        catch (Exception e)
        {}
    }

    public void insertLine(String text, int lineNo)
    {
        try
        {
            getTextArea().insert(text + "\n", getTextArea().getLineStartOffset(lineNo));
        }
        catch (Exception e)
        {}
    }

    public void addLine(String text, boolean endWithCR)
    {
        getTextArea().append(text + (endWithCR ? "\n" : ""));
    }

    public void addLine()
    {
        getTextArea().append("\n");
    }

    public void addLine(String text)
    {
        getTextArea().append(text + "\n");
    }

    public void addText(String text)
    {
        getTextArea().append(text);
    }

    public void clearText()
    {
        getTextArea().setText("");
    }

    public void scrollToPosition(int position)
    {
        try
        {
            getTextArea().setCaretPosition(position);
            Rectangle r = getTextArea().modelToView(position);
            if (r != null) getTextArea().scrollRectToVisible(r);
        }
        catch (BadLocationException e)
        {}
    }

    public int getLineCount()
    {
        return getTextArea().getLineCount();
    }

    public void scrollToStart()
    {
        scrollToPosition(0);
        // JScrollBar bar = getScrollPane().getVerticalScrollBar();
        // if (!bar.isVisible()) return;
        // bar.setValue(bar.getMinimum());
    }

    public void scrollToEnd()
    {
        scrollToPosition(getTextArea().getText().length());
        // JScrollBar bar = getScrollPane().getVerticalScrollBar();
        // if (!bar.isVisible()) return;
        // bar.setValue(bar.getMaximum());
    }
}
