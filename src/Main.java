import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/*
 * DBMS Project
 * UNR CS457 Database Management
 * 
 * Author: Christian Pilley
 * Project Creation date: 9/17/22
 * 
 * Part 1: Metadata Management (Due: 10/10/2022)
 * 	-Database Creation
 *  -Database Deletion
 * 	-Table Creation
 *  -Table Deletion
 *  -Table Update
 *  -Table Query
 * Part 2: Basic Data Manipulation (Due: 10/31/2022)
 *  -Tuple Creation
 *  -Tuple Deletion
 *  -Tuple Update
 *  -Tuple Query
 * Part 3: Table Joins (Due: 11/21/2022)
 *  -Inner Join
 *  -Left Outer Join
 *  -Right Outer Join
 *  -Full Outer Join
 * Part 4: Transactions (Due: 12/15/2022)
 *  -File Locking
 *  -File Commiting
 * Part 5: Aggregate Queries (Due: 12/15/2022) (BONUS)
 *  -COUNT
 *  -AVG
 *  -MAX
 */
public class Main {
	public static void main(String[] args) throws FileNotFoundException {
		System.out.println("DBMS system launched.\n");
		System.out.println(args.length + "\n");
		if (args.length == 1) {
			if (!args[0].endsWith(".sql")) {
				args[0] += ".sql";
			}
			Scanner scan = new Scanner(new File(args[0]));
			while (scan.hasNext()) {
				String s = scan.nextLine();
				// System.out.println(s);
				parse(s, scan);
			}

			scan.close();
			return;
		}
		Scanner inputStream = new Scanner(System.in);
		while (inputStream.hasNext()) {
			String input = inputStream.nextLine();
			parse(input, inputStream);
		}

		inputStream.close();
	}

	/*
	 * SQL parser.
	 * 
	 */
	public static void parse(String lineToParse, Scanner scan) {
		// check to see if the line is a comment, if so return (does not support multi
		// line comments yet)
		if (lineToParse.startsWith("--") || lineToParse.equalsIgnoreCase(""))
			return;
		// see if the line is a command (.exit, .header, etc)
		if (lineToParse.startsWith(".")) {
			parseCMD(lineToParse.substring(1));
			return;
		}
		// check to see if there is no semicolon (syntax error)
		if (lineToParse.indexOf(';') == -1) {
			if (lineToParse.endsWith(" ")) {
				lineToParse += parseMultiLine(scan);
			} else {
				lineToParse += " " + parseMultiLine(scan);
			}
			// System.out.println("!Invalid Syntax, missing ';'");
			// return;
		}
		// see if the line has multiple commands in it (CREATE DATABASE DB_1; USE DB_1;)
		// this is done because String.indexOf() checks for the first instance of a
		// character.
		if (lineToParse.indexOf(";") != lineToParse.length() - 1) {
			// breaks the string down into multiple at the location of semicolons.
			String[] listOfLines = lineToParse.split(";");
			// parse the lines (adding a semicolon because it was removed in the split)
			for (String s : listOfLines) {
				parse(s + ";", scan);
			}
		}
		if (lineToParse.indexOf(";") != -1) {
			lineToParse = lineToParse.substring(0, lineToParse.length() - 1);
		}
		// create an arraylist of the substrings.
		ArrayList<String> parseTree = new ArrayList<String>(Arrays.asList(lineToParse.split(" ")));
		for (String str : parseTree) {
			str = str.toLowerCase();
		}
		// System.out.println(parseTree);
		// check to see if the cmd is valid.
		String CMD = parseTree.remove(0);
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
		else if (CMD.equalsIgnoreCase("insert"))
			DBMS.insert(parseTree);
		else if (CMD.equalsIgnoreCase("delete"))
			DBMS.delete(parseTree);
		else if (CMD.equalsIgnoreCase("update"))
			DBMS.update(parseTree);
		else if (CMD.equalsIgnoreCase("commit"))
			DBMS.commit();
		else if (CMD.equalsIgnoreCase("begin"))
			DBMS.beginTransaction();
		else
			System.out.println("!Command Not Recognized!");
		// Just to make sure memory gets clear properly.
		parseTree.clear();
	}

	private static String parseMultiLine(Scanner s) {
		String line = s.nextLine();
		// System.out.println(line);
		if (line.indexOf(';') == -1) {
			if (line.endsWith(" "))
				return line + parseMultiLine(s);
			return line + " " + parseMultiLine(s);
		}
		return line;
	}

	/*
	 * SQL Command Parser
	 * 
	 * 
	 */
	public static void parseCMD(String lineToParse) {
		// TODO Auto-generated method stub
		String[] splits = lineToParse.split(" ");
		String cmd = splits[0];
		if (cmd.equalsIgnoreCase("exit")) {
			System.out.println("All done.");
			System.exit(0);
		} else if (cmd.equalsIgnoreCase("header")) {
			System.out.println("!Command not implemented!");
		} else if (cmd.equalsIgnoreCase("mode")) {
			System.out.println("!Command not implemented!");
		} else if (cmd.equalsIgnoreCase("nullValue")) {
			System.out.println("!Command not implemented!");
		}
	}
}
/*
 * Change Log:
 * 
 * | * * * * | + Created project.
 * | 9/17/22 | + Created parse method.
 * | * * * * | + Created parseCMD method.
 * + Added Method Stubs for Create,Drop,Select,Alter,Use.
 * + Worked on Use and Create implementation.
 * ------------------------------------------------------------------
 * | * * * * | + Finished parseCMD method
 * | 9/24/22 |
 * | * * * * |
 * -----------------------------------------------------------------
 * | * * * * | + Added method stub for insert.
 * | 9/29/22 |
 * | * * * * |
 * ------------------------------------------------------------------
 * | * * * * | + Added parseMultiLine
 * | 11/3/22 | + Added method stubs for update and delete.
 * | * * * * |
 * ------------------------------------------------------------------
 * | * * * * | + Added method stub for begin transaction
 * | 12/2/22 | + Added method stub for commit
 * | * * * * |
 */
