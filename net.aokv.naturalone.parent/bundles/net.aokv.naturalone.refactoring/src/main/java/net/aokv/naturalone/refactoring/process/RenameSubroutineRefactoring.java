package net.aokv.naturalone.refactoring.process;

import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.softwareag.naturalone.natural.parser.ENaturalASTNodeType;
import com.softwareag.naturalone.natural.parser.ast.internal.INaturalASTNode;
import com.softwareag.naturalone.natural.parser.internal.INode;
import com.softwareag.naturalone.natural.parser.internal.ITokenForEditor;
import com.softwareag.naturalone.natural.sourceeditor.editor.internal.INaturalParsingUnit;

@SuppressWarnings("restriction")
public class RenameSubroutineRefactoring extends Refactoring
{
	private TextSelection selectedText;
	private INaturalParsingUnit parsingUnit;
	private IDocument document;

	private DocumentChange change;
	private String newSubroutineName;
	private String name;
	private boolean updateReferences = false;

	public RenameSubroutineRefactoring(INaturalParsingUnit parsingUnit, TextSelection selectedText,
			IDocument document, String name)
	{
		this.parsingUnit = parsingUnit;
		this.selectedText = selectedText;
		this.document = document;
		this.name = name;
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor monitor)
			throws CoreException, OperationCanceledException
	{
		RefactoringStatus status = new RefactoringStatus();
		try
		{
			monitor.beginTask("Checking conditions...", 2);

			change = new DocumentChange(getName(), document);
			MultiTextEdit edit = new MultiTextEdit();

			INaturalASTNode astRoot = parsingUnit.getNaturalASTRoot();

			if (astRoot.hasChildren())
			{
				processNodeList(selectedText, astRoot, edit);
			}

			change.setEdit(edit);

		}
		finally
		{
			monitor.done();
		}
		return status;
	}

	private void processNodeList(final TextSelection textSelection, INaturalASTNode astRoot, MultiTextEdit multiTextEditedit)
	{
		for (Iterator<INode> iterator = astRoot.getChildren().iterator(); iterator.hasNext();)
		{
			INaturalASTNode astNode = (INaturalASTNode) iterator.next();

			if (astNode.hasChildren())
			{
				processNodeList(textSelection, astNode, multiTextEditedit);
			}
			processNode(textSelection, multiTextEditedit, astNode);
		}
	}

	private void processNode(TextSelection textSelection, MultiTextEdit multiTextEdit, INaturalASTNode astNode)
	{
		ENaturalASTNodeType type = astNode.getNaturalASTNodeType();
		if (type.equals(ENaturalASTNodeType.STATEMENT) || type.equals(ENaturalASTNodeType.SUBROUTINE))
		{
			processToken(textSelection, multiTextEdit, astNode.getTokenForPosition());
		}
	}

	private void processToken(TextSelection textSelection, MultiTextEdit multiTextEdit, ITokenForEditor token)
	{
		if (token != null)
		{
			if (textSelection.getText().equals(token.getImage()) && textSelection.getLength() == token.getLength())
			{
				multiTextEdit.addChild(new ReplaceEdit(token.getOffset(), token.getLength(), newSubroutineName));
			}
		}
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor arg0)
			throws CoreException, OperationCanceledException
	{
		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor arg0) throws CoreException, OperationCanceledException
	{
		return change;
	}

	@Override
	public String getName()
	{
		return name;
	}

	public void setNewSubroutineName(String text)
	{
		this.newSubroutineName = text;
	}

	public void setUpdateReferences(boolean update)
	{
		this.updateReferences = update;
	}

}
