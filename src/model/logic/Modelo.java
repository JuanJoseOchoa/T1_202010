package model.logic;

import model.data_structures.Multa;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import model.data_structures.ListaEnlazada;

/**
 * Definicion del modelo del mundo
 *
 */
public class Modelo {
	/**
	 * Atributos del modelo del mundo
	 */
	private ListaEnlazada datos;
	private final static String comparendos_small_GEOJSON_FILE = "./data/comparendos_dei_2018_small.geojson"; // Processing JSONObject
	
	private String rutaArchivo;
	private boolean inicioArrayComparendos;  // identificacion Inicio Array Comparendos
	private boolean leyendoPropiedades;      // inicio propiedad properties de un comparendo
	private boolean leyendoGeometria;        // inicio propiedad geometry de un comparendo
	private boolean crearObjComparendo;      // terminacion de lectura de un objeto Comparendo JSON
	
	private String propiedad;
	private boolean identificarObjectId;	// identificacion propiedad OBJECTID
	private int objectId;					// valor de OBJECTID (NUMBER)
	private boolean identificarLocalidad;   // identificacion propiedad LOCALIDAD
	private String localidad;				// valor de LOCALIDAD (STRING)
	
	private boolean identificarLongitud;    // identificacion propiedad coordinates
	private boolean identificarLatitud;     // identificacion propiedad coordinates
	private double longitud;				// valor de longitud geografica
	private double latitud;					// valor de latitud geografica

	/**
	 * Constructor del modelo del mundo con capacidad predefinida
	 */
	public Modelo(String pRuta)
	{
		datos = new ListaEnlazada( );
		rutaArchivo = pRuta;
		inicioArrayComparendos = false;
		leyendoPropiedades = false;
		leyendoGeometria = false;
		crearObjComparendo = false;
		
		propiedad = "";
		identificarObjectId = false;
		objectId = -1;
		identificarLocalidad = false;
		localidad = "";
		identificarLongitud = false;
		identificarLatitud = false;		
		longitud = 0.0;
		latitud = 0.0;
	}
	
	/**
	 * Servicio de consulta de numero de elementos presentes en el modelo 
	 * @return numero de elementos presentes en el modelo
	 */
	public int darTamano()
	{
		return datos.darLength();
	}

	/**
	 * Requerimiento de agregar dato
	 * @param dato
	 */
	public void agregar(Multa dato)
	{	
		datos.agregarNodo(dato);
	}
	
	/**
	 * Requerimiento buscar dato
	 * @param dato Dato a buscar
	 * @return dato encontrado
	 */
	public String buscar(int id)
	{
		Multa m = (Multa) datos.buscarMulta(id);
		
		if(m != null){
			
			return "Identificacion: " + m.darId() + "\n Fecha y Hora: " + m.darDyH() +  "\n Codigo de la Infraccion: " + m.darCI() + "\n Descripcion de la Infraccion: " + m.darDI() + "\n Clase Vehiculo: " + m.darTV() + "\n Tipo de Servicio: " + m.darTS() + "\n Localidad: " + m.darLoc(); 
		}
		else{
			return "El comparendo buscado no se encuentra en la base de datos";
		}
	}
	/**
	 * Handle an Object. Consume the first token which is BEGIN_OBJECT. Within
	 * the Object there could be array or non array tokens. We write handler
	 * methods for both. Noe the peek() method. It is used to find out the type
	 * of the next token without actually consuming it.
	 *
	 * @param reader
	 * @throws IOException
	 */
	private void handleObject(JsonReader reader) throws IOException
	{
		System.out.println("BEGIN_OBJECT");
		reader.beginObject();
		while (reader.hasNext()) {
			JsonToken token = reader.peek();
			if (token.equals(JsonToken.BEGIN_ARRAY))
			{
				handleArray(reader);
			}
			else if (token.equals(JsonToken.BEGIN_OBJECT)) {
				handleObject(reader);

				// adicional
				System.out.println("END_OBJECT");
				reader.endObject();
				if ( crearObjComparendo )
				{
					crearComparendo();
				}
			}
			/*
			else if (token.equals(JsonToken.END_OBJECT)) {
				System.out.println("END_OBJECT");
				reader.endObject();
				return;
			}
			*/
			else
			{
				handleNonArrayToken(reader, token);
			}
		}
	}
	 
	/**
	 * Handle a json array. The first token would be JsonToken.BEGIN_ARRAY.
	 * Arrays may contain objects or primitives.
	 *
	 * @param reader
	 * @throws IOException
	 */
	public void handleArray(JsonReader reader) throws IOException
	{
		boolean finish = false;
		System.out.println("BEGIN_ARRAY");
		reader.beginArray();
		while (!finish) {
			JsonToken token = reader.peek();
			if (token.equals(JsonToken.END_ARRAY)) {
				System.out.println("END_ARRAY");
				reader.endArray();
				finish = true;
			} else if (token.equals(JsonToken.BEGIN_OBJECT)) {
				handleObject(reader);
			} else if (token.equals(JsonToken.END_OBJECT)) {
				System.out.println("END_OBJECT");
				reader.endObject();
				if ( crearObjComparendo )
				{
					crearComparendo();
				}

			} else
				handleNonArrayToken(reader, token);
		}
	}

	/**
	 * Handle non array non object tokens
	 *
	 * @param reader
	 * @param token
	 * @throws IOException
	 */
	public void handleNonArrayToken(JsonReader reader, JsonToken token) throws IOException
	{
		if (token.equals(JsonToken.NAME))
		{
			propiedad = reader.nextName();
			System.out.println("NAME=" + propiedad);	
			if (propiedad.equalsIgnoreCase("features"))
			{  // Identificacion del JSON Array de comparendos
				inicioArrayComparendos = true;
				System.out.println("OK inicioArrayComparendos");
			}
			if (inicioArrayComparendos)
			{
				if ( propiedad.equalsIgnoreCase("properties") )
				{  // Se comienza a identificar las propiedades de un comparendo
					leyendoPropiedades = true;
					System.out.println("OK inicioPropiedades");
				}
				else if ( propiedad.equalsIgnoreCase("geometry") )
				{  // Se comienza a identificar la geometria de un comparendo
					leyendoGeometria= true;
					System.out.println("OK inicioGeometria");
				}	            

				if ( leyendoPropiedades )
				{
					if ( propiedad.equalsIgnoreCase("OBJECTID"))
					{  // Se identifica la propiedad OBJECTID de un comparendo
						identificarObjectId = true;
						System.out.println("OK identificarObjectId");
					}
					else if ( propiedad.equalsIgnoreCase("LOCALIDAD"))
					{	// Se identifica la propiedad LOCALIDAD de un comparendo
						// LOCALIDAD termina la seccion de Propiedades
						identificarLocalidad = true;
						leyendoPropiedades = false; 
						System.out.println("OK identificarLocalidad");
					}
				}
				else if ( leyendoGeometria )
				{
					if ( propiedad.equalsIgnoreCase("coordinates"))
					{  // Se identifica la propiedad coordinates de un comparendo
						identificarLongitud = true;
						identificarLatitud = true;
						System.out.println("OK identificarLongitud e identificarLatitud");
						leyendoGeometria = false; // coordinates termina la seccion de Geometry
						crearObjComparendo = true;
					}
					
				}
				
			}
		}
		else if (token.equals(JsonToken.STRING))
		{
			String valorString = reader.nextString();
            if ( identificarLocalidad )
			{
				localidad = valorString;
				identificarLocalidad = false;
				System.out.println("STRING LOCALIDAD="+localidad);				
			}
			else
			{
				System.out.println("STRING=" + valorString);
			}
		}
		else if (token.equals(JsonToken.NUMBER))
		{
			double valorNumerico = reader.nextDouble();
			if ( identificarObjectId )
			{
				objectId = (int) valorNumerico;
				identificarObjectId = false;
				System.out.println("NUMBER OBJECTID= "+objectId);
			}
			else if ( identificarLongitud )
			{
				longitud = valorNumerico;
				identificarLongitud = false;
				System.out.println("NUMBER Longitud= "+longitud);				
			}
			else
			{
				System.out.println("NUMBER=" + valorNumerico);				
			}
		}
		else if (token.equals(JsonToken.BOOLEAN))
		{
			boolean valorBool = reader.nextBoolean();
			System.out.println("BOOLEAN=" + valorBool);
		}
		/* else if (token.equals(JsonToken.BEGIN_OBJECT)) {
                handleObject(reader);
	        }
	        else if (token.equals(JsonToken.END_OBJECT)) {
	        	System.out.println("END_OBJECT");
                reader.endObject();
	        } */
		else
		{
			System.out.println("SKIP_VALUE: " + token);
			reader.skipValue();
		}
	}
	    	 
	public void processingJSONFile( ) 
	{
		try
		{
			BufferedReader rd = null;
			StringReader srd = null;
			
			rd = new BufferedReader(new FileReader(rutaArchivo));
			String inputLine = null;
			StringBuilder builder = new StringBuilder();

			//Store the contents of the file to the StringBuilder.
			while((inputLine = rd.readLine()) != null)
			{
				builder.append(inputLine);
			}
			srd = new StringReader(builder.toString());

			JsonReader reader = new JsonReader( srd );

			if ( rutaArchivo.equals(comparendos_small_GEOJSON_FILE))  // Definido como un JSON_OBJECT
			{
				// we call the handle object method to handle the full json object. This
				// implies that the first token in JsonToken.BEGIN_OBJECT, which is
				// always true.

				// Reading Test of a JSON object
				System.out.println("Reading the JSON Object File: " + rutaArchivo);
				handleObject(reader);
			}
			else
			{
				// Reading Test of a JSON Array
				System.out.println("Reading the JSON Array File: " + rutaArchivo);
				handleArray(reader);
			}
			System.out.println("End Test Handle JSON processing");

		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Creacion de un comparendo a partir de la lectura de todas sus propiedades
	 */
	public void crearComparendo()
	{   
		// TODO Crear el objeto comparendo con las propiedades leidas antes
		// TODO Adicionar el comparendo a la Lista de Comparendos
		System.out.println("Crear comparendo con: ObjectId: " + objectId + ", Localidad: " + localidad + ", (Long: " + longitud + ", Lat: " + latitud + ")");
		
		leyendoPropiedades = false;
		leyendoGeometria = false;
		crearObjComparendo = false;
	}
	
	public static void main(String[] args) 
	{
		// Inicializar el objeto de procesamiento con el nombre del archivo JSON o comparendos GEOJSON 
		JsonGsonProcessing objetoJsonGson = new JsonGsonProcessing( comparendos_small_GEOJSON_FILE );

		objetoJsonGson.processingJSONFile();
	}

}
