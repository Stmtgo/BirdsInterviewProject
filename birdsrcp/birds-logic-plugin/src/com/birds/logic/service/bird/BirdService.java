package com.birds.logic.service.bird;

import java.util.List;
import com.birds.logic.model.Bird;

/**
 * Service interface for managing bird entities.
 * <p>
 * Defines the contract for bird-related operations including
 * adding new birds and retrieving existing birds from the system.
 * </p>
 * 
 * @author Birds RCP Project
 * @version 1.0
 * @since 1.0
 * @see Bird
 * @see BirdServiceImpl
 */
public interface BirdService {

	/**
	 * Adds a new bird to the system.
	 * <p>
	 * The bird will be persisted via the backend REST API.
	 * </p>
	 * 
	 * @param bird the bird entity to add; must not be null
	 * @throws RuntimeException if the bird cannot be added due to API errors
	 */
	void addBird(Bird bird);

	/**
	 * Retrieves all birds from the system.
	 * <p>
	 * Returns the first page of birds from the backend API,
	 * sorted by name in descending order.
	 * </p>
	 * 
	 * @return a list of all birds, or an empty list if none exist
	 * @throws RuntimeException if the birds cannot be retrieved due to API errors
	 */
	List<Bird> getAllBirds();

}
