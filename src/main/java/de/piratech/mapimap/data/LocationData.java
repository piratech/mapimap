package de.piratech.mapimap.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * @author maria
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocationData {

  private float lat;
  private float lon;
  private Address address;

  public float getLat() {
    return lat;
  }

  public void setLat(float lat) {
    this.lat = lat;
  }

  public float getLon() {
    return lon;
  }

  public void setLon(float lon) {
    this.lon = lon;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }
}
