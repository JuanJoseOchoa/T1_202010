package model.data_structures;

public class ListaEnlazada <T>{
	
	private Nodo <T> first;
	
	private Nodo <T> last;
	
	private int length;
	
	public ListaEnlazada( )
	{
		first = null;
		last = null;
		length = 0;
	}
	
	public Nodo<T> darFirst( )
	{
		return first;
	}

	public Nodo<T> darLast( )
	{
		return last;
	}
	
	public int darLength(){
		return length;
	}
	
	public void agregarNodo( T pMulta)
	{
		if(first == null){
			
			first = new Nodo <T> (pMulta);
			last = first; 
			length ++;
		}
		
		else{
			Nodo <T> n = darLast( );
			n.agregarSiguiente(new Nodo<T>(pMulta));
			last = n.darSiguiente();
			length++;
		}
	}
	
	public T buscarMulta (int id)
	{
		if(first == null){
			return null;
		}
		
		else{
			int x = 1;
			Nodo <T> n = first;
			
			while(x<id){
				n = n.darSiguiente();
			}
			return n.darMulta();
		}
	}
}
