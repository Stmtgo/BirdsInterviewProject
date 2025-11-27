package com.birds.logic.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A generic wrapper class for paginated API responses.
 * <p>
 * This class encapsulates the standard pagination metadata returned by the backend API,
 * including the content list, page information, and navigation flags.
 * </p>
 * 
 * @param <T> the type of elements in the paginated response
 * 
 * @author Birds RCP Project
 * @version 1.0
 * @since 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PagedResponse<T> {

	/** The list of items in the current page. */
	private List<T> content;
	
	/** The total number of pages available. */
	private int totalPages;
	
	/** The total number of elements across all pages. */
	private long totalElements;
	
	/** The size of each page (number of elements per page). */
	private int size;
	
	/** The current page number (zero-based). */
	private int number;
	
	/** The actual number of elements in the current page. */
	private int numberOfElements;
	
	/** Flag indicating if this is the first page. */
	private boolean first;
	
	/** Flag indicating if this is the last page. */
	private boolean last;
	
	/** Flag indicating if the page has no content. */
	private boolean empty;

	/**
	 * Default constructor for creating an empty PagedResponse.
	 * Required for JSON deserialization.
	 */
	public PagedResponse() {
	}

	/**
	 * Returns the list of items in the current page.
	 * 
	 * @return the content list
	 */
	public List<T> getContent() {
		return content;
	}

	/**
	 * Sets the list of items in the current page.
	 * 
	 * @param content the content list to set
	 */
	public void setContent(List<T> content) {
		this.content = content;
	}

	/**
	 * Returns the total number of pages available.
	 * 
	 * @return the total page count
	 */
	public int getTotalPages() {
		return totalPages;
	}

	/**
	 * Sets the total number of pages.
	 * 
	 * @param totalPages the total page count to set
	 */
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	/**
	 * Returns the total number of elements across all pages.
	 * 
	 * @return the total element count
	 */
	public long getTotalElements() {
		return totalElements;
	}

	/**
	 * Sets the total number of elements.
	 * 
	 * @param totalElements the total element count to set
	 */
	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}

	/**
	 * Returns the size of each page.
	 * 
	 * @return the page size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Sets the size of each page.
	 * 
	 * @param size the page size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * Returns the current page number (zero-based).
	 * 
	 * @return the current page number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Sets the current page number.
	 * 
	 * @param number the page number to set
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * Returns the actual number of elements in the current page.
	 * 
	 * @return the number of elements in this page
	 */
	public int getNumberOfElements() {
		return numberOfElements;
	}

	/**
	 * Sets the number of elements in the current page.
	 * 
	 * @param numberOfElements the element count to set
	 */
	public void setNumberOfElements(int numberOfElements) {
		this.numberOfElements = numberOfElements;
	}

	/**
	 * Checks if this is the first page.
	 * 
	 * @return true if this is the first page, false otherwise
	 */
	public boolean isFirst() {
		return first;
	}

	/**
	 * Sets whether this is the first page.
	 * 
	 * @param first true if this is the first page
	 */
	public void setFirst(boolean first) {
		this.first = first;
	}

	/**
	 * Checks if this is the last page.
	 * 
	 * @return true if this is the last page, false otherwise
	 */
	public boolean isLast() {
		return last;
	}

	/**
	 * Sets whether this is the last page.
	 * 
	 * @param last true if this is the last page
	 */
	public void setLast(boolean last) {
		this.last = last;
	}

	/**
	 * Checks if the page has no content.
	 * 
	 * @return true if the page is empty, false otherwise
	 */
	public boolean isEmpty() {
		return empty;
	}

	/**
	 * Sets whether the page is empty.
	 * 
	 * @param empty true if the page has no content
	 */
	public void setEmpty(boolean empty) {
		this.empty = empty;
	}

}
