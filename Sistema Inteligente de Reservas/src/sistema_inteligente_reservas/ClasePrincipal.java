package sistema_inteligente_reservas;
/**
 * @version 1.0
 * @author RawVR
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Clase Principal que llamar� al sistema inteligente de reservas
 */
public class ClasePrincipal {

	/** 
	 * Funci�n que comprueba si el proceso hijo est� funcionando
	 * 
	 * @return False = 0 sino crea una excepcion y devuelve true
	 * @param p Proceso hijo
	 */
	public static boolean isAlive(Process p) {
		try {
			p.exitValue();
			return false;
		} catch (IllegalThreadStateException e) {
			return true;
		}
	}

	/**
	 * Funci�n main que se encarga de iniciar el proceso hijo y comunicarse con �l
	 * 
	 * @param args Argumento del main
	 * @throws IOException Excepcion posible en la entrada/salida
	 * */
	public static void main(String[] args) throws IOException {
		ProcessBuilder builder = new ProcessBuilder("java", "-cp", "src",
				"src/sistema_inteligente_reservas/ProcesoReservas.java"); // definimos el proceso que se va a ejecutar
		builder.redirectErrorStream(true); // redirige el buffer de error a la salida est�ndar
		Process process = builder.start(); // iniciamos el proceso
		InputStream out = process.getInputStream(); // configuramos la salida del proceso hijo
		OutputStream in = process.getOutputStream(); // configuramos la entrada del proceso hijo

		byte[] buffer = new byte[4000]; // buffer de comunicaci�n entre procesos har� de puente

		while (isAlive(process)) {// se comprueba el stream de salida del proceso hijo
			int no = out.available();
			if (no > 0) {

				// si el stream de salida del proceso hijo tiene informaci�n se muestra por pantalla
				int n = out.read(buffer, 0, Math.min(no, buffer.length));
				System.out.println(new String(buffer, 0, n));
			}

			// se comprueba si hay informaci�n para enviar al proceso hijo
			int ni = System.in.available();
			if (ni > 0) {

				// si existe informaci�n se env�a al proceso hijo
				int n = System.in.read(buffer, 0, Math.min(ni, buffer.length));
				in.write(buffer, 0, n);
				in.flush();
			}

			// se introduce un retardo de 10 milisegundos para que no haya colapsos
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}

		System.out.println(process.exitValue());
	}
}