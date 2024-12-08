package controller;



public class MontanCategorie {
private int id;
	private double montant;
	
public MontanCategorie(int id, double montant) {
		super();
		this.id = id;
		this.montant = montant;
	}



	public MontanCategorie(double montant) {
	super();
	this.montant = montant;
}



	public int getId() {
	return id;
}



public void setId(int id) {
	this.id = id;
}



	public double getMontant() {
		return montant;
	}


	public void setMontant(double montant) {
		this.montant = montant;
	}



}
