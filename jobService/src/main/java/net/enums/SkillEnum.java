package net.enums;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.TreeBidiMap;

public enum SkillEnum {
	
	C_SHARP		(1,"C#"),
	ASP			(2,"ASP"),
	NET			(3,".NET"),
	MVC			(4,"MVC"),
	SQL			(5,"SQL"),
	HTML		(6,"HTML"),
	CSS			(7,"CSS"),
	LAMP		(8,"LAMP"),
	PHP			(9,"PHP"),
	JAVA		(10,"Java"),
	Linux		(11,"Linux"),
	J2EE		(12,"J2EE"),
	UNIX		(13,"UNIX"),
	SPRING		(14,"Spring"),
	ANDROID		(15,"Android"),
	IOS			(16,"iOS"),
	WINDOWS		(17,"Windows"),
	JAVASCRIPT	(18,"Javascript"),
	XML			(19,"XML"),
	SOAP		(20,"SOAP"),
	REST		(21,"REST"),
	JSON		(22,"JSON"),
	SVN			(23,"SVN"),
	GIT			(24,"GIT"),
	UML			(25,"UML"),
	AJAX		(26,"AJAX"),
	TOMCAT		(27,"Tomcat"),
	PYTHON		(28,"Python"),
	DJANGO		(29,"Django"),	
	ANGULAR		(30,"Angular"),	
	ECLIPSE		(31,"Eclipse"),	
	EMBER		(32,"Ember"),	
	WPF			(33,"WPF");	
	
	
	private final int id;
	private final String name;
	
	private static BidiMap skillMap;
	
	SkillEnum (int id, String name) {
		this.id = id;
		this.name = name;
	}
	
    public static String getName(Integer id) {
        if (skillMap == null) {
            initMapping();
        }
        return skillMap.get(id).toString();
    }
    
    public static int getId(String name) {
        if (skillMap == null) {
            initMapping();
        }
        return (int)skillMap.getKey(name);
    }    
 
    private static void initMapping() {
    	skillMap = new TreeBidiMap();
        for (SkillEnum ce : values()) {
        	skillMap.put(ce.id, ce.name);
        }
    }
    
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
}
