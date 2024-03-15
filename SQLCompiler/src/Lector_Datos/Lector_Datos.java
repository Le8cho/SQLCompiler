/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Lector_Datos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author utente
 */
public class Lector_Datos {
    
    
    ArrayList <Tabla> baseTablas = new ArrayList<>();

    public Lector_Datos() {
    }

    // OBTIENE LA TABLA BUSCADA POR CÓDIGO
    // Busca la tabla en el array baseTablas con un código que coincida con el proporcionado
    public Tabla buscarTabla (int codTabla) {
        Tabla tablaResultado = new Tabla();
        
        for (Tabla ta : baseTablas) {
            if (ta.getCodTabla() == codTabla) {
                tablaResultado = ta;
            }
        }
        return tablaResultado;
    }
    
    
    // AGREGA REGISTRO (AGREGA VALOR DE ATRIBUTO EN ÍNDICE DETERMINADO)
    // Usa los datos del registro obtenidos en "separarDatos"
    // 1. Manda código de la tabla a la función "buscarTabla" para obtener el OBJETO TABLA correspondiente
    //      1.1. Debido a que el código se encuentra en la posición 0 del array (regitro[0])
    // 2. Guarda el objeto TABLA resultado de esta función en "ta"
    // 3. Recorre todos los OBJETOS ATRIBUTO de la tabla y guarda los datos en sus "listaValores"
    public void agregarRegistro(String[] registro) {
        Tabla ta = this.buscarTabla(Integer.parseInt(registro[0]));
        int indice = 1;
        
        for (Atributo at : ta.getListaAtributos()) {
            at.getListaValores().add(registro[indice]);
            indice++;
        }
    }
    
   
    // FUNCIÓN QUE SEPARARÁ LOS DATOS DEL REGISTRO Y LOS GUARDARÁ EN UN ARRAY STRING
    // Recibe los resultados de la función "obtenerCodigoTabla" (array con código y datos del registro separados)
    // Se usa el código de la tabla para determinar su ubicación en el array "baseTablas"
    // NOTA: Los constrains de tipo y longitud de atributos aparecen en el CATALOG
    // Guarda el registro leído en el array "datosFinal" y lo devuelve a "extraerDatosSM"
    public String[] separarDatos(String[] resultados) {
        
        int ubicacionTabla = Integer.parseInt(resultados[0]) - 1;
        
        // ARRAY QUE CONTENDRÁ LOS DATOS DEL REGISTRO SEPARADOS
        String[] datosFinal = new String[baseTablas.get(ubicacionTabla).getListaAtributos().size() + 1];        
        
        // SE TIENE LA LISTA DE ATRIBUTOS
        ArrayList<Atributo> listaAtributos = baseTablas.get(ubicacionTabla).getListaAtributos();
        
        int indiceDatosFinal = 1;
        int puntoInicial = 0;
        int espacioSeparador = 1;
        String datosLinea = resultados[1];
        
        //Empezamos a subdividir los resultados
        for (Atributo atributo: listaAtributos) {
            //Obtenemos la longitud de cada atributo
            int longitudAtributo = atributo.getLongitud();
            //Este es el limite de la extension del atributo
            int puntoFinal = puntoInicial + longitudAtributo;
            //Puede darse el caso que el ultimo campo no ocupe todo su espacio (en caso de ser varchar por ejemplo)
            if(puntoFinal > datosLinea.length()){
                //entonces el limiteSuperior se actualiza a la longitud de la linea
                puntoFinal = datosLinea.length();
            }
            //El contenido del atributo se extrae usando un punto de partido y un punto final(excluyente)
            String datoAtributo = datosLinea.substring(puntoInicial, puntoFinal);
            //el dato del atributo obtenido se añade a datos Final y se elimina algun espacio residual de haber alguno
            datosFinal[indiceDatosFinal] = datoAtributo.trim();
            //Se actualiza el indice de datos Final
            indiceDatosFinal++;
            //Actualizamos el punto inicial del siguiente dato el cual es el puntoFinal anterior sumandole el espacio de separacion o delimitador
            puntoInicial = puntoFinal + espacioSeparador;
 
        }
        
        datosFinal[0] = resultados[0];
        
        return datosFinal;
    }
    
        
    // FUNCIÓN QUE EXTRAE EL CÓDIGO DE LA TABLA ACTUAL (SE ENCUENTRA EN LA PRIMERA COLUMNA EN LOS REGISTROS)
    // 1. Se crea un array String de tamaño 2 donde se guardarán los resultados
    // 2. Se usa un buffer para guardar los caracteres del código
    // 3. Guardamos el código en resultados[0] y usamos "subtring" para separarlo del resto del registro (resultados[1])
    public String[] obtenerCodigoTabla(String linea) {
        
        String[] resultados = new String[2];    // CON LA FORMA: [(Cód. tabla), (Resto de la línea)]
        
        //los dos primeras posiciones de la linea corresponden al codigo de la tabla
        String codeTable = linea.substring(0,2); 
        resultados[0] = codeTable;
        
        //El resto de la linea con los demas valores
        resultados[1] = linea.substring(3);
        
        // PRIMER ELEMENTO: CÓDIGO DE TABLA, SEGUNDO: RESTO DE LA LÍNEA ACTUAL
        return resultados;
    }
    
  
    // FUNCIÓN QUE EXTRAERÁ LOS DATOS DEL ARCHIVO SM Y AÑADIRÁ LOS REGISTROS A LAS TABLAS
    // 1. Usa BufferedReader para leer líneas una por una (omite las que empiecen con #)
    // 2. Manda la línea actual a la función "obtenerCodigoTabla" para obtener el código de la tabla actual
    //      2.2. Debido a que los registros en SM contienen el código de la tabla a la que pertenecen
    // 3. Esta función devuelve un array de Strings con los resultados y lo pasa como parámetro a "separarDatos"
    // 4. Esta función devuelve un array String con los datos del registro separados
    // 5. Pasa el registro a la función "agregarRegistro" para finalmente ser agregado
    
    public void extraerDatosSM(File archivo) {
        try {
            
            BufferedReader lector = new BufferedReader(new FileReader(archivo));
            
            String linea;
            
            while ((linea = lector.readLine()) != null) {
                
                 //left trim de un espacio (falta por implementar) se encuentra en la libreria del profe
                 //Seria para quitar espacios a la izquierda de cualquier longitud
                 linea= linea.substring(1);
             
                if (!linea.startsWith("#") && !linea.equals("")) {
                    
                    String[] registro = separarDatos(obtenerCodigoTabla(linea));
                    agregarRegistro(registro);  
                }   
            }
            
        } catch (IOException ex) {
            Logger.getLogger("Ocurrió un ERROR");
        }
    }
    
  
    
    
    // AGREGA A LA TABLA LA COLUMNA (ATRIBUTO) CORRESPONDIENTE
    // Usa el objeto ATRIBUTO pasado por la función "crearAtributo"
    // 1. Usa el código de atributo para hallar la tabla correcta
    //      1.1. Esto debido a que el código tiene 4 números, donde los 2 primeros son el código de tabla
    // 2. Busca el objeto TABLA con el mismo código en el ArrayList "buscarTabla"
    // 3. Agrega el objeto ATRIBUTO al ArrayList "listaAtributos" dentro de la clase TABLA
    public void llenarTabla(Atributo at) {
        int codigoAt = at.getCodigo()/100;
        
        for (Tabla ta : baseTablas) {
            if (ta.getCodTabla() == codigoAt) {
                ta.agregarAtributo(at);
            }            
        }    
    }
    
    
    // CREA UN ATRIBUTO (COLUMNA) DE UNA TABLA. EL ARRAYLIST CON LOS VALORES SE LLENARÁ DESPUÉS.
    // Usa los datos de un atributo leídos por la función "extraerDatosCAT"
    // 1. Crea un OBJETO ATRIBUTO y setea sus atributos de clase (Código, Nombre, Tipo, Longitud y Decimales)
    // 2. Pasa el objeto creado a la función "llenarTabla"
    public void crearAtributo(String[] datAtributo) {
        
        int codigo = Integer.parseInt(datAtributo[0]);
        String nombre = datAtributo[1];
        char tipo = datAtributo[2].charAt(0);
        int longitud = Integer.parseInt(datAtributo[3]);
        int decimales = Integer.parseInt(datAtributo[4]);
        
        Atributo objAtributo = new Atributo(codigo, nombre, tipo, longitud, decimales);

        llenarTabla(objAtributo);
    }
    
    
    // RECIBE LOS DATOS PRINCIPALES DE LA TABLA. LA CREA Y AÑADE EN EL ARRAY TABLAS.
    // Usa los datos de cabecera leídos por la función "extraerDatosCAT"
    // 1. Crea un OBJETO TABLA y setea sus atributos de Código y Nombre
    // 2. Añade el objeto en el ArrayList "baseTablas" de esta clase
    public void crearTabla(String[] cabecera) {
        
        Tabla ta = new Tabla();
        
        //El primer elemento del array cabecera corresponde al codigo de la tabla
        String codeTable = cabecera[0];
        //El segundo elemento de la cabecera correspondera al nombre de la tabla
        String nameTable = cabecera[1];

        ta.setCodTabla(Integer.parseInt(codeTable));
        ta.setNombreTabla(nameTable);
        
        this.baseTablas.add(ta);
    }
   
    
    // FUNCIÓN QUE EXTRAERÁ LOS DATOS DEL CATALOG Y CREARÁ LAS TABLAS
    // 1. Usa la clase BufferedReader para leer las líneas una por una (bucle while principal)
    // 2. Se usa un estado para controlar el comportamiento del programa
    //      2.1. Si el estado es -1 (recién empieza), cambiará a 0 cuando detecte # y empezará la lectura
    //      2.2. Si el estado es 0 (detectó #),  leerá los datos de cabecera (Código y Nombre)
    //      2.3. Si el estado es 1 (se guardaron datos cabecera), leerá los datos de los atributos
    // 3. Termina cuando detecta dos caracteres "#" consecutivos o se llega al final del archivo    
    public void extraerDatosCAT(File archivo) {
        try {
            // MÉTODO READLINE DE BUFFEREDREADER DEVUELVE STRING
            BufferedReader lector = new BufferedReader(new FileReader(archivo));
            
            String linea;
            int estado = -1;         // 0 SI DETECTÓ "#", 1 SI DETECTÓ NOMBRE DE TABLA
            int contAtributo = -1;   // SE EMPIEZA CON -1 PARA NO IMPRIMIRLO SI RECIÉN EMPIEZA LA LECTURA
            int indice = 0;          // ÍNDICE DEL ARRAY ATRIBUTOS (LISTA DE CARACTERÍSTICAS)
            
            // COMIENZA A LEER TODAS LAS LÍNEAS DEL ARCHIVO
            while ((linea = lector.readLine()) != null) {
                
                // QUITAR ESPACIOS ANTES Y DESPUÉS DE LAS LÍNEAS
                linea = linea.trim();
                
                if (linea.startsWith("#")) {
                    
                    // SI YA HA EMPEZADO EL PROGRAMA (0 O 1) Y DETECTA 2 CARACTERES "#" CONSECUTIVOS
                    if(estado == 0) {
                        break;
                    }
                  
                    contAtributo = 0;   // REINICIA CONTADOR DE ATRIBUTOS PARA UNA NUEVA TABLA
                    estado = 0;         // ESTADO DE RECIBIR DATOS DE CABECERA
                    
                } else {
                    
                    if (estado == 0) {
                        //el codigo de la tabla son 2 chars
                        String codeTable = linea.substring(0,2);
                        //sumamos +2 chars vacios + 1 char delimitador
                        //el nombre de la tabla empieza a partir del indice 5 y son 10 chars quitamos espacios en blanco si hubieran
                        String nameTable = linea.substring(5,15).trim();
                        String[] cabecera = {codeTable, nameTable}; 
                        
                        this.crearTabla(cabecera);
                        
                        estado = 1;     // ESTADO DE RECIBIR REGISTROS
                        
                    } else {
                        //Quitaremos los espacios en blanco si se requiere
                        contAtributo++; 
                        //El codigo del atributo empieza en el indice 0 y tiene longitud 4 chars
                        String codeAttribute = linea.substring(0,4).trim();
                        //Sumamos +1 char delimitador y el nombre del atributo es de 10 chars;
                        String nameAttribute = linea.substring(5,15).trim();
                        //sumamos +1 char delimitador y el tipo es de 1 solo char
                        String typeAttribute = linea.substring(16,17).trim();
                        //sumamos +1 char delimitador y la longitud del atributo se expresa en 2 chars
                        String lengthAttribute = linea.substring(18,20).trim();
                        //sumamos +1 char delimitador y los decimales del atributo se expresa en 2 chars
                        String decAttribute = linea.substring(21,23).trim();

                        String[] atributo = {codeAttribute, nameAttribute, typeAttribute, lengthAttribute, decAttribute};
                        
                        this.crearAtributo(atributo);                                        
                    }
                }           
            }
            lector.close();
            
            System.out.println("Las tablas han sido creadas exitosamente");
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al leer el archivo (definirRegistro)");
        }     
    }

    
    // FUNCIÓN QUE VERIFICARÁ SI LOS ARCHIVOS SON .DAT Y LLAMARÁ A FUNCIONES DE LECTURA
    // 1. Se asegura que el nombre de los archivos termine con en ".DAT"
    // 2. Llama a función "extraerDatosDAT" para crear las tablas
    // 3. Llama a función "extraerDatosSM" para llenar las tablas con registros
    // 4. Crea un JFrame de la clase Menu_Tablas para mostrar las tablas creadas
    public void verificarDAT(File catalog, File datos) {

        if (catalog.getName().endsWith(".DAT") && catalog.getName().endsWith(".DAT")) {
            
            System.out.println("El archivo es un DAT");
            extraerDatosCAT(catalog);
            extraerDatosSM(datos);
            
            Menu_Tablas ventana = new Menu_Tablas(baseTablas);
            ventana.setVisible(true);
            ventana.setLocationRelativeTo(null);
                      
        } else {
            
            JOptionPane.showMessageDialog(null, "El archivo no es un DAT. Vuelva a intentarlo");
            iniciarGUI();
            
            }    
    }
    
  
    //DEBUG
    public void mostrarTexto(File archivo) {
      
        if (archivo.getName().endsWith(".DAT")) {
            System.out.println("El archivo es un DAT");
            System.out.println("");
            try {
                    // BUFFERED READER Y FILEREADER PARA LEER EL ARCHIVO (LÍNEA POR LÍNEA)
                    // STRINGBUILDER CON EL FIN DE DARLE EL FORMATO NECESARIO PARA MOSTRARLO EN CONSOLA
                    BufferedReader lector = new BufferedReader(new FileReader(archivo));
                    StringBuilder constructor = new StringBuilder();
                    
                    String linea;
                    while ((linea = lector.readLine()) != null) {
                        constructor.append(linea).append("\n");
                    }
                    lector.close();
                    System.out.println("Contenido del archivo:\n" + constructor.toString());
                    
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Error al leer el archivo");
                }
            
        } else {
            JOptionPane.showMessageDialog(null, "El archivo no es un DAT. Vuelva a intentarlo");
            iniciarGUI();
        }      
    }
   
    
    public static void iniciarGUI() {
        GUI principal = new GUI();
        principal.setVisible(true);
        principal.setLocationRelativeTo(null);    
    }
    
    
    public static void main(String[] args) 
    {
        iniciarGUI();
    }
    
}
