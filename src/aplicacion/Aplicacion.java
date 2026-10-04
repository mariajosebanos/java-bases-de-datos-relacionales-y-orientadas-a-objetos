package aplicacion;

// jdbc:h2:./proyectobase.h2db
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import org.h2.tools.Server;
import static java.lang.System.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.stream.Collectors;
import utilidades.ES;

/**
 * Programa principal que usa el acceso a base de datos configurada en H2
 *
 * @author Baños Saldaña, María José
 * @version abril/2026
 */
public class Aplicacion {

    /**
     * Nombre del archivo de base de datos local.
     */
    private static final String DB_NOMBRE = "proyectobase.h2db";
    /**
     * URL para la conexión a la base de datos.
     */
    private static final String URL_CONEXION = "jdbc:h2:./" + DB_NOMBRE;
    /**
     * Driver a utilizar para conectarse a la base de datos.
     */
    private static final String DRIVER = "org.h2.Driver";
    /**
     * Opciones de conexión.
     */
    private static final String PARAMS = ";MODE=MySQL;AUTO_RECONNECT=TRUE";

    /**
     * Path al archivo que contiene la estructura de la base de datos.
     */
    public final static String ESTRUCTURA_DB = "/resources/creaBD.sql";

    /**
     * Path al archivo que contiene la estructura de la base de datos.
     */
    public final static String INSERTA_DB = "/resources/cargaBD.sql";

    /**
     * Método principal de la aplicación. En él se realiza la preparación del
     * entorno antes de empezar. A destacar:
     *
     * - Se carga el driver (Class.forName). - Se establece una conexión con la
     * base de datos (DriverManager.getConnection) - Se crean las tablas, si no
     * están creadas, invocando el método createTables. - Se ejecuta una
     * consulta de prueba
     *
     * @param args
     */
    public static void main(String[] args) {
        boolean driverCargado = false;

        //Carga del driver de la base de datos.
        try {
            Class.forName(DRIVER).getDeclaredConstructor().newInstance();
            driverCargado = true;
        } catch (ClassNotFoundException e) {
            err.printf("No se encuentra el driver de la base de datos (%s)\n", DRIVER);
        } catch (InstantiationException | IllegalAccessException ex) {
            err.printf("No se ha podido iniciar el driver de la base de datos (%s)\n", DRIVER);
        } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            java.util.logging.Logger.getLogger(Aplicacion.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Si el driver está cargado, aseguramos que podremos conectar.
        if (driverCargado) {
            //Conectamos con la base de datos.
            //El try-with-resources asegura que se cerrará la conexión al salir.
            String[] wsArgs = {"-baseDir", System.getProperty("user.dir"), "-browser"};
            try ( Connection con = DriverManager.getConnection(URL_CONEXION + PARAMS, "", "")) {

                // Iniciamos el servidor web interno (consola H2 para depuraciones)
                Server sr = Server.createWebServer(wsArgs);
                sr.start();

                // Presentamos información inicial por consola
                out.println("¡¡Atención!!");
                out.println();
                out.println("Mientras tu aplicación se esté ejecutando \n"
                        + "puedes acceder a la consola de la base de datos \n"
                        + "a través del navegador web.");
                out.println();
                out.println("Página local: " + sr.getURL());
                out.println();
                out.println("Datos de acceso");
                out.println("---------------");
                out.println("Controlador: " + DRIVER);
                out.println("URL JDBC: " + URL_CONEXION);
                out.println("Usuario: (no indicar nada)");
                out.println("Password: (no indicar nada)");

                // Creamos las tablas y algunos datos de prueba si no existen
                // y continuamos
                if (crearTablas(con)) {

                    // Insertar los datos en las tablas de la BD
                    insertarDatosTablas(con);

                    boolean continuar = true;

                    do {
                        System.out.println("\n\n");
                        System.out.println("\n --------------------------------------------------------------------------------");
                        System.out.println("| MENÚ DE LA APLICACIÓN                                                          |");
                        System.out.println(" --------------------------------------------------------------------------------");
                        System.out.println("  1 - Mostrar picos más altos del mundo");
                        System.out.println("  2 - Borrar alpinista");
                        System.out.println("  3 - Modificar nacionalidad alpinista");
                        System.out.println("  4 - Mostrar ascensiones de cada pico");
                        System.out.println("  0 - Salir");
                        System.out.println(" --------------------------------------------------------------------------------\n\n");

                        // Leer la opción correspondiente a ejecutar.
                        int opcion = ES.leeEntero("Escriba opción: ", 0, 4);
                        switch (opcion) {
                            case 0:
                                continuar = false;
                                break;
                            case 1:
                                mostrarPicos(con);
                                break;
                            case 2:
                                borrarAlpinista(con);
                                break;
                            case 3:
                                modificarNacionalidadAlpinista(con);
                                break;
                            case 4:                                
                                mostrarAscensionesPicos(con);
                                break;
                        }
                    } while (continuar);

                    // Esperar tecla
                    ES.leeCadena("Antes de terminar, puedes acceder a la "
                            + "consola de H2 para ver y modificar la base de "
                            + "datos. Pulsa cualquier tecla para salir.");

                } else {
                    System.out.printf("Problema creando las tablas: ");
                }

                sr.stop();
                sr.shutdown();

            } catch (SQLException ex) {
                System.out.printf("No se pudo conectar a la base de datos (%s): %s\n", DB_NOMBRE, ex.getMessage());
            }
        }

    }

    /**
     * Dada una conexión válida, lleva a cabo la creación de la estructura de la
     * base de datos, usando como SQL para la creación el contenido en la
     * constante ESTRUCTURA_DB
     *
     * @param con conexión a la base de datos.
     * @see ESTRUCTURA_DB
     * @return true si se creo la estructura y false en caso contrario.
     */
    public static boolean crearTablas(Connection con) {
        boolean todoBien = false;

        try ( Statement st = con.createStatement()) {

            String sqlScript = loadResourceAsString(ESTRUCTURA_DB);
            if (sqlScript != null) {
                st.execute(sqlScript);
                todoBien = true;
            } else {
                System.out.printf("Problema cargando el archivo: %s \n", ESTRUCTURA_DB);
                System.out.printf("Para ejecutar este proyecto no puede usarse 'Run File'\n");
            }

        } catch (SQLException ex) {
            System.out.printf("Problema creando la estructura de la base de datos: %s\n", ex.getMessage());
        }
        return todoBien;
    }

    /**
     * Dada una conexión válida, lleva a cabo la inserción de datos de la base
     * de datos, usando como SQL para la creación el contenido en la constante
     * INSERTA_DB
     *
     * @param con conexión a la base de datos.
     * @see INSERTA_DB
     * @return true si se creo la estructura y false en caso contrario.
     */
    private static boolean insertarDatosTablas(Connection con) {
        boolean todoBien = false;

        try ( Statement st = con.createStatement()) {

            String sqlScript = loadResourceAsString(INSERTA_DB);
            if (sqlScript != null) {
                st.execute(sqlScript);
                todoBien = true;
            } else {
                System.out.printf("Problema cargando el archivo: %s \n", INSERTA_DB);
                System.out.printf("Para ejecutar este proyecto no puede usarse 'Run File'\n");
            }

        } catch (SQLException ex) {
            System.out.printf("Problema insertando datos en la base de datos: %s\n", ex.getMessage());
        }
        return todoBien;
    }

    /**
     * Carga un recurso que estará dentro del JAR como cadena de texto.
     *
     * @param resourceName Nombre del recurso dentro del JAR.
     * @return Cadena que contiene el contenido del archivo.
     */
    public static String loadResourceAsString(String resourceName) {
        String resource = null;
        InputStream is = Aplicacion.class.getResourceAsStream(resourceName);
        if (is != null) {
            try ( InputStreamReader isr = new InputStreamReader(is);  BufferedReader br = new BufferedReader(isr);) {
                resource = br.lines().collect(Collectors.joining("\n"));
            } catch (IOException ex) {
                System.out.printf("Problema leyendo el recurso como cadena: %S\n ", resourceName);
            }
        }
        return resource;
    }

    /**
     * Muestra por consola la información de los picos de la BD.
     *
     * @param con Conexión a la BD
     */
    private static void mostrarPicos(Connection con) {
        // Ejecutamos la consulta sobre la tabla picos y mostramos su contenido.

        // Si la consulta no está vacía
        if (con != null) {

            // Ejecutamos la consulta
            try (Statement consulta = con.createStatement()) {
                ResultSet resultados = consulta.executeQuery(
                        "SELECT idPico, nombre, cordillera, localizacion, altitud FROM PICOS");

                System.out.println("\n --------------- Listado de Picos más altos del mundo ----------------\n");
                System.out.printf("%-5s %-15s %-15s %-20s %-10s\n",
                        "Id", "Nombre", "Cordillera", "Localización", "Altitud (m)");
                System.out.println(" ---------------------------------------------------------------------");

                // Mientras haya registros, los mostramos
                while (resultados.next()) {
                    int idPico = resultados.getInt("idPico");
                    String nombre = resultados.getString("nombre");
                    String cordillera = resultados.getString("cordillera");
                    String localizacion = resultados.getString("localizacion");
                    int altitud = resultados.getInt("altitud");

                    System.out.printf("%-5d %-15s %-15s %-20s %-10d\n",
                            idPico, nombre, cordillera, localizacion, altitud);

                }

                // Si se produce algún error al ejecutar la consulta, lanzamos una excepción    
            } catch (SQLException ex) {
                System.err.printf("Se ha producido un error al ejecutar la consulta SQL: %s\n", ex.getMessage());
            }
        }

    }

    /**
     * Modificar la nacionalidad de un alpinista de la BD
     *
     * @param con Conexión a la BD
     */
    private static void modificarNacionalidadAlpinista(Connection con) {

        if (con != null) {
            boolean consultaCorrecta = false;

            // Consultamos y mostramos los distintos alplinistas insertados en la bd.
            try (Statement consulta = con.createStatement()) {
                ResultSet resultados = consulta.executeQuery(
                        "SELECT idAlpinista, nombre, apellidos, nacionalidad FROM ALPINISTAS");

                System.out.println("\n ------------- Listado de alpinistas ---------------");
                System.out.printf("%-5s %-15s %-15s %-15s\n", "Id", "Nombre", "Apellidos", "Nacionalidad");
                System.out.println(" ---------------------------------------------------");

                while (resultados.next()) {
                    int idAlpinista = resultados.getInt("idAlpinista");
                    String nombre = resultados.getString("nombre");
                    String apellidos = resultados.getString("apellidos");
                    String nacionalidad = resultados.getString("nacionalidad");
                    System.out.printf("%-5d %-15s %-15s %-15s\n", idAlpinista, nombre, apellidos, nacionalidad);
                }
                consultaCorrecta = true;

            } catch (SQLException ex) {
                System.err.printf("Se ha producido un error al consultar los alpinistas: %s\n", ex.getMessage());
            }

            // Si la consulta fue correcta
            if (consultaCorrecta) {
                // Petición del identificador y de la nueva nacionalidad del alpinista a modificar.
                int idModificar = ES.leeEntero("\nEscriba el identificador del alpinista a modificar: ");
                String nuevaNacionalidad = ES.leeCadena("Introduzca la nueva nacionalidad del alpinista: ");

                // Ejecutamos la sentencia SQL de actualización.
                String sqlUpdate = "UPDATE ALPINISTAS SET nacionalidad = ? WHERE idAlpinista = ?";
                try (PreparedStatement actualizar = con.prepareStatement(sqlUpdate)) {
                    actualizar.setString(1, nuevaNacionalidad);
                    actualizar.setInt(2, idModificar);
                    int filasAfectadas = actualizar.executeUpdate();

                    if (filasAfectadas > 0) {
                        System.out.printf("\nModificado correctamente el alpinista con identificador %d.\n", idModificar);
                    } else {
                        System.out.println("\nNo se realizó ninguna modificación en los datos de los alpinistas.");
                    }

                } catch (SQLException ex) {
                    System.err.printf("Se ha producido un error al modificar la nacionalidad: %s\n", ex.getMessage());
                }

            }

        }

    }

    /**
     * Borrar el registro de un alpinista de la BD
     *
     * @param con Conexión a la BD
     */
    private static void borrarAlpinista(Connection con) {

        if (con != null) {
            boolean consultaCorrecta = false;

            // Consultamos y mostramos los distintos alpinistas insertados en la bd.
            try (Statement consulta = con.createStatement()) {
                ResultSet resultados = consulta.executeQuery(
                        "SELECT idAlpinista, nombre, apellidos FROM ALPINISTAS");
                System.out.println("\n ------ Listado de alpinistas -------");
                System.out.printf("%-5s %-15s %-15s\n", "Id", "Nombre", "Apellidos");
                System.out.println(" ------------------------------------");

                while (resultados.next()) {
                    int idAlpinista = resultados.getInt("idAlpinista");
                    String nombre = resultados.getString("nombre");
                    String apellidos = resultados.getString("apellidos");
                    System.out.printf("%-5d %-15s %-15s\n", idAlpinista, nombre, apellidos);
                }
                consultaCorrecta = true;

            } catch (SQLException ex) {
                System.err.printf("Se ha producido un error al consultar los alpinistas: %s\n", ex.getMessage());
            }

            // Si la consulta fue correcta
            if (consultaCorrecta) {

                // Petición del identificador del alpinista a eliminar.
                int idBorrar = ES.leeEntero("\nEscriba el identificador del alpinista a borrar: ");

                // Ejecutamos la sentencia SQL de borrado.
                String sqlBorrado = "DELETE FROM ALPINISTAS WHERE idAlpinista =?";
                try (PreparedStatement borrado = con.prepareStatement(sqlBorrado)) {
                    borrado.setInt(1, idBorrar);
                    int filasAfectadas = borrado.executeUpdate();

                    if (filasAfectadas > 0) {
                        System.out.printf("Borrado correctamente el alpinista con identificador %d.\n", idBorrar);
                    } else {
                        System.out.println("No se realizó ninguna modificación en la base de datos.");
                    }

                } catch (SQLException ex) {
                    System.err.printf("Se ha producido un error al borrar el alpinista: %s\n", ex.getMessage());
                }

            }

        }

    }

    /**
     * Muestra el nombre de todos los picos existentes junto a los alpinistas que los han ascendido.
     *
     * @param con Conexión a la BD
     */
    private static void mostrarAscensionesPicos(Connection con) {

        if (con != null) {
            System.out.println("\n --------------------------------------------------------------------------");
            System.out.println("| ASCENSIONES A PICOS                                                      |");
            System.out.println(" --------------------------------------------------------------------------");

            // Consultamos todos los picos existentes en la bd.
            try (Statement consultaPicos = con.createStatement()) {
                ResultSet picos = consultaPicos.executeQuery(
                        "SELECT idPico, nombre FROM PICOS");

                // Almacenamos el identificador y nombre para cada pico devuelto en la consulta.
                while (picos.next()) {
                    int idPico = picos.getInt("idPico");
                    String nombrePico = picos.getString("nombre");

                    System.out.printf("\n\n----------------------- %s -----------------------\n\n", nombrePico);
                    System.out.printf("%-22s %-12s %-15s\n", "Nombre alpinista", "Fecha", "Ruta Ascenso");
                    System.out.println(" ------------------------------------------------------");

                    // Consultamos las  ascensiones a cada pico.
                    String sqlAscensiones = "SELECT idAlpinista, fechaAscension, ruta FROM ASCENSIONES WHERE idPico = ?";
                    try (PreparedStatement consultaAscensiones = con.prepareStatement(sqlAscensiones)) {
                        consultaAscensiones.setInt(1, idPico);
                        ResultSet ascensiones = consultaAscensiones.executeQuery();

                        while (ascensiones.next()) {
                            int idAlpinista = ascensiones.getInt("idAlpinista");
                            String fecha = ascensiones.getDate("fechaAscension").toString();
                            String ruta = ascensiones.getString("ruta");

                            // Consultamos los datos para cada alpinista involucrado en la ascensión al pico.
                            String sqlAlpinista = "SELECT nombre, apellidos FROM ALPINISTAS WHERE idAlpinista = ?";
                            try (PreparedStatement consultaAlpinista = con.prepareStatement(sqlAlpinista)) {
                                consultaAlpinista.setInt(1, idAlpinista);
                                ResultSet alpinista = consultaAlpinista.executeQuery();

                                if (alpinista.next()) {
                                    String nombre = alpinista.getString("nombre");
                                    String apellidos = alpinista.getString("apellidos");
                                    System.out.printf("%-10s %-11s %-12s %-15s\n",
                                            nombre, apellidos, fecha, ruta);
                                }
                            }

                        }
                    }

                }

            } catch (SQLException ex) {
                System.err.printf("Se ha producido un error al consultar las ascensiones: %s\n", ex.getMessage());
            }

        }

    }
}
