package hudson.plugins.warnings.parser;

import hudson.plugins.analysis.util.model.Priority;

import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;

/**
 * A parser for the Doxygen warnings.
 *
 * @author Frederic Chateau
 * @author Bruno Matos
 */
public class DoxygenParser extends RegexpDocumentParser {
    /** A Doxygen warning. */
    static final String WARNING_CATEGORY = "Doxygen warning";
    /** Warning type of this parser. */
    static final String WARNING_TYPE = "doxygen";
    /**
     * Pattern of Doxygen warnings.
     * Here are explanations of this fairly complex (yet efficient) pattern.
     * The pattern has 2 main parts:
     *  - one for doxygen messages related to a file or a function
     *  - one for global doxygen messages
     *
     * Global messages match the following simple pattern: "(Notice|Warning|Error): (.+)"
     * Local messages are more complicated:
     *  - if it is a file we assume doxygen always prints the absolute path
     *    (eg: /home/user/project/foo.cpp, C:\project\foo.cpp) so the
     *    expression (?:/|[A-Za-z]:) matches either a slash or a volume letter
     *    like C:. Then we match everything until the colon sign ':', which is
     *    followed by a line number (can be -1 in some cases, which explains
     *    why the group is (-?\\d+). Finally, the warning type is mandatory and
     *    can be either "Warning" or "Error"
     *  - if it is a function, the function name is displayed between angle
     *    brackets, and followed by a line number. Finally, the warning type
     *    is sometimes printed, but not always, which is why the expression
     *    is (?:: (Warning|Error))?
     * In both cases, local warnings are followed by a multi-line message that
     * can get quite complex.
     * The message is made of the remaining of the current line and of
     * an arbitrary long (and optional) sequence of lines which can take many
     * shapes, but that never begins like an absolute path or a function.
     * So we accept anything except '/' and '<' for the first character,
     * anything except ':' (windows drive colon) for the second character,
     * and anything except '/' (doxygen uses slash instead of backslash, after
     * the drive colon) for the third character.
     * For each of these 3 characters we also refuse newlines to avoid getting
     * empty or incomplete lines (lines with less than 3 characters are
     * suspicious).
     * After these 3 characters, we accept anything until the end of the line.
     * The whole multi-line message is matched by:
     * (.+(?:\\n[^/<\\n][^:\\n][^\\\\\\n].+)*
     * */
    private static final String DOXYGEN_WARNING_PATTERN =
        "^(?:(?:((?:/|[A-Za-z]:).+?):(-?\\d+): (Warning|Error)|<.+>:-?\\d+(?:: (Warning|Error))?): (.+(?:\\n[^/<\\n][^:\\n][^/\\n].+)*)|(Notice|Warning|Error): (.+))$";

    private static final int FILE_NAME_GROUP = 1;
    private static final int FILE_LINE_GROUP = 2;
    private static final int FILE_TYPE_GROUP = 3;
    private static final int FUNC_TYPE_GROUP = 4;
    private static final int LOCAL_MESSAGE_GROUP = 5;
    private static final int GLOBAL_TYPE_GROUP = 6;
    private static final int GLOBAL_MESSAGE_GROUP = 7;

    /**
     * Creates a new instance of <code>DoxygenParser</code>.
     */
    public DoxygenParser() {
        super(DOXYGEN_WARNING_PATTERN, true, "Doxygen");
    }

    /** {@inheritDoc} */
    @Override
    protected Warning createWarning(final Matcher matcher) {
        String message;
        String fileName = "";
        int lineNumber = 0;
        Priority priority;

        if (StringUtils.isNotBlank(matcher.group(LOCAL_MESSAGE_GROUP))) {
            // Warning message local to a file or a function
            message = matcher.group(LOCAL_MESSAGE_GROUP);

            if (StringUtils.isNotBlank(matcher.group(FILE_NAME_GROUP))) {
                // File related warning
                fileName = matcher.group(FILE_NAME_GROUP);
                lineNumber = getLineNumber(matcher.group(FILE_LINE_GROUP));
                priority = parsePriority(matcher.group(FILE_TYPE_GROUP));
            }
            else {
                // Function related warning
                priority = parsePriority(matcher.group(FUNC_TYPE_GROUP));
            }
        } else if(StringUtils.isNotBlank(matcher.group(GLOBAL_MESSAGE_GROUP))) {
            // Global warning message
            message = matcher.group(GLOBAL_MESSAGE_GROUP);
            priority = parsePriority(matcher.group(GLOBAL_TYPE_GROUP));
        } else {
            message = "Unknown doxygen error.";
            priority = Priority.HIGH;
            // should never happen
        }

        return new Warning(fileName, lineNumber, WARNING_TYPE, WARNING_CATEGORY, message, priority);
    }

    private Priority parsePriority(final String priorityLabel) {
        Priority priority;
        if (StringUtils.equalsIgnoreCase(priorityLabel, "notice")) {
            priority = Priority.LOW;
        } else if (StringUtils.equalsIgnoreCase(priorityLabel, "warning")) {
            priority = Priority.NORMAL;
        } else if (StringUtils.equalsIgnoreCase(priorityLabel, "error")) {
            priority = Priority.HIGH;
        } else {
            // empty label or other unexpected input
            priority = Priority.HIGH;
        }
        return priority;
    }
}
