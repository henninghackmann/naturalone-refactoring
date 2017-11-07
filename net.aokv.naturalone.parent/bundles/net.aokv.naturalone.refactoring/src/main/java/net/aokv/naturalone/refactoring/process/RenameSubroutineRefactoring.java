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
import com.softwareag.naturalone.natural.sourceeditor.editor.internal.NaturalSourceEditor;

@SuppressWarnings("restriction")
public class RenameSubroutineRefactoring extends Refactoring
{
	private TextSelection selectedText;
	private INaturalParsingUnit parsingUnit;
	private IDocument document;

	private DocumentChange change;
	private String newSubroutineName;
	private String name;

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

	private void processNodeList(final TextSelection textSel, INaturalASTNode astRoot, MultiTextEdit edit)
	{
		for (Iterator<INode> iterator = astRoot.getChildren().iterator(); iterator.hasNext();)
		{
			INaturalASTNode node = (INaturalASTNode) iterator.next();

			if (node.hasChildren())
			{
				processNodeList(textSel, node, edit);
			}
			processNode(textSel, edit, node);
		}
	}

	private void processNode(TextSelection textSel, MultiTextEdit edit, INaturalASTNode node)
	{
		ENaturalASTNodeType type = node.getNaturalASTNodeType();
		if (type.equals(ENaturalASTNodeType.STATEMENT) || type.equals(ENaturalASTNodeType.SUBROUTINE))
		{
			ITokenForEditor token = node.getTokenForPosition();

			if (token != null)
			{
				if (textSel.getText().equals(token.getImage()) && textSel.getLength() == token.getLength())
				{
					edit.addChild(new ReplaceEdit(token.getOffset(), token.getLength(), newSubroutineName));
				}
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

	public RefactoringStatus setNewSubroutineName(String text)
	{
		this.newSubroutineName = text;
		return new RefactoringStatus();
	}
	
	public void setUpdateReferences(boolean selection)
	{
		// TODO Auto-generated method stub
	}

}
