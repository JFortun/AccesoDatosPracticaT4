package colegio;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.matisse.MtDatabase;
import com.matisse.MtException;
import com.matisse.MtObjectIterator;
import com.matisse.MtPackageObjectFactory;

public class Principal {

	static String hostname= "localhost"; 
	static String dbname = "colegio"; 

	public static void main(String[] args) {
		crearObjeto(hostname, dbname);
		borrarObjeto(hostname, dbname);
		modificarObjeto(hostname, dbname, "Pepe", 954772234);
		Consultar(hostname, dbname);
	}

	public static void crearObjeto(String hostname, String dbname) { 
		try { 
			// Abre la base de datos con el Hostname (localhost), dbname (colegio) y el   namespace "colegio".
			MtDatabase db = new MtDatabase(hostname, dbname, new MtPackageObjectFactory("", "colegio"));
			db.open();
			db.startTransaction();
			System.out.println("Conectado a la base de datos " + db.toString() + " de Matisse");
			// Crea un objeto Profesor
			Profesores profesor1 = new Profesores(db);
			profesor1.setNombre("Pepe"); 
			profesor1.setApellidos("González"); 
			profesor1.setTelefono(636253453);
			profesor1.setDni("49223456T"); 
			System.out.println("Profesor Pepe creado...");
			// Crea un objeto Asignaturas
			Asignaturas asignatura1 = new Asignaturas(db); 
			asignatura1.setNombre("Matemáticas");  
			asignatura1.setDuracion(60);
			asignatura1.setAula(2);
			asignatura1.setHoraInicio(9);
			asignatura1.setDiaSemana("Lunes");
			System.out.println("Asignatura Matemáticas creada...");

			Clases clase1[] = new Clases[1];
			clase1[0] = asignatura1;
			// Guarda las relaciones de la clase con las asignaturas
			profesor1.setImparten(clase1);
			// Ejecuta un commit para materializar las peticiones.
			db.commit(); 
			// Cierra la   base de datos.
			db.close(); 
			System.out.println("\nHecho.");
		} 
		catch (MtException mte  ) {
			System.out.println("MtException : " + mte.getMessage());
		} 
	}

	// Borrar todos los objetos de una clase
	public static void borrarObjeto(String hostname, String dbname) {
		System.out.println("====================== Borrar Todas =====================\n");
		try {
			MtDatabase db = new MtDatabase(hostname, dbname, new MtPackageObjectFactory("", "colegio")); 
			db.open(); 
			db.startTransaction(); 
			// Lista todos los objetos Clase que hay en la base de datos, con el método // getInstanceNumber
			System.out.println("\n" + Clases.getInstanceNumber(db) + " Clase(s) en la DB.");
			// Borra todas las instancias de Clase
			Clases.getClass(db).removeAllInstances(); 
			// materializa los cambios y cierra la BD 
			db.commit(); 
			db.close(); 
			System.out.println("\nHecho.");
		} 
		catch (MtException mte  ) { 
			System.out.println("MtException : " + mte.getMessage());
		}
	}

	public static void modificarObjeto(String hostname, String dbname, String nombre, Integer nuevoTelefono) {
		System.out.println("=========== Modifica un objeto ==========\n"); 
		int nAutores = 0; 
		try { 
			MtDatabase db = new MtDatabase(hostname, dbname, new MtPackageObjectFactory("", "colegio"));
			db.open();
			db.startTransaction();
			// Lista cuántos objetos Clase con el método getInstanceNumber
			System.out.println("\n" + Profesores.getInstanceNumber(db) + " Profesores en la DB.");
			nAutores = (int) Profesores.getInstanceNumber(db); 
			// Crea un Iterador (propio de Java) 
			MtObjectIterator<Profesores> iter = Profesores.<Profesores>instanceIterator(db); 
			System.out.println("Recorro el iterador de uno en uno y cambio cuando encuentro 'nombre'");
			while (iter.hasNext()) { 
				Profesores[] profesores = iter.next(nAutores);
				for (int i = 0; i < profesores.length; i++) { 
					// Busca un profesor con nombre 'nombre' 
					if (profesores[i].getNombre().compareTo(nombre) == 0) {
						profesores[i].setTelefono(nuevoTelefono);
					} 
					else {}}} iter.close();
					// materializa los cambios y cierra la BD 
					db.commit();
					db.close();
					System.out.println("\nHecho.");
		} 
		catch (MtException mte) { 
			System.out.println("MtException : " + mte.getMessage());
		}
	} 

	public static void Consultar(String hostname, String dbname) {
		MtDatabase dbcon = new MtDatabase(hostname, dbname); 
		// Abre una conexión a la base de datos dbcon.open();
		try { 
			// Crea una instancia de Statement 
			Statement stmt = dbcon.createStatement();
			// Asigna una consulta OQL. Esta consulta lo que hace es utilizar REF() para obtener el objeto directamente en vez de obtener valores concretos (que también podría ser). 
			String commandText = "SELECT REF(a) from colegio.Profesor p;";
			// Ejecuta la consulta y obtiene un ResultSet 
			ResultSet rset = stmt.executeQuery(commandText);
			Profesores profesor1;
			// Lee rset uno a uno. 
			while (rset.next()) { 
				// Obtiene los objetos Autor. 
				profesor1 = (Profesores) rset.getObject(1);
				// Imprime los atributos de cada objeto con un formato determinado. 
				System.out.println("Profesor: " 
						+ String.format("%16s", profesor1.getNombre()) 
						+ String.format("%16s", profesor1.getApellidos()) 
						+ " Telefono: " + String.format("%16s", profesor1.getTelefono()));
			} 
			// Cierra las conexiones 
			rset.close();
			stmt.close();
		} 
		catch (SQLException e) { 
			System.out.println("SQLException:  " + e.getMessage());
		}
	}
}