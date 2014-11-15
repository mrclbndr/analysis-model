package hudson.plugins.ast.specific;

import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.ast.factory.Ast;

import java.util.List;

import com.puppycrawl.tools.checkstyle.api.DetailAST;

/**
 * FIXME: Document type MethodOrClassAst.
 *
 * @author Christian M&ouml;stl
 */
public class MethodOrClassAst extends Ast {

    /**
     * Creates a new instance of {@link MethodOrClassAst}.
     * @param filename
     * @param fileAnnotation
     */
    public MethodOrClassAst(final String filename, final FileAnnotation fileAnnotation) {
        super(filename, fileAnnotation);
        // FIXME Auto-generated constructor stub
    }

    @Override
    public List<DetailAST> chooseArea() {
        // FIXME Auto-generated method stub
        return null;
    }

}

