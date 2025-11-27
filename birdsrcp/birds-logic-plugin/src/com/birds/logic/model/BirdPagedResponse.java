package com.birds.logic.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A specialized paginated response wrapper for {@link Bird} entities.
 * <p>
 * This class extends {@link PagedResponse} to provide type-safe handling
 * of paginated bird data returned from the backend API.
 * </p>
 * 
 * @author Birds RCP Project
 * @version 1.0
 * @since 1.0
 * @see PagedResponse
 * @see Bird
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BirdPagedResponse extends PagedResponse<Bird> {

	/**
	 * Default constructor for creating an empty BirdPagedResponse.
	 * Required for JSON deserialization.
	 */
	public BirdPagedResponse() {
	}

}
