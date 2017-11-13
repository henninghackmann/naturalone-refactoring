package net.aokv.naturalone.refactoring.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.internal.e4.compatibility.CompatibilityEditor;

import com.softwareag.naturalone.natural.sourceeditor.editor.internal.INaturalParsingUnit;
import com.softwareag.naturalone.natural.sourceeditor.editor.internal.NaturalDocumentProvider;
import com.softwareag.naturalone.natural.sourceeditor.editor.internal.NaturalSourceEditor;

import net.aokv.naturalone.refactoring.process.RenameSubroutineRefactoring;
import net.aokv.naturalone.refactoring.ui.RenameSubroutineWizard;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;

@SuppressWarnings("restriction")
public class RenameSubroutine
{
	@Execute
	public int execute(@Named(IServiceConstants.ACTIVE_PART) MPart activePart,
			@Named(IServiceConstants.ACTIVE_SELECTION) TextSelection selectedText,
			@Named(IServiceConstants.ACTIVE_SHELL) Shell shell)
	{
		if (activePart.getObject() instanceof CompatibilityEditor)
		{
			IEditorPart part = ((CompatibilityEditor) activePart.getObject()).getEditor();

			if (part instanceof NaturalSourceEditor)
			{
				NaturalSourceEditor editor = (NaturalSourceEditor) part;
				NaturalDocumentProvider provider = (NaturalDocumentProvider) editor.getDocumentProvider();
				IDocument document = provider.getDocument(editor.getEditorInput());

				INaturalParsingUnit parsingUnit = editor.getNaturalParsingUnit();

				return openEditor(selectedText, shell, editor, document, parsingUnit);
			}
		}
		return -1;
	}

	private int openEditor(TextSelection selectedText, Shell shell, NaturalSourceEditor editor, IDocument document,
			INaturalParsingUnit parsingUnit)
	{
		int status = RefactoringStatus.FATAL;

		RenameSubroutineRefactoring refactoring =
				new RenameSubroutineRefactoring(parsingUnit, selectedText, document, editor.getPartName());

		RenameSubroutineWizard refactoringWizard = new RenameSubroutineWizard(refactoring);
		RefactoringWizardOpenOperation op = new RefactoringWizardOpenOperation(refactoringWizard);
		try
		{
			status = op.run(shell, "Rename Refactoring");
		}
		catch (InterruptedException e)
		{
			System.out.println(e);
		}
		return status;
	}

	@CanExecute
	public boolean canExecute()
	{
		return true;
	}

}