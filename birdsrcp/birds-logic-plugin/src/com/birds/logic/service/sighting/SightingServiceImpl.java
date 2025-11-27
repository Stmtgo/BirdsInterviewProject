package com.birds.logic.service.sighting;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.birds.logic.model.Sighting;
import com.birds.logic.model.SightingPagedResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementation of the {@link SightingService} interface.
 * <p>
 * Provides REST API-based operations for managing bird sighting records.
 * Communicates with a backend server running at localhost:8080.
 * </p>
 * 
 * @author Birds RCP Project
 * @version 1.0
 * @since 1.0
 * @see SightingService
 * @see Sighting
 */
public class SightingServiceImpl implements SightingService {

	/** The base URL for the sightings REST API endpoint. */
	private static final String API_URL = "http://localhost:8080/api/sightings";
	
	/** Jackson ObjectMapper for JSON serialization/deserialization. */
	private final ObjectMapper objectMapper;

	/**
	 * Constructs a new SightingServiceImpl with a default ObjectMapper.
	 */
	public SightingServiceImpl() {
		this.objectMapper = new ObjectMapper();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Sends a POST request to the sightings API endpoint with the sighting data
	 * serialized as JSON.
	 * </p>
	 * 
	 * @param sighting the sighting record to add
	 * @throws RuntimeException if the HTTP request fails or returns an error status
	 */
	@Override
	public void addSighting(Sighting sighting) {
		try {
			URL url = new URL(API_URL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			String json = objectMapper.writeValueAsString(sighting);

			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = json.getBytes(StandardCharsets.UTF_8);
				os.write(input, 0, input.length);
			}

			int responseCode = conn.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED) {
				throw new RuntimeException("Failed to add sighting. HTTP response code: " + responseCode);
			}

		} catch (Exception e) {
			throw new RuntimeException("Error adding sighting: " + e.getMessage(), e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Retrieves the first page of sightings with 10 items per page,
	 * sorted by ID in ascending order.
	 * </p>
	 */
	@Override
	public List<Sighting> getAllSightings() {
		return fetchSightings(API_URL + "?page=0&size=10&sort=id,asc");
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Builds a search URL with the provided filter criteria and sends
	 * a GET request to the sightings search API endpoint.
	 * </p>
	 */
	@Override
	public List<Sighting> searchSightings(String birdName, String location, String fromDate, String toDate) {
		try {
			StringBuilder searchUrl = new StringBuilder(API_URL + "/search?");
			
			if (birdName != null && !birdName.isEmpty()) {
				searchUrl.append("birdName=").append(java.net.URLEncoder.encode(birdName, "UTF-8")).append("&");
			}
			if (location != null && !location.isEmpty()) {
				searchUrl.append("location=").append(java.net.URLEncoder.encode(location, "UTF-8")).append("&");
			}
			if (fromDate != null && !fromDate.isEmpty()) {
				searchUrl.append("fromDate=").append(java.net.URLEncoder.encode(fromDate, "UTF-8")).append("&");
			}
			if (toDate != null && !toDate.isEmpty()) {
				searchUrl.append("toDate=").append(java.net.URLEncoder.encode(toDate, "UTF-8")).append("&");
			}
			
			searchUrl.append("page=0&size=10&sort=id,asc");
			
			return fetchSightings(searchUrl.toString());
		} catch (Exception e) {
			throw new RuntimeException("Error building search URL: " + e.getMessage(), e);
		}
	}

	/**
	 * Fetches sightings from the specified URL.
	 * <p>
	 * Sends a GET request to the provided URL and deserializes the JSON response
	 * into a list of Sighting objects.
	 * </p>
	 * 
	 * @param urlString the complete URL to fetch sightings from
	 * @return a list of sightings, or an empty list if none are found
	 * @throws RuntimeException if the HTTP request fails or returns an error status
	 */
	private List<Sighting> fetchSightings(String urlString) {
		try {
			URL url = new URL(urlString);
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
					SightingPagedResponse pagedResponse = objectMapper.readValue(response.toString(), SightingPagedResponse.class);
					return pagedResponse.getContent() != null ? pagedResponse.getContent() : new ArrayList<>();
				}
			} else {
				throw new RuntimeException("Failed to get sightings. HTTP response code: " + responseCode);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error getting sightings: " + e.getMessage(), e);
		}
	}

}
