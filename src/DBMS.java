
/*
 * 
 */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class DBMS {
	private static File useDirectory;

	// CREATE DATABASE <NAME>;
	// CREATE TABLE <NAME> (ARGS...);
	public static void create(ArrayList<String> parseTree) {
		if (parseTree.get(0).equalsIgnoreCase("database")) {
			createDB(parseTree.get(1));
		} else if (parseTree.get(0).equalsIgnoreCase("table")) {
			createTBL(parseTree.get(1), parseTree);
		}
	}

	// CREATE DATABASE <NAME>
	private static boolean createDB(String dir) {
		// TODO Auto-generated method stub
		File newDir = new File("/" + dir);
		if (newDir.exists()) {
			System.out.println("Failed to create database " + dir + " because it already exists");
			return false;
		} else {
			newDir.mkdir();
			if (newDir.exists()) {
				System.out.println("Database " + dir + " created.");
			} else {
				System.out.println("Failed to create database " + dir + "due to an unknown error");
				return false;
			}
		}
		return true;
	}

	// CREATE TABLE <NAME> (ARGS...)
	private static boolean createTBL(String tblname, ArrayList<String> parseTree) {
		if (useDirectory == null) {
			System.out.println("Failed to create table because a database was not selected.");
			return false;
		}
		parseTree.remove(0);
		parseTree.remove(0);
		String attributesLine = "";
		for (String s : parseTree) {
			attributesLine += s + " ";
		}
		parseTree.clear();
		attributesLine = attributesLine.substring(1, attributesLine.length() - 2);
		parseTree = new ArrayList<String>(Arrays.asList(attributesLine.split(",")));
		ArrayList<String> atts = new ArrayList<>();
		ArrayList<String> types = new ArrayList<>();
		for (String s : parseTree) {
			if (s.startsWith(" ")) {
				s = s.substring(1);
			}
			String[] atType = s.split(" ");
			atts.add(atType[0]);
			types.add(atType[1]);
		}
		File newTable = new File(useDirectory + "/" + tblname + ".tbl");
		FileWriter table;
		try {
			table = new FileWriter(newTable);
			table.write('|');
			for (String s : atts) {
				table.write(s + "|");
			}
			table.write("\n|");
			for (String s : types) {
				table.write(s + "|");
			}
			table.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	// DROP DATABASE <NAME>;
	// DROP TABLE <NAME>;
	public static void drop(ArrayList<String> parseTree) {
		if (parseTree.get(0).equalsIgnoreCase("table")) {
			if (useDirectory == null) {
				System.out.println("Failed to drop table, as no database was selected.");
			}
		} else if (parseTree.get(0).equalsIgnoreCase("database")) {

		}
	}

	// SELECT <REC:*> FROM <TBLNAME>
	public static void select(ArrayList<String> parseTree) {
		for (String s : parseTree) {
			System.out.println(s);
		}
	}

	// USE <DBNAME>

	public static void use(ArrayList<String> parseTree) {
		useDirectory = new File("/" + parseTree.get(0));
		if (useDirectory.exists()) {
			System.out.println("Using Database " + parseTree.get(0));
		} else {
			System.out.println("Failed to use database " + parseTree.get(0) + " because it does not exist.");
		}
	}

	// ALTER TABLE <TBLNAME> ADD <NAME> <TYPE>
	// ALTER TABLE <TBLNAME> ADD (<NAME> <TYPE>...)
	// ALTER TABLE <TBLNAME> REMOVE <NAME>
	// ALTER TABLE <TBLNAME> UPDATE <NAME> <TYPE>
	public static void alter(ArrayList<String> parseTree) {
		for (String s : parseTree) {
			System.out.println(s);
		}
	}
}
