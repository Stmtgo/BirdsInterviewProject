package com.birds.logic.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a bird sighting record containing location and time information.
 * <p>
 * A sighting captures when and where a specific bird was observed. It maintains
 * a reference to the observed {@link Bird} entity through the bird ID.
 * </p>
 * 
 * @author Birds RCP Project
 * @version 1.0
 * @since 1.0
 * @see Bird
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Sighting {
	
	/** The unique identifier for the sighting. */
	private Long id;
	
	/** The ID of the bird that was sighted. */
	private Long birdId;
	
	/** The location where the bird was sighted. */
	private String location;
	
	/** The date and time of the sighting in ISO-8601 format. */
	private String dateTime;
	
	/** The bird entity associated with this sighting. */
	private Bird bird;

	/**
	 * Default constructor for creating an empty Sighting instance.
	 * Required for JSON deserialization.
	 */
	public Sighting() {
	}

	/**
	 * Constructs a new Sighting with the specified attributes.
	 * 
	 * @param birdId   the ID of the bird that was sighted
	 * @param location the location where the bird was observed
	 * @param dateTime the date and time of the sighting in ISO-8601 format
	 * @param bird     the bird entity associated with this sighting
	 */
	public Sighting(Long birdId, String location, String dateTime, Bird bird) {
		this.birdId = birdId;
		this.location = location;
		this.dateTime = dateTime;
		this.bird = bird;
	}

	/**
	 * Returns the unique identifier of the sighting.
	 * 
	 * @return the sighting's ID, or null if not yet persisted
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the unique identifier of the sighting.
	 * 
	 * @param id the sighting's ID to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns the ID of the bird that was sighted.
	 * 
	 * @return the bird's ID
	 */
	public Long getBirdId() {
		return birdId;
	}

	/**
	 * Sets the ID of the bird that was sighted.
	 * 
	 * @param birdId the bird's ID to set
	 */
	public void setBirdId(Long birdId) {
		this.birdId = birdId;
	}

	/**
	 * Returns the location where the bird was sighted.
	 * 
	 * @return the sighting location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Sets the location where the bird was sighted.
	 * 
	 * @param location the sighting location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Returns the date and time of the sighting.
	 * 
	 * @return the date and time in ISO-8601 format
	 */
	public String getDateTime() {
		return dateTime;
	}

	/**
	 * Sets the date and time of the sighting.
	 * 
	 * @param dateTime the date and time in ISO-8601 format
	 */
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	
	/**
	 * Returns the bird entity associated with this sighting.
	 * 
	 * @return the bird that was sighted
	 */
	public Bird getBird() {
		return bird;
	}
	
	/**
	 * Sets the bird entity associated with this sighting.
	 * 
	 * @param bird the bird to associate with this sighting
	 */
	public void setBird(Bird bird) {
		this.bird = bird;
	}

}
