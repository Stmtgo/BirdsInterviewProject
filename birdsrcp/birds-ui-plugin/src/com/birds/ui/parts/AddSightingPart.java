package com.birds.ui.parts;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import jakarta.annotation.PostConstruct;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import com.birds.logic.model.Bird;
import com.birds.logic.model.Sighting;
import com.birds.logic.service.bird.BirdService;
import com.birds.logic.service.bird.BirdServiceImpl;
import com.birds.logic.service.sighting.SightingService;
import com.birds.logic.service.sighting.SightingServiceImpl;

/**
 * Eclipse E4 part for adding new bird sightings to the system.
 * <p>
 * This part provides a form-based UI for recording bird sightings, including
 * selecting a bird from a dropdown, entering the location, and specifying
 * the date and time of the sighting. The sighting is submitted to the
 * backend API via the {@link SightingService}.
 * </p>
 * 
 * @author Birds RCP Project
 * @version 1.0
 * @since 1.0
 * @see Sighting
 * @see SightingService
 * @see BirdService
 */
public class AddSightingPart {

	/** Service for bird-related operations. */
	private BirdService birdService;
	
	/** Service for sighting-related operations. */
	private SightingService sightingService;
	
	/** Combo viewer for selecting a bird. */
	private ComboViewer birdComboViewer;
	
	/** Text field for entering the sighting location. */
	private Text locationText;
	
	/** Date picker widget for the sighting date. */
	private DateTime datePicker;
	
	/** Time picker widget for the sighting time. */
	private DateTime timePicker;

	/**
	 * Constructs a new AddSightingPart with initialized services.
	 */
	public AddSightingPart() {
		this.birdService = new BirdServiceImpl();
		this.sightingService = new SightingServiceImpl();
	}

	/**
	 * Creates the composite UI for the Add Sighting form.
	 * <p>
	 * This method is called by the Eclipse framework after the part is constructed.
	 * It creates a bird selection dropdown, location text field, and date/time pickers,
	 * along with an add sighting button.
	 * </p>
	 * 
	 * @param parent the parent composite to build the UI in
	 */
	@PostConstruct
	public void createComposite(Composite parent) {
		parent.setLayout(new GridLayout(2, false));

		// Bird dropdown
		Label birdLabel = new Label(parent, SWT.NONE);
		birdLabel.setText("Bird:");
		birdLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

		birdComboViewer = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		birdComboViewer.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		birdComboViewer.setContentProvider(ArrayContentProvider.getInstance());
		birdComboViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				Bird bird = (Bird) element;
				return bird.getName() + " (" + bird.getColor() + ")";
			}
		});

		// Location field
		Label locationLabel = new Label(parent, SWT.NONE);
		locationLabel.setText("Location:");
		locationLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

		locationText = new Text(parent, SWT.BORDER);
		locationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Date picker
		Label dateLabel = new Label(parent, SWT.NONE);
		dateLabel.setText("Date:");
		dateLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

		datePicker = new DateTime(parent, SWT.DATE | SWT.DROP_DOWN);
		datePicker.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Time picker
		Label timeLabel = new Label(parent, SWT.NONE);
		timeLabel.setText("Time:");
		timeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

		timePicker = new DateTime(parent, SWT.TIME | SWT.SHORT);
		timePicker.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Spacer
		new Label(parent, SWT.NONE);

		// Add Sighting button
		Button addButton = new Button(parent, SWT.PUSH);
		addButton.setText("Add Sighting");
		addButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addSighting(parent);
			}
		});

		// Load birds into dropdown
		loadBirds();
	}

	/**
	 * Loads all available birds into the bird selection dropdown.
	 * <p>
	 * Retrieves birds from the BirdService and populates the combo viewer.
	 * Automatically selects the first bird if any are available.
	 * </p>
	 */
	private void loadBirds() {
		try {
			List<Bird> birds = birdService.getAllBirds();
			birdComboViewer.setInput(birds);
			if (!birds.isEmpty()) {
				birdComboViewer.setSelection(new StructuredSelection(birds.get(0)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Validates the form and adds a new sighting.
	 * <p>
	 * Validates that a bird is selected and a location is entered.
	 * Creates a Sighting object with the current date/time values and
	 * submits it via the SightingService.
	 * </p>
	 * 
	 * @param parent the parent composite for displaying messages
	 */
	private void addSighting(Composite parent) {
		// Get selected bird
		StructuredSelection selection = (StructuredSelection) birdComboViewer.getSelection();
		if (selection.isEmpty()) {
			showMessage(parent, "Error", "Please select a bird.");
			return;
		}
		Bird selectedBird = (Bird) selection.getFirstElement();

		// Get location
		String location = locationText.getText().trim();
		if (location.isEmpty()) {
			showMessage(parent, "Error", "Please enter a location.");
			return;
		}

		// Get date and time
		LocalDateTime dateTime = LocalDateTime.of(
			datePicker.getYear(),
			datePicker.getMonth() + 1, // SWT months are 0-based
			datePicker.getDay(),
			timePicker.getHours(),
			timePicker.getMinutes(),
			timePicker.getSeconds()
		);
		String dateTimeStr = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

		// Create sighting and save
		Sighting sighting = new Sighting(selectedBird.getId(), location, dateTimeStr, selectedBird);

		try {
			sightingService.addSighting(sighting);
			showMessage(parent, "Success", "Sighting added successfully!");
			
			// Clear form
			locationText.setText("");
		} catch (Exception e) {
			showMessage(parent, "Error", "Failed to add sighting: " + e.getMessage());
		}
	}

	/**
	 * Displays a message dialog to the user.
	 * 
	 * @param parent  the parent composite for the dialog
	 * @param title   the dialog title
	 * @param message the message to display
	 */
	private void showMessage(Composite parent, String title, String message) {
		MessageBox messageBox = new MessageBox(parent.getShell(), SWT.ICON_INFORMATION | SWT.OK);
		messageBox.setText(title);
		messageBox.setMessage(message);
		messageBox.open();
	}
	
	/**
	 * Refreshes the bird list when this part receives focus.
	 * <p>
	 * This ensures that newly added birds are available in the dropdown.
	 * </p>
	 */
	@Focus
	public void onFocus() {
		loadBirds();
	}
}
