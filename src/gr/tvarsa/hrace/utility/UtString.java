package gr.tvarsa.hrace.utility;

import java.text.DecimalFormat;

public class UtString
{
    public static final int JUSTIFY_LEFT = -1;
    public static final int JUSTIFY_CENTER = 0;
    public static final int JUSTIFY_RIGHT = 1;

    /**
     * Returns the part of the string that finishes at the start of the supplied substring.
     *
     * @param string
     *            the string to be searched
     * @param substring
     *            the substring to scan for
     * @param allIfMissing
     *            boolean to modify the returned result if the substring search does not find a match
     * @return the matched part of the string. If the searched substring is not found, the method return is defined by the
     *         allIfMissing boolean: return the original string if allIfMissing is true, or a zero-length string if allIfMissing
     *         is false
     */
    public static String copyBefore(String string, String substring, boolean allIfMissing)
    {
        int i = string.indexOf(substring);
        if (i < 0) return allIfMissing ? string : "";
        return string.substring(0, i);
    }

    /**
     * Returns the part of the string that starts right after the end of the supplied substring.
     *
     * @param string
     *            the string to be searched
     * @param substring
     *            the substring to scan for
     * @param allIfMissing
     *            boolean to modify the returned result if the substring search does not find a match
     * @return the matched part of the string. If the searched substring is not found, the method return is defined by the
     *         allIfMissing boolean: return the original string if allIfMissing is true, or a zero-length string if allIfMissing
     *         is false
     */
    public static String copyAfter(String string, String substring, boolean allIfMissing)
    {
        int i = string.indexOf(substring);
        if (i < 0) return allIfMissing ? string : "";
        return string.substring(i + substring.length());
    }

    public static String justifyString(String s, int width, int justification)
    {
        if (justification == JUSTIFY_CENTER)
        {
            width = width - s.length();
            int leftSpaces = width / 2;
            int rightSpaces = width - leftSpaces;
            for (int i = 0; i < leftSpaces; i++)
                s = " " + s;
            for (int i = 0; i < rightSpaces; i++)
                s = s + " ";
        }
        else if (justification == JUSTIFY_RIGHT)
            for (int i = s.length() + 1; i <= width; i++)
                s = " " + s;
        else
            for (int i = s.length() + 1; i <= width; i++)
                s = s + " ";
        return s;
    }

    public static String justifyLeft(String s, int width)
    {
        return justifyString(s, width, JUSTIFY_LEFT);
    }

    public static String justifyRight(String s, int width)
    {
        return justifyString(s, width, JUSTIFY_RIGHT);
    }

    private static double round(double num, int decimals)
    {
        double pow10 = Math.pow(10, decimals);
        return Math.round(num * pow10) / pow10;
    }

    public static String roundStr(double num, int wholeDigits, int decimals)
    {
        String s = "" + round(num, decimals);
        int pointAt = s.indexOf(".");
        if (pointAt == -1)
        {
            s += ".0";
            pointAt = s.indexOf(".");
        }
        else if (pointAt == 0)
        {
            s = "0" + s;
            pointAt = s.indexOf(".");
        }
        if (wholeDigits > 0 && wholeDigits >= pointAt) for (int i = wholeDigits - pointAt - 1; i >= 0; i--)
        {
            s = " " + s;
            pointAt++;
        }
        if (decimals < 1)
            s = s.substring(0, pointAt);
        else
            for (int i = s.length() - pointAt; i <= decimals; i++)
                s += "0";
        return s;
    }

    public static String roundStr(double num, int decimals)
    {
        if (decimals < 1) return "" + (long)num;
        String s = "" + round(num, decimals);
        if (s.indexOf(".") == -1) s = s + ".";
        if (s.indexOf("E") >= 3) // scientific notation
            s = new DecimalFormat("0.0").format(num);
        int count = s.indexOf(".") + 1;
        count = s.length() - count;
        if (count > decimals) return s.substring(0, s.length() - (count - decimals));
        for (int i = count + 1; i <= decimals; i++)
            s = s + "0";
        return s;
    }

    /**
     * Bounds-safe substring method. If the starting or ending indices are outside the string's length, they are adjusted
     * accordingly
     *
     * @param s
     *            the string to extract the substring from
     * @param start
     *            the first character position of the substring
     * @param afterEnd
     *            the first character position after the end of the substring
     * @return the specified substring
     */
    public static String substring(String s, int start, int afterEnd)
    {
        int length = s.length();
        if (start < 0) start = 0;
        if (afterEnd > length) afterEnd = length;
        if (start >= length || afterEnd < 1 || start >= afterEnd) return "";
        return s.substring(start, afterEnd);
    }

    /**
     * Bounds-safe substring method. The method returns the remaining of the string from the starting character supplied. If the
     * starting index is outside the string's length, it is adjusted accordingly
     *
     * @param s
     *            the string to extract the substring from
     * @param start
     *            the first character position of the substring
     * @return the specified substring
     */
    public static String substring(String s, int start)
    {
        return substring(s, start, s.length());
    }

    public static String commaString(String s)
    {
        int at = s.indexOf(".");
        if (at >= 0) return commaString(substring(s, 0, at)) + substring(s, at);
        String t = "";
        String sign = "";
        if (s.length() > 0 && s.charAt(0) == '-')
        {
            s = s.substring(1);
            sign = "-";
        }
        int count = 0;
        for (int i = s.length() - 1; i >= 0; i--)
        {
            t = s.charAt(i) + t;
            count++;
            if (count % 3 == 0 && i > 0) t = "," + t;
        }
        return sign + t;
    }

    public static String commaString(long i)
    {
        return commaString("" + i);
    }
    public static String padInt(long no, int width)
    {
                return padInt(no,width,'0');
    }

    public static String padInt(long no, int width, char padCharacter)
    {
        String s = "" + no;
        for (int i = s.length() + 1; i <= width; i++)
            s = padCharacter + s;
        return s;
    }

    public static String plural(int count, String text)
    {
        return plural(count, "0", text);
    }

    /**
     * Converts the pair of an amount and a word to a string with the word in either singular or plural form depending on the
     * amount. Assuming that <code>x = 0</code>, <code>y = 1</code>, <code>z = 2</code>, then <blockquote>
     *
     * <pre>
     * z, &quot;car&quot; -&gt; 2 cars
     *  y, &quot;car&quot; -&gt; 1 car
     * </pre>
     *
     * </blockquote> For the special case of zero, it is possible to make the string state a word instead of 0 as a quantity,
     * e.g. <blockquote>
     *
     * <pre>
     * y, &quot;0&quot;, car  -&gt; 1 car
     * x, &quot;0&quot;, car  -&gt; 0 cars
     * y, &quot;no&quot;, car -&gt; 1 car
     * x, &quot;no&quot;, car -&gt; no cars
     * </pre>
     *
     * </blockquote> Some words have irregular plurals. This be recreated by supplying the plural ending preceeded by dashes,
     * each indicating how many letters of the word will be replaced. This plural form must be added to the end of the word
     * after a '|' character. Alternatively, the plural form may be supplied if the ending is not preceeded by dashes. For
     * example <blockquote>
     *
     * <pre>
     * y, &quot;try|-ies&quot;    -&gt; 1 try
     * z, &quot;try|-ies&quot;    -&gt; 2 tries
     * x, &quot;man|--en&quot;    -&gt; 0 men
     * z, &quot;goose|geese&quot; -&gt; 2 geese
     * </pre>
     *
     * </blockquote> If the word is detected to be in uppercase, so will the plural form.
     *
     * @param amount
     *            number of items
     * @param zero
     *            what to report at the "0" position if count is zero
     * @param word
     *            the word to be converted
     * @return the plural quantity of the amount and word supplied
     */
    public static String plural(int amount, String zero, String word) // ' car',' try|-ies',' man|--en'
    {
        String root;
        String plural;
        boolean upperCase;
        int splitAt = word.indexOf('|');
        if (amount == 1)
        {
            if (splitAt != -1) word = word.substring(0, splitAt);
            return ("1 " + word).trim();
        }
        if (splitAt >= 0)
        {
            root = word.substring(0, splitAt).trim();
            upperCase = !root.equals("") && root.equals(root.toUpperCase());
            plural = word.substring(splitAt + 1).trim();
            if (plural.length() == 0) plural = word + "s";
            int toReplace = plural.lastIndexOf('-') + 1;
            if (toReplace == 0)
                root = "";
            else
            {
                plural = plural.substring(Math.min(toReplace, plural.length()));
                root = root.substring(0, Math.max(root.length() - toReplace, 0));
            }
        }
        else
        {
            root = word;
            upperCase = !root.equals("") && root.equals(root.toUpperCase());
            plural = "s";
        }
        String newWord;
        if (amount == 0)
            newWord = zero;
        else
            newWord = "" + amount;
        newWord += " " + root + plural;
        if (upperCase && !root.equals("")) newWord = newWord.toUpperCase();
        return newWord;
    }

    public static int stringToInt(String s)
    {
        return stringToInt(s, 0);
    }

    public static int stringToInt(String s, int valueIfMissing)
    {
        if (s == null || s.length() == 0) return valueIfMissing;
        String t = "";
        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if (Character.isDigit(c))
                t = t + c;
            else if (t.length() == 0 && c == '-') t = "-";
        }
        int result = valueIfMissing;
        try
        {
            result = Integer.parseInt(t);
        }
        catch (Exception e)
        {
            return valueIfMissing;
        }
        return result;
    }

}
