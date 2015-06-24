package scope.data;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class InitialFrame {

	public static JFrame frame;
	private JRadioButton MySQLButton= new JRadioButton("MySQL database");;
	private JRadioButton MongoDBButton = new JRadioButton("MongoDB database");
	private JButton startButton = new JButton("Select");
	protected static JTextField textField_ServerAddress;
	protected static JTextField textField_Port;
	protected static JTextField textField_DatabaseName;
	protected static JTextField textField_UserInstance;
	protected static JTextField textField_Password;
	private JLabel label_ServerAddress;
	private JLabel label_Port;
	private JLabel label_DatabaseName;
	private JLabel label_UserInstance;
	private JLabel label_Password;
	private JPanel panel_buttons;
	private JButton cancelButton;
	private JButton defaultButton;


	/**
	 * Create the application.
	 */
	public InitialFrame() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(200, 200, 450, 240);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel = new JLabel("Choose a database:");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel.setPreferredSize(new Dimension(97, 25));
		lblNewLabel.setMaximumSize(new Dimension(97, 25));
		lblNewLabel.setBounds(new Rectangle(0, 0, 0, 30));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblNewLabel, BorderLayout.NORTH);
		
		JPanel panel_radiobuttons = new JPanel();
		panel_radiobuttons.setMaximumSize(new Dimension(32767, 60));
		frame.getContentPane().add(panel_radiobuttons, BorderLayout.CENTER);
		GridBagLayout gbl_panel_radiobuttons = new GridBagLayout();
		gbl_panel_radiobuttons.columnWidths = new int[]{434, 0};
		gbl_panel_radiobuttons.rowHeights = new int[] {25, 25, 60, 0};
		gbl_panel_radiobuttons.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panel_radiobuttons.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_radiobuttons.setLayout(gbl_panel_radiobuttons);
		MySQLButton.setMaximumSize(new Dimension(107, 20));
		MySQLButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
		MySQLButton.setMinimumSize(new Dimension(107, 12));
		MySQLButton.setPreferredSize(new Dimension(107, 12));
		
		MySQLButton.setHorizontalAlignment(SwingConstants.CENTER);
		MySQLButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if(arg0.getStateChange()==1){
					MongoDBButton.setSelected(false);
					MongoDBButton.setEnabled(true);
					MySQLButton.setEnabled(false);
					textField_ServerAddress.setVisible(true);			
					textField_Port.setVisible(true);
					textField_DatabaseName.setVisible(true);
					textField_UserInstance.setVisible(true);
					textField_Password.setVisible(true);	
					label_ServerAddress.setVisible(true);
					label_Port.setVisible(true);
					label_DatabaseName.setVisible(true);
					label_UserInstance.setVisible(true);
					label_Password.setVisible(true);
					if(!cancelButton.isVisible()){
						textField_ServerAddress.setText("localhost");
						textField_Port.setText(3306+"");
						textField_DatabaseName.setText("java_database");
						textField_UserInstance.setText("root");
						textField_Password.setText("asdf");
					}
					if(startButton.isVisible()==false){
						startButton.setVisible(true);
						defaultButton.setVisible(true);
						frame.setBounds(200, 200, 450, 240);
					}
				}
			}
		});
		GridBagConstraints gbc_MySQLButton = new GridBagConstraints();
		gbc_MySQLButton.fill = GridBagConstraints.BOTH;
		gbc_MySQLButton.insets = new Insets(0, 0, 5, 0);
		gbc_MySQLButton.gridx = 0;
		gbc_MySQLButton.gridy = 0;
		panel_radiobuttons.add(MySQLButton, gbc_MySQLButton);
		MongoDBButton.setMaximumSize(new Dimension(119, 20));
		MongoDBButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
		MongoDBButton.setPreferredSize(new Dimension(119, 12));
		MongoDBButton.setMinimumSize(new Dimension(119, 12));
		
		MongoDBButton.setHorizontalAlignment(SwingConstants.CENTER);
		MongoDBButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if(arg0.getStateChange()==1){
					MySQLButton.setSelected(false);
					MySQLButton.setEnabled(true);
					MongoDBButton.setEnabled(false);
					textField_ServerAddress.setVisible(true);		
					textField_Port.setVisible(true);					
					textField_DatabaseName.setVisible(true);
					textField_UserInstance.setVisible(false);
					textField_Password.setVisible(false);
					label_ServerAddress.setVisible(true);
					label_Port.setVisible(true);
					label_DatabaseName.setVisible(true);
					label_UserInstance.setVisible(false);
					label_Password.setVisible(false);
					if(!cancelButton.isVisible()){
						textField_ServerAddress.setText("localhost");
						textField_Port.setText(27017+"");
						textField_DatabaseName.setText("database");
					}
					if(startButton.isVisible()==false){
						startButton.setVisible(true);
						defaultButton.setVisible(true);
						frame.setBounds(200, 200, 450, 240);
					}
				}
			}
		});
		GridBagConstraints gbc_MongoDBButton = new GridBagConstraints();
		gbc_MongoDBButton.fill = GridBagConstraints.BOTH;
		gbc_MongoDBButton.insets = new Insets(0, 0, 5, 0);
		gbc_MongoDBButton.gridx = 0;
		gbc_MongoDBButton.gridy = 1;
		panel_radiobuttons.add(MongoDBButton, gbc_MongoDBButton);
		
		JPanel panel_textfields = new JPanel();
		GridBagConstraints gbc_panel_textfields = new GridBagConstraints();
		gbc_panel_textfields.fill = GridBagConstraints.BOTH;
		gbc_panel_textfields.gridx = 0;
		gbc_panel_textfields.gridy = 2;
		panel_radiobuttons.add(panel_textfields, gbc_panel_textfields);
		panel_textfields.setMaximumSize(new Dimension(32767, 60));
		panel_textfields.setLayout(new GridLayout(5, 2, 0, 0));
		
		label_ServerAddress = new JLabel("Server address");
		label_ServerAddress.setVisible(false);
		label_ServerAddress.setHorizontalAlignment(SwingConstants.CENTER);
		label_ServerAddress.setPreferredSize(new Dimension(0, 20));
		label_ServerAddress.setMinimumSize(new Dimension(73, 20));
		label_ServerAddress.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel_textfields.add(label_ServerAddress);
		
		textField_ServerAddress = new JTextField();
		textField_ServerAddress.setVisible(false);
		textField_ServerAddress.setPreferredSize(new Dimension(0, 20));
		textField_ServerAddress.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textField_ServerAddress.setColumns(10);
		panel_textfields.add(textField_ServerAddress);
		
		label_Port = new JLabel("Port");
		label_Port.setVisible(false);
		label_Port.setHorizontalAlignment(SwingConstants.CENTER);
		label_Port.setPreferredSize(new Dimension(0, 20));
		label_Port.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel_textfields.add(label_Port);
		
		textField_Port = new JTextField();
		textField_Port.setVisible(false);
		textField_Port.setPreferredSize(new Dimension(0, 20));
		textField_Port.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textField_Port.setColumns(10);
		panel_textfields.add(textField_Port);
		
		label_DatabaseName = new JLabel("Database Name");
		label_DatabaseName.setVisible(false);
		label_DatabaseName.setHorizontalAlignment(SwingConstants.CENTER);
		label_DatabaseName.setPreferredSize(new Dimension(0, 20));
		label_DatabaseName.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel_textfields.add(label_DatabaseName);
		
		textField_DatabaseName = new JTextField();
		textField_DatabaseName.setVisible(false);
		textField_DatabaseName.setPreferredSize(new Dimension(0, 20));
		textField_DatabaseName.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textField_DatabaseName.setColumns(10);
		panel_textfields.add(textField_DatabaseName);
		
		label_UserInstance = new JLabel("User instance");
		label_UserInstance.setVisible(false);
		label_UserInstance.setHorizontalAlignment(SwingConstants.CENTER);
		label_UserInstance.setPreferredSize(new Dimension(0, 20));
		label_UserInstance.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel_textfields.add(label_UserInstance);
		
		textField_UserInstance = new JTextField();
		textField_UserInstance.setVisible(false);
		textField_UserInstance.setPreferredSize(new Dimension(0, 20));
		textField_UserInstance.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textField_UserInstance.setColumns(10);
		panel_textfields.add(textField_UserInstance);
		
		label_Password = new JLabel("Password");
		label_Password.setVisible(false);
		label_Password.setHorizontalAlignment(SwingConstants.CENTER);
		label_Password.setPreferredSize(new Dimension(0, 20));
		label_Password.setFont(new Font("Tahoma", Font.PLAIN, 12));
		panel_textfields.add(label_Password);
		
		textField_Password = new JTextField();
		textField_Password.setVisible(false);
		textField_Password.setPreferredSize(new Dimension(0, 20));
		textField_Password.setFont(new Font("Tahoma", Font.PLAIN, 12));
		textField_Password.setColumns(10);
		panel_textfields.add(textField_Password);
		
		panel_buttons = new JPanel();
		frame.getContentPane().add(panel_buttons, BorderLayout.SOUTH);
	
		startButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
		startButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				boolean test = false;
				if(MySQLButton.isSelected()){
					ImportButton.databaseSelected="MySQL";
					test = SQL.testConnection();
				}
				else if(MongoDBButton.isSelected()){
					ImportButton.databaseSelected="MongoDB";
					test = MongoDB.testConnection();
				}
				if(test==true){
					frame.setVisible(false);
					ImportButton.ImportCommand();
					cancelButton.setVisible(true);
					
				}
				else{
					JOptionPane.showMessageDialog(null, "No database found", "Incorrect parameters or driver problem", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		startButton.setVisible(false);
		panel_buttons.add(startButton);
		
		cancelButton = new JButton("Cancel");
		cancelButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
					frame.setVisible(false);
			}
		});
		panel_buttons.add(cancelButton);
		
		defaultButton = new JButton("Set default parameters");
		defaultButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(MySQLButton.isSelected()){
					textField_ServerAddress.setText("localhost");
					textField_Port.setText(3306+"");
					textField_DatabaseName.setText("java_database");
					textField_UserInstance.setText("root");
					textField_Password.setText("asdf");
				}
				else if(MongoDBButton.isSelected()){
					textField_ServerAddress.setText("localhost");
					textField_Port.setText(27017+"");
					textField_DatabaseName.setText("database");
				}
			}
		});
		
		panel_buttons.add(defaultButton);
	}

}
