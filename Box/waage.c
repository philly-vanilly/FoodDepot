#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>

#include <termios.h>
#include <fcntl.h>

#include <dirent.h>
#include <time.h>

#include <pthread.h>

#include <netinet/in.h>
#include <netdb.h>
#include <sys/types.h>
#include <sys/socket.h>

#define tolerance 5
#define intervall 10000

int main() {


	/**
	errorcodes in wlog[x][1]
	9 das gewicht ist gestiegen
	6 das gewicht ist gesunken, passt aber nicht zum gewählten produkt
	0 das gewicht ist gesunken, und passt zum gewählten produkt
	###################
	
	**/
	
	int counter = 0;
	int wlog[10000][3];
	memset(wlog, 0, sizeof(wlog));
	char dlog[10000][30];
	memset(dlog, 0, sizeof(dlog));
	int opened = 0;
	FILE *datei;

	char day[100];
	memset(day, 0, sizeof(day));

	/** Diese Struktur speichert das Produkt, das im Kasten angeboten wird. Es enthält den Namen, das Gewicht, das das Produkt üblicherweise hat, und die übliche Abweichung. Des weiteren den Preis, und ob das Produkt nacht Gewicht 		oder pro Einheit verkauft wird. **/
	struct products{
		char name[20];
		int gewicht;
		int abweichung;
		int preis;
		int einheit;
	};

	struct products product;

	memset(product.name, 0, sizeof(product.name));
	product.gewicht = 1000;
	product.abweichung = 100;
	product.preis = 100;
	product.einheit = 999;
	
	//Zeitvariablen
	struct tm *ts;
	time_t sek;
	
	int saver()
	{
	//	date();
		printf("*saver* \n");

		//Der Name der Datei wird generiert
		char title[19] = "foodlog2.txt";

		datei = fopen (title, "a+");	
		if (datei == NULL)
		{
			printf("Fehler beim Ã¶ffnen der Datei");
			return 0;
		}
		printf("datei erfolgreich geöffnet \n");
		//In die Datei wird geschrieben
		fprintf(datei, "\n /anfang/ \n \n");
		return 1;		
	}

	
	int save(char val[10000])
	{
		printf("*save* ? pointer:     %d \n", datei);
		fprintf(datei, "%s", val);
	}
	
	int json()
	{
	printf("*json* \n");
/**
		char id[20] = "00000000000000000000";
		char weight[20] = "00000000000000000000";
		char errorcode[2] = "00";
		
		sprintf(id, "%d", counter);
		sprintf(weight, "%s", wlog[counter][0]);
		sprintf(errorcode, "%s", wlog[counter][1]);
		
		save('{ \n}');
		save("id : ");
		save(id);
		save(", \n");
		
		save("errorcode :");
		save(errorcode);
		save(", \n");
		
		save("weight :");
		save(weight);
		save(", \n");
		
		save("time : ");
		save(date());
		save(", \n }";
**/
		char json[10000];
		memset(json, 0, sizeof(json));
		
		sprintf(json, "{ \n id : %d, \n errorcode : %d, \n weight : %d, \n time : %s, \n}", counter, wlog[counter][0], wlog[counter][1], day);
		printf("{ \n id : %d, \n errorcode : %d, \n weight : %d, \n time : %s, \n}\n", counter, wlog[counter][1], wlog[counter][0], day);
		save(json);
	}

	int date()
	{     
	    sek = time(NULL);
	    ts = localtime(&sek);
		
		printf("date(): %d \n",strftime(day, 100, "%d.%m.%Y", ts));
		strftime(day, 100, "%d.%m.%Y", ts);	
	
	    return 0;
	}

	int makefoto() {
	}
	
	//Bei Änderungen des Wertes der Waage, die keine ungefähren Vielfachen des Produktgewichtes sind, wird der Fehlerwert um die Differenz vergrößert. Dabe ist es wichtig zwei Werte zu haben, damit sich positive und negative Fehler nicht aufheben können.
	int posfail = 0;
	int negfail = 0;

	int weigh()
	{	int iweight = 0;
		char cweight[10] = "0000000000";
		printf("*weigh*        type weight: ");
		scanf("%s", &cweight);
		iweight = atoi(cweight);
		makefoto();
		return iweight;
	}
	


	int stage1(int weight, int ref)
	{
		printf("*stage1*        ref - tolerance = %d \n", (ref-tolerance));
		if((ref - tolerance) < weight && weight < (ref + tolerance))
		{	
			printf("*stage1*        1 \n");
			return 1;
		}
		else
		{
			printf("*stage1*        0 \n");
			return 0;
		}
	}
	int fruitstage(int weight)
	{
		if((product.gewicht - product.abweichung) < weight < (product.gewicht + product.abweichung))
			return 1;
		else
			return 0;
	}

	int stage2(int weight) 
	{
		printf("stage2 \n");
		int count = 0;
		int list[100];
		memset(list, 0, sizeof(list));
		list[count] = weight;
		int test = 0;

		while(1)
		{
			int val = weigh();
		
			if(stage1(val, list[count]))
			{
				test++;
				printf("*stage2*         test: %d \n", test);

				if(test >= 4)
				return val;
			}
			else
			{
				test = 0;
				printf("*stage2*    else test: %d \n", test);
			}

			if(!stage1(val, list[count]) && val > list[count])
			{
		//		wlog[counter+1][1] += 10;
			}

		count++;
		list[count] = val;
		usleep(intervall);
		}


		
	}
		
	int stage3(int weight)
	{
		printf("stage3 \n");
		if(weight > wlog[counter][0])
		{
			negfail += weight - wlog[counter][0];
			wlog[counter+1][0] = weight;
			wlog[counter+1][1] += 9;
			date();
			strcpy(dlog[counter+1], day);

		}
		else
		{
			wlog[counter+1][0] = weight;
			if(fruitstage(!weight))
			{
				negfail += wlog[counter][0] - weight;
				wlog[counter+1][1] += 6;
				strcpy(dlog[counter+1], date());
			}
		}
		printf("stage3 end* \n");
	}
		
	void *weightloop() 
		{		
			printf("thread started \n");	
			while (1)
			{

				int weight = weigh();
				printf("*loop*           wlog counter: %d \n", wlog[counter][0]);

				if(!stage1(weight, wlog[counter][0]))
				{
					printf("*loop*               stage1 test checked \n");
					weight = stage2(weight);
					stage3(weight);
				//	wlog[counter][0] = weight;
					counter++;
					json();
				}

				else
				{
					printf("*loop*              no change \n");
				}


				printf("*loop*               counter: %d \n", counter);
				date();
				strcpy(dlog[counter+1], day);
				printf("test************************************************************* \n");
				makefoto();

			}
		}
saver();
date();
printf("day %s: \n", day);
wlog[counter][0] = weigh();
printf("main        wlog: %d \n", wlog[counter][0]);
strcpy(dlog[counter], day);

pthread_t thread = NULL;
pthread_create(&thread, NULL, &weightloop, NULL);
while(1){};


	
}
