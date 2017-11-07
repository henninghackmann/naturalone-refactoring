package net.aokv.naturalone.refactoring.process;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.text.edits.TextEdit;
import org.junit.Before;
import org.junit.Test;

import com.softwareag.naturalone.natural.parser.ENaturalASTNodeType;
import com.softwareag.naturalone.natural.parser.ast.internal.INaturalASTNode;
import com.softwareag.naturalone.natural.parser.internal.INode;
import com.softwareag.naturalone.natural.parser.internal.ITokenForEditor;
import com.softwareag.naturalone.natural.sourceeditor.editor.internal.INaturalParsingUnit;

@SuppressWarnings("restriction")
public class RenameSubroutineRefactoringShould
{
	private static final int LENGTH_SELECTED = 14;
	private static final String NEW_SUBROUTINE_NAME = "NEW-SUBROUTINE";
	private static final String OTHER_STATEMENT = "PERFORM";
	private static final String OLD_SUBROUTINE_NAME = "OLD-SUBROUTINE";
	private static final String NAME = "RenameTest";

	private RenameSubroutineRefactoring sut;

	private INaturalParsingUnit parsingUnit;
	private TextSelection selectedText;
	private IDocument document;

	@Before
	public void setUp() throws Exception
	{
		parsingUnit = mock(INaturalParsingUnit.class);
		selectedText = mock(TextSelection.class);
		document = mock(IDocument.class);

		sut = new RenameSubroutineRefactoring(parsingUnit, selectedText, document, NAME);
	}

	@Test
	public final void testCheckInitialConditions() throws OperationCanceledException, CoreException
	{
		RefactoringStatus status = sut.checkInitialConditions(null);
		assertThat(status.isOK(), is(true));
	}

	@Test
	public final void testGetName() throws OperationCanceledException, CoreException
	{
		assertThat(sut.getName(), is(NAME));
	}

	@Test
	public final void testCheckFinalConditions() throws OperationCanceledException, CoreException
	{
		IProgressMonitor monitor = setUpMocks();

		sut.setNewSubroutineName(NEW_SUBROUTINE_NAME);
		RefactoringStatus status = sut.checkFinalConditions(monitor);
		assertThat(status.isOK(), is(true));

		DocumentChange newChange = (DocumentChange) sut.createChange(monitor);
		assertThat(newChange.getName(), is(NAME));

		TextEdit[] edits = newChange.getEdit().getChildren();
		assertThat(edits.length, is(4));
		assertThat(edits[0].toString(), is("{ReplaceEdit} [145,14] <<NEW-SUBROUTINE"));
		assertThat(edits[1].toString(), is("{ReplaceEdit} [215,14] <<NEW-SUBROUTINE"));
		assertThat(edits[2].toString(), is("{ReplaceEdit} [365,14] <<NEW-SUBROUTINE"));
		assertThat(edits[3].toString(), is("{ReplaceEdit} [555,14] <<NEW-SUBROUTINE"));
	}

	private IProgressMonitor setUpMocks()
	{
		IProgressMonitor monitor = mock(IProgressMonitor.class);
		INaturalASTNode astRoot = mock(INaturalASTNode.class);

		List<INode> nodeList = new ArrayList<INode>();

		addNode(nodeList, createAstNode(ENaturalASTNodeType.STATEMENT, 7, 207, OTHER_STATEMENT));
		addNode(nodeList, createAstNode(ENaturalASTNodeType.STATEMENT, LENGTH_SELECTED, 215, OLD_SUBROUTINE_NAME));
		addNode(nodeList, createAstNode(ENaturalASTNodeType.STATEMENT, LENGTH_SELECTED, 555, OLD_SUBROUTINE_NAME));
		addNode(nodeList, createAstNode(ENaturalASTNodeType.SUBROUTINE, LENGTH_SELECTED, 365, OLD_SUBROUTINE_NAME));

		INaturalASTNode node = createAstNode(ENaturalASTNodeType.LOCAL, 27, 125, OTHER_STATEMENT);
		List<INode> childList = new ArrayList<INode>();
		addChildNode(node, childList);
		addNode(childList, createAstNode(ENaturalASTNodeType.STATEMENT, LENGTH_SELECTED, 145, OLD_SUBROUTINE_NAME));

		addNode(nodeList, node);

		when(astRoot.getChildren()).thenReturn(nodeList);

		when(parsingUnit.getNaturalASTRoot()).thenReturn(astRoot);
		when(astRoot.hasChildren()).thenReturn(true);

		when(selectedText.getText()).thenReturn(OLD_SUBROUTINE_NAME);
		when(selectedText.getLength()).thenReturn(LENGTH_SELECTED);

		return monitor;
	}

	private void addChildNode(INaturalASTNode children, List<INode> nodeList)
	{
		when(children.getChildren()).thenReturn(nodeList);
		when(children.hasChildren()).thenReturn(true);
	}

	private void addNode(List<INode> nodeList, INaturalASTNode iNaturalASTNode)
	{
		nodeList.add(iNaturalASTNode);
	}

	private INaturalASTNode createAstNode(ENaturalASTNodeType nodeType, int tokenLength, int tokenOffset, String image)
	{
		INaturalASTNode node = mock(INaturalASTNode.class);

		when(node.getNaturalASTNodeType()).thenReturn(nodeType);

		ITokenForEditor token = mock(ITokenForEditor.class);

		when(node.getTokenForPosition()).thenReturn(token);
		when(token.getLength()).thenReturn(tokenLength);
		when(token.getOffset()).thenReturn(tokenOffset);
		when(token.getImage()).thenReturn(image);

		return node;
	}
}
