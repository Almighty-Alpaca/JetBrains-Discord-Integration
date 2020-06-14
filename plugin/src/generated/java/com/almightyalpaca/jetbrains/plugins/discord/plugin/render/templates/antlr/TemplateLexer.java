// Generated from Template.g4 by ANTLR 4.7.2
package com.almightyalpaca.jetbrains.plugins.discord.plugin.render.templates.antlr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class TemplateLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		DOLLAR_SIGN=1, PERCENT_SIGN=2, PR_OPEN=3, PR_CLOSED=4, BR_OPEN=5, BR_CLOSED=6, 
		RAW_TEXT_DELIM_BEGIN=7, RAW_TEXT_DELIM_END=8, IF_sym=9, NAME=10, TEXT=11;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"DOLLAR_SIGN", "PERCENT_SIGN", "PR_OPEN", "PR_CLOSED", "BR_OPEN", "BR_CLOSED", 
			"RAW_TEXT_DELIM_BEGIN", "RAW_TEXT_DELIM_END", "IF_sym", "NAME_CHAR", 
			"NAME", "CHAR", "TEXT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'$'", "'%'", "'('", "')'", "'{'", "'}'", "'#\"'", "'\"#'", "'if'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "DOLLAR_SIGN", "PERCENT_SIGN", "PR_OPEN", "PR_CLOSED", "BR_OPEN", 
			"BR_CLOSED", "RAW_TEXT_DELIM_BEGIN", "RAW_TEXT_DELIM_END", "IF_sym", 
			"NAME", "TEXT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public TemplateLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Template.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\r@\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6"+
		"\3\7\3\7\3\b\3\b\3\b\3\t\3\t\3\t\3\n\3\n\3\n\3\13\3\13\3\f\6\f\66\n\f"+
		"\r\f\16\f\67\3\r\3\r\3\16\6\16=\n\16\r\16\16\16>\2\2\17\3\3\5\4\7\5\t"+
		"\6\13\7\r\b\17\t\21\n\23\13\25\2\27\f\31\2\33\r\3\2\4\4\2C\\c|\7\2$\'"+
		"*+C\\c}\177\177\2?\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13"+
		"\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\27\3\2\2"+
		"\2\2\33\3\2\2\2\3\35\3\2\2\2\5\37\3\2\2\2\7!\3\2\2\2\t#\3\2\2\2\13%\3"+
		"\2\2\2\r\'\3\2\2\2\17)\3\2\2\2\21,\3\2\2\2\23/\3\2\2\2\25\62\3\2\2\2\27"+
		"\65\3\2\2\2\319\3\2\2\2\33<\3\2\2\2\35\36\7&\2\2\36\4\3\2\2\2\37 \7\'"+
		"\2\2 \6\3\2\2\2!\"\7*\2\2\"\b\3\2\2\2#$\7+\2\2$\n\3\2\2\2%&\7}\2\2&\f"+
		"\3\2\2\2\'(\7\177\2\2(\16\3\2\2\2)*\7%\2\2*+\7$\2\2+\20\3\2\2\2,-\7$\2"+
		"\2-.\7%\2\2.\22\3\2\2\2/\60\7k\2\2\60\61\7h\2\2\61\24\3\2\2\2\62\63\t"+
		"\2\2\2\63\26\3\2\2\2\64\66\5\25\13\2\65\64\3\2\2\2\66\67\3\2\2\2\67\65"+
		"\3\2\2\2\678\3\2\2\28\30\3\2\2\29:\n\3\2\2:\32\3\2\2\2;=\5\31\r\2<;\3"+
		"\2\2\2=>\3\2\2\2><\3\2\2\2>?\3\2\2\2?\34\3\2\2\2\5\2\67>\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}