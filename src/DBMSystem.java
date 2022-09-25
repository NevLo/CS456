import java.util.ArrayList;
import java.util.Arrays;

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
		parse("DROP DATABASE db_1;");
		parse("DROP DATABASE db_2;");
		System.out.println("\n\n\n");
		parse("CREATE DATABASE db_1;");
		parse("CREATE DATABASE db_1;");
		parse("CREATE DATABASE db_2;");
		parse("DROP DATABASE db_2;");
		parse("DROP DATABASE db_2;");
		parse("CREATE DATABASE db_2;");
		parse("USE db_1;");
		parse("CREATE TABLE tbl_1 (a1 int, a2 varchar(20));");
		parse("CREATE TABLE tbl_1 (a3 float, a4 char(20));");
		parse("DROP TABLE tbl_1;");
		parse("DROP TABLE tbl_1;");
		parse("CREATE TABLE tbl_1 (a1 int, a2 varchar(20));");
		parse("SELECT * FROM tbl_1;");
		parse("ALTER TABLE tbl_1 ADD a3 float;");
		parse("SELECT * FROM tbl_1;");
		parse("CREATE TABLE tbl_2 (a3 float, a4 char(20));");
		parse("SELECT * FROM tbl_2;");
		parse("USE db_2;");
		parse("SELECT * FROM tbl_1;");
		parse("CREATE TABLE tbl_1 (a3 float, a4 char(20));");
		parse("SELECT * FROM tbl_1;");
		parse("ALTER TABLE tbl_1 UPDATE a5 int");
		parse("SELECT * FROM tbl_1;");
		parse(".EXIT");
		/*
		 * -- Expected output
		 * -- Database db_1 created.
		 * -- !Failed to create database db_1 because it already exists.
		 * -- Database db_2 created.
		 * -- Database db_2 deleted.
		 * -- !Failed to delete db_2 because it does not exist.
		 * -- Database db_2 created.
		 * -- Using database db_1.
		 * -- Table tbl_1 created.
		 * -- !Failed to create table tbl_1 because it already exists.
		 * -- Table tbl_1 deleted.
		 * -- !Failed to delete tbl_1 because it does not exist.
		 * -- Table tbl_1 created.
		 * -- a1 int | a2 varchar(20)
		 * -- Table tbl_1 modified.
		 * -- a1 int | a2 varchar(20) | a3 float
		 * -- Table tbl_2 created.
		 * -- a3 float | a4 char(20)
		 * -- Using Database db_2.
		 * -- !Failed to query table tbl_1 because it does not exist.
		 * -- Table tbl_1 created.
		 * -- a3 float | a4 char(20)
		 * -- All done.
		 */
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
		// create an arraylist of the substrings.
		ArrayList<String> parseTree = new ArrayList<String>(Arrays.asList(lineToParse.split(" ")));
		// check to see if the cmd is an appropriate valid.
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
		else
			System.out.println("!Command Not Recognized!");
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

		} else if (cmd.equalsIgnoreCase("mode")) {

		} else if (cmd.equalsIgnoreCase("nullValue")) {

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
 * | * * * * |
 * | 9/21/22 |
 * | * * * * |
 * |
 */
