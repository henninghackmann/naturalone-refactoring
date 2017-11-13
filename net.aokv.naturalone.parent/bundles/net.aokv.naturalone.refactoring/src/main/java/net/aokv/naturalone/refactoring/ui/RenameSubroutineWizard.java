package net.aokv.naturalone.refactoring.ui;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import net.aokv.naturalone.refactoring.process.RenameSubroutineRefactoring;

public class RenameSubroutineWizard extends RefactoringWizard
{
	public RenameSubroutineWizard(RenameSubroutineRefactoring refactoringContext)
	{
		super(refactoringContext, DIALOG_BASED_USER_INTERFACE |
				CHECK_INITIAL_CONDITIONS_ON_OPEN |
				PREVIEW_EXPAND_FIRST_NODE |
				NO_BACK_BUTTON_ON_STATUS_DIALOG);
		setDefaultPageTitle("Rename Subroutine");
	}

	@Override
	protected void addUserInputPages()
	{
		addPage(new RenameSubroutineWizardInputPage(this));
	}
}
