package com.birds.rcp.handlers;

import java.util.List;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.jface.dialogs.PlainMessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Handler for the application quit command.
 * <p>
 * This handler is invoked when the user requests to close the application.
 * It displays a confirmation dialog before actually closing the workbench.
 * </p>
 * 
 * @author Birds RCP Project
 * @version 1.0
 * @since 1.0
 */
public class QuitHandler {
	
	/**
	 * Executes the quit handler logic.
	 * <p>
	 * Displays a confirmation dialog asking the user if they want to exit.
	 * If the user confirms, the workbench is closed; otherwise, no action is taken.
	 * </p>
	 * 
	 * @param workbench the Eclipse workbench instance to close
	 * @param shell     the parent shell for the confirmation dialog
	 */
	@Execute
	public void execute(IWorkbench workbench, Shell shell) {

		PlainMessageDialog dialog = PlainMessageDialog.getBuilder(shell, "Confirmation")
				.message("Do you want to exit?").buttonLabels(List.of("Exit", "Cancel")).build();

		if (dialog.open() == 0) {
			workbench.close();
		}
	}
}
