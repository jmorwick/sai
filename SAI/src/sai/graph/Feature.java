package sai.graph;


public final class Feature {
	private int id;
	private String value;
	private String name;
	
	public Feature(String name, String value) { 
		this.id = -1;
		this.name = name;
		this.value = value;
	}
	
	public Feature(String name, String value, int id) {
		this(name,value);
		this.id = id;
	}
	
	public int getID() { return id; }
	public String getValue() { return value; }
	public String getName() { return name; }
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Feature)) return false;
		Feature f = (Feature)o;
		return f.name.equals(name) && f.value.equals(value);
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	public String toString() { 
		return "("+name + ": " + value+")";
	}
}
