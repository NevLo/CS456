import java.util.ArrayList;
import java.util.Arrays;

/*
 * DBMS Project
 * UNR CS457 Database Management
 * 
 * Author: Christian Pilley
 * Project Creation date: 9/17/22
 * 
 * Part 1: Metadata Management (Due: __/__/2022)
 * 	-Database Creation
 *  -Database Deletion
 * 	-Table Creation
 *  -Table Deletion
 *  -Table Update
 *  -Table Query
 * Part 2: __ (Due: __/__/2022)
 * Part 3: __ (Due: __/__/2022)
 * Part 4: __ (Due: __/__/2022)
 */
public class DBMSystem {
	private static boolean isExternalFileInUse;

	public static void main(String[] args) {
		if (args.length == 1) {
			isExternalFileInUse = true;

		}

		parse("CREATE DATABASE test_DB;");
		parse("USE test_DB;");
	}

	/*
	 * SQL parser.
	 * 
	 */
	public static void parse(String lineToParse) {
		// check to see if the line is a comment, if so return (does not support multi
		// line comments yet)

		if (lineToParse.startsWith("--"))
			return;
		// see if the line is a command (.exit, .header, etc)
		if (lineToParse.startsWith(".")) {
			parseCMD(lineToParse.substring(1));
			return;
		}
		// see if the line has multiple commands in it (CREATE DATABASE DB_1; USE DB_1;)
		if (lineToParse.indexOf(";") != lineToParse.length() - 1) {

		} else {
			lineToParse = lineToParse.substring(0, lineToParse.length() - 1);
		}

		// create an arraylist of the substrings
		ArrayList<String> parseTree = new ArrayList<String>(Arrays.asList(lineToParse.split(" ")));
		String CMD = parseTree.get(0);
		parseTree.remove(0);

		if (CMD.equalsIgnoreCase("create"))
			DBMS.create(parseTree);
		else if (CMD.equalsIgnoreCase("drop"))
			DBMS.drop(parseTree);
		else if (CMD.equalsIgnoreCase("select"))
			DBMS.select(parseTree);
		else if (CMD.equalsIgnoreCase("use"))
			DBMS.use(parseTree);
		else if (CMD.equalsIgnoreCase("alter"))
			DBMS.alter(parseTree);
		else
			System.out.println("!Command Not Recognized!");
	}

	public static void parseCMD(String lineToParse) {
		// TODO Auto-generated method stub

	}
}
/*
 * Change Log: * * * * * 09/17/22 * * * * * * ,
 * 
 * .
 */
