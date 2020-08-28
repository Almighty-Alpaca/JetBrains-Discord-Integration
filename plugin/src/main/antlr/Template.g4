grammar Template;

template: text_eval;

DOLLAR_SIGN             :   '$'     ;
BR_OPEN                 :   '{'     ;
BR_CLOSED               :   '}'     ;
RAW_TEXT_DELIM_BEGIN    :   '#"'    ;
RAW_TEXT_DELIM_END      :   '"#'    ;
fragment CHAR           :   ~[${}"#];
TEXT                    :   CHAR+   ;

raw_text: RAW_TEXT_DELIM_BEGIN (TEXT | DOLLAR_SIGN | BR_OPEN | BR_CLOSED)+ RAW_TEXT_DELIM_END;
text_eval: (TEXT | raw_text | fun | var)*;
var: DOLLAR_SIGN BR_OPEN TEXT BR_CLOSED;
fun: DOLLAR_SIGN TEXT (BR_OPEN text_eval BR_CLOSED)+;
