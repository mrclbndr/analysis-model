package hudson.plugins.checkstyle.parser;

import static org.junit.Assert.*;
import hudson.plugins.analysis.util.ContextHashCode;
import hudson.plugins.analysis.util.Singleton;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.ast.factory.Ast;
import hudson.plugins.ast.factory.AstFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.FilenameUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test cases for the new warnings detector.
 *
 * @author Ullrich Hafner
 */
public class NewWarningDetectorTest {

    private static final String METHOD_AST_FOLDERNAME = "MethodAst";
    private static final String ENVIRONMENT_AST_FOLDERNAME = "EnvironmentAst";
    private static final String FILE_AST_FOLDERNAME = "FileAst";
    private static final String CLASS_AST_FOLDERNAME = "ClassAst";
    private static final String METHOD_OR_CLASS_AST_FOLDERNAME = "MethodOrClassAst";
    private static final String INSTANCEVARIABLE_AST_FOLDERNAME = "InstancevariableAst";
    private static final String NAME_PACKAGE_AST_FOLDERNAME = "NamePackageAst";

    private static final String REFACTORING_NEWLINE = "_Newline";
    private static final String REFACTORING_RENAME = "_Rename";
    private static final String REFACTORING_3 = "_";
    private static final String REFACTORING_4 = "_";
    private static final String REFACTORING_5 = "_";
    private static final String REFACTORING_6 = "_";
    private static final String REFACTORING_7 = "_";
    private static final String REFACTORING_8 = "_";
    private static final String REFACTORING_9 = "_";
    private static final String REFACTORING_10 = "_";

    /**
     * Verifies that the insertion of a new line above the warning does produce a different hashCode.
     */
    @Test
    public void testInsertLineAboveWarning() {
        FileAnnotation beforeWarning = readWarning("before/" + METHOD_AST_FOLDERNAME + "/InsertLine.xml");
        FileAnnotation afterWarning = readWarning("after/" + METHOD_AST_FOLDERNAME + "/InsertLine.xml");

        verifyWarning(beforeWarning, "Javadoc", 7, "InsertLine.java");
        verifyWarning(afterWarning, "Javadoc", 8, "InsertLine.java");

        int beforeCode = createHashCode("before/" + METHOD_AST_FOLDERNAME + "/InsertLine.java", 7);
        int afterCode = createHashCode("after/" + METHOD_AST_FOLDERNAME + "/InsertLine.java", 8);

        assertNotEquals("Hash codes do not match: ", beforeCode, afterCode);
    }

    /**
     * Verifies that the insertion of a new line before the package declaration does not change the hash code.
     */
    @Test
    public void testInsertLineBeforePackage() {
        FileAnnotation beforeWarning = readWarning("before/" + METHOD_AST_FOLDERNAME + "/InsertLine.xml");
        FileAnnotation afterWarning = readWarning("after/" + METHOD_AST_FOLDERNAME + "/InsertLine2.xml");

        verifyWarning(beforeWarning, "Javadoc", 7, "InsertLine.java");
        verifyWarning(afterWarning, "Javadoc", 8, "InsertLine.java");

        int beforeCode = createHashCode("before/" + METHOD_AST_FOLDERNAME + "/InsertLine.java", 7);
        int afterCode = createHashCode("after/" + METHOD_AST_FOLDERNAME + "/InsertLine2.java", 8);

        assertEquals("Hash codes do not match: ", beforeCode, afterCode);
    }

    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    private int createHashCode(final String fileName, final int line) {
        try {
            ContextHashCode hashCode = new ContextHashCode();
            return hashCode.create(read(fileName), "UTF-8", line);
        }
        catch (IOException e) {
            throw new RuntimeException("Can't read Java file " + fileName, e);
        }
    }

    private void verifyWarning(final FileAnnotation before, final String category, final int line, final String fileName) {
        assertEquals("Wrong category", category, before.getCategory());
        assertEquals("Wrong line", line, before.getPrimaryLineNumber());
        assertEquals("Wrong line", fileName, FilenameUtils.getName(before.getFileName()));
    }

    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    private FileAnnotation readWarning(final String fileName) {
        try {
            CheckStyleParser parser = new CheckStyleParser();
            Collection<FileAnnotation> warnings = null;
            warnings = parser.parse(read(fileName), "-");
            return Singleton.get(warnings);
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException("Can't read CheckStyle file " + fileName, e);
        }
    }

    private InputStream read(final String fileName) {
        return NewWarningDetectorTest.class.getResourceAsStream(fileName);
    }

    /**
     * Verifies that the ast calculates the same hashcode. (inclusive Refactoring: Newline).
     */
    @Test
    public void testFinalClassWithNewLines() {
        checkThatHashesMatching("FinalClass", REFACTORING_NEWLINE);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testNeedBracesWithNewLines() {
        checkThatHashesMatching("NeedBraces", REFACTORING_NEWLINE);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testInterfaceIsTypeWithNewLines() {
        checkThatHashesMatching("InterfaceIsType", REFACTORING_NEWLINE);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testExplicitInitializationWithNewLines() {
        checkThatHashesMatching("ExplicitInitialization", REFACTORING_NEWLINE);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testMethodNameWithNewLines() {
        checkThatHashesMatching("MethodName", REFACTORING_NEWLINE);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testRedundantModifierWithNewLines() {
        checkThatHashesMatching("RedundantModifier", REFACTORING_NEWLINE);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testPackageNameWithNewLines() {
        checkThatHashesMatching("PackageName", REFACTORING_NEWLINE);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testFinalClassWithRename() {
        checkThatHashesMatching("FinalClass", REFACTORING_RENAME);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testNeedBracesWithRename() {
        checkThatHashesMatching("NeedBraces", REFACTORING_RENAME);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testInterfaceIsTypeWithRename() {
        checkThatHashesMatching("InterfaceIsType", REFACTORING_RENAME);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testExplicitInitializationWithRename() {
        checkThatHashesMatching("ExplicitInitialization", REFACTORING_RENAME);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Test
    public void testMethodNameWithRename() {
        checkThatHashesMatching("MethodName", REFACTORING_RENAME);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Ignore
    @Test
    public void testRedundantModifierWithRename() {
        checkThatHashesMatching("RedundantModifier", REFACTORING_RENAME);
    }

    /**
     * Verifies that the ast calculates the same hashcode.
     */
    @Ignore
    @Test
    public void testPackageNameWithRename() {
        checkThatHashesMatching("PackageName", REFACTORING_RENAME);
    }

    /**
     * Verifies that the ClassAst works right.
     */
    @Test
    public void testClassAst() {
        String expectedResult = "PACKAGE_DEF ANNOTATIONS DOT DOT IDENT IDENT IDENT SEMI CLASS_DEF MODIFIERS LITERAL_PUBLIC LITERAL_CLASS IDENT OBJBLOCK LCURLY VARIABLE_DEF MODIFIERS LITERAL_PRIVATE TYPE LITERAL_INT IDENT SEMI CTOR_DEF MODIFIERS LITERAL_PRIVATE IDENT LPAREN PARAMETERS RPAREN SLIST RCURLY RCURLY ";

        Ast ast = getAst("FinalClass_Newline.java", "FinalClass_Newline.xml", CLASS_AST_FOLDERNAME, false);

        checkAst(expectedResult, ast);
    }

    /**
     * Verifies that the EnvironmentAst works right.
     */
    @Test
    public void testEnvironmentAst() {
        String expectedResult = "LITERAL_IF LPAREN EXPR GE IDENT NUM_INT RPAREN METHOD_DEF MODIFIERS LITERAL_PUBLIC TYPE LITERAL_INT IDENT LPAREN PARAMETERS PARAMETER_DEF MODIFIERS FINAL TYPE LITERAL_INT IDENT RPAREN SLIST RCURLY EXPR ASSIGN DOT LITERAL_THIS IDENT IDENT SEMI LITERAL_RETURN EXPR IDENT SEMI LITERAL_ELSE SLIST LITERAL_RETURN EXPR UNARY_MINUS IDENT SEMI ";

        Ast ast = getAst("NeedBraces_Newline.java", "NeedBraces_Newline.xml", ENVIRONMENT_AST_FOLDERNAME, false);

        checkAst(expectedResult, ast);
    }

    /**
     * Verifies that the FileAst works right.
     */
    @Test
    public void testFileAst() {
        String expectedResult = "PACKAGE_DEF ANNOTATIONS DOT DOT IDENT IDENT IDENT SEMI IMPORT DOT DOT IDENT IDENT IDENT SEMI INTERFACE_DEF MODIFIERS LITERAL_PUBLIC LITERAL_INTERFACE IDENT OBJBLOCK LCURLY VARIABLE_DEF MODIFIERS TYPE LITERAL_INT IDENT ASSIGN EXPR NUM_INT SEMI VARIABLE_DEF MODIFIERS TYPE IDENT IDENT ASSIGN EXPR STRING_LITERAL SEMI VARIABLE_DEF MODIFIERS TYPE IDENT IDENT ASSIGN EXPR LITERAL_NEW IDENT LPAREN ELIST EXPR NUM_INT RPAREN SEMI RCURLY ";

        Ast ast = getAst("InterfaceIsType_Newline.java", "InterfaceIsType_Newline.xml", FILE_AST_FOLDERNAME, false);

        checkAst(expectedResult, ast);
    }

    /**
     * Verifies that the InstancevariableAst works right.
     */
    @Test
    public void testInstancevariableAst() {
        String expectedResult = "OBJBLOCK VARIABLE_DEF MODIFIERS LITERAL_PRIVATE TYPE IDENT IDENT SEMI VARIABLE_DEF MODIFIERS LITERAL_PRIVATE TYPE LITERAL_INT IDENT ASSIGN EXPR NUM_INT SEMI VARIABLE_DEF MODIFIERS LITERAL_PRIVATE FINAL TYPE IDENT IDENT SEMI ";

        Ast ast = getAst("ExplicitInitialization_Newline.java", "ExplicitInitialization_Newline.xml",
                INSTANCEVARIABLE_AST_FOLDERNAME, false);

        checkAst(expectedResult, ast);
    }

    /**
     * Verifies that the MethodAst works right.
     */
    @Test
    public void testMethodAst() {
        String expectedResult = "METHOD_DEF MODIFIERS LITERAL_PUBLIC LITERAL_STATIC TYPE LITERAL_VOID IDENT LPAREN PARAMETERS RPAREN SLIST EXPR METHOD_CALL DOT DOT IDENT IDENT IDENT ELIST EXPR STRING_LITERAL RPAREN SEMI RCURLY ";

        Ast ast = getAst("MethodName_Newline.java", "MethodName_Newline.xml", METHOD_AST_FOLDERNAME, false);

        checkAst(expectedResult, ast);
    }

    /**
     * Verifies that the MethodOrClassAst works right.
     */
    @Test
    public void testMethodOrClassAstInMethodlevel() {
        String expectedResult = "METHOD_DEF MODIFIERS LITERAL_PUBLIC TYPE LITERAL_INT IDENT LPAREN PARAMETERS PARAMETER_DEF MODIFIERS TYPE LITERAL_INT IDENT COMMA PARAMETER_DEF MODIFIERS TYPE LITERAL_INT IDENT RPAREN SEMI ";

        Ast ast = getAst("RedundantModifier_Newline.java", "RedundantModifier_Newline.xml",
                METHOD_OR_CLASS_AST_FOLDERNAME, false);

        checkAst(expectedResult, ast);
    }

    /**
     * Verifies that the MethodOrClassAst works right.
     */
    @Test
    public void testMethodOrClassAstInClasslevel() {
        String expectedResult = "PACKAGE_DEF ANNOTATIONS DOT DOT IDENT IDENT IDENT SEMI CLASS_DEF MODIFIERS LITERAL_PUBLIC LITERAL_CLASS IDENT OBJBLOCK LCURLY CTOR_DEF MODIFIERS LITERAL_PUBLIC IDENT LPAREN PARAMETERS RPAREN SLIST RCURLY RCURLY ";

        Ast ast = getAst("JavadocStyle_Newline.java", "JavadocStyle_Newline.xml", METHOD_OR_CLASS_AST_FOLDERNAME, false);

        checkAst(expectedResult, ast);
    }

    /**
     * Verifies that the NamePackageAst works right.
     */
    @Test
    public void testNamePackageAst() {
        String expectedResult = "PACKAGE_DEF ANNOTATIONS DOT DOT IDENT IDENT IDENT SEMI ";

        Ast ast = getAst("PackageName_Newline.java", "PackageName_Newline.xml", NAME_PACKAGE_AST_FOLDERNAME, false);

        checkAst(expectedResult, ast);
    }

    private void checkAst(final String expectedResult, final Ast ast) {
        String realResult = ast.chosenAreaAsString(' ');

        compareString(expectedResult, realResult);
    }

    private String matchWarningTypeToFoldername(final String warningType) {
        String ordnerName = "";
        if (Arrays.asList(AstFactory.getClassAst()).contains(warningType)) {
            ordnerName = CLASS_AST_FOLDERNAME;
        }
        else if (Arrays.asList(AstFactory.getEnvironmentAst()).contains(warningType)) {
            ordnerName = ENVIRONMENT_AST_FOLDERNAME;
        }
        else if (Arrays.asList(AstFactory.getFileAst()).contains(warningType)) {
            ordnerName = FILE_AST_FOLDERNAME;
        }
        else if (Arrays.asList(AstFactory.getInstancevariableAst()).contains(warningType)) {
            ordnerName = INSTANCEVARIABLE_AST_FOLDERNAME;
        }
        else if (Arrays.asList(AstFactory.getMethodAst()).contains(warningType)) {
            ordnerName = METHOD_AST_FOLDERNAME;
        }
        else if (Arrays.asList(AstFactory.getMethodOrClassAst()).contains(warningType)) {
            ordnerName = METHOD_OR_CLASS_AST_FOLDERNAME;
        }
        else if (Arrays.asList(AstFactory.getNamePackageAst()).contains(warningType)) {
            ordnerName = NAME_PACKAGE_AST_FOLDERNAME;
        }

        return ordnerName;
    }

    /**
     * Use this method for calculating the hashcode, if the filename (inclusive file extension) before is equal after,
     * except that the after-filename has only(!) a postfix of the refactoring.
     *
     * @param warningType
     *            the warningType
     * @param refactoring
     *            the refactoring
     */
    private void checkThatHashesMatching(final String warningType, final String refactoring) {
        checkThatHashesMatching(warningType, warningType, warningType, refactoring);
    }

    /**
     * Use this method for calculating the hashcode, if the filename before is different as after, disregarded that the
     * after-filename has a postfix of the refactoring.
     *
     * @param warningType
     *            the warningType
     * @param beforeClass
     *            the name of the class before
     * @param afterClass
     *            the name of the class after
     * @param refactoring
     *            the refactoring
     */
    private void checkThatHashesMatching(final String warningType, final String beforeClass, final String afterClass,
            final String refactoring) {
        String foldername = matchWarningTypeToFoldername(warningType);
        String hashBefore = calcHashcode(beforeClass, foldername, true);
        String hashAfter = calcHashcode(afterClass + refactoring, foldername, false);

        compareHashcode(hashBefore, hashAfter);
    }

    private String calcHashcode(final String filename, final String foldername, final boolean beforeRefactoring) {
        String javaFile = filename.concat(".java");
        String xmlFile = filename.concat(".xml");

        return calcHashcode(javaFile, foldername, xmlFile, beforeRefactoring);
    }

    private void compareHashcode(final String hashBefore, final String hashAfter) {
        assertNotNull("Hash code isn't not null", hashBefore);
        assertEquals("Hash codes don't match: ", hashBefore, hashAfter);
    }

    private void compareString(final String first, final String second) {
        assertNotNull("String isn't not null", first);
        assertEquals("Strings don't match: ", first, second);
    }

    private String calcHashcode(final String javaFile, final String foldername, final String xmlFile,
            final boolean before) {
        Ast ast;
        if (before) {
            ast = getAst(javaFile, xmlFile, foldername, true);
        }
        else {
            ast = getAst(javaFile, xmlFile, foldername, false);
        }

        return ast.calcSha(ast.chooseArea());
    }

    private Ast getAst(final String javaFile, final String xmlFile, final String foldername, final boolean before) {
        String workspace = System.getProperty("user.dir");

        FileAnnotation fileAnnotation = readWarning(calcCorrectPath(xmlFile, foldername, before));

        @SuppressWarnings("PMD.InsufficientStringBufferDeclaration")
        StringBuilder stringBuilderJavafile = new StringBuilder();
        stringBuilderJavafile.append(workspace).append("/src/test/resources/hudson/plugins/checkstyle/parser/");
        stringBuilderJavafile.append(calcCorrectPath(javaFile, foldername, before));

        return AstFactory.getInstance(stringBuilderJavafile.toString(), fileAnnotation);
    }

    private String calcCorrectPath(final String nameOfFile, final String foldername, final boolean before) {
        StringBuilder stringBuilder = new StringBuilder();
        if (before) {
            stringBuilder.append("before/");
        }
        else {
            stringBuilder.append("after/");
        }
        stringBuilder.append(foldername);
        stringBuilder.append('/');
        stringBuilder.append(nameOfFile);

        return stringBuilder.toString();
    }
}