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

    private String  inputFile;      // Nombre del archivo de entrada.

    private String  outputFile;     // Nombre del archivo de salida.

    boolean[][][]   maze;           // Representacion del laberinto como matriz
                                    // booleana.

    int [][][]      nodes;          // Representacion de los nodos relacionándo-
                                    // los con los índices de this.maze

    BufferedReader  in;             // Buffer de entrada (Lectura)
    PrintStream     out;            // Flujo de salida (Escritura)

    int             l;              // No de niveles
    int             r;              // No de filas
    int             c;              // No de columnas
    int             s;              // Nodo de partida (Start)
    int             e;              // Nodo de llegada (End)


    /**
     *
     */
    public Main() {
        this.inputFile = "";
        this.outputFile = "";
        this.maze = new boolean[0][0][0];
        this.nodes = new int[0][0][0];
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
     * @param name
     * @throws IOException
     */
    public Main(String name) throws IOException {
        this.inputFile = name;

        // Se crea un objeto de tipo archivo para hacer el código más legible
        File file =  new File(this.inputFile);

        // Se verifica que el archivo este en condiciones de ser procesado
        if (file.exists() && file.isFile() && file.canRead())  {

            /* Si el archivo no es del formato nombreArchivo.input, lanza la
             * excepcion. Para verificar esto, analizamos la cadena de entrada:
             */
            if (this.inputFile.substring(this.inputFile.length() - 5).
                    equals("input"))
            {
                this.outputFile = this.inputFile.substring
                                    (0, this.inputFile.length() - 5) + "output";
            } else {
                throw new ExcepcionFormatoIncorrecto("Problema de formato en el"
                        + " nombre del archivo:\nSe esperaba un archivo con la "
                        + "extensión \".input\" y se encontró:\n\n\t\"" +
                        this.inputFile + "\"\n\n");
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
            this.maze = new boolean[this.l][this.r][this.c];
            this.nodes = new int[this.l][this.r][this.c];
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
     * @param i
     * @param j
     * @param k
     * @return
     */
    public int rclToNode(int i, int j, int k) {
        return ((k*this.r*this.c) + (j*this.r) + (i));
    }

    //ESTO NO ESTA BIEN!!!! ACOMODAR Y PENSAR MEJOR LAS COSAS
    /**
     *
     */
    public void procesarArchivo(){
        int counter = 0;
        for (int k = 0; k < this.l; k++) {
            for (int j = 0; j < this.c; j++) {
                for (int i = 0; i < this.r; i++) {
                    this.maze[i][j][k] = false;
                    this.nodes[i][j][k] = counter;
                    counter++;
                }
            }
        }
    }

    /**
     *
     * @return
     */
    private DiGraph llenarDigrafo() {
        return null;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Main main = null;
        if (1 < args.length) {
            main = new Main(args[0]);
        } else {
            throw new ExcepcionFormatoIncorrecto("Error de sintaxis en la " +
                    "llamada del programa.\n\nUSO:\n\n\tjava Main " +
                    "archivo_entrada.input\n\n");
        }


        DiGraph digrafo = main.llenarDigrafo();
    }
}
