
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
	private static ArrayList<String> lockedFiles = new ArrayList<String>();

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
			System.err.println("Failed to create database as no name was specified.");
			return;
		}
		File newDir = new File("/" + dir);
		// Don't create a database if a database with the same name exists.
		if (newDir.exists()) {
			System.err.println("Failed to create database " + dir + " because it already exists");
			return;
		}
		// Make directory and check it was properly made.
		newDir.mkdir();
		if (newDir.exists()) {
			System.out.println("Database " + dir + " created.");
		} else {
			System.err.println("Failed to create database " + dir + "due to an unknown error");
		}
	}

	// CREATE TABLE <NAME> (ARGS...)
	private static void createTBL(String tblname, ArrayList<String> parseTree) {
		// Don't create a table if no database is in use.
		if (useDirectory == null) {
			System.err.println("Failed to create table because a database was not selected.");
			return;
		}
		// Don't create a table if a table with the same name exists.
		if ((new File(useDirectory + "/" + tblname + ".tbl").exists())) {
			System.err.println("Failed to create table" + tblname + " because it already exists.");
			return;
		}
		// Don't create a table if no fields are given.
		if (parseTree.isEmpty()) {
			System.err.println("Failed to create table because no fields were given.");
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
			table.write("\n");
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
			System.err.println("!Failed to drop table, as no database was selected.");
			return;
		}
		String tblname = parseTree.get(0);
		// Don't drop the table if it doesnt exist.
		if (!(new File(useDirectory + "/" + tblname + ".tbl")).exists()) {
			System.err.println("!Failed to delete table " + tblname + " because it does not exist");
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
			System.err.println("!Failed to delete database " + dbname + " because it does not exist.");
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
		// please fucking rewrite this for the love of god holy shit.
		// this is a travesty against god.
		// check to see if a database is selected.
		if (useDirectory == null) {
			System.err.println("!Failed to select from table as no database has been used.");
			return;
		}

		boolean getAll = false;
		int joinInd = parseTree.indexOf("join");
		int joinType = -1; // 0 = inner, 1 = left outer, 2 = right outer, 3 = full outer, -1 = no join
		if (joinInd != -1) {
			if (parseTree.get(joinInd - 1).equalsIgnoreCase("inner"))
				joinType = 0;
			else if (parseTree.get(joinInd - 1).equalsIgnoreCase("left")
					|| parseTree.get(joinInd - 2).equalsIgnoreCase("left"))
				joinType = 1;
			else if (parseTree.get(joinInd - 1).equalsIgnoreCase("right")
					|| parseTree.get(joinInd - 2).equalsIgnoreCase("right"))
				joinType = 2;
			else if (parseTree.get(joinInd - 1).equalsIgnoreCase("full")
					|| parseTree.get(joinInd - 2).equalsIgnoreCase("full"))
				joinType = 3;
		}

		int fromInd = parseTree.indexOf("from");
		ArrayList<String> cols = null;

		if (fromInd != 1) {
			// check to see if from index is -1 (syntax error)
			if (fromInd == -1) {
				System.err.println("!Failed to select due to a syntax error! FROM not included.");
			}
			// grab all coloumn names.
			cols = new ArrayList<String>(parseTree.subList(0, fromInd));
			for (String s : cols) {
				if (s.indexOf(',') == s.length() - 1) {
					s = s.substring(0, s.length() - 1);
				}
			}
		} else {
			if (parseTree.get(0).equalsIgnoreCase("*")) {
				getAll = true;
				cols = new ArrayList<String>();
			} else {
				cols = new ArrayList<String>();
				cols.add(parseTree.get(0));
			}
		}
		boolean multipleTables = false;
		ArrayList<Scanner> tableReaders = new ArrayList<Scanner>();
		ArrayList<String[]> tables = null;
		int whereInd = Math.max(parseTree.indexOf("where"), parseTree.indexOf("on"));
		if (whereInd != -1 && whereInd - fromInd != 2) {
			ArrayList<String> tablesUnformatted = new ArrayList<String>(parseTree.subList(fromInd + 1, whereInd));
			if (joinType != -1) {
				tablesUnformatted.remove(2);
				tablesUnformatted.remove(2);
				if (joinType != 0) {
					tablesUnformatted.remove(2);
				}
			}
			String tablesString = "";
			for (String s : tablesUnformatted) {
				tablesString += s + " ";
			}
			String[] tableIDs = null;
			if (joinType != -1) {
				String[] temp = tablesString.split(" ");
				tableIDs = new String[temp.length / 2];
				for (int i = 0, j = 0; i < temp.length; i += 2, j++) {
					tableIDs[j] = temp[i] + " " + temp[i + 1];
				}
			} else
				tableIDs = tablesString.split(", ");
			tables = new ArrayList<String[]>();
			if (tableIDs[0].contains(" ")) {
				for (String s : tableIDs) {
					s.trim();
					tables.add(s.split(" "));
				}
			} else {
				for (String s : tableIDs)
					tables.add(new String[] { s.trim() });
			}
		}
		if (tables.size() > 1) {
			multipleTables = true;
		}

		// File tbl = new File(useDirectory + "/" + parseTree.get(fromInd + 1) +
		// ".tbl");
		Scanner fileReader = null;
		try {
			for (String[] tab : tables) {
				tableReaders.add(new Scanner(new File(useDirectory + "/" + tab[0] + ".tbl")));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (!multipleTables) {
			fileReader = tableReaders.get(0);
			String schemaLine = fileReader.nextLine();
			String[] schema = schemaLine.split(" \\| ");
			String[] atts = new String[schema.length];
			for (int i = 0; i < atts.length; i++) {
				atts[i] = schema[i].split(" ")[0];
			}
			ArrayList<String> attribs = new ArrayList<>(Arrays.asList(atts));
			int[] colNums = new int[cols.size()];
			if (!getAll) {
				for (int i = 0; i < colNums.length; i++) {
					for (int j = 0; j < attribs.size(); j++) {
						if (cols.get(i).equalsIgnoreCase(attribs.get(j))) {
							colNums[i] = j;
						}
					}
				}
				for (int i = 0; i < colNums.length; i++) {
					System.out.print(schema[colNums[i]]);
					if (i != colNums.length - 1) {
						System.out.print(" | ");
					}
				}
			} else {
				for (int i = 0; i < attribs.size(); i++) {
					System.out.print(schema[i]);
					if (i != colNums.length - 1) {
						System.out.print(" | ");
					}
				}
			}
			whereInd = Math.max(parseTree.indexOf("where"), parseTree.indexOf("on"));
			String whereAtt = "";
			String whereOp = "";
			String whereParam = "";
			int whereAttInd = -1;
			int whereSchemaNum = -1;
			if (whereInd != -1) {
				whereAttInd = whereInd + 1;
				whereAtt = parseTree.get(whereAttInd);
				whereOp = parseTree.get(whereInd + 2);
				whereParam = parseTree.get(whereInd + 3);
				for (int i = 0; i < schema.length; i++) {
					if (atts[i].equalsIgnoreCase(whereAtt)) {
						whereSchemaNum = i;
					}
				}
			}
			System.out.println();
			while (fileReader.hasNextLine()) {
				String line = fileReader.nextLine();
				String[] input = line.split(" \\| ");
				if (getAll) {
					for (int c = 0; c < input.length; c++) {
						System.out.print(input[c]);
						if (c != input.length - 1) {
							System.out.print(" | ");
						}
					}
					System.out.println();
				} else {
					if (whereInd == -1) {
						for (int i = 0; i < colNums.length; i++) {
							System.out.print(input[colNums[i]]);
							if (i != colNums.length - 1) {
								System.out.print(" | ");
							}
						}
						System.out.println();
					} else {
						if (matches(input[whereSchemaNum], whereOp, whereParam)) {
							for (int i = 0; i < colNums.length; i++) {
								System.out.print(input[colNums[i]]);
								if (i != colNums.length - 1) {
									System.out.print(" | ");
								}
							}
							System.out.println();
						}
					}
				}
			}
		} else {
			ArrayList<String> schemaLines = new ArrayList<String>();
			for (Scanner s : tableReaders) {
				schemaLines.add(s.nextLine());
			}
			ArrayList<String[]> schemas = new ArrayList<String[]>();
			for (String s : schemaLines) {
				schemas.add(s.split(" \\| "));
			}
			for (int i = 0; i < schemaLines.size(); i++) {
				System.out.print(schemaLines.get(i));
				if (i != schemaLines.size() - 1) {
					System.out.print(" | ");
				}
			}

			ArrayList<String[]> atts = new ArrayList<String[]>();
			for (int i = 0; i < schemas.size(); i++) {
				atts.add(new String[schemas.get(i).length]);
			}
			ArrayList<ArrayList<String>> attribs = new ArrayList<ArrayList<String>>();
			for (int i = 0; i < atts.size(); i++) {
				ArrayList<String> temp = new ArrayList<String>();
				for (int j = 0; j < atts.get(i).length; j++) {
					atts.get(i)[j] = schemas.get(i)[j].split(" ")[0];
				}
				attribs.add(new ArrayList<>(Arrays.asList(atts.get(i))));
			}

			ArrayList<int[]> colNums = new ArrayList<int[]>();
			whereInd = Math.max(parseTree.indexOf("where"), parseTree.indexOf("on"));
			String whereAtt = "";
			String whereOp = "";
			String whereParam = "";
			int whereAttInd = -1;
			int[] schemaNumbers = new int[attribs.size()];
			if (whereInd != -1) {
				whereAttInd = whereInd + 1;
				whereAtt = parseTree.get(whereAttInd);
				// System.out.println(whereAtt);
				whereOp = parseTree.get(whereInd + 2);
				whereParam = parseTree.get(whereInd + 3);
				// System.out.println(whereParam);
				String[] whereAttWithTable = whereAtt.split("\\.");
				String[] whereParamWithTable = whereParam.split("\\.");
				// System.out.println(whereAttWithTable.length);
				int whereAttNum = -1;
				int whereParamNum = -1;
				for (int i = 0; i < tables.size(); i++) {
					String temp = tables.get(i)[1];
					if (whereAttWithTable[0].equalsIgnoreCase(temp)) {
						whereAttNum = i;
					}
					if (whereParamWithTable[0].equalsIgnoreCase(temp)) {
						whereParamNum = i;
					}
				}
				for (int i = 0; i < schemas.size(); i++) {
					if (atts.get(whereAttNum)[i].equalsIgnoreCase(whereAttWithTable[1])) {
						schemaNumbers[0] = i;
					}
				}
				for (int i = 0; i < schemas.size(); i++) {
					if (atts.get(whereParamNum)[i].equalsIgnoreCase(whereParamWithTable[1])) {
						schemaNumbers[1] = i;
					}
				}
			}
			ArrayList<Integer> indexsToSkip = new ArrayList<Integer>();
			ArrayList<ArrayList<String[]>> allInputLines = new ArrayList<ArrayList<String[]>>();
			while (indexsToSkip.size() != tableReaders.size()) {
				ArrayList<String[]> lines = new ArrayList<String[]>();
				for (int i = 0; i < tableReaders.size(); i++) {
					if (indexsToSkip.contains(i)) {
						continue;
					}
					String line = tableReaders.get(i).nextLine();
					String[] input = line.split(" \\| ");
					lines.add(input);
					if (!tableReaders.get(i).hasNextLine()) {
						indexsToSkip.add(i);
					}
				}
				allInputLines.add(lines);
			}
			// lines contains all the lines of input for each run of the code.
			// so this would be somehting like:
			// x | x | y | y or something.
			System.out.println();
			ArrayList<String> pleasePrintTheseOnly = new ArrayList<String>();
			for (int i = 0; i < allInputLines.size(); i++) {
				for (int j = i; j < allInputLines.size(); j++) {
					if (joinType == -1 || joinType == 0) {
						// only if they have a match
						if (matches(allInputLines.get(i).get(0)[schemaNumbers[0]], whereOp,
								allInputLines.get(j).get(1)[schemaNumbers[1]])) {

							for (int k = 0; k < allInputLines.get(i).get(0).length; k++) {
								System.out.print(allInputLines.get(i).get(0)[k]);
								System.out.print(" | ");
							}
							for (int k = 0; k < allInputLines.get(j).get(1).length; k++) {
								System.out.print(allInputLines.get(j).get(1)[k]);
								if (k != allInputLines.get(j).get(1).length - 1)
									System.out.print(" | ");
							}
							System.out.println();
							// left join always left, right if it has a match
						}
					} else if (joinType == 1) {
						ArrayList<String[]> toPrint = new ArrayList<String[]>();

						String[] test = new String[allInputLines.get(i).get(0).length * 2 - 1];
						for (int g = 0; g < test.length; g++) {
							if (g % 2 == 0) {
								test[g] = allInputLines.get(i).get(0)[g / 2];
							} else {
								test[g] = " | ";
							}
						}

						toPrint.add(test);
						toPrint.add(new String[] { " | " });
						if (matches(allInputLines.get(i).get(0)[schemaNumbers[0]], whereOp,
								allInputLines.get(j).get(1)[schemaNumbers[1]])) {
							test = new String[allInputLines.get(j).get(1).length * 2 - 1];
							for (int g = 0; g < test.length; g++) {
								if (g % 2 == 0) {
									test[g] = allInputLines.get(j).get(1)[g / 2];
								} else {
									test[g] = " | ";
								}
							}

							toPrint.add(test);
						}
						String temp = "";
						for (String[] sa : toPrint) {
							for (String s : sa) {
								temp += s;
							}
						}
						pleasePrintTheseOnly.add(temp);
						// right join always right, left if has a match
					} else if (joinType == 2) {

						// always both, regardless of match.
					} else if (joinType == 3) {

					}
				}

			}

			for (int i = 0; i < pleasePrintTheseOnly.size(); i++) {
				boolean printable = true;
				for (int j = 0; j < pleasePrintTheseOnly.size(); j++) {
					if (pleasePrintTheseOnly.get(j).startsWith(pleasePrintTheseOnly.get(i))
							&& pleasePrintTheseOnly.get(j).length() > pleasePrintTheseOnly.get(i).length()) {
						pleasePrintTheseOnly.remove(i);
						printable = false;
						break;
					}
				}
				if (printable)
					System.out.println(pleasePrintTheseOnly.get(i));
			}

		}
	}

	// USE <DBNAME>
	public static void use(ArrayList<String> parseTree) {
		String dirName = parseTree.remove(0);
		useDirectory = new File("/" + dirName);
		if (useDirectory.exists()) {
			System.out.println("Using Database " + dirName);
		} else {
			System.err.println("!Failed to use database " + dirName + " because it does not exist.");
		}
	}

	// ALTERS NEED AN UPDATE, IT CURRENTLY CLEARS THE CONTENTS OF THE TABLE.
	// ALTER TABLE <TBLNAME> ADD <NAME> <TYPE>
	// ALTER TABLE <TBLNAME> ADD (<NAME> <TYPE>...)
	// ALTER TABLE <TBLNAME> REMOVE <NAME>
	// ALTER TABLE <TBLNAME> UPDATE <NAME> <TYPE>
	public static void alter(ArrayList<String> parseTree) {
		// Don't alter a table if no database is selected.
		if (useDirectory == null) {
			System.err.println("!Failed to alter table because no database has been selected.");
			return;
		}
		String tbl = parseTree.remove(0);
		String tblname = parseTree.remove(0);
		String cmd = parseTree.remove(0);
		// check for proper syntax on table.
		if (!tbl.equalsIgnoreCase("table")) {
			System.err.println("!Invalid Syntax: " + tbl + " is not a valid keyword.");
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
				System.err.println("Invalid alter command");
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
			System.err.println("!Failed to alter table " + tblname + "because it does not exist.");
			return;
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
			System.err.println("!Failed to add: no arguments specified.");
		}
		// Check to see if the parse tree size is 2
		// If size = 2, then you dont need to deal with commas and parenthesis.
		if (parseTree.size() == 2) {
			String att = parseTree.remove(0);
			String type = parseTree.remove(0);
			// if the new attribute name is the same as a previous one, return
			if (atts.contains(att)) {
				System.err.println("Failed to add field " + att + " because it already exists");
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
				System.err.println("Failed to add field " + atType[0] + " because it already exists");
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
			System.err.println("!Invalid number of arguments: " + parseTree.size());
			return;
		}
		for (int i = 0; i < atts.size(); i++) {
			if (atts.get(i).equalsIgnoreCase(parseTree.get(0))) {
				atts.remove(i);
				types.remove(i);
				return;
			}
		}
		System.err.println("!Failed to remove tuple " + parseTree.get(0) + " as it does not exist.");
	}

	// ALTER TABLE <TBLNAME> UPDATE <NAME> <TYPE>
	private static void alterUpdate(ArrayList<String> atts, ArrayList<String> types, ArrayList<String> parseTree) {
		if (parseTree.size() != 2) {
			System.err.println("!Invalid number of arguements: " + parseTree.size());
		}

		for (int i = 0; i < atts.size(); i++) {
			if (atts.get(i).equalsIgnoreCase(parseTree.get(0))) {
				types.set(i, parseTree.get(1));
				return;
			}
		}
		System.err.println("!Failed to update tuple " + parseTree.get(0) + " as it does not exist.");
	}

	// INSERT INTO <TBLNAME> (c1, c2, c3...) VALUES (v1, v2, v3...);
	// INSERT INTO <TBLNAME> VALUES (v1, v2, v3...);
	public static void insert(ArrayList<String> parseTree) {
		if (useDirectory == null) {
			System.err.println("Failed to insert into table because no database has been selected.");
			return;
		}
		File file = new File(useDirectory + "/" + parseTree.get(1) + ".tbl");
		if (!file.exists()) {
			System.err.println("Failed to insert into table, as it does not exist.");
			return;
		}
		FileWriter fw = null;
		try {
			fw = new FileWriter(file, true);
			// This is the case where it is not value specifc (inserting a thing for each
			// thing).
			if (parseTree.get(2).equalsIgnoreCase("values") || parseTree.get(2).startsWith("values")) {
				parseTree.remove(0); // remove into
				parseTree.remove(0); // remove tblname
				parseTree.remove(0); // remove values

				String vals = "";
				for (String s : parseTree) {
					vals += s + " ";
				}
				parseTree.clear();
				// remove parenthesis if they are there.
				if (vals.charAt(0) == '(' && vals.charAt(vals.length() - 2) == ')') {
					vals = vals.substring(1, vals.length() - 2);
				}
				parseTree = new ArrayList<String>(Arrays.asList(vals.split(", ")));
				for (int i = 0; i < parseTree.size(); i++) {
					fw.write(parseTree.get(i));
					if (i != parseTree.size() - 1) {
						fw.write(" | ");
					}
				}
				fw.write("\n");
				fw.close();
			}
		} catch (IOException e) {

		}
		System.out.println("1 new record inserted");
	}

	// delete from <tablename> : where <attname> (> : < : >= : <= : = : !=) param
	public static void delete(ArrayList<String> parseTree) {
		int recordsDeleted = 0;
		if (parseTree.size() == 2 && parseTree.get(0).equalsIgnoreCase("from")) {
			String filename = parseTree.get(1);
			// System.out.println(parseTree);
			try {
				File file = new File(useDirectory + "/" + filename + ".tbl");
				Scanner filereader = new Scanner(file);
				String line = filereader.nextLine();

				while (filereader.hasNextLine()) {
					recordsDeleted++;
					@SuppressWarnings("unused")
					String s = filereader.nextLine();
				}
				filereader.close();
				FileWriter fw = new FileWriter(file);
				fw.write(line + "\n");
				fw.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(recordsDeleted + " records deleted.");
			return;
		}
		String filename = parseTree.remove(1); // remove <tblname>
		parseTree.remove(0); // remove "From"
		if (!parseTree.get(0).equalsIgnoreCase("where")) {
			System.out.println("!invalid syntax");
			return;
		}
		parseTree.remove(0); // remove "where"
		String att = parseTree.remove(0);
		String operator = parseTree.remove(0);
		String param = parseTree.remove(0);

		File tbl = new File(useDirectory + "/" + filename + ".tbl");
		Scanner tblReader = null;
		try {
			tblReader = new Scanner(tbl);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String schemaLine = tblReader.nextLine();
		String[] schema = schemaLine.split(" \\| ");
		String[] atts = new String[schema.length];
		for (int i = 0; i < atts.length; i++) {
			atts[i] = schema[i].split(" ")[0];
		}

		int attNum = -1;
		for (int i = 0; i < atts.length; i++) {
			if (atts[i].equalsIgnoreCase(att)) {
				attNum = i;
			}
		}
		if (attNum == -1) {
			System.out.println("!Failed to delete attribute as it does not exist");
			return;
		}
		// get all records
		ArrayList<String[]> records = new ArrayList<String[]>();
		while (tblReader.hasNextLine()) {
			String[] s = tblReader.nextLine().split(" \\| ");
			boolean deleteThisRecord = matches(s[attNum], operator, param);
			if (deleteThisRecord) {
				recordsDeleted++;
				continue;
			}
			records.add(s);
		}
		try {
			FileWriter fw = new FileWriter(tbl);
			fw.write(schemaLine + "\n");
			for (String[] s : records) {
				for (int i = 0; i < s.length; i++) {
					fw.write(s[i]);
					if (i != s.length - 1) {
						fw.write(" | ");
					}
				}
				if (s != records.get(records.size() - 1)) {
					fw.write("\n");
				}
			}
			fw.write("\n");
			fw.close();
			System.out.println(recordsDeleted + " records deleted.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	// this file tests to see if two things match

	// takes in the testString, operator, and paramater to match with.
	private static boolean matches(String string, String operator, String param) {
		// System.out.println("> " + string + " : " + operator + " : " + param + " <");

		String returnString = null;
		boolean numeric = true;
		float strasfloat = 0.0f;
		float paramasfloat = 0.0f;
		try {
			strasfloat = Float.parseFloat(string);
			paramasfloat = Float.parseFloat(param);
		} catch (NumberFormatException e) {
			numeric = false;
		}

		switch (operator) {
		case ">": // greater than
			if (numeric) {
				returnString = (strasfloat > paramasfloat ? "null" : string);
			} else {
				System.out.println("> cannot be evaluated as a string");
			}
			break;
		case "<": // less than
			if (numeric) {
				returnString = (strasfloat < paramasfloat ? "null" : string);
			} else {
				System.out.println("< cannot be evaluated as a string");
			}
			break;
		case ">=": // greater than or equal to
			if (numeric) {
				returnString = (strasfloat < paramasfloat ? string : "null");
			} else {
				System.out.println(">= cannot be evaluated as a string");
			}
			break;
		case "<=": // less than or equal to
			if (numeric) {
				returnString = (strasfloat > paramasfloat ? string : "null");
			} else {
				System.out.println("<= cannot be evaluated as a string");
			}
			break;
		case "=": // equal to
			if (numeric) {
				returnString = (strasfloat != paramasfloat ? string : "null");
			} else {
				returnString = (string.equalsIgnoreCase(param) ? "null" : string);
			}
			break;
		case "!=": // not equal to.
			if (numeric) {
				returnString = (strasfloat == paramasfloat ? string : "null");
			} else {
				returnString = (string.equalsIgnoreCase(param) ? string : "null");
			}
			break;
		}
		return returnString == "null" ? true : false;
	}

	// update <tblname> set <att> = <param> where <att2> = <param2>
	public static void update(ArrayList<String> parseTree) {
		int recordsModified = 0;
		String tblname = parseTree.remove(0); // save and remove table name from parsetree.
		if (!parseTree.get(0).equalsIgnoreCase("set") || !parseTree.get(4).equalsIgnoreCase("where")) {
			System.err.println("Invalid Syntax");
			return;
		}
		File tbl = new File(useDirectory + "/" + tblname + ".tbl");
		Scanner tblReader = null;
		try {
			tblReader = new Scanner(tbl);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String schemaLine = tblReader.nextLine();
		String[] schema = schemaLine.split(" \\| ");
		String[] atts = new String[schema.length];
		for (int i = 0; i < atts.length; i++) {
			atts[i] = schema[i].split(" ")[0];
		}

		String setAtt = parseTree.get(1);
		String setParam = parseTree.get(3);
		String whereAtt = parseTree.get(5);
		String whereParam = parseTree.get(7);
		parseTree.clear();// memory cleanup.

		int setAttNum = -1;
		int whereAttNum = -1;
		for (int i = 0; i < atts.length; i++) {
			if (atts[i].equalsIgnoreCase(setAtt)) {
				setAttNum = i;
			}
			if (atts[i].equalsIgnoreCase(whereAtt)) {
				whereAttNum = i;
			}
		}
		if (setAttNum == -1 || whereAttNum == -1) {
			System.err.println("!Failed to update attribute because selected attribute doesnt exist.");
			return;
		}

		ArrayList<String[]> records = new ArrayList<String[]>();

		while (tblReader.hasNextLine()) {
			String[] input = tblReader.nextLine().split(" \\| ");
			if (input[whereAttNum].equalsIgnoreCase(whereParam)) {
				input[setAttNum] = setParam;
				recordsModified++;
			}
			records.add(input);
		}
		tblReader.close();
		try {
			FileWriter fw = new FileWriter(tbl);
			fw.write(schemaLine + "\n");
			for (String[] s : records) {
				for (int i = 0; i < s.length; i++) {
					fw.write(s[i]);
					if (i != s.length - 1) {
						fw.write(" | ");
					}
				}
				if (s != records.get(records.size() - 1)) {
					fw.write("\n");
				}
			}
			fw.write("\n");
			fw.close();
			System.out.println(recordsModified + " records modified.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void commit() {
		// TODO Auto-generated method stub

	}

	public static void beginTransaction() {
		// TODO Auto-generated method stub

	}

}
