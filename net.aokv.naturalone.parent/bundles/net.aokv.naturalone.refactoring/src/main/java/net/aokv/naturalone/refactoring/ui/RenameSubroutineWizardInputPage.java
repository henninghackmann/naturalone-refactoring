package net.aokv.naturalone.refactoring.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import net.aokv.naturalone.refactoring.process.RenameSubroutineRefactoring;

public class RenameSubroutineWizardInputPage extends UserInputWizardPage
{
	private RenameSubroutineRefactoring refactoring;
	private boolean initialized = false;
	private Text subroutineName;

	public RenameSubroutineWizardInputPage(RenameSubroutineWizard renameSubroutineWizard)
	{
		super("Rename Subroutine Input Page");
		refactoring = (RenameSubroutineRefactoring) renameSubroutineWizard.getRefactoring();
	}

	@Override
	public void createControl(Composite parent)
	{
		initializeDialogUnits(parent);
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 8;
		composite.setLayout(gridLayout);
	}

	@Override
	public void setVisible(boolean visible)
	{
		if (visible && !initialized)
		{
			initialized = true;
			Composite composite = (Composite) getControl();
			addOptionGroup(composite);
			Dialog.applyDialogFont(composite);
		}
		super.setVisible(visible);
	}

	private void addOptionGroup(Composite composite)
	{
		Group group = new Group(composite, SWT.NONE);
		group.setText("Subroutine umbenennen:");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridData.verticalIndent = 8;
		group.setLayoutData(gridData);
		GridLayout gridLayout2 = new GridLayout(1, false);
		gridLayout2.verticalSpacing = 8;
		gridLayout2.marginTop = 3;
		group.setLayout(gridLayout2);

		Label label = new Label(group, SWT.NONE);
		label.setText("&Neuer Subroutinename:");

		subroutineName = new Text(group, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		subroutineName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		subroutineName.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent event)
			{
				methodeNameChanged();
			}
		});

		final Button updateRefences = new Button(group, SWT.CHECK);
		updateRefences.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false));
		updateRefences.setText("Referenzen anpassen?");
		updateRefences.setSelection(false);
		updateRefences.setEnabled(false);
		updateRefences.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				refactoring.setUpdateReferences(updateRefences.getSelection());
			}
		});

		refactoring.setUpdateReferences(false);
		subroutineName.setFocus();
		subroutineName.selectAll();
		methodeNameChanged();
	}

	void methodeNameChanged()
	{
		RefactoringStatus status = new RefactoringStatus();

		setPageComplete(!status.hasError());
		int severity = status.getSeverity();
		String message = status.getMessageMatchingSeverity(severity);
		if (severity >= RefactoringStatus.INFO)
		{
			setMessage(message, severity);
		}
		else
		{
			setMessage("", NONE);
		}
	}
}
