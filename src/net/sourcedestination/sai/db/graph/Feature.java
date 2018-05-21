package net.sourcedestination.sai.db.graph;

public final class Feature implements Comparable<Feature> {
	private final String value;
	private final String name;
	
	public Feature(String name, String value) { 
		this.name = name;
		this.value = value;
	}
	
	public String getValue() { return value; }
	public String getName() { return name; }
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Feature)) return false;
		var f = (Feature)o;
		return f.name.equals(name) && f.value.equals(value);
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toString() { 
		return "("+name + ": " + value+")";
	}

	@Override
	public int compareTo(Feature f) {
		return getName().equals(f.getName()) ? 
				getValue().compareTo(f.getValue()) :
				getName().compareTo(f.getName());
	}
}
