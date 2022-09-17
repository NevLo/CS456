
/*
 * 
 */
import java.util.ArrayList;

public class DBMS {
	// CREATE DATABASE <NAME>;
	// CREATE TABLE <NAME> (ARGS...);
	public static void create(ArrayList<String> parseTree) {
		if (parseTree.get(0).equalsIgnoreCase("database")) {
			if (createDB(parseTree.get(1))) {
				System.out.println("Database " + parseTree.get(1) + " created.");
			} else {
				System.out.println("Failed to create database " + parseTree.get(1) + " because it already exists");
			}
		} else if (parseTree.get(0).equalsIgnoreCase("table")) {

		}
	}

	private static boolean createDB(String string) {
		// TODO Auto-generated method stub
		return true;
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
	public static void use(ArrayList<String> parseTree) {
		for (String s : parseTree) {
			System.out.println(s);
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
