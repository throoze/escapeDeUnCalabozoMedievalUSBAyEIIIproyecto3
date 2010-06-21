import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

/**
 *
 * @author victor
 */
public class Main {

    // MODELO DE REPRESENTACIÓN:

    private String  inputFile;  // Nombre del archivo de entrada.

    private String  outputFile; // Nombre del archivo de salida.

    int [][][]      maze;       // Representacion del laberinto, en base a los
                                // nodos, tal que si (maze[i][j][k] < 0), se
                                // representa un bloque, y en caso contrario, se
                                // representa un camino libre; el cual será a su
                                // vez representado en el DiGraph con el número
                                // contenido en maze[i][j][k].

    BufferedReader  in;         // Buffer de entrada (Lectura)
    PrintStream     out;        // Flujo de salida (Escritura)

    int             l;          // No de niveles
    int             r;          // No de filas
    int             c;          // No de columnas
    int             s;          // Nodo de partida (Start)
    int             e;          // Nodo de llegada (End)
    int             numNodes;   // Número de nodos a introducir en el DiGraph.

    DiGraph         digrafo;    // Digrafo donde se representará el laberinto.


    /**
     *
     */
    public Main() {
        this.inputFile = "";
        this.outputFile = "";
        this.maze = new int[0][0][0];
        this.in = null;
        this.out = null;
        this.l = -1;
        this.r = -1;
        this.c = -1;
        this.s = -1;
        this.e = -1;
    }

    /**
     *
     * @param inFile
     * @param outFile
     * @throws IOException
     */
    public Main(String inFile, String outFile) throws IOException {
        this.inputFile = inFile;
        this.outputFile = outFile;

        // Se crea un objeto de tipo archivo para hacer el código más legible
        File file =  new File(this.inputFile);

        // Se verifica que el archivo este en condiciones de ser procesado
        if (file.exists() && file.isFile() && file.canRead())  {

            /* Si el archivo no es del formato nombreArchivo.input, lanza la
             * excepcion. Para verificar esto, analizamos la cadena inFile:
             */
            if (!this.inputFile.substring(this.inputFile.length() - 6).
                    equals(".input"))
            {
                throw new ExcepcionFormatoIncorrecto("Problema de formato en el"
                        + " nombre del archivo:\nSe esperaba un archivo con la "
                        + "extensión \".input\" y se encontró:\n\n\t\"" +
                        this.inputFile + "\"\n\n");
            }

            /* Si el archivo no es del formato nombreArchivo.output, lanza la
             * excepcion. Para verificar esto, analizamos la cadena outFile:
             */
            if (!this.outputFile.substring(this.outputFile.length() - 7).
                    equals(".output"))
            {
                throw new ExcepcionFormatoIncorrecto("Problema de formato en el"
                        + " nombre del archivo:\nSe esperaba un archivo con la "
                        + "extensión \".output\" y se encontró:\n\n\t\"" +
                        this.outputFile + "\"\n\n");
            }

            // Se inicializan la entrada y la salida del programa
            try {
                in = new BufferedReader(new FileReader(this.inputFile));
                out = new PrintStream(this.outputFile);
            } catch (FileNotFoundException ex) {
                throw new ExcepcionArchivoNoExiste("Problema al leer el " +
                        "archivo \"" + this.inputFile +"\": EL ARCHIVO NO " +
                        " SE ENCUENTRA!!!");
            }

            /* Se lee la primera línea para inicializar el resto de las
             * estructurasde datos usadas en el programa
             */
            String   primera = this.in.readLine();
            String[] tokens = primera.split(" ");
            // Se verifica que solo sean tres números:
            if (tokens.length != 3) {
                throw new ExcepcionFormatoIncorrecto("En la primera linea de" +
                        this.inputFile +" se esperaban 3 elementos y se " +
                        "consiguieron: " + tokens.length);
            }
            this.l = Integer.parseInt(tokens[0]);
            this.r = Integer.parseInt(tokens[1]);
            this.c = Integer.parseInt(tokens[2]);

            // Se inicializan el resto de las estructuras:
            this.maze = new int[this.r][this.c][this.l];
            // Listo!!!
        } else if (!file.exists()) {
            throw new ExcepcionArchivoNoExiste("Problema al leer el archivo " +
                    "\"" + this.inputFile +"\": EL ARCHIVO NO EXISTE!!!");
        } else if (!file.isFile()) {
            throw new ExcepcionNoEsArchivo("Problema al leer el archivo \"" +
                    this.inputFile +"\": NO ES UN ARCHIVO!!!");
        } else if (!file.canRead()) {
            throw new ExcepcionArchivoNoSePuedeLeer("Problema al leer el ar" +
                    "chivo \"" + this.inputFile +"\": ESTE ARCHIVO NO SE PUEDE" +
                    " LEER!!!");
        }
    }
    
    /**
     *
     */
    public void leerLaberinto() throws IOException{
        int nLines = (this.l * this.r) + (this.l);
        int lineCounter = 1;
        int nNodes = 0;
        String line = "";
        for (int k = 0; k < this.l; k++) {
            for (int i = 0; i < this.r; i++) {
                try {
                    line = in.readLine();
                } catch (IOException ex) {
                    throw new IOException("Problema leyendo la linea #"
                            + lineCounter);
                }
                if (!line.equals("")) {
                    String[] tokens = line.split("");
                    if (tokens.length != (this.c + 1) && !line.equals("")) {
                        throw new ExcepcionFormatoIncorrecto("\nProblema al " +
                                "leer la linea " + lineCounter + ":\nSe " +
                                "esperaban " + this.c + "elementos en esta " +
                                "línea, y se encontraron " +
                                (tokens.length - 1));
                    }
                    for (int j = 1; j < tokens.length; j++) {
                        if (tokens[j].matches("[SE.#]")) {
                            if (tokens[j].matches("[SE.]")) {
                                if (tokens[j].equals("S")) {
                                    this.s = nNodes;
                                } else if (tokens[j].equals("E")) {
                                    this.e = nNodes;
                                }
                                this.maze[i][j - 1][k] = nNodes;
                                nNodes++;
                                this.numNodes = nNodes;
                            } else {
                                this.maze[i][j - 1][k] = -1;
                            }
                        } else {
                            throw new ExcepcionFormatoIncorrecto("\nProblema "
                                    + "al leer la linea " + lineCounter + ".\n"
                                    + "Se esperaba alguno de los símbolos "
                                    + "[S,E,.,#], y se encontró: " + tokens[j]);
                        }
                    }
                } else {
                    i--;
                }
                lineCounter++;
            }
        }
        if (lineCounter != nLines) {
            throw new ExcepcionFormatoIncorrecto("\nEl archivo contiene " +
                    "un número de lineas distinto del especificado...");
        }
    }

    /**
     *
     * @return
     */
    private void llenarDigrafo() {
        this.digrafo = new DiGraphMatrix(this.numNodes);
        //this.digrafo = new DiGraphList(this.numNodes);

        /* Se recorre todo el laberinto (this.maze) en busca de caminos libres
         * para enlazarlos por medio de arcos. Chequeando todos y cada uno de
         * los nodos, nos aseguramos de que hayan arcos de ida y de vuelta en
         * los nodos que lo requieran.
         */
        for (int k = 0; k < this.l; k++) {
            for (int i = 0; i< this.r; i++) {
                for (int j = 0; j < this.c; j++) {
                    if (this.maze[i][j][k] != -1) {
                        // Se revisan las columnas anterior y siguiente
                        if (0 < j) {
                            if (this.maze[i][j-1][k] != -1) {
                                this.digrafo.addArc
                                           (this.maze[i][j][k],
                                            this.maze[i][j-1][k],
                                            1.0);
                            }
                        }
                        if (j < (this.c - 1)) {
                            if (this.maze[i][j+1][k] != -1) {
                                this.digrafo.addArc
                                           (this.maze[i][j][k],
                                            this.maze[i][j+1][k],
                                            1.0);
                            }
                        }
                        // Se revisan las filas anterior y siguiente
                        if (0 < i) {
                            if (this.maze[i-1][j][k] != -1) {
                                this.digrafo.addArc
                                           (this.maze[i][j][k],
                                            this.maze[i-1][j][k],
                                            1.0);
                            }
                        }
                        if (i < (this.r - 1)) {
                            if (this.maze[i+1][j][k] != -1) {
                                this.digrafo.addArc
                                           (this.maze[i][j][k],
                                            this.maze[i+1][j][k],
                                            1.0);
                            }
                        }
                        // Se revisan los niveles anterior y siguiente
                        if (this.maze[i][j][k] == 14) {
                            System.out.println("i vale: " + i);
                            System.out.println("j vale: " + j);
                            System.out.println("k vale: " + k);
                            System.out.println("this.maze[i][j][k] == " + this.maze[i][j][k]);
                            System.out.println("this.maze[i][j][k+1] == " + this.maze[i][j][k+1]);
                        }
                        if (0 < k) {
                            if (this.maze[i][j][k-1] != -1) {
                                this.digrafo.addArc
                                           (this.maze[i][j][k],
                                            this.maze[i][j][k-1],
                                            1.0);
                            }
                        }
                        if (k < (this.l - 1)) {
                            if (this.maze[i][j][k+1] != -1) {
                                this.digrafo.addArc
                                           (this.maze[i][j][k],
                                            this.maze[i][j][k+1],
                                            1.0);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        Main main = null;
        if (args.length == 2) {
            main = new Main(args[0], args[1]);
            main.leerLaberinto();
        } else {
            throw new ExcepcionFormatoIncorrecto("Error de sintaxis en la " +
                    "llamada del programa.\n\nUSO:\n\n\tjava Main " +
                    "archivo_entrada.input archivo_salida.output\n\n");
        }

        for (int k = 0; k < main.l; k++) {
            for (int i = 0; i < main.r; i++) {
                for (int j = 0; j < main.c; j++) {
                    System.out.print(main.maze[i][j][k] + ", ");
                }
                System.out.print("\n");
            }
            System.out.println("\n");
        }

        System.out.println("El nodo de inicio del laberinto es el nodo: " + main.s);
        System.out.println("El nodo de llegada del laberinto es el nodo: " + main.e);
        System.out.println("El número de nodos es: " + main.numNodes);

        main.llenarDigrafo();
        System.out.println("El digrafo llenado es:\n\n" + main.digrafo.toString());



        /*
        for (int k = 0; k < a.length; k++) {
            System.out.print(a[k]);
        }
        System.out.print("\n");


        DiGraph digrafo = main.llenarDigrafo();
         * 
         */
    }
}
