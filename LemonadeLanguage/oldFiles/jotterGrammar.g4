grammar jotterGrammar;

options{
	language = Java;
}

parse:(lines NL)* EOF;

lines: 
	commenting
	| declaring_arrays
	| declaring_integers
	| declaring_decimals
	| declaring_strings
	| declaring_booleans
	| print_to_console
	| console_get
	| random_gen
	| random_pick
	| array_length
	| array_getelement
	| array_editelement
	| array_changeelement
	| array_sort
	| array_prime
	| array_find
	| math_add
	| math_addone
	| math_addinfront
	| math_subtract
	| math_subtractone
	| math_subtractinfront
	| math_divide
	| math_divideinfront
	| math_multiply
	| math_multiplyinfront;

//Tokens for data types and making arrays:

INTEGERARR:'int[';

DECIMALARR:'dec[';

STRINGARR:'str[';

BOOLEANARR:'bol[';

INTEGER:'int';

DECIMAL:'dec';

STRING:'str';

BOOLEAN:'bol';

//Number fragments to make types

fragment
FORDIGITS:[-?0-9]+;

fragment FORDECIMALS:([.][0-9]+);

//statement tokens:

IF: 'if';

ELSE: 'else';

THEN: 'then';

ENDOF: '~';

//Other tokens:

EXTRADOT: '.';

LENGTH: 'length';

EQUALS:'=';

DOUBLEQUOTES:'"';

CONSOLESTART:'Console.';

RANDOMSTART:'Random.';

TOPRINT:'print';

TOINPUT:'input';

TOSORT: 'sort';

TOGETRANDNUM:'getNum';

TOPICKNUM:'pick';

TOGETNUM:'get';

INSERT: 'insert';

REMOVE: 'remove';

PRIME: 'prime';

FIND: 'find';

BRACKETSTART:'(';

BRACKETEND:')';

COMMA:',';

COMMENTMARK:'#';

SQUAREBRACKETSTART:'[';

SQUAREBRACKETEND:']';

FANCYBRACKETSTART:'{';

FANCYBRACKETEND:'}';

//Math tokens:

ADD:
'+';

SUBTRACT:'-';

DIVIDE:'/';

MULTIPLY:'*';

NULL:'null';

SHORTADD:ADD EQUALS;

SHORTMINUS:SUBTRACT EQUALS;

SHORTDIVIDE:DIVIDE EQUALS;

SHORTMULTIPLY:MULTIPLY EQUALS;

ADDBYONE:'++';

MINUSBYONE:'--';

//Comparators

SAME:'==';

NOT:'!=';

AND:'&&';

OR:'||';

GREATERTHAN:'>';

LESSTHAN:'<';

GREATEREQUALS:'>=';

LESSEQUALS:'<=';

COMPARATORTOGETHER: SAME | NOT | GREATERTHAN | LESSTHAN | 
	GREATEREQUALS | LESSEQUALS;

//Actual Storage

FORBOOL: ('true' | 'false');

INTNUMS:FORDIGITS;

VARNAMES:[a-zA-Z0-9]+;

NAMES:DOUBLEQUOTES VARNAMES DOUBLEQUOTES;

DECNUMS:FORDIGITS FORDECIMALS;

//Some function stuff:

commenting: COMMENTMARK ((VARNAMES) |  
	(NAMES) | 
	(DECNUMS) | 
	(FORBOOL) | 
	(INTNUMS) | 
	(EXTRADOT))+ 
	COMMENTMARK;

declaring_arrays: ((INTEGERARR)+
		| (DECIMALARR)+
		| (STRINGARR)+
		| (BOOLEANARR)+) INTNUMS SQUAREBRACKETEND VARNAMES EQUALS
		(random_gen
		|array_prime
		| FANCYBRACKETSTART
		((VARNAMES)+
		|(NAMES)+
		|(INTNUMS)+
		|(FORBOOL)+
		|(DECNUMS)+
		|(COMMA)+)+ FANCYBRACKETEND
		| NULL);

declaring_integers:INTEGER VARNAMES EQUALS (random_pick | INTNUMS | VARNAMES | console_get);

declaring_decimals:DECIMAL VARNAMES EQUALS (DECNUMS | console_get);

declaring_strings:STRING VARNAMES EQUALS (NAMES | console_get);

declaring_booleans:BOOLEAN VARNAMES EQUALS (FORBOOL | console_get);

print_to_console:CONSOLESTART TOPRINT BRACKETSTART(((VARNAMES)+
			|(NAMES)+
			|(INTNUMS)+
			|(DECNUMS)+
			|random_pick
			|array_length
			|array_getelement
			|array_prime
			|array_find
			|(ADD)+)+
			(ADD)*)* BRACKETEND;

console_get:CONSOLESTART TOINPUT;

random_gen:RANDOMSTART TOGETRANDNUM BRACKETSTART INTNUMS COMMA INTNUMS COMMA INTNUMS BRACKETEND;
//Where third number is the size of the array

random_pick: RANDOMSTART TOPICKNUM BRACKETSTART VARNAMES BRACKETEND;

array_length: VARNAMES EXTRADOT LENGTH;

array_getelement: VARNAMES SQUAREBRACKETSTART INTNUMS SQUAREBRACKETEND;

array_editelement: VARNAMES EXTRADOT ((INSERT) | (REMOVE))+ BRACKETSTART ((INTNUMS) | (VARNAMES) | (DECNUMS) | (NAMES) | (FORBOOL))+ BRACKETEND;

array_changeelement: array_getelement EQUALS ((INTNUMS) | (VARNAMES) | (DECNUMS) | (NAMES) | (FORBOOL))+;

array_sort: VARNAMES EXTRADOT TOSORT;

array_prime: VARNAMES EXTRADOT PRIME;

array_find: VARNAMES EXTRADOT FIND BRACKETSTART ((VARNAMES) | (NAMES) | (INTNUMS) | (DECNUMS) | (FORBOOL))+ BRACKETEND;

math_add: VARNAMES EQUALS (VARNAMES| INTNUMS)+ ADD (VARNAMES| INTNUMS)+;

math_addone: VARNAMES ADDBYONE;

math_addinfront: VARNAMES ADD EQUALS (VARNAMES | INTNUMS)+;

math_subtract: VARNAMES EQUALS (VARNAMES| INTNUMS)+ SUBTRACT (VARNAMES| INTNUMS)+;

math_subtractone: VARNAMES MINUSBYONE;

math_subtractinfront: VARNAMES SUBTRACT EQUALS (VARNAMES | INTNUMS)+;

math_divide: VARNAMES EQUALS (VARNAMES| INTNUMS)+ DIVIDE (VARNAMES| INTNUMS)+;

math_divideinfront: VARNAMES DIVIDE EQUALS (VARNAMES | INTNUMS)+;

math_multiply:  VARNAMES EQUALS (VARNAMES| INTNUMS)+ MULTIPLY (VARNAMES| INTNUMS)+;

math_multiplyinfront: VARNAMES MULTIPLY EQUALS (VARNAMES | INTNUMS)+;

//ifelse_statement: IF (VARNAMES COMPARATORTOGETHER (VARNAMES | NAMES | INTNUMS | DECNUMS | FORBOOL)
	//(AND | OR)*)+ THEN 
	//(commenting
	//| declaring_arrays
	//| declaring_integers
	//| declaring_decimals
	//| declaring_strings
	//| declaring_booleans
	//| print_to_console
	//| console_get
	//| random_gen
	//| random_pick
	//| array_length
	//| array_getelement
	//| array_editelement
	//| array_changeelement
	//| array_sort
	//| array_prime
	//| array_find
	//| math_add
	//| math_addone
	//| math_addinfront
	//| math_subtract
	//| math_subtractone
	//| math_subtractinfront
	//| math_divide
	//| math_divideinfront
	//| math_multiply
	//| math_multiplyinfront)+
	//ELSE 
	//(commenting
	//| declaring_arrays
	//| declaring_integers
	//| declaring_decimals
	//| declaring_strings
	//| declaring_booleans
	//| print_to_console
	//| console_get
	//| random_gen
	//| random_pick
	//| array_length
	//| array_getelement
	//| array_editelement
	//| array_changeelement
	//| array_sort
	//| array_prime
	//| array_find
	//| math_add
	//| math_addone
	//| math_addinfront
	//| math_subtract
	//| math_subtractone
	//| math_subtractinfront
	//| math_divide
	//| math_divideinfront
	//| math_multiply
	//| math_multiplyinfront)+
	//ENDOF;

WS:[ \t\u000C] -> skip; //skip spaces and tabs

NL:'\r'? '\n';
