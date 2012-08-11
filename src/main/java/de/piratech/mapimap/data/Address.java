package de.piratech.mapimap.data;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author maria
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address {

	@JsonProperty("house_number")
	private String houseNumber;
	private String road;
	private String suburb;
	private String city;
	@JsonProperty("city_district")
	private String cityDistrict;
	private String state;
	private String postcode;
	private String country;
	@JsonProperty("country_code")
	private String countryCode;

	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCityDistrict() {
		return cityDistrict;
	}

	public void setCityDistrict(String cityDistrict) {
		this.cityDistrict = cityDistrict;
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

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String toString() {
		return "Address{house_number:" + houseNumber + ",road:" + road + ",suburb:"
				+ suburb + ",cityDistrict:" + cityDistrict + ",state:" + state
				+ ",postcode:" + postcode + ",country:" + country + ",countryCode:"
				+ countryCode;
	}

	@JsonIgnore
	public boolean isValid() {
		return StringUtils.isNotEmpty(road) && StringUtils.isNotEmpty(postcode)
				&& StringUtils.isNotEmpty(city);
	}

	@JsonIgnore
	public String getAddressString() {
		return this.road
				+ (StringUtils.isNotEmpty(this.houseNumber) ? " " + this.houseNumber
						: "") + ", " + this.postcode + " " + this.city;
	}
}
