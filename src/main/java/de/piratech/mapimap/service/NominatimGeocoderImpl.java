package de.piratech.mapimap.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.JsonParser;
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

  private static final Logger LOG = LoggerFactory.getLogger(NominatimGeocoderImpl.class);
  private MappingJsonFactory factory;
  private HttpClient client;

  public NominatimGeocoderImpl() {
    factory = new MappingJsonFactory();
    client = new DefaultHttpClient();
  }

  public LocationData getLocationData(String _address) {
    try {
      //@todo: deveth0@geirkairam: should be configured in settings
      String url = "http://nominatim.openstreetmap.org/search?q="
              + URLEncoder.encode(_address.replaceAll(",",	""), "UTF-8")
              + "&format=json&polygon=1&addressdetails=1";
      HttpGet get = new HttpGet(url);
      HttpResponse response = client.execute(get);
      InputStream stream = response.getEntity().getContent();
      JsonParser jsonParser = factory.createJsonParser(stream);	
      List<LocationData> locationData = jsonParser.readValueAs(new TypeReference<List<LocationData>>() {
      });
      if (!locationData.isEmpty()) {
        LOG.info("found loation data for address {}", _address);
        stream.close();
        return locationData.get(0);
      }
    } catch (IOException e) {
      LOG.error("message: {}", e.getMessage());
    } catch (IllegalStateException e) {
      LOG.error("message: {}", e.getMessage());
    }
    return null;
  }
}
