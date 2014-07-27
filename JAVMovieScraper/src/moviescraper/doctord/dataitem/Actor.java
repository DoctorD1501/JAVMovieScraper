package moviescraper.doctord.dataitem;

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


}
