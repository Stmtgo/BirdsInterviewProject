package com.birds.logic.service.bird;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.birds.logic.model.Bird;
import com.birds.logic.model.BirdPagedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementation of the {@link BirdService} interface.
 * <p>
 * Provides REST API-based operations for managing bird entities.
 * Communicates with a backend server running at localhost:8080.
 * </p>
 * 
 * @author Birds RCP Project
 * @version 1.0
 * @since 1.0
 * @see BirdService
 * @see Bird
 */
public class BirdServiceImpl implements BirdService {

	/** The base URL for the birds REST API endpoint. */
	private static final String API_URL = "http://localhost:8080/api/birds";
	
	/** Jackson ObjectMapper for JSON serialization/deserialization. */
	private final ObjectMapper objectMapper;

	/**
	 * Constructs a new BirdServiceImpl with a default ObjectMapper.
	 */
	public BirdServiceImpl() {
		this.objectMapper = new ObjectMapper();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Sends a POST request to the birds API endpoint with the bird data
	 * serialized as JSON.
	 * </p>
	 * 
	 * @param bird the bird entity to add
	 * @throws RuntimeException if the HTTP request fails or returns an error status
	 */
	@Override
	public void addBird(Bird bird) {
		try {
			URL url = new URL(API_URL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			String json = objectMapper.writeValueAsString(bird);

			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = json.getBytes(StandardCharsets.UTF_8);
				os.write(input, 0, input.length);
			}

			int responseCode = conn.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED) {
				throw new RuntimeException("Failed to add bird. HTTP response code: " + responseCode);
			}

		} catch (Exception e) {
			throw new RuntimeException("Error adding bird: " + e.getMessage(), e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Sends a GET request to the birds API endpoint to retrieve the first page
	 * of birds, with 10 items per page, sorted by name in descending order.
	 * </p>
	 * 
	 * @return a list of birds from the first page
	 * @throws RuntimeException if the HTTP request fails or returns an error status
	 */
	@Override
	public List<Bird> getAllBirds() {
		try {
			URL url = new URL(API_URL + "?page=0&size=10&sort=name,desc");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			int responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				try (BufferedReader reader = new BufferedReader(
						new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					BirdPagedResponse pagedResponse = objectMapper.readValue(response.toString(), BirdPagedResponse.class);
					return pagedResponse.getContent() != null ? pagedResponse.getContent() : new ArrayList<>();
				}
			} else {
				throw new RuntimeException("Failed to get birds. HTTP response code: " + responseCode);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error getting birds: " + e.getMessage(), e);
		}
	}

}
