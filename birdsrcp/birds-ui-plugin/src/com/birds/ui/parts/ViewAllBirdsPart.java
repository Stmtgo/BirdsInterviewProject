package com.birds.ui.parts;

import java.util.List;

import jakarta.annotation.PostConstruct;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.birds.logic.model.Bird;
import com.birds.logic.service.bird.BirdService;
import com.birds.logic.service.bird.BirdServiceImpl;

/**
 * Eclipse E4 part for displaying all birds in a table view.
 * <p>
 * This part provides a read-only table displaying all birds in the system
 * with columns for name, color, weight, and height. The data is retrieved
 * from the backend API via the {@link BirdService}.
 * </p>
 * 
 * @author Birds RCP Project
 * @version 1.0
 * @since 1.0
 * @see Bird
 * @see BirdService
 */
public class ViewAllBirdsPart {

	/** Table viewer for displaying bird data. */
	private TableViewer tableViewer;
	
	/** Service for bird-related operations. */
	private BirdService birdService;

	/**
	 * Constructs a new ViewAllBirdsPart with an initialized service.
	 */
	public ViewAllBirdsPart() {
		this.birdService = new BirdServiceImpl();
	}

	/**
	 * Creates the composite UI with a table displaying all birds.
	 * <p>
	 * This method is called by the Eclipse framework after the part is constructed.
	 * It creates a table with columns for Name, Color, Weight, and Height,
	 * and populates it with data from the BirdService.
	 * </p>
	 * 
	 * @param parent the parent composite to build the UI in
	 */
	@PostConstruct
	public void createComposite(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		tableViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Create columns
		createColumns();

		tableViewer.setContentProvider(ArrayContentProvider.getInstance());

		// Load and display birds
		loadBirds();
	}

	/**
	 * Creates the table columns for displaying bird information.
	 * <p>
	 * Creates columns for Name, Color, Weight, and Height with
	 * appropriate widths and label providers.
	 * </p>
	 */
	private void createColumns() {
		// Name column
		TableViewerColumn nameColumn = createTableViewerColumn("Name", 150);
		nameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Bird bird = (Bird) element;
				return bird.getName();
			}
		});

		// Color column
		TableViewerColumn colorColumn = createTableViewerColumn("Color", 100);
		colorColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Bird bird = (Bird) element;
				return bird.getColor();
			}
		});

		// Weight column
		TableViewerColumn weightColumn = createTableViewerColumn("Weight", 100);
		weightColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Bird bird = (Bird) element;
				return String.format("%.2f", bird.getWeight());
			}
		});

		// Height column
		TableViewerColumn heightColumn = createTableViewerColumn("Height", 100);
		heightColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Bird bird = (Bird) element;
				return String.format("%.2f", bird.getHeight());
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
	 * Loads all birds from the service and displays them in the table.
	 * <p>
	 * Retrieves birds from the BirdService and sets them as input
	 * to the table viewer. Errors are logged to the console.
	 * </p>
	 */
	private void loadBirds() {
		try {
			List<Bird> birds = birdService.getAllBirds();
			tableViewer.setInput(birds);
			tableViewer.refresh();
		} catch (Exception e) {
			// Handle error - could show error dialog
			e.printStackTrace();
		}
	}

	/**
	 * Refreshes the bird data when this part receives focus.
	 * <p>
	 * This ensures that the latest data is displayed, including
	 * any birds added since the view was last focused.
	 * </p>
	 */
	@Focus
	public void onFocus() {
		// Refresh data each time this part is accessed
		loadBirds();
		tableViewer.getTable().setFocus();
	}
}
