#include <stdio.h>
#include <stdlib.h>
#include <string.h>

struct KEYWORDS {
	int num;
	char name[10];
};

char character='\0';
char token[1000]="";
struct KEYWORDS keywords[100]=
{
	{3,"int"},
	{4,"if"},
	{5,"else"},
	{6,"while"},
	{7,"for"},
	{8,"read"},
	{9,"write"},
	{0,""}
};

void getch()
{
	character=getchar();
}

void getnbc()
{
	while((character==' ') || (character=='\t') || (character=='\r') || (character=='\n')) 
		character=getchar();	
}

void concat()
{
	int len=strlen(token);
	token[len]=character;
	token[len+1]='\0';	
}

int letter()
{
	if(((character>='a') && (character<='z')) || ((character>='A') && (character<='Z')))
		return 1;
	else 
		return 0;
}

int digit()
{
	if((character>='0') && (character<='9'))
		return 1;
	else 
		return 0;
}

void retract()
{
	ungetc(character,stdin);
	character='\0';
}

int keyword()
{
	int i=0;
	while(strcmp(keywords[i].name,""))
	{
		if(!strcmp(keywords[i].name,token)) 
			return keywords[i].num;
		i++;
	}
	return 0;
}

int lex_analyze()
{
	int num;
	char *lexeme;
		
	strcpy(token,"");
	getch();
	getnbc();

	switch(character)
	{
		case 'a':
		case 'b':
		case 'c':
		case 'd':
		case 'e':
		case 'f':
		case 'g':
		case 'h':
		case 'i':
		case 'j':
		case 'k':
		case 'l':
		case 'm':
		case 'n':
		case 'o':
		case 'p':
		case 'q':
		case 'r':
		case 's':
		case 't':
		case 'u':
		case 'v':
		case 'w':
		case 'x':
		case 'y':
		case 'z':
		case 'A':
		case 'B':
		case 'C':
		case 'D':
		case 'E':
		case 'F':
		case 'G':
		case 'H':
		case 'I':
		case 'J':
		case 'K':
		case 'L':
		case 'M':
		case 'N':
		case 'O':
		case 'P':
		case 'Q':
		case 'R':
		case 'S':
		case 'T':
		case 'U':
		case 'V':
		case 'W':
		case 'X':
		case 'Y':
		case 'Z':
		while(letter() || digit())
		{
			concat();
			getch();
		}
		retract();
		num=keyword(); // keyword
		if(num!=0) 
			printf("( %d , - )\n", num); // keyword
		else
			printf("( 1 , %s )\n", token); // identifier
		break;

		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
		while(digit())
		{
			concat();
			getch();
		}
		retract();
		printf("( 2 , %s )\n", token); // constant
		break;

		case '+':
		printf("( 10 , - )\n"); // +
		break;

		case '-':
		printf("( 11 , - )\n"); // -
		break;

		case '*':
		printf("( 12 , - )\n"); // *
		break;

		case '/':
		printf("( 13 , - )\n"); // /
		break;

		case '<':
		concat();
		getch();
		if(character=='=') 
		{
			printf("( 15 , - )\n"); // <=
		}
		else
		{
			retract();
			printf("( 14 , - )\n"); // <
		}		
		break;

		case '>':
		concat();
		getch();
		if(character=='=') 
		{
			printf("( 17 , - )\n"); // >=
		}
		else
		{
			retract();
			printf("( 16 , - )\n"); // >
		}		
		break;

		case '!':
		concat();
		getch();
		if(character=='=') 
		{
			printf("( 18 , - )\n"); // !=
		}
		else
		{
			retract();
			printf("error: !\n"); // error: !
		}		
		break;
		
		case '=':
		concat();
		getch();
		if(character=='=') 
		{
			printf("( 19 , - )\n"); // ==
		}
		else
		{
			retract();
			printf("( 20 , - )\n"); // =
		}		
		break;

		case '(':
		printf("( 21 , - )\n"); // (
		break;

		case ')':
		printf("( 22 , - )\n"); // )
		break;

		case ',':
		printf("( 23 , - )\n"); // ,
		break;

		case ';':
		printf("( 24 , - )\n"); // ;
		break;

		case EOF:
		return 0;

		default:
		printf("error: '%c'\n", character);
	}

	return 1;
}

int main(int argc,   char *argv[])
{
	if(argc!=2)
	{
		printf("usage: %s input_file\n", argv[0]);
		exit(0);
	}

	if(freopen(argv[1], "r", stdin)==NULL)
	{
		printf("error: can not open file \"%s\"\n", argv[1]);
		exit(0);
	}

	while(1)
	{
		if(lex_analyze()==0) break;
	}
	
	return 0;
}

