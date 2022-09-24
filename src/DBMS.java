
/*
 * 
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

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
		// TODO Auto-generated method stub0
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

		if ((new File(useDirectory + "/" + tblname + ".tbl").exists())) {
			System.out.println("Failed to create table" + tblname + " because it already exists.");
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
			for (int i = 0; i < atts.size(); i++) {
				table.write(atts.get(i) + " " + types.get(i));
				if (i != atts.size() - 1) {
					table.write(" | ");
				}
			}
			table.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Table " + tblname + " created.");
		return true;
	}

	// DROP DATABASE <NAME>;
	// DROP TABLE <NAME>;
	public static void drop(ArrayList<String> parseTree) {
		if (parseTree.get(0).equalsIgnoreCase("table")) {
			if (useDirectory == null) {
				System.out.println("!Failed to drop table, as no database was selected.");
			} else {
				if ((new File(useDirectory + "/" + parseTree.get(1) + ".tbl")).exists()) {
					File tbl = new File(useDirectory + "/" + parseTree.get(1) + ".tbl");
					tbl.delete();
					System.out.println("Table " + parseTree.get(1) + " deleted.");
				} else {
					System.out.println("!Failed to delete table " + parseTree.get(1) + " because it does not exist");
				}
			}
		} else if (parseTree.get(0).equalsIgnoreCase("database")) {
			File db = new File("/" + parseTree.get(1));
			if (db.exists()) {
				File[] tbls = db.listFiles();
				for (File f : tbls) {
					f.delete();
				}
				db.delete();
				System.out.println("Database " + parseTree.get(1) + " deleted.");
			} else {
				System.out.println("!Failed to delete database " + parseTree.get(1) + " because it does not exist.");
			}
		}
	}

	// SELECT <REC:*> FROM <TBLNAME>
	public static void select(ArrayList<String> parseTree) {
		if (!parseTree.get(1).equalsIgnoreCase("from")) {
			System.out.println("!Invalid Syntax! " + parseTree.get(1) + " is not a valid keyword");
			return;
		}
		if (parseTree.get(0).equalsIgnoreCase("*")) {
			File tbl = new File(useDirectory + "/" + parseTree.get(2) + ".tbl");
			Scanner fileReader = null;
			try {
				fileReader = new Scanner(tbl);
			} catch (FileNotFoundException e) {
				System.out.println("Failed to query table " + parseTree.get(2) + " as it does not exist");
				return;
			}
			while (fileReader.hasNext()) {
				System.out.print(fileReader.next() + " ");
			}
			System.out.println();
			fileReader.close();
			return;
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
		String tbl = parseTree.get(0);
		String tblname = parseTree.get(1);
		String cmd = parseTree.get(2);
		parseTree.remove(0);
		parseTree.remove(0);
		parseTree.remove(0);
		if (tbl.equalsIgnoreCase("table")) {
			if (cmd.equalsIgnoreCase("add")) {
				alterAdd(tblname, parseTree);
			} else if (cmd.equalsIgnoreCase("remove")) {
				alterRemove(tblname, parseTree);
			} else if (cmd.equalsIgnoreCase("update")) {
				alterUpdate(tblname, parseTree);
			} else {
				System.out.println("Invalid alter command");
			}
		} else {
			System.out.println("!Invalid Syntax: " + tbl + " is not a valid keyword.");
		}
	}

	private static void alterAdd(String tblname, ArrayList<String> parseTree) {
		// TODO Auto-generated method stub
		System.out.println("method stub for alter add.");
	}

	private static void alterRemove(String tblname, ArrayList<String> parseTree) {
		// TODO Auto-generated method stub
		System.out.println("method stub for alter remove");
	}

	private static void alterUpdate(String tblname, ArrayList<String> parseTree) {
		// TODO Auto-generated method stub
		System.out.println("method stub for alter update");
	}

}
