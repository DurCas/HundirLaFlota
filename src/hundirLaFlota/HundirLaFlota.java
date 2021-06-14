package hundirLaFlota;

import java.util.Random;
import java.util.Scanner;

public class HundirLaFlota {
	
	//CONSTANTS
	final static char AIGUA_NO_TOCAT 	= '.';
	final static char AIGUA 			= 'A';
	final static char TOCAT 			= 'X';
	
	//TAMANY
	final static int TAMANY = 10;
	
	private static Scanner sc;
	
	public static void main(String[] args) {
		
		//Llegir des del teclat
		sc = new Scanner(System.in); 
		
		//MAPES
		char[][] mapaUsuari 			= new char[TAMANY][TAMANY];
		char[][] mapaOrdinador 			= new char[TAMANY][TAMANY];
		//Mapa per anotar i visualitzar
		char[][] mapaOrdinadorXUsuari 	= new char[TAMANY][TAMANY];
		
		//PUNTS
		int ptsUsuari		= 24;
		int ptsOrdinador	= 24;
		
		//CONTROL
		//Si no queden vaixells a flote es passa a true
		boolean jocAcabat = false;
		
		//Indicar si el tir és correcte, així se'n realitza un altre
		boolean tirCorrecte = false;
		
		//Posicions de la tirada 
		int[] tir = new int[2];
		
		//Inicialitzar mapes, colocar vaixells
		inicialitzacio(mapaUsuari, mapaOrdinador);
		//Inicialitzar el mapa de registre
		inicialitzaMapaRegistre(mapaOrdinadorXUsuari);
		
		//Mentre quedin vaixells
		while (!jocAcabat) {
			//A l'inici pintem el mapa d'usuari
			System.out.println("MAPA USUARI");
			imprimirMapa(mapaUsuari);
			
			System.out.println("PUNTS RESTANTS DEL JUGADOR: " + ptsUsuari);
			System.out.println("TORN DEL JUGADOR");
			
			//Tirada de l'usuari
			tirCorrecte = false;
			while (!tirCorrecte) {
				//Sol·licitar coordenades per teclat
				tir = demanarCoordenada();
				//Veirficar si el tir és correcte o no
				if (tir[0] != -1 && tir[1] != -1) {
					//Pot ser incorrecte perque ja ha tirat sobre aquestes coordenades
					tirCorrecte = evaluarTir(mapaOrdinador, tir);
					if (!tirCorrecte) {
						System.out.println("TIR INCORRECTE");
					} 
				} else {
					System.out.println("TIR INCORRECTE");
				}
				//De no ser correcte, el jugador ha de tornar a tirar
			}
			
			//Actualizar mapa de l'ordinador i punts
			int ptsOrdinadorAnterior = ptsOrdinador;
			ptsOrdinador = actualitzarMapa(mapaOrdinador, tir, ptsOrdinador);
			
			//Actualitzem es mapa de registre i l'imprimim
			//Sabrem si la tirada ha sigut AIGUA O TOCAT si el número de punts decreix
			char tipusTir = (ptsOrdinadorAnterior - ptsOrdinador) > 0 ? TOCAT : AIGUA;
			actualitzarMapaRegistre(mapaOrdinadorXUsuari, tir, tipusTir);
			System.out.println("\nREGISTRE DEL MAPA DE L'ORDINADOR");
			imprimirMapa(mapaOrdinadorXUsuari);
			
			//El joc acaba si el número de punts arriba a 0
			jocAcabat = (ptsOrdinador == 0);
			
			//Si no ha guanyat el jugador, li toca a la màquina
			//Es torna a comprobar per quan el jugador fa l'últim punt
			if (!jocAcabat) {
				System.out.println("PUNTS RESTANTS DE L'ORDINADOR: "+ ptsOrdinador);
				System.out.println("TORN DE L'ORDINADOR");
				tirCorrecte = false;
				
				//Seguim els mateixos paràmetres de comprovació que ambl'usuari
				while (!tirCorrecte) {
					tir = tirAleatori();
					tirCorrecte = evaluarTir(mapaUsuari, tir);
				}
			}
			
			//Actualitzem el mapa
			ptsUsuari = actualitzarMapa(mapaUsuari, tir, ptsUsuari);
			
			//El joc acaba si el número de punts arriba a 0
			jocAcabat = (ptsUsuari == 0);
			
		} // FI DE LA PARTIDA. Algú ha guanyat
		
		if (ptsOrdinador == 0) {
			System.out.println("EL GUANYADOR HA ESTAT L'USUARI");
		} else {
			System.out.println("EL GUANYADOR HA ESTAT L'ORDINADOR");
		}
		sc.close();
	}

	//MÈTODES
	
	private static int[] demanarCoordenada() {
		System.out.println("Introdueix una casella (p.e. B4): ");
		String linea = sc.nextLine();
		
		//Passem la cadena a majúsucles
		linea = linea.toUpperCase();
		int[] t;
		
		//Comprovem que les coordenades introduides per l'usuari són correctes. EXPRESSIÓ REGULAR
		if (linea.matches("^[A-Z][0-9]*$")) {
			
			//Obtenim la lletra
			char lletra = linea.charAt(0);
			//El numero de la fila
			int fila = Character.getNumericValue(lletra) - Character.getNumericValue('A');
			
			//oBTENIR EL NÚMERO
			int columna = Integer.parseInt(linea.substring(1, linea.length()));
			//Si les coordenades estan dins del tamany del tauler ho retornem
			if (fila >= 0 && fila < TAMANY && columna >= 0 && columna <= TAMANY) {
				t = new int[] { fila, columna };
			} else { // Retornem -1 per demanar una altre tirada
				t = new int[] { -1, -1 };
			}				
		} else {
			t = new int[] { -1, -1 };			
		}
		
		return t;
	}
	
	//Mètode que ens permet evaluar si un tir és correcte (AIGUAO TOCAT) o si es tracta d'una casella per la que ja hem passat abans
	private static boolean evaluarTir(char[][] mapa, int[] t) {
		int fila 	= t[0];
		int columna = t[1];		
		return mapa[fila][columna] == AIGUA_NO_TOCAT || (mapa[fila][columna] >= '1' && mapa[fila][columna] <= '5');
	}

	//Mètode per inicialitzar els dos mapes
	public static void inicialitzacio(char[][] m1, char[][] m2) {
		inicialitzaMapa(m1);
		inicialitzaMapa(m2);		
	}
	
	//Mètode que inicialitza el mapa que mostrem a ll'usuari amb les tirades que ha fet sobre el mapa de l'ordinador
	private static void inicialitzaMapaRegistre(char[][] mapa) {		
		//Inicialitzem el mapa amb AIGUA_NO_TOCAT
		for (int i=0; i<TAMANY; i++) {
			for (int j=0; j<TAMANY; j++) {
				mapa[i][j] = AIGUA_NO_TOCAT;				
			}
		}
	}
	
	//Mètode que inicialitza un mapa de joc i col·loca els vaixells
	private static void inicialitzaMapa(char[][] mapa) {
			
		//Inicialitzem el mapa sencer a AIGUA NO TOCAT
		for (int i=0; i<TAMANY; i++) {
			for (int j=0; j<TAMANY; j++) {
				mapa[i][j] = AIGUA_NO_TOCAT;				
			}
		}
				
		/*
		* VAIXELLS:
		* 2 -> 5 caselles
		* 3 -> 3 caselles
		* 5 -> 1 casella
		*/
		int[] vaixells = {5, 5, 3, 3, 3, 1, 1, 1, 1, 1};
				
		//Direccions possibles
		char[] direccio = {'V', 'H'};
				
		//Per cada vaixell
		for (int v : vaixells) {
					
			//Intentar col·locar els vaixells
			//De gran a petit
			boolean colocat = false;
			while(!colocat) {
							
				//Obtenim una posició i direcció aleatòria
				int fila 	= aleatori();
				int columna = aleatori();
				char direc	= direccio[aleatori() % 2];
							
				//Mirar si el vaixell hi cap
				//Direccio Vertical
				if (direc == 'V') {
					//Fila + Tamany = Tamany -1?
					if (fila + v <= (TAMANY -1)) {
									
						//Comprovem que no se solapi amb un altre Vaixell
						boolean otro = false;
						for (int i = fila; (i <= fila + v) && !otro; i++) {
							if (mapa[i][columna] != AIGUA_NO_TOCAT) {
								otro = true;
							}
						}
									
						//Si no hi ha un altre vaixell el col·loquem
						if (!otro) {
							for (int i = fila; i < fila + v; i++) {
								mapa[i][columna] = Integer.toString(v).charAt(0);
							}
							colocat = true;
						}
					} 
				} else { //Direcció Horitzontal 
					if (columna + v <= (TAMANY -1)) {
										
						//Comprovem que no hi ha un altre vaixell que se solapi
						boolean otro = false;
						for (int j = columna; (j<= columna + v) && !otro; j++) {
							if (mapa[fila][j] != AIGUA_NO_TOCAT) {
								otro = true;
							}
						}
									
						//Si no hi ha un altre vaixell el col·loquem
						if (!otro) {
							for (int j = columna; j < columna + v; j++) {
								mapa[fila][j] = Integer.toString(v).charAt(0);
							}	
							colocat = true;
						}
					}
				}
			}		
		}
	}

	//Mètode que retorna un num aleatori
	private static int aleatori() {
		Random r = new Random(System.currentTimeMillis());
		return r.nextInt(TAMANY);
	}
	
	//Mètode que serveix perque l'ordinador pugui fer un tir
	private static int[] tirAleatori() {
		return new int[] {aleatori(), aleatori()};
	}

	//Mètode que imprimeix un mapa, amb una fila i una columna de capçalera
	private static void imprimirMapa(char[][] mapa) {
		//Calculem les lletres segons el tamany
		char[] lletres = new char[TAMANY];
		for (int i = 0; i < TAMANY; i++) {
			lletres[i] = (char) ('A' + i);
		}
		
		//Imprimim la fila de capçalera
		System.out.print("    ");
		for (int i = 0; i < TAMANY; i++) {
			System.out.print("[" + i + "] ");
		}
		
		System.out.println("");
		//Imprimim la resta de fileres
		for (int i = 0; i < TAMANY; i++) {
			System.out.print("[" + lletres[i] + "]  ");
			for ( int j = 0; j < TAMANY; j++) {
				System.out.print(mapa[i][j] + "   ");
			}
			System.out.println("");
		}
		
	}
	
	//Mètode que actualitza el mapa, amb un determinat tir. Retornem el número de punts restants
	private static int actualitzarMapa(char[][] mapa, int[] t, int pts) {

		int fila = t[0];
		int columna = t[1];
		
		if (mapa[fila][columna] == AIGUA_NO_TOCAT) {
			mapa[fila][columna] = AIGUA;
			System.out.println("AIGUA");
		} else {
			mapa[fila][columna] = TOCAT;
			System.out.println("HAS TOCAT UN VAIXELL");
			--pts;
		}
		return pts;		
	}
	
	//Mètode que inicialitza el mapa que mostrem a l'usuari amb les tirades que ha fet sobre el mapa de l'ordinador
	private static void actualitzarMapaRegistre(char[][] mapa, int[] t, char valor) {
		int fila = t[0];
		int columna = t[1];

		mapa[fila][columna] = valor;
		
	}


}
