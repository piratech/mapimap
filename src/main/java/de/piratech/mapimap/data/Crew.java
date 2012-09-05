package de.piratech.mapimap.data;


/**
 * @author maria
 *
 */
public class Crew extends Meeting{

	// TODO think about using generics
  private MeetingType type = MeetingType.CREW;


  public MeetingType getType() {
    return type;
  }

  public void setType(MeetingType type) {
    this.type = type;
  }


}
