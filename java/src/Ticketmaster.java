/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.math.BigInteger;

import java.sql.Time;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class Ticketmaster{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public Ticketmaster(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + Ticketmaster.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		Ticketmaster esql = null;
		
		try{
			System.out.println("(1)");
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new Ticketmaster (dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add User");
				System.out.println("2. Add Booking");
				System.out.println("3. Add Movie Showing for an Existing Theater");
				System.out.println("4. Cancel Pending Bookings");
				System.out.println("5. Change Seats Reserved for a Booking");
				System.out.println("6. Remove a Payment");
				System.out.println("7. Clear Cancelled Bookings");
				System.out.println("8. Remove Shows on a Given Date");
				System.out.println("9. List all Theaters in a Cinema Playing a Given Show");
				System.out.println("10. List all Shows that Start at a Given Time and Date");
				System.out.println("11. List Movie Titles Containing \"love\" Released After 2010");
				System.out.println("12. List the First Name, Last Name, and Email of Users with a Pending Booking");
				System.out.println("13. List the Title, Duration, Date, and Time of Shows Playing a Given Movie at a Given Cinema During a Date Range");
				System.out.println("14. List the Movie Title, Show Date & Start Time, Theater Name, and Cinema Seat Number for all Bookings of a Given User");
				System.out.println("15. EXIT");
				
				/*
				 * FOLLOW THE SPECIFICATION IN THE PROJECT DESCRIPTION
				 */
				switch (readChoice()){
					case 1: AddUser(esql); break;
					case 2: AddBooking(esql); break;
					case 3: AddMovieShowingToTheater(esql); break;
					case 4: CancelPendingBookings(esql); break;
					case 5: ChangeSeatsForBooking(esql); break;
					case 6: RemovePayment(esql); break;
					case 7: ClearCancelledBookings(esql); break;
					case 8: RemoveShowsOnDate(esql); break;
					case 9: ListTheatersPlayingShow(esql); break;
					case 10: ListShowsStartingOnTimeAndDate(esql); break;
					case 11: ListMovieTitlesContainingLoveReleasedAfter2010(esql); break;
					case 12: ListUsersWithPendingBooking(esql); break;
					case 13: ListMovieAndShowInfoAtCinemaInDateRange(esql); break;
					case 14: ListBookingInfoForUser(esql); break;
					case 15: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}
	// end readChoice
	
	//Ticketmaster esql is a java object

	public static void AddUser(Ticketmaster esql){//1
		// insert tuple into database
		
		//gather data
		String email;
		String lname;
		String fname;
		long phone;
		String pwd;

		do{
			System.out.println("Email: ");
			try {
				email = in.readLine();
				if(email.length() > 64 || email.length() == 0)  {
					throw new ArithmeticException("Email cannot be empty and has to be less 64 characters or less.");
				}
				else {
					break;
				}
			} catch(Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		} while(true);

		do{
			System.out.println("Last name: ");
			try {
				lname = in.readLine();
				if(lname.length() > 32 || lname.length() == 0)  {
					throw new ArithmeticException("Last name cannot be empty and has 32 characters or less.");
				}
				else {
					break;
				}
			} catch(Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		} while(true);

		do{
			System.out.println("First name: ");
			try {
				fname = in.readLine();
				if(fname.length() > 32 || fname.length() == 0)  {
					throw new ArithmeticException("First name cannot be empty and has to be 32 characters or less.");
				}
				else {
					break;
				}
			} catch(Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		} while(true);

		do{
			System.out.println("Phone number: ");
			try {
				phone = Long.parseLong(in.readLine());
				if(phone > 9999999999L || phone < 0) {
					throw new ArithmeticException("Phone number cannot be empty and has to be 10 digits or less.");
				}
				else {
					break;
				}
			} catch(Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		} while(true);

		do{
			System.out.println("Password: ");
			try {
				pwd = in.readLine();
				if(pwd.length() > 64 || pwd.length() == 0) {
					throw new ArithmeticException("Password cannot be empty and has to be 64 characters or less.");
				}
				else {
					break;
				}
			} catch(Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		} while(true);

		//insert into table
		try {
			String query = "INSERT INTO Users (email, lname, fname, phone, pwd) VALUES ('" + email + "', '" + lname + "', '" + fname + "', '" + phone + "', '" + pwd + "');";
			esql.executeUpdate(query);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	public static void AddBooking(Ticketmaster esql){//2

		String user_email;
		do{
			System.out.println("Enter the customer's email: ");
			try {
				user_email = in.readLine();
				if(user_email.length() > 64 || user_email.length() == 0)  {
					throw new ArithmeticException("Email cannot be empty and has to be less than 64 characters.");
				}
				else {
					break;
				}

			} catch(Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		} while(true);

		//insert into table
		try {
			//String query_user = "SELECT *\n FROM Users\n WHERE email = + user_email;
			String query_user = "SELECT *\n FROM Users\n WHERE email = '" + user_email + "';";
			if (esql.executeQuery(query_user) == 0) {
				System.out.println("This user does not exist");
				AddUser(esql); // no user found, so add user
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		//USER EXISTS, SO WE CAN CREATE A BOOKING


		String movie; // MOVIE
		do{
			System.out.println("Which movie does the customer want to watch?: ");
			try {
				movie = in.readLine();
				if(movie.length() > 128 || movie.length() == 0)  {
					throw new RuntimeException("Movie cannot be empty and has to be less than 128 characters.");
				}
				else {
					break;
				}

			} catch(Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		} while(true);

		List<List<String>> movie_id_list = new ArrayList<List<String>>();

		try {
			//String query_user = "SELECT *\n FROM Users\n WHERE email = + user_email;
			String query_mvid = "SELECT mvid\n FROM Movies\n WHERE title = '" + movie + "';";
			movie_id_list = esql.executeQueryAndReturnResult(query_mvid);
			esql.executeQueryAndPrintResult(query_mvid);

			if (movie_id_list.size() == 0) {
				System.out.println("This movie does not exist");
				//AddMovieShowingToTheater(esql); 
				System.exit(0);
				//movie doesn't exist, so add movie or choose another
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		String item = movie_id_list.get(0).get(0);
		Integer mvid = Integer.parseInt(item);
		//System.out.println(mvid);

		//NOW WE HAVE THE MOVIE ID

		String date = "";
		String time = "";
		
		do{
			try {
				System.out.println("Which day does the customer want to attend the show?: ");
				date = in.readLine();
				System.out.println("What time does the customer want to attend the show?: ");
				time = in.readLine();
				if((date.length() > 10 || date.length() == 0) || (time.length() > 8 || time.length() == 0))  {
					throw new RuntimeException("Date cannot be more than 10 characters and time cannot be more than 8 characters");
				}
				else {
					break;
				}

			} catch(Exception e) {
				System.out.println("Your input is invalid!");
				System.exit(0); // we are not implementing sophisticated error checking
				continue;
			}
		} while(true);

		List<List<String>> date_time = new ArrayList<List<String>>();

		try {
			//String query_user = "SELECT *\n FROM Users\n WHERE email = + user_email;
			String query_mvid = "SELECT sid\n FROM Shows\n WHERE mvid = '" + mvid + "'and sdate= '" + date + 
								"'and sttime = '" + time + "';";

			date_time = esql.executeQueryAndReturnResult(query_mvid);
			esql.executeQueryAndPrintResult(query_mvid);

			if (date_time.size() == 0) {
				System.out.println("This does not exist"); 
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		String item1 = date_time.get(0).get(0);
		Integer sid = Integer.parseInt(item1);

		//NOW WE KNOW THE SHOW ID THE CUSTOMER WANTS TO ATTEND


		List<List<String>> max_seats = new ArrayList<List<String>>(); //want to find out maximum number customer can reserve

		try {
			//String query_user = "SELECT *\n FROM Users\n WHERE email = + user_email;
			String query_max_num_seats = "SELECT COUNT(sid)\n FROM Showseats\n WHERE sid = '" + sid + "' and bid IS NULL OR bid = null;";

			max_seats = esql.executeQueryAndReturnResult(query_max_num_seats);
			esql.executeQueryAndPrintResult(query_max_num_seats);

			if (max_seats.size() == 0) {
				System.out.println("There are no more seats for this show");
				System.exit(0); 
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		String item2 = max_seats.get(0).get(0);
		Integer max_possible_seats = Integer.parseInt(item2);
		//System.out.println("Max Possible seats " + max_possible_seats);


		Integer seat_no; 
		do{
			try {
				System.out.println("How many seats does the customer want to book?: ");
				seat_no = Integer.parseInt(in.readLine());
				if(seat_no > max_possible_seats || seat_no <= 0)  {
					throw new RuntimeException("There are only " + max_possible_seats + " seats available for this show.");
				}
				else {
					break;
				}

			} catch(Exception e) {
				System.out.println("Your input is invalid!");
				System.exit(0); // we are not implementing sophisticated error checking
				continue;
			}
		} while(true);

		// NOW WE KNOW HOW MANY SEATS TO BOOK FOR THE CUSTOMER

		List<List<String>> show_seat_ids = new ArrayList<List<String>>(); //want to find out maximum number customer can reserve

		//The following code gives us a list of the ssids that we will need to update
		try {
			//String query_user = "SELECT *\n FROM Users\n WHERE email = + user_email;
			String query_show_seat_id = "SELECT ssid\n FROM Showseats\n WHERE sid = '" + sid + "' and bid IS NULL OR bid = null LIMIT '" + seat_no + "';";

			show_seat_ids = esql.executeQueryAndReturnResult(query_show_seat_id);
			esql.executeQueryAndPrintResult(query_show_seat_id);

			if (show_seat_ids.size() == 0) {
				System.out.println("This does not exist"); 
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		String status = "Pending"; //START CREATING THE BOOKING

		ZonedDateTime zone_date_time = ZonedDateTime.now();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ssx");
		
		String zdt = dtf.format(zone_date_time);

		List<List<String>> booking_id_list = new ArrayList<List<String>>(); //want to find out maximum number customer can reserve

		try {
			//String query_user = "SELECT *\n FROM Users\n WHERE email = + user_email;
			String booking_id_query = "SELECT max(bid) from bookings";

			booking_id_list = esql.executeQueryAndReturnResult(booking_id_query);
			esql.executeQueryAndPrintResult(booking_id_query);

			if (booking_id_list.size() == 0) {
				System.out.println("This does not exist"); 
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		Integer booking_id = Integer.parseInt(booking_id_list.get(0).get(0)) + 1;
		System.out.println(booking_id);
		//System.exit(0);
		//Insert Booking into table

		try {
			String query = "INSERT INTO Bookings (bid, status, bdatetime, seats, sid, email) VALUES ('" + booking_id + "', '" + status + "', '" + zdt
							 + "', '" + seat_no + "', '" + sid + "', '" + user_email + "');";
			esql.executeUpdate(query);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}


		//Update the bid for the seats that the customer has reserved
		for (int i = 0; i < show_seat_ids.size(); ++i) {
			//show_seat_ids.get(0).get(i)
			//System.out.println("Iteration" + i);
			try {
				String query = "UPDATE Showseats SET bid = '" + booking_id + "' WHERE ssid='" + Integer.parseInt(show_seat_ids.get(i).get(0)) + "';";
				esql.executeUpdate(query);
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}


	}
	
	public static void AddMovieShowingToTheater(Ticketmaster esql){//3
		/*takes input of the information of a new movie (i.e. title, duration) and 
		show(i.e. start time) and checks if the provided information is valid based 
		on the constraints of the database schema.*/
		//create movie
		int mvid;
		String title;
		String rdate;
		String country;
		String description = "";
		int duration = 0;
		String lang = "";
		String genre = "";

		do{
			System.out.println("Movie ID: ");
			try {
				mvid = Integer.parseInt(in.readLine());
				break;
			} catch(Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		} while(true);

		do{
			System.out.println("Title of movie: ");
			try {
				title = in.readLine();
				if(title.length() == 0 || title.length() > 128)  {
					throw new ArithmeticException("Title cannot be empty and has to be less than 129 characters.");
				}
				else {
					break;
				}
			} catch(Exception e) {
				System.out.println("Your input is invalid!");
					continue;
			}
		} while(true);

		do{
			System.out.println("Release Date(M/D/YYYY): ");
			try {
				rdate = in.readLine();
				break;
			} catch(Exception e) {
				System.out.println("Your input is invalid!");
					continue;
			}
		} while(true);

		do{
			System.out.println("Country of movie: ");
			try {
				country = in.readLine();
				if(title.length() == 0 || title.length() > 64)  {
					throw new ArithmeticException("Country of movie cannot be empty and has to be less than 65 characters.");
				}
				else {
					break;
				}
			} catch(Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		} while(true);
		
		System.out.println("Would you like to enter a description for this movie? (Y/N)");
		String answer;
		try {
			answer = in.readLine();
			if(answer.equals("N")) {
				description = "";
			}
			else { //just assume they say Y and not other characaters
				System.out.println("Enter description: ");
				try {
					description = in.readLine();
				} catch(Exception e) {
					System.out.println("Your input is invalid!");
				}
			}
	
		} catch(Exception e) {
			System.out.println("Your input is invalid!");
		}

		System.out.println("Would you like to enter the duration for this movie? (Y/N)");
		try {
			answer = in.readLine();
			if(answer.equals("N")) {
				duration = 0;
			}
			else { //just assume they say Y and not other characaters
				do {
					System.out.println("Enter duration in seconds: ");
					try {
						duration = Integer.parseInt(in.readLine());
						break;
					} catch(Exception e) {
						System.out.println("Your input is invalid!");
						continue;
					}
				} while(true);
			}
	
		} catch(Exception e) {
			System.out.println("Your input is invalid!");
		}

		System.out.println("Would you like to enter the language for this movie? (Y/N)");
		try {
			answer = in.readLine();
			if(answer.equals("N")) {
				lang = "";
			}
			else { //just assume they say Y and not other characaters
				do {
					System.out.println("Enter language using 2 characters (i.e. en for english): ");
					try {
						lang = in.readLine();
						if(lang.length() > 2) {
							throw new ArithmeticException("Language cannot be more than 2 characters");
						}
						else {
							break;
						}
					} catch(Exception e) {
						System.out.println("Your input is invalid!");
						continue;
					}
				} while(true);
			}
	
		} catch(Exception e) {
			System.out.println("Your input is invalid!");
		}

		System.out.println("Would you like to enter the genre for this movie? (Y/N)");
		try {
			answer = in.readLine();
			if(answer.equals("N")) {
				genre = "";
			}
			else { //just assume they say Y and not other characaters
				do {
					System.out.println("Genre: ");
					try {
						genre = in.readLine();
						if(genre.length() > 16) {
							throw new ArithmeticException("Genre cannot be more than 16 characters");
						}
						else {
							break;
						}
					} catch(Exception e) {
						System.out.println("Your input is invalid!");
						continue;
					}
				} while(true);
			}
		} catch(Exception e) {
			System.out.println("Your input is invalid!");
		}

		try {
			String queryOne = "INSERT INTO Movies (mvid, title, rdate, country, description, duration, lang, genre) VALUES ('" + mvid + "', '" + title + "', '" + rdate + "', '" + country + "', '" + description + "', '" + duration + "', '" + lang + "', '" + genre + "');";
			esql.executeUpdate(queryOne);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		//create show
		int sid;
		String sdate;
		String sttime;
		String edtime;

		do{
			System.out.println("Show ID: ");
			try {
				sid = Integer.parseInt(in.readLine());
				break;
			} catch(Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		} while(true);

		do{
			System.out.println("Show date(M/D/YYYY): ");
			try {
				sdate = in.readLine();
				break;
			} catch(Exception e) {
				System.out.println("Your input is invalid!");
					continue;
			}
		} while(true);

		do{
			System.out.println("Start time in military time(HH:MM): ");
			try {
				sttime = in.readLine();
				break;
			} catch(Exception e) {
				System.out.println("Your input is invalid!");
					continue;
			}
		} while(true);

		do{
			System.out.println("End time also in miltary time(HH:MM): ");
			try {
				edtime = in.readLine();
				break;
			} catch(Exception e) {
				System.out.println("Your input is invalid!");
					continue;
			}
		} while(true);

		try {
			String queryTwo = "INSERT INTO Shows (sid, mvid, sdate, sttime, edtime) VALUES ('" + sid + "', '" + mvid + "', '" + sdate + "', '" + sttime + "', '" + edtime + "');";
			esql.executeUpdate(queryTwo);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		//connect show and theater
		int tid;
		do{
			System.out.println("Enter theater ID you want to add to: ");
			try {
				tid = Integer.parseInt(in.readLine());
				break;
			} catch(Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		} while(true);

		try {
			String queryThree = "INSERT INTO Plays (sid, tid) VALUES ('" + sid + "', '" + tid + "');";
			esql.executeUpdate(queryThree);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

	}
	
	public static void CancelPendingBookings(Ticketmaster esql){//4

		try {
			//String query_user = "SELECT *\n FROM Users\n WHERE email = + user_email;
			String query_pending_bid = "UPDATE Bookings\n SET status = 'canceled' WHERE status = 'Pending'";

			esql.executeQuery(query_pending_bid);
			//esql.executeQueryAndPrintResult(query_pending_bid);
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		System.out.println("PENDING BOOKINGS SET TO CANCELLED");
	}
	
	public static void ChangeSeatsForBooking(Ticketmaster esql) throws Exception{//5

		//First, get the booking id.
		//Second, get the price of the booking (SELECT sum(price) FROM Showseats WHERE bid=501)
		//Third, use limit of number of seats of booking to find available ssid. Add up the price to see if same.
		//Make sure to save in a list the ssid.

		//If no seats available or seats more expensive, exit the function
		//If the above requirements are satisfied delete the bid from the linked showseats (Don't delete number of seats)
		//Now, use the saved list to update the bid in showseats

		Long booking_id;
		do{
			System.out.println("Enter booking ID: ");
			try {
				booking_id = Long.parseLong(in.readLine());
				if(booking_id > 9999999999L || booking_id == 0)  {
					throw new ArithmeticException("Booking ID cannot be more than 9 characters");
				}
				else {
					break;
				}

			} catch(Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		} while(true);

		List<List<String>> booking_list = new ArrayList<List<String>>();

		try {
			String query_seats_booked = "SELECT seats, sid\n FROM Bookings\n WHERE bid = '" + booking_id + "';";
			
			booking_list = esql.executeQueryAndReturnResult(query_seats_booked);
			esql.executeQueryAndPrintResult(query_seats_booked);

			if (booking_list.size() == 0) {
				System.out.println("There are no seats booked");
				return; 
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		//See how many seats
		Integer total_seats_booked = Integer.parseInt(booking_list.get(0).get(0));
		System.out.println("This booking contains " + total_seats_booked + " seats");

		Integer show_id = Integer.parseInt(booking_list.get(0).get(1));
		System.out.println("The show ID is: " + show_id);

		Integer change_seat_no;
		do{
			System.out.println("How many seats would the customer like to change?: ");
			try {
				change_seat_no = Integer.parseInt(in.readLine());
				if(change_seat_no > total_seats_booked || change_seat_no == 0)  {
					throw new ArithmeticException("You cannot change more seats than you have booked.");
				}
				else {
					break;
				}

			} catch(Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		} while(true);
		
		List<List<String>> price_query = new ArrayList<List<String>>(); 

		try {
			String query_sum_price = "SELECT sum(PRICE) from Showseats WHERE bid = '" + booking_id + "';";

			price_query = esql.executeQueryAndReturnResult(query_sum_price);
			//esql.executeQueryAndPrintResult(query_sum_price);

			if (price_query.size() == 0) {
				System.out.println("This does not exist"); 
				return;
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		Integer total_booking_price = Integer.parseInt(price_query.get(0).get(0));
		//This is the total price of the booking in question

		List<List<String>> new_price_list = new ArrayList<List<String>>(); 

		try {
			String query_new_prices = "SELECT sum(price) FROM (SELECT price FROM Showseats WHERE sid = '" + show_id + 
									"' and bid IS NULL OR bid=null LIMIT " + change_seat_no + " ) ALIAS;";

			new_price_list = esql.executeQueryAndReturnResult(query_new_prices);
			esql.executeQueryAndPrintResult(query_new_prices);

			if (new_price_list.size() == 0) {
				System.out.println("This does not exist"); 
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		Integer new_price_total = Integer.parseInt(new_price_list.get(0).get(0));

		if (new_price_total >= total_booking_price) { //No changes are made if we execute this statement
			System.out.println("The customer's seats are the cheapest possible for this show.");
			return;
		}
		//We delete the current bookings
		//Now we have to actually input the ids.

		//String query_show_seat_id = "SELECT ssid\n FROM Showseats\n WHERE sid = '" + sid + "' and bid IS NULL OR bid = null LIMIT '" + seat_no + "';";


		List<List<String>> new_ssid_list = new ArrayList<List<String>>(); 

		try {
			String query_new_ssid = "SELECT ssid from Showseats WHERE sid = '" + show_id + "'and bid IS NULL OR bid=null LIMIT " + change_seat_no + ";";
			new_ssid_list = esql.executeQueryAndReturnResult(query_new_ssid);
			esql.executeQueryAndPrintResult(query_new_ssid);

			if (price_query.size() == 0) {
				System.out.println("This does not exist"); 
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		//We have the new ssids stored. Now we can set the old ssids to bid=null

		try {
			String query_pending_bid = "UPDATE Showseats\n SET bid = null WHERE bid = '" + booking_id + "';";

			esql.executeQuery(query_pending_bid);
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		System.out.println("The size of the ssid_list is: " + new_ssid_list.size());

		// System.out.println(Integer.parseInt(new_ssid_list.get(0).get(0)));
		// System.out.println(Integer.parseInt(new_ssid_list.get(1).get(0)));
		// System.out.println(Integer.parseInt(new_ssid_list.get(2).get(0)));

		for (int i = 0; i < new_ssid_list.size(); ++i) {
			//show_seat_ids.get(0).get(i)
			//System.out.println("Iteration" + i);
			try {
				String query = "UPDATE Showseats SET bid = " + booking_id + " WHERE ssid=" + Integer.parseInt(new_ssid_list.get(i).get(0)) + ";";
				esql.executeUpdate(query);
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}



	}
	
	public static void RemovePayment(Ticketmaster esql){//6
		//get pid to identify payment to be cancelled
		int pid;
		do{
			System.out.println("Enter payment id: ");
			try {
				pid = Integer.parseInt(in.readLine());
				break;
			} catch(Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		} while(true);
		
		//find booking to change status to cancelled
		String status = "Cancelled";
		try {
			String queryUpdate = "UPDATE B SET B.status = '" + status + "' FROM Bookings B JOIN Payments P ON B.bid = P.bid WHERE P.bid = '" + pid + "';";
			esql.executeUpdate(queryUpdate);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		//remove payment
		try {
			String queryDelete = "DELETE FROM Payments WHERE pid = '" + pid + "';";
			esql.executeUpdate(queryDelete);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void ClearCancelledBookings(Ticketmaster esql){//7
		List<List<String>> canceled_pending_list = new ArrayList<List<String>>();

		try {
			//String query_user = "SELECT *\n FROM Users\n WHERE email = + user_email;
			String query_canceled_bid = "SELECT bid\n FROM bookings \nWHERE status = 'Cancelled'";

			canceled_pending_list = esql.executeQueryAndReturnResult(query_canceled_bid);
			esql.executeQueryAndPrintResult(query_canceled_bid);

			if (canceled_pending_list.size() == 0) {
				System.out.println("There are no pending bookings."); 
				return;
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		for (int i = 0; i < canceled_pending_list.size(); ++i) {
			//System.out.println("Iteration" + i);
			try {
				String query = "UPDATE Showseats SET bid =null WHERE bid = '" + Integer.parseInt(canceled_pending_list.get(i).get(0)) + "';";
				esql.executeUpdate(query);
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}

		for (int i = 0; i < canceled_pending_list.size(); ++i) {
			//System.out.println("Iteration" + i);
			try {
				String query = "DELETE from bookings WHERE bid = '" + Integer.parseInt(canceled_pending_list.get(i).get(0)) + "';";
				esql.executeUpdate(query);
			} catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public static void RemoveShowsOnDate(Ticketmaster esql){//8
		// //treat as if no one booked anything
		// //get cinema and date
		// String cname;
		// String sdate;
		// do{
		// 	System.out.println("Select Cinema you would like to remove a show from: ");
		// 	try {
		// 		cname = in.readLine();
		// 		if(cname.length() > 64 || cname.length() == 0)  {
		// 			throw new ArithmeticException("Cinema name cannot be empty and be 64 characters or less.");
		// 		}
		// 		else {
		// 			break;
		// 		}
		// 	} catch(Exception e) {
		// 		System.out.println("Your input is invalid!");
		// 		continue;
		// 	}
		// } while(true);

		// do{
		// 	System.out.println("Enter date of show(M/D/YYYY): ");
		// 	try {
		// 		sdate = in.readLine();
		// 		break;
		// 	} catch(Exception e) {
		// 		System.out.println("Your input is invalid!");
		// 		continue;
		// 	}
		// } while(true);

		// //remove show seating
		// try {
		// 	String queryDelete = "DELETE S1 FROM 
		// 	esql.executeUpdate(queryDelete);
		// } catch(Exception e) {
		// 	System.out.println(e.getMessage());
		// }
		// //remove show

	}
	
	public static void ListTheatersPlayingShow(Ticketmaster esql){//9
		int sid;
		do{
			System.out.println("Enter show ID to find Theaters: ");
			try {
				sid = Integer.parseInt(in.readLine());
				break;
			} catch(Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		} while(true);
		
		try {
			String query = "SELECT T.tname\nFROM Theaters T, Plays P\nWHERE T.tid = P.tid\nAND P.sid = '" + sid + "';";
			esql.executeQueryAndPrintResult(query);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void ListShowsStartingOnTimeAndDate(Ticketmaster esql){//10

		String date = "";
		String time = "";
		
		do{
			try {
				System.out.println("Which day does the customer want to attend the show?: ");
				date = in.readLine();
				System.out.println("What time does the customer want to attend the show?: ");
				time = in.readLine();
				if((date.length() > 10 || date.length() == 0) || (time.length() > 8 || time.length() == 0))  {
					throw new RuntimeException("Date cannot be more than 10 characters and time cannot be more than 8 characters");
				}
				else {
					break;
				}

			} catch(Exception e) {
				System.out.println("Your input is invalid!");
				System.exit(0); // we are not implementing sophisticated error checking
				continue;
			}
		} while(true);

		try {
			String query = "SELECT S.sid, M.title FROM Shows S, Movies M WHERE S.sdate = '" + date + "' AND S.sttime = '" + time + "' AND M.mvid = S.mvid;";
			esql.executeQueryAndPrintResult(query);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public static void ListMovieTitlesContainingLoveReleasedAfter2010(Ticketmaster esql){//11
		
	}

	public static void ListUsersWithPendingBooking(Ticketmaster esql){//12
		String status = "Pending";
		
		try {
			String query = "SELECT U.fname, U.lname, U.email\nFROM Users U, Bookings B\nWHERE B.email =  U.email\nAND B.status = '" + status + "';";
			esql.executeQueryAndPrintResult(query);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void ListMovieAndShowInfoAtCinemaInDateRange(Ticketmaster esql){//13
		
	}

	public static void ListBookingInfoForUser(Ticketmaster esql){//14
		//get email and password for User
		String email;
		String pwd;
		do{
			System.out.println("Email: ");
			try {
				email = in.readLine();
				if(email.length() > 64 || email.length() == 0)  {
					throw new ArithmeticException("Email cannot be empty and has to be less 64 characters or less.");
				}
				else {
					break;
				}
			} catch(Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		} while(true);

		do{
			System.out.println("Password: ");
			try {
				pwd = in.readLine();
				if(pwd.length() > 64 || pwd.length() == 0) {
					throw new ArithmeticException("Password cannot be empty and has to be 64 characters or less.");
				}
				else {
					break;
				}
			} catch(Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		} while(true);

		try {// find if user and password match (if it exists in database)
			String queryFindUser = "SELECT U.email\nFROM Users U\nWHERE U.email = '" + email + "'\nAND U.pwd = '" + pwd + "';";
			if((esql.executeQuery(queryFindUser)) == 0)  {
				System.out.println("Invalid email or password!");
			}
			else {
			//Movie Title, Show Date & Start Time, Theater Name, and Cinema Seat Number for
			//all Bookings of a Given User
				try {
					String queryBookingInfo = "SELECT M.title, S.sdate, S.sttime, T.tname, C.sno\n"
											+ "FROM Movies M, Shows S, Theaters T, CinemaSeats C, Users U, Bookings B, Plays P\n"
											+ "WHERE U.email = B.email\n" //connects User and Booking
											+ "AND S.mvid = M.mvid\n" // Shows to movies
											+ "AND S.sid = P.sid\n"  //Shows to plays
											+ "AND P.tid = T.tid;"; //plays to theater
											//idk if that is right ^ missing some links but i think it is ok bc of the relationships
											// and there are no way to link them i.e. cinema seating and theater but if theres a theater theres a seat
					esql.executeQueryAndPrintResult(queryBookingInfo);
				} catch(Exception e) {
					System.out.println(e.getMessage());
				}
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

	}
	
}
