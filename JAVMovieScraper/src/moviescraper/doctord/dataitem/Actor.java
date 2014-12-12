package moviescraper.doctord.dataitem;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import moviescraper.doctord.Thumb;

public class Actor extends Person {
	private String role;

	public Actor(String name, String role, Thumb thumb) {
		super(name, thumb);
		this.setRole(role);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String  toXML() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = sanitizeString(role);
	}

	@Override
	public String toString() {
		return "Actor [role=" + role + ", toString()=" + super.toString() + "]";
	}

	public void writeImageToFile(File fileNameToWrite) throws IOException {
		if(getThumb() != null && getThumb().getThumbURL() != null && getThumb().getThumbURL().getPath().length() > 0)
			FileUtils.copyURLToFile(getThumb().getThumbURL(), fileNameToWrite, connectionTimeout, readTimeout);
		
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Actor other = (Actor) obj;
		if (role == null) {
			if (other.role != null)
				return false;
		} else if (!role.equals(other.role))
			return false;
		return true;
	}
	
	


}
