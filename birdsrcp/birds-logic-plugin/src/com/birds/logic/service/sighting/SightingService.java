package com.birds.logic.service.sighting;

import java.util.List;

import com.birds.logic.model.Sighting;

/**
 * Service interface for managing bird sighting records.
 * <p>
 * Defines the contract for sighting-related operations including
 * adding new sightings, retrieving all sightings, and searching
 * sightings with various filter criteria.
 * </p>
 * 
 * @author Birds RCP Project
 * @version 1.0
 * @since 1.0
 * @see Sighting
 * @see SightingServiceImpl
 */
public interface SightingService {
	
	/**
	 * Adds a new sighting record to the system.
	 * <p>
	 * The sighting will be persisted via the backend REST API.
	 * </p>
	 * 
	 * @param sighting the sighting record to add; must not be null
	 * @throws RuntimeException if the sighting cannot be added due to API errors
	 */
	void addSighting(Sighting sighting);
	
	/**
	 * Retrieves all sighting records from the system.
	 * <p>
	 * Returns the first page of sightings from the backend API,
	 * sorted by ID in ascending order.
	 * </p>
	 * 
	 * @return a list of all sightings, or an empty list if none exist
	 * @throws RuntimeException if the sightings cannot be retrieved due to API errors
	 */
	List<Sighting> getAllSightings();
	
	/**
	 * Searches for sighting records matching the specified criteria.
	 * <p>
	 * All parameters are optional and can be null or empty. When provided,
	 * they are used to filter the results. Multiple criteria are combined
	 * using AND logic.
	 * </p>
	 * 
	 * @param birdName the name of the bird to search for (partial match)
	 * @param location the location to search for (partial match)
	 * @param fromDate the start date/time filter in ISO-8601 format
	 * @param toDate   the end date/time filter in ISO-8601 format
	 * @return a list of sightings matching the criteria, or an empty list if none match
	 * @throws RuntimeException if the search fails due to API errors
	 */
	List<Sighting> searchSightings(String birdName, String location, String fromDate, String toDate);

}
