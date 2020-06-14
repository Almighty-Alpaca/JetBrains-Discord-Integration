grammar Template;

template: text_eval;

DOLLAR_SIGN         :   '$'     ;
BR_OPEN             :   '{'     ;
BR_CLOSED           :   '}'     ;
fragment CHAR       :   ~[${}]  ;
TEXT                :   CHAR+   ;

text_eval: (TEXT | fun | var)*;
var: DOLLAR_SIGN BR_OPEN TEXT BR_CLOSED;
fun: DOLLAR_SIGN TEXT (BR_OPEN text_eval BR_CLOSED)+;
