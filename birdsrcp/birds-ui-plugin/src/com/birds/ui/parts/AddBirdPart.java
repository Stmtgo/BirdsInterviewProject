package com.birds.ui.parts;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import com.birds.logic.model.Bird;
import com.birds.logic.service.bird.BirdService;
import com.birds.logic.service.bird.BirdServiceImpl;

import jakarta.annotation.PostConstruct;

/**
 * Eclipse E4 part for adding new birds to the system.
 * <p>
 * This part provides a form-based UI for entering bird details including
 * name, color, weight, and height. The entered data is validated and
 * submitted to the backend API via the {@link BirdService}.
 * </p>
 * 
 * @author Birds RCP Project
 * @version 1.0
 * @since 1.0
 * @see Bird
 * @see BirdService
 */
public class AddBirdPart {

	/** Text field for entering the bird's name. */
	private Text nameText;
	
	/** Text field for entering the bird's color. */
	private Text colorText;
	
	/** Text field for entering the bird's weight. */
	private Text weightText;
	
	/** Text field for entering the bird's height. */
	private Text heightText;
	
	/** Service for bird-related operations. */
	private BirdService birdService;

	/**
	 * Creates the composite UI for the Add Bird form.
	 * <p>
	 * This method is called by the Eclipse framework after the part is constructed.
	 * It creates input fields for bird name, color, weight, and height,
	 * along with a save button.
	 * </p>
	 * 
	 * @param parent the parent composite to build the UI in
	 */
	@PostConstruct
	public void createComposite(Composite parent) {
		parent.setLayout(new GridLayout(2, false));
		
		// Create service instance
		birdService = new BirdServiceImpl();

		// Bird Name
		Label nameLabel = new Label(parent, SWT.NONE);
		nameLabel.setText("Bird Name:");
		nameText = new Text(parent, SWT.BORDER);
		nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Color
		Label colorLabel = new Label(parent, SWT.NONE);
		colorLabel.setText("Color:");
		colorText = new Text(parent, SWT.BORDER);
		colorText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Weight
		Label weightLabel = new Label(parent, SWT.NONE);
		weightLabel.setText("Weight (kg):");
		weightText = new Text(parent, SWT.BORDER);
		weightText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Height
		Label heightLabel = new Label(parent, SWT.NONE);
		heightLabel.setText("Height (cm):");
		heightText = new Text(parent, SWT.BORDER);
		heightText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Save Button
		Button saveButton = new Button(parent, SWT.PUSH);
		saveButton.setText("Save");
		saveButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		saveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				saveBird(parent);
			}
		});
	}

	/**
	 * Validates and saves the bird data entered in the form.
	 * <p>
	 * Validates that all fields are filled and that weight and height
	 * are valid numbers. If validation passes, creates a Bird object
	 * and saves it using the BirdService. Displays success or error
	 * messages accordingly.
	 * </p>
	 * 
	 * @param parent the parent composite for displaying messages
	 */
	private void saveBird(Composite parent) {
		try {
			// Get values from text fields
			String name = nameText.getText().trim();
			String color = colorText.getText().trim();
			String weightStr = weightText.getText().trim();
			String heightStr = heightText.getText().trim();

			// Validate inputs
			if (name.isEmpty() || color.isEmpty() || weightStr.isEmpty() || heightStr.isEmpty()) {
				showMessage(parent, "Error", "All fields are required!", SWT.ICON_ERROR);
				return;
			}

			// Parse numeric values
			double weight = Double.parseDouble(weightStr);
			double height = Double.parseDouble(heightStr);

			// Create Bird object
			Bird bird = new Bird(null, name, color, weight, height);
			
			// Save using BirdService
			birdService.addBird(bird);

			// Show success message
			showMessage(parent, "Success", "Bird saved successfully!", SWT.ICON_INFORMATION);
			
			// Clear the form
			clearFields();

		} catch (NumberFormatException ex) {
			showMessage(parent, "Error", "Weight and Height must be valid numbers!", SWT.ICON_ERROR);
		} catch (Exception ex) {
			showMessage(parent, "Error", "Failed to save bird: " + ex.getMessage(), SWT.ICON_ERROR);
		}
	}

	/**
	 * Clears all input fields in the form.
	 */
	private void clearFields() {
		nameText.setText("");
		colorText.setText("");
		weightText.setText("");
		heightText.setText("");
	}

	/**
	 * Displays a message dialog to the user.
	 * 
	 * @param parent  the parent composite for the dialog
	 * @param title   the dialog title
	 * @param message the message to display
	 * @param icon    the SWT icon constant (e.g., SWT.ICON_INFORMATION, SWT.ICON_ERROR)
	 */
	private void showMessage(Composite parent, String title, String message, int icon) {
		MessageBox messageBox = new MessageBox(parent.getShell(), icon | SWT.OK);
		messageBox.setText(title);
		messageBox.setMessage(message);
		messageBox.open();
	}

	/**
	 * Sets focus to the name text field when this part receives focus.
	 */
	@Focus
	public void setFocus() {
		nameText.setFocus();
	}
}
