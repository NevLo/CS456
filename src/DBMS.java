
/*
 * 
 */
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class DBMS {
	// CREATE DATABASE <NAME>;
	// CREATE TABLE <NAME> (ARGS...);
	public static void create(ArrayList<String> parseTree) {
		if (parseTree.get(0).equalsIgnoreCase("database")) {
			createDB(parseTree.get(1));
		} else if (parseTree.get(0).equalsIgnoreCase("table")) {

		}
	}

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

	private static boolean createTBL(String string, ArrayList<String> attributes, ArrayList<String> Types) {
		return false;

	}

	// DROP DATABASE <NAME>;
	// DROP TABLE <NAME>;
	public static void drop(ArrayList<String> parseTree) {
		for (String s : parseTree) {
			System.out.println(s);
		}
	}

	// SELECT <REC:*> FROM <TBLNAME>
	public static void select(ArrayList<String> parseTree) {
		for (String s : parseTree) {
			System.out.println(s);
		}
	}

	// USE <DBNAME>
	@SuppressWarnings("unused")
	public static void use(ArrayList<String> parseTree) {
		Path useDir = Paths.get("c:\\" + parseTree.get(0));
		if (false) {
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
