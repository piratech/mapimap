package de.piratech.mapimap.service;

import de.piratech.mapimap.data.LocationData;

/**
 * @author maria
 *
 */
public interface Geocoder {

  public LocationData getLocationData(String address);
  
  public LocationData getLocationData(float lat, float lon);
}
