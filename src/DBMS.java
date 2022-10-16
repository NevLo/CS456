
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
		String cmd = parseTree.remove(0);
		String name = parseTree.remove(0);
		if (cmd.equalsIgnoreCase("database")) {
			createDB(name);
		} else if (cmd.equalsIgnoreCase("table")) {
			createTBL(name, parseTree);
		}
	}

	// CREATE DATABASE <NAME>
	private static void createDB(String dir) {
		// Check to see if the database name string is empty.
		if (dir.isEmpty()) {
			System.out.println("Failed to create database as no name was specified.");
			return;
		}
		File newDir = new File("/" + dir);
		// Don't create a database if a database with the same name exists.
		if (newDir.exists()) {
			System.out.println("Failed to create database " + dir + " because it already exists");
			return;
		}
		// Make directory and check it was properly made.
		newDir.mkdir();
		if (newDir.exists()) {
			System.out.println("Database " + dir + " created.");
		} else {
			System.out.println("Failed to create database " + dir + "due to an unknown error");
		}
	}

	// CREATE TABLE <NAME> (ARGS...)
	private static void createTBL(String tblname, ArrayList<String> parseTree) {
		// Don't create a table if no database is in use.
		if (useDirectory == null) {
			System.out.println("Failed to create table because a database was not selected.");
			return;
		}
		// Don't create a table if a table with the same name exists.
		if ((new File(useDirectory + "/" + tblname + ".tbl").exists())) {
			System.out.println("Failed to create table" + tblname + " because it already exists.");
			return;
		}
		// Don't create a table if no fields are given.
		if (parseTree.isEmpty()) {
			System.out.println("Failed to create table because no fields were given.");
			return;
		}
		// Combine the parse tree into one single string
		// This makes it easier to deal with the parenthesis and resplit if needed.
		String attributesLine = "";
		for (String s : parseTree) {
			attributesLine += s + " ";
		}
		parseTree.clear();
		// remove parenthesis if they are there.
		if (attributesLine.charAt(0) == '(' && attributesLine.charAt(attributesLine.length() - 2) == ')') {
			attributesLine = attributesLine.substring(1, attributesLine.length() - 2);
		}
		// reuse the parse tree to split the string at commas, so each node is of the
		// form "name type"
		parseTree = new ArrayList<String>(Arrays.asList(attributesLine.split(",")));
		ArrayList<String> atts = new ArrayList<>();
		ArrayList<String> types = new ArrayList<>();
		// add each type and name to a list.
		for (String s : parseTree) {
			if (s.startsWith(" ")) {
				s = s.substring(1);
			}
			String[] atType = s.split(" ");
			atts.add(atType[0]);
			types.add(atType[1]);
		}
		// create a new filewriter for the table.
		File newTable = new File(useDirectory + "/" + tblname + ".tbl");
		FileWriter table;
		try {
			// init filewriter
			table = new FileWriter(newTable);
			// write to file atts and types ## THIS COULD BE A FUNCTION, AS THIS CODE IS
			// USED ELSEWHERE ##
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
	}

	// DROP DATABASE <NAME>;
	// DROP TABLE <NAME>;
	public static void drop(ArrayList<String> parseTree) {
		String cmd = parseTree.remove(0);
		if (cmd.equalsIgnoreCase("table")) {
			dropTBL(parseTree);
		} else if (cmd.equalsIgnoreCase("database")) {
			dropDB(parseTree);
		}
	}

	// DROP TABLE <NAME>;
	private static void dropTBL(ArrayList<String> parseTree) {
		// Don't drop the table if there is not a currently selected database.
		if (useDirectory == null) {
			System.out.println("!Failed to drop table, as no database was selected.");
			return;
		}
		String tblname = parseTree.get(0);
		// Don't drop the table if it doesnt exist.
		if ((new File(useDirectory + "/" + tblname + ".tbl")).exists()) {
			System.out.println("!Failed to delete table " + tblname + " because it does not exist");
			return;
		}
		File tbl = new File(useDirectory + "/" + tblname + ".tbl");
		tbl.delete();
		System.out.println("Table " + tblname + " deleted.");
	}

	// DROP DATABASE <NAME>
	private static void dropDB(ArrayList<String> parseTree) {
		String dbname = parseTree.get(0);
		File db = new File("/" + dbname);
		// Don't drop the database if it doesnt exist.
		if (!db.exists()) {
			System.out.println("!Failed to delete database " + dbname + " because it does not exist.");
			return;
		}
		File[] tbls = db.listFiles();
		for (File f : tbls) {
			f.delete();
		}
		db.delete();
		System.out.println("Database " + dbname + " deleted.");
	}

	// SELECT <REC:*> FROM <TBLNAME>
	public static void select(ArrayList<String> parseTree) {
		// if from isnt in here correctly
		// ## THIS WILL NEED TO BE CHANGED WHEN SELECT COLOUMNS IS ADDED LATER ##
		// THIS WILL NEED A MASSIVE REWRITE BUT THATS FOR FUTURE ME

		if (!parseTree.get(1).equalsIgnoreCase("from")) {
			System.out.println("!Invalid Syntax! " + parseTree.get(1) + " is not a valid keyword");
			return;
		}
		// Select all from table
		//
		if (parseTree.get(0).equalsIgnoreCase("*")) {
			File tbl = new File(useDirectory + "/" + parseTree.get(2) + ".tbl");
			Scanner fileReader = null;
			try {
				fileReader = new Scanner(tbl);
			} catch (FileNotFoundException e) {
				System.out.println("!Failed to query table " + parseTree.get(2) + " as it does not exist");
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
		String dirName = parseTree.remove(0);
		useDirectory = new File("/" + dirName);
		if (useDirectory.exists()) {
			System.out.println("Using Database " + dirName);
		} else {
			System.out.println("!Failed to use database " + dirName + " because it does not exist.");
		}
	}

	// ALTER TABLE <TBLNAME> ADD <NAME> <TYPE>
	// ALTER TABLE <TBLNAME> ADD (<NAME> <TYPE>...)
	// ALTER TABLE <TBLNAME> REMOVE <NAME>
	// ALTER TABLE <TBLNAME> UPDATE <NAME> <TYPE>
	public static void alter(ArrayList<String> parseTree) {
		// Don't alter a table if no database is selected.
		if (useDirectory == null) {
			System.out.println("!Failed to alter table because no database has been selected.");
			return;
		}
		String tbl = parseTree.remove(0);
		String tblname = parseTree.remove(0);
		String cmd = parseTree.remove(0);
		// check for proper syntax on table.
		if (!tbl.equalsIgnoreCase("table")) {
			System.out.println("!Invalid Syntax: " + tbl + " is not a valid keyword.");
		}
		File tblFile = new File(useDirectory + "/" + tblname + ".tbl");
		Scanner fileReader = null;
		try {
			fileReader = new Scanner(tblFile);
			ArrayList<String> atts = new ArrayList<String>();
			ArrayList<String> types = new ArrayList<String>();
			String[] list = fileReader.nextLine().split(" \\| ");
			for (String s : list) {
				String[] atType = s.split(" ");
				atts.add(atType[0]);
				types.add(atType[1]);
			}
			fileReader.close();
			if (cmd.equalsIgnoreCase("add")) {
				alterAdd(atts, types, parseTree);
			} else if (cmd.equalsIgnoreCase("remove")) {
				alterRemove(atts, types, parseTree);
			} else if (cmd.equalsIgnoreCase("update")) {
				alterUpdate(atts, types, parseTree);
			} else {
				System.out.println("Invalid alter command");
			}
			FileWriter fw = new FileWriter(tblFile);
			for (int i = 0; i < atts.size(); i++) {
				fw.write(atts.get(i) + " " + types.get(i));
				if (i != atts.size() - 1) {
					fw.write(" | ");
				}
			}
			fw.close();
		} catch (FileNotFoundException e) {
			System.out.println("!Failed to alter table " + tblname + "because it does not exist.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Table " + tblname + " modified.");
	}

	// ALTER TABLE <TBLNAME> ADD <NAME> <TYPE>
	// ALTER TABLE <TBLNAME> ADD (<NAME> <TYPE>...)
	private static void alterAdd(ArrayList<String> atts, ArrayList<String> types, ArrayList<String> parseTree) {
		// check to make sure arguments have been passed to add.
		if (parseTree.isEmpty()) {
			System.out.println("!Failed to add: no arguments specified.");
		}
		// Check to see if the parse tree size is 2
		// If size = 2, then you dont need to deal with commas and parenthesis.
		if (parseTree.size() == 2) {
			String att = parseTree.remove(0);
			String type = parseTree.remove(0);
			// if the new attribute name is the same as a previous one, return
			if (atts.contains(att)) {
				System.out.println("Failed to add field " + att + " because it already exists");
				return;
			}
			atts.add(att);
			types.add(type);
			return;
		}
		// combine the parse tree into one string.
		String addedAtts = "";
		for (String s : parseTree) {
			addedAtts += s + " ";
		}
		parseTree.clear();
		// remove parenthesis if they are there.
		addedAtts = addedAtts.substring(1, addedAtts.length() - 2);
		// reuse the parse tree to split the string at commas, so each node is of the
		// form "name type"
		parseTree = new ArrayList<String>(Arrays.asList(addedAtts.split(",")));
		// add each type and name to a list.
		ArrayList<String> a = new ArrayList<>();
		ArrayList<String> t = new ArrayList<>();
		for (String s : parseTree) {
			if (s.startsWith(" ")) {
				s = s.substring(1);
			}
			String[] atType = s.split(" ");
			// make sure that the type name isnt already one
			if (atts.contains(atType[0])) {
				System.out.println("Failed to add field " + atType[0] + " because it already exists");
				continue;
			}
			// add to a list just incase one fails, we dont want it to bork.
			a.add(atType[0]);
			t.add(atType[1]);
		}
		atts.addAll(a);
		types.addAll(t);

	}

	// ALTER TABLE <TBLNAME> REMOVE <NAME>
	private static void alterRemove(ArrayList<String> atts, ArrayList<String> types, ArrayList<String> parseTree) {
		if (parseTree.size() != 1) {
			System.out.println("!Invalid number of arguments: " + parseTree.size());
			return;
		}
		for (int i = 0; i < atts.size(); i++) {
			if (atts.get(i).equalsIgnoreCase(parseTree.get(0))) {
				atts.remove(i);
				types.remove(i);
				return;
			}
		}
		System.out.println("!Failed to remove tuple " + parseTree.get(0) + " as it does not exist.");
	}

	// ALTER TABLE <TBLNAME> UPDATE <NAME> <TYPE>
	private static void alterUpdate(ArrayList<String> atts, ArrayList<String> types, ArrayList<String> parseTree) {
		if (parseTree.size() != 2) {
			System.out.println("!Invalid number of arguements: " + parseTree.size());
		}

		for (int i = 0; i < atts.size(); i++) {
			if (atts.get(i).equalsIgnoreCase(parseTree.get(0))) {
				types.set(i, parseTree.get(1));
				return;
			}
		}
		System.out.println("!Failed to update tuple " + parseTree.get(0) + " as it does not exist.");
	}

	// INSERT INTO <TBLNAME> (c1, c2, c3...) VALUES (v1, v2, v3...);
	// INSERT INTO <TBLNAME> VALUES (v1, v2, v3...);
	public static void insert(ArrayList<String> parseTree) {
		if (useDirectory == null) {
			System.out.println("Failed to insert into table because no database has been selected.");
			return;
		}

	}

}
