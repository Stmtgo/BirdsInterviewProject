package com.birds.logic.model;

/**
 * Represents a bird entity with its physical characteristics.
 * <p>
 * This class serves as a data model for storing and transferring bird information
 * between the UI layer and the backend API.
 * </p>
 * 
 * @author Birds RCP Project
 * @version 1.0
 * @since 1.0
 */
public class Bird {

	/** The unique identifier for the bird. */
	private Long id;
	
	/** The name of the bird species. */
	private String name;
	
	/** The primary color of the bird. */
	private String color;
	
	/** The weight of the bird in kilograms. */
	private double weight;
	
	/** The height of the bird in centimeters. */
	private double height;

	/**
	 * Default constructor for creating an empty Bird instance.
	 * Required for JSON deserialization.
	 */
	public Bird() {
	}

	/**
	 * Constructs a new Bird with the specified attributes.
	 * 
	 * @param id     the unique identifier for the bird, can be null for new birds
	 * @param name   the name of the bird species
	 * @param color  the primary color of the bird
	 * @param weight the weight of the bird in kilograms
	 * @param height the height of the bird in centimeters
	 */
	public Bird(Long id, String name, String color, double weight, double height) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.weight = weight;
		this.height = height;
	}

	/**
	 * Returns the unique identifier of the bird.
	 * 
	 * @return the bird's ID, or null if not yet persisted
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the unique identifier of the bird.
	 * 
	 * @param id the bird's ID to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Returns the name of the bird species.
	 * 
	 * @return the bird's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the bird species.
	 * 
	 * @param name the bird's name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the primary color of the bird.
	 * 
	 * @return the bird's color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * Sets the primary color of the bird.
	 * 
	 * @param color the bird's color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * Returns the weight of the bird.
	 * 
	 * @return the bird's weight in kilograms
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * Sets the weight of the bird.
	 * 
	 * @param weight the bird's weight in kilograms
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}

	/**
	 * Returns the height of the bird.
	 * 
	 * @return the bird's height in centimeters
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * Sets the height of the bird.
	 * 
	 * @param height the bird's height in centimeters
	 */
	public void setHeight(double height) {
		this.height = height;
	}

	/**
	 * Returns a string representation of the bird.
	 * 
	 * @return a formatted string containing the bird's name and color
	 */
	@Override
	public String toString() {
		return name + " (" + color + ")";
	}
}
