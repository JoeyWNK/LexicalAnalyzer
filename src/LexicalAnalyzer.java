import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class LexicalAnalyzer {

	static final int LEXEME_MAX = 100;

	private Scanner scanner;
	private PrintStream out = null;

	private char[] lexeme = new char[LEXEME_MAX];
	private char nextChar;
	private CharClass charClass;
	private int lexLen;
	private Token nextToken;

	public LexicalAnalyzer(String fileName) throws FileNotFoundException {
		try {
			scanner = new Scanner(new File(fileName));
			scanner.useDelimiter("");
		} catch (FileNotFoundException e) {
			System.err.println(String.format("Error - cannot find %s", fileName));
			throw e;
		}
	}

	public void setOutPut(OutputStream out) {
		this.out = new PrintStream(out);
	}

	public void analyze() {
		if (scanner.hasNext()) {
			getChar();
			do {
				lex();
			} while (scanner.hasNext());
			out.println("Parsing complete");
		}
	}

	private Token lex() {
		lexLen = 0;
		getNonBlank();
		switch (charClass) {
		case LETTER:
			addChar();
			getChar();
			while (charClass == CharClass.LETTER || charClass == CharClass.DIGIT) {
				addChar();
				getChar();
			}
			nextToken = Token.IDENT;
			break;
		case DIGIT:
			addChar();
			getChar();
			while (charClass == CharClass.DIGIT) {
				addChar();
				getChar();
			}
			nextToken = Token.INT_LIT;
			break;
		case UNKNOWN:
			lookup(nextChar);
			getChar();
		}
		out.println(String.format("Next token is: %s, Next lexeme is %s", nextToken.getValue(), getStr(lexeme)));
		// out.println(String.format("Next token is: %s, Next lexeme is %s",
		// nextToken, getStr(lexeme)));
		return nextToken;
	}

	private String getStr(char[] lexeme) {
		int count = 0;
		while ((int) lexeme[count] != 0 && count < LEXEME_MAX)
			count++;
		return String.valueOf(lexeme, 0, count);
	}

	private Token lookup(char nextChar) {
		switch (nextChar) {
		case '(':
			addChar();
			nextToken = Token.LEFT_PAREN;
			break;

		case ')':
			addChar();
			nextToken = Token.RIGHT_PAREN;
			break;

		case '+':
			addChar();
			nextToken = Token.ADD_OP;
			break;

		case '-':
			addChar();
			nextToken = Token.SUB_OP;
			break;

		case '*':
			addChar();
			nextToken = Token.MULT_OP;
			break;

		case '/':
			addChar();
			nextToken = Token.DIV_OP;
			break;

		case '=':
			addChar();
			nextToken = Token.ASSIGN_OP;
			break;

		case ';':
			addChar();
			nextToken = Token.SEMI_COLUMN;
			break;

		case '>':
			addChar();
			nextToken = Token.COMP_OP_LARGE;
			break;

		case '<':
			addChar();
			nextToken = Token.COMP_OP_SMALL;
			break;

		case '"':
			addChar();
			nextToken = Token.QUOT_MARK_DOU;
			break;
			// TODO String detection
		case '!':
			addChar();
			nextToken = Token.EXCLAMATION;
			break;

		default:
			addChar();
			nextToken = Token.UNKNOWN;
			break;

		}
		return nextToken;
	}

	private void addChar() {
		if (lexLen <= LEXEME_MAX) {
			lexeme[lexLen++] = nextChar;
			lexeme[lexLen] = 0;
		} else {
			out.println("Error - lexeme is too long");
		}
	}

	@SuppressWarnings("deprecation")
	private void getNonBlank() {
		while (Character.isSpace(nextChar))
			getChar();
	}

	private void getChar() {
		nextChar = scanner.next().charAt(0);
		if (Character.isAlphabetic(nextChar))
			charClass = CharClass.LETTER;
		else if (Character.isDigit(nextChar))
			charClass = CharClass.DIGIT;
		else
			charClass = CharClass.UNKNOWN;
	}

	public static void main(String[] args) {
		try {
			LexicalAnalyzer analyzer = new LexicalAnalyzer("front.in");
			analyzer.setOutPut(System.out);
			analyzer.analyze();
		} catch (FileNotFoundException e) {
			System.exit(errorType.FILENOTFOUND.getValue());
		}
	}
}

enum Token {
	UNKNOWN(-1), INT_LIT(10), IDENT(11), ASSIGN_OP(20), ADD_OP(21), SUB_OP(22), MULT_OP(23), DIV_OP(24), 
	LEFT_PAREN(25), RIGHT_PAREN(26), SEMI_COLUMN(27), COMP_OP_SMALL(28), COMP_OP_LARGE(29), 
	COMP_OP_EQUAL(30), QUOT_MARK_DOU(31), EXCLAMATION(32);
	private final int value;

	private Token(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}

enum CharClass {
	LETTER, DIGIT, UNKNOWN
}

enum errorType {
	FILENOTFOUND(1), IOERROR(2);
	private final int value;

	private errorType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}