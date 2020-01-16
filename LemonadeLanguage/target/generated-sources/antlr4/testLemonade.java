import java.io.IOException;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.*;

public class testLemonade {

	public static void main(String[] args) {

		Scanner getInput = new Scanner(System.in);
		String theInput = getInput.next();

		CharStream charInput = CharStreams.fromString(theInput);

		LemonadeGrammarLexer lexer = new LemonadeGrammarLexer(charInput);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		LemonadeGrammarParser parser = new LemonadeGrammarParser(tokens);
		ParseTree tree = parser.parse();
		TreeViewer viewer = new TreeViewer(Arrays.asList(parser.getRuleNames()), tree);
		
		viewer.open();
		getInput.close();
		
	}

}
