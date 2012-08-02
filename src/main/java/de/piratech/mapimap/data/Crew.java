package de.piratech.mapimap.data;


/**
 * @author maria
 *
 */
public class Crew extends BLA{

	//@todo: deveth0@geirkairam: propably a map-url would be useful for external apps
  private DataType type = DataType.CREW;


  public DataType getType() {
    return type;
  }

  public void setType(DataType type) {
    this.type = type;
  }


}
