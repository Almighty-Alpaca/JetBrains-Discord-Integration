grammar Template;

template: text_eval;

DOLLAR_SIGN             :   '$'             ;
BR_OPEN                 :   '{'             ;
BR_CLOSED               :   '}'             ;
RAW_TEXT_DELIM_BEGIN    :   '#"'            ;
RAW_TEXT_DELIM_END      :   '"#'            ;
fragment NAME_CHAR      :   [a-zA-Z]        ;
NAME                    :   NAME_CHAR+      ;
fragment CHAR           :   ~[${}"#a-zA-Z]  ;
TEXT                    :   CHAR+           ;

raw_text: RAW_TEXT_DELIM_BEGIN (NAME | TEXT | DOLLAR_SIGN | BR_OPEN | BR_CLOSED)+ RAW_TEXT_DELIM_END;
text_eval: (NAME | TEXT | raw_text | fun | var)*;
var:    DOLLAR_SIGN NAME
   |    DOLLAR_SIGN BR_OPEN NAME BR_CLOSED;
fun: DOLLAR_SIGN NAME (BR_OPEN text_eval BR_CLOSED)+;
