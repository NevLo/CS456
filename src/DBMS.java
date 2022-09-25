
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
		// TODO Auto-generated method stub0
		File newDir = new File("/" + dir);
		if (newDir.exists()) {
			System.out.println("Failed to create database " + dir + " because it already exists");
		} else {
			newDir.mkdir();
			if (newDir.exists()) {
				System.out.println("Database " + dir + " created.");
			} else {
				System.out.println("Failed to create database " + dir + "due to an unknown error");
			}
		}
	}

	// CREATE TABLE <NAME> (ARGS...)
	private static void createTBL(String tblname, ArrayList<String> parseTree) {
		if (useDirectory == null) {
			System.out.println("Failed to create table because a database was not selected.");
		} else {
			if ((new File(useDirectory + "/" + tblname + ".tbl").exists())) {
				System.out.println("Failed to create table" + tblname + " because it already exists.");
			} else {
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
			}
		}
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
		if (useDirectory == null) {
			System.out.println("!Failed to drop table, as no database was selected.");
		} else {
			String tblname = parseTree.get(0);
			if ((new File(useDirectory + "/" + tblname + ".tbl")).exists()) {
				File tbl = new File(useDirectory + "/" + tblname + ".tbl");
				tbl.delete();
				System.out.println("Table " + tblname + " deleted.");
			} else {
				System.out.println("!Failed to delete table " + tblname + " because it does not exist");
			}
		}
	}

	// DROP DATABASE <NAME>
	private static void dropDB(ArrayList<String> parseTree) {
		String dbname = parseTree.get(0);
		File db = new File("/" + dbname);
		if (db.exists()) {
			File[] tbls = db.listFiles();
			for (File f : tbls) {
				f.delete();
			}
			db.delete();
			System.out.println("Database " + dbname + " deleted.");
		} else {
			System.out.println("!Failed to delete database " + dbname + " because it does not exist.");
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
		if (useDirectory == null) {
			System.out.println("!Failed to alter table because no database has been selected.");
		} else {
			String tbl = parseTree.remove(0);
			String tblname = parseTree.remove(0);
			String cmd = parseTree.remove(0);
			if (tbl.equalsIgnoreCase("table")) {
				File tblFile = new File(useDirectory + "/" + tblname + ".tbl");
				// System.out.println(tblFile.length());
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
			} else {
				System.out.println("!Invalid Syntax: " + tbl + " is not a valid keyword.");
			}
		}
	}

	// ALTER TABLE <TBLNAME> ADD <NAME> <TYPE>
	// ALTER TABLE <TBLNAME> ADD (<NAME> <TYPE>...)
	private static void alterAdd(ArrayList<String> atts, ArrayList<String> types, ArrayList<String> parseTree) {
		if (parseTree.size() == 2) {
			atts.add(parseTree.get(0));
			types.add(parseTree.get(1));
		} else {
			String addedAtts = "";
			for (String s : parseTree) {
				addedAtts += s + " ";
			}
		}
	}

	// ALTER TABLE <TBLNAME> REMOVE <NAME>
	private static void alterRemove(ArrayList<String> atts, ArrayList<String> types, ArrayList<String> parseTree) {
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
		for (int i = 0; i < atts.size(); i++) {
			if (atts.get(i).equalsIgnoreCase(parseTree.get(0))) {
				types.set(i, parseTree.get(1));
				return;
			}
		}
		System.out.println("!Failed to update tuple " + parseTree.get(0) + " as it does not exist.");
	}

}
