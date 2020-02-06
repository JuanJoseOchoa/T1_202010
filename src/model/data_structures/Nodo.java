package model.data_structures;

public class Nodo<T> {
	
	private T multa;
	
	private Nodo <T> siguiente;
	
	public Nodo ( T pMulta){
		
		multa = pMulta;
		siguiente = null;
	}
	
	public T darMulta(){
		return multa;
	}
	
	public void agregarSiguiente( Nodo<T> pSiguiente){

		siguiente = pSiguiente;
	}
	
	public boolean existeSiguiente(){
		
		boolean existe = false;
		
		if(siguiente != null){
			
			existe = true;
		}
		return existe;
	}
	
	public Nodo <T> darSiguiente(){
		return siguiente;
	}

}
