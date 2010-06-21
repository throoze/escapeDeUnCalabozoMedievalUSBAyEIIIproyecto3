import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

/**
 * 
 * @author Victor De Ponte, 05-38087
 * @author Karina Valera, 06-40414
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
    int             start;      // Nodo de partida (Start)
    int             end;        // Nodo de llegada (End)
    int             numNodes;   // Número de nodos a introducir en el DiGraph.

    DiGraph         digrafo;    // Digrafo donde se representará el laberinto.


    /**
     * Construye un nuevo {@code Main} vacío.
     * <b>Pre</b>: true;
     * <b>Post</b>: Se creó un nuevo {@code Main} vacío
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
        this.start = -1;
        this.end = -1;
    }

    /**
     * Construye un nuevo {@code Main}, inicializando los campos en los valores
     * obtenidos de leer la primera línea de un archivo de entrada, que contiene
     * la representación de un laerinto según el formato establecido en clase.
     * <b>Pre</b>: true;
     * <b>Post</b>: Se construye un nuevo {@code Main}, inicializado con los
     * valores obtenidos de la primera línea de {@code inFile}.
     * @param inFile Nombre del archivo de entrada
     * @param outFile Nombre del archivo de salida
     * @throws IOException En caso de encontrar un error en el formato del
     * archivo de entrada
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
     * Se encarga de llenar el campo {@code this.maze}. Esta matriz de tres
     * dimensiones representará el laberinto sobre el cuál se va a trabajar, de
     * la siguiente manera: Se recorrerá el archivo y se irán almacenando en
     * {@code this.maze}, desde {@code this.maze[0][0][0]} hasta
     * {@code this.maze[this.r-1][this.c-1][this.l-1]} cada espacio en el
     * laberínto, de modo de que {@code this.maze[i][j][k] == -1} si se
     * representa un bloque, y {@code this.maze[i][j][k] &gt -1} si se
     * representa un camino libre. Cada camino libre se enumerará desde el cero
     * en adelante, y estos números representarán los futuros nodos de nuestro
     * {@link DiGraph}. Además, este método almacena el número total de nodos,
     * el nodo en el que se empezará en el laberinto y el nodo de llegada.
     * <b>Pre</b>: {@code this} debe haber sido inicializado con el constructor
     * no vacío, de manera de que haya un buffer de lectura abierto al momento
     * de correr éste método.
     * <b>Post</b>: Se habrá llenado {@code this.maze} según lo descrito
     * anteriormente, y se habrán salvado el número total de nodos, el nodo de
     * partida y el nodo de llegada.
     */
    public void readMaze() throws IOException{
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
                                    this.start = nNodes;
                                } else if (tokens[j].equals("E")) {
                                    this.end = nNodes;
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
     * Se encarga de construir un nuevo {@code DiGraph} para this (el campo
     * {@code this.digrafo}), basado en la representación obtenida con el método
     * {@code this.readMaze}. Se crearán arcos en ambos sentidos para todos los
     * nodos en los que se deben hacer arcos, y todos con costo 1 (Sólo por
     * abstracción).
     * <b>Pre</b>;Se debe haber antes aplicado el método {@code this.readMaze},
     * de manera de que exista una matriz {@code this.maze} llena con la
     * representación obtenida de leer el archivo {@code this.inputFile}.
     * <b>Post</b>: Se llenará el digráfo conforme a la representación obtenida
     * de leer el archivo {@code this.inputFile} a travez del método
     * {@code this.readMaze}.
     */
    private void newDiGraph() {
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
     *
     * @return
     */
    public String bfs() {
        Queue<Integer> nodos = new Cola();
        Queue<String> caminos = new Cola();
        boolean[] visitado = new boolean[this.numNodes];
        String camino = "";

        for (int i = 0; i < visitado.length; i++) {
            visitado[i] = false;
        }

        visitado[this.start] = true;

        camino += "" + this.start;

        nodos.add(new Integer(this.start));
        caminos.add(camino);

        while (!nodos.isEmpty()) {
            
            int v = nodos.poll().intValue();
            camino = caminos.poll();

            List<Integer> sucesores = this.digrafo.getSucesors(v);

            Object[] suces = sucesores.toArray();
            int[] suc = new int[suces.length];
            for (int i = 0; i < suces.length; i++) {
                suc[i] = ((Integer)suces[i]).intValue();
            }

            for (int i = 0; i < suc.length; i++) {
                if (!visitado[suc[i]]) {
                    if (suc[i] != this.end) {
                        visitado[suc[i]] = true;
                        nodos.add(suc[i]);
                        caminos.add((camino + ","+ suc[i]));
                    } else {
                        camino += "," + suc[i];
                        return camino;
                    }
                }
            }
        }
        return "";
    }

    public void write (String camino) {
        if (camino.equals("")) {
            this.out.println("Atrapado!");
        } else {
            String[] nodos = camino.split(",");
            this.out.println("Escape en " + (nodos.length - 1) + " minuto(s).");
        }
    }

    /**
     * @param args argumentos introducidos por linea de comandos. Contiene el
     * nombre del archivo de entrada y del archivo de salida
     */
    public static void main(String[] args) throws IOException {

        Main main = null;
        if (args.length == 2) {
            main = new Main(args[0], args[1]);
            main.readMaze();
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

        System.out.println("El nodo de inicio del laberinto es el nodo: " + main.start);
        System.out.println("El nodo de llegada del laberinto es el nodo: " + main.end);
        System.out.println("El número de nodos es: " + main.numNodes);

        main.newDiGraph();
        System.out.println("El digrafo llenado es:\n\n" + main.digrafo.toString());

        String camino = main.bfs();

        System.out.println("El camino es: "+ camino);

        String[] nodos = camino.split(",");

        System.out.println("Escape en " + (nodos.length-1) + " minuto(s).");



        


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