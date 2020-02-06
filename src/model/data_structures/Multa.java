package model.data_structures;

public class Multa implements IMulta{

	private int id;
	
	private String dyH;
	
	private String tV;
	
	private String tS;
	
	private String cI;
	
	private String dI;
	
	private String loc;
	
	public Multa( int pId, String pDyH, String pTV, String pTS, String pCI, String pDI, String pLoc){
		
		id = pId;
		dyH = pDyH;
		tV = pTV;
		cI = pCI;
		dI = pDI;
		loc = pLoc;
	}
	
	public int darId(){
		return id;
	}
	
	public String darDyH(){
		return dyH;
	}
	
	public String darTV(){
		return tV;
	}
	
	public String darTS(){
		return tS;
	}
	
	public String darCI(){
		return cI;
	}
	
	public String darDI(){
		return dI;
	}
	
	public String darLoc(){
		return loc;
	}
}
