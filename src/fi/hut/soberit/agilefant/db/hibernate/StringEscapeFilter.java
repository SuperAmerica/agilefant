package fi.hut.soberit.agilefant.db.hibernate;

/**
 * UserTypeFilter to filter <'s and >'s to HTML ampersand codes.
 * 
 * @author Turkka Äijälä
 * @see UserTypeFilter
 */
public class StringEscapeFilter extends UserTypeFilter {
    private boolean enabled = false;

    /**
     * Conversions to apply. Every other element is the conversion character,
     * every other what to convert to.
     */
    private String[] conversions = { "<", "&lt;", ">", "&gt;"/*
                                                                 * , "ä",
                                                                 * "&auml;",
                                                                 * "ö",
                                                                 * "&ouml;",
                                                                 * "å",
                                                                 * "&aring;",
                                                                 * "Ä",
                                                                 * "&Auml;",
                                                                 * "Ö",
                                                                 * "&Ouml;",
                                                                 * "Å",
                                                                 * "&Aring;"
                                                                 */
    };

    /**
     * Handle converting single character in the string.
     * 
     * @param str
     *                string to convert
     * @param from
     *                character to replace
     * @param to
     *                string to replace the character with
     * @return "str" with "from"-charaters replaced with "to"-strings
     */
    private String handleSingleCharacter(String str, char from, String to) {
        String current = str;
        String result = "";

        int nextPos = current.indexOf(from);

        while (nextPos != -1) {
            result += current.substring(0, nextPos);

            result += to;

            if (nextPos + 1 <= current.length())
                current = current.substring(nextPos + 1);
            else
                break;

            nextPos = current.indexOf(from);
        }

        result += current;

        return result;
    }

    /**
     * Filter downgoing data by applying the HTML ampersand codes.
     */
    protected Object filterDown(Object ob) {
        if (!enabled) {
            return ob;
        }

        if (ob == null)
            return null;
        if (!(ob instanceof String))
            return ob;

        String str = (String) ob;

        // This isn't what we wanted!
        /*
         * String encoded; try { encoded = URLEncoder.encode(str,
         * Charset.defaultCharset().name()); }
         * catch(UnsupportedEncodingException e) { return ob; }
         * 
         * return (Object)encoded;
         */

        // go trough all the conversions, apply each
        // every other element is the conversion character,
        // every other conversion target
        /*
         * for(int i = 0; i < conversions.length; i += 2) {
         * 
         * char from = conversions[i].charAt(0); String to = conversions[i+1];
         * 
         * str = handleSingleCharacter(str, from, to); }
         * 
         * return str;
         */

        return org.springframework.web.util.HtmlUtils.htmlEscape(str);
    }
}
