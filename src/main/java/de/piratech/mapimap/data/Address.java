package de.piratech.mapimap.data;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * @author maria
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address {

  //@todo: deveth0@geirkairam: variables should be private
  //@todo: deveth0@geirkairam:  Camel-case for variables (e.g. houseNumber)
  String house_number;
  String road;
  String suburb;
  String city_district;
  String state;
  String postcode;
  String country;
  String country_code;

  public String getHouse_number() {
    return house_number;
  }

  
  public void setHouse_number(String house_number) {
    this.house_number = house_number;
  }

  public String getRoad() {
    return road;
  }

  public void setRoad(String road) {
    this.road = road;
  }

  public String getSuburb() {
    return suburb;
  }

  public void setSuburb(String suburb) {
    this.suburb = suburb;
  }

  public String getCity_district() {
    return city_district;
  }

  public void setCity_district(String city_district) {
    this.city_district = city_district;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getPostcode() {
    return postcode;
  }

  public void setPostcode(String postcode) {
    this.postcode = postcode;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getCountry_code() {
    return country_code;
  }

  public void setCountry_code(String country_code) {
    this.country_code = country_code;
  }
}
