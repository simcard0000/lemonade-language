package mainfiles;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import org.antlr.v4.runtime.tree.TerminalNodeImpl;

public class jotterGrammarCustomListener extends jotterGrammarBaseListener {
	
	//For keeping track of what needs to be output
	ArrayList<String> keepWords = new ArrayList<String>();
	int inputInstanceCount = -1;
	
	//For storing all the variables, arrays, and other things that would return data
	HashMap<String, Integer> variableIntegerMap = new HashMap<String, Integer>();
	HashMap<String, Float> variableDecimalMap = new HashMap<String, Float>();
	HashMap<String, String> variableStringMap = new HashMap<String, String>();
	HashMap<String, Boolean> variableBooleanMap = new HashMap<String, Boolean>();

	HashMap<String, Object> variablePickTempMap = new HashMap<String, Object>();
	HashMap<String, int[]> variableRandomTempMap = new HashMap<String, int[]>();

	HashMap<String, int[]> variableArrayIntMap = new HashMap<String, int[]>();
	HashMap<String, float[]> variableArrayDecMap = new HashMap<String, float[]>();
	HashMap<String, String[]> variableArrayStrMap = new HashMap<String, String[]>();
	HashMap<String, boolean[]> variableArrayBolMap = new HashMap<String, boolean[]>();

	HashMap<String, Integer> arrayLengthMap = new HashMap<String, Integer>();
	HashMap<String, Object> arrayElementTempMap = new HashMap<String, Object>();

	HashMap<String, int[]> arrayPrimesMap = new HashMap<String, int[]>();
	HashMap<String, Boolean> arrayHasElementMap = new HashMap<String, Boolean>();

	// For declaring variables in memory
	@Override
	public void exitDeclaring_integers(jotterGrammarParser.Declaring_integersContext ctx) {
		int tokenAmount = ctx.getChildCount();
		ArrayList<String> tokenNames = new ArrayList<String>();
		for (int i = 0; i < tokenAmount; i++) {
			tokenNames.add(ctx.getChild(i).toString());
		}
		if (ctx.INTNUMS() != null) {
			variableIntegerMap.put(ctx.VARNAMES(0).toString(), Integer.parseInt(ctx.INTNUMS().toString()));
		} else if (tokenNames.contains(".pick")) {
			if (variablePickTempMap.containsKey("first")) {
				variableIntegerMap.put(ctx.VARNAMES(0).toString(),
						Integer.parseInt(variablePickTempMap.get("first").toString()));
				variablePickTempMap.remove("first");
			}
		} else if (ctx.VARNAMES().size() > 1) {
			String key = ctx.VARNAMES(1).toString();
			if (variableIntegerMap.containsKey(key)) {
				variableIntegerMap.put(ctx.VARNAMES(0).toString(), variableIntegerMap.get(key));
			}
		} else if (ctx.console_get() != null) {
			inputInstanceCount++;
			File inputFile = new File("input.txt");
			Scanner inputRead = null;
			try {
				inputRead = new Scanner(inputFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			ArrayList<String> storeWords = new ArrayList<String>();
			while (inputRead.hasNextLine()) {
				// Reads the lines of the text file and keeps track of how many words there are
				// (assuming that all words are on separate lines).
				String lineContent = inputRead.nextLine();
				if (lineContent != null) {
					storeWords.add(lineContent);
				}
			}
			int oneTemp = Integer.parseInt(storeWords.get(inputInstanceCount));
			variableIntegerMap.put(ctx.VARNAMES(0).toString(), oneTemp);
		}
	}

	@Override
	public void exitDeclaring_decimals(jotterGrammarParser.Declaring_decimalsContext ctx) {
		if (ctx.DECNUMS() != null) {
			variableDecimalMap.put(ctx.VARNAMES().getText(), Float.parseFloat(ctx.DECNUMS().getText()));
		}
	}

	@Override
	public void exitDeclaring_strings(jotterGrammarParser.Declaring_stringsContext ctx) {
		if (ctx.NAMES() != null) {
			String stripName = ctx.NAMES().getText();
			String newName = "";
			String[] subNames = stripName.split("\"");
			for (int i = 0; i < subNames.length; i++) {
				newName = newName.concat(subNames[i]);
			}
			variableStringMap.put(ctx.VARNAMES().getText(), newName);
		}
	}

	@Override
	public void exitDeclaring_booleans(jotterGrammarParser.Declaring_booleansContext ctx) {
		if (ctx.FORBOOL() != null && ctx.FORBOOL().toString().equals("false")) {
			variableBooleanMap.put(ctx.VARNAMES().getText(), false);
		}
		if (ctx.FORBOOL() != null && ctx.FORBOOL().toString().equals("true")) {
			variableBooleanMap.put(ctx.VARNAMES().getText(), true);
		}
	}

	// Functions
	@Override
	public void exitPrint_to_console(jotterGrammarParser.Print_to_consoleContext ctx) {

		String finalPrint = "";
		Object[] names = ctx.NAMES().toArray();
		ArrayList<String> namesCompare = new ArrayList<String>();
		for (int i = 0; i < names.length; i++) {
			namesCompare.add(names[i].toString());
		}

		int amount = ctx.getChildCount();
		for (int i = 0; i < amount; i++) {
			String tokenID = ctx.getChild(i).getText();
			if (variableIntegerMap.containsKey(tokenID)) {
				finalPrint = finalPrint.concat(variableIntegerMap.get(tokenID).toString());
			} if (variableDecimalMap.containsKey(tokenID)) {
				finalPrint = finalPrint.concat(variableDecimalMap.get(tokenID).toString());
			} if (variableStringMap.containsKey(tokenID)) {
				finalPrint = finalPrint.concat(variableStringMap.get(tokenID));
			} if (variableBooleanMap.containsKey(tokenID)) {
				finalPrint = finalPrint.concat(variableBooleanMap.get(tokenID).toString());
			} if (variableArrayIntMap.containsKey(tokenID)) {
				finalPrint = finalPrint.concat(Arrays.toString(variableArrayIntMap.get(tokenID)));
			} if (variableArrayDecMap.containsKey(tokenID)) {
				finalPrint = finalPrint.concat(Arrays.toString(variableArrayDecMap.get(tokenID)));
			} if (variableArrayStrMap.containsKey(tokenID)) {
				finalPrint = finalPrint.concat(Arrays.toString(variableArrayStrMap.get(tokenID)));
			} if (variableArrayBolMap.containsKey(tokenID)) {
				finalPrint = finalPrint.concat(Arrays.toString(variableArrayBolMap.get(tokenID)));
			} if (variableRandomTempMap.containsKey(tokenID)) {
				finalPrint = finalPrint.concat(Arrays.toString(variableRandomTempMap.get(tokenID)));
				variableRandomTempMap.remove(tokenID);
			} if (ctx.random_pick() != null) {
				if (variablePickTempMap.containsKey("first")) {
					finalPrint = finalPrint.concat(variablePickTempMap.get("first").toString());
					variablePickTempMap.remove("first");
				}
			} if (ctx.array_length() != null) {
				if (arrayLengthMap.containsKey("first")) {
					finalPrint = finalPrint.concat(arrayLengthMap.get("first").toString());
					arrayLengthMap.remove("first");
				}
			} if (tokenID.equals("+")) {
				if (ctx.getChild(i + 1).getText().equals("+") && i != (amount - 2)) {
					finalPrint = finalPrint.concat(" ");
				}
			} if (ctx.NAMES() != null) {
				if (namesCompare.contains(tokenID)) {
					String tempName = "";
					String[] subNames = tokenID.split("\"");
					for (int j = 0; j < subNames.length; j++) {
						tempName = tempName.concat(subNames[j]);
					}
					finalPrint = finalPrint.concat(tempName);
				}
			} 
		}
		
		keepWords.add(finalPrint);
		//System.out.println(finalPrint);
	}

	@Override
	public void exitConsole_get(jotterGrammarParser.Console_getContext ctx) {
		/*
		 * KeyAdapter getIn = new KeyAdapter() {
		 * 
		 * @Override public void keyPressed(KeyEvent e) { if (e.getKeyCode() ==
		 * KeyEvent.VK_ENTER) { input = jotterGUI.consoleArea.getText();
		 * System.out.println(input); } } };
		 * 
		 * jotterGUI.consoleArea.addKeyListener(getIn); while(hasInput = false) { if
		 * (input != null) { hasInput = true;
		 * jotterGUI.consoleArea.removeKeyListener(getIn);
		 * jotterGUI.consoleArea.setText(""); } }
		 */
	}

	@Override
	public void exitDeclaring_arrays(jotterGrammarParser.Declaring_arraysContext ctx) {

		int tokenAmount = ctx.getChildCount();
		int arrLength = Integer.parseInt(ctx.INTNUMS(0).toString());
		String userArrName = ctx.VARNAMES(0).toString();
		ArrayList<String> tokenNames = new ArrayList<String>();
		ArrayList<String> tokenThings = new ArrayList<String>();

		if (ctx.random_gen() != null) {
			if (variableRandomTempMap.containsKey("first")) {
				variableArrayIntMap.put(userArrName, variableRandomTempMap.get("first"));
				variableRandomTempMap.remove("first");

			}
		} else if(ctx.array_prime() != null){
			if (arrayPrimesMap.containsKey("first")) {
				variableArrayIntMap.put(userArrName, arrayPrimesMap.get("first"));
				arrayPrimesMap.remove("first");
			}
			
		} else {
			for (int i = 0; i < tokenAmount; i++) {
				int part = ((TerminalNodeImpl) ctx.getChild(i)).getSymbol().getType();
				String typeName = jotterGrammarLexer.VOCABULARY.getSymbolicName(part);
				tokenNames.add(typeName);
				tokenThings.add(ctx.getChild(i).toString());
			}

			if (tokenNames.contains("INTEGERARR")) {
				ArrayList<Integer> tempList = new ArrayList<Integer>();
				int[] temp = new int[arrLength];
				for (int i = 0; i < tokenAmount; i++) {
					if (tokenNames.get(i).equals("INTNUMS")) {
						tempList.add(Integer.parseInt(tokenThings.get(i)));
					}
					if (tokenNames.get(i).equals("VARNAMES")) {
						String getVar = tokenThings.get(i);
						if (variableIntegerMap.containsKey(getVar)) {
							int getNum = variableIntegerMap.get(getVar);
							tempList.add(getNum);
						}
					}
				}
				tempList.remove(0);
				if (!tempList.isEmpty()) {
					for (int i = 0; i < arrLength; i++) {
						temp[i] = tempList.get(i);
					}
				}
				variableArrayIntMap.put(userArrName, temp);

			}

			else if (tokenNames.contains("DECIMALARR")) {
				ArrayList<Float> tempList = new ArrayList<Float>();
				float[] temp = new float[arrLength];
				for (int i = 0; i < tokenAmount; i++) {
					if (tokenNames.get(i).equals("DECNUMS")) {
						tempList.add(Float.parseFloat(tokenThings.get(i)));
					}
					if (tokenNames.get(i).equals("VARNAMES")) {
						String getVar = tokenThings.get(i);
						if (variableDecimalMap.containsKey(getVar)) {
							float getNum = variableDecimalMap.get(getVar);
							tempList.add(getNum);
						}
					}
				}
				if (!tempList.isEmpty()) {
					for (int i = 0; i < arrLength; i++) {
						temp[i] = tempList.get(i);
					}
				}
				variableArrayDecMap.put(userArrName, temp);

			}

			else if (tokenNames.contains("STRINGARR")) {
				ArrayList<String> tempList = new ArrayList<String>();
				String[] temp = new String[arrLength];
				for (int i = 0; i < tokenAmount; i++) {
					if (tokenNames.get(i).equals("NAMES")) {
						tempList.add(tokenThings.get(i));
					}
					if (tokenNames.get(i).equals("VARNAMES")) {
						String getVar = tokenThings.get(i);
						if (variableStringMap.containsKey(getVar)) {
							String getNum = variableStringMap.get(getVar);
							tempList.add(getNum);
						}
					}
				}
				if (!tempList.isEmpty()) {
					for (int i = 0; i < arrLength; i++) {
						temp[i] = tempList.get(i);
					}
				}
				variableArrayStrMap.put(userArrName, temp);

			}

			else if (tokenNames.contains("BOOLEANARR")) {
				ArrayList<Boolean> tempList = new ArrayList<Boolean>();
				boolean[] temp = new boolean[arrLength];
				for (int i = 0; i < tokenAmount; i++) {
					if (tokenNames.get(i).equals("FORBOOL")) {
						tempList.add(Boolean.parseBoolean(tokenThings.get(i)));
					}
					if (tokenNames.get(i).equals("VARNAMES")) {
						String getVar = tokenThings.get(i);
						if (variableBooleanMap.containsKey(getVar)) {
							boolean getNum = variableBooleanMap.get(getVar);
							tempList.add(getNum);
						}
					}
				}
				if (!tempList.isEmpty()) {
					for (int i = 0; i < arrLength; i++) {
						temp[i] = tempList.get(i);
					}
				}
				variableArrayBolMap.put(userArrName, temp);

			}
		}

	}

	@Override
	public void exitRandom_gen(jotterGrammarParser.Random_genContext ctx) {
		int arrSize = Integer.parseInt(ctx.INTNUMS(2).toString());
		int lowerBound = Integer.parseInt(ctx.INTNUMS(0).toString());
		int upperBound = Integer.parseInt(ctx.INTNUMS(1).toString());
		Random gen = new Random();
		int[] storeRandom = new int[arrSize];
		for (int i = 0; i < arrSize; i++) {
			storeRandom[i] = gen.nextInt(upperBound - lowerBound + 1) + lowerBound;
		}
		variableRandomTempMap.put("first", storeRandom);
	}

	@Override
	public void exitRandom_pick(jotterGrammarParser.Random_pickContext ctx) {
		String arrRandomGet = ctx.VARNAMES().toString();
		Random genGet = new Random();
		if (variableArrayIntMap.containsKey(arrRandomGet)) {
			int upBound = variableArrayIntMap.get(arrRandomGet).length - 1;
			int randomIndex = genGet.nextInt(upBound + 1);
			int randomSelection = variableArrayIntMap.get(arrRandomGet)[randomIndex];
			variablePickTempMap.put("first", randomSelection);
		} else if (variableArrayDecMap.containsKey(arrRandomGet)) {
			int upBound = variableArrayDecMap.get(arrRandomGet).length - 1;
			int randomIndex = genGet.nextInt(upBound + 1);
			float randomSelection = variableArrayDecMap.get(arrRandomGet)[randomIndex];
			variablePickTempMap.put("first", randomSelection);
		} else if (variableArrayStrMap.containsKey(arrRandomGet)) {
			int upBound = variableArrayStrMap.get(arrRandomGet).length - 1;
			int randomIndex = genGet.nextInt(upBound + 1);
			String randomSelection = variableArrayStrMap.get(arrRandomGet)[randomIndex];
			variablePickTempMap.put("first", randomSelection);
		} else if (variableArrayBolMap.containsKey(arrRandomGet)) {
			int upBound = variableArrayBolMap.get(arrRandomGet).length - 1;
			int randomIndex = genGet.nextInt(upBound + 1);
			boolean randomSelection = variableArrayBolMap.get(arrRandomGet)[randomIndex];
			variablePickTempMap.put("first", randomSelection);
		}
	}

	@Override
	public void exitArray_length(jotterGrammarParser.Array_lengthContext ctx) {
		String retrieveArray = ctx.VARNAMES().toString();
		if (variableArrayIntMap.containsKey(retrieveArray)) {
			arrayLengthMap.put("first", variableArrayIntMap.get(retrieveArray).length);
		}
		else if (variableArrayDecMap.containsKey(retrieveArray)) {
			arrayLengthMap.put("first", variableArrayDecMap.get(retrieveArray).length);
		}
		else if (variableArrayStrMap.containsKey(retrieveArray)) {
			arrayLengthMap.put("first", variableArrayStrMap.get(retrieveArray).length);
		}
		else if (variableArrayBolMap.containsKey(retrieveArray)) {
			arrayLengthMap.put("first", variableArrayBolMap.get(retrieveArray).length);
		}
	}

	@Override
	public void exitArray_getelement(jotterGrammarParser.Array_getelementContext ctx) {
		String retrieveArray = ctx.VARNAMES().toString();
		int element = Integer.parseInt(ctx.INTNUMS().toString());
		if (variableArrayIntMap.containsKey(retrieveArray)) {
			arrayElementTempMap.put(retrieveArray + element, variableArrayIntMap.get(retrieveArray)[element]);
		}
		if (variableArrayDecMap.containsKey(retrieveArray)) {
			arrayElementTempMap.put(retrieveArray + element, variableArrayDecMap.get(retrieveArray)[element]);
		}
		if (variableArrayStrMap.containsKey(retrieveArray)) {
			arrayElementTempMap.put(retrieveArray + element, variableArrayStrMap.get(retrieveArray)[element]);
		}
		if (variableArrayBolMap.containsKey(retrieveArray)) {
			arrayElementTempMap.put(retrieveArray + element, variableArrayBolMap.get(retrieveArray)[element]);
		}
	}

	@Override
	public void exitArray_editelement(jotterGrammarParser.Array_editelementContext ctx) {
		String retrieveArray = ctx.VARNAMES(0).toString();
		if (ctx.INSERT() != null) {
			if (ctx.INTNUMS() != null) {
				if (variableArrayIntMap.containsKey(retrieveArray)) {
					int newSize = variableArrayIntMap.get(retrieveArray).length + 1;
					int[] actual = variableArrayIntMap.get(retrieveArray);
					int[] temp = new int[newSize];
					for (int i = 0; i < newSize; i++) {
						temp[i] = actual[i];
					}
					actual = temp;
					actual[actual.length - 1] = Integer.parseInt(ctx.INTNUMS().toString());
					variableArrayIntMap.put(retrieveArray, actual);
				}
			}
			if (ctx.VARNAMES().size() > 1) {
				String varKey = ctx.VARNAMES(1).toString();
				if (variableArrayIntMap.containsKey(retrieveArray)) {
					if (variableIntegerMap.containsKey(varKey)) {
						int newInt = variableIntegerMap.get(varKey);
						int newSize = variableArrayIntMap.get(retrieveArray).length + 1;
						int[] actual = variableArrayIntMap.get(retrieveArray);
						int[] temp = new int[newSize];
						for (int i = 0; i < newSize; i++) {
							temp[i] = actual[i];
						}
						actual = temp;
						actual[actual.length - 1] = newInt;
						variableArrayIntMap.put(retrieveArray, actual);
					}
				}
				if (variableArrayDecMap.containsKey(retrieveArray)) {
					if (variableDecimalMap.containsKey(varKey)) {
						float newDec = variableDecimalMap.get(varKey);
						int newSize = variableArrayDecMap.get(retrieveArray).length + 1;
						float[] actual = variableArrayDecMap.get(retrieveArray);
						float[] temp = new float[newSize];
						for (int i = 0; i < newSize; i++) {
							temp[i] = actual[i];
						}
						actual = temp;
						actual[actual.length - 1] = newDec;
						variableArrayDecMap.put(retrieveArray, actual);
					}
				}
				if (variableArrayStrMap.containsKey(retrieveArray)) {
					if (variableStringMap.containsKey(varKey)) {
						String newWord = variableStringMap.get(varKey);
						int newSize = variableArrayStrMap.get(retrieveArray).length + 1;
						String[] actual = variableArrayStrMap.get(retrieveArray);
						String[] temp = new String[newSize];
						for (int i = 0; i < newSize; i++) {
							temp[i] = actual[i];
						}
						actual = temp;
						actual[actual.length - 1] = newWord;
						variableArrayStrMap.put(retrieveArray, actual);
					}
				}
				if (variableArrayBolMap.containsKey(retrieveArray)) {
					if (variableBooleanMap.containsKey(varKey)) {
						boolean newBoolean = variableBooleanMap.get(varKey);
						int newSize = variableArrayBolMap.get(retrieveArray).length + 1;
						boolean[] actual = variableArrayBolMap.get(retrieveArray);
						boolean[] temp = new boolean[newSize];
						for (int i = 0; i < newSize; i++) {
							temp[i] = actual[i];
						}
						actual = temp;
						actual[actual.length - 1] = newBoolean;
						variableArrayBolMap.put(retrieveArray, actual);
					}
				}
			}
			if (ctx.DECNUMS() != null) {
				int newSize = variableArrayDecMap.get(retrieveArray).length + 1;
				float[] actual = variableArrayDecMap.get(retrieveArray);
				float[] temp = new float[newSize];
				for (int i = 0; i < newSize; i++) {
					temp[i] = actual[i];
				}
				actual = temp;
				actual[actual.length - 1] = Float.parseFloat(ctx.INTNUMS().toString());
				variableArrayDecMap.put(retrieveArray, actual);
			}
			if (ctx.NAMES() != null) {
				int newSize = variableArrayStrMap.get(retrieveArray).length + 1;
				String[] actual = variableArrayStrMap.get(retrieveArray);
				String[] temp = new String[newSize];
				for (int i = 0; i < newSize; i++) {
					temp[i] = actual[i];
				}
				actual = temp;
				actual[actual.length - 1] = ctx.INTNUMS().toString();
				variableArrayStrMap.put(retrieveArray, actual);
			}
			if (ctx.FORBOOL() != null) {
				int newSize = variableArrayBolMap.get(retrieveArray).length + 1;
				boolean[] actual = variableArrayBolMap.get(retrieveArray);
				boolean[] temp = new boolean[newSize];
				for (int i = 0; i < newSize; i++) {
					temp[i] = actual[i];
				}
				actual = temp;
				actual[actual.length - 1] = Boolean.parseBoolean(ctx.INTNUMS().toString());
				variableArrayBolMap.put(retrieveArray, actual);
			}
		}
		if (ctx.REMOVE() != null) {
			int removeElement = Integer.parseInt(ctx.INTNUMS().toString());
			if (variableArrayIntMap.containsKey(retrieveArray)) {
				int newSize = variableArrayIntMap.get(retrieveArray).length - 1;
				int[] actual = variableArrayIntMap.get(retrieveArray);
				int[] temp = new int[newSize];
				actual[removeElement] = actual[actual.length - 1];
				for (int i = 0; i < newSize; i++) {
					temp[i] = actual[i];
				}
				actual = temp;
				variableArrayIntMap.put(retrieveArray, actual);
			}
			if (variableArrayDecMap.containsKey(retrieveArray)) {
				int newSize = variableArrayDecMap.get(retrieveArray).length - 1;
				float[] actual = variableArrayDecMap.get(retrieveArray);
				float[] temp = new float[newSize];
				actual[removeElement] = actual[actual.length - 1];
				for (int i = 0; i < newSize; i++) {
					temp[i] = actual[i];
				}
				actual = temp;
				variableArrayDecMap.put(retrieveArray, actual);
			}
			if (variableArrayStrMap.containsKey(retrieveArray)) {
				int newSize = variableArrayStrMap.get(retrieveArray).length - 1;
				String[] actual = variableArrayStrMap.get(retrieveArray);
				String[] temp = new String[newSize];
				actual[removeElement] = actual[actual.length - 1];
				for (int i = 0; i < newSize; i++) {
					temp[i] = actual[i];
				}
				actual = temp;
				variableArrayStrMap.put(retrieveArray, actual);
			}
			if (variableArrayBolMap.containsKey(retrieveArray)) {
				int newSize = variableArrayBolMap.get(retrieveArray).length - 1;
				boolean[] actual = variableArrayBolMap.get(retrieveArray);
				boolean[] temp = new boolean[newSize];
				actual[removeElement] = actual[actual.length - 1];
				for (int i = 0; i < newSize; i++) {
					temp[i] = actual[i];
				}
				actual = temp;
				variableArrayBolMap.put(retrieveArray, actual);
			}
		}
	}

	@Override
	public void exitArray_changeelement(jotterGrammarParser.Array_changeelementContext ctx) {
		String arrayName = ctx.array_getelement().VARNAMES().toString();
		String elementToChange = ctx.array_getelement().INTNUMS().toString();
		int elementChanger = Integer.parseInt(ctx.array_getelement().INTNUMS().toString());
		if (arrayElementTempMap.containsKey(arrayName + elementToChange)) {
			if (ctx.INTNUMS() != null && variableArrayIntMap.containsKey(arrayName)) {
				variableArrayIntMap.get(arrayName)[elementChanger] = Integer.parseInt(ctx.INTNUMS().toString());
			} else if (ctx.DECNUMS() != null && variableArrayDecMap.containsKey(arrayName)) {
				variableArrayDecMap.get(arrayName)[elementChanger] = Float.parseFloat(ctx.DECNUMS().toString());
			} else if (ctx.NAMES() != null && variableArrayStrMap.containsKey(arrayName)) {
				variableArrayStrMap.get(arrayName)[elementChanger] = ctx.DECNUMS().toString();
			} else if (ctx.FORBOOL() != null && variableArrayBolMap.containsKey(arrayName)) {
				variableArrayBolMap.get(arrayName)[elementChanger] = Boolean.parseBoolean(ctx.FORBOOL().toString());
			} else if (ctx.VARNAMES() != null) {
				String theVar = ctx.VARNAMES().toString();
				if (variableIntegerMap.containsKey(theVar)) {
					int varToPut = variableIntegerMap.get(ctx.VARNAMES().toString());
					variableArrayIntMap.get(arrayName)[elementChanger] = varToPut;
				}
				if (variableDecimalMap.containsKey(theVar)) {
					float varToPut = variableDecimalMap.get(ctx.VARNAMES().toString());
					variableArrayDecMap.get(arrayName)[elementChanger] = varToPut;
				}
				if (variableStringMap.containsKey(theVar)) {
					String varToPut = variableStringMap.get(ctx.VARNAMES().toString());
					variableArrayStrMap.get(arrayName)[elementChanger] = varToPut;
				}
				if (variableBooleanMap.containsKey(theVar)) {
					boolean varToPut = variableBooleanMap.get(ctx.VARNAMES().toString());
					variableArrayBolMap.get(arrayName)[elementChanger] = varToPut;
				}
			}
			arrayElementTempMap.remove(arrayName + elementToChange);
			// Add Random pick, array length, get element of another array
		}
	}

	@Override
	public void exitArray_sort(jotterGrammarParser.Array_sortContext ctx) {
		String arrayName = ctx.VARNAMES().toString();
		if (variableArrayIntMap.containsKey(arrayName)) {
			Integer[] arrToSort = Arrays.stream(variableArrayIntMap.get(arrayName)).boxed().toArray(Integer[]::new);
			int last = variableArrayIntMap.get(arrayName).length - 1;
			quickSort(arrToSort, 0, last);
			int[] arrDone = Arrays.stream(arrToSort).mapToInt(Integer::intValue).toArray();
			variableArrayIntMap.put(arrayName, arrDone);
		} else if (variableArrayDecMap.containsKey(arrayName)) {
			Float[] arrToSort = new Float[variableArrayDecMap.get(arrayName).length];
			for (int i = 0; i < arrToSort.length; i++) {
				arrToSort[i] = variableArrayDecMap.get(arrayName)[i];
			}
			int last = variableArrayDecMap.get(arrayName).length - 1;
			quickSort(arrToSort, 0, last);
			float[] arrDone = new float[arrToSort.length];
			for (int i = 0; i < arrDone.length; i++) {
				arrDone[i] = arrToSort[i];
			}
			variableArrayDecMap.put(arrayName, arrDone);
		} else if (variableArrayStrMap.containsKey(arrayName)) {
			String[] arrToSort = variableArrayStrMap.get(arrayName);
			Arrays.sort(arrToSort);
			variableArrayStrMap.put(arrayName, arrToSort);
		}
	}

	public <T> void quickSort(T[] array, int low, int high) {
		// An implementation of the QuickSort algorithm, learned from:
		// https://www.geeksforgeeks.org/quick-sort/
		if (low < high) {
			int splitIndex = partitionQuickSort(array, low, high);
			quickSort(array, low, splitIndex - 1);
			quickSort(array, splitIndex + 1, high);
		}

	}

	public <T> int partitionQuickSort(T[] arrayPassed, int lowEnd, int highEnd) {
		T pivot = arrayPassed[highEnd];
		int smallIndex = lowEnd - 1;
		for (int i = lowEnd; i < highEnd; i++) {
			if (Float.parseFloat(arrayPassed[i].toString()) <= Float.parseFloat(pivot.toString())) {
				smallIndex++;
				T temp = arrayPassed[smallIndex];
				arrayPassed[smallIndex] = arrayPassed[i];
				arrayPassed[i] = temp;
			}
		}

		T temp = arrayPassed[smallIndex + 1];
		arrayPassed[smallIndex + 1] = arrayPassed[highEnd];
		arrayPassed[highEnd] = temp;

		return smallIndex + 1;
	}

	@Override
	public void exitArray_prime(jotterGrammarParser.Array_primeContext ctx) {
		String arrayName = ctx.VARNAMES().toString();
		if (variableArrayIntMap.containsKey(arrayName)) {
			// An implementation of the Sieve of Eratosthenes algorithm,
			// learned from: https://www.geeksforgeeks.org/sieve-of-eratosthenes/
			ArrayList<Integer> tempArr = new ArrayList<Integer>();

			for (int i = 0; i < variableArrayIntMap.get(arrayName).length; i++) {
				tempArr.add(variableArrayIntMap.get(arrayName)[i]);
			}

			int max = Collections.max(tempArr);

			boolean[] compare = new boolean[max + 1];

			for (int i = 0; i < max + 1; i++) {
				compare[i] = true;
			}

			for (int i = 2; i * i < max + 1; i++) {
				if (compare[i] == true) {
					for (int j = i * i; j < max + 1; j += i) {
						compare[j] = false;
					}
				}
			}

			ArrayList<Integer> theMaxPrimes = new ArrayList<Integer>();
			ArrayList<Integer> actualPrimes = new ArrayList<Integer>();

			for (int i = 2; i <= max; i++) {
				if (compare[i] == true) {
					theMaxPrimes.add(i);
				}
			}

			for (int i = 0; i < theMaxPrimes.size(); i++) {
				if (tempArr.contains(theMaxPrimes.get(i))) {
					actualPrimes.add(theMaxPrimes.get(i));
				}
			}

			int[] donePrimes = new int[actualPrimes.size()];

			for (int i = 0; i < donePrimes.length; i++) {
				donePrimes[i] = actualPrimes.get(i);
			}

			arrayPrimesMap.put("first", donePrimes);
		}
	}

	@Override
	public void exitArray_find(jotterGrammarParser.Array_findContext ctx) {
		String arrayName = ctx.VARNAMES(0).toString();
		if (variableArrayIntMap.containsKey(arrayName)) {
			int length = variableArrayIntMap.get(arrayName).length;
			ArrayList<Integer> tempList = new ArrayList<Integer>();
			for (int i = 0; i < length; i++) {
				tempList.add(variableArrayIntMap.get(arrayName)[i]);
			}
			if (ctx.INTNUMS() != null) {
				if (tempList.contains(Integer.parseInt(ctx.INTNUMS(0).toString()))) {
					arrayHasElementMap.put("first", true);
				} else {
					arrayHasElementMap.put("first", false);
				}
			} else if (ctx.VARNAMES().size() > 1) {
				String getVar = ctx.VARNAMES(1).toString();
				if (variableIntegerMap.containsKey(getVar)) {
					int searchVar = variableIntegerMap.get(getVar);
					if (tempList.contains(searchVar)) {
						arrayHasElementMap.put("first", true);
					} else {
						arrayHasElementMap.put("first", false);
					}
				}
			}
		} else if (variableArrayDecMap.containsKey(arrayName)) {
			int length = variableArrayDecMap.get(arrayName).length;
			ArrayList<Float> tempList = new ArrayList<Float>();
			for (int i = 0; i < length; i++) {
				tempList.add(variableArrayDecMap.get(arrayName)[i]);
			}
			if (ctx.DECNUMS() != null) {
				if (tempList.contains(Float.parseFloat(ctx.DECNUMS().toString()))) {
					arrayHasElementMap.put("first", true);
				} else {
					arrayHasElementMap.put("first", false);
				}
			} else if (ctx.VARNAMES().size() > 1) {
				String getVar = ctx.VARNAMES(1).toString();
				if (variableDecimalMap.containsKey(getVar)) {
					float searchVar = variableDecimalMap.get(getVar);
					if (tempList.contains(searchVar)) {
						arrayHasElementMap.put("first", true);
					} else {
						arrayHasElementMap.put("first", false);
					}
				}
			}
		} else if (variableArrayStrMap.containsKey(arrayName)) {
			int length = variableArrayStrMap.get(arrayName).length;
			ArrayList<String> tempList = new ArrayList<String>();
			for (int i = 0; i < length; i++) {
				tempList.add(variableArrayStrMap.get(arrayName)[i]);
			}
			if (ctx.NAMES() != null) {
				if (tempList.contains(ctx.NAMES().toString())) {
					arrayHasElementMap.put("first", true);
				} else {
					arrayHasElementMap.put("first", false);
				}
			} else if (ctx.VARNAMES().size() > 1) {
				String getVar = ctx.VARNAMES(1).toString();
				if (variableStringMap.containsKey(getVar)) {
					String searchVar = variableStringMap.get(getVar);
					if (tempList.contains(searchVar)) {
						arrayHasElementMap.put("first", true);
					} else {
						arrayHasElementMap.put("first", false);
					}
				}
			}
		} else if (variableArrayBolMap.containsKey(arrayName)) {
			int length = variableArrayBolMap.get(arrayName).length;
			ArrayList<Boolean> tempList = new ArrayList<Boolean>();
			for (int i = 0; i < length; i++) {
				tempList.add(variableArrayBolMap.get(arrayName)[i]);
			}
			if (ctx.FORBOOL() != null) {
				if (tempList.contains(ctx.FORBOOL().toString())) {
					arrayHasElementMap.put("first", true);
				} else {
					arrayHasElementMap.put("first", false);
				}
			} else if (ctx.VARNAMES().size() > 1) {
				String getVar = ctx.VARNAMES(1).toString();
				if (variableBooleanMap.containsKey(getVar)) {
					boolean searchVar = variableBooleanMap.get(getVar);
					if (tempList.contains(searchVar)) {
						arrayHasElementMap.put("first", true);
					} else {
						arrayHasElementMap.put("first", false);
					}
				}
			}
			
		}
	}
	@Override
	public void exitMath_add(jotterGrammarParser.Math_addContext ctx) {
		String originalVar = ctx.VARNAMES(0).toString();
		int first = 0;
		int second = 0;
		int amount = ctx.getChildCount();
		ArrayList<String> children = new ArrayList<String>();
		for (int i = 0; i < amount; i++) {
			int part = ((TerminalNodeImpl) ctx.getChild(i)).getSymbol().getType();
			String typeName = jotterGrammarLexer.VOCABULARY.getSymbolicName(part);
			children.add(typeName);
		}
		if (variableIntegerMap.containsKey(originalVar)) {
			if (children.get(2).equals("VARNAMES")) {
				String anotherVar = ctx.VARNAMES(1).toString();
				if (variableIntegerMap.containsKey(anotherVar)) {
					first = variableIntegerMap.get(anotherVar);
				}
			}
			else if (children.get(2).equals("INTNUMS")) {
				first = Integer.parseInt(ctx.INTNUMS(0).toString());
			}
			if (children.get(4).equals("VARNAMES")) {
				String anotherVar2 = ctx.VARNAMES(2).toString();
				if (variableIntegerMap.containsKey(anotherVar2)) {
					second = variableIntegerMap.get(anotherVar2);
				}
			}
			else if (children.get(4).equals("INTNUMS")) {
				second = Integer.parseInt(ctx.INTNUMS(1).toString());
			}
		}
		int temp = first + second;
		variableIntegerMap.put(originalVar, temp);
	}
	//The starting implementation of if/else statements
	/*
	 * @Override public void
	 * exitIfelse_statement(jotterGrammarParser.Ifelse_statementContext ctx) {
	 * ArrayList<String> allChildren = new ArrayList<String>(); boolean satisfied =
	 * false; int amount = ctx.getChildCount(); int stopAtThen = 0; for (int i = 0;
	 * i < amount; i++) { allChildren.add(ctx.getChild(i).toString()); if
	 * (ctx.getChild(i).toString().equals("then")) { stopAtThen = i; } }
	 * List<String> startState = allChildren.subList(1, stopAtThen); if
	 * (startState.size() == 3) { String firstVar = startState.get(0); String
	 * operator = startState.get(1); String secondVar = startState.get(2); } else if
	 * (startState.size() == 7) {
	 * 
	 * } }
	 */

	@Override
	public void enterParse(jotterGrammarParser.ParseContext ctx) {
		
		//At the end of everything, taking whatever there is for output
		//and writing out to a text file.
			
			  File outputFile = new File("output.txt"); 
			  Writer outputWrite = null; 
			  try {
				  outputWrite = new BufferedWriter(new FileWriter(outputFile)); 
			  } catch (IOException e) { 
				  e.printStackTrace(); 
			  } 
			  for (int i = 0; i < keepWords.size(); i++) { 
				  try { 
					  outputWrite.write(keepWords.get(i) + '\r' + '\n'); 
				  } catch (IOException e) { 
					  e.printStackTrace(); 
				  } 
			  } try {
				  outputWrite.close(); 
			  } catch (IOException e) { 
				  e.printStackTrace(); 
			  }
	}
}
