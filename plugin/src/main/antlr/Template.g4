grammar Template;

template: text_eval;

DOLLAR_SIGN             :   '$'                 ;
PERCENT_SIGN            :   '%'                 ;
PR_OPEN                 :   '('                 ;
PR_CLOSED               :   ')'                 ;
BR_OPEN                 :   '{'                 ;
BR_CLOSED               :   '}'                 ;
RAW_TEXT                :   '#"' .*? '"#'       ;
IF_sym                  :   'if'                ;
fragment NAME_CHAR      :   [a-zA-Z]            ;
NAME                    :   NAME_CHAR+          ;
fragment CHAR           :   ~[$(){}a-zA-Z%#"]   ;
TEXT                    :   CHAR+               ;

raw_text_rule: RAW_TEXT;
text_eval:  (NAME | TEXT | raw_text_rule | fun | var | if_rule | PR_OPEN | PR_CLOSED | IF_sym | PERCENT_SIGN)*;
var:        DOLLAR_SIGN NAME
   |        DOLLAR_SIGN BR_OPEN NAME BR_CLOSED;
fun:        DOLLAR_SIGN NAME (BR_OPEN text_eval BR_CLOSED)+;
if_rule:    PERCENT_SIGN IF_sym PR_OPEN text_eval PR_CLOSED BR_OPEN text_eval BR_CLOSED (BR_OPEN text_eval BR_CLOSED)?;
