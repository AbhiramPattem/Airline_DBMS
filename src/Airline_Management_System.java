import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class Airline_Management_System extends JFrame {
    public Airline_Management_System() {
        super("Airplane Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        initializeGUI();
    }

    private void initializeGUI() {
        // Create a panel with a background image
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    // Load the background image from a URL
                    URL imageURL = new URL("https://www.pixelstalk.net/wp-content/uploads/2016/05/Airplane-HD-Wallpapers.png");
                    Image image = new ImageIcon(imageURL).getImage();
                    g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        // Set panel layout
        panel.setLayout(new FlowLayout());

        // Create the buttons
        JButton passengerButton = new JButton("Passenger");
        JButton displayButton = new JButton("Display");

        // Add action listeners to the buttons
        passengerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openPassengerInterface();
            }
        });

        displayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	setVisible(false);
                openDisplayInterface();
            }
        });

        // Add the buttons to the panel
        panel.add(passengerButton);
        panel.add(displayButton);

        // Add the panel to the frame
        getContentPane().add(panel, BorderLayout.CENTER);
    }

    private void openPassengerInterface() {
        // Create a panel for the Passenger interface
        JPanel passengerPanel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    // Load the background image from a URL
                    URL imageURL = new URL("https://e0.pxfuel.com/wallpapers/194/36/desktop-wallpaper-airplane-for-laptop-cute-plane.jpg");
                    Image image = new ImageIcon(imageURL).getImage();
                    g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        passengerPanel.setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        
        

        // Create the menu bar
        JMenuBar menuBar = new JMenuBar();

        // Create menus
        JMenu passengerMenu = new JMenu("Passenger");
        JMenu flightsMenu = new JMenu("Flights");
        JMenu bookingMenu = new JMenu("Booking");
        JMenu baggageMenu=new JMenu("Baggage");
        JMenu crewMenu = new JMenu("Crew");

        // Create menu items
        JMenuItem insertPassengerItem = new JMenuItem("Insert");
        JMenuItem displayPassengerItem = new JMenuItem("Display");
        JMenuItem deletePassengerItem = new JMenuItem("Delete");
        JMenuItem modifyPassengerItem = new JMenuItem("Modify");
        

        // Add action listeners to the menu items

        insertPassengerItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Prompt the user for passenger information
                String passengerId = JOptionPane.showInputDialog("Enter Passenger ID:");
                String firstName = JOptionPane.showInputDialog("Enter First Name:");
                String lastName = JOptionPane.showInputDialog("Enter Last Name:");
                String email = JOptionPane.showInputDialog("Enter Email:");
                String phoneNo = JOptionPane.showInputDialog("Enter Phone Number:");
                String dateOfBirth = JOptionPane.showInputDialog("Enter Date of Birth (YYYY-MM-DD):");

                // Insert the passenger into the database
                try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "pattem03","vasavi")) {
                    String sql = "INSERT INTO passengers (passenger_id, first_name, last_name, email, phone_number, date_of_birth) " +
                                 "VALUES (?, ?, ?, ?, ?, TO_DATE(?,'YYYY-MM-DD'))";

                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, passengerId);
                    statement.setString(2, firstName);
                    statement.setString(3, lastName);
                    statement.setString(4, email);
                    statement.setString(5, phoneNo);
                    statement.setString(6, dateOfBirth);	

                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(passengerPanel, "Passenger inserted successfully.");
                    } else {
                        JOptionPane.showMessageDialog(passengerPanel, "Failed to insert passenger.");
                    }
                } catch (SQLException ex) {
                	JOptionPane.showMessageDialog(passengerPanel,"Invalid Input");
                }
            }
        });


        displayPassengerItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JPanel passengersPanel = new JPanel();
                passengersPanel.setLayout(new BorderLayout());

                try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "pattem03", "vasavi")) {
                    String sql = "SELECT * FROM passengers";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    ResultSet resultSet = statement.executeQuery();

                    DefaultTableModel tableModel = new DefaultTableModel();
                    JTable table = new JTable(tableModel);
                    JScrollPane scrollPane = new JScrollPane(table);
                    passengersPanel.add(scrollPane, BorderLayout.CENTER);

                    // Add column names to the table model
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        tableModel.addColumn(metaData.getColumnLabel(i));
                    }

                    // Add rows to the table model
                    while (resultSet.next()) {
                        Object[] rowData = new Object[columnCount];
                        for (int i = 1; i <= columnCount; i++) {
                            rowData[i - 1] = resultSet.getObject(i);
                        }
                        tableModel.addRow(rowData);
                    }

                    if (tableModel.getRowCount() == 0) {
                        JOptionPane.showMessageDialog(passengerPanel, "No passengers found.");
                    } else {
                        JOptionPane.showMessageDialog(passengerPanel, passengersPanel, "Passengers", JOptionPane.PLAIN_MESSAGE);
                    }
                } catch (SQLException ex) {
                	JOptionPane.showMessageDialog(passengerPanel,"Invalid Input");
                }
            }
        });



        deletePassengerItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Prompt the user to enter the passenger ID to delete
                String passengerId = JOptionPane.showInputDialog(passengerPanel, "Enter Passenger ID:");

                if (passengerId != null) { // User clicked OK
                    try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "pattem03", "vasavi")) {
                        String sql = "DELETE FROM passengers WHERE passenger_id = ?";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, passengerId);

                        int rowsDeleted = statement.executeUpdate();

                        if (rowsDeleted > 0) {
                            JOptionPane.showMessageDialog(passengerPanel, "Passenger successfully deleted.");
                        } else {
                            JOptionPane.showMessageDialog(passengerPanel, "No passenger found with the provided Passenger ID.");
                        }
                    } catch (SQLException ex) {
                    	JOptionPane.showMessageDialog(passengerPanel,"Invalid Input");
                    }
                }
            }
        });


        modifyPassengerItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Prompt the user for the passenger ID
                String passengerIdInput = JOptionPane.showInputDialog(passengerPanel, "Enter Passenger ID:");

                // If the passenger ID is provided
                if (passengerIdInput != null && !passengerIdInput.isEmpty()) {
                    String passengerId = passengerIdInput.trim();

                    // Retrieve the existing passenger information based on the passenger ID
                    try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "pattem03", "vasavi")) {
                        String selectSql = "SELECT * FROM passengers WHERE passenger_id = ?";
                        PreparedStatement selectStatement = connection.prepareStatement(selectSql);
                        selectStatement.setString(1, passengerId);
                        ResultSet resultSet = selectStatement.executeQuery();

                        // If the passenger with the specified ID exists
                        if (resultSet.next()) {
                            // Prompt the user for the new attribute values
                            String firstName = JOptionPane.showInputDialog(passengerPanel, "Enter First Name:");
                            String lastName = JOptionPane.showInputDialog(passengerPanel, "Enter Last Name:");
                            String email = JOptionPane.showInputDialog(passengerPanel, "Enter Email:");
                            String phoneNo = JOptionPane.showInputDialog(passengerPanel, "Enter Phone Number:");
                            String dateOfBirth = JOptionPane.showInputDialog(passengerPanel, "Enter Date of Birth:");

                            // Update the passenger information in the database
                            String updateSql = "UPDATE passengers SET first_name = ?, last_name = ?, email = ?, phone_number = ?, date_of_birth = ? WHERE passenger_id = ?";
                            PreparedStatement updateStatement = connection.prepareStatement(updateSql);
                            updateStatement.setString(1, firstName);
                            updateStatement.setString(2, lastName);
                            updateStatement.setString(3, email);
                            updateStatement.setString(4, phoneNo);
                            updateStatement.setString(5, dateOfBirth);
                            updateStatement.setString(6, passengerId);
                            int rowsUpdated = updateStatement.executeUpdate();

                            if (rowsUpdated > 0) {
                                JOptionPane.showMessageDialog(passengerPanel, "Passenger information updated successfully.");
                            } else {
                                JOptionPane.showMessageDialog(passengerPanel, "Failed to update passenger information.");
                            }
                        } else {
                            JOptionPane.showMessageDialog(passengerPanel, "Passenger not found.");
                        }
                    } catch (SQLException ex) {
                    	JOptionPane.showMessageDialog(passengerPanel,"Invalid Input");
                    }
                }
            }
        });


        // Add the menu items to the Passenger menu
        passengerMenu.add(insertPassengerItem);
        passengerMenu.add(displayPassengerItem);
        passengerMenu.add(deletePassengerItem);
        passengerMenu.add(modifyPassengerItem);

        // Create menu items for Flights menu
        JMenuItem insertFlightsItem = new JMenuItem("Insert");
        JMenuItem displayFlightsItem = new JMenuItem("Display");
        JMenuItem deleteFlightsItem = new JMenuItem("Delete");
        JMenuItem modifyFlightsItem = new JMenuItem("Modify");

        // Add action listeners to the Flights menu items
        insertFlightsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Prompt the user for flight information
                String flightId = JOptionPane.showInputDialog("Enter Flight ID:");
                String airlineName = JOptionPane.showInputDialog("Enter Airline Name:");
                String origin = JOptionPane.showInputDialog("Enter Origin:");
                String destination = JOptionPane.showInputDialog("Enter Destination:");
                String departureTime = JOptionPane.showInputDialog("Enter Departure Time:");
                String arrivalTime = JOptionPane.showInputDialog("Enter Arrival Time:");
                String availableSeats = JOptionPane.showInputDialog("Enter Available Seats:");
                String pricePerTicket = JOptionPane.showInputDialog("Enter Price per Ticket:");

                // Insert the flight into the database
                try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "pattem03","vasavi")) {
                    String sql = "INSERT INTO flights (flight_id, airline, origin, destination, departure_time, arrival_time, available_seats, price_per_ticket) " +
                                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, flightId);
                    statement.setString(2, airlineName);
                    statement.setString(3, origin);
                    statement.setString(4, destination);
                    statement.setString(5, departureTime);
                    statement.setString(6, arrivalTime);
                    statement.setString(7, availableSeats);
                    statement.setString(8, pricePerTicket);

                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(passengerPanel, "Flight inserted successfully.");
                    } else {
                        JOptionPane.showMessageDialog(passengerPanel, "Failed to insert flight.");
                    }
                } catch (SQLException ex) {
                	JOptionPane.showMessageDialog(passengerPanel,"Invalid Input");
                }
            }
        });


        displayFlightsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JPanel flightsPanel = new JPanel();
                flightsPanel.setLayout(new BorderLayout());

                try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "pattem03", "vasavi")) {
                    String sql = "SELECT * FROM flights";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    ResultSet resultSet = statement.executeQuery();

                    DefaultTableModel tableModel = new DefaultTableModel();
                    JTable table = new JTable(tableModel);
                    JScrollPane scrollPane = new JScrollPane(table);
                    flightsPanel.add(scrollPane, BorderLayout.CENTER);

                    // Add column names to the table model
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        tableModel.addColumn(metaData.getColumnLabel(i));
                    }

                    // Add rows to the table model
                    while (resultSet.next()) {
                        Object[] rowData = new Object[columnCount];
                        for (int i = 1; i <= columnCount; i++) {
                            rowData[i - 1] = resultSet.getObject(i);
                        }
                        tableModel.addRow(rowData);
                    }

                    if (tableModel.getRowCount() == 0) {
                        JOptionPane.showMessageDialog(passengerPanel, "No flights found.");
                    } else {
                        JOptionPane.showMessageDialog(passengerPanel, flightsPanel, "Flights", JOptionPane.PLAIN_MESSAGE);
                    }
                } catch (SQLException ex) {
                	JOptionPane.showMessageDialog(passengerPanel,"Invalid Input");
                }
            }
        });

                

        deleteFlightsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Prompt the user to enter the flight ID to delete
                String flightId = JOptionPane.showInputDialog(passengerPanel, "Enter Flight ID:");

                if (flightId != null) { // User clicked OK
                    try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "pattem03", "vasavi")) {
                        String sql = "DELETE FROM flights WHERE flight_id = ?";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, flightId);

                        int rowsDeleted = statement.executeUpdate();

                        if (rowsDeleted > 0) {
                            JOptionPane.showMessageDialog(passengerPanel, "Flight successfully deleted.");
                        } else {
                            JOptionPane.showMessageDialog(passengerPanel, "No flight found with the provided Flight ID.");
                        }
                    } catch (SQLException ex) {
                    	JOptionPane.showMessageDialog(passengerPanel,"Invalid Input");
                    }
                }
            }
        });


        modifyFlightsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String flightId = JOptionPane.showInputDialog(passengerPanel, "Enter Flight ID:");
                if (flightId != null && !flightId.isEmpty()) {
                    // Prompt for new attribute values
                    String airlineName = JOptionPane.showInputDialog(passengerPanel, "Enter Airline Name:");
                    String origin = JOptionPane.showInputDialog(passengerPanel, "Enter Origin:");
                    String destination = JOptionPane.showInputDialog(passengerPanel, "Enter Destination:");
                    String departureTime = JOptionPane.showInputDialog(passengerPanel, "Enter Departure Time:");
                    String arrivalTime = JOptionPane.showInputDialog(passengerPanel, "Enter Arrival Time:");
                    String availableSeats = JOptionPane.showInputDialog(passengerPanel, "Enter Available Seats:");
                    String pricePerTicket = JOptionPane.showInputDialog(passengerPanel, "Enter Price per Ticket:");

                    // Update the flight record in the database
                    try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "pattem03", "vasavi")) {
                        String sql = "UPDATE flights SET airline = ?, origin = ?, destination = ?, departure_time = ?, arrival_time = ?, available_seats = ?, price_per_ticket = ? WHERE flight_id = ?";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, airlineName);
                        statement.setString(2, origin);
                        statement.setString(3, destination);
                        statement.setString(4, departureTime);
                        statement.setString(5, arrivalTime);
                        statement.setString(6, availableSeats);
                        statement.setString(7, pricePerTicket);
                        statement.setString(8, flightId);

                        int rowsUpdated = statement.executeUpdate();
                        if (rowsUpdated > 0) {
                            JOptionPane.showMessageDialog(passengerPanel, "Flight updated successfully.");
                        } else {
                            JOptionPane.showMessageDialog(passengerPanel, "Flight not found or failed to update.");
                        }
                    } catch (SQLException ex) {
                    	JOptionPane.showMessageDialog(passengerPanel,"Invalid Input");
                    }
                }
            }
        });

        // Add the menu items to the Flights menu
        flightsMenu.add(insertFlightsItem);
        flightsMenu.add(displayFlightsItem);
        flightsMenu.add(deleteFlightsItem);
        flightsMenu.add(modifyFlightsItem);

        // Create menu items for Booking menu
        JMenuItem insertBookingItem = new JMenuItem("Insert");
        JMenuItem displayBookingItem = new JMenuItem("Display");
        JMenuItem deleteBookingItem = new JMenuItem("Delete");
        JMenuItem modifyBookingItem = new JMenuItem("Modify");

        // Add action listeners to the Booking menu items
        insertBookingItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Prompt the user for booking information
                String baggageId = JOptionPane.showInputDialog("Enter Booking ID:");
                String passengerId = JOptionPane.showInputDialog("Enter Passenger ID:");
                String flightId = JOptionPane.showInputDialog("Enter Flight ID:");
                String bookingDate = JOptionPane.showInputDialog("Enter Booking Date (DD-MM-YYYY):");

                // Insert the booking into the database
                try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "pattem03","vasavi")) {
                    String sql = "INSERT INTO bookings (booking_id, passenger_id, flight_id, booking_date) " +
                                 "VALUES (?, ?, ?, ?)";

                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, baggageId);
                    statement.setString(2, passengerId);
                    statement.setString(3, flightId);
                    statement.setString(4, bookingDate);

                    int rowsAffected = statement.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(passengerPanel, "Booking inserted successfully.");
                    } else {
                        JOptionPane.showMessageDialog(passengerPanel, "Failed to insert booking.");
                    }
                } catch (SQLException ex) {
                	JOptionPane.showMessageDialog(passengerPanel,"Invalid Input");
                }
            }
        });


        displayBookingItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JPanel bookingsPanel = new JPanel();
                bookingsPanel.setLayout(new BorderLayout());

                try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "pattem03", "vasavi")) {
                    String sql = "SELECT * FROM bookings";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    ResultSet resultSet = statement.executeQuery();

                    DefaultTableModel tableModel = new DefaultTableModel();
                    JTable table = new JTable(tableModel);
                    JScrollPane scrollPane = new JScrollPane(table);
                    bookingsPanel.add(scrollPane, BorderLayout.CENTER);

                    // Add column names to the table model
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        tableModel.addColumn(metaData.getColumnLabel(i));
                    }

                    // Add rows to the table model
                    while (resultSet.next()) {
                        Object[] rowData = new Object[columnCount];
                        for (int i = 1; i <= columnCount; i++) {
                            rowData[i - 1] = resultSet.getObject(i);
                        }
                        tableModel.addRow(rowData);
                    }

                    if (tableModel.getRowCount() == 0) {
                        JOptionPane.showMessageDialog(passengerPanel, "No bookings found.");
                    } else {
                        JOptionPane.showMessageDialog(passengerPanel, bookingsPanel, "Bookings", JOptionPane.PLAIN_MESSAGE);
                    }
                } catch (SQLException ex) {
                	JOptionPane.showMessageDialog(passengerPanel,"Invalid Input");
                }
            }
        });


        deleteBookingItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Prompt the user to enter the booking ID to delete
                String bookingId = JOptionPane.showInputDialog(passengerPanel, "Enter Booking ID:");

                if (bookingId != null) { // User clicked OK
                    try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "pattem03", "vasavi")) {
                        String sql = "DELETE FROM bookings WHERE booking_id = ?";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, bookingId);

                        int rowsDeleted = statement.executeUpdate();

                        if (rowsDeleted > 0) {
                            JOptionPane.showMessageDialog(passengerPanel, "Booking successfully deleted.");
                        } else {
                            JOptionPane.showMessageDialog(passengerPanel, "No booking found with the provided Booking ID.");
                        }
                    } catch (SQLException ex) {
                    	JOptionPane.showMessageDialog(passengerPanel,"Invalid Input");
                    }
                }
            }
        });


        modifyBookingItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Get the booking ID to modify
                String bookingIdInput = JOptionPane.showInputDialog(passengerPanel, "Enter the Booking ID:");

                if (bookingIdInput != null && !bookingIdInput.isEmpty()) {
                    int bookingId = Integer.parseInt(bookingIdInput);

                    // Get the new attribute values from the user
                    String newPassengerId = JOptionPane.showInputDialog(passengerPanel, "Enter the new Passenger ID:");
                    String newFlightId = JOptionPane.showInputDialog(passengerPanel, "Enter the new Flight ID:");
                    String newSeatNumber = JOptionPane.showInputDialog(passengerPanel, "Enter the new booking date:");
                    // Add more input dialogs for other attributes

                    try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "pattem03", "vasavi")) {
                        // Update the row in the bookings table
                        String sql = "UPDATE bookings SET passenger_id = ?, flight_id = ?, booking_date = ? WHERE booking_id = ?";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, newPassengerId);
                        statement.setString(2, newFlightId);
                        statement.setString(3, newSeatNumber);
                        statement.setInt(4, bookingId);

                        int rowsAffected = statement.executeUpdate();

                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(passengerPanel, "Booking modified successfully.");
                        } else {
                            JOptionPane.showMessageDialog(passengerPanel, "No booking found with the given ID.");
                        }
                    } catch (SQLException ex) {
                    	JOptionPane.showMessageDialog(passengerPanel,"Invalid Input");
                    }
                }
            }
        });


        // Add the menu items to the Booking menu
        bookingMenu.add(insertBookingItem);
        bookingMenu.add(displayBookingItem);
        bookingMenu.add(deleteBookingItem);
        //bookingMenu.add(modifyBookingItem);
        
        JMenuItem insertCrewItem = new JMenuItem("Insert Crew");
        JMenuItem displayCrewItem = new JMenuItem("Display Crew");
        JMenuItem deleteCrewItem = new JMenuItem("Delete Crew");
        JMenuItem modifyCrewItem = new JMenuItem("Modify Crew");
        
        JMenuItem insertBaggageItem = new JMenuItem("Insert");
        JMenuItem displayBaggageItem = new JMenuItem("Display");
        JMenuItem deleteBaggageItem = new JMenuItem("Delete");
        JMenuItem modifyBaggageItem = new JMenuItem("Modify");
        
        insertBaggageItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Prompt the user to enter baggage details
                String baggageId = JOptionPane.showInputDialog(passengerPanel, "Enter Baggage ID:");
                String passengerId = JOptionPane.showInputDialog(passengerPanel, "Enter Passenger ID:");
                String flightId = JOptionPane.showInputDialog(passengerPanel, "Enter Flight ID:");
                String weight = JOptionPane.showInputDialog(passengerPanel, "Enter Weight (in kg):");

                if (baggageId != null && passengerId != null && flightId != null && weight != null) {
                    try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "pattem03", "vasavi")) {
                        String sql = "INSERT INTO baggage (baggage_id, passenger_id, flight_id, weight_kg) VALUES (?, ?, ?, ?)";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, baggageId);
                        statement.setString(2, passengerId);
                        statement.setString(3, flightId);
                        statement.setString(4, weight);

                        int rowsInserted = statement.executeUpdate();

                        if (rowsInserted > 0) {
                            JOptionPane.showMessageDialog(passengerPanel, "Baggage successfully inserted.");
                        } else {
                            JOptionPane.showMessageDialog(passengerPanel, "Failed to insert baggage.");
                        }
                    } catch (SQLException ex) {
                    	JOptionPane.showMessageDialog(passengerPanel,"Invalid Input");
                    }
                }
            }
        });
        displayBaggageItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Display all rows of the "baggage" table
                try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "pattem03", "vasavi")) {
                    String sql = "SELECT * FROM baggage";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    ResultSet resultSet = statement.executeQuery();

                    // Create a table model to hold the baggage data
                    DefaultTableModel tableModel = new DefaultTableModel();
                    tableModel.addColumn("Baggage ID");
                    tableModel.addColumn("Passenger ID");
                    tableModel.addColumn("Flight ID");
                    tableModel.addColumn("Weight (kg)");

                    // Populate the table model with the baggage records
                    while (resultSet.next()) {
                        String baggageId = resultSet.getString("baggage_id");
                        String passengerId = resultSet.getString("passenger_id");
                        String flightId = resultSet.getString("flight_id");
                        String weight = resultSet.getString("weight_kg");

                        tableModel.addRow(new Object[]{baggageId, passengerId, flightId, weight});
                    }

                    // Create a table using the table model
                    JTable table = new JTable(tableModel);

                    // Create a scroll pane to contain the table
                    JScrollPane scrollPane = new JScrollPane(table);

                    // Display the table in a dialog
                    JOptionPane.showMessageDialog(passengerPanel, scrollPane, "Baggage", JOptionPane.PLAIN_MESSAGE);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        
        deleteBaggageItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Prompt the user to enter the baggage_id to delete
                String baggageIdToDelete = JOptionPane.showInputDialog(passengerPanel, "Enter Baggage ID to delete:");

                if (baggageIdToDelete != null && !baggageIdToDelete.isEmpty()) {
                    // Delete the row from the "baggage" table based on baggage_id
                    try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "pattem03", "vasavi")) {
                        String sql = "DELETE FROM baggage WHERE baggage_id = ?";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, baggageIdToDelete);

                        int rowsAffected = statement.executeUpdate();

                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(passengerPanel, "Baggage with ID " + baggageIdToDelete + " deleted successfully.");
                        } else {
                            JOptionPane.showMessageDialog(passengerPanel, "No baggage found with ID " + baggageIdToDelete);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        
        modifyBaggageItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Prompt the user to enter the baggage_id and new weight_kg
                String baggageIdToModify = JOptionPane.showInputDialog(passengerPanel, "Enter Baggage ID to modify:");
                String newWeightKg = JOptionPane.showInputDialog(passengerPanel, "Enter New Weight (kg):");

                if (baggageIdToModify != null && !baggageIdToModify.isEmpty() && newWeightKg != null && !newWeightKg.isEmpty()) {
                    // Modify the weight_kg of the row in the "baggage" table based on baggage_id
                    try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "pattem03", "vasavi")) {
                        String sql = "UPDATE baggage SET weight_kg = ? WHERE baggage_id = ?";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, newWeightKg);
                        statement.setString(2, baggageIdToModify);

                        int rowsAffected = statement.executeUpdate();

                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(passengerPanel, "Baggage with ID " + baggageIdToModify + " modified successfully.");
                        } else {
                            JOptionPane.showMessageDialog(passengerPanel, "No baggage found with ID " + baggageIdToModify);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        baggageMenu.add(insertBaggageItem);
        baggageMenu.add(displayBaggageItem);
        baggageMenu.add(deleteBaggageItem);
        baggageMenu.add(modifyBaggageItem);
        
        
        
        insertCrewItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Prompt the user to enter crew details using input dialogs
                String crewId = JOptionPane.showInputDialog(passengerPanel, "Enter Crew ID:");
                String firstName = JOptionPane.showInputDialog(passengerPanel, "Enter First Name:");
                String lastName = JOptionPane.showInputDialog(passengerPanel, "Enter Last Name:");
                String position = JOptionPane.showInputDialog(passengerPanel, "Enter Position:");
                String baseSalary = JOptionPane.showInputDialog(passengerPanel, "Enter Base Salary:");

                // Insert the crew record into the "crew" table
                try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "pattem03", "vasavi")) {
                    String sql = "INSERT INTO crew (crew_id, first_name, last_name, position, base_salary) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, crewId);
                    statement.setString(2, firstName);
                    statement.setString(3, lastName);
                    statement.setString(4, position);
                    statement.setString(5, baseSalary);

                    int rowsAffected = statement.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(passengerPanel, "Crew record inserted successfully.");
                    } else {
                        JOptionPane.showMessageDialog(passengerPanel, "Failed to insert crew record.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        displayCrewItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Display all rows of the "crew" table in a table format
                try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "pattem03", "vasavi")) {
                    String sql = "SELECT * FROM crew";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    ResultSet resultSet = statement.executeQuery();

                    DefaultTableModel tableModel = new DefaultTableModel();
                    tableModel.addColumn("Crew ID");
                    tableModel.addColumn("First Name");
                    tableModel.addColumn("Last Name");
                    tableModel.addColumn("Position");
                    tableModel.addColumn("Base Salary");

                    while (resultSet.next()) {
                        String crewId = resultSet.getString("crew_id");
                        String firstName = resultSet.getString("first_name");
                        String lastName = resultSet.getString("last_name");
                        String position = resultSet.getString("position");
                        String baseSalary = resultSet.getString("base_salary");

                        tableModel.addRow(new Object[]{crewId, firstName, lastName, position, baseSalary});
                    }

                    JTable table = new JTable(tableModel);
                    JScrollPane scrollPane = new JScrollPane(table);
                    scrollPane.setPreferredSize(new Dimension(600, 400));

                    JOptionPane.showMessageDialog(passengerPanel, scrollPane, "Crew", JOptionPane.PLAIN_MESSAGE);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });


        deleteCrewItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Prompt the user to enter the crew_id for deletion
                String crewIdToDelete = JOptionPane.showInputDialog(passengerPanel, "Enter Crew ID to delete:");

                if (crewIdToDelete != null && !crewIdToDelete.isEmpty()) {
                    // Delete the crew record from the "crew" table based on crew_id
                    try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "pattem03", "vasavi")) {
                        String sql = "DELETE FROM crew WHERE crew_id = ?";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, crewIdToDelete);

                        int rowsAffected = statement.executeUpdate();

                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(passengerPanel, "Crew record with ID " + crewIdToDelete + " deleted successfully.");
                        } else {
                            JOptionPane.showMessageDialog(passengerPanel, "No crew record found with ID " + crewIdToDelete);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        modifyCrewItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Prompt the user to enter the crew_id and new base_salary for modification
                String crewIdToModify = JOptionPane.showInputDialog(passengerPanel, "Enter Crew ID to modify:");
                String newBaseSalary = JOptionPane.showInputDialog(passengerPanel, "Enter New Base Salary:");

                if (crewIdToModify != null && !crewIdToModify.isEmpty() && newBaseSalary != null && !newBaseSalary.isEmpty()) {
                    // Modify the base_salary of the row in the "crew" table based on crew_id
                    try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "pattem03", "vasavi")) {
                        String sql = "UPDATE crew SET base_salary = ? WHERE crew_id = ?";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, newBaseSalary);
                        statement.setString(2, crewIdToModify);

                        int rowsAffected = statement.executeUpdate();

                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(passengerPanel, "Crew record with ID " + crewIdToModify + " modified successfully.");
                        } else {
                            JOptionPane.showMessageDialog(passengerPanel, "No crew record found with ID " + crewIdToModify);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        
        crewMenu.add(insertCrewItem);
        crewMenu.add(displayCrewItem);
        crewMenu.add(deleteCrewItem);
        crewMenu.add(modifyCrewItem);


        

        // Add the menus to the menu bar
        menuBar.add(passengerMenu);
        menuBar.add(flightsMenu);
        menuBar.add(bookingMenu);
        menuBar.add(baggageMenu);
        menuBar.add(crewMenu);

        // Set the menu bar
        passengerPanel.add(menuBar, BorderLayout.NORTH);

        // Add the passengerPanel to the frame
        getContentPane().removeAll();
        getContentPane().add(passengerPanel);
        revalidate();
        repaint();
    }
    private void openDisplayInterface() {
        JFrame crewFrame = new JFrame("Display");
        crewFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        crewFrame.setSize(800, 500);
        crewFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0); // Add spacing of 10 pixels top and bottom

        JButton displayBookedPassengersButton = new JButton("Display Booked Passengers");
        JButton displayUnbookedPassengersButton = new JButton("Display Unbooked Passengers");
        JButton displayPassengerBaggageButton = new JButton("Display Passenger and Baggage Details");

        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        displayBookedPassengersButton.setFont(buttonFont);
        displayUnbookedPassengersButton.setFont(buttonFont);
        displayPassengerBaggageButton.setFont(buttonFont);

        panel.add(displayBookedPassengersButton, gbc);

        gbc.gridy++;
        panel.add(displayUnbookedPassengersButton, gbc);

        gbc.gridy++;
        panel.add(displayPassengerBaggageButton, gbc);

        crewFrame.setContentPane(panel);
        crewFrame.setVisible(true);

        ActionListener buttonActionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == displayBookedPassengersButton) {
                    try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "pattem03", "vasavi")) {
                        String sql = "SELECT b.booking_id,p.passenger_id,p.first_name,p.email,f.flight_id,f.airline FROM passengers p, flights f, bookings b WHERE b.booking_id IN (SELECT booking_id FROM bookings b2 where b2.passenger_id=p.passenger_id and b2.flight_id=f.flight_id)";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        ResultSet resultSet = statement.executeQuery();

                        DefaultTableModel tableModel = new DefaultTableModel();
                        tableModel.addColumn("Booking_id");
                        tableModel.addColumn("Passenger_id");
                        tableModel.addColumn("First_name");
                        tableModel.addColumn("Email");
                        tableModel.addColumn("Flight_id");
                        tableModel.addColumn("Airline");
                        while (resultSet.next()) {
                            String bookingId = resultSet.getString("booking_id");
                            String passengerId = resultSet.getString("passenger_id");
                            String firstName = resultSet.getString("first_name");
                            String email = resultSet.getString("email");
                            String flightId = resultSet.getString("flight_id");
                            String airline = resultSet.getString("airline");

                            tableModel.addRow(new Object[]{bookingId, passengerId, firstName, email, flightId, airline});
                        }

                        JTable table = new JTable(tableModel);
                        JScrollPane scrollPane = new JScrollPane(table);
                        scrollPane.setPreferredSize(new Dimension(600, 400));

                        JOptionPane.showMessageDialog(crewFrame, scrollPane, "Booked Passengers", JOptionPane.PLAIN_MESSAGE);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                else if (e.getSource() == displayUnbookedPassengersButton) {
                    try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "pattem03", "vasavi")) {
                        String sql = "SELECT p.passenger_id,p.first_name,p.email,p.phone_number FROM passengers p, bookings b WHERE b.booking_id NOT IN (SELECT booking_id FROM bookings b2 WHERE b2.passenger_id=p.passenger_id)";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        ResultSet resultSet = statement.executeQuery();

                        DefaultTableModel tableModel = new DefaultTableModel();
                        tableModel.addColumn("Passenger_id");
                        tableModel.addColumn("First_name");
                        tableModel.addColumn("Email");
                        tableModel.addColumn("Phone Number");
                        while (resultSet.next()) {
                            String passengerId = resultSet.getString("passenger_id");
                            String firstName = resultSet.getString("first_name");
                            String email = resultSet.getString("email");
                            String phoneNumber = resultSet.getString("phone_number");

                            tableModel.addRow(new Object[]{passengerId, firstName, email, phoneNumber});
                        }

                        JTable table = new JTable(tableModel);
                        JScrollPane scrollPane = new JScrollPane(table);
                        scrollPane.setPreferredSize(new Dimension(600, 400));

                        JOptionPane.showMessageDialog(crewFrame, scrollPane, "Unbooked Passengers", JOptionPane.PLAIN_MESSAGE);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                else if (e.getSource() == displayPassengerBaggageButton) {
                    try (Connection connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "pattem03", "vasavi")) {
                        String sql = "SELECT b.baggage_id, p.passenger_id, p.first_name, p.email, f.flight_id, f.airline, b.weight_kg FROM passengers p, flights f, baggage b WHERE b.passenger_id = p.passenger_id AND b.flight_id = f.flight_id";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        ResultSet resultSet = statement.executeQuery();

                        DefaultTableModel tableModel = new DefaultTableModel();
                        tableModel.addColumn("Baggage_id");
                        tableModel.addColumn("Passenger_id");
                        tableModel.addColumn("First_name");
                        tableModel.addColumn("Email");
                        tableModel.addColumn("Flight_id");
                        tableModel.addColumn("Airline");
                        tableModel.addColumn("Weight_kg");
                        while (resultSet.next()) {
                            String baggageId = resultSet.getString("baggage_id");
                            String passengerId = resultSet.getString("passenger_id");
                            String firstName = resultSet.getString("first_name");
                            String email = resultSet.getString("email");
                            String flightId = resultSet.getString("flight_id");
                            String airline = resultSet.getString("airline");
                            String weightKg = resultSet.getString("weight_kg");

                            tableModel.addRow(new Object[]{baggageId, passengerId, firstName, email, flightId, airline, weightKg});
                        }

                        JTable table = new JTable(tableModel);
                        JScrollPane scrollPane = new JScrollPane(table);
                        scrollPane.setPreferredSize(new Dimension(600, 400));

                        JOptionPane.showMessageDialog(crewFrame, scrollPane, "Passenger and Baggage Details", JOptionPane.PLAIN_MESSAGE);
                    } catch (SQLException ex) {
                        //JOptionPane.showMessageDialog(panel, "No rows selected");
                        ex.printStackTrace();
                    }
                }

                }
        };

        displayBookedPassengersButton.addActionListener(buttonActionListener);
        displayUnbookedPassengersButton.addActionListener(buttonActionListener);
        displayPassengerBaggageButton.addActionListener(buttonActionListener);
    }



    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Airline_Management_System().setVisible(true);
            }
        });
    }
}
