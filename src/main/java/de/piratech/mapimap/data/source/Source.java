/**
 * 
 */
package de.piratech.mapimap.data.source;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import de.piratech.mapimap.data.MeetingType;

/**
 * @author maria
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Source {

	@JsonProperty("_id")
	private String id;
	@JsonProperty("_rev")
	private String revision;
	private String name;
	private String base;
	private String url;
	private SourceType sourceType;
	private MeetingType meetingType;
	private List<SourceInformationIdentifier<?>> sourceInformationIdentifiers;
	private String type = "SOURCE";

	private String checkSum;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public SourceType getSourceType() {
		return sourceType;
	}

	public void setSourceType(SourceType htmltable) {
		this.sourceType = htmltable;
	}

	public MeetingType getMeetingType() {
		return meetingType;
	}

	public void setMeetingType(MeetingType meetingType) {
		this.meetingType = meetingType;
	}

	public List<SourceInformationIdentifier<?>> getInformationTypes() {
		return sourceInformationIdentifiers;
	}

	public void setInformationTypes(
			List<SourceInformationIdentifier<?>> informationTypes) {
		this.sourceInformationIdentifiers = informationTypes;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCheckSum() {
		if (StringUtils.isBlank(checkSum)) {
			checkSum = DigestUtils.md5Hex(getUrl() + getSourceType());
		}
		return checkSum;
	}

	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}

	@JsonIgnore
	public <ValueType> ValueType getSourceInformationIdentifier(
			SourceInformationIdentifierType type, Class<ValueType> clazz) {
		for (SourceInformationIdentifier<?> informationIdentifier : sourceInformationIdentifiers) {
			if (informationIdentifier.getType().equals(type)) {
				Object value = informationIdentifier.getValue();
				if (value.getClass().equals(clazz)) {
					return (ValueType) value;
				} else {
					Map<String, ?> map = (Map<String, ?>) value;
					try {
						Iterator<String> iterator = map.keySet().iterator();
						ValueType newInstance = clazz.newInstance();
						while (iterator.hasNext()) {
							String fieldName = iterator.next();
							Field field = clazz.getDeclaredField(fieldName);
							Method method = clazz.getMethod(
									"set" + WordUtils.capitalize(fieldName), field.getType());
							method.invoke(newInstance, map.get(fieldName));
						}
						return newInstance;
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();

					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchFieldException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}
		return null;
	}
}
