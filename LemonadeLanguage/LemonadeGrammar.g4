grammar LemonadeGrammar;

//overarching statements

options{
	language = Java;
}

parse
:
	(
		lines NL*
	)* EOF
;

lines
:
	declaring_variables_statement
	| declaring_arrays_statement
	| array_length_statement
	| array_index_statement
	| ifelse_statement
	| whileloop_statement
	| dowhileloop_statement
	| forloop_statement
	| definefunction_statement
	| return_statement
	| printconsole_statement
;

//types

DECLARE_INTNUM
:
	'int'
;

DECLARE_DECNUM
:
	'dec'
;

DECLARE_BOOLEAN
:
	'bol'
;

DECLARE_STRING
:
	'str'
;

//number inputs

DECNUMS
:
	(
		[.] [0-9]+
	)
;

INTNUMS
:
	[-?0-9]+
;

FUNCTION
:
	'func'
;

RETURNS
:
	'returns'
;

CONSOLE
:
	'console'
;

PRINT
:
	'.print'
;

INPUT
:
	'.input'
;

//signs + other

EQUALS
:
	'='
;

GREATER
:
	'>'
;

LESSER
:
	'<'
;

NOT
:
	'!'
;

AND
:
	'&&'
;

OR
:
	'||'
;

PLUS
:
	'+'
;

MINUS
:
	'-'
;

MULTIPLY
:
	'x'
	| '*'
;

DIVIDE
:
	'/'
;

QUOTATIONS
:
	'"'
;

NULL
:
	'null'
;

LENGTH
:
	'.length'
;

INDEX
:
	'.get'
;

IF
:
	'if'
;

THEN
:
	'then'
;

ELSE
:
	'else'
;

DO
:
	'do'
;

WHILE
:
	'while'
;

FOR
:
	'for'
;

LEFTBRACKET
:
	'['
;

RIGHTBRACKET
:
	']'
;

COLON
:
	':'
;

COMMA
:
	','
;

TILDA
:
	'~'
;

//inputs

BOOLEANS
:
	(
		'true'
		| 'false'
	)
;

VARNAMES
:
	[a-zA-Z0-9]+
;

STRINGS
:
	QUOTATIONS VARNAMES QUOTATIONS
;

//code statements

declaring_variables_statement
:
	(
		(DECLARE_INTNUM)
		| (DECLARE_DECNUM)
		| (DECLARE_BOOLEAN)
		| (DECLARE_STRING)
	) VARNAMES EQUALS
	(
		(VARNAMES)
		| (INTNUMS)
		| (DECNUMS)
		| (BOOLEANS)
		| (STRINGS)
		| (array_length_statement) //returns an integer
		| (array_index_statement) //returns stuff from array
	)
;

declaring_arrays_statement
:
	(
		DECLARE_INTNUM
		| DECLARE_DECNUM
		| DECLARE_BOOLEAN
		| DECLARE_STRING
	) LEFTBRACKET INTNUMS RIGHTBRACKET EQUALS
	(
		VARNAMES
		| NULL
		| LEFTBRACKET
		(
			VARNAMES
			| INTNUMS
			| DECNUMS
			| BOOLEANS
			| STRINGS
		)+ RIGHTBRACKET
	)
;

array_length_statement
:
	VARNAMES LENGTH
;

array_index_statement
:
	VARNAMES INDEX LEFTBRACKET INTNUMS RIGHTBRACKET
;

ARGUMENTBODY
:
	(
		VARNAMES
		(
			EQUALS EQUALS
			| GREATER EQUALS
			| LESSER EQUALS
			| GREATER
			| LESSER
			| NOT EQUALS
		)
		(
			VARNAMES
			| INTNUMS
			| DECNUMS
			| BOOLEANS
			| STRINGS
		)
		(
			AND
			| OR
		)*
	)+
;

ifelse_statement
:
	IF ARGUMENTBODY THEN COLON lines ELSE* lines TILDA
;

whileloop_statement
:
	WHILE ARGUMENTBODY COLON lines TILDA
;

dowhileloop_statement
:
	DO COLON lines WHILE ARGUMENTBODY TILDA
;

forloop_statement
:
	FOR declaring_variables_statement COMMA ARGUMENTBODY COLON lines TILDA
;

definefunction_statement
//finish define function statement

:
	FUNCTION VARNAMES LEFTBRACKET
	(
		(
			DECLARE_INTNUM
			| DECLARE_DECNUM
			| DECLARE_BOOLEAN
			| DECLARE_STRING
		)+
		(
			VARNAMES
		)+
		(
			COMMA
		)*
	)+ RIGHTBRACKET RETURNS
	(
		DECLARE_INTNUM
		| DECLARE_DECNUM
		| DECLARE_BOOLEAN
		| DECLARE_STRING
	) COLON lines TILDA
;

return_statement
//return statement is necessary to make a function

:
	RETURNS
	(
		VARNAMES
		| INTNUMS
		| DECNUMS
		| BOOLEANS
		| STRINGS
	)
;

printconsole_statement
:
	CONSOLE PRINT LEFTBRACKET
	(
		(
			VARNAMES
			| INTNUMS
			| DECNUMS
			| BOOLEANS
			| STRINGS
		) PLUS*
	)+
;

WS
:
	[ \t\u000C] -> skip
; //skip spaces and tabs

NL
:
	'\r'? '\n'
;
//ignore tabs, spaces, and newlines
