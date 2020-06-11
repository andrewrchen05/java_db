// String user_email;
		// do{
		// 	System.out.println("Email: ");
		// 	try {
		// 		user_email = in.readLine();
		// 		if(user_email.length() > 64 || user_email.length() == 0)  {
		// 			throw new ArithmeticException("Email cannot be empty and has to be less than 64 characters.");
		// 		}
		// 		else {
		// 			break;
		// 		}

		// 	} catch(Exception e) {
		// 		System.out.println("Your input is invalid!");
		// 		continue;
		// 	}
		// } while(true);

		// //insert into table
		// try {
		// 	//String query_user = "SELECT *\n FROM Users\n WHERE email = + user_email;
		// 	String query_user = "SELECT *\n FROM Users\n WHERE email = '" + user_email + "';";
		// 	if (esql.executeQueryAndReturnResult(query_user) == 0) {
		// 		System.out.println("This user does not exist"); 
		// 	}
			
		// } catch(Exception e) {
		// 	System.out.println(e.getMessage());
		// }
		// //USER EXISTS, SO WE CAN CREATE A BOOKING


		// String movie; // MOVIE
		// do{
		// 	System.out.println("Which movie does the customer want to watch?: ");
		// 	try {
		// 		movie = in.readLine();
		// 		if(movie.length() > 128 || movie.length() == 0)  {
		// 			throw new RuntimeException("Movie cannot be empty and has to be less than 128 characters.");
		// 		}
		// 		else {
		// 			break;
		// 		}

		// 	} catch(Exception e) {
		// 		System.out.println("Your input is invalid!");
		// 		continue;
		// 	}
		// } while(true);

		// Integer mvid;
		// try {
		// 	//String query_user = "SELECT *\n FROM Users\n WHERE email = + user_email;
		// 	String query_mvid = "SELECT mvid\n FROM Movies\n WHERE title = '" + movie + "';";
		// 	if (esql.executeQueryAndPrintResult(query_user) == 0) {
		// 		System.out.println("This  does not exist"); 
		// 	}
			
		// } catch(Exception e) {
		// 	System.out.println(e.getMessage());
		// }
		//MOVIE EXISTS SO WE CAN CREATE A BOOKING

		// String day; //DAY
		// do{
		// 	System.out.println("Which day does the customer want to attend the show?: ");
		// 	try {
		// 		day = in.readLine();
		// 		if(day.length() > 10 || day.length() == 0)  {
		// 			throw new RuntimeException("Date cannot be more than 10 characters.");
		// 		}
		// 		else {
		// 			break;
		// 		}

		// 	} catch(Exception e) {
		// 		System.out.println("Your input is invalid!");
		// 		continue;
		// 	}
		// } while(true);

		// String time; //TIME
		// do{
		// 	System.out.println("What time does the customer want to attend the show?: ");
		// 	try {
		// 		time = in.readLine();
		// 		if(time.length() > 8 || time.length() == 0)  {
		// 			throw new RuntimeException("Time cannot be more than 8 characters");
		// 		}
		// 		else {
		// 			break;
		// 		}

		// 	} catch(Exception e) {
		// 		System.out.println("Your input is invalid!");
		// 		continue;
		// 	}
		// } while(true);
		
		// try {
		// 	//String query_user = "SELECT *\n FROM Users\n WHERE email = + user_email;
		// 	String movie_title = "SELECT sid\n FROM Shows\n WHERE sttime = '" + time + "';";
		// 	if (esql.executeQueryAndPrintResult(movie_title) == 0) {
		// 		System.out.println("This  does not exist"); 
		// 	}
			
		// } catch(Exception e) {
		// 	System.out.println(e.getMessage());
		// }