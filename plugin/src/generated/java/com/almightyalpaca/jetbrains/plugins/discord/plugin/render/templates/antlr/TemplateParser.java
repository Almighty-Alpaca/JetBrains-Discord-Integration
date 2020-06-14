// Generated from Template.g4 by ANTLR 4.7.2
package com.almightyalpaca.jetbrains.plugins.discord.plugin.render.templates.antlr;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class TemplateParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		DOLLAR_SIGN=1, PERCENT_SIGN=2, PR_OPEN=3, PR_CLOSED=4, BR_OPEN=5, BR_CLOSED=6, 
		RAW_TEXT_DELIM_BEGIN=7, RAW_TEXT_DELIM_END=8, IF_sym=9, NAME=10, TEXT=11;
	public static final int
		RULE_template = 0, RULE_raw_text = 1, RULE_text_eval = 2, RULE_var = 3, 
		RULE_fun = 4, RULE_if_rule = 5;
	private static String[] makeRuleNames() {
		return new String[] {
			"template", "raw_text", "text_eval", "var", "fun", "if_rule"
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

	@Override
	public String getGrammarFileName() { return "Template.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public TemplateParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class TemplateContext extends ParserRuleContext {
		public Text_evalContext text_eval() {
			return getRuleContext(Text_evalContext.class,0);
		}
		public TemplateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_template; }
	}

	public final TemplateContext template() throws RecognitionException {
		TemplateContext _localctx = new TemplateContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_template);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(12);
			text_eval();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Raw_textContext extends ParserRuleContext {
		public TerminalNode RAW_TEXT_DELIM_BEGIN() { return getToken(TemplateParser.RAW_TEXT_DELIM_BEGIN, 0); }
		public TerminalNode RAW_TEXT_DELIM_END() { return getToken(TemplateParser.RAW_TEXT_DELIM_END, 0); }
		public List<TerminalNode> IF_sym() { return getTokens(TemplateParser.IF_sym); }
		public TerminalNode IF_sym(int i) {
			return getToken(TemplateParser.IF_sym, i);
		}
		public List<TerminalNode> PR_OPEN() { return getTokens(TemplateParser.PR_OPEN); }
		public TerminalNode PR_OPEN(int i) {
			return getToken(TemplateParser.PR_OPEN, i);
		}
		public List<TerminalNode> PR_CLOSED() { return getTokens(TemplateParser.PR_CLOSED); }
		public TerminalNode PR_CLOSED(int i) {
			return getToken(TemplateParser.PR_CLOSED, i);
		}
		public List<TerminalNode> NAME() { return getTokens(TemplateParser.NAME); }
		public TerminalNode NAME(int i) {
			return getToken(TemplateParser.NAME, i);
		}
		public List<TerminalNode> TEXT() { return getTokens(TemplateParser.TEXT); }
		public TerminalNode TEXT(int i) {
			return getToken(TemplateParser.TEXT, i);
		}
		public List<TerminalNode> DOLLAR_SIGN() { return getTokens(TemplateParser.DOLLAR_SIGN); }
		public TerminalNode DOLLAR_SIGN(int i) {
			return getToken(TemplateParser.DOLLAR_SIGN, i);
		}
		public List<TerminalNode> BR_OPEN() { return getTokens(TemplateParser.BR_OPEN); }
		public TerminalNode BR_OPEN(int i) {
			return getToken(TemplateParser.BR_OPEN, i);
		}
		public List<TerminalNode> BR_CLOSED() { return getTokens(TemplateParser.BR_CLOSED); }
		public TerminalNode BR_CLOSED(int i) {
			return getToken(TemplateParser.BR_CLOSED, i);
		}
		public Raw_textContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_raw_text; }
	}

	public final Raw_textContext raw_text() throws RecognitionException {
		Raw_textContext _localctx = new Raw_textContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_raw_text);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(14);
			match(RAW_TEXT_DELIM_BEGIN);
			setState(16); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(15);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << DOLLAR_SIGN) | (1L << PR_OPEN) | (1L << PR_CLOSED) | (1L << BR_OPEN) | (1L << BR_CLOSED) | (1L << IF_sym) | (1L << NAME) | (1L << TEXT))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				}
				setState(18); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << DOLLAR_SIGN) | (1L << PR_OPEN) | (1L << PR_CLOSED) | (1L << BR_OPEN) | (1L << BR_CLOSED) | (1L << IF_sym) | (1L << NAME) | (1L << TEXT))) != 0) );
			setState(20);
			match(RAW_TEXT_DELIM_END);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Text_evalContext extends ParserRuleContext {
		public List<TerminalNode> NAME() { return getTokens(TemplateParser.NAME); }
		public TerminalNode NAME(int i) {
			return getToken(TemplateParser.NAME, i);
		}
		public List<TerminalNode> TEXT() { return getTokens(TemplateParser.TEXT); }
		public TerminalNode TEXT(int i) {
			return getToken(TemplateParser.TEXT, i);
		}
		public List<TerminalNode> PR_OPEN() { return getTokens(TemplateParser.PR_OPEN); }
		public TerminalNode PR_OPEN(int i) {
			return getToken(TemplateParser.PR_OPEN, i);
		}
		public List<TerminalNode> PR_CLOSED() { return getTokens(TemplateParser.PR_CLOSED); }
		public TerminalNode PR_CLOSED(int i) {
			return getToken(TemplateParser.PR_CLOSED, i);
		}
		public List<Raw_textContext> raw_text() {
			return getRuleContexts(Raw_textContext.class);
		}
		public Raw_textContext raw_text(int i) {
			return getRuleContext(Raw_textContext.class,i);
		}
		public List<FunContext> fun() {
			return getRuleContexts(FunContext.class);
		}
		public FunContext fun(int i) {
			return getRuleContext(FunContext.class,i);
		}
		public List<VarContext> var() {
			return getRuleContexts(VarContext.class);
		}
		public VarContext var(int i) {
			return getRuleContext(VarContext.class,i);
		}
		public List<If_ruleContext> if_rule() {
			return getRuleContexts(If_ruleContext.class);
		}
		public If_ruleContext if_rule(int i) {
			return getRuleContext(If_ruleContext.class,i);
		}
		public Text_evalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_text_eval; }
	}

	public final Text_evalContext text_eval() throws RecognitionException {
		Text_evalContext _localctx = new Text_evalContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_text_eval);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(32);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					setState(30);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
					case 1:
						{
						setState(22);
						match(NAME);
						}
						break;
					case 2:
						{
						setState(23);
						match(TEXT);
						}
						break;
					case 3:
						{
						setState(24);
						match(PR_OPEN);
						}
						break;
					case 4:
						{
						setState(25);
						match(PR_CLOSED);
						}
						break;
					case 5:
						{
						setState(26);
						raw_text();
						}
						break;
					case 6:
						{
						setState(27);
						fun();
						}
						break;
					case 7:
						{
						setState(28);
						var();
						}
						break;
					case 8:
						{
						setState(29);
						if_rule();
						}
						break;
					}
					} 
				}
				setState(34);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VarContext extends ParserRuleContext {
		public TerminalNode DOLLAR_SIGN() { return getToken(TemplateParser.DOLLAR_SIGN, 0); }
		public TerminalNode NAME() { return getToken(TemplateParser.NAME, 0); }
		public TerminalNode BR_OPEN() { return getToken(TemplateParser.BR_OPEN, 0); }
		public TerminalNode BR_CLOSED() { return getToken(TemplateParser.BR_CLOSED, 0); }
		public VarContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var; }
	}

	public final VarContext var() throws RecognitionException {
		VarContext _localctx = new VarContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_var);
		try {
			setState(41);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(35);
				match(DOLLAR_SIGN);
				setState(36);
				match(NAME);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(37);
				match(DOLLAR_SIGN);
				setState(38);
				match(BR_OPEN);
				setState(39);
				match(NAME);
				setState(40);
				match(BR_CLOSED);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunContext extends ParserRuleContext {
		public TerminalNode DOLLAR_SIGN() { return getToken(TemplateParser.DOLLAR_SIGN, 0); }
		public TerminalNode NAME() { return getToken(TemplateParser.NAME, 0); }
		public List<TerminalNode> BR_OPEN() { return getTokens(TemplateParser.BR_OPEN); }
		public TerminalNode BR_OPEN(int i) {
			return getToken(TemplateParser.BR_OPEN, i);
		}
		public List<Text_evalContext> text_eval() {
			return getRuleContexts(Text_evalContext.class);
		}
		public Text_evalContext text_eval(int i) {
			return getRuleContext(Text_evalContext.class,i);
		}
		public List<TerminalNode> BR_CLOSED() { return getTokens(TemplateParser.BR_CLOSED); }
		public TerminalNode BR_CLOSED(int i) {
			return getToken(TemplateParser.BR_CLOSED, i);
		}
		public FunContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fun; }
	}

	public final FunContext fun() throws RecognitionException {
		FunContext _localctx = new FunContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_fun);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(43);
			match(DOLLAR_SIGN);
			setState(44);
			match(NAME);
			setState(49); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(45);
				match(BR_OPEN);
				setState(46);
				text_eval();
				setState(47);
				match(BR_CLOSED);
				}
				}
				setState(51); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==BR_OPEN );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class If_ruleContext extends ParserRuleContext {
		public TerminalNode PERCENT_SIGN() { return getToken(TemplateParser.PERCENT_SIGN, 0); }
		public TerminalNode IF_sym() { return getToken(TemplateParser.IF_sym, 0); }
		public TerminalNode PR_OPEN() { return getToken(TemplateParser.PR_OPEN, 0); }
		public List<Text_evalContext> text_eval() {
			return getRuleContexts(Text_evalContext.class);
		}
		public Text_evalContext text_eval(int i) {
			return getRuleContext(Text_evalContext.class,i);
		}
		public TerminalNode PR_CLOSED() { return getToken(TemplateParser.PR_CLOSED, 0); }
		public List<TerminalNode> BR_OPEN() { return getTokens(TemplateParser.BR_OPEN); }
		public TerminalNode BR_OPEN(int i) {
			return getToken(TemplateParser.BR_OPEN, i);
		}
		public List<TerminalNode> BR_CLOSED() { return getTokens(TemplateParser.BR_CLOSED); }
		public TerminalNode BR_CLOSED(int i) {
			return getToken(TemplateParser.BR_CLOSED, i);
		}
		public If_ruleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_if_rule; }
	}

	public final If_ruleContext if_rule() throws RecognitionException {
		If_ruleContext _localctx = new If_ruleContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_if_rule);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(53);
			match(PERCENT_SIGN);
			setState(54);
			match(IF_sym);
			setState(55);
			match(PR_OPEN);
			setState(56);
			text_eval();
			setState(57);
			match(PR_CLOSED);
			setState(58);
			match(BR_OPEN);
			setState(59);
			text_eval();
			setState(60);
			match(BR_CLOSED);
			setState(65);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==BR_OPEN) {
				{
				setState(61);
				match(BR_OPEN);
				setState(62);
				text_eval();
				setState(63);
				match(BR_CLOSED);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\rF\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\3\2\3\2\3\3\3\3\6\3\23\n\3\r\3\16\3"+
		"\24\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\7\4!\n\4\f\4\16\4$\13\4\3"+
		"\5\3\5\3\5\3\5\3\5\3\5\5\5,\n\5\3\6\3\6\3\6\3\6\3\6\3\6\6\6\64\n\6\r\6"+
		"\16\6\65\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\5\7D\n\7\3\7"+
		"\2\2\b\2\4\6\b\n\f\2\3\5\2\3\3\5\b\13\r\2K\2\16\3\2\2\2\4\20\3\2\2\2\6"+
		"\"\3\2\2\2\b+\3\2\2\2\n-\3\2\2\2\f\67\3\2\2\2\16\17\5\6\4\2\17\3\3\2\2"+
		"\2\20\22\7\t\2\2\21\23\t\2\2\2\22\21\3\2\2\2\23\24\3\2\2\2\24\22\3\2\2"+
		"\2\24\25\3\2\2\2\25\26\3\2\2\2\26\27\7\n\2\2\27\5\3\2\2\2\30!\7\f\2\2"+
		"\31!\7\r\2\2\32!\7\5\2\2\33!\7\6\2\2\34!\5\4\3\2\35!\5\n\6\2\36!\5\b\5"+
		"\2\37!\5\f\7\2 \30\3\2\2\2 \31\3\2\2\2 \32\3\2\2\2 \33\3\2\2\2 \34\3\2"+
		"\2\2 \35\3\2\2\2 \36\3\2\2\2 \37\3\2\2\2!$\3\2\2\2\" \3\2\2\2\"#\3\2\2"+
		"\2#\7\3\2\2\2$\"\3\2\2\2%&\7\3\2\2&,\7\f\2\2\'(\7\3\2\2()\7\7\2\2)*\7"+
		"\f\2\2*,\7\b\2\2+%\3\2\2\2+\'\3\2\2\2,\t\3\2\2\2-.\7\3\2\2.\63\7\f\2\2"+
		"/\60\7\7\2\2\60\61\5\6\4\2\61\62\7\b\2\2\62\64\3\2\2\2\63/\3\2\2\2\64"+
		"\65\3\2\2\2\65\63\3\2\2\2\65\66\3\2\2\2\66\13\3\2\2\2\678\7\4\2\289\7"+
		"\13\2\29:\7\5\2\2:;\5\6\4\2;<\7\6\2\2<=\7\7\2\2=>\5\6\4\2>C\7\b\2\2?@"+
		"\7\7\2\2@A\5\6\4\2AB\7\b\2\2BD\3\2\2\2C?\3\2\2\2CD\3\2\2\2D\r\3\2\2\2"+
		"\b\24 \"+\65C";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}