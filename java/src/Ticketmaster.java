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
		String status = "pending";

		Integer ID = esql.getCurrSeqVal("bid");

		System.out.println(ID);

		System.exit(0);

		String user_email;
		do{
			System.out.println("Email: ");
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
				System.out.exit(0);
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
			String query_max_num_seats = "SELECT COUNT(sid)\n FROM Showseats\n GROUP BY sid HAVING sid = '" + sid + "';";

			max_seats = esql.executeQueryAndReturnResult(query_max_num_seats);
			esql.executeQueryAndPrintResult(query_max_num_seats);

			if (date_time.size() == 0) {
				System.out.println("This does not exist"); 
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		String item2 = date_time.get(0).get(0);
		Integer max_possible_seats = Integer.parseInt(item2);


		Integer seat_no; 
		do{
			try {
				System.out.println("How many seats does the customer want to book?: ");
				seat_no = in.readLine();
				if(seat_no > max_possible_seats || seat_no <= 0)  {
					throw new RuntimeException("There are only " + max_possible_seats + " seats available for this show.");
				}
				else {
					break;
				}

			} catch(Exception e) {
				System.out.println("Your input is invalid!");
				exit(0); // we are not implementing sophisticated error checking
				continue;
			}
		} while(true);

		// NOW WE KNOW HOW MANY SEATS TO BOOK FOR THE CUSTOMER

		List<List<String>> show_seat_ids = new ArrayList<List<String>>(); //want to find out maximum number customer can reserve

		try {
			//String query_user = "SELECT *\n FROM Users\n WHERE email = + user_email;
			String query_show_seat_id = "SELECT ssid\n FROM Showseats\n WHERE sid = '" + sid + "';";

			show_seat_ids = esql.executeQueryAndReturnResult(query_show_seat_id);
			esql.executeQueryAndPrintResult(query_show_seat_id);

			if (date_time.size() == 0) {
				System.out.println("This does not exist"); 
			}
			
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}

		// String item3 = show_seat_ids.get(0).get(0);
		// Integer max_possible_seats = Integer.parseInt(item3);
		


		
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
		
	}
	
	public static void ChangeSeatsForBooking(Ticketmaster esql) throws Exception{//5
		
	}
	
	public static void RemovePayment(Ticketmaster esql){//6
		
	}
	
	public static void ClearCancelledBookings(Ticketmaster esql){//7
		
	}
	
	public static void RemoveShowsOnDate(Ticketmaster esql){//8
		
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
		
		String query = "SELECT T.tname FROM Theaters T, Plays P WHERE T.tid = P.tid AND P.sid = " + sid + ";";
	}
	
	public static void ListShowsStartingOnTimeAndDate(Ticketmaster esql){//10
		//
		
	}

	public static void ListMovieTitlesContainingLoveReleasedAfter2010(Ticketmaster esql){//11
		
		
	}

	public static void ListUsersWithPendingBooking(Ticketmaster esql){//12
		//
		
	}

	public static void ListMovieAndShowInfoAtCinemaInDateRange(Ticketmaster esql){//13
		//
	}

	public static void ListBookingInfoForUser(Ticketmaster esql){//14




		
		// try {
		// 	//String query_user = "SELECT *\n FROM Users\n WHERE email = + user_email;
		// 	String movie_title = "SELECT sid\n FROM Shows\n WHERE sttime = '" + time + "';";
		// 	if (esql.executeQueryAndPrintResult(movie_title) == 0) {
		// 		System.out.println("This  does not exist"); 
		// 	}
			
		// } catch(Exception e) {
		// 	System.out.println(e.getMessage());
		// }


		// String titanic = "Titanic";

		// List<List<String>> vector = new ArrayList<List<String>>();
		
		// try {
		// 	//String query_user = "SELECT *\n FROM Users\n WHERE email = + user_email;
		// 	String query = "SELECT s.sid\n FROM Movies m, Shows S\n WHERE m.mvid=s.mvid and m.title = '" + titanic + "';";
		// 	vector = esql.executeQueryAndReturnResult(query);
		// 	if (vector.size() == 0) {
		// 		System.out.println("This  does not exist"); 
		// 	}
			
		// } catch(Exception e) {
		// 	System.out.println(e.getMessage());
		// }

		// String item = vector.get(0).get(0);
		// System.out.println(item);

		// Integer sid = Integer.parseInt(item);

		// try {
		// 	//String query_user = "SELECT *\n FROM Users\n WHERE email = + user_email;
		// 	String query = "SELECT sid, count(ssid)\n FROM Showseats\n GROUP BY sid HAVING sid = '" + sid + "';";
		// 	if (esql.executeQueryAndPrintResult(query) == 0) {
		// 		System.out.println("This does not exist"); 
		// 	}
			
		// } catch(Exception e) {
		// 	System.out.println(e.getMessage());
		// }

		// try {
		// 	//String query_user = "SELECT *\n FROM Users\n WHERE email = + user_email;
		// 	List<List<String>> vector = "SELECT s.sid\n FROM Movies m, Shows S\n WHERE m.mvid=s.mvid and m.title = '" + titanic + "';";
		// 	if (esql.executeQueryAndPrintResult(movie_title) == 0) {
		// 		System.out.println("This  does not exist"); 
		// 	}
			
		// } catch(Exception e) {
		// 	System.out.println(e.getMessage());
		// }
	}
	
}
