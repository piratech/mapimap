package de.piratech.mapimap.data;


/**
 * @author maria
 *
 */
public class Crew extends Meeting{

	// TODO think about using generics
  private DataType type = DataType.CREW;


  public DataType getType() {
    return type;
  }

  public void setType(DataType type) {
    this.type = type;
  }


}
