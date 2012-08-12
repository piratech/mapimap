package de.piratech.mapimap.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.piratech.mapimap.data.LocationData;

/**
 * @author maria
 * 
 */
public class NominatimGeocoderImpl implements Geocoder {

	private static final Logger LOG = LoggerFactory
			.getLogger(NominatimGeocoderImpl.class);
	private MappingJsonFactory factory;
	private HttpClient client;

	public NominatimGeocoderImpl() {
		factory = new MappingJsonFactory();
		client = new DefaultHttpClient();
	}

	public LocationData getLocationData(String _address) {
		try {
			// @todo: deveth0@geirkairam: should be configured in settings

			String url = "http://nominatim.openstreetmap.org/search?q="
					+ URLEncoder.encode(_address, "UTF-8")
					+ "&format=json&polygon=1&addressdetails=1";

			List<LocationData> locationData = getLocationDataListFromURL(url);
			if (!locationData.isEmpty()) {
				LOG.info("found loation data for address {}", _address);
				return getCorrectLocationData(locationData, _address);
			} else {
				// location service sometimes works only with comma and sometimes only
				// without, don't know why
				url = "http://nominatim.openstreetmap.org/search?q="
						+ URLEncoder.encode(_address.replaceAll(",", ""), "UTF-8")
						+ "&format=json&polygon=1&addressdetails=1";
				locationData = getLocationDataListFromURL(url);
				if (!locationData.isEmpty()) {
					LOG.info("found loation data for address {}", _address);
					return getCorrectLocationData(locationData, _address);
				} else {
					LOG.warn("cannot get location from URL >{}<", url);
				}
			}
		} catch (IOException e) {
			LOG.error("message: {}", e.getMessage());
		} catch (IllegalStateException e) {
			LOG.error("message: {}", e.getMessage());
		}
		return null;
	}

	// for some reason nominatim does not return the correct address at first
	// position --> search for data with matching postal code
	private LocationData getCorrectLocationData(
			List<LocationData> _locationDataList, String _address) {
		if (_locationDataList.size() == 1) {
			return _locationDataList.get(0);
		} else {
			for (LocationData locationData : _locationDataList) {
				if (StringUtils.contains(_address, locationData.getAddress()
						.getPostcode())) {
					return locationData;
				}
			}
		}

		return _locationDataList.get(0);
	}

	private List<LocationData> getLocationDataListFromURL(String url)
			throws IOException, ClientProtocolException, JsonParseException,
			JsonProcessingException {
		HttpGet get = new HttpGet(url);
		HttpResponse response = client.execute(get);
		InputStream stream = response.getEntity().getContent();
		JsonParser jsonParser = factory.createJsonParser(stream);
		List<LocationData> locationData = jsonParser
				.readValueAs(new TypeReference<List<LocationData>>() {
				});
		stream.close();
		return locationData;
	}

	private LocationData getLocationDataFromURL(String url) throws IOException,
			ClientProtocolException, JsonParseException, JsonProcessingException {
		HttpGet get = new HttpGet(url);
		HttpResponse response = client.execute(get);
		InputStream stream = response.getEntity().getContent();
		JsonParser jsonParser = factory.createJsonParser(stream);
		LocationData locationData = jsonParser.readValueAs(LocationData.class);
		stream.close();
		return locationData;
	}

	@Override
	public LocationData getLocationData(float lat, float lon) {
		String url = "http://nominatim.openstreetmap.org/reverse?format=json&lat="
				+ lat + "&lon=" + lon + "+&zoom=18&addressdetails=1";
		try {
			LocationData locationData = getLocationDataFromURL(url);
			LOG.info("found address {} details for lon {} and  lat {} ",
					new Object[] { locationData.getAddress().getAddressString(), lon, lat });
			return locationData;
		} catch (JsonParseException e) {
			LOG.error(e.getMessage(), e);
		} catch (ClientProtocolException e) {
			LOG.error(e.getMessage(), e);
		} catch (JsonProcessingException e) {
			LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}

		return null;
	}
}
