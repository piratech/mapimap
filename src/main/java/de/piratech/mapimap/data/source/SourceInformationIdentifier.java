/**
 * 
 */
package de.piratech.mapimap.data.source;


/**
 * @author maria
 * 
 */
public class SourceInformationIdentifier<ValueType> {

	private SourceInformationIdentifierType type;
	private String key;
	private ValueType value;

	public SourceInformationIdentifier() {
	}

	public SourceInformationIdentifier(SourceInformationIdentifierType type,
			String key, ValueType value) {
		this.type = type;
		this.key = key;
		this.value = value;
	}

	public SourceInformationIdentifierType getType() {
		return type;
	}

	public void setType(SourceInformationIdentifierType type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public ValueType getValue() {
		return value;
	}

	public void setValue(ValueType value) {
		this.value = value;
	}

}
