package com.birds.ui.parts;

import java.util.List;

import jakarta.annotation.PostConstruct;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.birds.logic.model.Bird;
import com.birds.logic.model.Sighting;
import com.birds.logic.service.sighting.SightingService;
import com.birds.logic.service.sighting.SightingServiceImpl;

/**
 * Eclipse E4 part for viewing and searching bird sightings.
 * <p>
 * This part provides a searchable table view of all bird sightings in the system.
 * Users can filter sightings by bird name, location, and date/time range.
 * The data is retrieved from the backend API via the {@link SightingService}.
 * </p>
 * 
 * @author Birds RCP Project
 * @version 1.0
 * @since 1.0
 * @see Sighting
 * @see SightingService
 */
public class ViewSightingsPart {

	/** Service for sighting-related operations. */
	private SightingService sightingService;
	
	/** Table viewer for displaying sighting data. */
	private TableViewer tableViewer;
	
	/** Text field for filtering by bird name. */
	private Text birdNameText;
	
	/** Text field for filtering by location. */
	private Text locationText;
	
	/** Date picker for the "from" date filter. */
	private DateTime fromDatePicker;
	
	/** Time picker for the "from" time filter. */
	private DateTime fromTimePicker;
	
	/** Date picker for the "to" date filter. */
	private DateTime toDatePicker;
	
	/** Time picker for the "to" time filter. */
	private DateTime toTimePicker;

	/**
	 * Constructs a new ViewSightingsPart with an initialized service.
	 */
	public ViewSightingsPart() {
		this.sightingService = new SightingServiceImpl();
	}

	/**
	 * Creates the composite UI with search filters and sightings table.
	 * <p>
	 * This method is called by the Eclipse framework after the part is constructed.
	 * It creates a search area with filter fields for bird name, location, and
	 * date/time range, along with a table displaying the sighting results.
	 * </p>
	 * 
	 * @param parent the parent composite to build the UI in
	 */
	@PostConstruct
	public void createComposite(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		// Search area
		Composite searchComposite = new Composite(parent, SWT.NONE);
		searchComposite.setLayout(new GridLayout(4, false));
		searchComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Bird Name field
		Label birdNameLabel = new Label(searchComposite, SWT.NONE);
		birdNameLabel.setText("Bird Name:");

		birdNameText = new Text(searchComposite, SWT.BORDER);
		birdNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		birdNameText.setMessage("e.g., Blue Jay");

		// Location field
		Label locationLabel = new Label(searchComposite, SWT.NONE);
		locationLabel.setText("Location:");

		locationText = new Text(searchComposite, SWT.BORDER);
		locationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		locationText.setMessage("e.g., Central Park");

		// From Date/Time
		Label fromDateLabel = new Label(searchComposite, SWT.NONE);
		fromDateLabel.setText("From Date:");

		Composite fromDateTimeComposite = new Composite(searchComposite, SWT.NONE);
		fromDateTimeComposite.setLayout(new GridLayout(2, false));
		fromDateTimeComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		fromDatePicker = new DateTime(fromDateTimeComposite, SWT.DATE | SWT.DROP_DOWN);
		fromTimePicker = new DateTime(fromDateTimeComposite, SWT.TIME);

		// To Date/Time
		Label toDateLabel = new Label(searchComposite, SWT.NONE);
		toDateLabel.setText("To Date:");

		Composite toDateTimeComposite = new Composite(searchComposite, SWT.NONE);
		toDateTimeComposite.setLayout(new GridLayout(2, false));
		toDateTimeComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		toDatePicker = new DateTime(toDateTimeComposite, SWT.DATE | SWT.DROP_DOWN);
		toTimePicker = new DateTime(toDateTimeComposite, SWT.TIME);

		// Search button - spans full row
		Label spacer = new Label(searchComposite, SWT.NONE);
		spacer.setText("");

		Composite buttonComposite = new Composite(searchComposite, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(3, false));
		GridData buttonGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		buttonGridData.horizontalSpan = 3;
		buttonComposite.setLayoutData(buttonGridData);

		Button searchButton = new Button(buttonComposite, SWT.PUSH);
		searchButton.setText("Search");
		searchButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				performSearch();
			}
		});

		Button clearButton = new Button(buttonComposite, SWT.PUSH);
		clearButton.setText("Clear");
		clearButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearSearch();
			}
		});

		Button viewAllButton = new Button(buttonComposite, SWT.PUSH);
		viewAllButton.setText("View All");
		viewAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadAllSightings();
			}
		});

		// Table
		tableViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createColumns();

		tableViewer.setContentProvider(ArrayContentProvider.getInstance());

		// Load initial data
		loadAllSightings();
	}

	/**
	 * Creates the table columns for displaying sighting information.
	 * <p>
	 * Creates columns for Bird Name, Bird Color, Location, and Date/Time
	 * with appropriate widths and label providers.
	 * </p>
	 */
	private void createColumns() {
		// Bird Name column
		TableViewerColumn birdNameColumn = createTableViewerColumn("Bird Name", 150);
		birdNameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Sighting sighting = (Sighting) element;
				Bird bird = sighting.getBird();
				return bird != null ? bird.getName() : "Unknown";
			}
		});

		// Bird Color column
		TableViewerColumn birdColorColumn = createTableViewerColumn("Bird Color", 100);
		birdColorColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Sighting sighting = (Sighting) element;
				Bird bird = sighting.getBird();
				return bird != null ? bird.getColor() : "";
			}
		});

		// Location column
		TableViewerColumn locationColumn = createTableViewerColumn("Location", 150);
		locationColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Sighting sighting = (Sighting) element;
				return sighting.getLocation();
			}
		});

		// DateTime column
		TableViewerColumn dateTimeColumn = createTableViewerColumn("Date/Time", 150);
		dateTimeColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Sighting sighting = (Sighting) element;
				return sighting.getDateTime();
			}
		});
	}

	/**
	 * Creates a table viewer column with the specified title and width.
	 * 
	 * @param title the column header title
	 * @param width the column width in pixels
	 * @return the created TableViewerColumn
	 */
	private TableViewerColumn createTableViewerColumn(String title, int width) {
		TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(width);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	/**
	 * Performs a search with the current filter criteria.
	 * <p>
	 * Builds search parameters from the bird name, location, and date/time
	 * filter fields and calls the SightingService to retrieve matching sightings.
	 * </p>
	 */
	private void performSearch() {
		try {
			String birdName = birdNameText.getText().trim();
			String location = locationText.getText().trim();
			
			// Build from date/time string
			String fromDate = String.format("%04d-%02d-%02dT%02d:%02d:%02d",
					fromDatePicker.getYear(),
					fromDatePicker.getMonth() + 1,
					fromDatePicker.getDay(),
					fromTimePicker.getHours(),
					fromTimePicker.getMinutes(),
					fromTimePicker.getSeconds());
			
			// Build to date/time string
			String toDate = String.format("%04d-%02d-%02dT%02d:%02d:%02d",
					toDatePicker.getYear(),
					toDatePicker.getMonth() + 1,
					toDatePicker.getDay(),
					toTimePicker.getHours(),
					toTimePicker.getMinutes(),
					toTimePicker.getSeconds());
			
			
			List<Sighting> sightings = sightingService.searchSightings(birdName, location, fromDate, toDate);
			tableViewer.setInput(sightings);
			tableViewer.refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Clears all search filter fields and loads all sightings.
	 */
	private void clearSearch() {
		birdNameText.setText("");
		locationText.setText("");
		loadAllSightings();
	}

	/**
	 * Loads all sightings from the service without any filters.
	 * <p>
	 * Retrieves sightings from the SightingService and displays them
	 * in the table viewer. Errors are logged to the console.
	 * </p>
	 */
	private void loadAllSightings() {
		try {
			List<Sighting> sightings = sightingService.getAllSightings();
			tableViewer.setInput(sightings);
			tableViewer.refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Refreshes the sighting data when this part receives focus.
	 * <p>
	 * This ensures that the latest data is displayed, including
	 * any sightings added since the view was last focused.
	 * </p>
	 */
	@Focus
	public void onFocus() {
		loadAllSightings();
		tableViewer.getTable().setFocus();
	}
}
