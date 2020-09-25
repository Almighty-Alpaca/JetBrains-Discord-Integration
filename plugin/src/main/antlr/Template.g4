grammar Template;

template: text_eval;

DOLLAR_SIGN             :   '$'                 ;
PERCENT_SIGN            :   '%'                 ;
PR_OPEN                 :   '('                 ;
PR_CLOSED               :   ')'                 ;
BR_OPEN                 :   '{'                 ;
BR_CLOSED               :   '}'                 ;
RAW_TEXT_DELIM_BEGIN    :   '#"'                ;
RAW_TEXT_DELIM_END      :   '"#'                ;
IF_sym                  :   'if'                ;
fragment NAME_CHAR      :   [a-zA-Z]            ;
NAME                    :   NAME_CHAR+          ;
fragment CHAR           :   ~[$(){}"#a-zA-Z%]   ;
TEXT                    :   CHAR+               ;

raw_text:   RAW_TEXT_DELIM_BEGIN (IF_sym | PR_OPEN | PR_CLOSED | NAME | TEXT | DOLLAR_SIGN | BR_OPEN | BR_CLOSED)+ RAW_TEXT_DELIM_END;
text_eval:  (NAME | TEXT | PR_OPEN | PR_CLOSED | raw_text | fun | var | if_rule)*;
var:        DOLLAR_SIGN NAME
   |        DOLLAR_SIGN BR_OPEN NAME BR_CLOSED;
fun:        DOLLAR_SIGN NAME (BR_OPEN text_eval BR_CLOSED)+;
if_rule:    PERCENT_SIGN IF_sym PR_OPEN text_eval PR_CLOSED BR_OPEN text_eval BR_CLOSED (BR_OPEN text_eval BR_CLOSED)?;
