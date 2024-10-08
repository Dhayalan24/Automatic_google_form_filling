package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.*;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class ChatBotFormApp extends JFrame {

    private JTextArea chatArea;
    private JTextField chatInput;
    private JButton yesButton, noButton;
    private JDialog formDialog;
    private JTextField nameField, phoneField, emailField, collegeField, departmentField, cgpaField;
    private JTextField[] semFields = new JTextField[8];
    private JTextField tenthSchoolField, tenthMarkField, tenthPercentageField;
    private JTextField twelfthSchoolField, twelfthMarkField, twelfthPercentageField;
    private JButton uploadResumeButton;
    private File resumeFile;

    public ChatBotFormApp() {
        // Chatbot Panel
        setTitle("Interactive ChatBot Form");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(Color.WHITE);

        chatArea = new JTextArea(10, 40);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 16));
        chatArea.setEditable(false);
        chatArea.setBackground(new Color(240, 248, 255)); // Light background color for better readability
        chatArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        chatInput = new JTextField(40);
        chatInput.setFont(new Font("Arial", Font.PLAIN, 14));
        chatInput.setBackground(Color.WHITE);
        chatInput.setForeground(Color.DARK_GRAY);
        chatInput.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true)); // Rounded border for the input field

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        chatPanel.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        chatPanel.add(chatInput, BorderLayout.SOUTH);
        add(chatPanel, BorderLayout.CENTER);

        chatArea.append("Bot: Welcome! Have you already stored your information?\n");

        yesButton = new JButton("Yes");
        noButton = new JButton("No");
        styleButton(yesButton, new Color(76, 175, 80)); // Green for "Yes"
        styleButton(noButton, new Color(244, 67, 54)); // Red for "No"
        yesButton.setFont(new Font("Arial", Font.BOLD, 14));
        noButton.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Center the frame on the screen
        setLocationRelativeTo(null);

        // Yes button listener
        yesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chatArea.append("User: Yes\n");
                chatArea.append("Bot: Please enter your name to check if it is stored.\n");

                // Temporarily disable Yes/No buttons
                yesButton.setEnabled(false);
                noButton.setEnabled(false);

                chatInput.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String userName = chatInput.getText().trim();
                        chatArea.append("User: " + userName + "\n");
                        chatInput.setText("");

                        if (checkUserNameInDatabase(userName)) {
                            chatArea.append("Bot: Your name is found in the database. Please paste the Google form link here.\n");

                            // Wait for the user to enter the Google form link

                            chatInput.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    String googleFormLink = chatInput.getText().trim();
                                    chatArea.append("User: " + googleFormLink + "\n");
                                    chatInput.setText("");
                                    chatArea.append("Bot: Thank you! The form link has been received.\n");

                                    // Call the method to fill the Google Form using the automation
                                    fillGoogleForm(googleFormLink, userName);
                                }

                            });
                        } else {
                            chatArea.append("Bot: Sorry, your name is not found in the database.\n");
                            yesButton.setEnabled(true); // Re-enable buttons
                            noButton.setEnabled(true);
                        }
                    }
                });
            }
        });

        // No button listener
        noButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chatArea.append("User: No\n");
                chatArea.append("Bot: Let's collect your information.\n");
                showFormDialog(); // Show form in a new dialog
            }
        });

        setVisible(true);
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setContentAreaFilled(true); // Ensures the background color is filled
        button.setBorderPainted(false);    // Removes the border outline if you want a flat look
    }


    private void showFormDialog() {
        formDialog = new JDialog(this, "User Information Form", true);
        formDialog.setSize(600, 800);
        formDialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.anchor = GridBagConstraints.CENTER;

        nameField = new JTextField(20);
        phoneField = new JTextField(20);
        emailField = new JTextField(20);
        collegeField = new JTextField(20);
        departmentField = new JTextField(20);
        cgpaField = new JTextField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Phone Number:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Email ID:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("College Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(collegeField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(departmentField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("CGPA:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(cgpaField, gbc);



        tenthSchoolField = new JTextField(20);
        tenthMarkField = new JTextField(20);
        tenthPercentageField = new JTextField(20);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("10th Standard School:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(tenthSchoolField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("10th Mark:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(tenthMarkField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("10th Percentage:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(tenthPercentageField, gbc);

        twelfthSchoolField = new JTextField(20);
        twelfthMarkField = new JTextField(20);
        twelfthPercentageField = new JTextField(20);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("12th Standard School:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(twelfthSchoolField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("12th Mark:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(twelfthMarkField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("12th Percentage:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(twelfthPercentageField, gbc);

        uploadResumeButton = new JButton("Upload Resume");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Resume:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(uploadResumeButton, gbc);

        uploadResumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    resumeFile = fileChooser.getSelectedFile();
                    JOptionPane.showMessageDialog(null, "Resume uploaded: " + resumeFile.getName());
                }
            }
        });

        JButton submitButton = new JButton("Submit");
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 3;
        formPanel.add(submitButton, gbc);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveUserDataToMySQL();
                formDialog.dispose();
            }
        });

        formDialog.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        formDialog.setVisible(true);
    }


    private void saveUserDataToMySQL() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String college = collegeField.getText();
        String department = departmentField.getText();
        String cgpa = cgpaField.getText();


        String tenthSchool = tenthSchoolField.getText();
        String tenthMark = tenthMarkField.getText();
        String tenthPercentage = tenthPercentageField.getText();
        String twelfthSchool = twelfthSchoolField.getText();
        String twelfthMark = twelfthMarkField.getText();
        String twelfthPercentage = twelfthPercentageField.getText();

        // Database connection details
        String url = "jdbc:mysql://localhost:3306/userdetails"; // Replace with your DB name
        String user = "root";  // Replace with your MySQL username
        String password = "Dhayalan@123";  // Replace with your MySQL password

        String sql = "INSERT INTO usersinformation (name, phone, email, college, department, cgpa, tenth_school, tenth_mark, tenth_percentage, twelfth_school, twelfth_mark, twelfth_percentage, resume) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set the values for each column
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, email);
            pstmt.setString(4, college);
            pstmt.setString(5, department);
            pstmt.setString(6, cgpa);
            pstmt.setString(7, tenthSchool);
            pstmt.setString(8, tenthMark);
            pstmt.setString(9, tenthPercentage);
            pstmt.setString(10, twelfthSchool);
            pstmt.setString(11, twelfthMark);
            pstmt.setString(12, twelfthPercentage);
            pstmt.setString(13, (resumeFile != null) ? resumeFile.getAbsolutePath() : null);

            // Execute the insert
            pstmt.executeUpdate();
            chatArea.append("Bot: Your information has been stored successfully.\n");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage());
        }
    }

    public void fillGoogleForm(String googleFormLink, String userName) {


        String jdbcUrl = "jdbc:mysql://localhost:3306/userdetails";
        String dbUsername = "root";
        String dbPassword = "Dhayalan@123";
        String sqlQuery = "SELECT * FROM usersinformation WHERE name = '" + userName + "'";

        try {
            // Step 1: Database connection
            Connection conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
            Statement stmt = conn.createStatement();

            // Step 2: Execute query
            ResultSet rs = stmt.executeQuery(sqlQuery);
            Map<String, String> userData = new HashMap<>();

            if (!rs.next()) {
                System.out.println("No user data found for the given query.");
            } else {
                userData.put("name", rs.getString("name"));
                userData.put("email", rs.getString("email"));
                userData.put("phone", rs.getString("phone"));
                userData.put("college", rs.getString("college"));
                userData.put("department", rs.getString("department"));
                userData.put("cgpa", rs.getString("cgpa"));
                userData.put("tenth_school", rs.getString("tenth_school"));
                userData.put("tenth_mark", rs.getString("tenth_mark"));
                userData.put("tenth_percentage", rs.getString("tenth_percentage"));
                userData.put("twelfth_school", rs.getString("twelfth_school"));
                userData.put("twelfth_mark", rs.getString("twelfth_mark"));
                userData.put("twelfth_percentage", rs.getString("twelfth_percentage"));

                // Step 3: WebDriver setup using WebDriverManager

                WebDriver driver = new ChromeDriver();

                // Step 4: Open the Google Form
                driver.get( googleFormLink);
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                  // Ensure the page has fully loaded
                 wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));

//                 Step 5: Dynamically find all input fields
                    java.util.List<WebElement> inputFields = driver.findElements(By.cssSelector("input[type='text'], input[type='email'], input[type='tel']"));

                    // Prepare data to fill (order is important if you don't have field labels)
                    String[] data = {
                            userData.get("name"),
                            userData.get("email"),
                            userData.get("phone"),
                            userData.get("college"),
                            userData.get("department"),
                            userData.get("cgpa"),
                            userData.get("tenth_school"),
                            userData.get("tenth_mark"),
                            userData.get("tenth_percentage"),
                            userData.get("twelfth_school"),
                            userData.get("twelfth_mark"),
                            userData.get("twelfth_percentage")
                    };// Add more fields here as needed
                // Step 6: Fill the form dynamically based on field count
                for (int i = 0; i < inputFields.size() && i < data.length; i++) {
                    WebElement field = inputFields.get(i);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", field);
                    wait.until(ExpectedConditions.elementToBeClickable(field));
                    field.sendKeys(data[i]);
                }

                // Step 6: Submit the form
                WebElement submitButton = driver.findElement(By.xpath("//span[text()='Submit']"));
                submitButton.click();

                // Close the browser
                driver.quit();
            }


            // Step 7: Close database connection
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    private boolean checkUserNameInDatabase(String name) {
        boolean userExists = false;

        // Database connection details
        String url = "jdbc:mysql://localhost:3306/userdetails"; // Replace with your DB name
        String user = "root";  // Replace with your MySQL username
        String password = "Dhayalan@123";  // Replace with your MySQL password

        String sql = "SELECT COUNT(*) FROM usersinformation WHERE name = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count > 0) {
                        userExists = true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error checking user: " + e.getMessage());
        }

        return userExists;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatBotFormApp());
    }
}

