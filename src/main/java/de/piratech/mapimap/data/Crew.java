package de.piratech.mapimap.data;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author maria
 *
 */
public class Crew {
  //@todo: deveth0@geirkairam:  see Address.java
  //@todo: deveth0@geirkairam: There should be a timestamp, when the crew has been updated for the last time
  //@todo: deveth0@geirkairam: propably a map-url would be useful for external apps
  
  @JsonProperty("_id")
  private String id;
  @JsonProperty("_rev")
  private String revision;
  private String name;
  private String wikiUrl;
  private LocationData locationData;
  private DataType type = DataType.crew;

  public String getId() {
    return id;
  }

  public void setId(String s) {
    id = s;
  }

  public String getRevision() {
    return revision;
  }

  @JsonProperty("_rev")
  public void setRevision(String s) {
    revision = s;
  }

  public void setName(String s) {
    name = s;
  }

  public String getName() {
    return name;
  }

  public String getWikiUrl() {
    return wikiUrl;
  }

  public void setWikiUrl(String url) {
    this.wikiUrl = url;
  }

  public LocationData getLocationData() {
    return locationData;
  }

  public void setLocationData(LocationData locationData) {
    this.locationData = locationData;
  }

  public DataType getType() {
    return type;
  }

  public void setType(DataType type) {
    this.type = type;
  }
}