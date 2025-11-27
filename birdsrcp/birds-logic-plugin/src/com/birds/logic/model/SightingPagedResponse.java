package com.birds.logic.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A specialized paginated response wrapper for {@link Sighting} entities.
 * <p>
 * This class extends {@link PagedResponse} to provide type-safe handling
 * of paginated sighting data returned from the backend API.
 * </p>
 * 
 * @author Birds RCP Project
 * @version 1.0
 * @since 1.0
 * @see PagedResponse
 * @see Sighting
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SightingPagedResponse extends PagedResponse<Sighting> {

	/**
	 * Default constructor for creating an empty SightingPagedResponse.
	 * Required for JSON deserialization.
	 */
	public SightingPagedResponse() {
	}

}
