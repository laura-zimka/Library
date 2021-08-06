import java.sql.*;
import java.util.Scanner;


public class Library {

    public static void main(String[] args) {

        try {
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/library", "root", "laura1111");

            operation(con);

//            addBook(con);
//            removeBook(con);
//            issueBook(con);
//            returnBook(con);
//            addAuthor(con);
//            addRelation(con);
//            registerBorrower(con);
//            blockBorrower(con);
//            unblockBorrower(con);
//            findByGenre(con);
//            findByAuthor(con);
//            borrowedById(con);
//            genreStatistic(con);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    static void operation (Connection con){


        System.out.println("\n1: insert book \n" +
                "2: delete book \n" +
                "3: issue book \n" +
                "4: return book \n" +
                "5: add author \n" +
                "6: add book-author relation \n" +
                "7: register borrower \n" +
                "8: block borrower \n" +
                "9: unblock borrower \n" +
                "10: find books by genre \n" +
                "11: find books by author \n" +
                "12: books issued to borrower \n" +
                "13: genre statistics \n");
        System.out.print("select an operation: ");


        Scanner in = new Scanner(System.in);
        int choice = in.nextInt();

        switch (choice) {
            case 1:
                addBook(con);
                break;
            case 2:
                removeBook(con);
                break;
            case 3:
                issueBook(con);
                break;
            case 4:
                returnBook(con);
                break;
            case 5:
                addAuthor(con);
                break;
            case 6:
                addRelation(con);
                break;
            case 7:
                registerBorrower(con);
                break;
            case 8:
                blockBorrower(con);
                break;
            case 9:
                unblockBorrower(con);
                break;
            case 10:
                findByGenre(con);
                break;
            case 11:
                findByAuthor(con);
                break;
            case 12:
                borrowedById(con);
                break;
            case 13:
                genreStatistic(con);
                break;
            default:
                System.out.print("\noperation not found");
                break;

        }

    }

    static void addBook (Connection con){
        System.out.println("\nadd a new book to the database\n");
        Scanner in = new Scanner(System.in);
        System.out.print("\ntitle: ");
        String b_title = in.nextLine();
        System.out.print("\ngenre: ");
        String b_genre = in.nextLine();
        System.out.print("\nyear: ");
        String b_year = in.nextLine();
        in.close();
        CallableStatement bks;


        try {
            String SQLCountries = "{call insert_Book(?,?,?)}";
            bks = con.prepareCall(SQLCountries);

            bks.setString(1, b_title);
            bks.setString(2, b_genre);
            bks.setString(3, b_year);


            bks.execute();
            System.out.println("\nbook added successfully");
        } catch (SQLException err) {
            err.printStackTrace();
        }
    }

    static void removeBook(Connection con) {
        System.out.println("\nremove book\n");
        boolean validCheck = false;

        while (!validCheck) {
            Scanner scan = new Scanner(System.in);
            System.out.print("book id: ");
            int book_id = scan.nextInt();
            String validIdQuery = "SELECT * FROM library.books WHERE books.id = " + book_id + ";";
            String removeBookQuery1 = "DELETE FROM library.author_to_book WHERE book_id = " + book_id + ";";
            String removeBookQuery2 = "DELETE FROM library.books WHERE books.id = " + book_id + ";";

            try (Statement s = con.createStatement()) {
                ResultSet rs = s.executeQuery(validIdQuery);
                if (rs.next()) {
                    rs.previous();
                    try (Statement s2 = con.createStatement()) {
                        s2.addBatch(removeBookQuery1);
                        s2.addBatch(removeBookQuery2);
                        s2.executeBatch();
                        validCheck = true;
                        System.out.print("\nbook removed successfully");
                        scan.close();
                    } catch (SQLException err) {
                        err.printStackTrace();
                    }

                } else {
                    System.out.println("book not found\n"); }

            } catch (SQLException err) {
                err.printStackTrace(); }
        }
    }

    static void addAuthor(Connection con) {
        Scanner in = new Scanner(System.in);
        System.out.print("\nfirst name: ");
        String f_name = in.nextLine();
        System.out.print("\nlast name: ");
        String l_name = in.nextLine();
        System.out.print("\ngender: ");
        String gender = in.nextLine();
        String insertRecords = "INSERT INTO library.authors(f_name, l_name,gender) VALUES(?,?,?)";

        try (PreparedStatement insertQuery = con.prepareStatement(insertRecords)) {
            insertQuery.setString(1, f_name);
            insertQuery.setString(2, l_name);
            insertQuery.setString(3, gender);

            insertQuery.executeUpdate();
            System.out.println("\n" + "author added successfully");
        } catch (SQLException err) {
            err.printStackTrace();
        }
    }

    static void addRelation (Connection con){
        System.out.println("\nadding a new relation between a book and an author");
        Scanner in = new Scanner(System.in);
        System.out.print("\nbook id: ");
        int book_id = in.nextInt();
        System.out.print("\nauthor id: ");
        int author_id = in.nextInt();
        String addRelationQuery = "INSERT INTO library.author_to_book values (?, ?)";

        try (PreparedStatement prep = con.prepareStatement(addRelationQuery)){
            prep.setInt(1, book_id);
            prep.setInt(2, author_id);

            prep.executeUpdate();
            System.out.println("\nbook and author relation added successfully");
            in.close();
        } catch (SQLException err) {
            err.printStackTrace();
        }

    }

    static void findByGenre (Connection con) {
        System.out.println("\nsearching books by genre");

        boolean validCheck = false;
        while (!validCheck) {
            Scanner in = new Scanner(System.in);
            System.out.print("\ngenre: ");
            String genre = in.nextLine();
            String genreFilter = "SELECT books.id, title, book_status, authors.f_name, authors.l_name FROM books JOIN library.author_to_book AS a_b ON books.id = a_b.book_id JOIN authors ON authors.id = a_b.author_id WHERE books.genre = '" + genre + "'";

            try (Statement statement = con.createStatement()) {

                ResultSet set = statement.executeQuery(genreFilter);
                if (set.next()) {
                    validCheck = true;
                    set.previous();
                    while (set.next()) {
                        System.out.println("\n" + set.getInt(1) + ":    " + set.getString(2)
                                + "\n" + "author: " + set.getString(4) + "  " + set.getString(5)
                                + "\n" + "status: " + set.getString(3) + "\n");
                    }
                    in.close();
                } else {
                    System.out.println("genre not found");
                }
            } catch (SQLException err) {
                err.printStackTrace();
            }
        }
    }

    static void findByAuthor(Connection con) {
        boolean validCheck = false;
        while (!validCheck) {
            Scanner scan = new Scanner(System.in);
            System.out.print("\nlast name of author: ");
            String author = scan.nextLine();

            String fetchByAuthorQuery = "SELECT books.id, title, book_Status FROM books JOIN author_to_book AS a_b ON books.id = a_b.book_id " +
                    "JOIN authors ON authors.id = a_b.author_id WHERE authors.l_name = '" + author + "'";

            try (Statement statement = con.createStatement()) {
                ResultSet rs = statement.executeQuery(fetchByAuthorQuery);
                if (rs.next()) {
                    validCheck = true;
                    rs.previous();
                    while (rs.next()) {
                        System.out.println("\n" + rs.getInt(1) + ": " + rs.getString(2) + " \n" + rs.getString(3) + "\n");
                    }
                    scan.close();
                } else {
                    System.out.println("author not found");
                }
            } catch (SQLException err) {
                err.printStackTrace();
            }
        }
    }

    static void borrowedById(Connection con) {
        System.out.println("\nsearching for books in possession of borrower");
        boolean validCheck = false;
        while (!validCheck) {
            Scanner scan = new Scanner(System.in);
            System.out.print("\nid: ");
            int borrower = scan.nextInt();

            String fetchBorrowerQuery = "SELECT books.id, title, authors.f_name, authors.l_name FROM books " +
                    "JOIN library.author_to_book AS a_b ON books.id = a_b.book_id " +
                    "JOIN authors ON authors.id = a_b.author_id WHERE borrower_id = " + borrower + ";" ;
            String fetchBooleanQuery = "SELECT * FROM borrower WHERE borrower.id = " + borrower + ";";

            try (Statement s = con.createStatement()){
                ResultSet rs = s.executeQuery(fetchBorrowerQuery);
                if (rs.next()) {
                    validCheck = true;
                    rs.previous();
                    while (rs.next()) {
                        System.out.println("\n" + rs.getInt(1) + ": " + rs.getString(2) + " \n" + rs.getString(3) + "  " + rs.getString(4));
                    }
                    scan.close();
                } else {
                    try(Statement s2 = con.createStatement()) {
                        ResultSet rs2 = s2.executeQuery(fetchBooleanQuery);
                        if( !rs2.next()) {
                            System.out.println("no borrower found"); }
                        else {
                            System.out.println("no matches found");
                        }
                    } catch (SQLException err2 ) {
                        err2.printStackTrace();
                    }
                }
            } catch (SQLException err) {
                err.printStackTrace();
            }
        }
    }

    static void blockBorrower(Connection con) {

        System.out.print("\nblocking borrower and adding commentary\n");
        boolean validCheck = false;

        while (!validCheck) {
            System.out.print("\nborrower id: ");
            Scanner scan = new Scanner(System.in);
            int id = Integer.parseInt(scan.nextLine());

            String blockBorrowerQuery = "update library.borrower set borrower_status= ? , commentary = ? where borrower.id = " + id + ";";
            String fetchBooleanQuery = "SELECT * FROM borrower WHERE borrower.id = " + id + ";";

            try (Statement s = con.createStatement()) {
                ResultSet rs = s.executeQuery(fetchBooleanQuery);
                if (rs.next()) {
                    validCheck = true;
                    rs.previous();
                    System.out.print("add commentary: ");
                    String commentary = scan.nextLine();
                    try (PreparedStatement blockBorrower = con.prepareStatement(blockBorrowerQuery)) {
                        blockBorrower.setString(1, "blocked");
                        blockBorrower.setString(2, commentary);
                        blockBorrower.executeUpdate();
                        System.out.println("\nborrower blocked successfully");
                        scan.close();
                    } catch (SQLException err) {
                        err.printStackTrace(); }
                }
                else {
                    System.out.println("borrower not found"); }
            } catch (SQLException err){
                err.printStackTrace(); }
        }
    }

    static void unblockBorrower(Connection con) {
        System.out.println("\nunblock borrower and delete commentary\n");
        boolean validCheck = false;

        while (!validCheck) {
            System.out.print("borrower id: ");
            Scanner scan = new Scanner(System.in);
            int id = scan.nextInt();

            String unblockBorrowerQuery = "update library.borrower set borrower_status= ? , commentary = null where borrower.id = " + id + ";";
            String fetchBooleanQuery = "SELECT * FROM borrower WHERE borrower.id = " + id + ";";

            try (Statement s = con.createStatement()) {
                ResultSet rs = s.executeQuery(fetchBooleanQuery);
                if(rs.next()) {
                    validCheck = true;
                    rs.previous();
                    try (PreparedStatement unblockBorrower = con.prepareStatement(unblockBorrowerQuery)) {
                        unblockBorrower.setString(1, "active");
                        unblockBorrower.executeUpdate();
                        System.out.println("\nborrower unblocked successfully");
                        scan.close();
                    } catch (SQLException err) {
                        err.printStackTrace(); }
                }
                else {
                    System.out.println("borrower not found"); }

            } catch (SQLException err) {
                err.printStackTrace();
            }
        }
    }

    static void issueBook(Connection con) {
        System.out.println("\nissue book to borrower\n");
        boolean validCheck = false;

        while (!validCheck) {
            Scanner scan = new Scanner(System.in);
            System.out.print("borrower id: ");
            int br_id = scan.nextInt();

            String userExistsQuery = "SELECT * FROM borrower WHERE borrower.id = " + br_id + ";";
            String isActiveQuery = "SELECT * FROM library.borrower WHERE borrower_status = 'active' AND borrower.id = " +  br_id + ";";

            try (Statement s = con.createStatement()) {
                ResultSet rs = s.executeQuery(userExistsQuery);

                if(rs.next()) {
                    try(Statement s2 = con.createStatement()) {
                        ResultSet rs2 = s2.executeQuery(isActiveQuery);
                        if (rs2.next()) {
                            validCheck = true;
                            rs.previous();
                            System.out.print("book id: ");
                            int book_id = scan.nextInt();
                            String issueBookQuery = "update library.books set borrower_id = " + br_id + ", book_status = 'unavailable', statistic = statistic + 1 where books.id = " + book_id;
                            try (PreparedStatement issueBook = con.prepareStatement(issueBookQuery)) {
                                issueBook.executeUpdate();
                                System.out.println("\nbook issued successfully");
                                scan.close();
                            } catch (SQLException err) {
                                err.printStackTrace(); }
                        }
                        else {
                            System.out.println("\noperation failed to execute" + "\nborrower is blocked\n");
                        }
                    } catch (SQLException err) {
                        err.printStackTrace(); }
                }
                else {
                    System.out.println("borrower not found"); }
            } catch (SQLException err) {
                err.printStackTrace();
            }
        }
    }

    static void returnBook(Connection con) {
        System.out.println("\nreturn book\n");
        Scanner scan = new Scanner(System.in);
        System.out.print("book id: ");
        int book_id = scan.nextInt();

        String returnBookQuery = "update library.books set borrower_id = null, book_status = 'available' where books.id = "+ book_id;
        String availableQuery = "SELECT * FROM library.books WHERE book_status = 'available' AND books.id = " + book_id +  ";";

        try (Statement s = con.createStatement()) {
            ResultSet rs = s.executeQuery(availableQuery);
            if (!rs.next()) {
                try (PreparedStatement returnBook = con.prepareStatement(returnBookQuery)) {
                    rs.previous();
                    returnBook.executeUpdate();
                    System.out.println("\nbook returned successfully");
                    scan.close();
                } catch (SQLException err) {
                    err.printStackTrace();
                }
            }
            else {
                System.out.println("\nbook already returned");
            }
        } catch (SQLException err) {
            err.printStackTrace();
        }
    }

    static void registerBorrower(Connection con) {
        System.out.println("\nadd a new borrower\n");
        boolean validCheck = false;
        Scanner in = new Scanner(System.in);
        System.out.print("name: ");
        String borrower_name = in.nextLine();

        while (!validCheck) {
            System.out.print("e-mail: ");
            String borrower_email = in.nextLine();
            String email_regex = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

            if (borrower_email.matches(email_regex)) {
                System.out.print("phone number: ");
                String borrower_phone = in.nextLine();
                String registerBorrowerQuery = "INSERT INTO library.borrower(borrower_name, borrower_email,borrower_phone) VALUES(?,?,?)";
                String fetchBorrowerIdQuery = "SELECT library.borrower.id from borrower WHERE borrower_email = '" + borrower_email + "';";

                try (PreparedStatement registerUserQuery = con.prepareStatement(registerBorrowerQuery)) {

                    registerUserQuery.setString(1, borrower_name);
                    registerUserQuery.setString(2, borrower_email);
                    registerUserQuery.setString(3, borrower_phone);

                    registerUserQuery.executeUpdate();
                    validCheck = true;
                    System.out.print("\nborrower registered successfully\nborrower ID: ");
                    in.close();

                    try (Statement statement = con.createStatement()) {
                        ResultSet rs = statement.executeQuery(fetchBorrowerIdQuery);
                        while (rs.next())
                            System.out.println(rs.getInt(1));
                    } catch (SQLException err) {
                        err.printStackTrace();
                    }
                } catch (SQLException err) {
                    err.printStackTrace(); }
            } else {
                System.out.println("invalid input for e-mail\n");
            }
        }

    }

    static void genreStatistic(Connection connection) {
        System.out.println("\naverage book issuing frequency by genre\n");

        String statisticGenreQuery = "select genre, CAST(AVG(statistic) AS DECIMAL(3,1)) from books group by genre;";
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(statisticGenreQuery);
            while (rs.next())
                System.out.println(rs.getString(1) + " " + rs.getDouble(2));
        } catch (SQLException err) {
            err.printStackTrace();
        }
    }
}

