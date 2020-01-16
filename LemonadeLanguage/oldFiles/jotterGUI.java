package mainfiles;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.image.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import javax.imageio.ImageIO;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.StyledDocument;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

@SuppressWarnings("serial")
class jotterGUI extends JFrame implements UndoableEditListener, Printable {
	
	static Font infotext = new Font("Arial", Font.PLAIN, 15);
	static Font editortext = new Font("OCR A Extended", Font.PLAIN, 15);
	
	public JTextArea consoleArea = new JTextArea();
	
	Process addJotType = null;
	
	//There are two JFrame instances which share the highlighter and related variables
	String jotName = "new1";
	String forToolTip = "";
	int endIndex = 0;
	int startIndex = 0;
	int sideBarCount = 0;

	Color highlightColor = Color.CYAN;
	Highlighter noteHilit = new DefaultHighlighter();

	@SuppressWarnings("rawtypes")
	ArrayList<ArrayList> storeHilits = new ArrayList<ArrayList>();

	static boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
	JFileChooser fc = new JFileChooser();
	FileNameExtensionFilter filter = new FileNameExtensionFilter(".jot", "jot");
	
	//For keeping track of state changes in the code editor:
	UndoManager forUndoAndRedo = new UndoManager();

	public static void main(String[] args) throws IOException, InterruptedException {
		
		jotterGUI editor = new jotterGUI();
		// Making a new instance of "jotter GUI" which is basically a JFrame
	
		// Setting the images
		BufferedImage bmpIcon = ImageIO.read(editor.getClass().getResourceAsStream("jotterIcontransparent.png"));
		BufferedImage bmpLogoload = ImageIO.read(editor.getClass().getResourceAsStream("jotterLogo.png"));
		BufferedImage bmpLogo = new BufferedImage(250, 150, BufferedImage.TYPE_INT_RGB);
		BufferedImage bmpRunload = ImageIO.read(editor.getClass().getResourceAsStream("jotterRun.png"));
		BufferedImage bmpRun = new BufferedImage(15, 15, BufferedImage.TRANSLUCENT);

		editor.initEditor(editor, bmpIcon, bmpLogoload, bmpLogo, bmpRunload, bmpRun);

	}

	@SuppressWarnings("static-access")
	public void initEditor(jotterGUI editor, BufferedImage bmpIcon, BufferedImage bmpLogoload, BufferedImage bmpLogo,
			BufferedImage bmpRunload, BufferedImage bmpRun) throws IOException {
		
		Highlighter hilit = new DefaultHighlighter();

		// For menu bar and submenus, and a few buttons
		JMenuBar mainMenu = new JMenuBar();
		JMenu fileMenu = new JMenu();
		JMenu helpMenu = new JMenu();
		JMenu editMenu = new JMenu();
		
		//Adding the start image to the runJot button
		Graphics forRunButton = bmpRun.createGraphics();
		forRunButton.drawImage(bmpRunload, 0, 0, 15, 15, null);
		ImageIcon forStoreRun = new ImageIcon(bmpRun);
		JButton runJot = new JButton("RUN", (Icon) forStoreRun);
		JButton endJot = new JButton("END");

		// For text field for coding and console, giving them scrollable properties
		JTextArea codeArea = new JTextArea();
		codeArea.getDocument().addUndoableEditListener(editor);
		codeArea.setBackground(Color.LIGHT_GRAY);
		codeArea.setFont(editortext);
		codeArea.setHighlighter(hilit);
		JScrollPane codeScroll = new JScrollPane(codeArea);
		codeScroll.setMaximumSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width, 300));
		codeScroll.setViewportView(codeArea);
		codeScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		codeScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		consoleArea.setBackground(Color.DARK_GRAY);
		consoleArea.setForeground(Color.WHITE);
		consoleArea.setFont(editortext);
		JScrollPane consoleScroll = new JScrollPane(consoleArea);
		consoleScroll.setMaximumSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width, 200));
		consoleScroll.setViewportView(consoleArea);
		consoleScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		consoleScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		//Adding the text areas for the console and coding area to a panel, including labels
		//to make everything look nice, and formatting for colour and alignment
		JPanel codeHolderPlusConsole = new JPanel();
		codeHolderPlusConsole.setLayout(new BoxLayout(codeHolderPlusConsole, BoxLayout.PAGE_AXIS));
		JLabel codeTitle = new JLabel();
		codeTitle.setBackground(Color.BLACK);
		codeTitle.setForeground(Color.RED);
		codeTitle.setOpaque(true);
		codeTitle.setText("new1.jot");
		codeTitle.setAlignmentX(codeTitle.CENTER_ALIGNMENT);
		codeHolderPlusConsole.add(codeTitle);
		codeHolderPlusConsole.add(codeScroll);
		JLabel consoleTitle = new JLabel();
		consoleTitle.setBackground(Color.BLACK);
		consoleTitle.setForeground(Color.RED);
		consoleTitle.setOpaque(true);
		consoleTitle.setText("CONSOLE");
		consoleTitle.setAlignmentX(consoleTitle.CENTER_ALIGNMENT);
		codeHolderPlusConsole.add(consoleTitle);
		codeHolderPlusConsole.add(consoleScroll);

		//FOR FILE MENU
		//NEW JOT OPTION:
		fileMenu.setText("File");
		JMenuItem newJot = new JMenuItem("New Jot", KeyEvent.VK_N);
		fileMenu.add(newJot);
		//Most buttons can be used through shortcuts
		newJot.setMnemonic(KeyEvent.VK_N);
		newJot.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		newJot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//When the user wants to make a new file, a dialog appears prompting for a valid name
				JOptionPane forNewFileName = new JOptionPane();
				String newFileInput = (String) forNewFileName.showInputDialog(editor,
						"What's the name of your new file?", "New Jot", JOptionPane.INFORMATION_MESSAGE, null, null,
						"");
				if (newFileInput != null && newFileInput.length() > 0
						&& newFileInput.charAt(newFileInput.length() - 1) != '.'
						&& newFileInput.charAt(newFileInput.length() - 1) != ' ' && !newFileInput.contains(">")
						&& !newFileInput.contains("<") && !newFileInput.contains(":") && !newFileInput.contains("\"")
						&& !newFileInput.contains("/") && !newFileInput.contains("\\") && !newFileInput.contains("|")
						&& !newFileInput.contains("?") && !newFileInput.contains("*")) {
					//If the input String is valid, a new file "appears" by clearing the coding area
					//and renaming one of the labels.
					jotName = newFileInput;
					codeTitle.setText(jotName + ".jot");
					codeArea.setText("");
					consoleArea.setText("");
				} else {
					forNewFileName.showMessageDialog(editor, "Please input a valid file name; operation cancelled.",
							"New Jot Cancelled", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		
		//OPEN JOT OPTION
		JMenuItem openJot = new JMenuItem("Open Jot", KeyEvent.VK_F10);
		fileMenu.add(openJot);
		openJot.setMnemonic(KeyEvent.VK_F10);
		openJot.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0));
		openJot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fc.setDialogTitle("Open Jot");
				//By opening a file, whatever is in the code area will get overwritten
				int returnVal = fc.showOpenDialog(editor);
				//Creates a file opening dialog that applies the selected file types to filter
				fc.removeChoosableFileFilter(fc.getAcceptAllFileFilter());
				fc.addChoosableFileFilter(filter);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fc.getSelectedFile();
					//Gets file name and tries reading the file to the code area.
					try {
						FileReader inputJot = new FileReader(selectedFile);
						codeArea.read(inputJot, null);
						inputJot.close();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					jotName = selectedFile.getName();
					codeTitle.setText(jotName);
				}
			}
		});
		//SAVE JOT OPTION
		JMenuItem saveJot = new JMenuItem("Save Jot", KeyEvent.VK_S);
		fileMenu.add(saveJot);
		saveJot.setMnemonic(KeyEvent.VK_S);
		saveJot.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveJot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Opens a dialog similar to the opening file one (it also applies the same
				//file filter), however, this time the action is for writing out to a file
				fc.setDialogTitle("Save Jot");
				fc.setSelectedFile(new File(jotName));
				int returnVal = fc.showSaveDialog(editor);
				fc.removeChoosableFileFilter(fc.getAcceptAllFileFilter());
				fc.addChoosableFileFilter(filter);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					try {
						PrintWriter outputTheJot = new PrintWriter(fc.getSelectedFile() + ".jot", "UTF-8");
						outputTheJot.write(codeArea.getText());
						//Writes code area out to file
						outputTheJot.close();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		//PRINT JOT OPTION
		JMenuItem printJot = new JMenuItem("Print", KeyEvent.VK_P);
		fileMenu.add(printJot);
		printJot.setMnemonic(KeyEvent.VK_P);
		printJot.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		printJot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Opens a new print dialog which takes the code area and sends it to a code document.
				JOptionPane printTip = new JOptionPane();
				printTip.showMessageDialog(editor,
						"If you want a PDF, please select 'Microsoft Print to PDF' and not the 'print to file' checkbox.",
						"Print Tip", JOptionPane.INFORMATION_MESSAGE);
					//Just an FYI message for printing to PDF
				try {
					PrinterJob job = PrinterJob.getPrinterJob();
					boolean doPrint = job.printDialog();
					if (doPrint) {
						job.setPrintable(codeArea.getPrintable(null, null));
						job.setJobName("jotter Code");
						job.print();
					}

				} catch (PrinterException e1) {
					e1.printStackTrace();
				}
			}
		});
		//EXIT JOT OPTION
		JMenuItem exitJot = new JMenuItem("Exit", KeyEvent.VK_F4);
		fileMenu.add(exitJot);
		exitJot.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK));
		exitJot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//This is another way of closing down the program.
				editor.dispatchEvent(new WindowEvent(editor, WindowEvent.WINDOW_CLOSING));
			}
		});
		
		//FOR THE HELP MENU
		//SEE CREDITS OPTION
		helpMenu.setText("Help");
		JMenuItem seeCredits = new JMenuItem();
		seeCredits.setText("Credits");
		helpMenu.add(seeCredits);
		seeCredits.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Basically, all JFrames used are created from this class, 
				//but have their look and feel made in different initiation methods.
				//This is for opening the credits window.
				editor.initCredits(bmpIcon, bmpLogoload, bmpLogo);
			}
		});
		//SEE DOCUMENTATION OPTION
		JMenuItem seeDoc = new JMenuItem();
		seeDoc.setText("Documentation");
		helpMenu.add(seeDoc);
		seeDoc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//This is for opening the documentation window.
				editor.initDocumentation(bmpIcon, bmpLogoload, bmpLogo);
			}
		});

		//FOR EDIT MENU:
		editMenu.setText("Edit");
		//CUT OPTION
		JMenuItem doCut = new JMenuItem("Cut", KeyEvent.VK_X);
		editMenu.add(doCut);
		doCut.setMnemonic(KeyEvent.VK_X);
		doCut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		doCut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//When the user selects text (highlights with cursor) in the code area,
				//the text can be cut out.
				codeArea.cut();
			}
		});
		//COPY OPTION
		JMenuItem doCopy = new JMenuItem("Copy", KeyEvent.VK_C);
		editMenu.add(doCopy);
		doCopy.setMnemonic(KeyEvent.VK_C);
		doCopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		doCopy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//The user can also copy selected text (highlights with cursor).
				codeArea.copy();
			}
		});
		//PASTE OPTION
		JMenuItem doPaste = new JMenuItem("Paste", KeyEvent.VK_V);
		editMenu.add(doPaste);
		doPaste.setMnemonic(KeyEvent.VK_V);
		doPaste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		doPaste.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Takes whatever text was put into the clipboard from other cut and copy functions
				//and puts it into the code area. 
				codeArea.paste();
			}
		});
		//UNDO OPTION
		editMenu.addSeparator();
		JMenuItem doUndo = new JMenuItem("Undo", KeyEvent.VK_V);
		editMenu.add(doUndo);
		doUndo.setMnemonic(KeyEvent.VK_Z);
		doUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		doUndo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!forUndoAndRedo.canUndo()) {
					doUndo.setForeground(Color.lightGray);
				} else {
					doUndo.setForeground(Color.black);
				}
				try {
					//Depending on the amount of dos and undos done, the button greys out
					//appropriately. Using the undo/redo manager, I can get states of the 
					//code area and apply them.
					forUndoAndRedo.undo();
				} catch (CannotUndoException ex) {
					JOptionPane errorUndoMessage = new JOptionPane();
					errorUndoMessage.showMessageDialog(editor, "The last action cannot be undone.", "Unable to Undo",
							JOptionPane.ERROR_MESSAGE);
				}

			}
		});
		//REDO OPTION
		JMenuItem doRedo = new JMenuItem("Redo", KeyEvent.VK_Y);
		editMenu.add(doRedo);
		doRedo.setMnemonic(KeyEvent.VK_Y);
		doRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		doRedo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!forUndoAndRedo.canRedo()) {
					doRedo.setForeground(Color.lightGray);
				} else {
					doRedo.setForeground(Color.black);
				}
				try {
					forUndoAndRedo.redo();
					//Similar to the UNDO OPTION, except that instead of going forward through
					//edits this enables users to go back.
				} catch (CannotRedoException ex) {
					JOptionPane errorUndoMessage = new JOptionPane();
					errorUndoMessage.showMessageDialog(editor, "The last action cannot be redone.", "Unable to Redo",
							JOptionPane.ERROR_MESSAGE);
					//A message dialog appears for both if actions cannot be done or redone.
				}
			}
		});

		editMenu.addSeparator();
		//FIND OPTION
		JMenuItem doFind = new JMenuItem("Find");
		editMenu.add(doFind);
		doFind.setMnemonic(KeyEvent.VK_F);
		doFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		doFind.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane forFinding = new JOptionPane();
				String findInput = (String) forFinding.showInputDialog(editor, "Input your keyword:", "Find",
						JOptionPane.INFORMATION_MESSAGE, null, null, "");
				String compareText = codeArea.getText();
				int counter = 0;
				//The user can find specific words or phrases (String input) - first store input
				HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.green);

				if (compareText.contains(findInput)) {
					
					while (compareText.lastIndexOf(findInput) >= 0) {
						int index = compareText.lastIndexOf(findInput);
						int end = index + findInput.length();
						//Adding highlights to instances of the found words, 
						//keeping track of word positions
						counter++;
						try {
							hilit.addHighlight(index, end, painter);
						} catch (BadLocationException e1) {
							e1.printStackTrace();
						}
						codeArea.setCaretPosition(end);

						compareText = compareText.substring(0, index);
					}
					Object[] searchOptions = { "Search Again", "Instances" };
					int returnVal = forFinding.showOptionDialog(editor,
							"Would you like to continue finding other things?", "Find", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, searchOptions, searchOptions[0]);
					//Another dialog pops up enabling users to search again, and see
					//how many times their word/phrase appeared in the code.
					if (returnVal == JOptionPane.CLOSED_OPTION) {
						hilit.removeAllHighlights();
						//When out of finding, the highlights are removed,
						//as well as when searching for a new String
					}
					if (returnVal == JOptionPane.YES_OPTION) {
						hilit.removeAllHighlights();
						doFind.doClick();
					}
					if (returnVal == JOptionPane.NO_OPTION) {
						forFinding.showMessageDialog(editor,
								"There are " + counter + " instance(s) of your search text.", "Find",
								JOptionPane.INFORMATION_MESSAGE);
						//Showing the counter
						hilit.removeAllHighlights();
					}
				} else {
					forFinding.showMessageDialog(editor, "The text cannot be found.", "Null Search",
							JOptionPane.WARNING_MESSAGE);
					//If the text cannot be found, there is another dialog message.
				}

			}
		});

		//JOURNAL OPTION
		JMenuItem forJournal = new JMenuItem();
		forJournal.setText("Journal");
		forJournal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sideBarCount = 0;
				try {
					//Creates a new window for the Journal:
					editor.initNotes(bmpIcon, codeArea.getText(), editor);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		editMenu.add(forJournal);

		//RUN OPTION
		runJot.setFocusPainted(false);
		runJot.setBorderPainted(false);
		runJot.setBackground(new Color(238, 238, 238));
		runJot.setOpaque(false);
		runJot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				String actualIn = consoleArea.getText();
				String[] storeInput = actualIn.split("\\s+");
				for (int i = 0; i < storeInput.length; i++) {
					System.out.println(storeInput[i]);
				}
				File outputFile = new File("input.txt");
				Writer outputWrite = null;
				try {
					outputWrite = new BufferedWriter(new FileWriter(outputFile));
					//Writing to an input.txt file than can be seen by the ANTLR 
					//custom listener.
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				for (int i = 0; i < storeInput.length; i++) {
					try {
						outputWrite.write(storeInput[i] + '\r' + '\n');
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				try {
					outputWrite.close();
				} catch (IOException e1) {
				}
				
				String userProgram = codeArea.getText();
				CharStream userToChar = CharStreams.fromString(userProgram);
				
				//Making new instances of the ANTLR lexer, parser, and beginning parsing
				jotterGrammarLexer lexer = new jotterGrammarLexer(userToChar);

				CommonTokenStream tokens = new CommonTokenStream(lexer);

				jotterGrammarParser parser = new jotterGrammarParser(tokens);
				
				ParseTree tree = parser.parse();
				
				//ALso generates a tree to see where the code is going:
				TreeViewer viewr = new TreeViewer(Arrays.asList(parser.getRuleNames()), tree);
		        viewr.open();
				
		        //Reading whatever output there is
				  File inputFile = new File("output.txt"); 
				  Scanner inputRead = null; 
				  try {
					  inputRead = new Scanner(inputFile); 
				  } catch (FileNotFoundException e1) {
					  e1.printStackTrace(); 
				  } 
				  ArrayList<String> storeWords = new ArrayList<String>(); 
				  while (inputRead.hasNextLine()) { 
					  String lineContent = inputRead.nextLine();
					  if (lineContent != null) { 
						  storeWords.add(lineContent); 
					  } 
				  } 
				  String place = ""; 
				  for (int i = 0; i < storeWords.size(); i++) { 
					  place = place.concat(storeWords.get(i)); 
				  }
				  consoleArea.setText(place);
			}
		});

		//FOR END BUTTON
		endJot.setFocusPainted(false);
		endJot.setBorderPainted(false);
		endJot.setBackground(new Color(238, 238, 238));
		endJot.setOpaque(false);
		endJot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//stopping the program
			}
		});
		
		//Adding the submenus to the main menu bar
		mainMenu.add(fileMenu);
		mainMenu.add(editMenu);
		mainMenu.add(helpMenu);
		mainMenu.add(Box.createHorizontalGlue());
		mainMenu.add(runJot);
		mainMenu.add(endJot);

		//Setting the menu bar for the main JFrame:
		editor.setJMenuBar(mainMenu);
		editor.add(codeHolderPlusConsole, BorderLayout.CENTER);
		editor.setContentPane(codeHolderPlusConsole);
		editor.getContentPane().setBackground(Color.BLACK);
		editor.setDefaultCloseOperation(EXIT_ON_CLOSE);
		editor.pack();
		//Setting size, title, and icon image
		editor.setSize(800, 500);
		editor.setTitle("Jotter");
		editor.setIconImage(bmpIcon);
		editor.setVisible(true);

	}

	public void initNotes(BufferedImage bmpIcon, String codeRetrieve, jotterGUI holder) throws IOException {
		//Making the frame for the Notes GUI
		jotterGUI notes = new jotterGUI();

		JMenuBar notesEdit = new JMenuBar();
		
		//For storing highlights~
		ArrayList<Object> tempStoreHilits = new ArrayList<Object>();
		
		//There's one area to hold the code of the code area on the original frame.
		JTextArea holdCode = new JTextArea();
		holdCode.setText(codeRetrieve);
		holdCode.setFont(editortext);
		holdCode.setEditable(false);
		holdCode.setHighlighter(noteHilit);
		for (int i = 0; i < storeHilits.size(); i++) {
			try {
				//Applies highlights that were made previously.
				tempStoreHilits.add(noteHilit.addHighlight((int)storeHilits.get(i).get(1), (int)storeHilits.get(i).get(2),
						new DefaultHighlighter.DefaultHighlightPainter((Color) storeHilits.get(i).get(4))));
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}
		//When a highlight is made with a note, clicking over the highlight will
		//result in a tooltip text with the note.
		MouseListener rollOver = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int mousePos = holdCode.viewToModel2D(e.getPoint());
				for (int i = 0; i < storeHilits.size(); i++) {
					if (mousePos >= (int) storeHilits.get(i).get(1) && mousePos <= (int) storeHilits.get(i).get(2)) {
						holdCode.setToolTipText(i + 1 + ". " + (String) storeHilits.get(i).get(3));
					}
				}
			}
		};

		holdCode.addMouseListener(rollOver);
		
		//Adding a scroll pane to the code holder.
		JScrollPane noteScroll = new JScrollPane(holdCode);
		noteScroll.setMaximumSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width,
				Toolkit.getDefaultToolkit().getScreenSize().height));
		noteScroll.setViewportView(holdCode);
		noteScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		noteScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		//Main panel of the frame:
		JPanel notebookHolder = new JPanel();
		notebookHolder.setLayout(new BoxLayout(notebookHolder, BoxLayout.X_AXIS));
		notebookHolder.add(noteScroll);

		MouseListener addH = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				startIndex = holdCode.getCaretPosition();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				//This is a mouse listener for when making a highlight.
				//The user must select text by highlighting with the cursor, and then
				//a dialog will appear prompting for the respective note.
				endIndex = holdCode.getCaretPosition();
				if (holdCode.getSelectedText() != null) {
					try {
						ArrayList<Object> tempData = new ArrayList<Object>();
						tempData.add(noteHilit.addHighlight(startIndex, endIndex,
								new DefaultHighlighter.DefaultHighlightPainter(highlightColor)));
						tempData.add(startIndex);
						tempData.add(endIndex);
						String userNote = JOptionPane.showInputDialog(notes, "Please type your note below:", "Set Note",
								JOptionPane.INFORMATION_MESSAGE);
						tempData.add(userNote);
						tempData.add(highlightColor);
						storeHilits.add(tempData);
						forToolTip = userNote;
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
				}
			}
		};
		MouseListener removeH = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int mousePos = holdCode.viewToModel2D(e.getPoint());
				for (int i = 0; i < storeHilits.size(); i++) {
					if (mousePos >= (int) storeHilits.get(i).get(1) && mousePos <= (int) storeHilits.get(i).get(2)) {
						noteHilit.removeHighlight(storeHilits.get(i).get(0));
						storeHilits.remove(i);
						noteHilit.removeHighlight(tempStoreHilits.get(i));
						tempStoreHilits.remove(i);
					}
				}
			}
		};
		JMenu forHighlight = new JMenu("Highlighter");
		
		//There is a menu item which generates a colour chooser with which to pick 
		//the highlighter colour.
		JMenuItem setHighlightColour = new JMenuItem();
		setHighlightColour.setText("Set Highlight Colour");
		forHighlight.add(setHighlightColour);
		setHighlightColour.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				highlightColor = JColorChooser.showDialog(holder, "Highlighter Colour", null);
				holdCode.removeMouseListener(addH);
				holdCode.removeMouseListener(removeH);
			}
		});

		JMenuItem addHighlight = new JMenuItem();
		addHighlight.setText("Add Highlight");
		forHighlight.add(addHighlight);
		addHighlight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//When adding a highlight, one mouselistener is removed,
				//and the other is added
				holdCode.addMouseListener(addH);
				holdCode.removeMouseListener(removeH);
			}
		});

		JMenuItem removeHighlight = new JMenuItem();
		removeHighlight.setText("Remove Highlight");
		forHighlight.add(removeHighlight);
		removeHighlight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				holdCode.removeMouseListener(addH);
				holdCode.addMouseListener(removeH);
			}
		});
		
		//There is a panel, a label, and a text pane for viewing the notes
		//instead of using the tooltiptext.
		JPanel viewNotesPanel = new JPanel();
		viewNotesPanel.setLayout(new BoxLayout(viewNotesPanel, BoxLayout.PAGE_AXIS));
		
		JLabel seeNotes = new JLabel("YOUR NOTES");
		seeNotes.setForeground(Color.DARK_GRAY);
		seeNotes.setOpaque(true);
		seeNotes.setAlignmentX(Component.CENTER_ALIGNMENT);

		JTextPane noteHold = new JTextPane();
		noteHold.setBackground(Color.gray);
		noteHold.setForeground(Color.white);
		noteHold.setFont(infotext);

		viewNotesPanel.add(seeNotes);
		viewNotesPanel.add(noteHold);

		JButton viewNoteSidebar = new JButton("View Note Sidebar");
		viewNoteSidebar.setFocusPainted(false);
		viewNoteSidebar.setBorderPainted(false);
		viewNoteSidebar.setBackground(new Color(238, 238, 238));
		viewNoteSidebar.setOpaque(false);

		viewNoteSidebar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				holdCode.removeMouseListener(addH);
				sideBarCount++;
				if (sideBarCount % 2 != 0) {
					//With viewing/closing the sidebar button, the panel
					//hides and shows itself depending on the actions.
					viewNoteSidebar.setText("Close Note Sidebar");
					viewNotesPanel.setVisible(true);
					String noteTemp = "";
					for (int i = 0; i < storeHilits.size(); i++) {
						noteTemp = noteTemp.concat(i+1 + ". " + (String) storeHilits.get(i).get(3) + "\n");
					}
					noteHold.setText(noteTemp);
					noteHold.setEditable(false);
					if (sideBarCount == 1) {
						notebookHolder.add(viewNotesPanel);
					}
				}
				if (sideBarCount % 2 == 0) {
					viewNoteSidebar.setText("View Note Sidebar");
					viewNotesPanel.setVisible(false);
				}
			}
		});
		JButton forPrintingNotes = new JButton("Print Notes");
		forPrintingNotes.setMnemonic(KeyEvent.VK_P);
		forPrintingNotes.setFocusPainted(false);
		forPrintingNotes.setBorderPainted(false);
		forPrintingNotes.setBackground(new Color(238, 238, 238));
		forPrintingNotes.setOpaque(false);
		forPrintingNotes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				holdCode.removeMouseListener(addH);
				JOptionPane.showMessageDialog(notes,
						"If you want a PDF, please select 'Microsoft Print to PDF' and not the 'print to file' checkbox.",
						"Print Tip", JOptionPane.INFORMATION_MESSAGE);
				try {
					PrinterJob job = PrinterJob.getPrinterJob();
					boolean doPrint = job.printDialog();
					if (doPrint) {
						job.setPrintable(holdCode.getPrintable(null, null));
						job.setJobName("jotter Journal");
						job.print();
					}

				} catch (PrinterException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		//Adding parts to the frame:
		notesEdit.add(forHighlight);
		notesEdit.add(viewNoteSidebar);
		notesEdit.add(forPrintingNotes);
		
		//Settings aspects of the frame:
		notes.setJMenuBar(notesEdit);
		notes.add(notebookHolder, BorderLayout.CENTER);
		notes.setContentPane(notebookHolder);
		notes.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		notes.pack();
		notes.setSize(400, 400);
		notes.setTitle("Your Journal");
		notes.setIconImage(bmpIcon);
		notes.setVisible(true);

	}

	public void initCredits(BufferedImage bmpIcon, BufferedImage bmpLogoload, BufferedImage bmpLogo) {
		
		//Making the credits frame:
		jotterGUI credits = new jotterGUI();

		JPanel itemHolder = new JPanel();
		itemHolder.setLayout(new BoxLayout(itemHolder, BoxLayout.PAGE_AXIS));
		
		//Placing an image of the logo within the frame with some graphics:
		Graphics forLogoResize = bmpLogo.createGraphics();
		forLogoResize.drawImage(bmpLogoload, 0, 0, 250, 150, null);
		ImageIcon forStoreLogo = new ImageIcon(bmpLogo);
		JLabel showLogo = new JLabel((Icon) forStoreLogo);
		showLogo.setAlignmentX(CENTER_ALIGNMENT);
		itemHolder.add(showLogo);
		
		//A small credits section in an uneditable text area:
		JTextArea words = new JTextArea();
		words.setText("Final project of Simran Thind, ICS4U 2019!" + "\n" 
		+ "This is a basic code editor for the 'jotter' language." + "\n"
		+ "It's still a work in progress!");
		words.setLineWrap(true);
		words.setEditable(false);
		itemHolder.add(words);
		
		//Setting aspects of the frame like size, title, and adding components:
		credits.add(itemHolder);
		credits.setContentPane(itemHolder);
		credits.getContentPane().setBackground(Color.WHITE);
		credits.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		credits.pack();
		credits.setSize(300, 300);
		credits.setResizable(false);
		credits.setTitle("Jotter Credits");
		credits.setIconImage(bmpIcon);
		credits.setVisible(true);

	}

	public void initDocumentation(BufferedImage bmpIcon, BufferedImage bmpLogoload, BufferedImage bmpLogo) {
		
		//Making the documentation frame:
		jotterGUI doc = new jotterGUI();
		
		//This text area with a lot of writing is basically examples of the language:
		JTextArea usingJot = new JTextArea();
		usingJot.setEditable(false);
		usingJot.setText("DECLARING VARIABLES:" + "\n" + 
				"examples: int num1 = 3, dec 2num = 4.56, str word = \"Hey\", bol check = false"
				+ "\n" + "Variable names can only include a combination of letters and numbers." + "\n"
				+ "PRINTING TO CONSOLE:" + "\n" + 
				"Console.print(...)" + "\n" + 
				"INPUTTING FROM CONSOLE:" + "\n" + 
				"Console.input" + "\n" + 
				"This method needs for input to be put into the console area before running."
				+ "\n" + "GENERATE ARRAY OF RANDOM NUMBERS:" + "\n"
				+ "Random.getNum(0, 10, 3)" + "\n"
				+ "The first two numbers define the range, while the third is the amount."
				+ "\n" + "RANDOM PICK FROM ARRAY:" + "\n" +
				"Random.pick(myNumbers)" + "\n" + 
				"This is for picking randomly from an array." + "\n"
				+ "COMMENTING:" + "\n" + "#This is a comment#" + "\n"
				+ "DECLARING ARRAYS:" + "\n" + "str[2] words = {\"hi\", \"hello\"}"
				+ "\n" + "LENGTH OF ARRAY:" + "\n" + "words.length" + "\n"
				+ "GET ELEMENT OF ARRAY:" + "\n" + "words[2]" + "\n"
				+ "ADD ELEMENT TO ARRAY:" + "\n" + "words.insert(\"yo\")" + "\n"
				+ "REMOVE ELEMENT FROM ARRAY:" + "\n" + "words.remove(1)" + "\n"
				+ "Where the number is the index number." + "\n" +
				"CHANGING ELEMENT:" + "\n" + "words[1] = \"hey\"" + "\n"
				+ "SORT ARRAY ASCENDING:" + "\n" + "words.sort" + "\n"
				+ "GETTING PRIME NUMBERS FROM ARRAY:" + "\n" + "nums.prime" + "\n"
				+ "FINDING ELEMENT IN ARRAY:" + "\n" + "nums.find(123)" + "\n" + 
				"ADDITION:" + "\n" + "a++, a+= 3, a = a+3" + "\n" + 
				"SUBTRACTION:" + "\n" + "b--, b -= 4, b = b - 4" + "\n" + 
				"MULTIPLICATION:" + "\n" + " c *= 4, c = c * 4" + "/n" + 
				"DIVISION:" + "\n" + "d /= 6, d = d/6");
		
		//Applying scroll bars to the text area to make it more readable
		JScrollPane noteScroll = new JScrollPane(usingJot);
		noteScroll.setMaximumSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width,
				Toolkit.getDefaultToolkit().getScreenSize().height));
		noteScroll.setViewportView(usingJot);
		noteScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		noteScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		//Setting aspects of the frame:
		doc.add(noteScroll);
		doc.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		doc.pack();
		doc.setSize(300, 300);
		doc.setTitle("Jotter Documentation");
		doc.setIconImage(bmpIcon);
		doc.setVisible(true);

	}

	@Override
	public void undoableEditHappened(UndoableEditEvent e) {
		//Getting an edit from the undo/redo manager
		forUndoAndRedo.addEdit(e.getEdit());
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
		//Checking if the print dialog is ok
		return 0;
	}

}
